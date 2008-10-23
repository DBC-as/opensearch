/**
 * \file CompassFactory.java
 * \brief The CompassFactory class
 * \package tools
 */
package dbc.opensearch.tools;

import org.compass.core.Compass;
import org.compass.core.CompassSession;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassConfigurationFactory;
import org.compass.core.CompassException;


import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Logger;
import org.apache.commons.configuration.ConfigurationException;

import java.net.URL;
import java.io.File;

/**
 * \ingroup tools
 * \brief Compass factory. The role of this class is to build and hold one
 * Compass, and spawn references to it.
 */
public class CompassFactory{
    private static Compass compass = null;

    Logger log = Logger.getLogger("CompassFactory"); 

    /**
     * The getCompass method returns a reference to the Compass. If
     * none exist a new one is build and returned
     * @return the Compass
     */
    public Compass getCompass(){
        log.debug("Entering CompassFactory.getCompass");

        if( compass == null ){
            buildCompass();
        }
        return compass;
    }
    
    /**
     * builds the Compass with appropriate mapping and configuration files
     */
    private void buildCompass(){
        log.debug("Entering CompassFactory.buildCompass");

        log.debug( "Setting up the Compass object" );
 
        log.debug( "Obtaining configuration parameters." );
        CompassConfiguration conf = new CompassConfiguration();
        URL cfg = getClass().getResource("/compass.cfg.xml");
        URL cpm = getClass().getResource("/xml.cpm.xml");
        log.debug( String.format( "Compass configuration=%s", cfg.getFile() ) );
        log.debug( String.format( "XSEM mappings file   =%s", cpm.getFile() ) );
        File cpmFile = new File( cpm.getFile() );
 
        log.debug( "Building Compass." );
        conf.configure( cfg );
        conf.addFile( cpmFile );
        compass = conf.buildCompass();
    }        
}