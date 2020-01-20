/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import javax.servlet.http.HttpServlet;

/**
 *
 * @author cyrilmongis
 */
public class AsyncServlet extends HttpServlet{
    
    
    private JobManager jobManager;
    public JobManager getJobManager() {
        if(jobManager == null) {
            jobManager = (JobManager)getServletContext().getAttribute(JobManagerStarter.JOB_MANAGER);
        }
        
        return jobManager;
    }
    
}
