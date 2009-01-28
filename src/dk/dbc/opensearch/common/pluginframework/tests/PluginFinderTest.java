/**
 * \file PluginFinderTest.java
 * \brief UnitTest for PluginFinder
 * \package tests
 */

package dk.dbc.opensearch.common.pluginframework.tests;

import dk.dbc.opensearch.common.pluginframework.PluginFinder;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.os.XmlFileFilter;
//import dk.dbc.opensearch.common.pluginframework.TasksNotValidatedException;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;

import static org.junit.Assert.*;
import org.junit.*;
import static org.easymock.classextension.EasyMock.*;
import mockit.Mockit;

import java.lang.InstantiationException;
import java.lang.ClassNotFoundException;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Vector;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.FileNotFoundException;

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
     * Happy path.
     */
    @Test public void constructorTest() throws SAXException, IOException, NullPointerException, PluginResolverException {

        /** 1 setup
         *
         */
        String testString = "test";

        /** 2 expectations
         *
         */
        //in the updatePluginClassNameMap method
        expect( mockVector.iterator() ).andReturn( mockIterator );
        // value doesn't matter, this call is logging
        expect( mockVector.size() ).andReturn( 0 );
        //entering while
        expect( mockIterator.hasNext() ).andReturn( true );
        expect( mockIterator.next() ).andReturn( testString );
        expect( mockDocBuilder.parse( isA( File.class ) ) ).andReturn( mockDocument );
        expect( mockDocument.getDocumentElement() ).andReturn( mockElement );
        expect( mockElement.getElementsByTagName( isA( String.class ) ) ).andReturn( mockNodeList );
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( testString ).times( 4 ) ;
        expect( mockElement.getTagName() ).andReturn( "plugins" );
        expect( mockIterator.hasNext() ).andReturn( true );
        //in while again
        expect( mockIterator.next() ).andReturn( testString );
        expect( mockDocBuilder.parse( isA( File.class ) ) ).andReturn( mockDocument );
        expect( mockDocument.getDocumentElement() ).andReturn( mockElement );
        expect( mockElement.getElementsByTagName( isA( String.class ) ) ).andReturn( mockNodeList );
        expect( mockNodeList.item ( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( testString ).times( 4 ) ;
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
     * test the happy path of the getPluginClassName method, where
     * classNameMap  needs to be rebuild.
     */
    @Test public void getPluginClassNameTest() throws InvocationTargetException, IOException, PluginResolverException, SAXException, FileNotFoundException, NoSuchMethodException, IllegalAccessException {

        /** 1 setup
         *
         */
        String testString = "test";

        /** 2 expectations
         *
         */
        //in the updatePluginClassNameMap method
        expect( mockVector.iterator() ).andReturn( mockIterator );
        // value doesn't matter, this call is logging
        expect( mockVector.size() ).andReturn( 0 );
        //entering while
        expect( mockIterator.hasNext() ).andReturn( true );
        expect( mockIterator.next() ).andReturn( testString );
        expect( mockDocBuilder.parse( isA( File.class ) ) ).andReturn( mockDocument );
        expect( mockDocument.getDocumentElement() ).andReturn( mockElement );
        expect( mockElement.getElementsByTagName( isA( String.class ) ) ).andReturn( mockNodeList );
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( testString ).times( 4 ) ;
        expect( mockElement.getTagName() ).andReturn( "plugins" );
        expect( mockIterator.hasNext() ).andReturn( false );

        /**
         * has been cleared, the getPluginClassName forces the map
         * classNameMap to be rebuild
         * */

        //in the updatePluginClassNameMap method again
        expect( mockVector.iterator() ).andReturn( mockIterator );
        // value doesn't matter, this call is logging
        expect( mockVector.size() ).andReturn( 0 );
        //entering while
        expect( mockIterator.hasNext() ).andReturn( true );
        expect( mockIterator.next() ).andReturn( testString );
        expect( mockDocBuilder.parse( isA( File.class ) ) ).andReturn( mockDocument );
        expect( mockDocument.getDocumentElement() ).andReturn( mockElement );
        expect( mockElement.getElementsByTagName( isA( String.class ) ) ).andReturn( mockNodeList );
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( testString ).times( 4 ) ;
        expect( mockElement.getTagName() ).andReturn( "plugins" );
        expect( mockIterator.hasNext() ).andReturn( false );

        //back in the getPluginClassName method that should return testString



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
        Method method1;
        Method method2;
        Class[] argClasses1 = new Class[]{};
        Class[] argClasses2 = new Class[]{ String.class };
        Object[] args2 = new Object[]{ testString + testString + testString };


        pluginFinder = new PluginFinder( mockDocBuilder, "" );
       
            method1 = pluginFinder.getClass().getDeclaredMethod( "clearClassNameMap", argClasses1 );
            method2 = pluginFinder.getClass().getDeclaredMethod( "getPluginClassName", argClasses2 );
            method1.setAccessible( true );
            method2.setAccessible( true );
            method1.invoke( pluginFinder );
            assertTrue( testString.equals( method2.invoke( pluginFinder, args2 ) ) );
            //pluginFinder.clearClassNameMap();
            //assertTrue( testString.equals( pluginFinder.getPluginClassName( testString + testString + testString ) ) );
        
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
     * Tests the throwing of the FileNotFoundException when there is no
     * plugin classname corresponding to the key
     */

    @Test public void noFileFoundExceptionTest () throws IOException, PluginResolverException, SAXException, FileNotFoundException, NoSuchMethodException, IllegalAccessException{

        /** 1 setup
         *
         */
        String testString = "test";
        boolean fileNotFound = false;
        /** 2 expectations
         *
         */
        //in the updatePluginClassNameMap method
        expect( mockVector.iterator() ).andReturn( mockIterator );
        // value doesn't matter, this call is logging
        expect( mockVector.size() ).andReturn( 0 );
        //entering while
        expect( mockIterator.hasNext() ).andReturn( true );
        expect( mockIterator.next() ).andReturn( testString );
        expect( mockDocBuilder.parse( isA( File.class ) ) ).andReturn( mockDocument );
        expect( mockDocument.getDocumentElement() ).andReturn( mockElement );
        expect( mockElement.getElementsByTagName( isA( String.class ) ) ).andReturn( mockNodeList );
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( testString ).times( 4 ) ;
        expect( mockElement.getTagName() ).andReturn( "plugins" );
        expect( mockIterator.hasNext() ).andReturn( false );

        //in the getPluginClassName method, that cant find a value for the key


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
        Method method;
        Class[] argClasses = new Class[]{ String.class };
        Object[] args = new Object[]{ testString };

        pluginFinder = new PluginFinder( mockDocBuilder, "" );
        try{
            method = pluginFinder.getClass().getDeclaredMethod( "getPluginClassName", argClasses );
            method.setAccessible( true );
            method.invoke( pluginFinder, args );
        }catch( InvocationTargetException ite ){
            fileNotFound = ( ite.getCause().getClass() == FileNotFoundException.class );
        }
        assertTrue( fileNotFound );

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
}