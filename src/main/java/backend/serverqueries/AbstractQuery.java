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

import java.util.Set;
import java.util.concurrent.Callable;

import utils.Constants.Algorithm;
import backend.entries.TemporaryEntry;
import java.util.logging.Logger;

public abstract class AbstractQuery implements Callable<Set<TemporaryEntry>> {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    public Set<TemporaryEntry> call() throws Exception {
        logger.info("Starting query for " + getClass().getSimpleName());
        Set<TemporaryEntry> queryServer = queryServer();
        int count = queryServer != null ? queryServer.size() : -1;
        logger.info(String.format("End query for %s : %d returned", getClass().getSimpleName(), count));
        return queryServer;
    }

    protected abstract Set<TemporaryEntry> queryServer();

    protected abstract void processLine(String line, Algorithm anAlgorithm);

}
