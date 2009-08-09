/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dk.dbc.opensearch.common.fedora;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.configuration.ConfigurationException;
import org.xml.sax.SAXException;


/**
 *
 */
public class FedoraUtils {

    /**
     * Creates a fedora digital object document XML representation of a given
     * {@link #CargoContainer}
     *
     * @param cargo the CargoContainer to create a foxml representation of
     * @return a byte[] containing the xml representation
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public static byte[] CargoContainerToFoxml( CargoContainer cargo ) throws ParserConfigurationException, TransformerConfigurationException, TransformerException, ServiceException, ConfigurationException, IOException, MalformedURLException, UnsupportedEncodingException, XPathExpressionException, SAXException
    {
        FoxmlDocument foxml = new FoxmlDocument( cargo.getDCIdentifier(), cargo.getCargoObject( DataStreamType.OriginalData ).getFormat(), cargo.getCargoObject( DataStreamType.OriginalData ).getSubmitter(), System.currentTimeMillis() );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        foxml.serialize( baos, null );

        return baos.toByteArray();
    }


}
