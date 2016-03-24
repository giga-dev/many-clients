package com.gigaspaces.quality.manyclients;

import com.gigaspaces.quality.manyclients.data.ManyClientsMessage;
import com.gigaspaces.quality.manyclients.utils.SSHExecute;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.gsm.GridServiceManager;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.space.SpaceDeployment;
import org.openspaces.core.GigaSpace;

import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnvironmentManager
{   
    private static final Logger logger = Logger.getLogger(EnvironmentManager.class.getName());
    
    public static void deployEnv(Properties props){
        String gsaScript = props.getProperty(Props.GSA_SCRIPT);
        String username = props.getProperty(Props.USERNAME);
        String password = props.getProperty(Props.PASSWORD);
        String keyFile = props.getProperty(Props.KEY_FILE);
        String[] gsaMachines = props.getProperty(Props.GSA_MACHINES).split(",");
        String lookupGroup = props.getProperty(Props.LOOKUP_GROUP);
        String spaceName = props.getProperty(Props.SPACE_NAME);
        Integer numOfObjectsInSpace = Integer.valueOf(props.getProperty(Props.NUM_OF_OBJECTS_IN_SPACE));
        
//        setupMachines(gsaScript, username, password, keyFile, gsaMachines);
        GigaSpace gigaSpace = deploySpace(lookupGroup, spaceName);
        feedSpace(gigaSpace, numOfObjectsInSpace);
    }

    private static void setupMachines(String gsaScript, String username, String password, String keyFile, String[] ipAddresses)
    {
        long timeoutMilliseconds = 5000;
        for (int i = 0; i < ipAddresses.length; i++)
        {
            String ipAddress = ipAddresses[i];
            SSHExecute.runCommand(ipAddress, timeoutMilliseconds, gsaScript, username, password, keyFile);
        }
    }
    
    public static void killAll(Properties props)
    {
        String username = props.getProperty(Props.USERNAME);
        String password = props.getProperty(Props.PASSWORD);
        String keyFile = props.getProperty(Props.KEY_FILE);
        
        String[] gsaMachines = props.getProperty(Props.GSA_MACHINES).split(",");
        killMachines(gsaMachines, username, password, keyFile);
        
        String[] clientMachines = props.getProperty(Props.CLIENT_MACHINES).split(",");
        killMachines(clientMachines, username, password, keyFile);
    }
    
    public static void killMachines(String[] ipAddresses, String username, String password, String keyFile){
        long timeoutMilliseconds = 3000;
        for (int i = 0; i < ipAddresses.length; i++)
        {
            String ipAddress = ipAddresses[i];
            SSHExecute.runCommand(ipAddress, timeoutMilliseconds, "killall -9 java", username, password, keyFile);
        }
    }
    
    public static void feedSpace(GigaSpace gigaSpace, int numOfObjectsInSpace)
    {
        if (logger.isLoggable(Level.INFO)){
            logger.info("starting to feed space");
        }
        gigaSpace.snapshot(new ManyClientsMessage());
        for (int i = 0; i < numOfObjectsInSpace; i++)
        {
            try{
                gigaSpace.write(new ManyClientsMessage(UUID.randomUUID().toString()));
            }catch(Exception e){
                if (logger.isLoggable(Level.INFO)){
                    logger.log(Level.INFO, "failed to perform write", e);
                }
            }
        }
        if (logger.isLoggable(Level.INFO)){
            logger.info("finished feeding space");
        }
    }

    private static GigaSpace deploySpace(String lookupGroup, String spaceName)
    {
        
        Admin admin = new AdminFactory().addGroup(lookupGroup).createAdmin();
        admin.getMachines().waitFor(1);
        GridServiceManager gsm=admin.getGridServiceManagers().waitForAtLeastOne();
        if (logger.isLoggable(Level.INFO)){
            logger.info("trying to deploy space");
        }
        ProcessingUnit pu = gsm.deploy(new SpaceDeployment(spaceName).partitioned(2, 1));
        pu.waitForSpace();
        if (logger.isLoggable(Level.INFO)){
            logger.info("deployed space succesfully");
        }
        return pu.getSpace().getGigaSpace();
    }
}
