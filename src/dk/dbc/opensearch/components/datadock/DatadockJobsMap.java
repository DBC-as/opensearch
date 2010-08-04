/*
  This file is part of opensearch.
  Copyright © 2009, Dansk Bibliotekscenter a/s,
  Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043

  opensearch is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  opensearch is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * \file DatadockJobsMap
 * \brief
 */


package dk.dbc.opensearch.components.datadock;


import dk.dbc.opensearch.common.config.DatadockConfig;
import dk.dbc.opensearch.common.config.FileSystemConfig;
import dk.dbc.opensearch.common.pluginframework.JobMapCreator;
import dk.dbc.opensearch.common.types.Pair;

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
    private static ArrayList< String > datadockPluginList = new ArrayList< String >();
    private static HashMap< Pair< String, String >, ArrayList< String > > datadockJobMap;

    public DatadockJobsMap() {}


    private static void init( String submitter, String format )
    {
        if ( submitter == null || submitter.isEmpty() ||
             format == null || format.isEmpty() )
        {
            String error = "Submitter or format was given as null values, cannot continue";
            log.error( error );
            throw new IllegalStateException( error );
        }

        if ( ! initiated )
        {
            try
            {
                String XMLPath = DatadockConfig.getPath();
                String XSDPath = FileSystemConfig.getDataDockJobsXsdPath();

                JobMapCreator.validateXsdJobXmlFile( XMLPath, XSDPath );
                JobMapCreator.init( XMLPath );

                datadockJobMap = JobMapCreator.jobMap;
                initiated = true;
            }catch( ParserConfigurationException ex )
            {
                String error = "Could not configure parser for reading ";
                log.error( error, ex );
                throw new IllegalStateException( error, ex );
                    
            }catch(  ConfigurationException ex )
            {
                String error = "Could not configure parser for reading ";
                log.error( error, ex );
                throw new IllegalStateException( error, ex );

            }catch( SAXException ex )
            {
                String error = "Could not read configuration file";
                log.error( error, ex );
                throw new IllegalStateException( error, ex );

            }catch( IOException ex )
            {
                String error = "Could not read configuration file";
                log.error( error, ex );
                throw new IllegalStateException( error, ex );
            }
        }
    }

    public static boolean hasPluginList( String submitter, String format )
    {
        init( submitter, format );

        if( null ==  datadockJobMap.get( new Pair< String, String >( submitter, format ) ) )
        {
            return false;
        }
        return true;
    }


    public static ArrayList< String > getDatadockPluginsList( String submitter, String format ) throws ConfigurationException, IOException, SAXException, ParserConfigurationException//, IllegalArgumentException, IllegalStateException
    {
        init( submitter, format );

        datadockPluginList = datadockJobMap.get( new Pair< String, String >( submitter, format ) );

        if ( datadockPluginList == null )
        {
            String error = String.format( "Could not construct plugin list for submitter %s, format %s", submitter, format );
            log.error( error );
            throw new IllegalStateException( error );
        }

        return datadockPluginList;
    }


    /**
     * @return indexalias if one is found, null otherwise
     */
    public static String getIndexingAlias( String submitter, String format ) throws ConfigurationException, IOException, ParserConfigurationException, SAXException
    {
        init( submitter, format );

        return aliasMap.get(new Pair< String, String >( submitter, format ) );
    }
}