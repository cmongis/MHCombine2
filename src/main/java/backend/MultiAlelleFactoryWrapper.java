/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import backend.serverqueries.AbstractQuery;
import backend.serverqueries.QueryInputType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author cyrilmongis
 */
public class MultiAlelleFactoryWrapper implements QueryFactoryInterface{
    
    
    final QueryFactoryInterface factory;

    public MultiAlelleFactoryWrapper(QueryFactoryInterface factory) {
        this.factory = factory;
    }

    @Override
    public List<AbstractQuery> createQueryForServer(String server, String sequence, String allelList, String length, QueryInputType inputType) {
        
        if(allelList.indexOf(",") > -1) {
            return Stream.of(allelList.split(","))
                    .map(allel-> {
                        return createQueryForServer(server,sequence,allel,length,inputType);
                    })
                    .flatMap(queryList->queryList.stream())
                    .collect(Collectors.toList());
        }
        else {
            return factory.createQueryForServer(server, sequence, allelList, length, inputType);
        }
    }

    @Override
    public boolean support(String server) {
        return factory.support(server);
    }    
}
