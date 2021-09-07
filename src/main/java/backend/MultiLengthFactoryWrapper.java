/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import backend.serverqueries.AbstractQuery;
import backend.serverqueries.QueryInputType;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Parses each length and create a query out of it
 * @author cyrilmongis
 */
public class MultiLengthFactoryWrapper implements QueryFactoryInterface{

    
    private final QueryFactoryInterface factory;

    public MultiLengthFactoryWrapper(QueryFactoryInterface factory) {
        this.factory = factory;
    }
    @Override
    public List<AbstractQuery> createQueryForServer(String server, String sequence, String allel, String length, QueryInputType inputType) {
        String[] lengthArray = length.split(",");
        return Stream.of(lengthArray)
                .map(individualLength->factory.createQueryForServer(server, sequence, allel, individualLength, inputType))
                .flatMap(queryList->queryList.stream())
                .collect(Collectors.toList());
        
    }

    @Override
    public boolean support(String server) {
        return factory.support(server);
    }
    
    
    
}
