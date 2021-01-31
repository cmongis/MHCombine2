/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import backend.entries.Algorithm;
import backend.serverqueries.AbstractQuery;
import backend.serverqueries.NetMHC34Query;
import backend.serverqueries.NetMHCPan28Query;
import backend.serverqueries.Peptide;
import backend.serverqueries.QueryInputType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author cyrilmongis
 */
public class SizeDependantNetMhcFactory implements QueryFactoryInterface {

    @Override
    public List<AbstractQuery> createQueryForServer(String server, String sequence, String allel, Integer length, QueryInputType inputType) {
        
        if(inputType == QueryInputType.SEQUENCE) {
            return Arrays.asList(createQuery(server,sequence, allel, length));
        }
        else {
          return Stream
                    .of(sequence.split("[\\s\\n]")) // split the sequence
                    .map(Peptide::new) // create a peptide
                    .collect(Collectors.groupingBy(peptide->new Integer(peptide.getLength()))) // group the peptides by lenght
                    .entrySet() // map to entry set
                   .stream() // then to stream
                   .map(set-> createQuery(server,peptideListToInput(set.getValue()), allel, set.getKey())) // key is the length, value is the peptide list
                   .collect(Collectors.toList());
        }
    }
    
    
    AbstractQuery createQuery(String server, String sequence, String allel, Integer length) {
        switch(server) {
            case "NetMHCpan28":
                return new NetMHCPan28Query(sequence, allel, length);
            case "NetMHC34":
                return new NetMHC34Query(sequence, allel, length);
        }
        return null;
    } 

    @Override
    public boolean support(String server) {
        return server.equals(Algorithm.NetMHCpan28.toString());
    }
    
    private String peptideListToInput(List<Peptide> peptideList) {
        return peptideList
                .stream()
                .map(Peptide::getSequence)
                .collect(Collectors.joining("\n"));
    }
    
}
