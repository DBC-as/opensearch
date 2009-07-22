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

import dk.dbc.opensearch.common.config.FedoraConfig;
import dk.dbc.opensearch.common.db.IProcessqueue;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.fedora.IFedoraAdministration;
import dk.dbc.opensearch.common.helpers.XMLFileReader;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.statistics.IEstimate;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.components.datadock.DatadockJob;
import dk.dbc.opensearch.common.types.IndexingAlias;
import dk.dbc.opensearch.common.types.InputPair;

import fedora.server.types.gen.RelationshipTuple;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.ParseException;
import java.net.MalformedURLException;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.apache.commons.lang.NotImplementedException;


/**
 *
 */
public class FedoraAdministrationMock implements IFedoraAdministration
{
    /**
     *
     */
    
    private CargoContainer cc;

    public FedoraAdministrationMock(){}

    // public InputPair<String, Float> storeContainer( CargoContainer cc, DatadockJob datadockJob, IProcessqueue queue, IEstimate estimate ) throws ClassNotFoundException, IOException, MarshalException, ParseException, ParserConfigurationException, RemoteException, SAXException, SQLException, TransformerException, ValidationException{
    //     
        
    //     return new InputPair<String, Float>( datadockJob.getPID(), -1.0f );
        
    // }

    public CargoContainer retrieveCargoContainer( String fedoraPid ) throws IOException, ParserConfigurationException, RemoteException, SAXException
    {
    	System.out.println( "mock retrieve..." );
    	System.out.println( "cc: " + cc.toString() );
        return cc;
    }


    public String storeCargoContainer( CargoContainer cargo, String submitter, String format )throws IOException, MarshalException, ParseException, ParserConfigurationException, RemoteException, TransformerException, ValidationException
    {
    	System.out.println( "mockpid returned" );
        cc = cargo;
        System.out.println( "cargo: " + cargo.toString() );
        return "MOCK_PID";
    }



    public void deleteObject( String pid, boolean force ) throws RemoteException, ConfigurationException, MalformedURLException, ServiceException, IOException
    {
        throw new NotImplementedException( "Not implemented - shouldn't be used with this implementation" );
    }

   
    public boolean markObjectAsDeleted( String pid )
    {
        throw new NotImplementedException( "Not implemented - shouldn't be used with this implementation" );
    }


    public CargoContainer getDataStreamsOfType( String pid, DataStreamType streamtype ) throws MalformedURLException, IOException, RemoteException, ParserConfigurationException, SAXException, ServiceException, ConfigurationException
  {
        throw new NotImplementedException( "Not implemented - shouldn't be used with this implementation" );
    }
  

    public CargoContainer getDataStream( String pid, String streamID ) throws MalformedURLException, IOException, RemoteException, ParserConfigurationException, SAXException, ServiceException, ConfigurationException
  {
        throw new NotImplementedException( "Not implemented - shouldn't be used with this implementation" );
    }
      public String addDataStreamToObject( CargoObject cargo, String pid, boolean versionable, boolean overwrite ) throws RemoteException, MalformedURLException, ParserConfigurationException, TransformerConfigurationException, TransformerException, SAXException, IOException, ConfigurationException, ServiceException
  {
        throw new NotImplementedException( "Not implemented - shouldn't be used with this implementation" );
    }

    
    public String modifyDataStream( CargoObject cargo, String sID, String pid, boolean versionable, boolean breakdependencies ) throws RemoteException, MalformedURLException, IOException, ConfigurationException, ServiceException
  {
        throw new NotImplementedException( "Not implemented - shouldn't be used with this implementation" );
    }

    public boolean removeDataStream( String pid, String sID, String startDate, String endDate, boolean breakDependencies ) throws RemoteException, ParserConfigurationException, TransformerConfigurationException, TransformerException, IOException, SAXException, ConfigurationException, ServiceException
  {
        throw new NotImplementedException( "Not implemented - shouldn't be used with this implementation" );
    }


    public boolean addRelation( String pid, String predicate, String targetPid, boolean literal, String datatype ) throws RemoteException, ConfigurationException, MalformedURLException, ServiceException, IOException
  {
        throw new NotImplementedException( "Not implemented - shouldn't be used with this implementation" );
    }

    public RelationshipTuple[] getRelationships( String pid, String predicate) throws RemoteException, ConfigurationException, MalformedURLException, ServiceException, IOException
  {
        throw new NotImplementedException( "Not implemented - shouldn't be used with this implementation" );
    }

   
    public boolean hasRelationship( String subject, String predicate, String target, boolean isLiteral ) throws RemoteException, ConfigurationException, MalformedURLException, ServiceException, IOException 
  {
        throw new NotImplementedException( "Not implemented - shouldn't be used with this implementation" );
    }

    public String[] findObjectPids( String property, String operator, String value ) throws RemoteException, ConfigurationException, ServiceException, MalformedURLException, IOException
  {
        throw new NotImplementedException( "Not implemented - shouldn't be used with this implementation" );
    }
}