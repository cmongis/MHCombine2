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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.entries.Algorithm;
import backend.entries.TemporaryEntry;
import java.util.Arrays;
import java.util.List;

public class NetMHCPan30Query extends NetMHC40Query {

   
    public NetMHCPan30Query(String sequence, String allel, Integer length) {
        super(
                Algorithm.NetMHCpan30
                , "/usr/opt/www/pub/CBS/services/NetMHCpan-3.0/NetMHCpan.cf"
                , sequence, allel, length
        );

    }

    @Override
    public String processAllel(String allel) {
        return allel.replace("*", "");
    }
    /*
    protected List<TemporaryEntry> processLine(String line) {
        // Line contains (space separated)
        // "  Pos          HLA         Peptide       Core Of Gp Gl Ip Il        Icore        Identity   Score Aff(nM)   %Rank  BindLevel"
        //     1  HLA-A*01:01        SYFPEITH  -SYFPEITH  0  0  0  0  1     SYFPEITH        Sequence 0.02170 39535.2   60.00

        String allel = null;
        String sequence = null;
        Integer position = null;
        Double score = null;

        String aLineToWork = line.trim().replaceAll("\\s+", " ");
        String[] aSplitLine = aLineToWork.split(" ");
        if (aSplitLine.length < 14) {

            logger().warn("not enough input! " + aLineToWork);
            return Arrays.asList();
        } else if (aSplitLine.length > 16) {
            // Strong and weak binders have last entry "<= WB", which gives two fields in the array!
            logger().warn("too much input! " + aLineToWork);
            return Arrays.asList();
        }

        // allel is returned "HLA-A*01:01" here, even though as input one had to provide "HLA-A01:01".
        allel = this.allel; //aSplitLine[1];
        sequence = aSplitLine[2];
        position = Integer.parseInt(aSplitLine[0]);
        score = Double.parseDouble(aSplitLine[12]); // take Aff[nM] 
        TemporaryEntry temporaryEntry = new TemporaryEntry(allel, sequence, position, getAlgorithm().toColumn(), score);
        return Arrays.asList(temporaryEntry);
    }*/

}
