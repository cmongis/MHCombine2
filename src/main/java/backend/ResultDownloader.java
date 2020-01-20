/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.core.util.IOUtils;

/**
 *
 * @author Cyril MONGIS
 */
@WebServlet("/download")
public class ResultDownloader extends AsyncServlet{
    
    
    private static final int ARBITARY_SIZE = 1024;
    
   
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        
        String id = request.getParameter("id");
        
        Job job = getJobManager().getJob(id);
        
        if(job == null) {
            response.setStatus(404);
            response.getWriter().close();
        }
        
        else {
           response.setStatus(HttpServletResponse.SC_OK);
           response.setHeader("Content-Type", "application/octet-stream");
           response.setHeader("Content-Disposition", "attachment;filename=\""+job.getFileName()+"\"");
           response.setHeader("Cache-Control","cache");
           response.setHeader("Cache-Control","must-revalidate");  
           
           FileJob fileJob = (FileJob) job;
           FileReader fr = new FileReader(fileJob.getFile());
           
           InputStream in = new FileInputStream(fileJob.getFile());
           OutputStream out = response.getOutputStream();
           
           
           byte[] buffer = new byte[ARBITARY_SIZE];
         
            int numBytesRead;
            while ((numBytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, numBytesRead);
            }
          
           out.flush();
           out.close();
           in.close();
        }
        
    }
    
}
