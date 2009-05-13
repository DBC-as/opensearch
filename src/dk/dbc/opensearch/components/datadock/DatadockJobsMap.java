package dk.dbc.opensearch.components.datadock;


import dk.dbc.opensearch.common.config.DatadockConfig;
import dk.dbc.opensearch.common.pluginframework.JobMapCreator;
import dk.dbc.opensearch.common.types.InputPair;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


public class DatadockJobsMap extends JobMapCreator 
{
    static Logger log = Logger.getLogger( DatadockJobsMap.class );
	
	
    private static ArrayList< String > DatadockPluginsList = new ArrayList< String >();
	
	
    public DatadockJobsMap()
    {
        System.out.println( "PTIJobsMap constructor called" );
    }
	
	
    public static ArrayList< String > getDatadockPluginsList( String submitter, String format ) throws ConfigurationException, IllegalArgumentException, IllegalStateException, IOException, SAXException, ParserConfigurationException
    {
        String path = DatadockConfig.getPath();
        JobMapCreator.init( path );
        JobMapCreator.validateJobXmlFile( path );
		
        if ( jobMap != null )
        {
            DatadockPluginsList = jobMap.get( new InputPair< String, String >( submitter, format ) );
            return DatadockPluginsList;
        }
        else
	{
            throw new NullPointerException( "jobMap is null" );
        }
    }
}