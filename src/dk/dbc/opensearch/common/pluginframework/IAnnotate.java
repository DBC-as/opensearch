package dk.dbc.opensearch.common.pluginframework;


import dk.dbc.opensearch.common.types.CargoContainer;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


public interface IAnnotate extends IPluggable
{
    CargoContainer getCargoContainer( CargoContainer cargo ) throws IOException, ParserConfigurationException, SAXException;
}