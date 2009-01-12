package dk.dbc.opensearch.components.datadock.tests;
/**\brief UnitTest for the DataDock */
import static org.junit.Assert.*;
import org.junit.*;
import static org.easymock.classextension.EasyMock.*;

import dk.dbc.opensearch.components.datadock.DataDock;

import dk.dbc.opensearch.components.datadock.CargoContainer;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.fedora.FedoraHandler;

public class DataDockTest{
    
    CargoContainer mockCargoContainer = createMock( CargoContainer.class );
    Estimate mockEstimate = createMock( Estimate.class );
    Processqueue processqueue = createMock( Processqueue.class );
    FedoraHandler fedeoraHandler = createMock ( FedoraHandler.class );
    DataDock dataDock;

    @Before public void setup(){
    
    }

    @After public void teardown(){
        reset( mockCargoContainer );
        reset( mockEstimate );
        reset( processqueue );
        reset( fedeoraHandler );
    }

    @Test public void dataDockContructorTest(){
    
    }


}