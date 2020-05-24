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
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import backend.entries.Algorithm;
import backend.entries.ResultColumn;
import java.util.Arrays;
import java.util.List;
import org.apache.http.entity.mime.MultipartEntityBuilder;

/**
 *
 * @author cyril
 */
public class NetMHC34Query extends AbstractNetMhcQuery {

    private final Logger logger = LogManager.getLogger(this.getClass());

    public NetMHC34Query(String sequence, String allel, Integer length) {
        super(Algorithm.NetMHC34, "/usr/opt/www/pub/CBS/services/NetMHC-3.4/NetMHC.cf", sequence, allel, length);

        setLengthName("peplen");
        setMasterValue("2");

    }

    @Override
    public String processAllel(String allel) {
        return allel.replace("*", "");
    }

    protected List<TemporaryEntry> processLine(String line) {
        logger.info(line);
        //    0  HLA-A*02:01     MHQKRTAM sp_P03126_VE6_H         0.036     33984.93    50.00
        String allel = this.allel;
        String sequence = null;
        Integer position = null;
        Double score = null;

        String aLineToWork = line.trim().replaceAll("\\s+", " ");
        String[] aSplitLine = aLineToWork.split(" ");
        if (aSplitLine.length < 6) {
            logger.warn("not enough input! " + aLineToWork);
            return NO_ENTRY;
        } else if (aSplitLine.length > 7) {
            // Strong and weak binders have last entry "<= WB", which gives two fields in the array!
            logger.warn("too much input! " + aLineToWork);
            return NO_ENTRY;
        }

        // allel is returned "HLA-A*01:01" here, even though as input one had to provide "HLA-A01:01".
        //allel = aSplitLine[5];
        sequence = aSplitLine[1];
        position = Integer.parseInt(aSplitLine[0]) + 1;
        score = Double.parseDouble(aSplitLine[3]); // take Aff[nM] 
        TemporaryEntry scoreEntry = new TemporaryEntry(allel, sequence, position, getAlgorithm().toColumn(), score);

        return Arrays.asList(scoreEntry);
    }
}
