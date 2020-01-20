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
import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;

/**
 *
 * @author cyril
 */
public class NetMHC34Query extends NetMHCQuery{
     private final String configFileValue = "/usr/opt/www/pub/CBS/services/NetMHC-3.4/NetMHC.cf";
    
    Pattern pattern = Pattern.compile("\\s+([^\\s])+");
    
    Logger logger = LogManager.getLogger(this.getClass());
    
    public NetMHC34Query(String sequence, String allel, Integer length) {
        super(sequence, allel, length);
        logger.info("Creating request");
        setConfigFile(configFileValue);
        this.lengthName  = "peplen";
        this.masterValue = "2";
        this.algorithm = Constants.Algorithm.NetMHC34;
        
    }
    
    @Override
    protected HttpEntity preparePayload() {

        String lengthValue = length == ALL_LENGTHS_CODE ? ALL_LENGTH_VALUE : length.toString();
         
        MultipartEntityBuilder builder = MultipartEntityBuilder
                .create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setBoundary(boundary)
                
                .addTextBody(configFileName, configFileValue)
                //.addTextBody(inputTypeName, inputTypeValue)
                .addTextBody(sequenceName, sequence)
                .addBinaryBody(sequenceFileName, sequenceFileValue, ContentType.APPLICATION_OCTET_STREAM, "")
                 .addTextBody(lengthName, lengthValue)
                //.addTextBody(peptideName, peptideValue)
                
                //.addBinaryBody(peptideFileName, sequenceFileValue, ContentType.APPLICATION_OCTET_STREAM, "")
                
                //.addBinaryBody(sequenceFileName, sequenceFileValue, ContentType.APPLICATION_OCTET_STREAM, "") // file may not be null
               
              
                //.addBinaryBody(peptideFileName, sequenceFileValue, ContentType.APPLICATION_OCTET_STREAM, "") // file may not be null
                .addTextBody(masterName, masterValue)
                .addTextBody(slave0Name, netMhcAllel)
              
                .addTextBody(alleleName, netMhcAllel);
              //    .addTextBody(thresholdStrongName, thresholdStrongValue)
               // .addTextBody(thresholdWeakName, thresholdWeakValue);

        // specific entity building
        //builder = preparePayload(builder);
        
        HttpEntity entity = builder.build();
        
        try {
        entity.writeTo(System.out);
        
        }catch(IOException e) {
            
        }
        return entity;
    }
    
    
    @Override
    public String processAllel(String allel) {
       return allel.replace("*", "");
    }
    /*
    @Override
    public MultipartEntityBuilder preparePayload(MultipartEntityBuilder builder) {
        return builder
                .addBinaryBody(sequenceFileName, sequenceFileValue, ContentType.APPLICATION_OCTET_STREAM, "");
               //.addBinaryBody(sequenceFileName, getEmptyFile(), ContentType.APPLICATION_OCTET_STREAM,"");
    }*/
    
    
    @Override
     protected void processLine(String line, Constants.Algorithm anAlgorithm) {
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
            return;
        } else if (aSplitLine.length > 7) {
            // Strong and weak binders have last entry "<= WB", which gives two fields in the array!
            logger.warn("too much input! " + aLineToWork);
            return;
        }

        // allel is returned "HLA-A*01:01" here, even though as input one had to provide "HLA-A01:01".
        //allel = aSplitLine[5];
        sequence = aSplitLine[1];
        position = Integer.parseInt(aSplitLine[0])+1;
        score = Double.parseDouble(aSplitLine[3]); // take Aff[nM] 
        TemporaryEntry temporaryEntry = new TemporaryEntry(allel, sequence, position, anAlgorithm, score);
         results.add(temporaryEntry);
         super.processLine(line, anAlgorithm);
     }
}
