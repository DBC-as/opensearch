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

/**
 * \file FedoraMain.java
 * \brief
 */


package dk.dbc.opensearch.common.fedora;


import dk.dbc.opensearch.common.config.FileSystemConfig;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import fedora.server.types.gen.Datastream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;


/**
 * The Main method of the datadock. It secures all necessary
 * resources for the program, starts the datadockManager and then
 * closes stdin and stdout thus closing connection to the console.
 *
 * It also adds a shutdown hook to the JVM so orderly shutdown is
 * accompleshed when the process is killed.
 */
public class FedoraMain
{
    static Logger log = Logger.getLogger( FedoraMain.class );


    public FedoraMain() {}

    private static String action = "";
    private static String textFile = "";
    private static String harvestCatalog = "";

    private static final String purge = "-purge";
    private static final String retrieve = "-retrieve";
    private static final String usage = "\n\tUsage: $ java -jar dist/OpenSearch_FEDORA.jar -[retrieve|purge] file_name harvest_katalog\n" +
                                        "\tEx:    $ java -jar dist/OpenSearch_FEDORA.jar -retrieve sanitize.txt HarvestAgain\n" +
                                        "\tFile format for file_name: work:xxx submitter:pid. E.g. \"work:1 710100:097838 710100:895623 ...\"\n";

    /**
     * The datadocks main method.
     * Starts the datadock and starts the datadockManager.
     */
    static public void main( String[] args ) throws Throwable
    {
        log.trace( "FedoraMain main called" );

        FedoraObjectRepository fo = new FedoraObjectRepository();
        
        testArgs( "test", args );

        if ( action.equals( purge ) )
        {
            testArgs( purge, args );
            
            try
            {
                BufferedReader input =  new BufferedReader( new FileReader( textFile ) );
                try
                {
                    String line = null;
                    while ( ( line = input.readLine()) != null )
                    {
                        String[] work_mani = line.split( " " );
                        String pid = work_mani[ 0 ];
                        for ( int i = 1; i < work_mani.length; i++ )
                        {
                            String predicate = "http://oss.dbc.dk/rdf/dkbib#hasManifestation";
                            String object = work_mani[i];
                            String dataType = null;
                            fo.purgeRelationship( pid, predicate, object, dataType );
                        }
                    }
                }
                finally
                {
                    input.close();
                }
            }
            catch ( IOException ioex )
            {
                ioex.printStackTrace();
            }
        }
        else if ( action.equals( retrieve ) )
        {
            testArgs( retrieve, args );

            try
            {
                String path = FileSystemConfig.getTrunkPath();
                BufferedReader input =  new BufferedReader( new FileReader( textFile ) );
                try
                {
                    String line = null;
                    int j = 1;
                    while ( ( line = input.readLine()) != null )
                    {
                        String[] work_mani = line.split( " " );
                        
                        for ( int i = 1; i < work_mani.length; i++ )
                        {                            
                            String pid = work_mani[i];
                            String submitter = pid.split( ":" )[0];
                            Datastream ds = fo.getDatastream( pid, "originalData.0" );
                            String format = ds.getLabel();

                            String harvestPath = path + harvestCatalog + "/" + submitter + "/" + format;
                            File harvestDir = new File( harvestPath );
                            harvestDir.mkdirs();
                            String xmlFile = harvestDir + "/" + j + ".xml";
                            File file = new File( xmlFile );
                            FileOutputStream fos = new FileOutputStream( file );
                            
                            CargoContainer cc = fo.getObject( pid );
                            CargoObject co = cc.getCargoObject( DataStreamType.OriginalData );
                            byte[] bytes = co.getBytes();
                            fos.write( bytes );

                            j++;
                        }
                    }
                }
                finally
                {
                    input.close();
                }
            }
            catch ( IOException ioex )
            {
                ioex.printStackTrace();
            }
        }
    }


    private static void testArgs( String actionArg, String[] args )
    {
        if ( actionArg.equals( "test" ) )
        {
            try
            {
                action = (String)args[0];
            }
            catch ( IndexOutOfBoundsException iob )
            {
                System.out.println( usage );
                System.exit( 0 );
            }
        }
        else if ( action.equals( "-purge" ) )
        {
            try
            {
                action = (String)args[0];
                textFile = (String)args[1];
            }
            catch ( IndexOutOfBoundsException iob )
            {
                System.out.println( usage );
                System.exit( 0 );
            }
        }
        else if ( action.equals( "-retrieve" ) )
        {
            try
            {
                action = (String)args[0];
                textFile = (String)args[1];
                harvestCatalog = (String)args[2];
            }
            catch ( IndexOutOfBoundsException iob )
            {
                System.out.println( usage );
                System.exit( 0 );
            }
        }
    }
}
