package dk.dbc.opensearch.components.datadock;


import dk.dbc.opensearch.common.config.DatadockConfig;
import dk.dbc.opensearch.common.pluginframework.JobMapCreator;
import dk.dbc.opensearch.common.types.InputPair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


public class DatadockJobsMap extends JobMapCreator
{
    static Logger log = Logger.getLogger( DatadockJobsMap.class );

    private static boolean initiated = false;
    private static ArrayList< String > datadockPluginsList = new ArrayList< String >();
    private static HashMap< InputPair< String, String >, ArrayList< String > > datadockJobMap;

    public DatadockJobsMap()
    {
        //System.out.println( "DatadockJobsMap constructor called" );
    }


    public static ArrayList< String > getDatadockPluginsList( String submitter, String format ) throws ConfigurationException, IllegalArgumentException, IllegalStateException, IOException, SAXException, ParserConfigurationException
    {
        if( !initiated )
        {
            //System.out.println( "initiating" );
            String path = DatadockConfig.getPath();
            JobMapCreator.validateJobXmlFile( path );
            JobMapCreator.init( path );

            datadockJobMap = jobMap;
            initiated = true;
        }

        datadockPluginsList = datadockJobMap.get( new InputPair< String, String >( submitter, format ) );
        return datadockPluginsList;

    }
}