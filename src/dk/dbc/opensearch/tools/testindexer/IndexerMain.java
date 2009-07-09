/**
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


package dk.dbc.opensearch.tools.testindexer;


import dk.dbc.opensearch.common.compass.CompassFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.compass.core.Compass;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassEnvironment;
import org.compass.core.converter.mapping.xsem.XmlContentMappingConverter;
import org.compass.core.xml.dom4j.converter.SAXReaderXmlContentConverter;
import org.compass.core.CompassSession;
import dk.dbc.opensearch.tools.testindexer.Estimate;
import dk.dbc.opensearch.tools.testindexer.FedoraCommunication;
import dk.dbc.opensearch.tools.testindexer.Processqueue;
import dk.dbc.opensearch.common.db.IProcessqueue;
import dk.dbc.opensearch.common.fedora.FedoraAdministration;
import dk.dbc.opensearch.common.fedora.IFedoraCommunication;
import dk.dbc.opensearch.common.statistics.IEstimate;
import dk.dbc.opensearch.components.datadock.DatadockJob;
import java.util.concurrent.ExecutionException;

/**
 * The main class for the testindexer. Provides parsing of commandline
 * properties and arguments.
 */
public class IndexerMain
{

    /**
     * The Main method
     */
    static public void main( String[] args ) throws ConfigurationException, IOException, MalformedURLException, ServiceException
    {
        // Validating properties

        // submitter
        String submitter = System.getProperty( "submitter" );
        if ( submitter == null )
        {
            System.err.println( "submitter option must be specified" );
            System.err.println( usage() );
            System.exit( 1 );
        }

        // format
        String format = System.getProperty( "format" );
        if ( format == null )
        {
            System.err.println( "format option must be specified" );
            System.err.println( usage() );
            System.exit( 1 );
        }

        // mapping
        String mappingString = System.getProperty( "mapping" );
        if ( mappingString == null )
        {
            System.err.println( "mapping option must be specified" );
            System.err.println( usage() );
            System.exit( 1 );
        }
        File mappingFile = new File( mappingString );
        if ( ! mappingFile.exists() )
        {
            System.err.println( String.format( "mappingfile: %s, does not exist", mappingString ) );
            System.err.println( usage() );
            System.exit( 1 );
        }
        URL mapping = mappingFile.toURI().toURL();

        // index dir
        String indexDir = System.getProperty( "index" );
        if ( indexDir == null )
        {
            System.err.println( "index option must be specified" );
            System.err.println( usage() );
            System.exit( 1 );
        }

        // Validating arguments
        ArrayList<String> jobs = new ArrayList<String>();

        for ( String arg: args )
        {
            File jobfile = new File( arg );
            if ( ! jobfile.exists() )
            {
                System.err.println( String.format( "Cannot find : %s, directory or file does not exist", arg ) );
                System.exit( 1 );
            }
            if ( jobfile.isDirectory() )
            {
                jobs.addAll( listFiles( jobfile ) );
            }
            else // isfile
            {
                jobs.add( arg );
            }
        }

        if ( jobs.isEmpty() )
        {
            System.err.println( "no files to index" );
            System.err.println( usage() );
            System.exit( 1 );
        }

        // starting indexing
        System.out.println( "Starting indexing" );
        System.out.println( String.format( "submitter: %s", submitter ) );
        System.out.println( String.format( "format:    %s", format ) );
        System.out.println( String.format( "mapping:   %s", mapping.toString() ) );
        System.out.println( String.format( "indexDir:  %s", indexDir ) );

        System.out.println( "Indexing the following files:" );
        for ( String j : jobs )
        {
            System.out.println( " " + j );
        }

        // Configuring Compass
        Compass compass = buildCompass( mapping, indexDir );

        IEstimate e = new Estimate();
        IProcessqueue p = new Processqueue();
        //IFedoraCommunication c = new FedoraCommunication();
        FedoraAdministration fedoraAdministration = new FedoraAdministration();
        ExecutorService pool = Executors.newFixedThreadPool( 1 );
        Indexer indexer = new Indexer( compass, e, p, fedoraAdministration, pool );

        System.out.println( "--------------------" );
        for ( String j : jobs )
        {
            try
            {
                DatadockJob datadockJob = new DatadockJob( new File( j ).toURI(), submitter, format, "Mock_fedoraPID" );
                indexer.index( datadockJob );
                System.out.println( String.format( "indexed: %s", j ) );
            }
            catch ( ExecutionException ee )
            {
                // getting exception from thread
                Throwable cause = ee.getCause();

                System.err.println( String.format( "Exception Caught: '%s'\n'%s'", cause.getClass() , cause.getMessage() ) );
                StackTraceElement[] trace = cause.getStackTrace();
                for ( int i = 0; i < trace.length; i++ )
                {
                    System.err.println( trace[i].toString() );
                }
            }
            catch ( Exception exc )
            {
                System.err.println( "Caught error during indexing: " + exc.getMessage() );
                exc.printStackTrace();
                System.exit( 1 );
            }
        }
        System.out.println( "--------------------" );
        System.out.println( "Indexing done" );
        System.exit( 0 );
    }

    /**
     * Returns the files recursively found in root directory
     *
     * @param root. The root directory to start from
     *
     * @return a ArrayList with the names af the found files
     *
     * @throws FileNotFoundException if the root is not a directory.
     */
    private static ArrayList<String> listFiles( File root ) throws FileNotFoundException
    {
        ArrayList<String> jobs = new ArrayList<String>();
        if ( ! root.isDirectory() )
        {
            throw new FileNotFoundException( String.format( "%s is not i directory", root ) );
        }
        File[] files = root.listFiles();
        for ( File f: files )
        {
            if ( f.isFile() )
            {
                jobs.add( f.toString() );
            }
            else
            {
                jobs.addAll( listFiles( f ) );
            }
        }
        return jobs;
    }

    /**
     *
     */
    private static Compass buildCompass( URL mappingFile, String indexDir){
        CompassConfiguration conf = new CompassConfiguration()
            .addURL( mappingFile )
            .setSetting( CompassEnvironment.CONNECTION, indexDir )
            .setSetting( CompassEnvironment.Converter.TYPE, "org.compass.core.converter.mapping.xsem.XmlContentMappingConverter" )
            .setSetting( CompassEnvironment.Converter.XmlContent.TYPE, "org.compass.core.xml.dom4j.converter.SAXReaderXmlContentConverter" );

        Compass compass = conf.buildCompass();
        return compass;
    }


    private static String usage()
    {
        String usage = "usage:\n\n";
        usage += " java -jar -Dsubmitter=[submitter] -Dformat=[format] -Dmapping=[mapping] -Dindex=[index] OpenSearch_TESTINDEXER [index targets]\n\n";
        usage += " [submitter]      The submitter (used to find the right mapping in the mappingfile)\n";
        usage += " [format]         The format (used to find the right mapping in the mappingfile)\n";
        usage += " [mapping]        The mappingfile to use\n";
        usage += " [index]          The index directory to write the index to\n";
        usage += " [index targets]  The files or directorys to index\n";
        return usage;
    }
}



