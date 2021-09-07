package backend;


import backend.Job;
import java.util.logging.Logger;
import backend.QueryJobObserver;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cyrilmongis
 */
public class ConsoleQueryObserver implements QueryJobObserver{

    final static Logger logger = Logger.getLogger(ConsoleQueryObserver.class.getName());
    
    private final Job job;
    private final QueryJobObserver observer;
    
    public ConsoleQueryObserver(Job job) {
        this.job = job;
        this.observer = null;
    }
    
    public ConsoleQueryObserver(Job job, QueryJobObserver obs) {
        this.job = job;
        observer = obs;
    }
    
    private void log(String msg, Object... params) {
        this.logger.info(String.format(msg,params));
    }
    @Override
    public void setStarted() {
        log("Started");
        if(observer != null) observer.setStarted();
    }

    @Override
    public void setFinished() {
        log("Finished");
        if(observer != null) observer.setFinished();
        
    }

    @Override
    public void notifyProgress() {
        log("Progess %d / %d ",job.getProgress(),job.getTotal());
        if(observer != null) observer.notifyProgress();
    }
}
