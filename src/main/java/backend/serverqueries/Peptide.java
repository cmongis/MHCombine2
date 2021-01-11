/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend.serverqueries;

/**
 *
 * @author cyrilmongis
 */
public class Peptide {
    
    
    private final String sequence;
    
    private final int length;

    public Peptide(String sequence) {
        this.sequence = sequence.trim();
        this.length = this.sequence.length();
    }

    public String getSequence() {
        return sequence;
    }

    public int getLength() {
        return length;
    }
    
    public boolean equals(Object o) {
        return o.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
       return  sequence.hashCode() + Integer.hashCode(length);
        
    }
    
    
}
