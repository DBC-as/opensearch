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


/**
 * \file DatadockMainTest.java
 * \brief The DatadockManagerTest class
 * \package tests;
 */

package dk.dbc.opensearch.components.datadock;



import dk.dbc.opensearch.components.harvest.IHarvest;
import dk.dbc.opensearch.components.harvest.FileHarvest;
import dk.dbc.opensearch.common.pluginframework.PluginTask;
import java.util.List;
import mockit.Mock;
import mockit.MockClass;
import dk.dbc.opensearch.common.db.Processqueue;
import mockit.Mockit;
import dk.dbc.opensearch.common.db.IDBConnection;
import java.util.HashMap;
import mockit.Expectations;
import dk.dbc.opensearch.common.config.DatadockConfig;
import dk.dbc.opensearch.common.db.PostgresqlDBConnection;
import dk.dbc.opensearch.common.fedora.FedoraObjectRepository;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.pluginframework.FlowMapCreator;
import dk.dbc.opensearch.common.pluginframework.PluginResolver;
import java.io.File;
import java.util.Map;
import mockit.Deencapsulation;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * 
 */
public class DatadockMainTest 
{

    @Mocked DatadockConfig configClass;
    @Mocked IDBConnection dbConn;
    
//    @BeforeClass
//    public static void setUpClass() throws Exception
//    {
//        BasicConfigurator.configure();
//        LogManager.getRootLogger().setLevel( Level.TRACE );
//    }

    private Expectations setConfigExpectations() throws Exception
    {
        return new NonStrictExpectations()
        {{
                DatadockConfig.getPluginFlowXmlPath();returns( new File( "." ) );
                DatadockConfig.getPluginFlowXsdPath();returns( new File( "." ) );
        }};
    }

    @Test( expected = ConfigurationException.class )
    public void testConstructorNullChecks() throws Exception
    {
        new DatadockMain();
    }

    @Test
    public void testConstructorCorrectlyInitialized() throws Exception
    {
        Expectations a = setConfigExpectations();
        new DatadockMain();
    }

    @Test
    public void testSetServerMode() throws Exception
    {
        setConfigExpectations();

        DatadockMain datadock = new DatadockMain();
        Deencapsulation.invoke( datadock, "setServerMode" );
        Boolean field = (Boolean)Deencapsulation.getField( datadock, "terminateOnZeroSubmitted" );
        assertFalse( field.booleanValue() );
    }

    @Ignore( "Until I figure out how to get to the method-private field 'mode'")
    @Test
    public void testSetServerModeTerminateOnZeroSubmitted() throws Exception
    {
        setConfigExpectations();

        DatadockMain datadock = new DatadockMain();
        Deencapsulation.invoke( datadock, "setServerMode" );
        Deencapsulation.setField( datadock, "mode", "true" );
        Boolean field = (Boolean)Deencapsulation.getField( datadock, "terminateOnZeroSubmitted" );
        System.out.println( String.format( "%s", field.booleanValue() ) );
        assertTrue( field.booleanValue() );
    }

    @Test
    public void testInitializeServices() throws Exception
    {
        setConfigExpectations();
        DatadockMain datadock = new DatadockMain();
        Mockit.setUpMock( PostgresqlDBConnection.class, MockDBConnection.class );
        Mockit.setUpMock( FedoraObjectRepository.class, MockRepository.class );
        Mockit.setUpMock( FlowMapCreator.class, MockMapCreator.class );
        Mockit.setUpMock( FileHarvest.class, MockHarvest.class );
        Mockit.setUpMock( DatadockManager.class, MockManager.class );
        Deencapsulation.setField( datadock, "queueSize", 1 );
        Deencapsulation.setField( datadock, "corePoolSize", 1 );
        Deencapsulation.setField( datadock, "maxPoolSize", 1);
        Deencapsulation.setField( datadock, "keepAliveTime", 1);
        Deencapsulation.invoke( datadock, "initializeServices" );
    }

    @MockClass( realClass=PostgresqlDBConnection.class)
    public static class MockDBConnection{
        @Mock public void $init(){}
    }

    @MockClass( realClass=FedoraObjectRepository.class )
    public static class MockRepository{
        @Mock public void $init(){}
    }

    @MockClass( realClass=FlowMapCreator.class )
    public static class MockMapCreator{
        @Mock public void $init( File a, File b ){}
        @Mock
        public Map<String, List<PluginTask>> createMap( PluginResolver a, IObjectRepository b )
        {
            return new HashMap<String,List<PluginTask>>();
        }
    }

    @MockClass( realClass=FileHarvest.class )
    public static class MockHarvest{
        @Mock public void $init(){}
    }

    @MockClass( realClass=DatadockManager.class )
    public static class MockManager
    {
        @Mock public void $init( DatadockPool a, IHarvest b, Map< String, List< PluginTask > > c){}
    }

}
