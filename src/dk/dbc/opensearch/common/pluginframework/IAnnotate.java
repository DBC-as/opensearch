package dk.dbc.opensearch.common.pluginframework;


import dk.dbc.opensearch.common.types.CargoContainer;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;

public interface IAnnotate extends IPluggable
{
    CargoContainer getCargoContainer( CargoContainer cargo ) throws IOException,  XPathExpressionException;
}
