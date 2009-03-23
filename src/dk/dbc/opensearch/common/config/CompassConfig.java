/**
 * \file CompassConfig.java
 * \brief The CompassConfig class
 * \package config;
 */

package dk.dbc.opensearch.common.config;


/**
 * 
 */
public class CompassConfig extends Config{
    
    private String getConfigPath(){
        String ret = config.getString( "compass.configpath" );
        return ret;
    }
    public static String getCompassConfigPath(){
        CompassConfig cc = new CompassConfig();
        return cc.getConfigPath();
    }
    private String getXSEMPath(){
        String ret = config.getString( "compass.cpmpath" );
        return ret;
    }
    public static String getCompassXSEMPath(){
        CompassConfig cc = new CompassConfig();
        return cc.getXSEMPath();
    }
    
}
