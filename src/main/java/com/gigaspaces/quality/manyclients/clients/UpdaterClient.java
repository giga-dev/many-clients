package com.gigaspaces.quality.manyclients.clients;

import com.gigaspaces.quality.manyclients.data.ManyClientsMessage;
import com.j_spaces.core.LeaseContext;
import com.j_spaces.core.client.Modifiers;
import net.jini.core.lease.Lease;
import org.openspaces.core.GigaSpace;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdaterClient extends AbstractClient
{

    final static Logger logger = Logger.getLogger(UpdaterClient.class.getName());
    private final int id ;
    
    public UpdaterClient(int id, GigaSpace gigaSpace, long delay, int printInterval)
    {
        super(id, gigaSpace, delay, printInterval, logger);
        this.id = id;
    }

    @Override
    public boolean doAction()
    {
        try{
            ManyClientsMessage message = _gigaSpace.read(new ManyClientsMessage());
            if (message == null){
                return false;
            }
            message.setData(ManyClientsMessage.generateRandomString());
            LeaseContext<ManyClientsMessage> leaseContext = _gigaSpace.write(message, Lease.FOREVER, 10000, Modifiers.UPDATE);
            if (logger.isLoggable(Level.INFO)){
                logger.info("id=" + id + ", update operation");
            }
            if (leaseContext != null){
                return true;
            }
        }catch(Exception e){
            if (logger.isLoggable(Level.INFO)){
                logger.log(Level.INFO, "an exception was thrown while performing read/update", e);
            }
        }
        return false;
    }

}
