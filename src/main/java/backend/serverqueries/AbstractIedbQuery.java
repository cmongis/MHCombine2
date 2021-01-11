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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import backend.entries.Algorithm;
import backend.entries.TemporaryEntry;
import backend.serverqueries.exceptions.LineProcessingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public abstract class AbstractIedbQuery extends AbstractQuery {

    // for querying the server
    //protected static final String url = "http://tools-api.iedb.org/tools_api/mhci/";
    protected static final String url = "http://tools-cluster-interface.iedb.org/tools_api/mhci/";
    protected static final String methodName = "method";
    protected static final String sequenceName = "sequence_text";
    protected static final String alleleName = "allele";
    protected static final String lengthName = "length";

    protected final String sequence;
    protected final String allel;
    protected final Integer length;

    private final Algorithm algoritm;

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    private final String formDataField;

    private final int TRIAL_LIMIT = 5;
    private final long RETRY_INTERVAL = 300;

    private final IsEmptyStringPredicate isEmptyPredicate = new IsEmptyStringPredicate();

    private final Set<TemporaryEntry> results = new HashSet<>();

    private Set<Peptide> peptides;
    
    
    private final static int PEPTIDE_LENGTH_MIN = 8;
    private final static int PEPTIDE_LENGTH_MAX = 14;

    public AbstractIedbQuery(Algorithm algorithm, String formDataField, String sequence, String allel, Integer length) {
        this.sequence = sequence;
        this.allel = allel;
        this.length = length;
        this.algoritm = algorithm;
        this.formDataField = formDataField;

    }
    
    
    
    
    protected Set<Integer> findAllDifferentPeptideLengths(){
        return getPeptides()
                .stream()
                .mapToInt(Peptide::getLength)
                .distinct()
                .filter(i->i >=PEPTIDE_LENGTH_MIN && i <= PEPTIDE_LENGTH_MAX)
                .mapToObj(Integer::new)
                .collect(Collectors.toSet());
    }
    
    

    protected HttpEntity getFormData(String method) {

        List<NameValuePair> formdata = new ArrayList<NameValuePair>();
        formdata.add(new BasicNameValuePair(methodName, method));
        

        String lengthStr = length.toString();
        
        String allelStr;
        
        String sequence;
        

        // in a peptide query, only one allel at the time should be checked.
        if(isPeptideQuery()) {
            
            Set<Peptide> peptides = getPeptides();
            Set<Integer> peptideLengths = findAllDifferentPeptideLengths();
            
            sequence = peptides.stream().map(Peptide::getSequence).collect(Collectors.joining("\n"));
            allelStr = peptideLengths.stream().map(peptide->allel).collect(Collectors.joining(","));
            lengthStr = peptideLengths.stream().map(length->length.toString()).collect(Collectors.joining(","));
            
        }
        
        // in a sequence query, multiple alleles can be checked at the same time. it's okay
        else {
            
            sequence = this.sequence;
            allelStr = allel;
            lengthStr = Stream.of(allel.split(","))
                    .map(el->length.toString())
                    .collect(Collectors.joining(","));  
        }
        
        formdata.add(new BasicNameValuePair(sequenceName, sequence));
        formdata.add(new BasicNameValuePair(alleleName, allelStr));
        formdata.add(new BasicNameValuePair(lengthName, lengthStr));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formdata, Consts.UTF_8);
        return entity;
    }

    private boolean isEmpty(String str) {
        return str != null && "".equals(str.trim()) == false;
    }

    public Set<Peptide> getPeptides() {
        if (peptides == null) {
            peptides = new HashSet<>();

            Stream.of(sequence.split("\\n"))
                    .map(Peptide::new)
                    .forEach(peptides::add);
        }
        return peptides;
    }

    public Set<TemporaryEntry> queryServer() {

        CloseableHttpClient client = HttpClients.createSystem();

      

        HttpPost postRequest = new HttpPost(url);
        HttpEntity entity = getFormData(getFormDataField());
        postRequest.setEntity(entity);

        CloseableHttpResponse response = null;

        try {

            logger.info(String.format("POST: %s | %s", url, getFormDataField()));
            logger.info(entity.toString());
            response = client.execute(postRequest);

            for (int retry = 0; retry != TRIAL_LIMIT; retry++) {
                if (response.getStatusLine().getStatusCode() == 403) {
                    logger.warning("Retrying query for " + getAlgorithm().name());
                    response = client.execute(postRequest);
                    Thread.sleep(RETRY_INTERVAL);
                }
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = null;
            boolean processing = false;
            do {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                System.out.println(line);

                System.out.println(new StringBuilder()
                        .append("[")
                        .append(getAlgorithm().name())
                        .append("] ")
                        .append(line)
                        .toString()
                );

                if (processing) {
                    try {
                        for (TemporaryEntry entry : processLine(line)) {
                            
                            // if it's peptide query, we only wants the peptides previously entered
                            if(isPeptideQuery()) {
                                Peptide peptide = new Peptide((entry.getSequence()));
                                if (peptides.contains(peptide)) {
                                    results.add(entry);
                                }
                            }
                            // otherwise, we want everything
                            else {
                                results.add(entry);
                            }
                        }
                        //processLine(line, getAlgorithm());
                    } catch (Exception e) {
                        throw new LineProcessingException(getAlgorithm(), line, e);
                    }
                } else {
                    if (line.startsWith("allele")) {
                        processing = true;
                    }
                }
            } while (true);

            EntityUtils.consume(response.getEntity());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception happened while executing POST request. Abort.", e);

        } catch (InterruptedException ie) {
            logger.log(Level.SEVERE, "Exception happened while executing POST request. Abort.", ie);

        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Exception happened while closing response. Abort.", e);
                }
            }
        }

        return results;

    }

    public Algorithm getAlgorithm() {
        return algoritm;
    }

    protected Logger logger() {
        return logger;
    }

    protected String getFormDataField() {
        return formDataField;
    }

    protected List<TemporaryEntry> processLine(String line) {
        String[] entries = line.split("\t");
        if (entries.length < 7) {
            logger().severe("Output line " + line + " contains less than the mandatory 7 entries (allele, seq_num, start, end, length, peptide, ic50), did maybe something change??");
            return Arrays.asList();
        }
        // Line:
        // allele, seq_num, start, end, length, peptide, score, [other stuff]
        TemporaryEntry entry = new TemporaryEntry(allel, entries[5], Integer.parseInt(entries[2]), getAlgorithm().toColumn(), Double.parseDouble(entries[6]));

        return Arrays.asList(entry);

    }

    private class IsEmptyStringPredicate implements Predicate<String> {

        @Override
        public boolean test(String t) {
            return t != null && "".equals(t.trim()) == false;
        }
    }

}
