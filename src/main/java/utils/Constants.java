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
package utils;


public class Constants {
	
	public static String CRLF = "\r\n";
	
	public static enum Algorithm {
		NetMHC40("NetMHC 4.0"),
		NetMHCpan30("NetMHCpan 3.0"),
                NetMHCpan28("NetMHCpan 2.8"),
                NetMHCpan40("NetMHCpan 4.0"),
                NetMHC34("NetMHC 3.4"),
		IEDB_netMHCcons("NetMHCcons 1.1"),
		//IEDB_ann("NetMHC 3.4 = IEDB ann"),
		//IEDB_netMHCpan("NetMHCpan 2.8"),
		IEDB_pickpocket("Pickpocket 1.1"),
		IEDB_recommended("IEDB Recommended"),
		IEDB_consensus("IEDB Consensus"),
		IEDB_smmpmbec(" IEDB SMMPMBEC"),
		IEDB_smm("IEDB SMM"),
		SYFPEITHI("SYFPEITHI");
//		BIMAS("BIMAS");
		
		private final String printName;
		
		private Algorithm(String printName) {
			this.printName = printName;
		}
		
		@Override
		public String toString() {
			return this.printName;
		}
		
	};

}
