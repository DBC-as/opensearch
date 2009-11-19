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
    private static ArrayList< String > datadockPluginList = new ArrayList< String >();
    private static HashMap< InputPair< String, String >, ArrayList< String > > datadockJobMap;

    public DatadockJobsMap() {}


    public static ArrayList< String > getDatadockPluginsList( String submitter, String format ) throws ConfigurationException, IOException, SAXException, ParserConfigurationException//, IllegalArgumentException, IllegalStateException
    {
        if( null == submitter ||
             null == format ||
             submitter.isEmpty() ||
             format.isEmpty() )
        {
            String error = "Submitter or format was given as null values, cannot continue";
            log.error( error );
            throw new IllegalStateException( error );
        }
        if( !initiated )
        {
            String path = DatadockConfig.getPath();
            JobMapCreator.validateXsdJobXmlFile( path );            
            JobMapCreator.init( path );

            datadockJobMap = JobMapCreator.jobMap;
            initiated = true;
        }
        datadockPluginList = datadockJobMap.get( new InputPair< String, String >( submitter, format ) );

        if( null == datadockPluginList )
        {
            String error = String.format( "Could not construct plugin list for submitter %s, format %s", submitter, format );
            log.error( error );
            throw new IllegalStateException( error );
        }
        return datadockPluginList;
    }
}