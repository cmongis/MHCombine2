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

public class EntryKey implements Comparable<EntryKey> {
	
	private final String allel;
	private final String sequence;
	private final int position;
	private final int length;
	
	public EntryKey(String allel, String sequence, int position) {
		this.allel = allel;
		this.sequence = sequence.toUpperCase();
		this.position = position;
		this.length = sequence.length();
	}

	
	
	public String getAllel() {
		return allel;
	}



	public String getSequence() {
		return sequence;
	}



	public int getPosition() {
		return position;
	}



	public int getLength() {
		return length;
	}



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
		EntryKey other = (EntryKey) obj;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(allel, other.allel);
		eb.append(sequence, other.sequence);
		eb.append(length, other.length);
		eb.append(position, other.position);
		return eb.isEquals();
	}



	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(37, 41);
		hcb.append(allel);
		hcb.append(sequence);
		hcb.append(length);
		hcb.append(position);
		return hcb.toHashCode();
	}



	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("EntryKey: ");
		sb.append("allel="+allel+", ");
		sb.append("sequence="+sequence+", ");
		sb.append("length="+length+", ");
		sb.append("position="+position);
		return sb.toString();
	}


	@Override
	public int compareTo(EntryKey o) {
		// return -1 if this is less than o
		// return 1 if this is greater than o
		// return 0 if this is equal to o
		if (o == null ) {
			return -1;
		}
		
		if (this.position < o.position) {
			return -1;
		} else if (this.position > o.position) {
			return 1;
		} else {
			// position = other.position
			return this.sequence.compareTo(o.sequence);
		}
	
	}

}
