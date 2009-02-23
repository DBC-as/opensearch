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

import dk.dbc.opensearch.common.types.DatadockJob;
/**
 * @author mro
 *
 */
public interface IRepositoryStore extends IPluggable 
{
    String storeCargoContainer( CargoContainer cargo, DatadockJob job ) throws Exception;
}
