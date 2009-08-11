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


package dk.dbc.opensearch.common.types;


import java.io.IOException;

import org.apache.log4j.Logger;


/**
 * \ingroup common.types
 * 
 * \brief CargoObject is a data structure used throughout OpenSearch,
 * which basically consists of a pair (common.types.Pair) of
 * CargoObjectInfo and a byte[]. This class is the access point
 * (through the CargoObjectInfo object) for information about the
 * input stream stored in the byte[]. It is used as a
 * complex type by the CargoContainer class.
 */
public class CargoObject
{
    private Logger log = Logger.getLogger( CargoObject.class );
	
    private final byte[] data;
    private final CargoObjectInfo coi;
    

    /**
     * Constructor for the CargoObject class. Here an object of the
     * type CargoMimeType is constructed, which in turn is used in the
     * construction of a CargoObjectInfoObject. Also, the InputStream
     * is read into a byte[] holding the actual data of the
     * object. The two are stored in a pair
     * (dk.dbc.opensearch.common.types.Pair).
     * 
     * The constructor will create an object id on the basis of the
     * hashcodes of the parts of the CargoContainer. This means that
     * two identical CargoObjects (i.e. two CargoContainers with
     * _exactly_ the same input data provided to the constructor) will
     * have the same id. 
     *
     * @param dataStreamName the DataStreamType of the data
     * @param mimeType the mimetype of the data
     * @param language the language of the data
     * @param submitter the submitter of the data
     * @param format the format of the data
     * @param alias the IndeingAlias that should be used when indexing the data
     * @param data the data to be stored in the CargoContainer
     * @throws IOException
     */
    CargoObject( DataStreamType dataStreamName, 
                 String mimetype, 
                 String language, 
                 String submitter,
                 String format,
                 IndexingAlias alias,
                 byte[] data ) throws IOException
    {
        CargoMimeType cmt = CargoMimeType.getMimeFrom( mimetype );
        /** \todo: fix hashcode generation to ensure uniqueness */
        long id = 0L;
        id += dataStreamName.hashCode();
        id += cmt.hashCode(); 
        id += language.hashCode(); 
        id += submitter.hashCode(); 
        id += format.hashCode(); 
        id += alias.hashCode(); 
        id += data.hashCode(); 
        
        log.trace( String.format( "id for CargoObject = %s", id ) );
        assert( id != 0L );
        
        coi = new CargoObjectInfo( dataStreamName, cmt, language, submitter, format, alias, id );
        
        this.data = data;
        log.trace( String.format( "length of data: %s", data.length ) );
    }

    
    /** 
     * Returns the globally unique id of the CargoObject 
     * 
     * @return the id of the CargoObject
     */
    public long getId()
    {
        return coi.getId();
    }
    
    
    /**
     * Checks if the language of the submitted data is allowed in a
     * CargoObject
     * 
     * @param language the language to be checked
     * @return True if language is allowed, False otherwise
     */
    public boolean checkLanguage( String language )
    {
        return coi.checkLanguage( language );
    }


    /**
     * Checks if the mimetype of the submitted data is allowed in a
     * CargoObject. The string should contain a mimetype conforming 
     * to the RFC 822 ( http://www.faqs.org/rfcs/rfc822.html)
     * 
     * @param mimetype the mimetype to be checked
     * @return True if mimetype is allowed, False otherwise
     */
    public boolean validMimetype( String mimetype )
    {
        return coi.validMimetype( mimetype );
    }


    /**
     * Checks if the submitter of the submitted data is allowed in a
     * CargoObject
     * 
     * @param submitter the submitter to be checked
     * @return True if mimetype is allowed, False otherwise
     */
    public boolean validSubmitter( String name ) throws IllegalArgumentException
    {
        return coi.validSubmitter( name );
    }


    /**
     * gets the name of the datastream (\see:
     * dk.dbc.opensearch.common.types.DataStreamNames)
     * 
     * @return the enum value of the name of the Datastream
     */
    public DataStreamType getDataStreamType()
    {
        return coi.getDataStreamType();
    }


    /** 
     * returns the IndexingAlias for the CargoObject.
     * The returned value will never be null
     * 
     * @return the IndexingAlias for the CargoObject
     */
    public IndexingAlias getIndexingAlias()
    {
        IndexingAlias ret_ia = coi.getIndexingAlias();
        return ret_ia;
    }


    /**
     * Gets the size of the underlying byte[].
     *
     * @return the size of the contents
     */
    public int getContentLength()
    {
        return data.length;
    }


    /** 
     * Returns the language of the submitted which the data stored in
     * the CargoObject. Language strings try (but are not obliged) to adhere to RFC 4646
     * http://www.rfc-editor.org/rfc/rfc4646.txt
     * 
     * No checks are made and no guarantees given
     * 
     * @return the language code as a String
     */
    public String getLang()
    {
        return coi.getLanguage();
    }


    /**
     * Gets the format (type of material) of the CargoObject
     * 
     * @return the format as a String
     */
    public String getFormat()
    {
        return coi.getFormat();
    }

   
    /**
     * Returns the mimetype of the data associated with the underlying
     * CargoObjectInfo
     *
     * @returns the mimetype of the data as a String
     */
    public String getMimeType()
    {
        return coi.getMimeType();
    }


    /**
     * Returns the name of the submitter of the data associated with
     * the underlying CargoObjectInfo
     *
     * @returns the submitter as a String
     */
    public String getSubmitter()
    {
        return coi.getSubmitter();
    }


    /**
     * Returns this CargoObject CargoObjectInfo's timestamp
     *
     * @returns the timestamp of the underlying CargoObjectInfo
     */
    public long getTimestamp()
    {
        return coi.getTimestamp();
    }


    /**
     * Returns the underlying data in the CargoObject as a byte[]
     * 
     * @return a byte[] containing the data of the CargoObject
     */
    public byte[] getBytes()
    {
        return data.clone();
    }
}