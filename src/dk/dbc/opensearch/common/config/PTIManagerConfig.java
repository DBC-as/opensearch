/**
 * \file PTIManagerConfig.java
 * \brief The PTIManagerConfig class
 * \package config;
 */

package dk.dbc.opensearch.common.config;

public class PTIManagerConfig extends Config
{

    /* *************************************
     * PTIMANAGER QUEUE RESULTSET MAX SIZE *
     * *************************************/

    private String getPtiManagerQueueResultsetMaxSize()
    {
        String ret = config.getString( "pti.queue-resultset-maxsize" );
        return ret;
    }

    public static String getQueueResultsetMaxSize()
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

    public static String getRejectedSleepTime()
    {
        PTIManagerConfig pmc = new PTIManagerConfig();
        return pmc.getPtiManagerRejectedSleepTime();
    }
}

