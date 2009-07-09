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


package dk.dbc.opensearch.tools.indexchecker;


import dk.dbc.opensearch.common.db.IProcessqueue;
import dk.dbc.opensearch.common.fedora.FedoraAdministration;
import dk.dbc.opensearch.common.fedora.IFedoraCommunication;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.statistics.IEstimate;
import dk.dbc.opensearch.components.datadock.DatadockJob;
import dk.dbc.opensearch.tools.readindex.ReadIndex;
import dk.dbc.opensearch.tools.testindexer.Estimate;
import dk.dbc.opensearch.tools.testindexer.FedoraCommunication;
import dk.dbc.opensearch.tools.testindexer.Indexer;
import dk.dbc.opensearch.tools.testindexer.Processqueue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.InterruptedException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.configuration.ConfigurationException;
import org.compass.core.Compass;
import org.xml.sax.SAXException;


/**
 * The main class for using the IndexChecker
 */
public class IndexCheckerMain
{
    private static IndexChecker indexChecker;
    

    /**
     * The main method of the IndexChecker application
     */
    static public void main( String[] args )
    {
        // Validating properties
        String mode = System.getProperty( "mode" );
        if ( mode == null )
        {
            mode = "check";
        }
        if ( ! mode.equals( "check" ) && ! mode.equals( "create" ) )
        {
            System.err.println( "mode must be either check or create. if mode is not set, it defaults to check" );
            System.err.println( usage() );
            System.exit( 1 );
        }

        System.out.println( String.format( "Starting IndexChecker in mode=%s", mode ) );

        // do stuff
        try
        {
            indexChecker =  new IndexChecker();

            if ( args.length == 0 )
            {
                System.out.println( "Nothing to do !" );
            }

            for ( String arg: args )
            {
                File jobFolder = new File( arg );
                if ( mode.equals( "check" ) )
                {
                    boolean passed = indexChecker.runTest( jobFolder );
                    if ( passed )
                    {
                        System.out.println( String.format( "%s     OK", jobFolder.getAbsolutePath() ) );
                    }
                    else
                    {
                        System.out.println( String.format( "%s     FAILED", jobFolder.getAbsolutePath() ) );
                    }
                }
                else  // mode.equals( "create" )
                {
                    if ( ! jobFolder.exists() )
                    {
                        System.err.println( String.format( "Cannot find : %s, directory or file does not exist", arg ) );
                        System.exit( 1 );
                    }
                    create( jobFolder );
                }
            }
        }

        catch ( Exception e )
        {
            System.err.println( "Caught error in IndexCheckerMain: " + e.getMessage() );
            e.printStackTrace();
            System.exit( 1 );
        }
        System.exit( 0 );
    }

    
    /**
     * Creates a new result.out file in the specified jobFolder
     *
     * @param orgFolder the Folder to read jobfiles from, and write the result.out file to.
     *
     * @throws ClassNotFoundException
     * @throws ConfigurationException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws PluginResolverException
     * @throws SAXException
     * @throws ServiceException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws URISyntaxException
     *
     */
    private static void create( File orgFolder ) throws ClassNotFoundException, ConfigurationException, ExecutionException, InterruptedException, IOException, ParserConfigurationException, PluginResolverException, SAXException, ServiceException, TransformerConfigurationException, TransformerException, URISyntaxException
    {
        File tmpFolder = indexChecker.getTmpFolder();
        String subIndexName = indexChecker.modifyMapping( tmpFolder ); // modifying xsem file
        File xsemPath = new File( new File( tmpFolder.getAbsolutePath(), "xml.cpm.xml" ).getAbsolutePath() );

        // Configuring Compass
        Compass compass = indexChecker.buildCompass( tmpFolder.getAbsolutePath(), xsemPath );

        IEstimate e = new Estimate();
        IProcessqueue p = new Processqueue();
        //IFedoraCommunication c = new FedoraCommunication();
        FedoraAdministration fedoraAdministration = new FedoraAdministration();
        ExecutorService pool = Executors.newFixedThreadPool( 1 );
        Indexer indexer = new Indexer( compass, e, p, fedoraAdministration, pool );
        ReadIndex reader = new ReadIndex();

        // Index the jobfiles
        for ( DatadockJob job : indexChecker.getJobList( orgFolder ) )
        {
            indexer.index( job );
        }

        // Reading index result
        File indexFolder = new File( new File( tmpFolder.getAbsolutePath(), "index" ).getAbsolutePath(), subIndexName );
        String content = reader.readIndexFromFolder( indexFolder );

        File resultFile =  new File( orgFolder, "result.out" );
        if ( resultFile.exists() )
        {
            resultFile.delete();
        }

        // writing index result
        BufferedWriter out = new BufferedWriter( new FileWriter( resultFile ) );
        out.write( content );
        out.close();
    }

    
    /**
     * The help string for this application
     */
    private static String usage()
    {
        String usage = "usage:\n\n";
        usage += " java -jar -Dmode=[create, check] OpenSearch_INDEXCHEKER [index folder(s)]\nMust be called from the dist folder, for the program to find job specification files\n\n";
        usage += " [mode]          mode must be either check or create. if mode is not set, it defaults to check\n";
        usage += " [index folder]  The directory containing the index files\n";
        return usage;
    }
}
