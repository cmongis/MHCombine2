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

import backend.entries.TemporaryEntry;
import backend.entries.Algorithm;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author cyril
 */
public class NetMHCpan28Query extends AbstractNetMhcQuery {

    public NetMHCpan28Query(String sequence, String allel, Integer length) {
        super(Algorithm.NetMHCpan28, "/usr/opt/www/pub/CBS/services/NetMHCpan-2.8/NetMHCpan.cf", sequence, allel, length);
    }

    @Override
    public String processSingleAllel(String allel) {
        return allel.replace("*", "");
    }

    protected List<TemporaryEntry> processLine(String line) {

 
        String aLineToWork = line.trim().replaceAll("\\s+", " ");
        String[] aSplitLine = aLineToWork.split(" ");
        
        String sequence = null;
        Integer position = null;
        Double score = null;
        String allel = findCorrespondingAllele(aSplitLine[1]);
        
        
        if (aSplitLine.length < 7) {
            logger().warn("not enough input! " + aLineToWork);
            return NO_ENTRY;
        } else if (aSplitLine.length > 9) {
            // Strong and weak binders have last entry "<= WB", which gives two fields in the array!
            logger().warn("too much input! " + aLineToWork);
            return NO_ENTRY;
        }

        // allel is returned "HLA-A*01:01" here, even though as input one had to provide "HLA-A01:01".
        sequence = aSplitLine[2];
        position = Integer.parseInt(aSplitLine[0]) + 1;
        score = Double.parseDouble(aSplitLine[5]); // take Aff[nM] 
        TemporaryEntry temporaryEntry = new TemporaryEntry(allel, sequence, position, getAlgorithm().toColumn(), score);
        return Arrays.asList(temporaryEntry);
    }

}
