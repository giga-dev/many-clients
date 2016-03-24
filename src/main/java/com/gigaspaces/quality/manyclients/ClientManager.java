package com.gigaspaces.quality.manyclients;

import com.gigaspaces.quality.manyclients.clients.AbstractClient;
import com.gigaspaces.quality.manyclients.clients.ReaderClient;
import com.gigaspaces.quality.manyclients.clients.UpdaterClient;
import com.gigaspaces.quality.manyclients.utils.SSHExecute;
import com.j_spaces.core.IJSpace;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

import java.util.Properties;

public class ClientManager
{
    public static void startClients(Properties props)
    {
        String[] ipAddresses = props.getProperty(Props.CLIENT_MACHINES).split(",");
        String username = props.getProperty(Props.USERNAME);
        String password = props.getProperty(Props.PASSWORD);
        String keyFile = props.getProperty(Props.KEY_FILE);
        String clientScript = props.getProperty(Props.CLIENT_SCRIPT);
        String processPerMachine = props.getProperty(Props.PROCESSES_PER_MACHINE);
        String command = clientScript + " " + processPerMachine;
        for (int i = 0; i < ipAddresses.length; i++)
        {
            String ipAddress = ipAddresses[i];
            SSHExecute.runCommand(ipAddress, 7000, command, username, password, keyFile);
        }
    }


    public static Thread[] runClients(Properties props, int numOfClients) throws InterruptedException
    {
        System.out.println(" spaceFinder url: start" );
        Thread[] threads = new Thread[numOfClients];
        long clientActionDelay = Long.valueOf(props.getProperty(Props.CLIENT_ACTION_DELAY));
        int printInterval = Integer.valueOf(props.getProperty(Props.CLIENT_PRINT_INTERVAL));
        GigaSpace gigaSpace = findSpace( props );
        AbstractClient client;
        for (int i = 0; i < numOfClients; i++)
        {
            int random = (int)(Math.random() * 2);
            if (random % 2 == 0){
                client = new UpdaterClient(i, gigaSpace, clientActionDelay, printInterval);
            }else{
                client =  new ReaderClient(i, gigaSpace, clientActionDelay, printInterval);
            }
            threads[i] = client;
            System.out.println("start =" + gigaSpace );
            client.start();
        }
        return threads;
    }

    public static GigaSpace findSpace( Properties props )  throws InterruptedException{

        String lookupGroup = props.getProperty(Props.LOOKUP_GROUP);
        String lookupLocator = props.getProperty(Props.LOOKUP_LOCATOR);
        String spaceName = props.getProperty(Props.SPACE_NAME);

        GigaSpace gigaSpace = null;
        int retries = 60;
        while(retries > 0){
            try{
                String spaceFinderUrl = "jini://*/*/" + spaceName + "?groups=" + lookupGroup;
                if( lookupLocator != null ){
                    spaceFinderUrl += "&locators=" + lookupLocator;
                }
                System.out.println("spaceFinder url:" + spaceFinderUrl);
                IJSpace space = new UrlSpaceConfigurer( spaceFinderUrl ).space();
                System.out.println("spaceFound" );
                gigaSpace = new GigaSpaceConfigurer(space).gigaSpace();
                System.out.println("spaceFound, gigaSpace=" + gigaSpace );
                break;
            }catch(Exception e){
                retries--;
                Thread.sleep(1000);
            }
        }

        return gigaSpace;
    }
}
