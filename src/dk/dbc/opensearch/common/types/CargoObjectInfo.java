/**
 * \file CargoObjectInfo.java
 * \brief The CargoObjectInfo class
 * \package datadock
 */
package dk.dbc.opensearch.common.types;

import java.util.Date;
import dk.dbc.opensearch.common.types.CargoMimeType;
/**
 * \ingroup datadock
 * \brief Holds the metadata for cargo
 */
public class CargoObjectInfo 
{
    private CargoMimeType mimeType;/**< \see CargoMimeType */

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

    /** determines if the data associated with the CargoObjectInfo is indexable*/
    private boolean indexable;

    /** used to make statistics and estimates regarding the processtime of the dataobject */
    private Date timestamp; 

    /**
     * Constructs a CargoObjectInfo instance that acts as a container
     * for the metadata associated with a given CargoContainers
     * data. This constructor defaults the indexability of the data to
     * false
     *
     * @param mimeType The mimetype of the data
     * @param lang The language of the data
     * @param submitter The submitter of the data
     * @param format The format of the data
     * @param indexable true if the material can be indexed, false otherwise
     */
    public CargoObjectInfo ( CargoMimeType mimeType, String lang, String submitter, String format, boolean indexable )
    {
        this.mimeType = mimeType;

        this.language = lang;

        this.submitter = submitter;
        
        this.format = format;

        this.indexable = indexable;

        this.timestamp = new Date();
    } 
    
    
    /**
     * Constructs a CargoObjectInfo instance that acts as a container
     * for the metadata associated with a given CargoContainers
     * data. This constructor defaults the indexability of the data to
     * false
     *
     * @param mimeType The mimetype of the data
     * @param lang The language of the data
     * @param submitter The submitter of the data
     * @param format The format of the data
     */
    public CargoObjectInfo ( CargoMimeType mimeType, String lang, String submitter, String format ) 
    {
        this( mimeType, lang, submitter, format, false );
    }

    /**
     * Returns this CargoContainers timestamp
     *
     * @returns the timestamp of the CargoContainer
     */
    public long getTimestamp()
    {
        return timestamp.getTime();
    }
    
    
    /**
     * Returns the mimetype
     *
     * @returns the mimetype of the data as a string
     */
    public String getMimeType()
    {
        return mimeType.getMimeType();
    }

    
    /**
     * Returns the name of the submitter
     *
     * @returns submitter as string
     */
    public String getSubmitter()
    {
        return submitter;
    }
 
    
    /**
     * Returns the format
     *
     * @returns format as string
     */
    public String getFormat()
    {
        return format;
    }


    /**
     * @returns an indication of the indexability of the data
     * associated with the CargoObjectInfo object
     */
    public boolean isIndexable(){
        return indexable;
    }

    /**
     * @returns the language
     */
    public String getLanguage() {
        return language;
    }
}