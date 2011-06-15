/**
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

/**
 * \file CompassConfig.java
 * \brief The CompassConfig class
 * \package dk.dbc.opensearch.config;
 */

package dk.dbc.opensearch.config;


import org.apache.commons.configuration.ConfigurationException;


/**
 * Sub class of Config providing access to compass settings in the 
 * configuration file. Method names should be explanatory enough.
 * 
 * See super class Config for description of methodology.
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


    /* MODIFIED XSEM PATH */
    private String getCompassModifiedXSEMPath()
    {
        String ret = config.getString( "compass.modified_xsempath" );
        return ret;
    }
    
    public static String getModifiedXSEMPath() throws ConfigurationException
    {
        CompassConfig cc = new CompassConfig();
        return cc.getCompassModifiedXSEMPath();
    }


    /* DTD PATH */
    private static String getCompassDTDPath()
    {
        String ret = config.getString( "compass.dtdpath" );
        return ret;
    }


    public static String getDTDPath() throws ConfigurationException
    {
        CompassConfig cc = new CompassConfig();
        return cc.getCompassDTDPath();   
    }


    /* HTTP URL */
    private static String getCompassHttpUrl()
    {
        String ret = config.getString( "compass.httpurl" );
        return ret;
    }


    public static String getHttpUrl() throws ConfigurationException
    {
        CompassConfig cc = new CompassConfig();
        return cc.getCompassHttpUrl();
    }
}
