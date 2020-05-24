/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend.entries;

/**
 *
 * @author cyrilmongis
 */
public class ResultColumns {
    
    
    /* Requirements:
    From left to right it should be:

        NetMHCpan 4.1 (%Rank EL)
        NetMHCpan 4.1 (%Rank BA)
        NetMHCpan 4.1 (nM)

        NetMHC 4.0 (%Rank)
        NetMHC 4.0 (nM)

        NetMHCpan 4.0 (%Rank)
        NetMHCpan 4.0 (nM)

        NetMHCpan 3.0 (%Rank)
        NetMHCpan 3.0 (nM)

        NetMHC 3.4 (%Rank)
        NetMHC 3.4 (nM)

        NetMHCcons 1.1  (nM)
        Pickpocket 1.1  (nM)

        IEDB Recommended (EL-Score) – better (%Rank)
        IEDB Consensus (%Rank)
        IEDB SMMPMBEC (nM)
        IEDB SMM (nM)
        SYFPEITHI (score)
    */
    
    
    public static final ResultColumn[] ALL = {
        Algorithm.NetMHCpan41.toColumn(ResultColumn.RANK_EL),
        //Algorithm.NetMHCpan41.toColumn(ResultColumn.RANK_BA),
        Algorithm.NetMHCpan41.toColumn(),
        
        Algorithm.NetMHC40.toColumn(ResultColumn.RANK),
        Algorithm.NetMHC40.toColumn(),
        
        Algorithm.NetMHCpan40.toColumn(ResultColumn.RANK),
        Algorithm.NetMHCpan40.toColumn(),
        
        Algorithm.NetMHCpan30.toColumn(ResultColumn.RANK),
        Algorithm.NetMHCpan30.toColumn(),
        
        //Algorithm.NetMHC34.toColumn(ResultColumn.RANK), // --> No Rank in this one
        Algorithm.NetMHC34.toColumn(),
        
        Algorithm.NetMHCpan28.toColumn(),
        
        Algorithm.IedbNetMHCcons.toColumn(),
        Algorithm.IedbPickpocket.toColumn(),
        Algorithm.IedbRecommended.toColumn(),
        Algorithm.IedbConsensus.toColumn(),
        Algorithm.IedbSmmpmbec.toColumn(),
        Algorithm.SYFPEITHI.toColumn()};

}
