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

import utils.Constants.Algorithm;
import backend.entries.TemporaryEntry;

public class NetMHCPan30Query extends NetMHCQuery {

    public final Logger logger = LogManager.getLogger(NetMHCPan30Query.class);

   // private final String netMhcPanAllel;

    // form parameters names
    private final String configFileName = "configfile";
    private final String inputTypeName = "inp";
    private final String sequenceName = "SEQPASTE";
    // private final String sequenceFileName = "SEQSUB"; // not required
    private final String lengthName = "len";
    private final String peptideName = "PEPPASTE";
    // private final String peptideFileName = "PEPSUB"; // not required
    private final String masterName = "master";
    private final String slave0Name = "slave0";
    private final String alleleName = "allele";
    private final String mhcSequenceName = "MHCSEQPASTE";
//	private final String mhcSequenceFileName = "MHCSEQSUB"; // not required
    private final String thresholdStrongName = "thrs";
    private final String thresholdWeakName = "thrw";

    // form parameters default
    private final String configFileValue = "/usr/opt/www/pub/CBS/services/NetMHCpan-3.0/NetMHCpan.cf";
    private final String inputTypeValue = "0"; // 0 means FASTA, 1 means Peptides
    // private final File sequenceFileValue = null; // not required
    private final String peptideValue = "";
    // private final File peptideFileValue = null; // not required
    private final String masterValue = "1";
    private final String mhcSequenceValue = "";
//	private final File mhcSequenceFileValue = null; // not required
    private final String thresholdStrongValue = "0.5";
    private final String thresholdWeakValue = "2";

    // response specific structures
    private final String htmlTitleTag = "<title>";
    private final String predictionResultTitle = "prediction results";
    private final String predictionResultTableHeader = "Pos ";
    private final String predictionResultTableBorder = "------------------";

    public NetMHCPan30Query(String sequence, String allel, Integer length) {
        super(sequence, allel, length);
        setConfigFile(configFileValue);
       
       this.algorithm = Algorithm.NetMHCpan30;
    }
    @Override
    public String processAllel(String allel) {
        return allel.replace("*", "");
    }
    /*
    @Override
    protected HttpEntity preparePayload() {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody(configFileName, configFileValue);
        builder.addTextBody(inputTypeName, inputTypeValue);
        builder.addTextBody(sequenceName, sequence);
        // builder.addBinaryBody(sequenceFileName, sequenceFileValue, ContentType.APPLICATION_OCTET_STREAM, ""); // file may not be null
        builder.addTextBody(lengthName, length.toString());
        builder.addTextBody(peptideName, peptideValue);
        // builder.addBinaryBody(peptideFileName, peptideFileValue, ContentType.APPLICATION_OCTET_STREAM, ""); // file may not be null
        builder.addTextBody(masterName, masterValue);
        builder.addTextBody(slave0Name, netMhcPanAllel);
        builder.addTextBody(alleleName, netMhcPanAllel);
        builder.addTextBody(mhcSequenceName, mhcSequenceValue);
        // builder.addBinaryBody(mhcSequenceFileName, mhcSequenceFileValue, ContentType.APPLICATION_OCTET_STREAM, ""); // file may not be null
        builder.addTextBody(thresholdStrongName, thresholdStrongValue);
        builder.addTextBody(thresholdWeakName, thresholdWeakValue);

        HttpEntity entity = builder.build();
        return entity;
    }*/

    /*
	 * look for line containing html title -> if it states "prediction results", you have results
	 * else you have to repeat querying the URL
	 * 
	 * return true if results are NOT available, else false!
     */
    /*
    @Override
    protected boolean processResponse(CloseableHttpResponse response) throws IllegalStateException, IOException {
        
        boolean resultsAvailable = false;
        HttpEntity responseEntity = response.getEntity();
        BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
        String line = null;
        do {
            line = reader.readLine();
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

            // find the beginning of the prediction!
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    break; // End of Input
                }

                line = line.trim();
                if (line.startsWith(predictionResultTableHeader)) {
                    // this is the header. Skip the header's bottom line and then start processing.
                    processLine = true;
                    break;
                }
            }
            logger.log(Level.INFO, line);
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
                        processLine(line, Algorithm.NetMHCpan30);
                    }
                }
            }

            EntityUtils.consume(responseEntity);
        }

        return !resultsAvailable;
    }
    */
    @Override
    protected void processLine(String line, Algorithm anAlgorithm) {
        // Line contains (space separated)
        // "  Pos          HLA         Peptide       Core Of Gp Gl Ip Il        Icore        Identity   Score Aff(nM)   %Rank  BindLevel"
        //     1  HLA-A*01:01        SYFPEITH  -SYFPEITH  0  0  0  0  1     SYFPEITH        Sequence 0.02170 39535.2   60.00

        String allel = null;
        String sequence = null;
        Integer position = null;
        Double score = null;
        System.out.println(line);
        String aLineToWork = line.trim().replaceAll("\\s+", " ");
        String[] aSplitLine = aLineToWork.split(" ");
        if (aSplitLine.length < 14) {
            logger.warn("not enough input! " + aLineToWork);
            return;
        } else if (aSplitLine.length > 16) {
            // Strong and weak binders have last entry "<= WB", which gives two fields in the array!
            logger.warn("too much input! " + aLineToWork);
            return;
        }

        // allel is returned "HLA-A*01:01" here, even though as input one had to provide "HLA-A01:01".
        allel = this.allel; //aSplitLine[1];
        sequence = aSplitLine[2];
        position = Integer.parseInt(aSplitLine[0]);
        score = Double.parseDouble(aSplitLine[12]); // take Aff[nM] 
        TemporaryEntry temporaryEntry = new TemporaryEntry(allel, sequence, position, anAlgorithm, score);
        results.add(temporaryEntry);
    }

}
