/**
 * \file CompassConfig.java
 * \brief The CompassConfig class
 * \package config;
 */

package dk.dbc.opensearch.common.config;


import org.apache.commons.configuration.ConfigurationException;


/**
 * @author mro
 * 
 * Sub class of Config providing access to compass settings in the 
 * configuration file. Method names should be explanatory enough.
 * 
 * See super class Config for description of methodology.
 *
 */
public class CompassConfig extends Config
{    
    public CompassConfig() throws ConfigurationException 
    {
		super();
	}


    /* CONFIG PATH */
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
    
    
    /* XSEM PATH */
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
