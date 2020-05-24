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
import org.apache.http.entity.mime.MultipartEntityBuilder;
import backend.entries.Algorithm;
import backend.entries.ResultColumn;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author cyril
 */
public class NetMHCPan40Query extends AbstractNetMhcQuery {
    
    public NetMHCPan40Query(String sequence, String allel, Integer length) {
        super(Algorithm.NetMHCpan40,"/usr/opt/www/pub/CBS/services/NetMHCpan-4.0/NetMHCpan.cf",sequence, allel, length);
        
    }
    
    public String processAllel(String allel) {
        return allel.replace("*", "");
    }
     
    @Override
    protected List<TemporaryEntry> processLine(String line) {
        // Line contains (space separated)
        // "  Pos          HLA         Peptide       Core Of Gp Gl Ip Il        Icore        Identity   Score Aff(nM)   %Rank  BindLevel"
        //     1  HLA-A*01:01        SYFPEITH  -SYFPEITH  0  0  0  0  1     SYFPEITH        Sequence 0.02170 39535.2   60.00

        String allel = null;
        String sequence = null;
        Integer position = null;
        Double score = null;
        Double rank = null;
        String aLineToWork = line.trim().replaceAll("\\s+", " ");
        String[] aSplitLine = aLineToWork.split(" ");
        
        allel = this.allel; 
        sequence = aSplitLine[2];
        position = Integer.parseInt(aSplitLine[0]);
        score = Double.parseDouble(aSplitLine[11]); // take Aff[nM]
        rank = Double.parseDouble(aSplitLine[12]);
        TemporaryEntry scoreEntry = new TemporaryEntry(allel, sequence, position, getAlgorithm().toColumn(), score);
        TemporaryEntry rankEntry = new TemporaryEntry(allel, sequence, position, getAlgorithm().toColumn(ResultColumn.RANK), rank);
        //TemporaryEntry rankBaEntry = new TemporaryEntry(allel, sequence, position, getAlgorithm().toColumn(ResultColumn.RANK_EL), rankBa);
        return Arrays.asList(scoreEntry,rankEntry);
    }
    
    protected MultipartEntityBuilder preparePayload(MultipartEntityBuilder builder) {
        return builder.addTextBody("BApred", "on");
    }
    
}
