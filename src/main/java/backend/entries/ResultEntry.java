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
package backend.entries;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import utils.Constants.Algorithm;

public final class ResultEntry {

	private final EntryKey key;
	private final Map<Algorithm, Double> scores;
	
        
        
	public ResultEntry(String allel, String sequence, int position) {
		this.key = new EntryKey(allel, sequence, position);
		scores = new HashMap<>();
	}

	public String getSequence() {
		return key.getSequence();
	}

	public int getLength() {
		return key.getLength();
	}
	
	public int getPosition() {
		return key.getPosition();
	}
	
	public String getAllel() {
		return key.getAllel();
	}
	
	public EntryKey getKey() {
		return key;
	}

	public Double getScore(Algorithm algo) {
		return scores.get(algo);
	}

	public void setScore(Algorithm algo, double score) {
		scores.put(algo, score);
	}


	/* ========================================================================
	 * Override these methods, as result is only identified by sequence and pos
	 * ========================================================================
	 */
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		ResultEntry other = (ResultEntry) obj;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(key, other.key);
		return eb.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(key);
		return hcb.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("ResultEntry: ");
		sb.append("allel="+getAllel()+", ");
		sb.append("position="+getPosition()+", ");
		sb.append("sequence=\""+getSequence()+"\", ");
		sb.append("length="+getLength()+"; ");
                
                for(Algorithm a : Algorithm.values()) {
                    sb
                            .append(a.name())
                            .append("=")
                            .append(scores.get(a))
                            .append(", ");
                }
                /*
		sb.append("NetMHC="+scores.get(Algorithm.NetMHC)+", ");
		sb.append("NetMHCpan="+scores.get(Algorithm.NetMHCpan)+", ");
		sb.append("IEDB_netMHCcons="+scores.get(Algorithm.IEDB_netMHCcons)+", ");
		sb.append("IEDB_ann="+scores.get(Algorithm.IEDB_ann)+", ");
		sb.append("IEDB_netMHCpan="+scores.get(Algorithm.IEDB_netMHCpan)+", ");
		sb.append("IEDB_pickpocket="+scores.get(Algorithm.IEDB_pickpocket)+", ");
		sb.append("IEDB_recommended="+scores.get(Algorithm.IEDB_recommended)+", ");
		sb.append("IEDB_consensus="+scores.get(Algorithm.IEDB_consensus)+", ");
		sb.append("IEDB_smmpmbec="+scores.get(Algorithm.IEDB_smmpmbec)+", ");
		sb.append("IEDB_smm="+scores.get(Algorithm.IEDB_smm)+", ");
		sb.append("SYFPEITHI="+scores.get(Algorithm.SYFPEITHI));
//		sb.append("BIMAS="+scores.get(Algorithm.BIMAS)+", ");
                  */
		return sb.toString();
	}

	
	


}
