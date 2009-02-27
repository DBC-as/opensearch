/**
 * 
 */
package dk.dbc.opensearch.common.pluginframework;


import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DatadockJob;

import java.io.IOException;
import java.text.ParseException;

import javax.xml.rpc.ServiceException;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * @author mro
 *
 */
public interface IRepositoryStore extends IPluggable 
{
    float storeCargoContainer( CargoContainer cargo, DatadockJob job ) throws MarshalException, ValidationException, IllegalStateException, ServiceException, IOException, ParseException, ParserConfigurationException, SAXException;
}
