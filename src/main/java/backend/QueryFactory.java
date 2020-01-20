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
package backend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.serverqueries.AbstractQuery;
import backend.serverqueries.IedbAnnQuery;
import backend.serverqueries.IedbConsensusQuery;
import backend.serverqueries.IedbNetMHCconsQuery;
import backend.serverqueries.IedbNetMHCpanQuery;
import backend.serverqueries.IedbPickPocketQuery;
import backend.serverqueries.IedbRecommendedQuery;
import backend.serverqueries.IedbSmmQuery;
import backend.serverqueries.IedbSmmpmbecQuery;
import backend.serverqueries.NetMHCpan28Query;
import backend.serverqueries.NetMHC34Query;
import backend.serverqueries.NetMHCPan30Query;
import backend.serverqueries.NetMHCPan40Query;
import backend.serverqueries.NetMHCQuery;
import backend.serverqueries.SyfpeithiQuery;

public class QueryFactory {

    private final Logger logger = LogManager.getLogger(QueryFactory.class);

    public QueryFactory() {

    }

    public AbstractQuery createQueryForServer(String server, String sequence, String allel, Integer length) {
        switch (server) {
            case "NetMHC40":
                return new NetMHCQuery(sequence, allel, length);
            case "NetMHC34":
                return new NetMHC34Query(sequence, allel, length);
            case "NetMHCpan40":
                return new NetMHCPan40Query(sequence, allel, length);
            case "NetMHCpan30":
                return new NetMHCPan30Query(sequence, allel, length);

            case "NetMHCpan28":
                return new NetMHCpan28Query(sequence, allel, length);

            case "IEDBNetMHCcons":
                return new IedbNetMHCconsQuery(sequence, allel, length);
            case "IEDBPickPocket":
                return new IedbPickPocketQuery(sequence, allel, length);
            case "IedbNetMHCpan":
                return new IedbNetMHCpanQuery(sequence, allel, length);
            case "IedbAnn":
                return new IedbAnnQuery(sequence, allel, length);
            case "IedbRecommended":
                return new IedbRecommendedQuery(sequence, allel, length);
            case "IedbConsensus":
                return new IedbConsensusQuery(sequence, allel, length);
            case "IedbSmmpmbec":
                return new IedbSmmpmbecQuery(sequence, allel, length);
            case "IedbSmm":
                return new IedbSmmQuery(sequence, allel, length);
            case "SYFPEITHI":
                return new SyfpeithiQuery(sequence, allel, length);
            // Deprecate BIMAS
//		case "BIMAS":
//			return new BimasQuery(sequence, allel, length);
            default:
                logger.warn("Attempted to create query for server " + server + ", no query type found! Returning null.");
                break;
        }
        return null;
    }

}
