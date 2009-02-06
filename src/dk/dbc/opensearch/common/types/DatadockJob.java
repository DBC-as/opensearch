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
 * job for the datadock. it provides get and set method
 */
public class DatadockJob {
    
    Logger log = Logger.getLogger("DatadockJob");
    private URI path;
    private String submitter;
    private String format;

    /**
     * Constructor. initializes the DatadockJob
     * 
     * @param path The path to the job
     * @param submitter The submitter of the Job
     * @param format The format of the Job
     */
    public DatadockJob( URI path, String submitter, String format) {
        log.debug( String.format( "Constructor( path='%s', submitter='%s', format='%s' ) called", 
                                  path.getRawPath(), submitter, format ) );
        this.path = path;
        this.submitter = submitter;
        this.format = format;
    }
    
    /**
     * Gets the path
     * @return The path 
     */
    public URI getPath(){
        return path;
    }
    
    /**
     * Gets the submitter
     * @return The submitter
     */
    public String getSubmitter(){
        return submitter;
    }
    
    /**
     * Gets the format
     * @return The format
     */
    public String getFormat(){
        return format;
    }

    /**
     * Sets the path
     * @param The path 
     */
    public void setPath( URI path){
        log.debug( String.format( "setPath( path='%s' ) called", path.getRawPath() ) ); 
           this.path = path;
    }
    
    /**
     * Sets the submitter
     * @param The submitter
     */
    public void setSubmitter( String submitter ){
        log.debug( String.format( "setSubmitter( submitter='%s' ) called", submitter ) ); 
        this.submitter = submitter;
    }

    /**
     * Sets the format
     * @param The format 
     */
    public void setFormat( String format ){
        log.debug( String.format( "setFormat( format='%s' ) called", format ) );
        this.format = format;
    }
}
