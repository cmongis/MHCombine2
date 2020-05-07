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

import utils.Constants.Algorithm;
import backend.entries.TemporaryEntry;
import backend.serverqueries.exceptions.LineProcessingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    protected final Set<TemporaryEntry> results;

    final Algorithm algoritm;

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    private final String formDataField;

    public AbstractIedbQuery(Algorithm algorithm, String formDataField, String sequence, String allel, Integer length) {
        this.sequence = sequence;
        this.allel = allel;
        this.length = length;
        this.results = new HashSet<TemporaryEntry>();
        this.algoritm = algorithm;
        this.formDataField = formDataField;
    }

    protected HttpEntity getFormData(String method) {
        List<NameValuePair> formdata = new ArrayList<NameValuePair>();
        formdata.add(new BasicNameValuePair(methodName, method));
        formdata.add(new BasicNameValuePair(sequenceName, sequence));
        formdata.add(new BasicNameValuePair(alleleName, allel));
        formdata.add(new BasicNameValuePair(lengthName, length.toString()));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formdata, Consts.UTF_8);
        return entity;
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
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = null;
            boolean processing = false;
            do {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (processing) {
                    try {
                        processLine(line, getAlgorithm());
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

    protected void processLine(String line, Algorithm algorithm) {
        String[] entries = line.split("\t");
        if (entries.length < 7) {
            logger().severe("Output line " + line + " contains less than the mandatory 7 entries (allele, seq_num, start, end, length, peptide, ic50), did maybe something change??");
            return;
        }
        // Line:
        // allele, seq_num, start, end, length, peptide, score, [other stuff]
        TemporaryEntry entry = new TemporaryEntry(allel, entries[5], Integer.parseInt(entries[2]), algorithm, Double.parseDouble(entries[6]));
        results.add(entry);
    }
}
