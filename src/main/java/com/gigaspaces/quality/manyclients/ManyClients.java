package com.gigaspaces.quality.manyclients;

import org.openspaces.core.GigaSpace;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;


/**
 * an application that deploys a 2,1 cluster and runs multiple clients who
 * perform various operations on the cluster.
 * currently, the implemented clients perform read and update.
 * application settings can be found and changed in config/manyclients.properties.
 * 
 * Running the application:
 * 1) run inside eclipse, launch ManyClients manager.launch
 * 2) run ./scripts/manager.sh
 * 
 * instructions can be found on scripts/readme.txt
 * 
 * @author Rafi Pinto
 */
public class ManyClients
{
    public static void main(String[] args) throws IOException, InterruptedException
    {
        String configFile = System.getProperty("configfile");
        Properties configProperties = getConfigProperties(configFile);
        System.out.println( ">>configFile=" + configFile + ", args=" + Arrays.toString( args ) );
        if (args[0].equals("manager")){
            Scanner in = new Scanner(System.in);
            String cmd;
            while(true){
                System.out.print("$");
                cmd = in.nextLine();
                if (cmd.equals("exit")){
                    System.out.println("Exiting...");
                    System.exit(0);
                }else if(cmd.equals("deploy")){
                    System.out.println("*** Deploying enviroment ***");
                    EnvironmentManager.deployEnv(configProperties);
                }else if(cmd.equals("clients")){
                    System.out.println("*** Running clients ***");
                    ClientManager.startClients(configProperties);
                }else if(cmd.equals("kill")){
                    System.out.println("*** Killing machines ***");
                    EnvironmentManager.killAll(configProperties);
                }
            }
        }
        else if(args[0].equals("feed")){
            System.out.println( "handle feed" );
            GigaSpace space = ClientManager.findSpace(configProperties);
            System.out.println( "handle feed, space found=" + space );
            Integer numOfObjectsInSpace = Integer.valueOf(configProperties.getProperty(Props.NUM_OF_OBJECTS_IN_SPACE));
            System.out.println( "before running feed" );
            EnvironmentManager.feedSpace(space, numOfObjectsInSpace);
            System.out.println( "after running feed" );
        }
        else if(args[0].equals("client")){
            int count = Integer.valueOf(args[1]);
            Thread[] clients = ClientManager.runClients(configProperties, count);
            for (int i = 0; i < clients.length; i++)
            {
                try
                {
                    clients[i].join();
                }
                catch (InterruptedException e)
                {
                    // TODO 
                }
            }
        }
    }
    
    public static Properties getConfigProperties(String configFile) throws IOException{
        InputStream in = new FileInputStream(configFile);
        Properties props = new Properties();
        props.load(in);
        return props;
    }
}
