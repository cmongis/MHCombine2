/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend.serverqueries;

import backend.entries.Algorithm;
import backend.entries.ResultColumnSuffix;
import backend.entries.TemporaryEntry;
import java.util.Arrays;
import java.util.List;
import org.apache.http.entity.mime.MultipartEntityBuilder;

public class NetMHCPan41Query extends AbstractNetMhcQuery {

    public NetMHCPan41Query(String sequence, String allel, Integer length) {
        super(Algorithm.NetMHCpan41, "/usr/opt/www/pub/CBS/services/NetMHCpan-4.1/NetMHCpan.cf", sequence, allel, length);

    }

    @Override
    public String processSingleAllel(String allel) {
        return allel.replace("*", "");
    }

    @Override
    protected List<TemporaryEntry> processLine(String line) {
        /*
        Pos         MHC        Peptide      Core Of Gp Gl Ip Il        Icore        Identity  Score_EL %Rank_EL BindLevel
        ---------------------------------------------------------------------------------------------------------------------------
           1 HLA-A*01:01       MHGDTPTL MHGD-TPTL  0  0  0  4  1     MHGDTPTL sp_P03129_VE7_H 0.0003470   35.400
         */
        String allel = null;
        String sequence = null;
        Integer position = null;
        Double score = null;
        Double rankBa = null;
        Double rankEl = null;

        String aLineToWork = line.trim().replaceAll("\\s+", " ");
        String[] aSplitLine = aLineToWork.split(" ");

        allel = findCorrespondingAllele(aSplitLine[1]);
        sequence = aSplitLine[2];
        position = Integer.parseInt(aSplitLine[0]);
        score = Double.parseDouble(aSplitLine[15]); // take Aff[nM] 
        rankBa = Double.parseDouble(aSplitLine[14]);
        rankEl = Double.parseDouble(aSplitLine[12]);

        TemporaryEntry scoreEntry = new TemporaryEntry(allel, sequence, position, getAlgorithm().toColumn(), score);
        TemporaryEntry rankElEntry = new TemporaryEntry(allel, sequence, position, getAlgorithm().toColumn(ResultColumnSuffix.RANK_EL), rankEl);
        TemporaryEntry rankBaEntry = new TemporaryEntry(allel, sequence, position, getAlgorithm().toColumn(ResultColumnSuffix.RANK_BA), rankBa);
        return Arrays.asList(scoreEntry, rankElEntry, rankBaEntry);
    }

    protected MultipartEntityBuilder preparePayload(MultipartEntityBuilder builder) {
        
        return super.preparePayload(builder).addTextBody("BApred", "on");
    }

}
