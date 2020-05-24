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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.entries.Algorithm;
import backend.entries.TemporaryEntry;
import java.util.Arrays;
import java.util.List;

public class IedbRecommendedQuery extends AbstractIedbQuery {
	
	private final Logger logger = LogManager.getLogger(IedbRecommendedQuery.class);

        
       
	public IedbRecommendedQuery(String sequence, String allel,
			Integer length) {
		super(Algorithm.IEDB_recommended,"recommended",sequence, allel, length);
	}

        
        /*
	public Set<TemporaryEntry> queryServer() {
		CloseableHttpClient client = HttpClients.createSystem();

		HttpPost postRequest = new HttpPost(url);
		HttpEntity entity = getFormData("recommended");
		postRequest.setEntity(entity);

		CloseableHttpResponse response = null;
		try {
			response = client.execute(postRequest);

			// consume input and create result.
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			String line = null;
			boolean processing = false;
			do {
				line = reader.readLine();
                                System.out.println(line);
				if (line == null) {
					break;
				}
				if (processing) {
					processLine(line, Algorithm.IEDB_recommended);
				} else {
					if (line.startsWith("allele")) {
						// next line will be first dataset.
						processing = true;
					}
				}
			} while (true);

			EntityUtils.consume(response.getEntity());
		} catch (IOException e) {
			logger.error("Exception happened while executing POST request. Abort.", e);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					logger.error("Exception happened while closing response. Abort.", e);
				}
			}
		}

		return results;

	}*/

	@Override
	protected List<TemporaryEntry> processLine(String line) {
		String[] entries = line.split("\t");
		if (entries.length < 8) {
			logger.error("Output line "+line+" contains less than the mandatory 8 entries (allele, seq_num, start, end, length, peptide, method, precentile_rank, [other]), did maybe something change??");
			return Arrays.asList();
		}
		// Line:
		// allele, seq_num, start, end, length, peptide, method, percentile_rank, ann_ic50, ann_rank, smm_ic50, smm_rank, comblib_sidney2008_score, comblib_sidney2008_rank, netmhcpan_ic50, netmhcpan_rank
		TemporaryEntry entry = new TemporaryEntry(entries[0], entries[5], Integer.parseInt(entries[2]), getAlgorithm().toColumn(), Double.parseDouble(entries[8]));
		return Arrays.asList(entry);
	}
}
