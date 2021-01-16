/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;


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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utils.Util;
import backend.entries.EntryKey;
import backend.entries.ResultEntry;
import backend.entries.TemporaryEntry;
import backend.serverqueries.AbstractQuery;
import java.util.concurrent.TimeUnit;
import backend.entries.ResultColumn;
import backend.entries.ResultColumns;
import backend.serverqueries.QueryInputType;
import java.util.Arrays;

/**
 * Servlet implementation class ServerQuerier
 */
public class QueryJob implements Job {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(QueryJob.class);

    private static final int THREAD_POOL_SIZE = 15;

    private OutputStream outputStream;

    private String[] servers;

    private String allel;

    private String sequence;

    private String len;

    private boolean succeeded = false;

    private boolean finished = false;

    private QueryObserver observer;

    private String id;

    private int totalQueries;

    private int progress = 0;

    private Throwable error;

    private String fileName;

    private QueryInputType queryInputType = QueryInputType.SEQUENCE;
    
    private final long creation = System.currentTimeMillis();
    private final long START_QUERY_INTERVAL = 250; //milliseconds
    private boolean adaptiveColumn = false;
    private final String ADAPTIVE_COLUMNS = "adaptiveColumns";

    public void configure(QueryInputType queryInputType, String sequence, String allel, String len, String... servers) {
        this.queryInputType = queryInputType;
        this.sequence = sequence;
        this.allel = allel;
        this.len = len;
        this.servers = servers;
    }

    @Override
    public void setConfig(String key, Object value) {
        if(ADAPTIVE_COLUMNS.equals(key) && value != null) {
            setAdaptiveColumn("true".equals(value));
        } 
    }

    @Override
    public void run() {
        observer.setStarted();

        Map<EntryKey, ResultEntry> results = createAndExecuteQueries(servers, sequence, allel, len);
        try {
            createCsvFile(results, ";", outputStream);
            outputStream.close();
            
            
            succeeded = true;
        } catch (Exception e) {
            logger.error(e);

            error = e;

        } finally {
            if (observer != null) {
                observer.setFinished();
            }
            finished = true;
        }

    }
    
    

    public void setAdaptiveColumn(boolean adaptiveColumn) {
        this.adaptiveColumn = adaptiveColumn;
    }

    public boolean isAdaptiveColumn() {
        return adaptiveColumn;
    }
    
    public boolean isMultiAllels() {
        return allel.contains(",");
    }
    
    public boolean isPeptideQuery() {
        return queryInputType == QueryInputType.PEPTIDE;
    }

    public List<ResultColumn> getColumnList() {

        if (!isAdaptiveColumn()) {

            return Arrays.asList(ResultColumns.ALL);

        } else {
             List<ResultColumn> columnList = new ArrayList();
             for(ResultColumn column : ResultColumns.ALL) {
                 
                 if(column.isAlgorithmInList(servers)) {
                     columnList.add(column);
                 }
             }
             return columnList;
        }
    }

    private Map<EntryKey, ResultEntry> createAndExecuteQueries(String[] servers, String sequence, String allel, String length) {
        if (servers.length == 0 || StringUtils.isEmpty(sequence) || StringUtils.isEmpty(allel) || StringUtils.isEmpty(length)) {
            return null;
        }

        List<AbstractQuery> queries = getQueriesforServers(servers, sequence, allel, length);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        CompletionService<Set<TemporaryEntry>> completionService = new ExecutorCompletionService<Set<TemporaryEntry>>(
                executor);

        Map<EntryKey, ResultEntry> results = new HashMap<EntryKey, ResultEntry>();
        totalQueries = queries.size();

        try {

            for (AbstractQuery query : queries) {
                Thread.sleep(START_QUERY_INTERVAL);
                completionService.submit(query);

            }

            for (int i = 0; i < queries.size(); i++) {
                Future<Set<TemporaryEntry>> future = completionService.take();
                Set<TemporaryEntry> res = future.get();
                
                if(res == null) {
                    throw new RuntimeException("Error when querying server");
                }

                incrementProgress();
                for (TemporaryEntry temp : res) {
                    ResultEntry value = results.get(temp.getKey());
                    if (value == null) {
                        // entry not yet in results map, let us create a new one.
                        value = new ResultEntry(temp.getAllel(), temp.getSequence(), temp.getPosition());
                        results.put(temp.getKey(), value);
                    }
                    value.setScore(temp.getColumn(), temp.getScore());
                }
            }
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.MINUTES);

        } catch (InterruptedException e) {
            ///e.printStackTrace();
            error = e;
            logger.error("Exception happened: ", e);
        } catch (ExecutionException e) {
            //e.printStackTrace();
            error = e;
            logger.error("Exception happened: ", e);
        }

        return results;

    }

    private List<AbstractQuery> getQueriesforServers(String[] servers, String sequence, String allel, String length) {
        List<AbstractQuery> queries = new ArrayList<AbstractQuery>();

        String[] lengths = length.split(",");

        QueryFactory factory = new QueryFactory();
        for (String server : servers) {
            for (String aLength : lengths) {
                List<AbstractQuery> newQueries = factory.createQueryForServer(server, sequence, allel, Integer.parseInt(aLength),queryInputType);
                if (newQueries.size() > 0) {
                    queries.addAll(newQueries);
                } else {
                    logger.error("Query was NULL for server " + server + " and length " + length);
                }
            }
        }
        return queries;
    }

    private void createCsvFile(Map<EntryKey, ResultEntry> results, String delimiter, OutputStream out) throws IOException {
        List<EntryKey> aSortedEntryList = Util.asSortedList(results.keySet());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
        // first headers
        
        
        if(!isPeptideQuery()) {
            writer.append("Position Start");
            writer.append(delimiter);
            writer.append("Region");
            writer.append(delimiter);
            writer.append("Length");
            writer.append(delimiter);
        }
        if(isMultiAllels()) {
            writer.append("Allel");
            writer.append(delimiter);
                    
        }
        writer.append("Sequence");
        writer.append(delimiter);
        
        
        
        
        for (ResultColumn column : getColumnList()) {
            writer.append(column.toString());
            writer.append(delimiter);
        }
        writer.newLine();

        for (EntryKey anEntry : aSortedEntryList) {
            if(!isPeptideQuery()) {
                writer.append(String.valueOf(anEntry.getPosition())); // Position Start
                writer.append(delimiter);
                writer.append("\"" + String.valueOf(anEntry.getPosition()) + " - " + String.valueOf(anEntry.getPosition() + anEntry.getLength() - 1) + "\""); // Region
                writer.append(delimiter);
                writer.append(String.valueOf(anEntry.getLength())); // Length
                writer.append(delimiter);
            }
            if(isMultiAllels()) {
                writer.append(anEntry.getAllel());
                writer.append(delimiter);
            }
            writer.append(anEntry.getSequence()); // Sequenz
            writer.append(delimiter);

            ResultEntry aResult = results.get(anEntry);
            for (ResultColumn column : getColumnList()) {
                Double score = aResult.getScore(column);
                String field = "N/A";
                if (score != null) {
                    field = escapeForCSV(aResult.getScore(column).toString(), delimiter);
                }
                writer.append(field);
                writer.append(delimiter);
            }
            writer.newLine();
        }

        writer.flush();
        writer.close();
    }

    private String escapeForCSV(String theString, String delimiter) {
        String field = theString.replace("\"", "\"\"");
        if (field.indexOf(delimiter) > -1 || field.indexOf('"') > -1) {
            field = '"' + field + '"';
        }
        return field;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setObserver(QueryObserver observer) {
        this.observer = observer;
    }

    @Override
    public void setOutputStream(OutputStream stream) {
        this.outputStream = stream;
    }

    @Override
    public long getTimeCreation() {
        return creation;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public boolean hasSucceeded() {
        return succeeded;
    }

    private void incrementProgress() {
        progress++;
        if (observer != null) {
            observer.notifyProgress();
        }
    }

    @Override
    public int getProgress() {
        return progress;
    }

    public int getTotal() {
        return totalQueries;
    }

    public void clean() {

    }

    public Throwable getError() {
        return error;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

}
