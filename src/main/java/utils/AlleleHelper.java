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

public class AlleleHelper {

	public static void append(StringBuilder sb, String s) {
		if (s != null) {
			sb.append(s+Constants.CRLF);
		}
	}

	@Deprecated
	public static String getBIMASAllelForGeneralAllel(String generalAllel) {
		switch(generalAllel) {
		case "HLA-A*02:01":
			return "A_0201";
		case "HLA-A*02:05":
			return "A_0205";
		case "HLA-A*24:02":
			return "A24";
		case "HLA-A*03:01":
			return "A3";
		case "HLA-A*68:01":
			return "A68.1";
		case "HLA-A*11:01":
			return "A_1101";
		case "HLA-A*31:01":
			return "A_3101";
		case "HLA-A*33:02":
			return "A_3302";
		case "HLA-B*14:01":
			return "B14";
		case "HLA-B*60:01":
			return "B60";
		case "HLA-B*61:01":
			return "B61";
		case "HLA-B*15:01":
			return "B62";
		case "HLA-B*07:02":
			return "B7";
		case "HLA-B*08:01":
			return "B8";
		case "HLA-B*27:02":
			return "B_2702";
		case "HLA-B*27:05":
			return "B2705";
		case "HLA-B*35:01":
			return "B_3501";
		case "HLA-B*38:01":
			return "B_3801";
		case "HLA-B*39:01":
			return "B_3901";
		case "HLA-B*39:02":
			return "B_3902";
		case "HLA-B*44:03":
			return "B_4403";
		case "HLA-B*51:01":
			return "B_5101";
		case "HLA-B*51:02":
			return "B_5102";
		case "HLA-B*51:03":
			return "B_5103";
		case "HLA-B*52:01":
			return "B_5201";
		case "HLA-B*58:01":
			return "B_5801";
		case "HLA-C*03:01":
			return "Cw_0301";
		case "HLA-C*04:01":
			return "Cw_0401";
		case "HLA-C*06:02":
			return "Cw_0602";
		case "HLA-C*07:02":
			return "Cw_0702";
		default:
			return null;
		}
	}

	@Deprecated
	public static String getGeneralAllelForBIMASAllel(String bimasAllel) {
		switch(bimasAllel) {
		case "A_0201":
			return "HLA-A*02:01";
		case "A_0205":
			return "HLA-A*02:05";
		case "A24":
			return "HLA-A*24:02";
		case "A3":
			return "HLA-A*03:01";
		case "A68.1":
			return "HLA-A*68:01";
		case "A_1101":
			return "HLA-A*11:01";
		case "A_3101":
			return "HLA-A*31:01";
		case "A_3302":
			return "HLA-A*33:02";
		case "B14":
			return "HLA-B*14:01";
		case "B60":
			return "HLA-B*60:01";
		case "B61":
			return "HLA-B*61:01";
		case "B62":
			return "HLA-B*15:01";
		case "B7":
			return "HLA-B*07:02";
		case "B8":
			return "HLA-B*08:01";
		case "B_2702":
			return "HLA-B*27:02";
		case "B2705":
			return "HLA-B*27:05";
		case "B_3501":
			return "HLA-B*35:01";
		case "B_3801":
			return "HLA-B*38:01";
		case "B_3901":
			return "HLA-B*39:01";
		case "B_3902":
			return "HLA-B*39:02";
		case "B_4403":
			return "HLA-B*44:03";
		case "B_5101":
			return "HLA-B*51:01";
		case "B_5102":
			return "HLA-B*51:02";
		case "B_5103":
			return "HLA-B*51:03";
		case "B_5201":
			return "HLA-B*52:01";
		case "B_5801":
			return "HLA-B*58:01";
		case "Cw_0301":
			return "HLA-C*03:01";
		case "Cw_0401":
			return "HLA-C*04:01";
		case "Cw_0602":
			return "HLA-C*06:02";
		case "Cw_0702":
			return "HLA-C*07:02";
		default:
			return null;
		}

	}
	
	
	public static String getSyfpeithiAllelForGeneralAllel(String generalAllel) {
		switch (generalAllel) {
		case "HLA-A*01:01":
			return "HLA-A*01";
		case "HLA-A*02:01":
			return "HLA-A*02:01";
		case "HLA-A*03:01":
			return "HLA-A*03";
		case "HLA-A*11:01":
			return "HLA-A*11:01";
		case "HLA-A*24:02":
			return "HLA-A*24:02";
		case "HLA-A*26:01":
			return "HLA-A*26";
		case "HLA-A*38:01":
			return "HLA-A*68:01";
		case "HLA-B*07:02":
			return "HLA-B*07:02";
		case "HLA-B*08:01":
			return "HLA-B*08";
		case "HLA-B*13:01":
			return "HLA-B*13";
		case "HLA-B*14:02":
			return "HLA-B*14:02";
		case "HLA-B*15:01":
			return "HLA-B*15:01(B62)";
		case "HLA-B*15:10":
			return "HLA-B*15:10";
		case "HLA-B*15:16":
			return "HLA-B*15:16";
		case "HLA-B*18:01":
			return "HLA-B*18";
		case "HLA-B*27:05":
			return "HLA-B*27:05";
		case "HLA-B*27:09":
			return "HLA-B*27:09";
		case "HLA-B*35:01":
			return "HLA-B*35:01";
		case "HLA-B*37:05":
			return "HLA-B*37";
		case "HLA-B*38:01":
			return "HLA-B*38:01";
		case "HLA-B*39:01":
			return "HLA-B*39:01";
		case "HLA-B*39:02":
			return "HLA-B*39:02";
		case "HLA-B*40:01":
			return "HLA-B*40:01(B60)";
		case "HLA-B*41:01":
			return "HLA-B*41:01";
		case "HLA-B*44:02":
			return "HLA-B*44:02";
		case "HLA-B*45:01":
			return "HLA-B*45:01";
		case "HLA-B*47:01":
			return "HLA-B*47:01";
		case "HLA-B*49:01":
			return "HLA-B*49:01";
		case "HLA-B*50:01":
			return "HLA-B*50:01";
		case "HLA-B*51:01":
			return "HLA-B*51:01";
		case "HLA-B*53:01":
			return "HLA-B*53:01";
		case "HLA-B*57:01":
			return "HLA-B*57:01";
		case "HLA-B*58:02":
			return "HLA-B*58:02";
		default: 
			return null;
		}
	}
	

	public static String getNetMhcPanAllelForGeneralAllel(String generalAllel) {
		return generalAllel.replace("*", "");
	}
	
	public static String getNetMhcAllelForGeneralAllel(String generalAllel) {
		return generalAllel.replace("*", "").replace("-","");
	}

}
