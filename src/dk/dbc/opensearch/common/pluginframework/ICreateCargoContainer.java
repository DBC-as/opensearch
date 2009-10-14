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


import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.components.datadock.DatadockJob;

import java.io.InputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


public interface ICreateCargoContainer extends IPluggable
{
	/**
     * \todo: describe
     *
     * @param job 
	 * @return the CargoContainer that results from the plugin activity 
     * @throws IOException if the URI provided by the DatadockJob from the init call could not be read
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
    public CargoContainer getCargoContainer( DatadockJob job, byte[] data ) throws PluginException;

}