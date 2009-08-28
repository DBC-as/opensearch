/*
   
This file is part of opensearch.
Copyright © 2009, Dansk Bibliotekscenter a/s, 
Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043

opensearch is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

opensearch is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * \file DatadockJob.java
 * \brief
 */


package dk.dbc.opensearch.components.datadock;


import java.net.URI;
import org.apache.log4j.Logger;
import dk.dbc.opensearch.components.harvest.IIdentifier; 


/**
 * The purpose of the datadockJob is to hold the information about a
 * job for the datadock. it provides get and set methods
 */
public class DatadockJob implements Comparable<DatadockJob>
{    
    Logger log = Logger.getLogger( DatadockJob.class );
 
    //    private URI uri;
    private String submitter;
    private String format;
    private String PID;
    private IIdentifier identifier;
    private byte[] referenceData;    


    /**
     * Constructor that initializes the DatadockJob
     * @param submitter, the submitter of the job
     * @param format, the format of the job
     * @param identifier, the identifier of the data of the job
     * @param referenceData, the data concerning the job
     */

    public DatadockJob( String submitter, String format, IIdentifier identifier, byte[] referenceData )
    {
        this.submitter = submitter;
        this.format = format;
        this.identifier = identifier;
        this.referenceData = referenceData;

        log.debug( String.format( "Constructor submitter= %s format= %s indentifier = %s", submitter, format, identifier ) );
    }

    /**
     * Constructor. initializes the DatadockJob
     * 
     * @param path The path to the job
     * @param submitter The submitter of the Job
     * @param format The format of the Job
     */
//     public DatadockJob( URI uri, String submitter, String format) 
//     {
//         log.debug( String.format( "Constructor( uri='%s', submitter='%s', format='%s' ) called", uri.getRawPath(), submitter, format ) );
        
//         this.uri = uri;
//         this.submitter = submitter;
//         this.format = format;
//         PID = "";
//     }
    
    
    /**
     * Constructor. initializes the DatadockJob
     * 
     * @param path The path to the job
     * @param submitter The submitter of the Job
     * @param format The format of the Job
     * @param PID the fedoraPID for the job
     */
//     public DatadockJob( URI uri, String submitter, String format, String PID ) 
//     {
//         log.debug( String.format( "Constructor( uri='%s', submitter='%s', format='%s', PID='%s' ) called", 
//                                   uri.getRawPath(), submitter, format, PID ) );
//         this.uri = uri;
//         this.submitter = submitter;
//         this.format = format;
//         this.PID = PID;
//     }
    
    
     public int compareTo( DatadockJob datadockJob){
         int result = identifier.compareTo( datadockJob.getIdentifier() ); 
         return result;


     }
    /**
     * Gets the uri object from the job
     * @return The URI of the job
     */
//     public URI getUri()
//     {
//         return uri;
//     }
    
    
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
     * Gets the identifier from the job
     * @returns the identifier of the job
     */

    public IIdentifier getIdentifier()
    {
        return identifier;
    }

    /**
     * gets the DCData from the job
     */
    public byte[] getReferenceData()
    {
        return referenceData;
    }
    
    /**
     * Sets the path
     * @param The path 
     */
//     public void setUri( URI uri )
//     {
//         log.debug( String.format( "setUri( uri='%s' ) called", uri.getRawPath() ) ); 
//            this.uri = uri;
//     }
    
    
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

    /**
     * Sets the Identifier
     * @param the Identifier
     */
    public void setIdentifier( IIdentifier identifier )
    {
        log.debug( "setIdentifier called" );
        this.identifier = identifier;
    }
}
