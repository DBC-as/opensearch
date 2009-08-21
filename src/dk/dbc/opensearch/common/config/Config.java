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


package dk.dbc.opensearch.common.config;


import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;


/**
 * Super (or base) class for the config classes. Hence: 
 * 	 
 *       DO NOT ALTER THIS CLASS IF IT CAN BE AVOIDED!!!
 *    
 * It should read one config file and make this file accessible via a 
 * constructor -- and do nothing else! That is, the sole purpose of this 
 * class is to provide access to the configuration file:
 *                   
 *                   ../config/config.xml. 
 *                    
 * This file is parsed and made available to sub classes through a non 
 * static object.
 * 
 * The intended use is for sub classes to provide access to configuration 
 * settings via static methods. This is done via:
 * 
 *     1) a private non static method, and 
 *     2) a public static method using a sub class object. Throws a 
 *        ConfigurationException.
 * 
 * Sub classes shall implement a constructor to handle the exception 
 * thrown in case the config file cannot be read (ConfigurationException).
 */
public class Config 
{
    Logger log = Logger.getLogger( Config.class );
	
	
    URL cfgURL = getClass().getResource( "/config.xml" );
    static XMLConfiguration config;
    

    /**
     * Essential method providing access to the solution's config file.
     * 
     * @throws ConfigurationException
     */
    public Config() throws ConfigurationException
    {
        try 
        {
            log.debug( String.format( "Creating config XMLConfiguration object from '%s'", cfgURL ) );
            config = new XMLConfiguration( cfgURL );
        } 
        catch ( ConfigurationException e ) 
        {
            log.fatal( "ConfigurationException caught in class Config:" );
            log.fatal( e.getStackTrace().toString() );
            throw e;
        }
    }
}
