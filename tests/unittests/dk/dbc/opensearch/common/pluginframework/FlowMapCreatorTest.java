package dk.dbc.opensearch.common.pluginframework;
 
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.types.InputPair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.FileOutputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;



import static org.junit.Assert.*;
import org.junit.*;

//import mockit.Mock;
//import mockit.MockClass;
//import mockit.Mocked;
//import mockit.Mockit;

public class FlowMapCreatorTest
{

    FlowMapCreator fmc;
    String path;
    String xsdPath;
    Map<String, List<PluginTask>> flowMap;

    String format1 = "format1";
    String format2 = "format2";
    String submitter1 = "submitter1";
    String submitter2 = "submitter2";
    String pluginClassName1 = "pluginClassName1";
    String pluginClassName2 = "pluginClassName2";
    String pluginClassName3 = "pluginClassName3";
    String argName1 = "argName1";
    String argName2 = "argName2";
    String argValue1 = "argValue1";
    String argValue2 = "argValue2";
    String scriptName1 = "scriptName1";
    String scriptName2 = "scriptName2";
    String scriptName3 = "scriptName3";

    @Before public void setUp() throws Exception
    {
        File workFlowFile = FileHandler.getFile( "workFlow.xml" );
        workFlowFile.deleteOnExit();
        
        //create the xml file to build the map from
        createWorkFlowFile( workFlowFile );
        path = workFlowFile.getAbsolutePath();
    
        File XSDFile = FileHandler.getFile( "config/workflows.xsd" );
        xsdPath = XSDFile.getAbsolutePath();
    }

    @After public void tearDown() throws Exception
    {
    }

    /**
     * happy path constructor test that checks that there is a file to 
     * build the map from
     */
    @Test public void constructorTest() throws Exception
    {
        fmc = new FlowMapCreator( path, xsdPath );
    }

    @Test public void createMapTest() throws Exception
    {
        fmc = new FlowMapCreator( path, xsdPath );
        flowMap = fmc.createMap();
        assertTrue( validateMap1( flowMap ) );
    }

    /**
     * Helper methods
     */

    /**
     * method that validates that a map contains 2 elements, that the first has length 2, the second length 1
     * that the first elements first member has scriptname value "scriptName"  
     * further more it checks that the first PluginTask has an argList and that the first members first value 
     * equals argName1 and the second argValue1
     */
    private boolean validateMap1( Map<String, List<PluginTask>> flowMap )
    {
        //  System.out.println( String.format("flowmap size: %s", flowMap.size() ) );
        //System.out.println( String.format( "size of firstElement: %s", flowMap.get( format1 + submitter1 ).size() ) );
        //System.out.println( String.format( "scriptname of 1 element: %s", flowMap.get( format1 + submitter1 ).get( 0 ).getScriptName() ) );
        
        if(! ( flowMap.size() == 2 ) )
        {
            return false;
        }
        if(! ( flowMap.get( format1 + submitter1 ).size() == 2 ) )
        {
            return false;
        }

        PluginTask task0 = flowMap.get( format1 + submitter1 ).get( 0 );

        if(! ( task0.getScriptName().equals( "scriptName1" ) ) )
        {
            return false;
        }

        InputPair<String, String> argPair0 = task0.getArgList().get( 0 );

        if(! ( argPair0.getFirst().equals( argName1 ) && argPair0.getSecond().equals( argValue1 ) ) )
        {
            return false;
        }


        return true;
    }


    /**
     * Creates a workflow file for test purposes in the specified file 
     */
    private void createWorkFlowFile( File workFlowFile ) throws XMLStreamException, FileNotFoundException
    {
        //Create an outputfactory
        XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlw;
        OutputStream out = (OutputStream)new FileOutputStream( workFlowFile );

        try
        {
            xmlw = xmlof.createXMLStreamWriter( out );

            xmlw.writeStartDocument("UTF-8", "1.0");
            
            xmlw.writeStartElement( "workflows" );
            xmlw.writeDefaultNamespace( "info:opensearch.dbc.dk#" );
            xmlw.writeStartElement( "workflow" );
            xmlw.writeAttribute( "format", format1 );
            xmlw.writeAttribute( "submitter", submitter1 );
            
            xmlw.writeStartElement( "plugin" );
            xmlw.writeAttribute( "class", pluginClassName1 );
            xmlw.writeStartElement( "args" );
            xmlw.writeStartElement( "arg" );
            xmlw.writeAttribute( "name", argName1 );
            xmlw.writeAttribute( "value", argValue1);
            xmlw.writeEndElement();//ends arg
            xmlw.writeEndElement();//ends args
            xmlw.writeStartElement( "script" );
            xmlw.writeAttribute( "name", scriptName1 );
            xmlw.writeEndElement();//ends script
            xmlw.writeEndElement();//ends 1st plugin 
            
            xmlw.writeStartElement( "plugin" );//start plugin 2
            xmlw.writeAttribute( "class", pluginClassName2 );
            xmlw.writeEmptyElement( "args" );
            xmlw.writeStartElement( "script" );
            xmlw.writeAttribute( "name", scriptName2 );
            xmlw.writeEndElement();//ends script
            xmlw.writeEndElement();//ends 2nd plugin 
            
            xmlw.writeEndElement();//ends 1st workflow 
            
            xmlw.writeStartElement( "workflow" );
            xmlw.writeAttribute( "format", format2 );
            xmlw.writeAttribute( "submitter", submitter2 );
            
            xmlw.writeStartElement( "plugin" );
            xmlw.writeAttribute( "class", pluginClassName3 );
            xmlw.writeStartElement( "args" );
            xmlw.writeStartElement( "arg" );
            xmlw.writeAttribute( "name", argName2 );
            xmlw.writeAttribute( "value", argValue2 );
            xmlw.writeEndElement();//ends arg
            xmlw.writeEndElement();//ends args
            xmlw.writeStartElement( "script" );
            xmlw.writeAttribute( "name", scriptName3 );
            xmlw.writeEndElement();//ends script
            xmlw.writeEndElement();//ends plugin
            
            xmlw.writeEndElement();//ends 2nd workflow
            
            xmlw.writeEndElement();//ends workflows
            xmlw.writeEndDocument();
            xmlw.flush();

            //System.out.println( String.format("The workflow testfile: %s", out ) );
        }
        catch( XMLStreamException xse )
        {
            throw xse;
        }

    }
}