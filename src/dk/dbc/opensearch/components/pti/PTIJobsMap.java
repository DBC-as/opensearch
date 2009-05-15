package dk.dbc.opensearch.components.pti;


import dk.dbc.opensearch.common.config.PtiConfig;
import dk.dbc.opensearch.common.pluginframework.JobMapCreator;
import dk.dbc.opensearch.common.types.InputPair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


public class PTIJobsMap extends JobMapCreator
{
    static Logger log = Logger.getLogger( PTIJobsMap.class );

    private static boolean initiated = false;
    private static ArrayList< String > ptiPluginsList = new ArrayList< String >();
    private static HashMap< InputPair< String, String >, ArrayList< String > > ptiJobMap;

    public PTIJobsMap()
    {
        //System.out.println( "PTIJobsMap constructor called" );
    }


    public static ArrayList< String > getPtiPluginsList( String submitter, String format ) throws ConfigurationException, IllegalArgumentException, IllegalStateException, IOException, SAXException, ParserConfigurationException
    {

        if( !initiated || ptiJobMap.isEmpty() )
        {
            //System.out.println( "initiating" );
            String path = PtiConfig.getPath();
            JobMapCreator.validateJobXmlFile( path );
            JobMapCreator.init( path );

            // is this possible, wont the init method throw an IllegalStateException
            if( jobMap == null )
            {
                throw new NullPointerException( "jobMap is null" );
            }
            ptiJobMap = jobMap;
            initiated = true;
        }
        // else
//         {
//             System.out.println( "already initiated" );
//         }


        ptiPluginsList = ptiJobMap.get( new InputPair< String, String >( submitter, format ) );
        return ptiPluginsList;
    }
}