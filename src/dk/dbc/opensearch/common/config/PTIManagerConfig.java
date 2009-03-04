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

    private String getQueueResultsetMaxSize()
    {
        String ret = config.getString( "pti.queue-resultset-maxsize" );
        return ret;
    }

    public static String getPTIManagerQueueResultsetMaxSize()
    {
        PTIManagerConfig pmc = new PTIManagerConfig();
        return pmc.getQueueResultsetMaxSize();
    }

    /* *************************************
     * PTIMANAGER REJECTED JOB SLEEP TIME  *
     * *************************************/

    private String getRejectedSleepTime()
    {
        String ret = config.getString( "pti.rejected-sleep-time" );
        return ret;
    }

    public static String getPTIManagerRejectedSleepTime()
    {
        PTIManagerConfig pmc = new PTIManagerConfig();
        return pmc.getRejectedSleepTime();
    }
}

