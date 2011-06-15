/**
 * \file PTIManagerConfig.java
 * \brief The PTIManagerConfig class
 * \package config;
 */

package dk.dbc.opensearch.config;

/*
   
This file is part of opensearch.
Copyright © 2009, Dansk Bibliotekscenter a/s, 
Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043

opensearch is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

opensearch is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
*/

import org.apache.commons.configuration.ConfigurationException;


public class PTIManagerConfig extends Config
{

    public PTIManagerConfig() throws ConfigurationException 
    {
		super();
	}

	/* *************************************
     * PTIMANAGER QUEUE RESULTSET MAX SIZE *
     * *************************************/

    private String getPtiManagerQueueResultsetMaxSize()
    {
        String ret = config.getString( "pti.queue-resultset-maxsize" );
        return ret;
    }

    public static String getQueueResultsetMaxSize() throws ConfigurationException
    {
        PTIManagerConfig pmc = new PTIManagerConfig();
        return pmc.getPtiManagerQueueResultsetMaxSize();
    }

    
    /* *************************************
     * PTIMANAGER REJECTED JOB SLEEP TIME  *
     * *************************************/
    private String getPtiManagerRejectedSleepTime()
    {
        String ret = config.getString( "pti.rejected-sleep-time" );
        return ret;
    }

    public static String getRejectedSleepTime() throws ConfigurationException
    {
        PTIManagerConfig pmc = new PTIManagerConfig();
        return pmc.getPtiManagerRejectedSleepTime();
    }
}

