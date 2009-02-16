/**
 * 
 */
package dk.dbc.opensearch.common.pluginframework;

import java.io.IOException;
import java.text.ParseException;

import javax.xml.rpc.ServiceException;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import dk.dbc.opensearch.common.types.CargoContainer;

/**
 * @author mro
 *
 */
public interface IRepositoryStore extends IPluggable {
	void init( CargoContainer cargo ) throws ServiceException;
	
	String storeCargoContainer() throws MarshalException, ValidationException, IllegalStateException, ServiceException, IOException, ParseException;
}
