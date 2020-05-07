/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend.serverqueries.exceptions;

import utils.Constants;

/**
 *
 * @author cyrilmongis
 */
public class LineProcessingException extends RuntimeException{
    public LineProcessingException(Constants.Algorithm algorithm, String line, Throwable e) {
        super(String.format("Error when processing line ( %s )\n%s\n",algorithm.toString(),line), e);
    }
}
