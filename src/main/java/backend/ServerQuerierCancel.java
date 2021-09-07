/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import backend.serverqueries.QueryInputType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import utils.Util;

/**
 *
 * @author cyrilmongis
 */
@WebServlet(name = "/cancel",value = "/cancel",asyncSupported = true)
public class ServerQuerierCancel extends AsyncServlet {
    
    private ObjectMapper mapper;
    
    public ServerQuerierCancel() {
        super();
         mapper = new ObjectMapper();
    }
    
    private static final String ADAPTIVE_COLUMNS = "adaptiveColumns";
    
    
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

           response.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
           String id = request.getParameter("id");
           Job job = getJobManager().getJob(id);
  
           if(job == null) {
               response.setStatus(404);
           }
           
           else {
               job.cancel();
               getJobManager().delete(id);
               response.setStatus(200);
           }
          response.getWriter().close();
    }

    
}
