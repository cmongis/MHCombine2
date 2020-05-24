/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend.entries;

import backend.serverqueries.NetMHCPan41Query;
import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 * @author cyrilmongis
 */
public class ResultColumn {
    
    
    
    
    
    
    final Algorithm algorithm;
    
    final String suffix;
    
    public ResultColumn(Algorithm algorithm) {
        this(algorithm,"");
    }
    
    public ResultColumn(Algorithm algorithm, String suffix) {
        this.algorithm = algorithm;
        this.suffix = suffix;
    }
    
    public int orderScore() {
        
        int i = 0;
        for (Algorithm algo :  Algorithm.values()) {
            if(algorithm == algo) {
                return i;
            }
            
            i++;
        }
        return i;
    }
    
    @Override
    public boolean equals(Object object) {
        return hashCode() == object.hashCode();
    }
    
    
    public boolean isAlgorithmInList(String... servers) {
       for(String server : servers) {

           Algorithm algo = Algorithm.valueOf(server);  
           if(algo == null) {
               continue;
           }
           if(algo == algorithm) {
               return true;
           }
       }
       
       return false;
                
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(algorithm.hashCode())
                .append(suffix).toHashCode();
    }
    
    @Override
    public String toString() {
        return new StringBuilder(20).append(algorithm.toString())
                .append(" ")
                .append(suffix)
                .toString();
    }
   
    
}
