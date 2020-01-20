/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author cyrilmongis
 */
public class JobManagerStarter implements ServletContextListener{

    public final static String JOB_MANAGER = "JOB_MANAGER";
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
        sce.getServletContext().setAttribute(JOB_MANAGER, JobManager.getSingleton());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
        JobManager.getSingleton().stop();
        JobManager.cleanSingleton();
        
    }
    
}
