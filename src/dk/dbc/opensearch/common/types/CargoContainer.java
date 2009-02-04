/**
 * \file CargoContainer.java
 * \brief The CargoContainer class
 * \package datadock
 */
package dk.dbc.opensearch.common.types;

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.io.IOException; 
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * \ingroup datadock
 * \brief CargoContainer is a datastructure used throughout OpenSearch for
 * carrying the informations submitted for indexing. CargoContainer
 * Handles the verification of the submitted datatypes as well as
 * adding additional information used by the OpenSearch components
 * when dealing with the data.
 */
public class CargoContainer 
{    
    private String dublinCoreMetadata = null;

    /** object to hold information about the data*/
    private CargoObjectInfo coi;

    /** holder for the data (InputStream supports estimations through .available()) */
    private BufferedInputStream data;
    private HashMap< CargoObjectInfo, List<Byte> > data2;

    private long timestamp;

    /** the length of the InputStream */
    private final int contentLength;

    Logger log = Logger.getLogger( "CargoContainer" );

    
    public CargoContainer( ArrayList< Pair< CargoObjectInfo, InputStream > > data ) throws IOException
    {  
    	//todo: delete
    	contentLength = 0;
    	
    	for ( Pair< CargoObjectInfo, InputStream > p : data )
    	{    		
    		CargoObjectInfo coi = p.getFirst();
    		String mimetype = coi.getMimeType();
    		if ( ! checkMimeType( mimetype ) ) {
            	log.fatal( String.format( "no mimetype goes by the name of %s", mimetype ) );
            	throw new IllegalArgumentException( String.format( "no mimetype goes by the name of %s", mimetype ) );
    		}
    		
            /** \todo: How to specify allowed languages? enums? db? */
    		String lang = coi.getLanguage();
            if( ! checkLanguage( lang ) )
            {
                log.fatal( String.format( "Language '%s' not in list of allowed languages", lang ) );
                throw new IllegalArgumentException( String.format( "%s is not in the languagelist", lang ) );
            }
            log.debug( String.format( "Identified language %s", lang ) );

            String format = coi.getFormat();
            /** \todo: how to identify allowed formats? */
            log.debug( String.format( "Identified format %s", format ) );

            String submitter = coi.getSubmitter();
            /** \todo: need better checking of values (perhaps using enums?) before constructing */
            if( !checkSubmitter( submitter ) )
            {
                log.fatal( String.format( "Submitter '%s' not in list of allowed submitters", submitter ) );
                throw new IllegalArgumentException( String.format( "%s is not in the submitterlist", submitter ) );
            }
            log.debug( String.format( "Identified submitter %s", submitter ) );

    		InputStream is = p.getSecond();
    		List<Byte> tmp = new ArrayList<Byte>( readStream( is ) );
    		data2.put( coi, tmp );
    	}
    }
    
    private List<Byte> readStream( InputStream is ) throws IOException{
		ArrayList<Byte> al = new ArrayList<Byte>();
    	
		while( is.available() > 0 )
		{
			Byte dataByte = new Byte( (byte)is.read() );
			al.add( dataByte ); 
		}
		return al;
    }

    /**
     * Constructor for the CargoContainer class. Creates an instance
     * based on a string representation of the data to be submitted
     * into the OpenSearch repository and indexing mechanism
     * (dk.dbc.opensearch.components.pti) and associated metadata in a
     * CargoMetadata container
     *
     * \see dk.dbc.opensearch.components.pti
     *
     * @param data: The incoming data represented as a
     * ByteArrayInputStream (or any stream that have a count method )
     * @param mime: The mimetype of the data. Must conform to the allowed mimetypes
     * @param lang: the language used in the data
     * @param submitter: the submitter of the data, for identifying the pid namespace
     * @param format: the format of the submitted data, for generating itemIDs and getting the correct Compass mappings
     *
     * @throws java.io.IOException if the stream is corrupted
     * @throws IllegalArgumentException if arguments are illformed.
     */
    public CargoContainer( InputStream data, String mime, String lang, String submitter, String format ) throws IOException, IllegalArgumentException, NullPointerException
    {
        log.debug( String.format( "Entering CargoContainer constructor" ) );
        // 05: get the stream into the object
        if( data.available() > 0 )
        {
            this.data = new BufferedInputStream( data );
        }
        else
        {
            log.fatal( String.format( "No data in inputstream, refusing to construct empty CargoContainer" ) );
            throw new NullPointerException( "Refusing to construct a cargocontainer without cargo" );
        }
        
        log.debug( String.format( "Saved data" ) );

        // 10: check mimetype
        CargoMimeType CMT = null;
        log.debug( String.format( "checking mimetype: %s", mime ) );
        for (CargoMimeType cmt : CargoMimeType.values() )
        {
        	//System.out.println( "cmt: " + cmt.getMimeType() );System.out.println( "mime: " + mime );
        	if( mime.equals( cmt.getMimeType() ) )        	
            {
                log.debug( String.format( "Identified mimetype %s", mime ) );
                CMT = cmt;
            }
        }
        
        if( CMT == null )
        {
            throw new IllegalArgumentException( String.format( "no mimetype goes by the name of %s", mime ) );
            
        }

        // 30: check language
        /** \todo: How to specify allowed languages? enums? db? */
        if( !checkLanguage( lang ) )
        {
            log.fatal( String.format( "Language '%s' not in list of allowed languages", lang ) );
            throw new IllegalArgumentException( String.format( "%s is not in the languagelist", lang ) );
        }
        
        log.debug( String.format( "Identified language %s", lang ) );

        // 33: check format
        /** \todo: how to identify allowed formats? */
        log.debug( String.format( "Identified format %s", format ) );


        // 35: check submitter (credentials checking)
        /** \todo: need better checking of values (perhaps using enums?) before constructing */
        if( !checkSubmitter( submitter ) )
        {
            log.fatal( String.format( "Submitter '%s' not in list of allowed submitters", submitter ) );
            throw new IllegalArgumentException( String.format( "%s is not in the submitterlist", submitter ) );
        }

        log.debug( String.format( "Identified submitter %s", submitter ) );

        // 40: get stream length
        // available returns (count - pos), both of which are private
        /** \todo: Make _absolutely_ sure that a call to the length of
            the internal representation of the stream returns the
            total, always.

            The InputStream.available() returns the number of bytes
            available in the stream, not the total number of bytes in
            the stream. When doing a StreamObject.available() before
            the stream is read, available() returns the number of
            bytes that _can_ be read from the Stream.

            I'm not confidently sure that this always is the number of
            bytes actually in the stream, as sun discourages the use
            of available to use the return value of this method to
            allocate a buffer intended to hold all data from the
            stream:
            http://java.sun.com/javase/6/docs/api/java/io/InputStream.html#available()
        */
        contentLength = this.data.available();

        log.debug( String.format( "Identified streamlength %s", contentLength ) );

        // 50: construct CargoObjectInfo object
        coi = new CargoObjectInfo( CMT, lang, submitter, format, contentLength );
        
        log.debug( String.format( "All Checks passed, CargoContainer constructed with values %s, %s, %s, %s", this.getStreamLength(), this.getMimeType(), lang, this.getSubmitter() ) );        
    }
    
    
    /**
     * Constructor for the CargoContainer class. facilitates dublin core.
     *
     * @param data: The incoming data represented as a
     * ByteArrayInputStream (or any stream that have a count method )
     * @param data The data to process
     * @param dublinCore The dublin core data to attach to the data
     * @param submitter the submitter of the data, for identifying the pid namespace
     * @param format the format of the submitted data, for generating itemIDs and getting the correct Compass mappings
     *
     * @throws ParserConfigurationException if something goes wrong while parsing dcxml
     * @throws SAXException if something goes wrong while parsing dcxml
     * @throws java.io.IOException if the stream is corrupted
     * @throws IllegalArgumentException if arguments are illformed.
     */
    public CargoContainer( InputStream data, String dublinCore, String submitter, String format) throws ParserConfigurationException, SAXException, IOException, IllegalArgumentException
    {
        // read data
        if( data.available() > 0 )
        {
            this.data = new BufferedInputStream( data );
        }
        
        dublinCoreMetadata = dublinCore;
        
        // 10: isolate and validate used dublin core fields
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(new InputSource(new StringReader( dublinCore )));

        NodeList langCand = doc.getElementsByTagName( "dc:language" );
        String language = null;
        for( int i = 0; i < langCand.getLength() ; i++ ){
            Element tmpElem = (Element) langCand.item( i );
            String cand =  tmpElem.getFirstChild().getNodeValue();
            language = cand;
            break;            
        }        
        if ( language == null){
            throw new IllegalArgumentException( "No language specifier found" );
        }

        NodeList mimeCand = doc.getElementsByTagName( "dc:format" );
        String mimeType = null;
        String candMime = null;
        for( int i = 0; i < mimeCand.getLength() ; i++ ){
   
            Element tmpElem = (Element) mimeCand.item( i );
            candMime =  tmpElem.getFirstChild().getNodeValue();
            if ( checkMimeType( candMime ) ){
                mimeType = candMime;
                break;
            }
        }
        
        // TODO: make getMime method

        if ( mimeType == null){
            throw new IllegalArgumentException( String.format( "Mimetype '%s' not supported ", candMime ) );
        }
        CargoMimeType CMT = null;
        for (CargoMimeType cmt : CargoMimeType.values() ){
            if( mimeType.equals( cmt.getMimeType() ) ){
                log.debug( String.format( "Identified mimetype %s", mimeType ) );
                CMT = cmt;
            }
        }

        System.out.println( "MIMETYPE '"+mimeType+"'" );
        System.out.println( "LANGUAGE '"+language+"'" );
                
        contentLength = this.data.available();
        
       log.debug( String.format( "Identified streamlength %s", contentLength ) );

       coi = new CargoObjectInfo( CMT, language, submitter, format, contentLength );
    }

    
    /**
     * Checks the validity if the submitter
     *
     * @returns true if name is found in submitter-list, false otherwise
     */
    public boolean checkSubmitter( String name ) throws IllegalArgumentException
    {
        /** \todo: FIXME: Hardcoded values for allowed submitters */
        return true;
    }

    /**
     * Checks the validity if the mimeType
     *
     * @returns true if mimetype is allowed in OpenSearch, false otherwise
     */
    public boolean checkMimeType( String mimetype )
    {
        CargoMimeType CMT = null;
        log.debug( "checking mimetype" );
        for (CargoMimeType cmt : CargoMimeType.values() )
        {
            if( mimetype.equals( cmt.getMimeType() ) )
            {
                CMT = cmt;
            }
        }
        
        if( CMT == null )
        {
        	return false;
        }
        
        return true;

    }
    
    
    /**
     * Checks the validity if the language
     *
     *@returns true if language is allowed in Opensearch, false otherwise
     */
    public boolean checkLanguage(String lang)
    {
        return true;
    }

    
    /**
     * Gets the data from the container
     *
     * @returns the data of the container-object as an BufferedInputStream
     */
    public BufferedInputStream getData()
    {
        return this.data;
    }

    
    /**
     * Get the InputStream returned as a byte array
     *
     * @returns the internal data representation as a byte[]
     *
     * @throws IOException if the CargoContainer data could not be written to the byte[]
     * @throws NullPointerException if the pointer to the internal representation was corrupted
     */
    public byte[] getDataBytes() throws IOException, NullPointerException
    {
        log.info( String.format( "Constructing byte[] with length %s", this.contentLength ) );

        byte[] ba = new byte[ this.contentLength ];

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedInputStream in = getData();

        try{
            in.mark( getStreamLength() );
            in.read(ba, 0, getStreamLength() );
            bos.write(ba, 0, getStreamLength() );
            // }catch( IOException ioe ){
            //     log.fatal( String.format( "Could not write CargoContainer data to byte array. The data might be corrupted:\n %s", ioe.toString() ) );
            //     throw new IOException( ioe );
            // }catch( NullPointerException npe){
            //     log.fatal( String.format( "Tried to read data from empty pointer. Data in pointer was %s", npe.toString() ) );
        }
        finally
        {
            in.reset();
        }
        
        log.debug( String.format( "Returning bytearray" ) );
        return bos.toByteArray();
    }

    
    /**
     * Get the InputStream returned as a bytearrayoutputstream
     *
     * @returns the internal data represented as a ByteArrayOutputStream
     *
     * @throws IOException if the data could not be written to the ByteArrayOutputStream
     * @throws NullPointerException if the pointer to the internal representation was corrupted
     */
    public ByteArrayOutputStream getDataBAOS() throws IOException, NullPointerException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedInputStream in = getData();

        // try {
        int c = in.read();
        while(c != -1) {
            bos.write(c);
            c = in.read();
        }
        // }catch( IOException ioe ){
        //     log.fatal( String.format( "Could not write CargoContainer data to byte array. The data might be corrupted:\n %s", ioe.toString() ) );
        //     throw new IOException( ioe );
        // }catch( NullPointerException npe){
        //     log.fatal( String.format( "Tried to read data from empty pointer. Data in pointer was %s", npe.toString() ) );
        // }

        return bos;

    }

    /**
     * Returns the length of the datastream in bytes
     *
     * @returns the length of the data-stream
     */
    public int getContentLength( CargoObjectInfo key ) {
        return this.data2.get( key ).size();
    }
    
    public int getStreamLength()
    {
    	return contentLength;
    }

    /**
     * Returns the mimetype
     *
     * @returns the mimetype of the data as a string
     */
    public String getMimeType(){
        return coi.getMimeType();
    }

    /**
     * Returns the name of the submitter
     *
     * @returns the submitter as a string
     */
    public String getSubmitter(){
        return coi.getSubmitter();
    }

    /**
     * Returns the format
     *
     * @returns the format as a string
     */
    public String getFormat(){
        return coi.getFormat();
    }
 
    /** \todo: needs unittest */

    /**
     * Returns this CargoContainers timestamp
     *
     * @returns the timestamp of the CargoContainer
     */
    public long getTimestamp(){
        return timestamp;
    }

    /** \todo: needs unittest */

    /**
     * Sets this CargoContainers timestamp
     * Sets a timestamp on the cargocontainer. This is not reflecting
     * when the CargoContainer was initialized, but is solely up to
     * the client
     */
    public void setTimestamp(){
        timestamp = System.currentTimeMillis();
    }

}