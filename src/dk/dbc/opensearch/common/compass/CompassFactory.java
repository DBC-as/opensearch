/**
 * \file CompassFactory.java
 * \brief The CompassFactory class
 * \package tools
 */
package dk.dbc.opensearch.common.compass;

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


import dk.dbc.opensearch.common.config.CompassConfig;
import dk.dbc.opensearch.common.os.FileHandler;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.compass.core.Compass;
import org.compass.core.config.CompassConfiguration;


/**
 * \ingroup tools
 * \brief Compass factory. The role of this class is to build and hold one
 * Compass, and spawn references to it.
 */
public class CompassFactory
{
	Logger log = Logger.getLogger( CompassFactory.class );
	
	
	private static Compass compass = null;    

    
    /**
     * The getCompass method returns a reference to the Compass. If
     * none exist a new one is build and returned
     *
     * @return the Compass
     */
    public Compass getCompass() throws ConfigurationException
    {
        log.debug("Entering CompassFactory.getCompass");

        if( compass == null )
        {
            buildCompass();
        }
        
        return compass;
    }
    
    
    /**
     * builds the Compass with appropriate mapping and configuration files
     */
    private void buildCompass() throws ConfigurationException
    {
        log.debug("Entering CompassFactory.buildCompass");
        log.debug( "Setting up the Compass object" ); 

        log.debug( "Obtaining configuration parameters." );
        CompassConfiguration conf = new CompassConfiguration();

        String cfg  = CompassConfig.getConfigPath();
        String xsem = CompassConfig.getModifiedXSEMPath();
        log.debug( String.format( "Compass configuration = %s", cfg ) );
        log.debug( String.format( "XSEM mappings file    = %s", xsem ) );
       
        log.debug( "Building Compass." );
        conf.configure( FileHandler.getFile( cfg ) );
        conf.addFile( xsem );
        compass = conf.buildCompass();
    }        
}