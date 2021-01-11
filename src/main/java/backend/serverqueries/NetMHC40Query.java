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
import backend.entries.ResultColumn;
import backend.entries.ResultColumnSuffix;
import backend.entries.TemporaryEntry;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class NetMHC40Query extends AbstractNetMhcQuery {

    private final Logger logger = Logger.getLogger(NetMHC40Query.class.getName());

    
    public NetMHC40Query(String sequence, String allel, Integer length) {
        super(Algorithm.NetMHC40,"/usr/opt/www/pub/CBS/services/NetMHC-4.0/NetMHC.cf",sequence, allel, length);       
    }
    
    
   public NetMHC40Query(Algorithm algorithm, String configFile,String sequence, String allel, Integer length) {
       super(algorithm, configFile, sequence, allel, length);
   }
   
 
    @Override
    protected List<TemporaryEntry> processLine(String line) {
        /* Line contains (space separated)
          pos          HLA      peptide         Core Offset  I_pos  I_len  D_pos  D_len        iCore        Identity 1-log50k(aff) Affinity(nM)    %Rank  BindLevel
          -----------------------------------------------------------------------------------
          0    HLA-A0101    MHGDTPTLH    MHGDTPTLH      0      0      0      0      0    MHGDTPTLH sp_P03129_VE7_H         0.050     29171.04    33.00
        */
        String allel = null;
        String sequence = null;
        Integer position = null;
        Double score = null;
        Double rank = null;
       

        String aLineToWork = line.trim().replaceAll("\\s+", " ");
        String[] aSplitLine = aLineToWork.split(" ");
        

        // Allel of NetMHC is "HLA-A0101", but everyone else gives "HLA-A*01:01" -> return original allel
        allel = this.allel;

        // add offset to cope with NetMHC being 0-indexed
        position = Integer.parseInt(aSplitLine[0]) + 1;

        sequence = aSplitLine[2];
        score = Double.parseDouble(aSplitLine[12]);
        rank = Double.parseDouble((aSplitLine[13]));
        // take Affinity[nM] 
        TemporaryEntry affinityEntry = new TemporaryEntry(allel, sequence, position, getAlgorithm().toColumn(), score);
        TemporaryEntry rankEntry = new TemporaryEntry(allel, sequence, position, getAlgorithm().toColumn(ResultColumnSuffix.RANK), rank);
        
        return Arrays.asList(rankEntry,affinityEntry);
    }

}
