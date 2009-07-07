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
import dk.dbc.opensearch.common.fedora.IFedoraCommunication;
//import dk.dbc.opensearch.common.fedora.FedoraTools;
import dk.dbc.opensearch.common.helpers.XMLFileReader;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.statistics.IEstimate;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.components.datadock.DatadockJob;
import dk.dbc.opensearch.common.types.IndexingAlias;
import dk.dbc.opensearch.common.types.InputPair;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.ParseException;

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
public class FedoraCommunication implements IFedoraCommunication{
    /**
     *
     */
    
    private CargoContainer cc;

    public FedoraCommunication() {}

    public InputPair<String, Float> storeContainer( CargoContainer cc, DatadockJob datadockJob, IProcessqueue queue, IEstimate estimate ) throws ClassNotFoundException, IOException, MarshalException, ParseException, ParserConfigurationException, RemoteException, SAXException, SQLException, TransformerException, ValidationException{
        this.cc = cc;
        
        return new InputPair<String, Float>( datadockJob.getPID(), -1.0f );
        
    }

    public CargoContainer retrieveContainer( String fedoraPid ) throws IOException, ParserConfigurationException, RemoteException, SAXException{
        return cc;
    }


    public String storeContainer( CargoContainer cargo )throws ClassNotFoundException, IOException, MarshalException, ParseException, ParserConfigurationException, RemoteException, SAXException, SQLException, TransformerException, ValidationException
    {

        throw new NotImplementedException( "Not just yet" );

    }

}