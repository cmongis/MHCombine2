/*
 * This file is part of MHCcombine.
 *
 * MHCcombine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MHCcombine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MHCcombine.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright 2018 Angelika Riemer, Maria Bonsack
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
 * @author Cyril MONGIS
 */
@WebServlet(name = "/job",value = "/job",asyncSupported = true)
public class ServerQuerierAsync extends AsyncServlet {
    
    private ObjectMapper mapper;
    
    public ServerQuerierAsync() {
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
               response.getWriter().write(mapper.writeValueAsString(new JobStatus(job)));
           }
          response.getWriter().close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        
        response.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        PrintWriter out = response.getWriter();
        String sequence = request.getParameter("seqsub");
        String species = request.getParameter("species");
        String allel = request.getParameter("alleleList");
        String[] servers = request.getParameterValues("server");
        String len = request.getParameter("length");
        String filenameProtId = request.getParameter("file_name");
        String adaptiveColumns = request.getParameter(ADAPTIVE_COLUMNS);
        QueryInputType queryInputType = Util.queryInputTypeFromString(request.getParameter("queryInputType"));

        

        if("on".equals(adaptiveColumns)) {
            adaptiveColumns = "true";
        }
        
        if (StringUtils.isEmpty(sequence)
                || StringUtils.isEmpty(allel)
                || StringUtils.isEmpty(len)) {
            
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.close();
            return;
        }
        
        

        
        if (servers == null || servers.length == 0) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.close();
            return;
        }
        
        String filename = "DKFZ-webtool_";
		if (filenameProtId != null && !filenameProtId.isEmpty()) {
			filename = filename + filenameProtId + "_";
		}
		filename = filename+len+"_"+allel+".csv";
        
        Job job = getJobManager()
                .createJob();
        
        job.configure(queryInputType,sequence, allel, len, servers);
        job.setConfig("adaptiveColumns",adaptiveColumns);
        job.setFileName(filename);
        
        getJobManager()
                .start(job);

       out.write(mapper.writeValueAsString(new JobStatus(job)));
       out.close();
    }
}
