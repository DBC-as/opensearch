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


//import dk.dbc.opensearch.common.helpers.Log4jConfiguration;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.common.types.TargetFields;

import fedora.server.types.gen.Condition;
import fedora.server.types.gen.ComparisonOperator;
import fedora.server.types.gen.Datastream;
import fedora.server.types.gen.FieldSearchQuery;
import fedora.server.types.gen.FieldSearchResult;
import fedora.server.types.gen.ObjectFields;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.configuration.ConfigurationException;
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
    //static Logger log = Logger.getLogger( FedoraMain.class );


    public FedoraMain() {}
    

    private static FedoraHandle fedoraHandle;
    
    private static String action = "";
    private static String textFile = "";
    private static String harvestCatalog = "";

    private static final String purge = "-purge";
    private static final String retrieve = "-retrieve";
    private static final String getObjects = "-getObjects";
    private static final String deleteSubmitter = "-deleteSubmitter";
    private static final String deleteWork = "-deleteWork";

    private static ComparisonOperator gt = ComparisonOperator.fromString( "gt" );
    private static ComparisonOperator lt = ComparisonOperator.fromString( "lt" );
    private static ComparisonOperator eq = ComparisonOperator.fromString( "eq" );

    private static String pidBegin = "";
    private static String pidEnd = "";
    private static String cDateBegin = "";
    private static String cDateEnd = "";
    private static String mDateBegin = "";
    private static String mDateEnd = "";
    private static String submitter = "";
    private static String format = "";
    private static long noOfObjects = 0;
    private static int condLen = 0;
    private static int resultLen = 0;

    private static final String usage = "\n\tUsage: $ java -jar dist/OpenSearch_FEDORA.jar -[retrieve|purge] file_name harvest_katalog\n" +
                                        "\tEx:    $ java -jar dist/OpenSearch_FEDORA.jar -retrieve sanitize.txt HarvestAgain\n" +
                                        "\tEx:    $ java -jar dist/OpenSearch_FEDORA.jar -purge sanitize.txt\n" +
                                        "\tEx:    $ java -jar dist/OpenSearch_FEDORA.jar -deleteSubmitter dbc\n" +
                                        "\tEx:    $ java -jar dist/OpenSearch_FEDORA.jar -deleteWork sanitize.txt\n" +
                                        "\tEx:    $ java -jar dist/OpenSearch_FEDORA.jar -getObjects -h harvest-katalog [-cDate YYYY-mm-dd mDate YYYY-mm-dd -format <format> -submitter <submitter>] -n <No to retrieve>\n" +
                                        "\tEx:                                           -getObjects -h Harvest -cDate 2010-01-29[--2010-02-29] -mDate 2010-01-29[--2010-02-29] -format katalog -submitter 775100 -n 10\n" +
                                        "\tFile format for file_name: work:xxx submitter:pid. E.g. \"work:1 710100:097838 710100:895623 ...\"\n";

    /**
     * The datadocks main method.
     * Starts the datadock and starts the datadockManager.
     */
    public static void main( String[] args ) throws Throwable
    {
        //Log4jConfiguration.configure( "log4j_fedora.xml" );
        //log.trace( "FedoraMain main called" );

        FedoraObjectRepository fo = new FedoraObjectRepository();
        fedoraHandle = new FedoraHandle();

        testArgs( "test", args );
        
        // PURGE
        if ( action.equals( purge ) )
        {
            purge( args, fo );
        }
        // RETRIEVE
        else if ( action.equals( retrieve ) )
        {
            retrieve( args, fo );
        }
        // GET OBJECTS
        else if ( action.equals( getObjects ) )
        {
            getObjects( args, fo );
        }
        // DELETE SUBMITTER
        else if ( action.equals( deleteSubmitter ) )
        {
            submitter = args[1];
            for ( int i = 0; i < 600; i++ )
            {
                deleteSubmitter( submitter );
                FedoraMain fm = new FedoraMain();
                fm.thisSleep( 10000 );
            }
        }
        // DELETE WORK
        else if ( action.equals( deleteWork ) )
        {
            deleteWork( args );
        }
    }

    private void thisSleep( long time ) throws InterruptedException
    {
        Thread t = Thread.currentThread ( );
        long timer = System.currentTimeMillis();
        t.sleep( time );
        timer = System.currentTimeMillis() - timer;
        System.out.println( timer );
    }


    private static void purge( String[] args, FedoraObjectRepository fo ) throws Throwable
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


    private static void retrieve( String[] args, FedoraObjectRepository fo ) throws Throwable
    {
        testArgs( retrieve, args );

        try
        {
            String path = ""; // FileSystemConfig.getTrunkPath();
            File cwd = new File( "." );
            try
            {
                path = cwd.getCanonicalPath() + "/";
            }
            catch ( Exception ex )
            {
                ex.printStackTrace();
            }

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
                        submitter = pid.split( ":" )[0];
                        Datastream ds = fo.getDatastream( pid, "originalData.0" );
                        format = ds.getLabel();

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
                        fos.close();

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


    private static void getObjects( String[] args, FedoraObjectRepository fo )
    {
        testArgs( getObjects, args );

        String path = "";
        File cwd = new File( "." );
        try
        {
            path = cwd.getCanonicalPath() + "/";
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }

        //log.debug( String.format( "getObjects path: %s", path ) );

        // DO NOT SET ANY GLOBAL VARIABLES AFTER CALL TO setVariables!!!
        setVariables( args );
        // DO NOT SET ANY GLOBAL VARIABLES AFTER CALL TO setVariables!!!

        printSearchVariables();

        if ( !submitter.isEmpty() && !format.isEmpty() && pidBegin.isEmpty() && pidEnd.isEmpty() )
        {
            Condition[] cond = new Condition[ condLen ];
            String[] resultFields = new String[ resultLen ];
            cond = setConditions( cond, resultFields );

            /*for ( int i = 0; i < condLen; i++ )
            {
                System.out.println(cond[ i ].getValue() );
                System.out.println(resultFields[ i ] );
            }*/

            NonNegativeInteger max = new NonNegativeInteger( Integer.toString( 1000 ) );
            FieldSearchQuery fsq = new FieldSearchQuery( cond, null );
            try
            {
                FieldSearchResult result = fedoraHandle.findObjects( resultFields, max, fsq );
                ObjectFields[] objectFields = result.getResultList();
                int ofLength = objectFields.length;
                //log.debug( String.format( "No of objectFields lines %s", objectFields.length ) );
                int j = 1;
                for( int i = 0; i < ofLength; i++ )
                {
                    String pid = objectFields[i].getPid();
                    try
                    {
                        if ( noOfObjects != 0 && j > noOfObjects )
                        {
                            break;
                        }
                        
                        if ( writeDatastream( fo, pid, path, j ) )
                        {
                            j++;
                        }
                    }
                    catch ( ObjectRepositoryException orex )
                    {
                        //orex.printStackTrace();
                    }
                }
            }
            catch ( ConfigurationException cex )
            {
                cex.printStackTrace();
            }
            catch ( MalformedURLException muex )
            {
                muex.printStackTrace();
            }
            catch ( IOException ioex )
            {
                ioex.printStackTrace();
            }
            catch ( ServiceException sex )
            {
                sex.printStackTrace();
            }
        }
        else if ( !pidBegin.isEmpty() && !pidEnd.isEmpty() )
        {
            String[] firstPid = pidBegin.split( ":" );

            String[] lastPid = pidEnd.split( ":" );

            if ( submitter.isEmpty() )
            {
                submitter = firstPid[0];
            }

            String firstPost = firstPid[1].toString();
            String lastPost = lastPid[1].toString();

            String currPost = firstPost;
            int j = 1;
            while ( lessThanOrEqualTo( currPost, lastPost ) )
            {
                String currPid = submitter + ":" + currPost;
                try
                {
                    if ( noOfObjects != 0 && j > noOfObjects )
                    {
                        break;
                    }

                    if ( writeDatastream( fo, currPid, path, j ) )
                    {
                        j++;
                    }
                }
                catch( ObjectRepositoryException orex )
                {
                    //orex.printStackTrace();
                }

                currPost = addOne( currPost );
            }
        }
    }


    private static boolean writeDatastream( FedoraObjectRepository fo, String currPid, String path, int fileNo ) throws ObjectRepositoryException
    {
        Datastream ds = fo.getDatastream( currPid, "originalData.0" );

        String postFormat = ds.getLabel();
        
        String harvestPath = "";
        if ( !format.isEmpty() && format.equals( postFormat ) )
        {
            harvestPath = path + harvestCatalog + "/" + submitter + "/" + format;
        }
        else if ( format.isEmpty() )
        {
            harvestPath = path + harvestCatalog + "/" + submitter + "/" + postFormat;
        }

        File harvestDir = new File( harvestPath );
        harvestDir.mkdirs();
        harvestDir.mkdirs();
        String xmlFile = harvestDir + "/" + fileNo + ".xml";
        File file = new File( xmlFile );
        try
        {
            FileOutputStream fos = new FileOutputStream( file );
            CargoContainer cc = fo.getObject( currPid );
            CargoObject co = cc.getCargoObject( DataStreamType.OriginalData );
            byte[] bytes = co.getBytes();
            fos.write( bytes );
            fos.close();
        }
        catch ( FileNotFoundException fnfex )
        {
            fnfex.printStackTrace();
        }
        catch ( IOException ioex )
        {
            ioex.printStackTrace();
        }

        return true;
    }


    private static String addOne( String fedoraPid)
    {
        String leadingZeros = fedoraPid.replaceAll("[1-9]+0*", "");
        int intValue = new Integer( fedoraPid );
        intValue++;
        fedoraPid = leadingZeros + new Integer( intValue ).toString();
        
        return fedoraPid;
    }

    private static boolean lessThanOrEqualTo( String first, String second )
    {
        int firstInt = new Integer( first );
        int secondInt = new Integer( second );
        if ( firstInt <= secondInt )
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    private static Condition[] setConditions( Condition[] cond, String[] resultFields )
    {
        int condPos = 0;
        int resPos = 0;

        resultFields[ resPos ] = FedoraObjectFields.PID.fieldname();
        resPos++;
        
        if ( !cDateBegin.isEmpty() || !cDateEnd.isEmpty() )
        {
            resultFields[ resPos ] = FedoraObjectFields.CDATE.fieldname();
            resPos++;
        }
        
        if ( !mDateBegin.isEmpty() || !mDateEnd.isEmpty() )
        {
            resultFields[ resPos ] = FedoraObjectFields.MDATE.fieldname();
            resPos++;
        }

        if ( !format.isEmpty() )
        {
            cond[ condPos ] = new Condition( FedoraObjectFields.LABEL.fieldname(), eq, format );
            condPos++;
            resultFields[ resPos ] = FedoraObjectFields.LABEL.fieldname();
            resPos++;
        }

        if ( !submitter.isEmpty() )
        {
            cond[ condPos ] = new Condition( FedoraObjectFields.OWNERID.fieldname(), eq, submitter );
            condPos++;
            resultFields[ resPos ] = FedoraObjectFields.CDATE.fieldname();
            resPos++;
        }

        return cond;
    }


    private static void printSearchVariables()
    {
        System.out.println( "\nSearching fedora repository specified in config/config.txt with values:");
        System.out.println( "\t Storing files in harvest catalog: " + harvestCatalog );
        System.out.println( "\t Pid interval:                     " + pidBegin + "-" + pidEnd );
        System.out.println( "\t Created date interval:            " + cDateBegin + "-" + cDateEnd );
        System.out.println( "\t Last modified date interval:      " + mDateBegin + "-" + mDateEnd );
        System.out.println( "\t Submitter:                        " + submitter );
        System.out.println( "\t Format:                           " + format + "\n" );
    }
    

    private static void setVariables( String[] args )
    {
        boolean pidIsSet = false;
        for ( int i = 1; i < args.length; i++ )
        {
            String arg = args[i];

            if ( arg.equals( "-pid" ) )
            {
                String pid = args[ ++i ];
                String[] pids = pid.split( "--" );
                pidBegin = pids[ 0 ];
                //condLen++;
                pidEnd = pids[ 1 ];
                //condLen++;
                resultLen++;
                pidIsSet = true;
            }
            else if ( arg.equals( "-h" ) )
            {
                harvestCatalog = args[ ++i ];
            }
            else if ( arg.equals( "-cDate" ) )
            {
                String cDates = args[ ++i ];
                String[] dates = cDates.split( "--" );
                try
                {
                    cDateBegin = dates[ 0 ];
                    //condLen++;
                    cDateEnd = dates[ 1 ];
                    //condLen++;
                    resultLen++;
                }
                catch( IndexOutOfBoundsException ioobex )
                {
                    // do nothing!
                }
            }
            else if ( arg.equals( "-mDate" ) )
            {
                String mDates = args[ ++i ];
                String[] dates = mDates.split( "--" );
                try
                {
                    mDateBegin = dates[ 0 ];
                    //condLen++;
                    mDateEnd = dates[ 1 ];
                    //condLen++;
                    resultLen++;
                }
                catch( IndexOutOfBoundsException ioobex )
                {
                    // do nothing!
                }
            }
            else if ( arg.equals( "-submitter" ) )
            {
                submitter = args[ ++i ];
                condLen++;
                resultLen++;
            }
            else if ( arg.equals( "-format" ) )
            {
                format = args[ ++i ];
                condLen++;
                resultLen++;
            }
            else if ( arg.equals( "-n" ) )
            {
                noOfObjects = new Long( args[ ++i ] );
            }
        }

        if ( !pidIsSet )
        {
            resultLen++;
        }
    }

    
    private static int setDateConditions( Condition[] cond, String[] resultFields, int position, String dateBegin, String dateEnd )
    {
        if ( !dateBegin.isEmpty() && !dateEnd.isEmpty() )
        {
            cond[ position ] = new Condition( FedoraObjectFields.CDATE.fieldname(), gt, dateBegin );
            resultFields[ position ] = FedoraObjectFields.CDATE.fieldname();
            cond[ ++position ] = new Condition( FedoraObjectFields.CDATE.fieldname(), lt, dateEnd );
            position++;
        }
        else if ( !dateBegin.isEmpty() && dateEnd.isEmpty() )
        {
            cond[ position ] = new Condition( FedoraObjectFields.CDATE.fieldname(), gt, dateBegin );
            resultFields[ position ] = FedoraObjectFields.CDATE.fieldname();
            position++;
        }
        else if ( dateBegin.isEmpty() && dateEnd.isEmpty() )
        {
            // do nothing!
        }
        else
        {
            System.out.println( "Something is wrong with your date interval!");
            System.exit( 0 );
        }

        return position;
    }


    private static void deleteSubmitter( String submitter ) throws ObjectRepositoryException
    {
        IObjectRepository objectRepository;
        try
        {
            objectRepository = new FedoraObjectRepository();
        }
        catch( ObjectRepositoryException ore )
        {
            System.out.println( "Could not initialize objectRepository" );
            throw new ObjectRepositoryException( "Could not initialize FedoraObjectRepository (is fedora running?)" );
        }

        System.out.println( "*** kalder deleteSubmitter ***" );
        String[] labels = { "pg" }; //, "anmeldelser", "anmeld", "forfatterw", "matvurd", "katalog", "danmarcxchange", "ebrary", "ebsco", "artikler", "dr_forfatteratlas", "dr_atlas", "dr_bonanza", "materialevurderinger", "docbook_forfatterweb", "docbook_faktalink", "format" };
        List< InputPair< TargetFields, String > > resultSearchFields = new ArrayList< InputPair< TargetFields, String > >();
        int maximumResult = 100;
        //for ( int i = 0; i < 10; i++ )
        //{
            TargetFields targetLabel = FedoraObjectFields.LABEL;
            InputPair< TargetFields, String > pair = new InputPair< TargetFields, String >( targetLabel, labels[0] );
            resultSearchFields.add( pair );
            List< String > pids = objectRepository.getIdentifiersUnqualified( resultSearchFields, maximumResult );
            //System.out.println( "pids.length: " + pids.size() );

            for ( String pid : pids )
            {
                if ( submitter != null && pid.startsWith( submitter ) )
                {
                    System.out.println( String.format( "Deleting object pid: %s with label %s", pid, labels[0] ) );
                    objectRepository.deleteObject( pid, "Deleting from FedoraAuxiliaryMain" );
                }
            }

            resultSearchFields.clear();
        //}
    }


    private static void deleteWork( String[] args ) throws ObjectRepositoryException
    {
        testArgs( deleteWork, args );
        
        IObjectRepository objectRepository;
        try
        {
            objectRepository = new FedoraObjectRepository();
        }
        catch( ObjectRepositoryException ore )
        {
            System.out.println( "Could not initialize objectRepository" );
            throw new ObjectRepositoryException( "Could not initialize FedoraObjectRepository (is fedora running?)" );
        }

        try
        {
            BufferedReader input =  new BufferedReader( new FileReader( textFile ) );
            try
            {
                String line = null;
                while ( ( line = input.readLine()) != null )
                {
                    String[] work_mani = line.split( " " );
                    String pid = work_mani[ 1 ];
                    for ( int i = 1; i < work_mani.length; i++ )
                    {
                        if ( pid.startsWith( "work" ) )
                        {
                            objectRepository.deleteObject( pid, "Deleting from FedoraAuxiliaryMain" );
                        }
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
        else if ( action.equals( "-purge" ) || action.equals( "-deleteWork" ) )
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
        else if ( action.equals( getObjects ) )
        {
            try
            {
                action = (String)args[0];
                //harvestCatalog = (String)args[1];
            }
            catch ( IndexOutOfBoundsException iob )
            {
                System.out.println( "test" + usage );
                System.exit( 0 );
            }
        }
        else
        {
            System.out.println( "usage: " + usage );
            System.exit( 0 );
        }
    }
}
