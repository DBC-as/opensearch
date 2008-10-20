package dbc.opensearch.components.pti.tests;
/** \brief UnitTest for PTIPool class */

import dbc.opensearch.components.pti.PTIPool;
import dbc.opensearch.tools.FedoraHandler;
// import dbc.opensearch.tools.Estimate;
// import dbc.opensearch.tools.Processqueue;
// import dbc.opensearch.components.datadock.CargoContainer;

import java.util.concurrent.Executors;

import org.junit.*;
import static org.junit.Assert.*;

import static org.easymock.classextension.EasyMock.*;




// import java.util.concurrent.*;
// import java.util.Date;
// import dbc.opensearch.tools.PrivateAccessor;

import org.apache.log4j.Logger;

// import org.compass.core.Compass;
// import org.compass.core.CompassSession;
// import org.compass.core.CompassTransaction;
// import org.compass.core.xml.AliasedXmlObject;
// import org.compass.core.xml.dom4j.Dom4jAliasedXmlObject;
// import org.compass.core.xml.javax.NodeAliasedXmlObject;
// import org.compass.core.CompassException;

// import org.dom4j.Document;
// import org.dom4j.Element;
// import org.dom4j.io.SAXReader;

// import java.io.BufferedInputStream;

public class PTIPoolTest {
    FedoraHandler mockFedoraHandler; 

    @Before public void Setup(){
        mockFedoraHandler = createMock( FedoraHandler.class );
    }
    @Test public void constructorTest()throws Exception{
        
        
        PTIPool ptiPool = new PTIPool( 10, mockFedoraHandler );

    }
    @Test public void createAndJoinThreadTest(){}
}