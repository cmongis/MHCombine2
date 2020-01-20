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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;

import utils.Constants.Algorithm;
import backend.entries.TemporaryEntry;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;

public class NetMHCQuery extends AbstractNetMhcQuery {

    private final Logger logger = Logger.getLogger(NetMHCQuery.class.getName());

    protected final String netMhcAllel;

    // form parameters names
    protected final String configFileName = "configfile";
    protected final String inputTypeName = "inp";
    protected final String sequenceName = "SEQPASTE";
    protected final String sequenceFileName = "SEQSUB"; // not required
    protected String lengthName = "len";
    protected final String peptideName = "PEPPASTE";
    protected final String peptideFileName = "PEPSUB"; // not required
    protected final String masterName = "master";
    protected final String slave0Name = "slave0";
    protected final String alleleName = "allele";
    protected final String thresholdStrongName = "thrs";
    protected final String thresholdWeakName = "thrw";

    protected static final Integer ALL_LENGTHS_CODE = 0;
    protected static final String ALL_LENGTH_VALUE = "8,9,10,11";
    // form parameters default
    private String configFileValue = "/usr/opt/www/pub/CBS/services/NetMHC-4.0/NetMHC.cf";
    protected final String inputTypeValue = "0"; // 0 means FASTA, 1 means Peptides
    protected final File sequenceFileValue = new File(new File(System.getProperty("java.io.tmpdir")),"netmhcempty"); // not required
    protected final String peptideValue = "";
    // private final File peptideFileValue = null; // not required
    protected String masterValue = "1";
    protected final String thresholdStrongValue = "0.5";
    protected final String thresholdWeakValue = "2";

    // response specific structures
    protected final String htmlTitleTag = "<title>";
    protected final String predictionResultTitle = "prediction results";
    protected final String predictionResultTableHeader = "pos ";
    protected final String predictionResultTableBorder = "------------------";
    protected final String boundary = "---------------------------183079827324139952612037066";
    protected Algorithm algorithm = Algorithm.NetMHC40;

    public NetMHCQuery(String sequence, String allel, Integer length) {
        super(sequence, allel, length);
        netMhcAllel = processAllel(allel);
        
        if(sequenceFileValue.exists() == false) try {
            sequenceFileValue.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(NetMHCQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected String processAllel(String allel) {
        return allel.replace("*", "").replace(":", "");
    }

    @Override
    protected HttpEntity preparePayload() {

        String lengthValue = length == ALL_LENGTHS_CODE ? ALL_LENGTH_VALUE : length.toString();
         
        MultipartEntityBuilder builder = MultipartEntityBuilder
                .create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setBoundary(boundary)
                
                .addTextBody(configFileName, configFileValue)
                .addTextBody(inputTypeName, inputTypeValue)
                .addTextBody(sequenceName, sequence)
                .addBinaryBody(sequenceFileName, sequenceFileValue, ContentType.APPLICATION_OCTET_STREAM, "")
                 .addTextBody(lengthName, lengthValue)
                .addTextBody(peptideName, peptideValue)
                
                .addBinaryBody(peptideFileName, sequenceFileValue, ContentType.APPLICATION_OCTET_STREAM, "")
                
                //.addBinaryBody(sequenceFileName, sequenceFileValue, ContentType.APPLICATION_OCTET_STREAM, "") // file may not be null
               
              
                //.addBinaryBody(peptideFileName, sequenceFileValue, ContentType.APPLICATION_OCTET_STREAM, "") // file may not be null
                .addTextBody(masterName, masterValue)
                .addTextBody(slave0Name, netMhcAllel)
              
                .addTextBody(alleleName, netMhcAllel)
                  .addTextBody(thresholdStrongName, thresholdStrongValue)
                .addTextBody(thresholdWeakName, thresholdWeakValue);

        // specific entity building
        builder = preparePayload(builder);
        
        HttpEntity entity = builder.build();
        
        try {
        entity.writeTo(System.out);
        
        }catch(IOException e) {
            
        }
        return entity;
    }
    /*
    // this method executes specific entity building
    protected MultipartEntityBuilder preparePayload(MultipartEntityBuilder builder) {
        return builder.addTextBody(inputTypeName, inputTypeValue)
                
                .addTextBody(peptideName, peptideValue)
                .addBinaryBody(sequenceFileName, sequenceFileValue, ContentType.APPLICATION_OCTET_STREAM, "")
                .addBinaryBody(peptideFileName, sequenceFileValue, ContentType.APPLICATION_OCTET_STREAM, "")
        .addTextBody(thresholdStrongName, thresholdStrongValue)
                .addTextBody(thresholdWeakName, thresholdWeakValue)
                //.addBinaryBody(sequenceFileName, sequenceFileValue, ContentType.APPLICATION_OCTET_STREAM, "")
                ;
    }*/
    
    protected MultipartEntityBuilder preparePayload(MultipartEntityBuilder builder) {
        return builder;
    }

    protected void setConfigFile(String value) {
        this.configFileValue = value;
    } 

   
    
    protected String getConfigFile() {
        return configFileValue;
    }

    /*
	 * look for line containing html title -> if it states "prediction results", you have results
	 * else you have to repeat querying the URL
	 * 
	 * return true if results are NOT available, else false!
     */
    @Override
    protected boolean processResponse(CloseableHttpResponse response) throws IllegalStateException, IOException {

        logger.info("Processing response");

        boolean resultsAvailable = false;
        HttpEntity responseEntity = response.getEntity();
        BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));

        String line = null;
        do {
            line = reader.readLine();
            System.out.println(line);
            if (line == null) {
                break;
            }
            if (line.startsWith(htmlTitleTag)) {
                resultsAvailable = line.contains(predictionResultTitle);
                break;
            }
        } while (true);

        if (resultsAvailable) {
            boolean processLine = false;
            boolean lineIsPrediction = false;
            logger.info("Result available !");
            // find the beginning of the prediction!
            while (true) {
                line = reader.readLine();
                System.out.println(line);
                if (line == null) {
                    break; // End of Input
                }

                line = line.trim();
                if (line.toLowerCase().startsWith(predictionResultTableHeader)) {
                    // this is the header. Skip the header's bottom line and then start processing.
                    processLine = true;
                    break;
                }
            }
            logger.info("Processing line "+processLine);
            if (processLine) {
                // read out bottom line of header "----------------"
                line = reader.readLine();
                if (line == null) {
                    // oops - no output after header?!
                    return false; // do not recover and return to not repeat querying
                }
                lineIsPrediction = true;

                while (lineIsPrediction) {
                    line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    if (line.startsWith(predictionResultTableBorder)) {
                        lineIsPrediction = false;
                    } else {
                        processLine(line, this.algorithm);
                    }
                }
            }

            EntityUtils.consume(responseEntity);
        }

        return !resultsAvailable;
    }

    @Override
    protected void processLine(String line, Algorithm anAlgorithm) {
        // Line contains (space separated)
        // "  pos          HLA      peptide         Core  Offset  I_pos  I_len  D_pos  D_len        iCore        Identity 1-log50k(aff) Affinity(nM)    %Rank  BindLevel"
        //     0    HLA-A0101     SYFPEITH    -SYFPEITH      0      0      1      0      0     SYFPEITH        Sequence         0.031     35906.04    70.00

        String allel = null;
        String sequence = null;
        Integer position = null;
        Double score = null;

        String aLineToWork = line.trim().replaceAll("\\s+", " ");
        String[] aSplitLine = aLineToWork.split(" ");
        if (aSplitLine.length < 14) {
            logger.warning("not enough input! " + aLineToWork);
            return;
        } else if (aSplitLine.length > 16) {
            // Strong/Weak binders have the last column "<= WB", which results in two fields!
            logger.warning("too much input! " + aLineToWork);
            return;
        }

        // Allel of NetMHC is "HLA-A0101", but everyone else gives "HLA-A*01:01" -> return original allel
        allel = this.allel;

        // add offset to cope with NetMHC being 0-indexed
        position = Integer.parseInt(aSplitLine[0]) + 1;

        sequence = aSplitLine[2];
        score = Double.parseDouble(aSplitLine[12]); // take Affinity[nM] 
        TemporaryEntry temporaryEntry = new TemporaryEntry(allel, sequence, position, anAlgorithm, score);
        results.add(temporaryEntry);
    }

}
