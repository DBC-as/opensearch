/**
 * \file CargoObjectInfo.java
 * \brief The CargoObjectInfo class
 * \package datadock
 */
package dk.dbc.opensearch.common.types;

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


import dk.dbc.opensearch.common.types.CargoMimeType;

import java.util.Date;

import org.apache.log4j.Logger;


/**
 * \ingroup datadock
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
    
    private IndexingAlias alias;

    /**
     * @deprecated users of the CargoObjectInfo API should now pass
     * IndexingAlias and ids along as well. This constructor passes null value for id and default value (Article) IndexingAlias
     */
    @Deprecated
    CargoObjectInfo( DataStreamType dataStreamName, 
                     CargoMimeType mimeType, 
                     String lang, 
                     String submitter, 
                     String format )
    {
        this( dataStreamName, 
              mimeType, 
              lang, 
              submitter, 
              format, 
              IndexingAlias.Article,
              0 );
        log.warn( "Passing default values: (0) for id and (Article) for IndexingAlias" );
    }

    /** 
     * 
     * 
     * @param dataStreamName 
     * @param mimeType 
     * @param lang 
     * @param submitter 
     * @param format 
     * @param id 
     * 
     * @deprecated passes default values for id and IndexingAlias
     */
    @Deprecated
    CargoObjectInfo( DataStreamType dataStreamName, 
                     CargoMimeType mimeType, 
                     String lang, 
                     String submitter, 
                     String format, 
                     long id )
    {
        this( dataStreamName, 
                         mimeType, 
                         lang, 
                         submitter, 
                         format, 
                         IndexingAlias.Article,
                         id );
        log.warn( "Passing default value (Article) for IndexingAlias" );
    }

    /**
     * Constructs a CargoObjectInfo instance that acts as a container
     * for the metadata associated with a given CargoContainers
     * data. 
     *
     * @param dataStreamName the DataStreamType of the data
     * @param mimeType the mimetype of the data
     * @param lang the language of the data
     * @param submitter the submitter of the data
     * @param format the format of the data
     * @param alias the alias that should be used when indexing the data
     * @param id the id that identifies the data
     */
    CargoObjectInfo( DataStreamType dataStreamName, 
                     CargoMimeType mimeType, 
                     String lang,
                     String submitter, 
                     String format,
                     IndexingAlias alias,
                     long id )
    {
        log.debug( String.format( "Entering CargoObjectInfo" ) );
        assert( dataStreamName != null && format != null && lang != null && mimeType != null && submitter != null && alias != null && id != 0 );
        this.dataStreamName = dataStreamName;
        this.format = format;
        this.language = lang;
        this.mimeType = mimeType;
        this.submitter = submitter;
        this.timestamp = new Date();
        this.alias = alias;
        this.id = id;
    }

    /**
     * Returns the globally unique id of the CargoObject. This method
     * will not be exposed outside the package
     * 
     * @return the id of the CargoObject
     */
    long getId()
    {
        return id;
    }


    /**
     * Checks the validity if the language
     *
     *@returns true if language is allowed, false otherwise
     */
    boolean checkLanguage( String language )
    {
        /** \todo: implement real check of language*/
        return true;
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
     * @returns true if name is found in a list of submitters, false otherwise
     */
    boolean validSubmitter( String name ) throws IllegalArgumentException
    {
        /** \todo: implement real check of submitter*/
        return true;
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
     * setter for the indexingAlias
     */
    void setIndexingAlias( IndexingAlias indexingAlias )
    {
        alias = indexingAlias;
    }

    /**
     * getter for the indexing alias
     */
    IndexingAlias getIndexingAlias()
    {
        return alias;
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
    //          return DataStreamNames.getDataStreamNameFrom( name ).name;
    //    }
}