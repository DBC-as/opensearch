/**
 * \file CompassConfig.java
 * \brief The CompassConfig class
 * \package config;
 */

package dk.dbc.opensearch.common.config;

import org.apache.commons.configuration.ConfigurationException;


/**
 * 
 */
public class CompassConfig extends Config
{    
    public CompassConfig() throws ConfigurationException 
    {
		super();
	}


	private String getCompassConfigPath()
    {
        String ret = config.getString( "compass.configpath" );
        return ret;
    }
    
    
    public static String getConfigPath() throws ConfigurationException
    {
        CompassConfig cc = new CompassConfig();
        return cc.getCompassConfigPath();
    }
    
    
    private String getCompassXSEMPath()
    {
        String ret = config.getString( "compass.xsempath" );
        return ret;
    }
    
    
    public static String getXSEMPath() throws ConfigurationException
    {
        CompassConfig cc = new CompassConfig();
        return cc.getCompassXSEMPath();
    }
    
}
