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

package dk.dbc.opensearch.common.pluginframework;


import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.configuration.ConfigurationException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.SAXException;

import dk.dbc.opensearch.common.types.CargoContainer;


/**
 * Defines the behavior for all plugins that stores information or data in
 * repositories.
 */
public interface IRepositoryStore extends IPluggable 
{
    /**
     * Stores {@code cargo} in a repository.
     * @param cargo the {@link CargoContainer} to store
     * @param submitter
     * @return
     * @throws PluginException
     * @throws MarshalException
     * @throws ValidationException
     * @throws MalformedURLException
     * @throws RemoteException
     * @throws ConfigurationException
     * @throws ServiceException
     * @throws IOException
     * @throws SAXException
     * @throws ParseException
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws XPathExpressionException
     */
    CargoContainer storeCargoContainer( CargoContainer cargo ) throws PluginException, MarshalException, ValidationException, MalformedURLException, RemoteException, ConfigurationException, ServiceException, IOException, SAXException, ParseException, ParserConfigurationException, TransformerException, XPathExpressionException;
}
