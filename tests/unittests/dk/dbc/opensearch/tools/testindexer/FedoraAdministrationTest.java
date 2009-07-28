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

/** \brief UnitTest for FedoraAdministration */



import dk.dbc.opensearch.common.db.IProcessqueue;
import dk.dbc.opensearch.common.fedora.IFedoraAdministration;
import dk.dbc.opensearch.common.statistics.IEstimate;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.components.datadock.DatadockJob;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.tools.testindexer.FedoraAdministrationMock;

import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import static org.easymock.classextension.EasyMock.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.*;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;
import javax.xml.rpc.ServiceException;
import org.apache.commons.configuration.ConfigurationException;


/**
 * 
 */
public class FedoraAdministrationTest 
{
    CargoContainer mockCargoContainer;
    IProcessqueue mockProcessqueue;
    IEstimate mockEstimate;
    IFedoraAdministration mockFedoraAdministration;
    DatadockJob datadockJob;
    

    // public InputPair<String, Float> storeContainer( CargoContainer cc, DatadockJob datadockJob, IProcessqueue queue, IEstimate estimate ) throws ClassNotFoundException, IOException, MarshalException, ParseException, ParserConfigurationException, RemoteException, SAXException, SQLException, TransformerException, ValidationException{
    /**
     * 
     */

    @Test public void testFedoraAdministration() throws URISyntaxException, ClassNotFoundException, ConfigurationException, IOException, MarshalException, ParseException, ParserConfigurationException, ServiceException, SAXException, SQLException, TransformerException, ValidationException, XPathExpressionException
    {
        mockCargoContainer = createMock( CargoContainer.class);
        mockEstimate = createMock( IEstimate.class);
        mockProcessqueue = createMock( IProcessqueue.class );
        mockFedoraAdministration = createMock( IFedoraAdministration.class );

        URI uri = new URI( "testURI" );
        String submitter = "testSubmitter";
        String format = "testFormat";
        String PID = "testPID";
        datadockJob = new DatadockJob( uri, submitter, format, PID );

        // //InputPair result = new InputPair<String, Float>();
        
        IFedoraAdministration fc = new FedoraAdministrationMock();
        
        String result = fc.storeCargoContainer( mockCargoContainer, submitter, format );
    
        // //InputPair result = fc.storeContainer( mockCargoContainer, datadockJob, mockProcessqueue, mockEstimate );
        // assertEquals( result.getFirst(), PID );
        // assertEquals( result.getSecond(), -1.0f );

        // CargoContainer resultContainer = fc.retrieveContainer( PID );
        // assertEquals( mockCargoContainer, resultContainer );
    }
}