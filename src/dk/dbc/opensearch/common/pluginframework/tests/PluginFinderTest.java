/**
 * \file PluginFinderTest.java
 * \brief UnitTest for PluginFinder
 * \package tests
 */

package dk.dbc.opensearch.common.pluginframework.tests;

import dk.dbc.opensearch.common.pluginframework.PluginFinder;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.os.XmlFileFilter;
import dk.dbc.opensearch.common.types.ThrownInfo;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.InstantiationException;
import java.lang.ClassNotFoundException;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;

import mockit.Mockit;

import static org.easymock.classextension.EasyMock.*;
import org.junit.*;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * The class that test the PluginFinder. There is an if-clause that cannot be tested,
 * because it tests for a freak case that we hope never ever will happen, since
 * it would take that there are plugin files to be found, they are of valid format but
 * somehow none of them are registrered in the classNameMap
 */
public class PluginFinderTest 
{
    static File mockFile = createMock( File.class );
    static Vector< String > mockVector = createMock( Vector.class );

    PluginFinder pluginFinder;
    FileHandler mockFH;

    Iterator mockIterator;
    DocumentBuilder mockDocBuilder;
    Document mockDocument;
    Element mockElement;
    NodeList mockNodeList;
    

    public static class MockFileHandler
    {
        public static File getFile( String path )
        {
            return mockFile;
        }
        
        
        public static Vector<String> getFileList( String path, FilenameFilter[] fileFilters, boolean descend )
        {
            return mockVector;
        }
    }


    /**
     *
     */
    @Before 
    public void setUp() 
    {
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
    @After
    public void tearDown() 
    {
        reset( mockIterator );
        reset( mockDocBuilder );
        reset( mockDocument );
        reset( mockElement );
        reset( mockVector );
        reset( mockNodeList );
        reset( mockFile );

        Mockit.restoreAllOriginalDefinitions();
    }
    

    /**
     * Test that the finder is constructed and that the classNameMap
     * is build correctly. The updatePluginClassMap method is
     * tested indirectly, since it is called in the constructor
     * So there is no seperate test for that methods general functionality.
     * Happy path.
     */
    @Test
    public void constructorTest() throws SAXException, IOException, NullPointerException, PluginResolverException 
    {
        /** 1 setup
         *
         */
        String testString = "test";

        /** 2 expectations
         *
         */
        //in the updatePluginClassNameMap method

        expect( mockVector.iterator() ).andReturn( mockIterator );
        expect( mockVector.size() ).andReturn( 0 ); //log call
        expect( mockVector.size() ).andReturn( 2 ); //must be larger than 0
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
     * Test the behaviour when there are no .plugin files to be found
     */
    @Test( expected = FileNotFoundException.class )
        public void noPluginDescriptionFiles() throws SAXException, IOException, NullPointerException, PluginResolverException{
        /** 1 setup
         *
         */
        String testString = "test";

        /** 2 expectations
         *
         */
        //in the updatePluginClassNameMap method
        expect( mockVector.size() ).andReturn( 0 ).times( 2 );

        /** 3 replay
         *
         */
        replay( mockVector );

        /** do stuff */
        pluginFinder = new PluginFinder( mockDocBuilder, "" );

        /**  4 check if it happened as expected
         * verify
         */
        verify( mockVector );
    }
    
    
    /**
     * Tests that the SAXException that can be caused by a parse operation is
     * put unto the PluginResolverException and that this is thrown
     */
    @Test
    public void saxExceptionParseTest() throws FileNotFoundException, SAXException, IOException 
    {
        /** 1 setup
         *
         */
        String testString = "test";
        Vector<ThrownInfo> exceptionVector = null;
        Iterator expVecIter;
        /** 2 expectations
         *
         */
        //in the updatePluginClassNameMap method

        expect( mockVector.iterator() ).andReturn( mockIterator );
        expect( mockVector.size() ).andReturn( 0 ); //log call
        expect( mockVector.size() ).andReturn( 2 ); //must be larger than 0
        //entering while
        expect( mockIterator.hasNext() ).andReturn( true );
        expect( mockIterator.next() ).andReturn( testString );
        expect( mockDocBuilder.parse( isA( File.class ) ) ).andThrow( new SAXException( testString ) );
        expect( mockIterator.hasNext() ).andReturn( false );

        /** 3 replay
         *
         */
        replay( mockVector );
        replay( mockIterator );
        replay( mockDocBuilder );

        /** do stuff */
        try{
            pluginFinder = new PluginFinder( mockDocBuilder, "" );
        }catch( PluginResolverException pre ){
            exceptionVector = pre.getExceptionVector();
        }
        expVecIter = exceptionVector.iterator();
        assertTrue( SAXException.class == ( ( (ThrownInfo) expVecIter.next() ).getThrowable().getClass() ) );

        /**  4 check if it happened as expected
         * verify
         */
        verify( mockVector );
        verify( mockIterator );
        verify( mockDocBuilder );
    }
    
    
    /**
     * tests that the IOException that can be caused by a parse is sent with the
     * PluginResolverException
     */
    @Test 
    public void ioExceptionParseTest() throws FileNotFoundException, SAXException, IOException 
    {
        /** 1 setup
         *
         */
        String testString = "test";
        Vector<ThrownInfo> exceptionVector = null;
        Iterator expVecIter;
        /** 2 expectations
         *
         */
        //in the updatePluginClassNameMap method

        expect( mockVector.iterator() ).andReturn( mockIterator );
        expect( mockVector.size() ).andReturn( 0 ); //log call
        expect( mockVector.size() ).andReturn( 2 ); //must be larger than 0
        //entering while
        expect( mockIterator.hasNext() ).andReturn( true );
        expect( mockIterator.next() ).andReturn( testString );
        expect( mockDocBuilder.parse( isA( File.class ) ) ).andThrow( new IOException( testString ) );
        expect( mockIterator.hasNext() ).andReturn( false );

        /** 3 replay
         *
         */
        replay( mockVector );
        replay( mockIterator );
        replay( mockDocBuilder );

        /** do stuff */
        try{
            pluginFinder = new PluginFinder( mockDocBuilder, "" );
        }catch( PluginResolverException pre ){
            exceptionVector = pre.getExceptionVector();
        }
        expVecIter = exceptionVector.iterator();
        assertTrue( IOException.class == ( ( (ThrownInfo)expVecIter.next() ).getThrowable().getClass() ) );

        /**  4 check if it happened as expected
         * verify
         */
        verify( mockVector );
        verify( mockIterator );
        verify( mockDocBuilder );
    }
    
    
    /**
     * tests that the NullPointerException that can be caused by a parse is put
     * into the PluginResolverException, and that this is thrown
     */
    @Test 
    public void nullPointerExceptionParseTest() throws FileNotFoundException, SAXException, IOException 
    {
        /** 1 setup
         *
         */
        String testString = "test";
        Vector<ThrownInfo> exceptionVector = null;
        Iterator expVecIter;
        /** 2 expectations
         *
         */
        //in the updatePluginClassNameMap method

        expect( mockVector.iterator() ).andReturn( mockIterator );
        expect( mockVector.size() ).andReturn( 0 ); //log call
        expect( mockVector.size() ).andReturn( 2 ); //must be larger than 0
        //entering while
        expect( mockIterator.hasNext() ).andReturn( true );
        expect( mockIterator.next() ).andReturn( testString );
        expect( mockDocBuilder.parse( isA( File.class ) ) ).andThrow( new NullPointerException( testString ) );
        expect( mockIterator.hasNext() ).andReturn( false );

        /** 3 replay
         *
         */
        replay( mockVector );
        replay( mockIterator );
        replay( mockDocBuilder );

        /** do stuff */
        try{
            pluginFinder = new PluginFinder( mockDocBuilder, "" );
        }catch( PluginResolverException pre ){
            exceptionVector = pre.getExceptionVector();
        }
        expVecIter = exceptionVector.iterator();
        assertTrue( NullPointerException.class == ( ( ( ThrownInfo ) expVecIter.next() ).getThrowable().getClass() ) );

        /**  4 check if it happened as expected
         * verify
         */
        verify( mockVector );
        verify( mockIterator );
        verify( mockDocBuilder );

    }
    
    
    /**
      * Test that if the plugin xml file doesnt have "plugins" as name of the
      * root element or one of the four values extracted from the file are
      * null a SAXException is put onto the PluginResolverException. We make
      * this happen by returning null when the mockElement.getAttribute is called,
      * to get the submitter
      */
    @Test 
    public void invalidFileFormatSubmitterTest() throws SAXException, FileNotFoundException, IOException
    {
        /** 1 setup
         *
         */
        String testString = "test";
        Vector<ThrownInfo> exceptionVector = null;
        Iterator expVecIter;
        /** 2 expectations
         *
         */
        //in the updatePluginClassNameMap method

        expect( mockVector.iterator() ).andReturn( mockIterator );
        expect( mockVector.size() ).andReturn( 0 ); //log call
        expect( mockVector.size() ).andReturn( 2 ); //must be larger than 0
        //entering while
        expect( mockIterator.hasNext() ).andReturn( true );
        expect( mockIterator.next() ).andReturn( testString );
        expect( mockDocBuilder.parse( isA( File.class ) ) ).andReturn( mockDocument );
        expect( mockDocument.getDocumentElement() ).andReturn( mockElement );
        expect( mockElement.getElementsByTagName( isA( String.class ) ) ).andReturn( mockNodeList );
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( null );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( testString ).times( 3 );
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
        try
        {
            pluginFinder = new PluginFinder( mockDocBuilder, "" );
        }
        catch( PluginResolverException pre)
        {
            exceptionVector = pre.getExceptionVector();
        }
        
        expVecIter = exceptionVector.iterator();
        assertTrue( SAXException.class == ( ( (ThrownInfo)expVecIter.next() ).getThrowable().getClass() ) );

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
     * Test that if the plugin xml file doesnt have "plugins" as name of the
     * root element or one of the four values extracted from the file are
     * null a SAXException is put onto the PluginResolverException. We make
     * this happen by returning another String than the expected "plugins"
     */
    @Test 
    public void invalidFileFormatPluginsTest() throws SAXException, FileNotFoundException, IOException
    {
        /** 1 setup
         *
         */
        String testString = "test";
        Vector<ThrownInfo> exceptionVector = null;
        Iterator expVecIter;
        /** 2 expectations
         *
         */
        //in the updatePluginClassNameMap method

        expect( mockVector.iterator() ).andReturn( mockIterator );
        expect( mockVector.size() ).andReturn( 0 ); //log call
        expect( mockVector.size() ).andReturn( 2 ); //must be larger than 0
        //entering while
        expect( mockIterator.hasNext() ).andReturn( true );
        expect( mockIterator.next() ).andReturn( testString );
        expect( mockDocBuilder.parse( isA( File.class ) ) ).andReturn( mockDocument );
        expect( mockDocument.getDocumentElement() ).andReturn( mockElement );
        expect( mockElement.getElementsByTagName( isA( String.class ) ) ).andReturn( mockNodeList );
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( testString ).times( 4 );
        expect( mockElement.getTagName() ).andReturn( "invalid" );

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
        try
        {
            pluginFinder = new PluginFinder( mockDocBuilder, "" );
        }
        catch( PluginResolverException pre)
        {
            exceptionVector = pre.getExceptionVector();
        }
        
        expVecIter = exceptionVector.iterator();
        assertTrue( SAXException.class == ( ( (ThrownInfo)expVecIter.next() ).getThrowable().getClass() ) );

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
     * Test that if the plugin xml file doesnt have "plugins" as name of the
     * root element or one of the four values extracted from the file are
     * null a SAXException is put onto the PluginResolverException. We make
     * this happen by returning null when the mockElement.getAttribute is called,
     * to get the format
     */
    @Test 
    public void invalidFileFormatFormatTest() throws SAXException, FileNotFoundException, IOException
    {
        /** 1 setup
         *
         */
        String testString = "test";
        Vector<ThrownInfo> exceptionVector = null;
        Iterator expVecIter;
        /** 2 expectations
         *
         */
        //in the updatePluginClassNameMap method

        expect( mockVector.iterator() ).andReturn( mockIterator );
        expect( mockVector.size() ).andReturn( 0 ); //log call
        expect( mockVector.size() ).andReturn( 2 ); //must be larger than 0
        //entering while
        expect( mockIterator.hasNext() ).andReturn( true );
        expect( mockIterator.next() ).andReturn( testString );
        expect( mockDocBuilder.parse( isA( File.class ) ) ).andReturn( mockDocument );
        expect( mockDocument.getDocumentElement() ).andReturn( mockElement );
        expect( mockElement.getElementsByTagName( isA( String.class ) ) ).andReturn( mockNodeList );
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( testString );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( null );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( testString ).times( 2 );
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
        try
        {
            pluginFinder = new PluginFinder( mockDocBuilder, "" );
        }
        catch( PluginResolverException pre)
        {
            exceptionVector = pre.getExceptionVector();
        }
        expVecIter = exceptionVector.iterator();
        assertTrue( SAXException.class == ( ( (ThrownInfo) expVecIter.next() ).getThrowable().getClass() ) );

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
     * Test that if the plugin xml file doesnt have "plugins" as name of the
     * root element or one of the four values extracted from the file are
     * null a SAXException is put onto the PluginResolverException. We make
     * this happen by returning null when the mockElement.getAttribute is called,
     * to get the task
     */
    @Test 
    public void invalidFileFormatTaskTest() throws SAXException, FileNotFoundException, IOException
    {
        /** 1 setup
         *
         */
        String testString = "test";
        Vector<ThrownInfo> exceptionVector = null;
        Iterator expVecIter;
        /** 2 expectations
         *
         */
        //in the updatePluginClassNameMap method

        expect( mockVector.iterator() ).andReturn( mockIterator );
        expect( mockVector.size() ).andReturn( 0 ); //log call
        expect( mockVector.size() ).andReturn( 2 ); //must be larger than 0
        //entering while
        expect( mockIterator.hasNext() ).andReturn( true );
        expect( mockIterator.next() ).andReturn( testString );
        expect( mockDocBuilder.parse( isA( File.class ) ) ).andReturn( mockDocument );
        expect( mockDocument.getDocumentElement() ).andReturn( mockElement );
        expect( mockElement.getElementsByTagName( isA( String.class ) ) ).andReturn( mockNodeList );
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( testString ).times( 2 );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( null );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( testString );
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
        try
        {
            pluginFinder = new PluginFinder( mockDocBuilder, "" );
        }
        catch( PluginResolverException pre)
        {
            exceptionVector = pre.getExceptionVector();
        }
        expVecIter = exceptionVector.iterator();
        assertTrue( SAXException.class == ( ( ( ThrownInfo ) expVecIter.next()).getThrowable().getClass() ) );

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
     * Test that if the plugin xml file doesnt have "plugins" as name of the
     * root element or one of the four values extracted from the file are
     * null a SAXException is put onto the PluginResolverException. We make
     * this happen by returning null when the mockElement.getAttribute is called,
     * to get the classname
     */

    
    @Test 
    public void invalidFileFormatClassnameTest() throws SAXException, FileNotFoundException, IOException
    {
        /** 1 setup
         *
         */
        String testString = "test";
        Vector<ThrownInfo> exceptionVector = null;
        Iterator expVecIter;
        /** 2 expectations
         *
         */
        //in the updatePluginClassNameMap method

        expect( mockVector.iterator() ).andReturn( mockIterator );
        expect( mockVector.size() ).andReturn( 0 ); //log call
        expect( mockVector.size() ).andReturn( 2 ); //must be larger than 0
        //entering while
        expect( mockIterator.hasNext() ).andReturn( true );
        expect( mockIterator.next() ).andReturn( testString );
        expect( mockDocBuilder.parse( isA( File.class ) ) ).andReturn( mockDocument );
        expect( mockDocument.getDocumentElement() ).andReturn( mockElement );
        expect( mockElement.getElementsByTagName( isA( String.class ) ) ).andReturn( mockNodeList );
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( testString ).times( 3 );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( null );
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
        try
        {
            pluginFinder = new PluginFinder( mockDocBuilder, "" );
        }
        catch( PluginResolverException pre)
        {
            exceptionVector = pre.getExceptionVector();
        }
        
        expVecIter = exceptionVector.iterator();
        assertTrue( SAXException.class == ( ( (ThrownInfo) expVecIter.next() ).getThrowable().getClass() ) );

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
    @Test 
    public void getPluginClassNameTest() throws InvocationTargetException, IOException, PluginResolverException, SAXException, FileNotFoundException, NoSuchMethodException, IllegalAccessException 
    {
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
        expect( mockVector.size() ).andReturn( 1 );
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
         * classNameMap has been cleared, the getPluginClassName forces
         * it to be rebuild
         */
        //in the getPluginClassName method again

        //in the updatePluginClassNameMap method again
        expect( mockVector.iterator() ).andReturn( mockIterator );
        expect( mockVector.size() ).andReturn( 0 );//log call
        expect( mockVector.size() ).andReturn( 2 );
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
        Class[] argClasses2 = new Class[]{ int.class };
        Object[] args2 = new Object[]{ (testString + testString + testString).hashCode() };

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
    @Test 
    public void noFileFoundExceptionTest () throws IOException, PluginResolverException, SAXException, FileNotFoundException, NoSuchMethodException, IllegalAccessException
    {
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
        expect( mockVector.size() ).andReturn( 0 );// log call
        expect( mockVector.size() ).andReturn( 1 );
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
        Class[] argClasses = new Class[]{ int.class };
        Object[] args = new Object[]{ testString.hashCode() };

        pluginFinder = new PluginFinder( mockDocBuilder, "" );
        try
        {
            method = pluginFinder.getClass().getDeclaredMethod( "getPluginClassName", argClasses );
            method.setAccessible( true );
            method.invoke( pluginFinder, args );
        }
        catch( InvocationTargetException ite )
        {
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