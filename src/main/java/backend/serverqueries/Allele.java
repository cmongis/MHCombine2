/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend.serverqueries;

import backend.entries.Algorithm;

/**
 *
 * @author cyrilmongis
 */
public class Allele {
    private final String name;
    
    private final String inputName;
    
    private final String outputName;

    public Allele(String name, String inputName, String outputName) {
        this.name = name;
        this.inputName = inputName;
        this.outputName = outputName;
    }

    
    
    public static Allele create(String name, Algorithm algorithm) {

        return new Allele(name,getInputName(name, algorithm),getOutputName(name, algorithm));
    }

    public String getInputName() {
        return inputName;
    }

    public String getOutputName() {
        return outputName;
    }
    
    
    
    public String getInputName(Algorithm algorithm) {
        return getInputName(name, algorithm);
    }
    
    public String getOutputName(Algorithm algorithm) {
        return getOutputName(name, algorithm);
    }

    public String getName() {
        return name;
    }

    public final static Allele INVALID_ALLELE = new Allele("INVALID","invalid","invalid");

    
    
     public static String getInputName(String name, Algorithm method) {
        switch(method) {
            
            case NetMHC34:
                return name.replace("*", "");
            case NetMHC40:
                return name.replace("*", "").replace(":","");
           
            case NetMHCpan30:
                return name.replace("*","");
            
            case NetMHCpan40:
                return name.replace("*","");
            
            case NetMHCpan41:
                return name.replace("*","");
            
            case NetMHCpan28:
                return name.replace("*","");

                
            default:
                return name;
        }
    }
    
    public static String getOutputName(String name, Algorithm method) {
        
        switch(method) {
            case NetMHC40:
                return name.replace("*","").replace(":", ""); 
            
            
            default:
                return name;
        }
        
    }
    
    

    
}
