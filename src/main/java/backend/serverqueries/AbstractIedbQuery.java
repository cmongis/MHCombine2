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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utils.Constants.Algorithm;
import backend.entries.TemporaryEntry;

public abstract class AbstractIedbQuery extends AbstractQuery {
	
	private final Logger logger = LogManager.getLogger(AbstractIedbQuery.class);	
	
	// for querying the server
	//protected static final String url = "http://tools-api.iedb.org/tools_api/mhci/";
        protected static final String url  = "http://tools-cluster-interface.iedb.org/tools_api/mhci/";
	protected static final String methodName = "method";
	protected static final String sequenceName = "sequence_text";
	protected static final String alleleName = "allele";
	protected static final String lengthName = "length";
	
	
	protected final String sequence;
	protected final String allel;
	protected final Integer length;
	
	protected final Set<TemporaryEntry> results;
	
	public AbstractIedbQuery (String sequence, String allel, Integer length) {
		this.sequence = sequence;
		this.allel = allel;
    		this.length = length;
		this.results = new HashSet<TemporaryEntry>();
	}
	
	protected HttpEntity getFormData(String method) {
		List<NameValuePair> formdata = new ArrayList<NameValuePair>();
		formdata.add(new BasicNameValuePair(methodName, method));
		formdata.add(new BasicNameValuePair(sequenceName, sequence));
		formdata.add(new BasicNameValuePair(alleleName, allel));
		formdata.add(new BasicNameValuePair(lengthName, length.toString()));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formdata, Consts.UTF_8);
		return entity;
	}
	
	protected void processLine(String line, Algorithm algorithm) {
		String[] entries = line.split("\t");
		if (entries.length < 7) {
			logger.error("Output line "+line+" contains less than the mandatory 7 entries (allele, seq_num, start, end, length, peptide, ic50), did maybe something change??");
			return;
		}
		// Line:
		// allele, seq_num, start, end, length, peptide, score, [other stuff]
		TemporaryEntry entry = new TemporaryEntry(allel, entries[5], Integer.parseInt(entries[2]), algorithm, Double.parseDouble(entries[6]));
		results.add(entry);
	}
	
}
