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
        
        if(job.getTotal()== 0) {
            builder.append("Pending...");
        }
        else if (job.hasSucceeded()) {
            builder.append("Success.");
        }
        else if(job.isFinished()) {
            builder.append("Error.");
        }
        else {
            builder.append("Processing ... ");
        }
        builder = builder
                .append(" (")
                .append(job.getProgress())
                .append("/")
                .append(job.getTotal())
                .append(")");
                
        return builder.toString();
    }
}
