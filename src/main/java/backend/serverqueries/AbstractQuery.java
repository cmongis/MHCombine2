/*
 * This file is part of MHCcombine.
 *
 * MHCcombine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MHCcombine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MHCcombine.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright 2018 Angelika Riemer, Maria Bonsack
 */
package backend.serverqueries;

import backend.QueryObserver;
import backend.entries.Algorithm;
import java.util.Set;
import java.util.concurrent.Callable;

import backend.entries.TemporaryEntry;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractQuery implements Callable<Set<TemporaryEntry>> {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    private QueryInputType queryInputType = QueryInputType.SEQUENCE;
    
    public static final List<TemporaryEntry> NO_ENTRY = Arrays.asList();

    private Algorithm algorithm;

    
    private boolean logging = true;
    
    private Set<Peptide> peptides;
    
    
    private List<Allele> alleleList;
    
    private boolean canceled = false;
    
    private QueryObserver observer;

    
    @Override
    public Set<TemporaryEntry> call() throws Exception {
        
        if(canceled) {
            return new HashSet<>();
        }
        
        notifyStart();
        
        logger.info("Starting query for " + getClass().getSimpleName());
        Set<TemporaryEntry> queryServer = queryServer();
        int count = queryServer != null ? queryServer.size() : -1;
        logger.info(String.format("End query for %s : %d returned", getClass().getSimpleName(), count));
        
        notifyEnd();
        return queryServer;
    }

    public void setQueryInputType(QueryInputType queryInputType) {
        this.queryInputType = queryInputType;
    }

    public QueryInputType getQueryInputType() {
        return queryInputType;
    }
    
    public void setQueryInputType(String queryInputType) {
        if(queryInputType == null) {
            throw new IllegalArgumentException("Null query input type");
        }
        setQueryInputType(QueryInputType.valueOf(queryInputType.toUpperCase()));
    }

    protected abstract Set<TemporaryEntry> queryServer();

    protected abstract List<TemporaryEntry> processLine(String line);

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public boolean isCanceled() {
        return canceled;
    }
    
    
    
    public void cancel() {
        canceled = true;
    }
    
    protected boolean isPeptideQuery() {
        return queryInputType == QueryInputType.PEPTIDE;
    }
    
    
    protected Set<Peptide> getPeptides(String sequence)  {
        
       if (peptides == null) {
            peptides = new HashSet<>();

            Stream.of(sequence.split("\\n"))
                    .map(Peptide::new)
                    .forEach(peptides::add);
        }
        return peptides;
    }
    
    protected List<Allele> getAlleleList(String alleleInput,Algorithm algorithm) {
        if(alleleList == null) {
            alleleList = processAllel(alleleInput, algorithm);
        }
        return alleleList;
    }
    
     protected List<Allele> processAllel(String alleleInput, Algorithm algorithm) {
        return Stream.of(alleleInput.split(","))
                .map(allele -> Allele.create(allele, algorithm))
                .collect(Collectors.toList());
    }
     
     protected void log(String msg, Object... args) {
         
         if(logging) {
             System.out.println(String.format("[%s]",getAlgorithm())+String.format(msg,args));
         }
         
     }
     
     protected void forceLog(String msg, Object... args) {
         System.out.println(String.format("[%s]",getAlgorithm())+String.format(msg,args));
     }
     
     
    public boolean isSingleThread() {
        return false;
    }
    
       public void setObserver(QueryObserver observer) {
        this.observer = observer;
    }
    
    public void notifyStart() {
        if(this.observer != null) {
            observer.notifyStart(this);
        }
        
    }
    
    public void notifyEnd() {
        if(this.observer != null) {
            observer.notifyEnd(this);
        }
    }
    
    


}
