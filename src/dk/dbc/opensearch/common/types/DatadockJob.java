/**
 * \file DatadockJob.java
 * \brief The DatadockJob class
 * \package types;
 */

package dk.dbc.opensearch.common.types;


import java.net.URI;
import org.apache.log4j.Logger;


/**
 * The purpose of the datadockJob is to hold the information about a
 * job for the datadock. it provides get and set methods
 */
public class DatadockJob 
{    
    Logger log = Logger.getLogger( DatadockJob.class );
 
    private URI uri;
    private String submitter;
    private String format;
    private String PID;

    
    /**
     * Constructor. initializes the DatadockJob
     * 
     * @param path The path to the job
     * @param submitter The submitter of the Job
     * @param format The format of the Job
     */
    public DatadockJob( URI uri, String submitter, String format) 
    {
        log.debug( String.format( "Constructor( uri='%s', submitter='%s', format='%s' ) called", uri.getRawPath(), submitter, format ) );
        
        this.uri = uri;
        this.submitter = submitter;
        this.format = format;
        PID = "";
    }
    
    
    /**
     * Constructor. initializes the DatadockJob
     * 
     * @param path The path to the job
     * @param submitter The submitter of the Job
     * @param format The format of the Job
     * @param PID the fedoraPID for the job
     */
    public DatadockJob( URI uri, String submitter, String format, String PID ) 
    {
        log.debug( String.format( "Constructor( uri='%s', submitter='%s', format='%s', PID='%s' ) called", 
                                  uri.getRawPath(), submitter, format, PID ) );
        this.uri = uri;
        this.submitter = submitter;
        this.format = format;
        this.PID = PID;
    }
    
    
    /**
     * Gets the uri object from the job
     * @return The URI of the job
     */
    public URI getUri()
    {
        return uri;
    }
    
    
    /**
     * Gets the submitter
     * @return The submitter
     */
    public String getSubmitter()
    {
        return submitter;
    }
    
    
    /**
     * Gets the format
     * @return The format
     */
    public String getFormat()
    {
        return format;
    }

    
    /**
     * Gets the PID from the job
     * @return The PID of the job
     */
    public String getPID()
    {
        return PID;
    }
    
    
    /**
     * Sets the path
     * @param The path 
     */
    public void setUri( URI uri )
    {
        log.debug( String.format( "setUri( uri='%s' ) called", uri.getRawPath() ) ); 
           this.uri = uri;
    }
    
    
    /**
     * Sets the submitter
     * @param The submitter
     */
    public void setSubmitter( String submitter )
    {
        log.debug( String.format( "setSubmitter( submitter='%s' ) called", submitter ) ); 
        this.submitter = submitter;
    }

    
    /**
     * Sets the format
     * @param The format 
     */
    public void setFormat( String format )
    {
        log.debug( String.format( "setFormat( format='%s' ) called", format ) );
        this.format = format;
    }
    
    
    /**
     * Sets the PID
     * @param The PID 
     */
    public void setPID( String PID )
    {
        log.debug( String.format( "setPID( PID='%s' ) called", PID ) );
        this.PID = PID;
    }
}
