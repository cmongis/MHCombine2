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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.entries.Algorithm;

public class IedbSmmpmbecQuery extends AbstractIedbQuery {
	
	private final Logger logger = LogManager.getLogger(IedbSmmpmbecQuery.class);
	
	public IedbSmmpmbecQuery(String sequence, String allel, String length) {
		super(Algorithm.IedbSmmpmbec,"smmpmbec",sequence, allel, length);
	}
        	
	
}
