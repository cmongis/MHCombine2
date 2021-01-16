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
public enum Algorithm {
    NetMHC40("NetMHC 4.0"),
    NetMHCpan30("NetMHCpan 3.0"),
    NetMHCpan28("NetMHCpan 2.8"),
    NetMHCpan40("NetMHCpan 4.0"),
    NetMHCpan41("NetMHCpan 4.1"),
    NetMHC34("NetMHC 3.4"),
    IedbNetMHCcons("NetMHCcons 1.1"), //IEDB_ann("NetMHC 3.4 = IEDB ann"),
    IedbPickpocket("Pickpocket 1.1"),
    IedbRecommended("IEDB Recommended",ResultColumnSuffix.RANK),
    IedbConsensus("IEDB Consensus", ResultColumnSuffix.RANK),
    IedbSmmpmbec(" IEDB SMMPMBEC"),
    IedbSmm("IEDB SMM"),
    SYFPEITHI("SYFPEITHI",ResultColumnSuffix.SCORE);

    //BIMAS("BIMAS");
    //@Deprecated
    //IEDB_ann("IEDB Annotated"),
    //@Deprecated
    //IEDB_netMHCpan("NetMHCpan 2.8");
    private final String printName;

    private String defaultSuffix = ResultColumnSuffix.NM;

    private Algorithm(String printName) {
        this.printName = printName;
    }

    private Algorithm(String printName, String suffix) {
        this.printName = printName;
        this.defaultSuffix = suffix;
    }

    @Override
    public String toString() {
        return this.printName;
    }
    
    public ResultColumn toColumn() {
        return new ResultColumn(this, defaultSuffix);
    }

    public ResultColumn toColumn(String suffix) {
        return new ResultColumn(this, suffix);
    }

}
