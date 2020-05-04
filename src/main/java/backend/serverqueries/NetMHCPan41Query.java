/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend.serverqueries;

import utils.Constants;

public class NetMHCPan41Query extends NetMHCPan40Query {
    
    public NetMHCPan41Query(String sequence, String allel, Integer length) {
        super(sequence, allel, length);
        setConfigFile("/usr/opt/www/pub/CBS/services/NetMHCpan-4.1/NetMHCpan.cf");
         this.algorithm = Constants.Algorithm.NetMHCpan41;
    }
    
}