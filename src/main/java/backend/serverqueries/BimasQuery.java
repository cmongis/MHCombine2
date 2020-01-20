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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utils.AlleleHelper;
import utils.Constants.Algorithm;
import backend.entries.TemporaryEntry;
@Deprecated
public class BimasQuery extends AbstractQuery {
	
	private final Logger logger = LogManager.getLogger(BimasQuery.class);

	private String baseURL = "http://www-bimas.cit.nih.gov";
	private String queryForm = "/cgi-bin/molbio/ken_parker_comboform";
			
	private final String sequence;
	private final String originalAllel;
	private final String allel;
	private final Integer length;
	
	// form parameter names & some default values
	private final String sequenceText = "inseq";
	private final String allelText = "hla_molecule_type";
	private final String lengthText = "subsequence_length";
	private final String resultLimitMethodText = "result_limit_method";
	private final String resultLimitMethodValue = "explicit number";
	private final String numResultsToDisplayText = "number_of_results_to_display";
	private final String numResultsToDisplayValue = "250";
	private final String cutoffScoreText = "cutoff_score";
	private final String cutoffScoreValue = "100";
	private final String echoSeqText = "echo_inseq";
	private final String echoSeqValue = "Y";
	private final String hiddenEchoFormatText = "echo_format";
	private final String hiddenEchoFormatValue = "Numbered lines (useful)";
	private final String hiddenProgramText = "program";
	private final String hiddenProgramValue ="KenParker HTML Form .9beta";
	
	private final String mimeType = "application/x-www-form-urlencoded";
	private final String refererText = "Referer";
	private final String refererValue = "http://www-bimas.cit.nih.gov/molbio/hla_bind/";
	
	
	
	private final Set<TemporaryEntry> results;
	
	public BimasQuery(String sequence, String allel, int length) {
		this.sequence = sequence;
		this.originalAllel = allel;
		this.allel = AlleleHelper.getBIMASAllelForGeneralAllel(originalAllel);
		this.length = length;
		this.results = new HashSet<TemporaryEntry>();
	}
	
	@Override
	protected Set<TemporaryEntry> queryServer() {		
		List<NameValuePair> formData = new ArrayList<NameValuePair>();
		formData.add(new BasicNameValuePair(allelText, allel));
		formData.add(new BasicNameValuePair(lengthText, length.toString()));
		formData.add(new BasicNameValuePair(resultLimitMethodText, resultLimitMethodValue));
		formData.add(new BasicNameValuePair(numResultsToDisplayText, numResultsToDisplayValue));
		formData.add(new BasicNameValuePair(cutoffScoreText, cutoffScoreValue));
		formData.add(new BasicNameValuePair(sequenceText, sequence));
		formData.add(new BasicNameValuePair(echoSeqText, echoSeqValue));
		formData.add(new BasicNameValuePair(hiddenEchoFormatText, hiddenEchoFormatValue));
		formData.add(new BasicNameValuePair(hiddenProgramText, hiddenProgramValue));

		CloseableHttpResponse response = null;
		try {
			HttpEntity entity = new UrlEncodedFormEntity(formData);
			
			HttpPost postRequest = new HttpPost(baseURL+queryForm);
			postRequest.setEntity(entity);
			postRequest.setHeader("Content-Type", mimeType);
			postRequest.setHeader(refererText, refererValue);
			
			CloseableHttpClient client = HttpClients.createSystem();
			
			logger.info("Querying server "+baseURL+queryForm);
			
			response = client.execute(postRequest);
			HttpEntity responseEntity = response.getEntity();
			
			int statuscode = response.getStatusLine().getStatusCode();
			if (statuscode != HttpStatus.SC_OK) {
				logger.error("Error while executing. Status Line was: "+response.getStatusLine());
			}
			
			// consume input and create result.
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					responseEntity.getContent()));
			String line = null;
			boolean processing = false;
			do {
				line = reader.readLine();
				if (line == null) {
					break;
				}
				if (processing) {
					if (line.startsWith("</TABLE>")) {
						break;
					}
					// TODO re-enable if BIMAS should be used again!
//					processLine(line, Algorithm.BIMAS);
				} else {
					if (line.startsWith("<TR><TH>Rank</TH>")) {
						// next line will be first dataset.
						processing = true;
					}
				}
			} while (true);

			EntityUtils.consume(responseEntity);
		} catch (IOException e) {
			logger.error("Exception while executing POST request. Abort.", e);
			return results;
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					logger.error("Exception while closing BIMAS response.", e);
				}
			}
		}

		
		return results;
	}

	@Override
	protected void processLine(String line, Algorithm algorithm) {
		// Line:
		// rank, Start position, Subsequence Residue Listing, Score
		
		// replace first all "</TD>" with ";" to mark where table entry ends, then remove all html tags from string -> only values remain :)
		line = line.replaceAll("</TD>", ";").replaceAll("<[^<>]*>", "");
		
		String[] entries = line.split(";");
		if (entries.length != 4) {
			logger.error("Output line "+line+" contains less or more than the mandatory 4 table entries (rank, Start position, Subsequence Residue Listing, Score), did maybe something change??");
			return;
		}

		TemporaryEntry entry = new TemporaryEntry(originalAllel, entries[2].trim(), Integer.parseInt(entries[1].trim()), algorithm, Double.parseDouble(entries[3].trim()));
		results.add(entry);

	}

}
