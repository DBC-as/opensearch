/**
 * \file CargoContainer.java
 * \brief The CargoContainer class
 * \package datadock
 */
package dk.dbc.opensearch.common.types;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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
    /** object to hold information about the data*/
    private CargoObjectInfo coi;

    /** The internal representation of the data contained in the CC*/
    private ArrayList< Pair< CargoObjectInfo, List<Byte> > > data_list;
    private ArrayList< CargoObject > data;

    private long timestamp;

    Logger log = Logger.getLogger( "CargoContainer" );

    /**
     * Constructor for the CargoContainer class. Creates an instance
     * based on a string representation of the data to be submitted
     * into the OpenSearch repository and indexing mechanism
     * (dk.dbc.opensearch.components.pti) and associated metadata in a
     * CargoMetadata container
     *
     * \see dk.dbc.opensearch.components.pti
     *
     *  This method is marked as deprecated, and will be deleted when
     *  all calls to it from other components are weeded out.
     *  
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
    @Deprecated
        public CargoContainer( InputStream data, String mime, String lang, String submitter, String format ) throws IOException, IllegalArgumentException, NullPointerException, IllegalStateException
    {
        log.warn( "Calling the constructor CargoContainer( InputStream data, String mime, String lang, String submitter, String format ) is deprecated. Please use CargoContainer( List< Pair< CargoObjectInfo, InputStream > > data )" );
        CargoMimeType mimetype = null;
        for (CargoMimeType cmt : CargoMimeType.values() ){
            if( mime.equals( cmt.getMimeType() ) ){
                log.debug( String.format( "Identified mimetype %s", mime ) );
                mimetype = cmt;
            }
        }

        CargoObjectInfo coi = new CargoObjectInfo( mimetype, lang, submitter, format );
        // Pair< CargoObjectInfo, InputStream > data_pair = new Pair< CargoObjectInfo, InputStream >( coi, data );
        // List< Pair< CargoObjectInfo, InputStream> > indata = new ArrayList< Pair< CargoObjectInfo, InputStream > >();
        // indata.add( data_pair );

        insertData( coi, readStream( data ) );
        log.debug( String.format( "Successfully inserted single InputStream into (deprecated) CargoContainer." ) );
    }
    

    /**
     * Constructor for the CargoContainer class. The constructor
     * recieves a java.util.List with one or more
     * dk.dbc.opensearch.common.types.Pair<CargoObjectInfo,
     * InputStream> as argument and converts the InputStreams to an
     * internal more traversable datatype. 
     * 
     * @param data the Pair of CargoObjectInfo(s) and InputStream(s)
     * to be inserted into the CargoContainer
     * 
     * @throws IOExceptionException if one of the supplied streams
     * cannot be read or is closed during reading
     * @throws IllegalArgumentException in one of the cases where: the
     * mimetype is unknown, the language is unknown, the submitter is
     * unknown or the CargoContainer already contains a datastream
     * with the exact same identifiers
     * @throws IllegalStateException if the insertion of the data
     * could not be carried through
     */
    public CargoContainer() 
    {
    	// do nothing.
    }
    
    
    public void add( String format, String submitter, String language, String mimetype, InputStream data ) throws IOException
    {
    	CargoObject co = new CargoObject( mimetype, language, submitter, format, data );    	
    	this.data.add( co );    	
    }
    
    
    public List< Byte > getContent( String format)
    {
    	List< Byte > ret = null;
    	
    	for ( CargoObject co : data )
    	{
    		if( co.getData().getFirst().getFormat().equals( format ) )
    		{	
    			ret = co.getData().getSecond();
    		}
    	}
    	
    	return ret;
    }

    
    public CargoContainer( List< Pair< CargoObjectInfo, InputStream > > data ) throws IOException, 
                                                                                      IllegalArgumentException, 
                                                                                      IllegalStateException
    {  
    	for ( Pair< CargoObjectInfo, InputStream > p : data )
    	{    		
            CargoObjectInfo coi = p.getFirst();
            String mimetype = coi.getMimeType();
            if ( ! checkMimeType( mimetype ) ) 
            {
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
            insertData( coi, tmp );

            log.debug( String.format( "Successfully inserted data into CargoContainer" ) );
    	}
    }


    /**
     * Helper method that inserts the pair CargoObjectInfo, List<Byte>
     * into the CargoContainers internal data representation format
     * 
     * @param coi The CargoObjectInfo object which will serve as the key for the data
     * @param lb The data (in the form of a byte array) that is to be stored
     * 
     * @throws IllegalArgumentException if the CargoContainer already contains the data of lb
     */
    private void insertData( CargoObjectInfo coi, List<Byte> lb ) throws IllegalArgumentException
    {
        for ( Pair< CargoObjectInfo, List< Byte > > map : data_list )
        {
            if ( map.getFirst().equals( coi ) )
            {
                /** \todo: should we just insert the key, value and log the duplicate instead of excepting?*/
                log.fatal( String.format( "The CargoContainer already contains an exact match of the data ( format: %s, submitter: %s, mimetype: %s )", coi.getMimeType(), coi.getLanguage(), coi.getFormat() ) );
                throw new IllegalArgumentException( String.format( "The CargoContainer already contains an exact match of the data ( format: %s, submitter: %s, mimetype: %s )", coi.getMimeType(), coi.getLanguage(), coi.getFormat() ) );
                
            }

            Pair< CargoObjectInfo, List< Byte > > pair = new Pair< CargoObjectInfo, List< Byte > >( coi, lb );
            data_list.add( pair );
        }
    }


    /**
     * Helper method that reads all the bytes from the submitted
     * InputStream into a List<Byte> datatype
     * 
     * @param is the InputStream containing the bytestream
     * @returns a List<Byte> containing the bytearray
     * @throws IOException if the stream could not be read or was
     * closed during reading
     */
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
    // public CargoContainer( InputStream data, String dublinCore, String submitter, String format) throws ParserConfigurationException, SAXException, IOException, IllegalArgumentException
    // {
    //     // read data
    //     if( data.available() > 0 )
    //     {
    //         this.data = new BufferedInputStream( data );
    //     }
        
    //     dublinCoreMetadata = dublinCore;
        
    //     // 10: isolate and validate used dublin core fields
    //     DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    //     Document doc = db.parse(new InputSource(new StringReader( dublinCore )));

    //     NodeList langCand = doc.getElementsByTagName( "dc:language" );
    //     String language = null;
    //     for( int i = 0; i < langCand.getLength() ; i++ ){
    //         Element tmpElem = (Element) langCand.item( i );
    //         String cand =  tmpElem.getFirstChild().getNodeValue();
    //         language = cand;
    //         break;            
    //     }        
    //     if ( language == null){
    //         throw new IllegalArgumentException( "No language specifier found" );
    //     }

    //     NodeList mimeCand = doc.getElementsByTagName( "dc:format" );
    //     String mimeType = null;
    //     String candMime = null;
    //     for( int i = 0; i < mimeCand.getLength() ; i++ ){
   
    //         Element tmpElem = (Element) mimeCand.item( i );
    //         candMime =  tmpElem.getFirstChild().getNodeValue();
    //         if ( checkMimeType( candMime ) ){
    //             mimeType = candMime;
    //             break;
    //         }
    //     }
        
    //     // TODO: make getMime method

    //     if ( mimeType == null){
    //         throw new IllegalArgumentException( String.format( "Mimetype '%s' not supported ", candMime ) );
    //     }
    //     CargoMimeType CMT = null;
    //     for (CargoMimeType cmt : CargoMimeType.values() ){
    //         if( mimeType.equals( cmt.getMimeType() ) ){
    //             log.debug( String.format( "Identified mimetype %s", mimeType ) );
    //             CMT = cmt;
    //         }
    //     }

    //     System.out.println( "MIMETYPE '"+mimeType+"'" );
    //     System.out.println( "LANGUAGE '"+language+"'" );
                
    //     contentLength = this.data.available();
        
    //    log.debug( String.format( "Identified streamlength %s", contentLength ) );

    //    coi = new CargoObjectInfo( CMT, language, submitter, format, contentLength );
    // }

    
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
     * Get data as a byte array given a key that identifies the data
     *
     * @returns the internal data representation as a byte[]
     *
     * @throws IOException if the CargoContainer data could not be written to the byte[]
     * @throws NullPointerException if the pointer to the internal representation was corrupted
     */
    public byte[] getDataBytes( CargoObjectInfo key ) throws IOException, NullPointerException
    {

        int srcLen = getContentLength( key );

        log.info( String.format( "Constructing byte[] with length %s", srcLen ) );

        byte[] ba = new byte[ srcLen ];

        List<Byte> srcData = null;
        for ( Pair< CargoObjectInfo, List<Byte> > map : data_list )
        {
            if ( map.getFirst().equals( key ) ){
                srcData = map.getSecond();
            }
        }
        if ( srcData == null )
        {
            log.fatal( String.format( "Could not look up data from key %s", key.toString() ) );
            throw new NullPointerException( String.format( "CargoContainer does not contain data based on key: %s", key.toString() ) );
        }
        System.arraycopy( srcData, 0, ba, 0, srcLen );

        log.debug( String.format( "Returning bytearray" ) );

        return ba;
    }

    
    /**
     * Get the InputStream returned as a bytearrayoutputstream
     *
     * @returns the internal data represented as a ByteArrayOutputStream
     *
     * @throws IOException if the data could not be written to the ByteArrayOutputStream
     * @throws NullPointerException if the pointer to the internal representation was corrupted
     */
    // public ByteArrayOutputStream getDataBAOS() throws IOException, NullPointerException
    // {
    //     ByteArrayOutputStream bos = new ByteArrayOutputStream();
    //     BufferedInputStream in = getData();

    //     // try {
    //     int c = in.read();
    //     while(c != -1) {
    //         bos.write(c);
    //         c = in.read();
    //     }
    //     // }catch( IOException ioe ){
    //     //     log.fatal( String.format( "Could not write CargoContainer data to byte array. The data might be corrupted:\n %s", ioe.toString() ) );
    //     //     throw new IOException( ioe );
    //     // }catch( NullPointerException npe){
    //     //     log.fatal( String.format( "Tried to read data from empty pointer. Data in pointer was %s", npe.toString() ) );
    //     // }

    //     return bos;

    // }

    
    // \todo: this method is a dummy
    public int getContentLength()
    {    	
    	return -1;
    }

    /**
     * Returns the length of of the data associated with the CargoObjectInfo identified by `key`
     *
     * @returns The size of the byte array.
     */
    public int getContentLength( CargoObjectInfo key ) 
    {
        int ret_val = 0;
        for ( Pair< CargoObjectInfo, List<Byte> > map : data_list )
        {
            if ( map.getFirst().equals( key ) )
            {
                ret_val =  map.getSecond( ).size();
            }
        }
        return ret_val;
    }

    
    public int getItemsCount(){
        return data_list.size();
    }

    
    public ArrayList< Pair< CargoObjectInfo, List<Byte > > > getDataLists(){
        return data_list;
    }
    
    
    public ArrayList< CargoObject > getData()
    {
    	return data;
    }

    /**
     * Returns the mimetype of the data associated with the CargoObjectInfo 
     *
     * @returns the mimetype of the data as a string
     */
    public String getMimeType( ){
        return coi.getMimeType();
    }

    /**
     * Returns the name of the submitter of the data associated with the CargoObjectInfo 
     *
     * @returns the submitter as a string
     */
    public String getSubmitter( CargoObjectInfo key ){
        return coi.getSubmitter();
    }


    /**
     * Returns the format of the data associated with the CargoObjectInfo 
     *
     * @returns the format as a string
     */
    public String getFormat( CargoObjectInfo key ){
        return coi.getFormat();
    }
    
    
    // \todo: this is a dummy method. to be moved to CargoObject.
    public String getFormat()
    {
    	return "to be moved to CargoObject";
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