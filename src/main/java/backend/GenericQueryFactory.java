/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import backend.serverqueries.AbstractQuery;
import backend.serverqueries.NetMHC40Query;
import backend.serverqueries.NetMHCPan30Query;
import backend.serverqueries.NetMHCPan40Query;
import backend.serverqueries.NetMHCPan41Query;
import backend.serverqueries.QueryInputType;
import backend.serverqueries.SyfpeithiQuery;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cyrilmongis
 */
public class GenericQueryFactory implements QueryFactoryInterface{

    @Override
    public List<AbstractQuery> createQueryForServer(String server, String sequence, String allel, String lengthStr, QueryInputType inputType) {
         List<AbstractQuery> queries = new ArrayList<>();
        
         Integer length = Integer.parseInt(lengthStr);
         
        AbstractQuery query = null;
        switch (server) {
            case "NetMHC40":
                query =  new NetMHC40Query(sequence, allel, length);
                break;
            case "NetMHC34":
                queries.addAll(new SizeDependantNetMhcFactory().createQueryForServer(server, sequence, allel, lengthStr, inputType));
                break;
            case "NetMHCpan40":
                query =  new NetMHCPan40Query(sequence, allel, length);
                break;
            case "NetMHCpan41":
                query = new NetMHCPan41Query(sequence, allel, length);
                break;
            case "NetMHCpan30":
                query = new NetMHCPan30Query(sequence, allel, length);
                break;
            case "NetMHCpan28":
                queries.addAll(new SizeDependantNetMhcFactory().createQueryForServer(server, sequence, allel, lengthStr, inputType));
                break;
          
            //case "SYFPEITHI":
            //    query = new SyfpeithiQuery(sequence, allel, lengthStr);
            //    break;
            // Deprecate BIMAS
//		case "BIMAS":
//			return new BimasQuery(sequence, allel, length);      
            default:
                QueryFactory.LOGGER.warn("Attempted to create query for server " + server + ", no query type found! Returning null.");
                query = null;
                break;
               
        }
        if(query != null) {
            queries.add(query);
        }
        queries.forEach(q->q.setQueryInputType(inputType));
        return queries;
    }

    @Override
    public boolean support(String server) { 
        return server.startsWith("NetMHC");
    }
    
}
