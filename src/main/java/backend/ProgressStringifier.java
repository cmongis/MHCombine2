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
public class ProgressStringifier {
    
    
    
    public static String stringify(Job job) {
        
        
        
        
        StringBuilder builder = new StringBuilder(100);
        
        String status;
        
        
        
        if(job.getTotal()== 0) {
            status = "Pending...";
        }
        else if (job.hasSucceeded()) {
            status = "Success.";
        }
        else if(job.isFinished()) {
           status = "Error!";
        }
        else {
            status = "Processing ... ";
        }
        
        return String.format(
                "%s (Running: %d, Finished: %d/%d)"
                ,status
                ,job.getRunning()
                ,job.getProgress()
                ,job.getTotal()
                );
        
   
    }
}
