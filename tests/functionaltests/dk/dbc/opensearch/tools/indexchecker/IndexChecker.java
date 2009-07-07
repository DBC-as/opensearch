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


import dk.dbc.opensearch.common.compass.CompassFactory;
import dk.dbc.opensearch.common.config.CompassConfig;
import dk.dbc.opensearch.common.db.IProcessqueue;
import dk.dbc.opensearch.common.fedora.IFedoraCommunication;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.statistics.IEstimate;
import dk.dbc.opensearch.components.datadock.DatadockJob;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.tools.readindex.ReadIndex;
import dk.dbc.opensearch.tools.testindexer.Estimate;
import dk.dbc.opensearch.tools.testindexer.FedoraCommunication;
import dk.dbc.opensearch.tools.testindexer.Indexer;
import dk.dbc.opensearch.tools.testindexer.Processqueue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.ClassNotFoundException;
import java.lang.IllegalAccessException;
import java.lang.InterruptedException;
import java.lang.NoSuchFieldException;
import java.lang.StringBuilder;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.configuration.ConfigurationException;
import org.compass.core.Compass;
import org.compass.core.CompassSession;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassEnvironment;
import org.compass.core.converter.mapping.xsem.XmlContentMappingConverter;
import org.compass.core.xml.dom4j.converter.SAXReaderXmlContentConverter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.util.Collections;


/**
 * Run index tests and compare the result to a predefined result
 */
public class IndexChecker
{
    public IndexChecker() {}

    /**
     * run tests on all the tesfolders in tesfolder
     * @param testFolder The folder where to look for single testfolders
     *
     * @throws ClassNotFoundException
     * @throws ConfigurationException
     * @throws ExecutionException
     * @throws FileNotFoundException
     * @throws InterruptedException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws PluginResolverException
     * @throws SAXException
     * @throws ServiceException
     * @throws URISyntaxException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     **/
    public boolean runTests( File testFolder )throws ClassNotFoundException, ConfigurationException, ExecutionException, FileNotFoundException, IllegalAccessException, InterruptedException, IOException, NoSuchFieldException, ParserConfigurationException, PluginResolverException, SAXException, ServiceException, TransformerConfigurationException, TransformerException, URISyntaxException
    {
        resetMapClasses(); // reset the jobmaps

        // Run the tests
        boolean allPassed = true;

        System.out.println( "Check test-indexes from folder:" );
        System.out.println( String.format( " %s\n", testFolder.getAbsolutePath() ) );
        File[] testFolders = testFolder.listFiles();

        int testNameLength = 0;
        for ( File f: testFolders )
        {
            if ( f.isDirectory() && f.getName().length() > testNameLength )
            {
                testNameLength = f.getName().length();
            }
        }


        for ( File f: testFolders )
        {
            if ( f.isDirectory() && ! ".svn".equals( f.getName() ) )
            {
                System.out.println("NAME: "+ f.getName() );
                
                int spaces = testNameLength + 5 - f.getName().length();
                System.out.print( String.format( " Running test: %s", f.getName() ) );

                for ( int i = 0; i < spaces ;i++ )
                {
                    System.out.print( " " );
                }

                boolean testresult = runTest( f );
                if ( testresult )
                {
                    System.out.println( "OK" );
                }
                else
                {
                    System.out.println( "FAILED" );
                    allPassed = false;
                }

            }
        }

        if ( allPassed )
        {
            System.out.println( "\nall Tests passed." );
        }
        else
        {
            System.out.println( "\nsome Tests failed." );
        }

        resetMapClasses(); // reset the jobmaps

        return allPassed;
    }

    /**
     * Runs a single test folder
     *
     * @param testFolder The folder to perform the test on
     *
     * @throws ClassNotFoundException
     * @throws ConfigurationException
     * @throws ExecutionException
     * @throws FileNotFoundException
     * @throws InterruptedException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws PluginResolverException
     * @throws SAXException
     * @throws ServiceException
     * @throws URISyntaxException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     *
     **/
    public boolean runTest( File testFolder ) throws ClassNotFoundException, ConfigurationException, ExecutionException, FileNotFoundException, InterruptedException, IOException, ParserConfigurationException, PluginResolverException, SAXException, ServiceException, TransformerConfigurationException, TransformerException, URISyntaxException
    {
        // find result.out file
        File resultFile = null;
        File[] files = testFolder.listFiles();
        for ( File f: files )
        {
            if ( f.getName().equals( "result.out" ) )
            {
                resultFile = f;
            }
        }
        if ( resultFile == null )
        {
            throw new FileNotFoundException( String.format( "Couldn't find result.out file in %s", testFolder ) );
        }

        BufferedReader rd = new BufferedReader( new InputStreamReader( FileHandler.readFile( resultFile.getAbsolutePath() ) ) );
        StringBuilder sb = new StringBuilder();
        String line;
        while (( line = rd.readLine() ) != null )
        {
            sb.append( line + "\n" );
        }

        rd.close();
        String expectedIndexString = sb.toString().trim();

        // Get jobs
        ArrayList<DatadockJob> jobs = getJobList( testFolder );

        // make temp dir
        File tmpFolder = getTmpFolder();

        // compass configuration
        String subIndex = modifyMapping( tmpFolder );

        String xsemPath = new File( tmpFolder.getAbsolutePath(), "xml.cpm.xml" ).getAbsolutePath();
        File xsemFile = new File( tmpFolder.getAbsolutePath(), "xml.cpm.xml" );
        Compass compass = buildCompass( tmpFolder.getAbsolutePath(), xsemFile );

        // setup classes needed for indexing
        IEstimate e = new Estimate();
        IProcessqueue p = new Processqueue();
        IFedoraCommunication c = new FedoraCommunication();
        ReadIndex readIndex = new ReadIndex();
        ExecutorService pool = Executors.newFixedThreadPool( 1 );

        Indexer indexer = new Indexer( compass, e, p, c, pool );

        //index the jobs
        for ( DatadockJob job: jobs )
        {
            indexer.index( job );
        }

        File indexDir = new File( new File( tmpFolder, "index" ), subIndex );

        // read the index.
        String resultString = readIndex.readIndexFromFolder( indexDir ).trim();

        // cleanup
        deleteFolder( tmpFolder );

        // check for equality
        return expectedIndexString.equals( resultString );
    }


    /**
     * Used to reset the JobMaps for the datadock and pti.  Uses
     * reflection to set the private static field initiated to false,
     * thus provoking a new initialization
     *
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private void resetMapClasses() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException
    {

        // Re-initialize the DatadockJobsMap ( is possibly set by earlier unittests )
        Class datadockJobMapClass = Class.forName( "dk.dbc.opensearch.components.datadock.DatadockJobsMap" );
        Field field = datadockJobMapClass.getDeclaredField( "initiated" );
        field.setAccessible( true );
        field.setBoolean( field, false );

        // Re-initialize the PTIJobsMap ( is possibly set by earlier unittests )
        Class ptiJobMapClass = Class.forName( "dk.dbc.opensearch.components.pti.PTIJobsMap" );
        field = ptiJobMapClass.getDeclaredField( "initiated" );
        field.setAccessible( true );
        field.setBoolean( field, false );
    }


    /**
     * creates and returns a temporary folder
     *
     * @return a temporary folder
     *
     * @throw IOException
     */
    public File getTmpFolder() throws IOException
    {
        File tmpFile = File.createTempFile( "opensearch_mappingTest", "" );
        String tempFileName = tmpFile.getAbsolutePath();
        tmpFile.delete();
        File tmpFolder = new File( tempFileName );
        tmpFolder.mkdir();
        tmpFolder.deleteOnExit();
        return tmpFolder;
    }


    /**
     * Deletes the folder recursively
     *
     * @param folder The folder to delete
     */
    private static void deleteFolder( File folder )
    {
        if ( folder.isDirectory() )
        {
            File fileList[] = folder.listFiles();
            for ( int index = 0; index < fileList.length; index++ )
            {
                File file = fileList[index];
                deleteFolder( file );
            }
        }
        folder.delete();
    }


    /**
     * Builds a arraylist of datadockjobs in root and returns it
     *
     * @param root the folder to look for jobs in
     *
     * @throws FileNotFoundException
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public ArrayList<DatadockJob> getJobList( File root ) throws FileNotFoundException, MalformedURLException, URISyntaxException
    {

        if ( ! root.isDirectory() )
        {
            throw new FileNotFoundException( String.format( "%s is not i directory", root ) );
        }

        ArrayList<DatadockJob> returnList = new ArrayList<DatadockJob>();

        File[] submitters = root.listFiles();
        for ( File s: submitters )
        {
            if ( s.isDirectory() && ! ".svn".equals( s.getName() ) )// found submitter
            {
                String submitter = s.getName();
                File[] format = s.listFiles();
                for ( File f: format )
                {
                    if ( f.isDirectory() && ! ".svn".equals( f.getName() ) )// found format
                    {
                        String form = f.getName();
                        File[] jobs = f.listFiles();
                        for ( File j: jobs )
                        {
                            if( ! j.getName().equals( ".svn" ) ){
                            String job = j.getName();
                            returnList.add( new DatadockJob( j.toURI() , submitter, form, "Mock_fedoraPID" ) );
                            }
                            }
                    }
                }
            }
        }

        Collections.sort( returnList );
        return returnList;
    }


    /**
     * Builds a compass instance.
     *
     * @param indexDir the name of the folder to put the index in
     * @param xsemPath the path to the mapping file (xml.cpm.xml)
     *
     * @throws ConfigurationException
     * @throws MalformedURLException
     */
    public static Compass buildCompass( String indexDir, File xsemPath ) throws ConfigurationException, MalformedURLException
    {
        CompassConfiguration conf = new CompassConfiguration()
            .addURL( xsemPath.toURL() )
            .setSetting( CompassEnvironment.CONNECTION, indexDir )
            .setSetting( CompassEnvironment.Converter.TYPE, "org.compass.core.converter.mapping.xsem.XmlContentMappingConverter" )
            .setSetting( CompassEnvironment.Converter.XmlContent.TYPE, "org.compass.core.xml.dom4j.converter.SAXReaderXmlContentConverter" );

        Compass compass = conf.buildCompass();

        return compass;
    }


    /**
     * reads xsem file and modifies the subindex to write
     * to. Afterwards it writes it to a file in destFolder and returns
     * the name of the subindex
     *
     * @param destFolder The folder to write the modified mapping file to
     *
     * @throws ConfigurationException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public String modifyMapping( File destFolder )throws ConfigurationException, IOException, ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerException
    {
        // read xml.cpm.xml file
        File cfgFile  = new File( CompassConfig.getXSEMPath() );

        // isolate doctype line HACK \todo: do this properly
        BufferedReader br = new BufferedReader( new FileReader( CompassConfig.getXSEMPath() ) );
        String b;
        String r = "";
        while (( b = br.readLine() ) != null )
        {
            r = r + b;
        }
        int indexOfDocTypeStart = r.indexOf( "<!DOCTYPE" );
        int indexOfDocTypeEnd = r.indexOf( ">", indexOfDocTypeStart ) + 1;
        String doctypeString = r.substring( indexOfDocTypeStart, indexOfDocTypeEnd );
        String formattedString = "";
        boolean newline = true;
        CharacterIterator it = new StringCharacterIterator( doctypeString );
        for ( char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next() )
        {
            if ( ch == "\"".toCharArray()[0] )
            {
                if ( newline )
                {
                    formattedString += "\n";
                    newline = false;
                }
                else
                {
                    newline = true;
                }
            }

            formattedString += ch;
        }
        doctypeString = formattedString;
        // HACK ends here

        //build cpm document and modify mapping
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse( cfgFile );

        String subIndex = "";
        Element root = doc.getDocumentElement();
        NodeList nodeLst = doc.getElementsByTagName( "xml-object" );

        // retrieve sub-index if any
        for ( int i = 0; i < nodeLst.getLength(); i++ )
        {
            Node node = nodeLst.item( i );
            NamedNodeMap nnm = node.getAttributes();
            for ( int j = 0; j < nnm.getLength(); j++ )
            {
                Node atrNode = nnm.item( j );
                if ( "sub-index".equals( atrNode.getNodeName() ) )
                {
                    if ( subIndex.equals( "" ) )
                    {
                        subIndex = atrNode.getNodeValue();
                    }
                }
            }
        }

        // set sub-index
        if ( subIndex.equals( "" ) )
        {
            subIndex = "opensearch_index";
        }

        for ( int i = 0; i < nodeLst.getLength(); i++ )
        {
            boolean subIndexNotFound = true;
            Node node = nodeLst.item( i );
            NamedNodeMap nnm = node.getAttributes();

            for ( int j = 0; j < nnm.getLength(); j++ )
            {
                Node atrNode = nnm.item( j );
                if ( "sub-index".equals( atrNode.getNodeName() ) )
                {
                    atrNode.setNodeValue( subIndex );
                    subIndexNotFound = false;
                }
            }

            if ( subIndexNotFound ) // no sub-index found, inserting one
            {
                (( Element ) node ).setAttribute( "sub_index", subIndex );
            }
        }

        // Write modified mapping file to destination folder
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer xform = tfactory.newTransformer();
        Source src = new DOMSource( doc );
        StringWriter writer = new StringWriter();
        Result result = new javax.xml.transform.stream.StreamResult( writer );
        xform.transform( src, result );

        String modifiedmapping = writer.toString();

        // isolate doctype line HACK \todo: do this properly
        int split = modifiedmapping.indexOf( ">" ) + 1;
        String head = modifiedmapping.substring( 0, split );
        String tail = modifiedmapping.substring( split, modifiedmapping.length() );
        modifiedmapping = head + doctypeString + tail;
        // hack end here

        // write file
        File cpmPath = new File( destFolder.getAbsolutePath(), "xml.cpm.xml" );
        FileWriter out = new FileWriter( cpmPath );
        out.write( modifiedmapping );
        out.close();

        return subIndex;
    }
}
