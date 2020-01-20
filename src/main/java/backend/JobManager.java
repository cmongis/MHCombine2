/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author cyrilmongis
 */
public class JobManager {

    private static JobManager manager;

    public static void cleanSingleton() {
        if(manager != null) {
            manager.stop();
        }
        manager = null;
    }

    private final HashMap<String, Job> jobMap = new HashMap<String, Job>();

    private static final long MAXIMUM_RETENTION_TIME = 1000 * 60 * 10;

    private ExecutorService executor = Executors.newFixedThreadPool(10);
    
    
    private static final Logger logger = LogManager.getLogger(JobManager.class);
    
    
    public static JobManager getSingleton() {
        if (manager == null) {
            manager = new JobManager();
        }

        return manager;
    }
    public JobManager() {
        System.out.println("JobManager was created.");
    }
    
    public static long getExpirationDate(Job job) {
        return job.getTimeCreation() + MAXIMUM_RETENTION_TIME;
    }
    
    public Job createJob() throws IOException {

            QueryJob job = new QueryJob();
            job.setId(UUID.randomUUID().toString());
            
            FileJob fileJob = new QueryFileJob(job);
            jobMap.put(fileJob.getId(), fileJob);
            fileJob.setObserver(new ConsoleQueryObserver(job));
            return fileJob;
        
    }
    
    public Job getJob(String id) {
        
        cleanJobs();
        
        return jobMap.get(id);
    }
    
    public void start(Job job) {
        executor.execute(job);
    }

    public void cleanJobs() {
        long now = System.currentTimeMillis();
        for (String key : jobMap.keySet()) {
            Job job = jobMap.get(key);
            if((now - job.getTimeCreation()) > MAXIMUM_RETENTION_TIME) {
                logger.info("Cleaning "+job.getId());
                job.clean();
            }
        }
    }
    
    public void stop() {
        executor.shutdown();
    }
}
