package dbc.opensearch.components.datadock;

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
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

    /** holder for the data (InputStream supports estimations through .available()) */
    private BufferedInputStream data;

    private long timestamp;

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
    public CargoContainer( InputStream data, String mime, String lang, String submitter ) throws IOException, IllegalArgumentException{

        log.debug( String.format( "Entering CargoContainer constructor" ) );
        // 05: get the stream into the object
        if( data.available() > 0 ){
            this.data = new BufferedInputStream( data );
        }else{
            log.fatal( String.format( "No data in inputstream, refusing to construct empty CargoContainer" ) );
            throw new NullPointerException( "Refusing to construct a cargocontainer without cargo" );
        }

        log.debug( String.format( "Setting timestamp" ) );

        
        // 10: check mimetype
        CargoMimeType CMT = null;
        log.debug( String.format( "checking mimetype: %s", mime ) );
        for (CargoMimeType cmt : CargoMimeType.values() ){
            if( mime.equals( cmt.getMimeType() ) ){
                log.debug( String.format( "mimetype %s validated", mime ) );
                CMT = cmt;
            }
        }
        if( CMT == null ){
            throw new IllegalArgumentException( String.format( "no mimetype goes by the name of %s", mime ) );
        }

        // 30: check language
        /** \todo: How to specify allowed languages? enums? db? */
        if( !checkLanguage( lang ) ){
            log.fatal( String.format( "Language '%s' not in list of allowed languages", lang ) );
            throw new IllegalArgumentException( String.format( "%s is not in the languagelist", lang ) );
        }

        // 35: check submitter (credentials checking)
        /** \todo: need better checking of values (perhaps using enums?) before constructing */
        if( !checkSubmitter( submitter ) ){
            log.fatal( String.format( "Submitter '%s' not in list of allowed submitters", submitter ) );
            throw new IllegalArgumentException( String.format( "%s is not in the submitterlist", submitter ) );
        }

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
        // try{
        contentLength = this.data.available();
        // } catch( IOException ioe ){
        //     log.fatal( String.format( "Unable to read from the InputStream: %s", ioe.toString() ) );
        //     throw new IOException( ioe );
        //   }

        // 50: construct CargoObjectInfo object
        coi = new CargoObjectInfo( CMT, lang, submitter, contentLength );
        
        log.debug( String.format( "All Checks passed, CargoContainer constructed with values %s, %s, %s, %s", this.getStreamLength(), this.getMimeType(), lang, this.getSubmitter() ) );
        
    }
    /**
     * @returns true if name is found in submitter-list, false otherwise
     */
    public boolean checkSubmitter( String name ) throws IllegalArgumentException{
        /** \todo: FIXME: Hardcoded values for allowed submitters */
        return true;
    }

    /**
     * @returns true if mimetype is allowed in OpenSearch, false otherwise
     */
    public boolean checkMimeType( String mimetype ){
        CargoMimeType CMT = null;
        log.debug( "checking mimetype" );
        for (CargoMimeType cmt : CargoMimeType.values() ){
            if( mimetype.equals( cmt.getMimeType() ) ){
                CMT = cmt;
            }
        }
        if( CMT == null ){
            return false;
        }
        return true;

    }
    /**
     *@returns true if language is allowed in Opensearch, false otherwise
     */
    public boolean checkLanguage(String lang){
        return true;
    }

    /**
     * @returns the data of the container-object as an BufferedInputStream
     */
    public BufferedInputStream getData(){
        return this.data;
    }

    /**
     * Get the InputStream returned as a byte array
     * @returns the internal data representation as a byte[]
     * @throws IOException if the CargoContainer data could not be written to the byte[]
     * @throws NullPointerException if the pointer to the internal representation was corrupted
     */
    public byte[] getDataBytes() throws IOException, NullPointerException{

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
        }finally{
            in.reset();
        }
        log.debug( String.format( "Returning bytearray" ) );
        return bos.toByteArray();

    }

    /**
     * Get the InputStream returned as a bytearrayoutputstream
     * @returns the internal data represented as a ByteArrayOutputStream
     * @throws IOException if the data could not be written to the ByteArrayOutputStream
     * @throws NullPointerException if the pointer to the internal representation was corrupted
     */
    public ByteArrayOutputStream getDataBAOS() throws IOException, NullPointerException{

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
     * @returns the length of the data-stream
     */
    public int getStreamLength() {
        return contentLength;
    }
    /**
     * @returns the mimetype of the data as a string
     */
    public String getMimeType(){
        return coi.getMimeType();
    }
    /**
     *@returns the submitter as a string
     */
    public String getSubmitter(){
        return coi.getSubmitter();
    }

    /** \todo: needs unittest */

    /**
     * @returns the timestamp of the CargoContainer
     */
    public long getTimestamp(){
        return timestamp;
    }

    /** \todo: needs unittest */

    /**
     * Sets a timestamp on the cargocontainer. This is not reflecting
     * when the CargoContainer was initialized, but is solely up to
     * the client
     */
    public void setTimestamp(){
        timestamp = System.currentTimeMillis();
    }

}