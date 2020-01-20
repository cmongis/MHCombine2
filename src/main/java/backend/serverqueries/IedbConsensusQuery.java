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
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import utils.Constants.Algorithm;
import backend.entries.TemporaryEntry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IedbConsensusQuery extends AbstractIedbQuery {
	
	//private final Logger logger = LogManager.getLogger(IedbConsensusQuery.class);
	private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
	
	public IedbConsensusQuery(String sequence, String allel, Integer length) {
		super(sequence, allel, length);
	}
	
		
	public Set<TemporaryEntry> queryServer() {

		CloseableHttpClient client = HttpClients.createSystem();
		
		HttpPost postRequest = new HttpPost(url);
		HttpEntity entity = getFormData("consensus");
               
		postRequest.setEntity(entity);
		
		CloseableHttpResponse response = null;
		try {
			response = client.execute(postRequest);
			System.out.println("Going for it");
			// consume input and create result.
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = null;
			boolean processing = false;
			do {
				line = reader.readLine();
                                System.out.println(line);
				if (line == null) {
					break;
				}
				if (processing) {
					processLine(line, Algorithm.IEDB_consensus);
				} else {
					if (line.startsWith("allele")) {
						// next line will be first dataset.
						processing = true;
					}
				}
			} while (true);
			
			
			EntityUtils.consume(response.getEntity());
		} catch (IOException e) {
			logger.log(Level.SEVERE,"Exception happened while executing POST request. Abort.", e);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					logger.log(Level.SEVERE,"Exception happened while closing response. Abort.", e);
				}
			}
		}
		
		return results;

	}
		
	
	@Override
	protected void processLine(String line, Algorithm algorithm) {
		String[] entries = line.split("\t");
		if (entries.length < 8) {
			logger.log(Level.WARNING,"Output line "+line+" contains less than the mandatory 8 entries (allele, seq_num, start, end, length, peptide, consensus_percentile_rank, ann_ic50, ann_rank, [other]), did maybe something change??");
			return;
		}
		// Line:
		// allele, seq_num, start, end, length, peptide, consensus_percentile_rank, ann_ic50, ann_rank, smm_ic50, smm_rank, comblib_sidney2008_score, comblib_sidney2008_rank
		TemporaryEntry entry = new TemporaryEntry(entries[0], entries[5], Integer.parseInt(entries[2]), algorithm, Double.parseDouble(entries[6]));
		results.add(entry);
	}
	
}
