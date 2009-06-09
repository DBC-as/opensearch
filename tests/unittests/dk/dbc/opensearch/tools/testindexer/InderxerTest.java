/**
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


package dk.dbc.opensearch.tools.testindexer;

/** \brief UnitTest for Indexer */


import dk.dbc.opensearch.common.db.IProcessqueue;
import dk.dbc.opensearch.common.fedora.IFedoraCommunication;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.statistics.IEstimate;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.tools.testindexer.Indexer;

import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.lang.InterruptedException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import mockit.Mock;
import mockit.MockClass;
import mockit.Mockit;

import org.apache.commons.configuration.ConfigurationException;
import org.compass.core.Compass;
import org.compass.core.CompassSession;
import static org.easymock.classextension.EasyMock.*;
import org.junit.*;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;


/**
 *
 */
public class InderxerTest
{

    IEstimate mockEstimate;
    IProcessqueue mockProcessqueue;
    IFedoraCommunication mockFedoraCommunication;
    Compass mockCompass;
    CompassSession mockSession;
    ThreadPoolExecutor mockThreadPoolExecutor;

    static FutureTask mockFutureDatadock = createMock( FutureTask.class );
    static FutureTask mockFuturePTI = createMock( FutureTask.class );

    @MockClass( realClass = Indexer.class )
    public static class MockIndexer
    {

        @Mock( invocations = 1 )
        public static FutureTask getDatadockTask( DatadockJob datadockjob, IEstimate estimate, IProcessqueue processqueue, IFedoraCommunication fedoraCommunication )
        {
            return mockFutureDatadock;
        }

        @Mock( invocations = 1 )
        public static FutureTask getPTITask( String fedoraPID, CompassSession session, IEstimate estimate, IFedoraCommunication fedoraCommunication )
        {
            return mockFuturePTI;
        }

    }

    /**
     *
     */
    @Test public void testIndexer() throws ClassNotFoundException, ConfigurationException, ExecutionException, InterruptedException, IOException, ParserConfigurationException, PluginResolverException, SAXException, ServiceException, URISyntaxException
    {
        mockEstimate = createMock( IEstimate.class );
        mockProcessqueue = createMock( IProcessqueue.class );
        mockFedoraCommunication = createMock( IFedoraCommunication.class );
        mockCompass = createMock( Compass.class );
        mockSession = createMock( CompassSession.class );
        mockThreadPoolExecutor = createMock( ThreadPoolExecutor.class );

        Mockit.setUpMocks( MockIndexer.class );

        URI uri = new URI( "testURI" );
        String submitter = "testSubmitter";
        String format = "testFormat";
        String PID = "testPID";
        DatadockJob datadockJob = new DatadockJob( uri, submitter, format, PID );
        float testfloat = 48.16f;
        long testlong = 48l;

        expect( mockCompass.openSession() ).andReturn( mockSession );

        expect( mockThreadPoolExecutor.submit( mockFutureDatadock ) ).andReturn( mockFutureDatadock );
        expect( mockThreadPoolExecutor.submit( mockFuturePTI ) ).andReturn( mockFuturePTI );

        expect( mockFutureDatadock.isDone() ).andReturn( false );
        expect( mockFutureDatadock.isDone() ).andReturn( true );
        expect( mockFutureDatadock.get() ).andReturn( testfloat );


        expect( mockFuturePTI.isDone() ).andReturn( false );
        expect( mockFuturePTI.isDone() ).andReturn( true );
        expect( mockFuturePTI.get() ).andReturn( testlong );

        replay( mockCompass );
        replay( mockFutureDatadock );
        replay( mockThreadPoolExecutor );
        replay( mockFuturePTI );

        Indexer testIndexer = new Indexer( mockCompass, mockEstimate, mockProcessqueue, mockFedoraCommunication, mockThreadPoolExecutor );
        testIndexer.index( datadockJob );

        verify( mockCompass );
        verify( mockFutureDatadock );
        verify( mockFuturePTI );
        verify( mockThreadPoolExecutor );

        // teardown
        Mockit.tearDownMocks();
        reset( mockEstimate );
        reset( mockProcessqueue );
        reset( mockFedoraCommunication );
        reset( mockCompass );
        reset( mockSession );
        reset( mockFutureDatadock );
        reset( mockFuturePTI );
    }
}

