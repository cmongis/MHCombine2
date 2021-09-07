/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import backend.serverqueries.AbstractQuery;
import backend.serverqueries.IedbConsensusQuery;
import backend.serverqueries.IedbNetMHCconsQuery;
import backend.serverqueries.IedbPickpocketQuery;
import backend.serverqueries.IedbRecommendedQuery;
import backend.serverqueries.IedbSmmQuery;
import backend.serverqueries.IedbSmmpmbecQuery;
import backend.serverqueries.QueryInputType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author cyrilmongis
 */
public class IedbQueryFactory implements QueryFactoryInterface {

    @Override
    public List<AbstractQuery> createQueryForServer(String server, String sequence, String allelList, String length, QueryInputType inputType) {
        // if it's peptide search and there are multiple alleles
        //if(allelList.indexOf(",") > -1) {
            
            String[] lengthArray = length.split(","); // 4,5 -> 4,5 x allele number
            String[] alleleArray =  allelList.split(","); // HLA1,HLA,HLA, -> each allele repeated the number of lengths
            
            
            String finalLength = IntStream.range(0,alleleArray.length)
                    .mapToObj(i->length)
                    .collect(Collectors.joining(","));
            
            
            String finalAlleleList = Stream.of(alleleArray)
                    .flatMap(allele->IntStream.range(0,lengthArray.length).mapToObj(i->allele))
                    .collect(Collectors.joining(","));
            
            
            return Arrays.asList(createQuery(server, sequence, finalAlleleList, finalLength, inputType));
                    
            
            /*
            // return one query per allel
            return Stream.of(allelList.split(","))
                    .map(allel-> {
                        return createQuery(server,sequence,allel,length,inputType);
                    })
                    .collect(Collectors.toList());*
                    */
        //}
        //else {
        //    return Arrays.asList(createQuery(server,sequence,allelList,length,inputType));
        //}
    }

    public AbstractQuery createQuery(String server, String sequence, String allel, String length, QueryInputType inputType) {

        AbstractQuery query = null;
        switch (server) {
            case "IedbNetMHCcons":
                query = new IedbNetMHCconsQuery(sequence, allel, length);
                break;
            case "IedbPickpocket":
                query = new IedbPickpocketQuery(sequence, allel, length);
                break;
            case "IedbRecommended":
                query = new IedbRecommendedQuery(sequence, allel, length);
                break;
            case "IedbConsensus":
                query = new IedbConsensusQuery(sequence, allel, length);
                break;
            case "IedbSmmpmbec":
                query = new IedbSmmpmbecQuery(sequence, allel, length);
                break;
            case "IedbSmm":
                query = new IedbSmmQuery(sequence, allel, length);
                break;

        }
        return query;
    }

    @Override
    public boolean support(String server) {
        return server.contains("Iedb");
    }

}
