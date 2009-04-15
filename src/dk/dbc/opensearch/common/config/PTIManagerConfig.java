/**
 * \file PTIManagerConfig.java
 * \brief The PTIManagerConfig class
 * \package config;
 */

package dk.dbc.opensearch.common.config;

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

