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
 * \file CargoObjectInfo.java
 * \brief The CargoObjectInfo class
 * \package datadock
 */
package dk.dbc.opensearch.common.types;


import java.util.Date;

import org.apache.log4j.Logger;


/**
 * \brief Holds the metadata for CargoObject that are contained in a CargoContainer
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

    /** unique identifier of the CargoObject*/
    private long id;

    /**
     * Constructs a CargoObjectInfo instance that acts as a container
     * for the metadata associated with a given CargoContainers
     * data. 
     * See the CargoObject constructor for implementation documentation
     */
    CargoObjectInfo( DataStreamType dataStreamName, 
                     CargoMimeType mimeType, 
                     String lang,
                     String submitter, 
                     String format,
                     long id )
    {
        log.debug( String.format( "Entering CargoObjectInfo" ) );
        assert( dataStreamName != null && format != null && lang != null && mimeType != null && submitter != null  && id != 0 );
        this.dataStreamName = dataStreamName;
        this.format = format;
        this.language = lang;
        this.mimeType = mimeType;
        this.submitter = submitter;
        this.timestamp = new Date();
        this.id = id;
    }
    

    long getId()
    {
        return id;
    }


    boolean checkLanguage( String language )
    {
        throw new UnsupportedOperationException( "this method has yet to be implemented" );
        /** \todo: implement real check of language*/
        // return true;
    }


    boolean validMimetype( String mimetype )
    {
        return CargoMimeType.validMimetype( mimetype );
    }


    boolean validSubmitter( String name )
    {
        throw new UnsupportedOperationException( "this method has yet to be implemented" );
        /** \todo: implement real check of submitter*/
        // return true;
    }


    long getTimestamp()
    {
        return timestamp.getTime();
    }


    String getMimeType()
    {
        return mimeType.getMimeType();
    }


    String getSubmitter()
    {
        return submitter;
    }


    String getFormat()
    {
        return format;
    }


    String getLanguage()
    {
        return language;
    }

    
    DataStreamType getDataStreamType()
    {
        return dataStreamName;
    }
}