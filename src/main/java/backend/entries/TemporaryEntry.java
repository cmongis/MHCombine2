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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import utils.Constants.Algorithm;

public class TemporaryEntry {


	private final EntryKey key;
	private final Algorithm algorithm;
	private final double score;

	
	public TemporaryEntry(String allel, String sequence, int position, Algorithm algorithm, double score) {
		this.key = new EntryKey(allel, sequence, position);
		this.algorithm = algorithm;
		this.score = score;
	}
	
	
	public String getSequence() {
		return key.getSequence();
	}
	
	public String getAllel() {
		return key.getAllel();
	}

	public int getLength() {
		return key.getLength();
	}

	public int getPosition() {
		return key.getPosition();
	}

	public EntryKey getKey() {
		return key;
	}




	public Algorithm getAlgorithm() {
		return algorithm;
	}




	public double getScore() {
		return score;
	}




	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != (getClass())) {
			return false;
		}
		TemporaryEntry other = (TemporaryEntry) obj;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(key, other.key);
//		Do not use the algorithm? Because from two different algorithms, we want to compare if they're the same...
//		eb.append(algorithm, other.algorithm);
		return eb.isEquals();
	}


	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(23, 19);
		hcb.append(key);
		hcb.append(algorithm);
		return hcb.toHashCode();
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("TemporaryEntry: ");
		sb.append("allel="+getAllel()+", ");
		sb.append("position="+getPosition()+", ");
		sb.append("sequence=\""+getSequence()+"\", ");
		sb.append("length="+getLength()+"; ");
		sb.append("algorithm="+algorithm+"; ");
		sb.append("score="+score);
		return sb.toString();
	}
	
	
	
	
}
