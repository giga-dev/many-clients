package com.gigaspaces.quality.manyclients.clients;

import org.openspaces.core.GigaSpace;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractClient extends Thread
{
    protected boolean _alive = true;
    protected final long _delay;
    protected final GigaSpace _gigaSpace;
    protected int _succesfulActions = 0;
    protected int _totalActions = 0;
    private final int _printInterval;
    private final int _id;
    private final Logger _logger;
    
    public AbstractClient(int id, GigaSpace gigaSpace, long delay, int printInterval, Logger logger)
    {
        _id = id;
        _gigaSpace = gigaSpace;
        _delay = delay;
        _printInterval = printInterval;
        _logger = logger;
    }
    
    /**
     * @see Thread#run()
     */
    @Override
    public void run() {
        while(_alive){
            _totalActions++;
            if(doAction()){
               _succesfulActions++;
            }
            if (_totalActions % _printInterval == 0){
                if(_logger.isLoggable(Level.INFO)){
                    _logger.info("Client" + _id + ": performed " + _totalActions + " actions of which " + _succesfulActions + " were successful");
                }
            }
            try {
                sleep(_delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void shutdown(){
        _alive = false;
        try {
            this.join();
        } catch (InterruptedException e) {
        }
    }
    
    public abstract boolean doAction();
}
