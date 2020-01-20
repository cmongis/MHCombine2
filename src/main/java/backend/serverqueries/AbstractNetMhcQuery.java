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
package backend.serverqueries;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.entries.TemporaryEntry;

public abstract class AbstractNetMhcQuery extends AbstractQuery {
	
	private final Logger logger = LogManager.getLogger(AbstractNetMhcQuery.class);
	
	// form we want to query (the same for all NetMHC servers)
	private final String baseURL = "http://www.cbs.dtu.dk";
	private final String queryForm = "/cgi-bin/webface2.fcgi";
	
	// Query specific parameters
	protected final String sequence;
	protected final String allel;
	protected final Integer length;

	// storing the final result
	protected final Set<TemporaryEntry> results;

	public AbstractNetMhcQuery(String sequence, String allel, Integer length) {
		this.sequence = sequence;
                
		this.allel = allel;
		this.length = length;
		this.results = new HashSet<TemporaryEntry>();
                logger.info("Creating NetMHC query");
	}

	@Override
	protected Set<TemporaryEntry> queryServer() {
		
		// Step 1: Submit Job
		HttpEntity entity = preparePayload();
		HttpPost postRequest = new HttpPost(baseURL + queryForm);
		postRequest.setEntity(entity);
                //postRequest.setHeader("Referer", "http://www.cbs.dtu.dk/services/NetMHC-3.4/");
                postRequest.setHeader("Content-Type","multipart/form-data; boundary=---------------------------183079827324139952612037066");
                postRequest.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"); 
                postRequest.setHeader("Accept-Language","en-US,en;q=0.5");
                postRequest.setHeader("Connection", "keep-alive");
                postRequest.setHeader("User-Agent","Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:54.0) Gecko/20100101 Firefox/54.0");
                //postRequest.setHeader("Cookie","__utma=151498347.2065779939.1499803940.1499803940.1499803940.1; __utmb=151498347.3.10.1499803940; __utmc=151498347; __utmz=151498347.1499803940.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); __utmt=1");
		String location = null;
		int statuscode = HttpStatus.SC_OK;
                System.out.println(postRequest.toString());
		CloseableHttpClient client = HttpClients.createSystem();
		CloseableHttpResponse originalResponse = null;
		try {
			// execute POST to submit Job
			originalResponse = client.execute(postRequest);
			statuscode = originalResponse.getStatusLine().getStatusCode();
			if (statuscode == HttpStatus.SC_OK) {
				// No redirect? Process the original result.
                                logger.info("No redirect, processing the result.");
				processResponse(originalResponse);
				return results;
			} else if (statuscode == HttpStatus.SC_MOVED_TEMPORARILY) {
                                logger.info("Received redirect. Querying the job id ");
				// redirect, as expected. They show us the jobId of our result 				// in the Location header
				Header[] header = originalResponse.getHeaders("Location");
				if (header.length == 0) {
					// no redirect location, this means no idea where our result is.
					// abort.
					 logger.error("Retrieving header for this query returned no location. Cannot process this redirect.");
					return results;
				}
				location = header[0].getValue();
			} else {
				logger.warn("Attempted to POST to server, but got back status code of "+statuscode+" with Status line "+originalResponse.getStatusLine());
			}
			
			EntityUtils.consume(originalResponse.getEntity());
		} catch (IOException e) {
			logger.error("Exception while executing POST. Abort.", e);
			return results; // do not recover from this.
		} finally {
			if (originalResponse != null) {
				try {
					originalResponse.close();
				} catch (IOException e) {
					logger.error("Exception while closing POST's original response. Abort.", e);
					return results; // do not recover from this.
				}
			}
		}

		
		// Step 2: Query JobId until Result is here
		

		// if we do not have any redirect location, we do not know where our
		// results are...
		if (location == null) {
			logger.warn("No redirect location! Abort.");
			return results; // cannot recover
		}
 		boolean responseIsWait = true;
		while (responseIsWait) {
			HttpGet getRequest = new HttpGet(baseURL + location);
			CloseableHttpResponse response = null;
			try {
				response = client.execute(getRequest);
				statuscode = response.getStatusLine().getStatusCode();
				if (statuscode == HttpStatus.SC_OK) {
					responseIsWait = processResponse(response);
				} else {
					logger.error("StatusLine returned was "+response.getStatusLine()+". Abort.");
					break; // do not recover?
				}
				// needed to properly close connection.
				EntityUtils.consume(response.getEntity());
			} catch (IOException e) {
				logger.error("Exception happened while querying for results. Abort.", e);
				return results; // do not recover from this.
			} finally {
				try {
					response.close();
				} catch (IOException e) {
					logger.error("Exception happened while closing response. Abort.", e);
					return results; // do not recover from this.
				}
			}

			if (responseIsWait) {
				try {
                                        logger.info("waiting 2 seconds");
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					logger.error("Interrupted while sleeping!", e);
				}
			}
		}

		return results;
	}

	/*
	 * prepare payload for POST request to NetMHC Servers
	 */
	protected abstract HttpEntity preparePayload();

	/*
	 * look for line containing html title -> if it states "prediction results", you have results
	 * else you have to repeat querying the URL
	 * 
	 * return true if results are NOT available, else false!
	 */
	protected abstract boolean processResponse(CloseableHttpResponse response) throws IllegalStateException, IOException;
	

}
