/**
 * \file CompassFactory.java
 * \brief The CompassFactory class
 * \package tools
 */
package dk.dbc.opensearch.common.compass;


import dk.dbc.opensearch.common.config.CompassConfig;
import dk.dbc.opensearch.common.os.FileHandler;

import java.io.File;
import java.net.URL;

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
    private static Compass compass = null;

    Logger log = Logger.getLogger("CompassFactory"); 

    
    /**
     * The getCompass method returns a reference to the Compass. If
     * none exist a new one is build and returned
     *
     * @return the Compass
     */
    public Compass getCompass()
    {
        log.debug("Entering CompassFactory.getCompass");

        if( compass == null )
            buildCompass();
        
        return compass;
    }
    
    
    /**
     * builds the Compass with appropriate mapping and configuration files
     */
    private void buildCompass()
    {
        log.debug("Entering CompassFactory.buildCompass");
        log.debug( "Setting up the Compass object" ); 

        log.debug( "Obtaining configuration parameters." );
        CompassConfiguration conf = new CompassConfiguration();
        //URL cfg = getClass().getResource("/compass.cfg.xml");
        //URL cpm = getClass().getResource("/xml.cpm.xml");

        String cfg = CompassConfig.getCompassConfigPath();
        String cpm = CompassConfig.getCompassXSEMPath();
        log.debug( String.format( "Compass configuration=%s", cfg ) );
        log.debug( String.format( "XSEM mappings file   =%s", cpm ) );
       
        log.debug( "Building Compass." );
        conf.configure( FileHandler.getFile( cfg ) );
        conf.addFile( cpm );
        compass = conf.buildCompass();
    }        
}