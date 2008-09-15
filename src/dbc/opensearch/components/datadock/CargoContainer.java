package dbc.opensearch.components.datadock;

// import java.util.Date;
// import java.util.Calendar;

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
//import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;

import org.apache.log4j.Logger;
/**
 * CargoContainer is a datastructure used throughout OpenSearch for
 * carrying the informations submitted for indexing. CargoContainer
 * Handles the verification of the submitted datatypes as well as
 * adding additional information used by the OpenSearch components
 * when dealing with the data.
 */
public class CargoContainer {

    /** object to hold information about the data*/
    private CargoObjectInfo coi;

    /** date when the container was constructed*/
    private final long date;

    /** holder for the data (InputStream supports estimations through .available()) */
    private BufferedInputStream data;


    /** the length of the InputStream */
    private final int contentLength;

    Logger log = Logger.getLogger("CargoContainer");

    /**
     * Constructor for the CargoContainer class. Creates an instance
     * based on a string representation of the data to be submitted
     * into the OpenSearch repository and indexing mechanism
     * (dbc.opensearch.components.pti) and associated metadata in a
     * CargoMetadata container
     *
     * \see dbc.opensearch.components.pti
     *
     * @params data: The incoming data represented as a
     * ByteArrayInputStream (or any stream that have a count method )
     * @params mime: The mimetype of the data. Must conform to the allowed mimetypes
     * @params lang: the language used in the data
     * @params submitter: the submitter of the data, for authentication purposes
     * @throws java.io.IOException if the stream is corrupted
     */
    public CargoContainer( InputStream data, String mime, String lang, String submitter ) throws IOException{
        // 05: get the stream into the object
        if( data.available() > 0 ){
            this.data = new BufferedInputStream( data );
        }else{
            throw new NullPointerException( "Refusing to construct a cargocontainer without cargo" );
        }
        
        // 07: timestamp the container
        date = System.currentTimeMillis();
        // 10: check mimetype
        /** \todo: How do we specify the allowed mimetypes? enums? config? 
         *
         * Seeing that we need a xmlcontentconverter and a
         * modification of mappings each time we need to handle a new
         * mimetype, we already need to rebuild the entire project
         * anyway. This lends itself to specifying the mimetypes and
         * languages through enums.
         */
        // 30: check language
        /** \todo: How to specify allowed languages? enums? db? */
        // 35: check submitter (credentials checking)
        /** \todo: need better checking of values (perhaps using enums?) before constructing */
        // 40: get stream length
        // available returns (count - pos), both of which are private
        /** \todo: Make _absolutely_ sure that .available() returns
            the total, always.  

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
        try{
            contentLength = this.data.available();
            //            contentLength = this.data.available();
        } catch( IOException ioe ){
            log.fatal( String.format( "Unable to read from the InputStream: %s", ioe.toString() ) );
            throw new IOException( ioe );
        }

        // 50: construct CargoObjectInfo object

        coi = new CargoObjectInfo( mime, lang, submitter, contentLength );

    }
    
    /**
     * @returns true if name is found in submitter-list, false otherwise
     */
    public boolean checkSubmitter( String name ) throws IllegalArgumentException{
        if ( name != "stm"){
            throw new IllegalArgumentException( String.format( "no submitter goes by the name of %s", name ) );
        }else{
            return true;
        }
    }

    /**
     * @returns true if mimetype is allowed in OpenSearch, false otherwise
     */
    public boolean checkMimeType( String mimetype ) throws IllegalArgumentException{
        if( mimetype != "text/xml" ) {
            throw new IllegalArgumentException( String.format( "no mimetype goes by the name of %s", mimetype ) );
        }
        else {
            return true;
        }
    }
    /**
     *@returns true if language is alowed in Opensearch, otherwise false
     * \todo: Find the right language codes for Danish and English     
     */
    public boolean checkLanguage(String lang){
        if ("dk" != lang.toLowerCase() ||
            "eng" != lang.toLowerCase()){
            return false;
        }
        return true;   
    }

    /**
     * @returns the jave.util.Date (in milliseconds) for when the 
     */
    public long getDate(){
        return this.date;
    }

    /**
     * @return the data of the container-object as an BufferedInputStream
     */
    public BufferedInputStream getData(){
        return this.data;
    }

    /**
     * Get the InputStream returned as a byte array
     */
    public byte[] getDataBytes() throws IOException{
        
        log.info( String.format( "Constructing byte[] with length %s", this.contentLength ) );
        
        byte[] ba = new byte[ this.contentLength ];

        ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
        BufferedInputStream in = getData();       
        
        try{
            in.mark( getStreamLength() );
            in.read(ba, 0, getStreamLength() );
            bos.write(ba, 0, getStreamLength() );
        }catch( IOException ioe ){
            log.fatal( String.format( "Could not write CargoContainer data to byte array. The data might be corrupted:\n %s", ioe.toString() ) );
            throw new IOException( ioe );
        }catch( NullPointerException npe){
            log.fatal( String.format( "Tried to read data from empty pointer. Data in pointer was %s", npe.toString() ) );
        }
        finally{
            in.reset();
        }
        log.debug( String.format( "Returning bytearray" ) );
        return bos.toByteArray();    

}

    /**
     * Get the InputStream returned as a bytearrayoutputstream
     */
    public ByteArrayOutputStream getDataBAOS() throws IOException{
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
        BufferedInputStream in = getData();       
        
        try {
            int c = in.read();
            while(c != -1) {
                bos.write(c);
                c = in.read();
            }
        }catch( IOException ioe ){
            log.fatal( String.format( "Could not write CargoContainer data to byte array. The data might be corrupted:\n %s", ioe.toString() ) );
            throw new IOException( ioe );
        }catch( NullPointerException npe){
            log.fatal( String.format( "Tried to read data from empty pointer. Data in pointer was %s", npe.toString() ) );
        }
        
        return bos;

}

    /**
     * @return the length of the data-stream
     */
    public int getStreamLength() {
        return this.contentLength;
    }
    /**
     * @return the mimetype of the data as a string
     */    
    public String getMimeType(){
        return this.coi.getMimeType();
    }
    /**
     *@return the submitter as a string
     */
    public String getSubmitter(){
        return this.coi.getSubmitter();
    }

}