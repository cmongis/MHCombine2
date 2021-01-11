/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import backend.serverqueries.AbstractQuery;
import backend.serverqueries.QueryInputType;
import java.util.List;

/**
 *
 * @author cyrilmongis
 */
public interface QueryFactoryInterface {
       public List<AbstractQuery> createQueryForServer(String server, String sequence, String allel, Integer length,QueryInputType inputType);
       public boolean support(String server);
}
