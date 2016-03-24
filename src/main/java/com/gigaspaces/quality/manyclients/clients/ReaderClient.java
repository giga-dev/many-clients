package com.gigaspaces.quality.manyclients.clients;

import com.gigaspaces.quality.manyclients.data.ManyClientsMessage;
import org.openspaces.core.GigaSpace;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReaderClient extends AbstractClient
{
    final static Logger logger = Logger.getLogger(ReaderClient.class.getName());
    private final int id;
    
    public ReaderClient(int id, GigaSpace gigaSpace, long delay, int printInterval)
    {
        super(id, gigaSpace, delay, printInterval, logger);
        this.id = id;
    }

    public boolean doAction()
    {
        try{
            long start = System.nanoTime();
            ManyClientsMessage message = _gigaSpace.read(new ManyClientsMessage());
            long end = System.nanoTime();
            long latency = TimeUnit.NANOSECONDS.toMillis(end - start);
            if (latency > 2){
                if (logger.isLoggable(Level.INFO)){
                    logger.info("id=" + id + ", read operation took " + latency + " millis");
                }
            }
            if (end - start > 2)
            if (message != null){
                return true;
            }
        }catch(Exception e){
            if (logger.isLoggable(Level.INFO)){
                logger.log(Level.INFO, "an exception was thrown while performing read", e);
            }
        }
        return false;
    }

}
