/** 
 * \file Log4jConfiguration.java
 * \brief Helper class used for configuring log4j framework
 * \package dk.dbc.opensearch.helpers;
 */
package dk.dbc.opensearch.helpers;


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


import dk.dbc.opensearch.config.FileSystemConfig;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


/**
 * Class used for configuration of the log4j framework.
 */
public class Log4jConfiguration
{
    static Logger log = Logger.getLogger( Log4jConfiguration.class );

    
    public static void configure( String configFile ) throws ConfigurationException
    {
        try
        {
            String path = FileSystemConfig.getConfigPath();
            String logFile = path + configFile; // "log4j_datadock.xml";
            DOMConfigurator.configure( logFile );

            log.debug( String.format( "Configuring log4j using logFile: '%s' done!", logFile ) );
        }
        catch( ConfigurationException ex )
        {
            log.error( "DatadockMain exception in log4j configuration" + ex ); 
            throw ex;
        } 
    }
}