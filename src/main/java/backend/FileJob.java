/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.io.File;

/**
 *
 * @author cyrilmongis
 */
public interface FileJob extends Job {
   
    
    File getFile();
    
}
