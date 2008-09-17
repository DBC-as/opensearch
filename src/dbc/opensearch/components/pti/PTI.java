package dbc.opensearch.components.pti;

import dbc.opensearch.components.datadock.CargoContainer;

// import java.io.ByteArrayInputStream;
// import java.io.File;
// import java.io.IOException;
// import java.net.URL;
// // import javax.xml.parsers.DocumentBuilderFactory;
// // import javax.xml.parsers.DocumentBuilder;
// import org.compass.core.Compass;
// import org.compass.core.CompassSession;
// import org.compass.core.CompassTransaction;
// import org.compass.core.config.CompassConfiguration;
// import org.compass.core.config.CompassConfigurationFactory;
// import org.compass.core.xml.AliasedXmlObject;
// import org.compass.core.xml.dom4j.Dom4jAliasedXmlObject;
// import org.compass.core.xml.javax.NodeAliasedXmlObject;
// import org.compass.core.CompassException;
// import org.apache.log4j.Logger;

// import org.dom4j.Document;
// import org.dom4j.DocumentHelper;
// import org.dom4j.DocumentException;
// import org.dom4j.io.SAXReader;

// import org.apache.commons.configuration.ConfigurationException;

public class PTI implements Callable<Float>{
    

    /**
     * Log
     */
    private static final Logger log = Logger.getRootLogger();

    
    /**
     * Constructor
     */
    public PTI( String fedorahandle, compasssession s ){
        log.debug( "Entering PTI Constructor" );
    }
    /**
     * call is the main function of the PTI class. It reads the data
     * pointed to by the fedorahandler given to the class in the
     * constructor and indexes it with compass, and finally returning
     * a float, representing the processtime for the data pointed to
     * by the fedorahandle.
     * @return the processtime
     */
    public float call(){
        log.debug( "PTI call function" );

        // start timer 
        // retrive data from handle
        // start compasss session
        // index data
        // store data 
        // id processtime
        // update estimation base
        // return processtime
    }
    
    
}