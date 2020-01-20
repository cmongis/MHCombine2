/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author cyrilmongis
 */
public interface Job extends Runnable {
    
    
    public void setId(String id);
    
    public String getId();
    public void setObserver(QueryObserver observer);
    
    public void configure(String sequence, String allel, String len, String... servers);    
    public void setOutputStream(OutputStream stream);
    
    
    public long getTimeCreation();
    
    public boolean isFinished();
    
    public boolean hasSucceeded();
    
    public int getProgress();
    
    public int getTotal();

    public void clean();
    
    public Throwable getError();
    
    public void setFileName(String filename);
    public String getFileName();
    
}
