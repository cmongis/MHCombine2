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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utils.AlleleHelper;
import utils.Constants.Algorithm;
import backend.entries.TemporaryEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyfpeithiQuery extends AbstractQuery {

    private final Logger logger = LogManager.getLogger(SyfpeithiQuery.class);

    private final String splitRegex = "[" + StringUtils.CR + StringUtils.LF + "]+";
    private final String fastaHeaderStartChar = ">";
    private final String fastaCommentStartChar = ";";

    private String baseURL = "http://www.syfpeithi.de";
    private String queryForm = "/bin/MHCServer.dll/EpitopePrediction?";

    private final String sequence;
    private final String originalAllel;
    private final String allel;
    private final Integer length;

    // form parameter names & some default values
    private final String sequenceText = "SEQU";
    private final String allelText = "Motif";
    private final String lengthText = "amers";
    private final String doItText = "DoIT";
    private final String doItValue = "++Run++";

    private final String refererText = "Referer";
    private final String refererValue = "http://www.syfpeithi.de/bin/MHCServer.dll/EpitopePrediction.htm";

    private final Set<TemporaryEntry> results;

    private final LineProcessor lineProcessor = new LineProcessor();

    public SyfpeithiQuery(String sequence, String allel, int length) {
        this.sequence = preprocessFastaSequence(sequence);
        this.originalAllel = allel;
        this.allel = AlleleHelper
                .getSyfpeithiAllelForGeneralAllel(originalAllel);
        this.length = length;
        this.results = new HashSet<TemporaryEntry>();
    }

    @Override
    protected Set<TemporaryEntry> queryServer() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(allelText, allel));
        params.add(new BasicNameValuePair(lengthText, length + ""));
        params.add(new BasicNameValuePair(sequenceText, sequence));
        params.add(new BasicNameValuePair(doItText, doItValue));

        String queryString = URLEncodedUtils.format(params, "utf-8");
        HttpGet getRequest = new HttpGet(baseURL + queryForm + queryString);
        getRequest.setHeader(refererText, refererValue);

        CloseableHttpResponse response = null;
        try {
            CloseableHttpClient client = HttpClients.createSystem();
            response = client.execute(getRequest);
            HttpEntity responseEntity = response.getEntity();

            int statuscode = response.getStatusLine().getStatusCode();
            if (statuscode != 200) {
                logger.error("Error while executing. StatusLine was " + response.getStatusLine());
            }

            // consume input and create result.
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    responseEntity.getContent()));
            String line = null;
            boolean processing = false;
            boolean oneLineBeforeProcessing = false;
            do {
                System.out.println(line);
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                line = line.toLowerCase();
                if (processing) {
                    //String[] lines = line.split("</tr>");
                    //for (String entryLine : lines) {
                    //	processLine(entryLine, Algorithm.SYFPEITHI);

                    //}
                    processLine(line, Algorithm.SYFPEITHI);
                } else if (oneLineBeforeProcessing) {
                    // next line will be dataset
                    processing = true;
                } else {
                    if (line.contains("score</th>")) {
                        // line after next line will be first dataset.
                        oneLineBeforeProcessing = true;
                    }
                }
            } while (true);

            EntityUtils.consume(responseEntity);
        } catch (IOException e) {
            logger.error("Exception happened while executing GET request. Abort.", e);
            return results;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("Exception happened while closing SYFPEITHI response. Abort. ", e);
                }
            }
        }

        return results;
    }

    @Override
    protected void processLine(String line, Algorithm algorithm) {

        lineProcessor.feed(line);

        // Line:
        // position, Subsequence, Score
        // first, remove all "&nbsp" characters between the sequence elements. Then mark the different sections via translating "</td>" to ";", then remove all html tags with the last regex
        /*
                line = line.replaceAll("&nbsp;", "").replaceAll("</td>", ";").replaceAll("<[^<>]*>", "");

		if (line.isEmpty()) {
			logger.info("SYFPEITHI output line is empty after clearing out HTML. Abort.");
			return;
		}
		
		String[] entries = line.split(";");
		if (entries.length != 3) {
			logger.error("Output line "+line+" contains less or more than the mandatory 3 table entries (position, Subsequence, Score), did maybe something change??");
			return;
		}

		TemporaryEntry entry = new TemporaryEntry(originalAllel, entries[1], Integer.parseInt(entries[0]), algorithm, Double.parseDouble(entries[2]));
		results.add(entry);*/
    }

    private String preprocessFastaSequence(String inputSequence) {
        if (inputSequence == null) {
            return null;
        }
        String[] inputLines = inputSequence.split(splitRegex);
        if (inputLines != null) {
            String outputQuery = "";
            for (String line : inputLines) {
                if (!line.startsWith(fastaHeaderStartChar) && !line.startsWith(fastaCommentStartChar)) {
                    outputQuery = outputQuery + line;
                }
            }
            return outputQuery;
        } else {
            // something went wrong and input split from sequence returned null.
            // to avoid bad prediction, return empty string here.
            return "";
        }
    }

    final static Pattern posRE = Pattern.compile("<td class=\"pos\">(\\d+)</td>");

    final static Pattern scoreRE = Pattern.compile("<td class=\"score\">([\\d\\-]+)</td>");

    final static Pattern liganRE = Pattern.compile("<td class=\"ligand\">(.+)</td>");

    private class LineProcessor {

        String currentPos;

        String currentSequence;

        String currentScore;

        public void feed(String line) {
          
            currentPos = extract(posRE, line, currentPos);
            currentSequence = extract(liganRE, line, currentSequence);
            currentScore = extract(scoreRE, line, currentScore);

            if (isTheEnd(line)) {

                addCurrentEntry();

                currentPos = null;
                currentSequence = null;
                currentScore = null;
            }
        }

        public boolean isTheEnd(String line) {
            return line.contains("</tr>");
        }

        private void addCurrentEntry() {
            if (currentPos != null && currentSequence != null && currentScore != null) {
                currentSequence = currentSequence.replaceAll("&nbsp;", "").replaceAll("</td>", ";").replaceAll("<[^<>]*>", "").toUpperCase().trim();
               
                TemporaryEntry entry = new TemporaryEntry(originalAllel, currentSequence, Integer.decode(currentPos), Algorithm.SYFPEITHI, Double.parseDouble(currentScore));
                results.add(entry);
            }
        }

        private String extract(Pattern p, String input, String current) {
            Matcher m = p.matcher(input);
            if (m.find()) {
                return m.group(1);
            } else {
                return current;
            }
        }

    }

}
