/**
 * \file IndexerMain.java
 * \brief The IndexerMain class
 * \package testindexer;
 */

package dk.dbc.opensearch.tools.testindexer;

import dk.dbc.opensearch.common.compass.CompassFactory;
import org.compass.core.Compass;
import org.apache.commons.configuration.ConfigurationException;

public class IndexerMain{


static public void main(String[] args) throws ConfigurationException
    {
        CompassFactory compassFactory = new CompassFactory();
        Compass compass = compassFactory.getCompass();
            
        System.out.println( "HEJ.... jeg er ikke implementeret endnu, nænej");    
    }
}