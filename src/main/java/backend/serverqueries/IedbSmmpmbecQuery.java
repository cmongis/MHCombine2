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
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.entries.Algorithm;
import backend.entries.TemporaryEntry;

public class IedbSmmpmbecQuery extends AbstractIedbQuery {
	
	private final Logger logger = LogManager.getLogger(IedbSmmpmbecQuery.class);
	
	public IedbSmmpmbecQuery(String sequence, String allel, Integer length) {
		super(Algorithm.IedbSmmpmbec,"smmpmbec",sequence, allel, length);
	}
        
       
		
	
}
