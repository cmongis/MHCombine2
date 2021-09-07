/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import backend.serverqueries.AbstractQuery;

/**
 *
 * @author cyrilmongis
 */
public interface QueryObserver {
    
    
    void notifyStart(AbstractQuery query);
    
    void notifyEnd(AbstractQuery query);
    
    
}
