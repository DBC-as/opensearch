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

package dk.dbc.opensearch.common.fedora;


import dk.dbc.opensearch.common.config.FedoraConfig;
import dk.dbc.opensearch.common.db.IProcessqueue;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.fedora.FedoraHandle;
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


/**
 * FedoraCommunication has methods to facilitate storing and
 * retrieving of CargoContainers in the fedora Repository
 */

public interface IFedoraCommunication{

    /**
     * storeContainer Stores the cargoContainer in the repository, and
     * returns a pair, where the first element is the fedoraPid, and
     * the second is a estimate for how long before the data in the
     * container is searchable.
     *
     * @param cc The cargoContainer to store in the repository
     * @param DatadockJob Contains the fedoraPid and other informnation about how to store the container
     * @param queue the queue to push a new indexable job to, corresponding to the stored container
     * @param estimate The estimate class used to retrieve a estimate before data in container is indexed
     *
     * @return A pair, where the first element is the fedoraPid, and the second element is the retrieved estimate.
     *
     * @throws ClassNotFoundException if the database could not be initialised in the Estimation class \see dk.dbc.opensearch.tools.Estimate
     * @throws ConfigurationException if the FedoraHandler could not be initialized. \see dk.dbc.opensearch.tools.FedoraHandler
     * @throws IOException Thrown if the FedoraHandler Couldn't be initialized properly. \see FedoraHandle.
     * @throws MarshalException Thrown if something went wrong during the marshalling of the cargoiContainer.
     * @throws ParseException Thrown if the construction of the Foxml went wrong. \see FedoraTools
     * @throws ParserConfigurationException Thrown if the construction of the Foxml went wrong. \see FedoraTools
     * @throws SAXException  Thrown if the construction of the Foxml went wrong. \see FedoraTools
     * @throws SQLException if something went wrong communicating with the database, either through the queue or estimate. \IProcessqueue, \see IEstimate.
     * @throws RemoteException Thrown if the fedora repository is unreachable
     * @throws TransformerException Thrown if the construction of the Foxml went wrong. \see FedoraTools
     * @throws ValidationExceptiom Thrown if the construction of the Foxml went wrong. \see FedoraTools
     */

    public InputPair<String, Float> storeContainer( CargoContainer cc, DatadockJob datadockJob, IProcessqueue queue, IEstimate estimate ) throws ClassNotFoundException, IOException, MarshalException, ParseException, ParserConfigurationException, RemoteException, SAXException, SQLException, TransformerException, ValidationException, ServiceException, ConfigurationException;
    

    public String storeContainer( CargoContainer cc ) throws ClassNotFoundException, IOException, MarshalException, ParseException, ParserConfigurationException, RemoteException, SAXException, SQLException, TransformerException, ValidationException, ServiceException, ConfigurationException;
    


    /**
     * The retrieveContainer method retrieves a digital obejct
     * matching the fedoraPid, and assembles the corresponding
     * CargoContainer.
     *
     * @param fedoraPid the Pid of the digital Object to retrieve.
     *
     * @return The retrieved cargoContainer.
     *
     * @throws IOException Thrown if the FedoraHandler Couldn't be initialized properly. \see FedoraHandle.
     * @throws ParserConfigurationException Thrown if the construction of the xml went wrong. \see FedoraTools
     * @throws RemoteException Thrown if the fedora repository is unreachable
     * @throws SAXException  Thrown if the construction of the xml went wrong. \see FedoraTools
     */

    public CargoContainer retrieveContainer( String fedoraPid ) throws IOException, ParserConfigurationException, RemoteException, SAXException;
}