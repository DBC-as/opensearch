/**
 * \file PluginFinderTest.java 
 * \brief UnitTest for PluginFinder
 * \package tests
 */

package dk.dbc.opensearch.common.pluginframework.tests;

import dk.dbc.opensearch.common.pluginframework.PluginFinder;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.os.XmlFileFilter;

import static org.junit.Assert.*;
import org.junit.*;
import static org.easymock.classextension.EasyMock.*;

import java.io.FilenameFilter;
import java.util.Vector;
import java.util.Iterator;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;
import java.io.IOException;

/**
 *
 */
public class PluginFinderTest {
    PluginFinder pluginFinder;
    FileHandler mockFH;
    File mockFile;
    Vector <String> mockVector = null;
    Iterator mockIterator;
    DocumentBuilder mockDocBuilder;
    Document mockDocument;
    Element mockElement;
    /**
     *
     */
    @Before public void setUp() {
        mockFH = createMock( FileHandler.class );
        mockFile = createMock( File.class );
        mockVector = createMock( Vector.class );
        mockIterator = createMock( Iterator.class );
        mockDocBuilder = createMock( DocumentBuilder.class );
        mockDocument = createMock( Document.class );
        mockElement = createMock( Element.class);
    }

    /**
     *
     */
    @After public void tearDown() {

    }

    /**
     * Test that the finder is constructed and that the classNameMap
     * is build correctly. The updatePluginClassMap method is
     * tested indirectly, since it is called in the constructor
     * So there is no seperate test for that methods general functionality.
     */
    @Test public void constructorTest() throws SAXException, IOException, NullPointerException {

        /** 1 setup
         *
         */
        String path = "";

        /** 2 expectations
         *
         */
        //in the updatePluginClassMap method
        expect( mockFH.getFileList( isA( String.class ), isA( FilenameFilter[].class ), true ) ).andReturn( mockVector );
        expect( mockVector.iterator() ).andReturn( mockIterator );

        //we say there are 2 elements in the vector
        expect( mockVector.size() ).andReturn( 2 );
        //parsing of element 1
        expect( mockIterator.hasNext() ).andReturn( true );
        expect( mockIterator.next() ).andReturn( "testString" );
        expect( mockFH.getFile( isA( String.class ) ) ).andReturn( mockFile );
        expect( mockDocBuilder.parse( isA( File.class ) ) ).andReturn( mockDocument );
        expect( mockDocument.getDocumentElement() ).andReturn( mockElement );
        expect( mockElement.getTagName() ).andReturn( "unitTestString" );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn("unitTest" );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn("unitTest" );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn("unitTest" );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn("unitTest" );
        expect( mockElement.getTagName() ).andReturn( "plugin" );
        //parsing of element 2
        expect( mockIterator.hasNext() ).andReturn( true );
        expect( mockIterator.next() ).andReturn( "testString" );
        expect( mockFH.getFile( isA( String.class ) ) ).andReturn( mockFile );
        expect( mockDocBuilder.parse( isA( File.class ) ) ).andReturn( mockDocument );
        expect( mockDocument.getDocumentElement() ).andReturn( mockElement );
        expect( mockElement.getTagName() ).andReturn( "unitTestString" );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn("unitTest" );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn("unitTest" );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn("unitTest" );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn("unitTest" );
        expect( mockElement.getTagName() ).andReturn( "plugin" );
        expect( mockIterator.hasNext() ).andReturn( false );
        //out of the while and done
        /** 3 replay
         *
         */
        replay( mockFH );
        replay( mockVector );
        replay( mockIterator );
        replay( mockDocBuilder );
        replay( mockDocument );
        replay( mockElement );
        replay( mockFile );

        /** do stuff */
        pluginFinder = new PluginFinder( path, mockFH, mockDocBuilder );

        /**  4 check if it happened as expected
         *
         */ 
        verify( mockFH );
        verify( mockVector );
        verify( mockIterator );
        verify( mockDocBuilder );
        verify( mockDocument );
        verify( mockElement );
        verify( mockFile );
    }

    /**
     *
     */
    @Test public void getPluginClassTest() {

    }
}