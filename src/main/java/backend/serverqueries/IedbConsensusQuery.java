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


import backend.entries.Algorithm;
import backend.entries.TemporaryEntry;
import java.util.Arrays;
import java.util.List;

public class IedbConsensusQuery extends AbstractIedbQuery {
	
	
	public IedbConsensusQuery(String sequence, String allel, Integer length) {
		super(Algorithm.IedbConsensus,"consensus",sequence, allel, length);
	}
	
       
	@Override
	protected List<TemporaryEntry> processLine(String line) {
		String[] entries = line.split("\t");
	
		// Line:
		// allele, seq_num, start, end, length, peptide, consensus_percentile_rank, ann_ic50, ann_rank, smm_ic50, smm_rank, comblib_sidney2008_score, comblib_sidney2008_rank
		
                Double rank = Double.parseDouble(entries[6]);
                
                TemporaryEntry entry = new TemporaryEntry(entries[0],
                        entries[5],
                        Integer.parseInt(entries[2]),
                        getAlgorithm().toColumn(),
                        rank);
		return Arrays.asList(entry);
	}
	
}
