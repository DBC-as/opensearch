/**
 * \file CargoObjectInfo.java
 * \brief The CargoObjectInfo class
 * \package datadock
 */

package dbc.opensearch.components.datadock;
import java.util.Date;
/**
 * \ingroup datadock
 * \brief Holds the metadata for cargo
 */
public class CargoObjectInfo {

    private CargoMimeType mimeType;/** \see CargoMimeType */

    /** \todo: the language of the submitted data determines which analyzer
     * should be used in the indexing process, therefore we want full
     * control of allowed languages
     */
    private String language;

    /** \todo submitter is primarily thought as an authentication
     * prerequisite, it will probably change in time
     */
    private String submitter;

    private String format;

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
    public CargoObjectInfo ( CargoMimeType mimeType, String lang, String submitter, String format, int contentLength ) {

        this.mimeType = mimeType;

        this.language = lang;

        this.submitter = submitter;
        
        this.format = format;

        this.contentLength = contentLength;
        
        this.timestamp = new Date();

    }

    int getContentLength(){
        return contentLength;
    }

    long getTimestamp(){
        return timestamp.getTime();
    }
    String getMimeType(){
        return mimeType.getMimeType();
    }

    /**
     *@return submitter as string
     */
    public String getSubmitter(){
        return submitter;
    }
 
    /**
     *@return format as string
     */
    public String getFormat(){
        return format;
    }
}