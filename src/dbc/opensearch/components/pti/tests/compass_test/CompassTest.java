package dbc.opensearch.components.pti.tests.compass_test;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.File;

import org.compass.core.Compass;
import org.compass.core.CompassSession;
import org.compass.core.CompassTransaction;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassConfigurationFactory;
import org.compass.core.config.CompassEnvironment;
import org.compass.core.converter.mapping.xsem.XmlContentMappingConverter;
//import org.compass.core.xml.dom4j.converter.XPPReaderXmlContentConverter; // Hvilken Converter skal vi bruge??
import org.compass.core.xml.dom4j.converter.SAXReaderXmlContentConverter; // Hvilken Converter skal vi bruge??
import org.compass.core.config.CompassSettings;
import java.net.URL;
import org.dom4j.io.SAXReader;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.compass.core.xml.AliasedXmlObject;
import org.compass.core.xml.dom4j.Dom4jAliasedXmlObject;
import java.lang.reflect.Array;
import org.apache.log4j.Logger;

public class CompassTest{
    
    private static Compass compass;

    Logger log = Logger.getLogger("CompassTest");
    
    public CompassTest (){

        CompassConfiguration conf = new CompassConfiguration();

        URL cfg = getClass().getResource("/compass.cfg.xml");
        URL cpm = getClass().getResource("/xml.cpm.xml");

        log.debug( String.format( "Compass configuration=%s", cfg.getFile() ) );
        log.debug( String.format( "XSEM mappings file   =%s", cpm.getFile() ) );

        File cpmFile = new File( cpm.getFile() );

        conf.configure( cfg );
        conf.addFile( cpmFile );

        compass = conf.buildCompass();        

        // a non-validating saxreader instance
        SAXReader saxReader = new SAXReader( false);

        File xmlFile = new File( "./src/dbc/opensearch/components/pti/tests/compass_test/data2.xml" );

        log.debug( String.format( "file=%s", xmlFile.getAbsolutePath() ) );

        Document doc = null;
        try{
            doc = saxReader.read( xmlFile );
        }
        catch( DocumentException de){
            System.out.println(String.format( "DocumentException=%s",de.getMessage() ) );
        }

        AliasedXmlObject xmlObjext = new Dom4jAliasedXmlObject( "data1", doc.getRootElement() ); 
        
        CompassSession session = compass.openSession();
        CompassTransaction trans = null;
        
        if( !session.isClosed() ){
            trans = session.beginTransaction();
        }
        session.save( xmlObjext );
        
        if( !session.isClosed() ){
            trans.commit();
            session.close();
        }

        
    }
    
}