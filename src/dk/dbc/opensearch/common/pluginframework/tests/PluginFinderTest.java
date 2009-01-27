/**
 * \file PluginFinderTest.java
 * \brief UnitTest for PluginFinder
 * \package tests
 */

package dk.dbc.opensearch.common.pluginframework.tests;

import dk.dbc.opensearch.common.pluginframework.PluginFinder;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.os.XmlFileFilter;
import dk.dbc.opensearch.common.pluginframework.TasksNotValidatedException;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;

import static org.junit.Assert.*;
import org.junit.*;
import static org.easymock.classextension.EasyMock.*;
import mockit.Mockit;

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

    static File mockFile = createMock( File.class );
    static Vector<String> mockVector = createMock( Vector.class );

    PluginFinder pluginFinder;
    FileHandler mockFH;


    Iterator mockIterator;
    DocumentBuilder mockDocBuilder;
    Document mockDocument;
    Element mockElement;
    NodeList mockNodeList;

    public static class MockFileHandler{

        public static File getFile( String path ){
            return mockFile;
        }
        public static Vector<String> getFileList( String path, FilenameFilter[] fileFilters, boolean descend ){
            return mockVector;
        }
    }


    /**
     *
     */
    @Before public void setUp() {

        mockIterator = createMock( Iterator.class );
        mockDocBuilder = createMock( DocumentBuilder.class );
        mockDocument = createMock( Document.class );
        mockElement = createMock( Element.class);
        mockVector = createMock( Vector.class );
        mockNodeList = createMock( NodeList.class );

        Mockit.redefineMethods( FileHandler.class, MockFileHandler.class );
    }

    /**
     *
     */
    @After public void tearDown() {

        reset( mockIterator );
        reset( mockDocBuilder );
        reset( mockDocument );
        reset( mockElement );
        reset( mockVector );
        reset( mockNodeList );
        reset( mockFile );


    }

    /**
     * Test that the finder is constructed and that the classNameMap
     * is build correctly. The updatePluginClassMap method is
     * tested indirectly, since it is called in the constructor
     * So there is no seperate test for that methods general functionality.
     */
    @Test public void constructorTest() throws TasksNotValidatedException, SAXException, IOException, NullPointerException, PluginResolverException {

        /** 1 setup
         *
         */
        String hestString = "hest";

        /** 2 expectations
         *
         */
        //in the updatePluginClassNameMap method
        expect( mockVector.iterator() ).andReturn( mockIterator );
        // value doesn't matter, this call is logging
        expect( mockVector.size() ).andReturn( 0 );
        //entering while
        expect( mockIterator.hasNext() ).andReturn( true );
        expect( mockIterator.next() ).andReturn( hestString );
        expect( mockDocBuilder.parse( isA( File.class ) ) ).andReturn( mockDocument );
        expect( mockDocument.getDocumentElement() ).andReturn( mockElement );
        expect( mockElement.getElementsByTagName( isA( String.class ) ) ).andReturn( mockNodeList );
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( hestString ).times( 4 ) ;
        expect( mockElement.getTagName() ).andReturn( "plugins" );
        expect( mockIterator.hasNext() ).andReturn( true );
        //in while again
        expect( mockIterator.next() ).andReturn( hestString );
        expect( mockDocBuilder.parse( isA( File.class ) ) ).andReturn( mockDocument );
        expect( mockDocument.getDocumentElement() ).andReturn( mockElement );
        expect( mockElement.getElementsByTagName( isA( String.class ) ) ).andReturn( mockNodeList );
        expect( mockNodeList.item ( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( hestString ).times( 4 ) ;
        expect( mockElement.getTagName() ).andReturn( "plugins" );
        expect( mockIterator.hasNext() ).andReturn( false );

        /** 3 replay
         *
         */
        replay( mockVector );
        replay( mockIterator );
        replay( mockDocBuilder );
        replay( mockDocument );
        replay( mockElement );
        replay( mockNodeList );
        replay( mockFile );

        /** do stuff */
        pluginFinder = new PluginFinder( mockDocBuilder, "" );

        /**  4 check if it happened as expected
         * verify
         */
        verify( mockVector );
        verify( mockIterator );
        verify( mockDocBuilder );
        verify( mockDocument );
        verify( mockElement );
        verify( mockNodeList );
        verify( mockFile );
    }

    /**
     *
     */
    @Ignore
        @Test public void getPluginClassNameTest() {

    }
}