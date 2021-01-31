/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import backend.serverqueries.AbstractQuery;
import backend.serverqueries.QueryInputType;
import backend.serverqueries.SyfpeithiQuery;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author cyrilmongis
 */
public class SyfpeithiFactory implements QueryFactoryInterface{

    @Override
    public List<AbstractQuery> createQueryForServer(String server, String sequence, String allel, Integer length, QueryInputType inputType) {
        return Arrays.asList(new SyfpeithiQuery(sequence, allel, length));
    }

    @Override
    public boolean support(String server) {
        return server.toUpperCase().contains("SYFPEITHI");
    }
    
}
