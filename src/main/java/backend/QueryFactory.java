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
package backend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.serverqueries.AbstractQuery;
import backend.serverqueries.QueryInputType;
import java.util.ArrayList;
import java.util.List;

public class QueryFactory {
 
    public static final Logger LOGGER = LogManager.getLogger(QueryFactory.class);

    List<QueryFactoryInterface> factories = new ArrayList<>();
    
    public QueryFactory() {
        factories.add(new MultiLengthFactoryWrapper(new GenericQueryFactory()));
        factories.add(new IedbQueryFactory());
        factories.add(new MultiLengthFactoryWrapper(new MultiAlelleFactoryWrapper(new SyfpeithiFactory())));
    }
    
    //TODO: Transform this factory to return a list of queries
   // public List<AbstractQuery> createQueryForServer(String server, String input, String allel, Integer length) {
   //     return createQueryForServer(server,input,allel,length,QueryInputType.SEQUENCE);
   // }
    public List<AbstractQuery> createQueryForServer(String server, String sequence, String allel, String length,QueryInputType inputType) {
        
        List<AbstractQuery> queries = new ArrayList<>(10);
        
        for(QueryFactoryInterface factory : factories) {
            if(factory.support(server)) {
                List<AbstractQuery> createdQueries = factory.createQueryForServer(server, sequence, allel, length, inputType);
                if(createdQueries.size() > 0) {
       
                    queries.addAll(createdQueries);
                    break;
                }
                else {
                    LOGGER.warn(String.format("Factory %s proposed support for %s but didn't return any query.",factory.getClass().getSimpleName(),server));
                    continue;
                }
            }
        }
        queries.forEach(query->query.setQueryInputType(inputType));
        return queries;
       
    }

}
