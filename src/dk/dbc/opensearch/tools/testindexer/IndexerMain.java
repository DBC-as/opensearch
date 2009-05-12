/**
 * \file IndexerMain.java
 * \brief The IndexerMain class
 * \package testindexer;
 */

package dk.dbc.opensearch.tools.testindexer;

import dk.dbc.opensearch.common.compass.CompassFactory;
import org.compass.core.Compass;
import org.apache.commons.configuration.ConfigurationException;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassEnvironment;

import org.compass.core.xml.dom4j.converter.SAXReaderXmlContentConverter;
import org.compass.core.converter.mapping.xsem.XmlContentMappingConverter;

import java.net.URL;
import java.net.MalformedURLException;
import javax.xml.rpc.ServiceException;
import java.io.IOException;

public class IndexerMain{

    static public void main(String[] args) throws ConfigurationException, MalformedURLException, ServiceException, IOException
    {


        URL file = new URL( "xml.cpm.xml");        
        CompassConfiguration conf = new CompassConfiguration()
            .addURL( file )
            .setSetting( CompassEnvironment.CONNECTION, "test-dir" ) // get from command line parameter
            //.setSetting( CompassEnvironment.Converter.DefaultTypeNames.Mapping.XML_CONTENT_MAPPING, org.compass.core.converter.mapping.xsem.XmlContentMappingConverter )
            .setSetting( CompassEnvironment.Converter.TYPE, "org.compass.core.converter.mapping.xsem.XmlContentMappingConverter" )
            .setSetting( CompassEnvironment.Converter.XmlContent.TYPE, "org.compass.core.xml.dom4j.converter.SAXReaderXmlContentConverter" );
        // cpm.xml skal også sættes !!!

        Compass compass = conf.buildCompass();
        
        // indexer
        Indexer indexer = new Indexer();
        

        System.out.println( "HEJ.... jeg er ikke implementeret endnu, nænej");    
    }
}