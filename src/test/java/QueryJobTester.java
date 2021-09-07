
import backend.ConsoleQueryObserver;
import backend.QueryJob;
import backend.serverqueries.QueryInputType;
import org.junit.Test;
import utils.Constants;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cyrilmongis
 */

public class QueryJobTester {
    
    @Test
    public void testQueryJob() {
        QueryJob job = new QueryJob();
        job.configure(QueryInputType.SEQUENCE,"MHQKRTAMFQDPQERPRKLPQLCTELQTTIHDIILECVYCKQQLLRREVYDFAFRDLCIV"
                , "HLA-A*02:01"
                , "8"
                ,"NetMHC40","NetMHCpan41"
        ,"IedbNetMHCcons");
        
        ConsoleQueryObserver observer = new ConsoleQueryObserver(job);
        job.setObserver(observer);
        job.setOutputStream(System.out);
        job.run();
    }
        
    
    
    
}
