/**
 * \file CargoObjectInfo.java
 * \brief The CargoObjectInfo class
 * \package datadock
 */
package dk.dbc.opensearch.common.types;


import dk.dbc.opensearch.common.types.CargoMimeType;

import java.util.Date;

import org.apache.log4j.Logger;


/**
 * \ingroup datadock
 * \brief Holds the metadata for cargo
 */
public class CargoObjectInfo 
{
	Logger log = Logger.getLogger( CargoObjectInfo.class );
	
	/**
	 * Property naming type of data stream.
	 */
	private DataStreamType dataStreamName;
	
	private String format;

	/** \todo: the language of the submitted data determines which analyzer
     * should be used in the indexing process, therefore we want full
     * control of allowed languages
     */
    private String language;

    /** \see CargoMimeType */
    private CargoMimeType mimeType;

    /** \todo submitter is primarily thought as an authentication
     * prerequisite, it will probably change in time
     */
    private String submitter;

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
    CargoObjectInfo ( DataStreamType dataStreamName, CargoMimeType mimeType, String lang, String submitter, String format )
    {
    	this.dataStreamName = dataStreamName;
    	this.format = format;
    	this.language = lang;
    	this.mimeType = mimeType;        
        this.submitter = submitter;        
        this.timestamp = new Date();
    } 
    
    
    /**
     * Checks the validity if the language
     *
     *@returns true if language is allowed in Opensearch, false otherwise
     */
    boolean checkLanguage( String language )
    {
    	/** \todo: implement a proper check for valid languages */
    	if ( language.toLowerCase().equals( "da" ) )
    		return true;
    	else
    		return false;    		
    }

    
    /**
     * Checks the validity if the mimeType
     *
     * @returns true if mimetype is allowed in OpenSearch, false otherwise
     */
    boolean validMimetype( String mimetype )
    {
    	return CargoMimeType.validMimetype( mimetype );
    }
    
    
    /**
     * Checks the validity if the submitter
     *
     * @returns true if name is found in submitter-list, false otherwise
     */
    boolean checkSubmitter( String name ) throws IllegalArgumentException
    {
        /** \todo: FIXME: Hardcoded values for allowed submitters */
    	if ( name.toLowerCase().equals( "dbc" ) )
    		return true;
    	else
    		return false;
    }
    
    
    /**
     * Returns this CargoContainers timestamp
     *
     * @returns the timestamp of the CargoContainer
     */
    long getTimestamp()
    {
        return timestamp.getTime();
    }
    
    
    /**
     * Returns the mimetype
     *
     * @returns the mimetype of the data as a string
     */
    String getMimeType()
    {
        return mimeType.getMimeType();
    }

    
    /**
     * Returns the name of the submitter
     *
     * @returns submitter as string
     */
    String getSubmitter()
    {
        return submitter;
    }
 
    
    /**
     * Returns the format
     *
     * @returns format as string
     */
    String getFormat()
    {
        return format;
    }


    /**
     * @returns the language
     */
    String getLanguage() 
    {
        return language;
    }
    
    DataStreamType getDataStreamName(){
    	return dataStreamName;    	
    }
    
//    String getDataStreamNameFrom( String name )
//    {
//    	return DataStreamNames.getDataStreamNameFrom( name ).name;
//    }
}