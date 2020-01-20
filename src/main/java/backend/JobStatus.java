/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

/**
 *
 * @author cyrilmongis
 */
public class JobStatus {

    private final Job job;

    public JobStatus(Job job) {
        this.job = job;
    }
    
    public String getStatus() {
        return ProgressStringifier.stringify(job);
    }
    
    public boolean isFinished() {
        return job.isFinished();
    }
    
    public int getProgress() {
        return job.getProgress();
    }
    
    public int getTotalProgress() {
        return job.getTotal();
    }
    
    public String getId() {
        return job.getId();
    }
    
    public Throwable getError() {
        return job.getError();
    }
    
    public String getFileName() {
        return job.getFileName();
    }
    
}
