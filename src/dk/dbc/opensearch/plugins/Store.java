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


package dk.dbc.opensearch.plugins;


import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import dk.dbc.opensearch.common.fedora.FedoraAdministration;
import dk.dbc.opensearch.common.pluginframework.IRepositoryStore;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.SAXException;


/**
 * Class for storing marcxchange sources
 */
public class Store implements IRepositoryStore
{
    private static Logger log = Logger.getLogger( Store.class );


    private PluginType pluginType = PluginType.STORE;


    public CargoContainer storeCargoContainer( CargoContainer cargo, String submitter, String format ) throws PluginException, MarshalException, ValidationException, MalformedURLException, RemoteException, ConfigurationException, ServiceException, IOException, SAXException, ParseException, ParserConfigurationException, TransformerException, XPathExpressionException
    {
    	log.debug( "just before fa.storeCargoContainer()" );
    	FedoraAdministration fa = new FedoraAdministration();
        /*String pid =*/ fa.storeCargoContainer( cargo, submitter, format );
        //cargo.setDCIdentifier( pid );
        
        return cargo;
    }


    public PluginType getPluginType()
    {
        return pluginType;
    }
}