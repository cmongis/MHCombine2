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

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import backend.entries.Algorithm;
import backend.entries.ResultColumnSuffix;
import backend.entries.TemporaryEntry;
import java.util.Arrays;
import java.util.List;

public class NetMHCPan30Query extends AbstractNetMhcQuery {

   
    public NetMHCPan30Query(String sequence, String allel, Integer length) {
        super(
                Algorithm.NetMHCpan30
                , "/usr/opt/www/pub/CBS/services/NetMHCpan-3.0/NetMHCpan.cf"
                , sequence, allel, length
        );

    }
    
    
    @Override
    protected List<TemporaryEntry> processLine(String line) {
   
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
        position = Integer.parseInt(aSplitLine[0]);

        sequence = aSplitLine[2];
        score = Double.parseDouble(aSplitLine[12]);
        rank = Double.parseDouble((aSplitLine[13]));
        // take Affinity[nM] 
        TemporaryEntry affinityEntry = new TemporaryEntry(allel, sequence, position, getAlgorithm().toColumn(), score);
        TemporaryEntry rankEntry = new TemporaryEntry(allel, sequence, position, getAlgorithm().toColumn(ResultColumnSuffix.RANK), rank);
        
        return Arrays.asList(rankEntry,affinityEntry);
    }

    @Override
    public String processAllel(String allel) {
        return allel.replace("*", "");
    }
    
}
