package dbc.opensearch.components.datadock;
import java.util.Date;
/**
 * 
 */
public class CargoObjectInfo {

    private String mimeType;/** \see CargoMimeType */

    /** \todo: the language of the submitted data determines which analyzer
     * should be used in the indexing process, therefore we want full
     * control of allowed languages
     */
    private String language;

    /** \todo submitter is primarily thought as an authentication
     * prerequisite, it will probably change in time
     */
    private String submitter;

    /** contentlength is used in estimating the processing time of the
     * data. Contentlength is provided by datadock-reception, by the count method on the stream 
     * \todo streams are iterators, we need content-length before-hand
     */
    private int contentLength;
    private Date timestamp; /** used to make statistics and estimates regarding the processtime of the dataobject */
  
    /**
     * Constructs a CargoObjectInfo instance that acts as a container
     * for the metadata associated with a given CargoContainers data
     * \todo: should this constructor throw? And on what occasions?
     */
    public CargoObjectInfo ( String mimeType, String lang, String submitter, int contentLength ) {

        this.mimeType = mimeType;

        this.language = lang;

        this.submitter = submitter;
        
        this.contentLength = contentLength;
        
        this.timestamp = new Date();

    }

    int getContentLength(){
        return this.contentLength;
    }

    long getTimestamp(){
        return this.timestamp.getTime();
    }
    String getMimeType(){
        return this.mimeType;
    }
    /**
     *@return submitter as string
     */
    public String getSubmitter(){
        return this.submitter;
    }
    
}