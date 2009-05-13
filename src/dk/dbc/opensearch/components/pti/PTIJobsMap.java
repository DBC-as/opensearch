package dk.dbc.opensearch.components.pti;


import dk.dbc.opensearch.common.config.PtiConfig;
import dk.dbc.opensearch.common.pluginframework.JobMapCreator;
import dk.dbc.opensearch.common.types.InputPair;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


public class PTIJobsMap extends JobMapCreator 
{
    static Logger log = Logger.getLogger( PTIJobsMap.class );
	
	
    private static ArrayList< String > PtiJobsMap = new ArrayList< String >();
	
    
    public PTIJobsMap()
    {
        System.out.println( "PTIJobsMap constructor called" );
    }
	
	
    public static ArrayList< String > getPtiPluginsList( String submitter, String format ) throws ConfigurationException, IllegalArgumentException, IllegalStateException, IOException, SAXException, ParserConfigurationException
    {
        String path = PtiConfig.getPath();
        JobMapCreator.init( path );
        JobMapCreator.validateJobXmlFile( path );
	
        if ( jobMap != null )
        {
            PtiJobsMap = jobMap.get( new InputPair< String, String >( submitter, format ) );
            return PtiJobsMap;
        }
        else
	{
            throw new NullPointerException( "jobMap is null" );
        }
    }
}