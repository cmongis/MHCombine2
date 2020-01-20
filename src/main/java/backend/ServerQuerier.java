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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utils.Constants.Algorithm;
import utils.Util;
import backend.entries.EntryKey;
import backend.entries.ResultEntry;
import backend.entries.TemporaryEntry;
import backend.serverqueries.AbstractQuery;
import java.util.concurrent.TimeUnit;

/**
 * Servlet implementation class ServerQuerier
 */
@WebServlet("/queryServer")
public class ServerQuerier extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final Logger logger = LogManager.getLogger(ServerQuerier.class);
	
        private static final int THREAD_POOL_SIZE = 15;
        
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ServerQuerier() {
		super();
               
	}
        
       

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// we do not want to serve anyone here, we only accept POSTs...
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head><title>MHCcombined</title></head>");
		out.println("<body>");
		out.println("<h1>Oops... </h1>");
		out.println("Seems you got lost here.");
		out.println("If you want to execute a query on MHCcombined, go to <a href=\"/DKFZ-webtool\">the main page</a>");
		out.println("</body>");
		out.println("</html>");
		out.close();
		
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String sequence = request.getParameter("seqsub");
		String species = request.getParameter("species");
		String allelParam = "allele"+species;
		String allel = request.getParameter(allelParam).trim();
		String[] servers = request.getParameterValues("server");
		String len = request.getParameter("length");
		String filenameProtId = request.getParameter("file_name");
	
		if (StringUtils.isEmpty(sequence) 
				|| StringUtils.isEmpty(allel)
				|| StringUtils.isEmpty(len)
				) {
			PrintWriter out = response.getWriter();
			printOopsOutput(out, "Sequence, allel, or desired length are null or empty.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.close();
			return;
		}
		if (servers == null || servers.length == 0) {
			PrintWriter out = response.getWriter();
			printOopsOutput(out, "List of passed servers was null or empty.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		Map<EntryKey, ResultEntry> results = createAndExecuteQueries(servers, sequence, allel, len);
		
		String filename = "DKFZ-webtool_";
		if (filenameProtId != null && !filenameProtId.isEmpty()) {
			filename = filename + filenameProtId + "_";
		}
		filename = filename+len+"_"+allel+".csv";
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "text/csv");
		response.setHeader("Content-Disposition", "attachment;filename=\""+filename+"\"");
		createCsvFile(results, ";", response.getOutputStream());
	}
	
	private void printOopsOutput(PrintWriter out, String bodymsg) {
		out.println("<html><head><title>MHCcombined</title></head>");
		out.println("<body>");
		out.println("<h1>Ooops...</h1>");
		out.println("Something went wrong during transmission of your input: " + bodymsg);
		out.println("Please inform the developer of this, and also let him know your browser type and version. Thank you.");
		out.println("</body></html>");;
		
	}
	
	
	private Map<EntryKey, ResultEntry> createAndExecuteQueries(String[] servers, String sequence, String allel, String length) {
		if (servers.length == 0 || StringUtils.isEmpty(sequence) || StringUtils.isEmpty(allel) || StringUtils.isEmpty(length)) {
			return null;
		}
		
		List<AbstractQuery> queries = getQueriesforServers(servers, sequence, allel, length);
		
		// TODO What's max num threads we want to allow?
		int numThreads = queries.size();
		ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		CompletionService<Set<TemporaryEntry>> completionService = new ExecutorCompletionService<Set<TemporaryEntry>>(
				executor);

		Map<EntryKey, ResultEntry> results = new HashMap<EntryKey, ResultEntry>();
		try {
			
			for (AbstractQuery query : queries) {
				
                                completionService.submit(query);
                                
			}
			
			for (int i = 0; i < queries.size(); i++) {
				Future<Set<TemporaryEntry>> future = completionService.take();
				Set<TemporaryEntry> res = future.get();

				for (TemporaryEntry temp : res) {
					ResultEntry value = results.get(temp.getKey());
					if (value == null) {
						// entry not yet in results map, let us create a new one.
						value = new ResultEntry(temp.getAllel(), temp.getSequence(), temp.getPosition());
						results.put(temp.getKey(), value);
					}
					value.setScore(temp.getAlgorithm(), temp.getScore());
				}
			}
                        executor.shutdown();
                        executor.awaitTermination(10, TimeUnit.MINUTES);

		} catch (InterruptedException e) {
			logger.error("Exception happened: ", e);
		} catch (ExecutionException e) {
			logger.error("Exception happened: ", e);
		}
		
		
		return results;

	}
	
	
	private List<AbstractQuery> getQueriesforServers(String[] servers, String sequence, String allel, String length) {
		List<AbstractQuery> queries = new ArrayList<AbstractQuery>();
		
		String[] lengths = length.split(",");
		
		QueryFactory factory = new QueryFactory();
		for (String server : servers) {
			for (String aLength : lengths) {
				AbstractQuery query = factory.createQueryForServer(server, sequence, allel, Integer.parseInt(aLength));
				if (query != null) {
    					queries.add(query);
				} else {
					logger.error("Query was null for server "+server+" and length "+length);
				}
			}
		}
		
		
		return queries;
	}
	
	
	private void createCsvFile(Map<EntryKey, ResultEntry> results, String delimiter, OutputStream out) throws IOException {
		List<EntryKey> aSortedEntryList = Util.asSortedList(results.keySet());
	    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
	    // first headers
	    writer.append("Position Start");
	    writer.append(delimiter);
	    writer.append("Region");
	    writer.append(delimiter);
	    writer.append("Length");
	    writer.append(delimiter);
	    writer.append("Sequence");
	    writer.append(delimiter);
	    for (Algorithm algo : Algorithm.values()) {
	    	writer.append(algo.toString());
	    	writer.append(delimiter);
	    }
	    writer.newLine();
	        
	    for (EntryKey anEntry : aSortedEntryList) {
	    	writer.append(String.valueOf(anEntry.getPosition())); // Position Start
	    	writer.append(delimiter);
	    	writer.append("\"" + String.valueOf(anEntry.getPosition()) + " - " + String.valueOf(anEntry.getPosition() + anEntry.getLength() - 1) + "\""); // Region
	    	writer.append(delimiter);
	    	writer.append(String.valueOf(anEntry.getLength())); // Length
	    	writer.append(delimiter);
	    	writer.append(anEntry.getSequence()); // Sequenz
	    	writer.append(delimiter);

	    	ResultEntry aResult = results.get(anEntry);
	        for (Algorithm algo : Algorithm.values()) {
	        	Double score = aResult.getScore(algo);
	        	String field = "N/A";
	        	if (score != null) {
	        		field = escapeForCSV(aResult.getScore(algo).toString(), delimiter);
	        	}
	            writer.append(field);
	            writer.append(delimiter);
	        }
	        writer.newLine();
	    }
	    
	    writer.flush();
	}
	
	private String escapeForCSV(String theString, String delimiter) {
        String field = theString.replace("\"", "\"\"");
        if (field.indexOf(delimiter) > -1 || field.indexOf('"') > -1) {
            field = '"' + field + '"';
        }
        return field;
	}
	


}
