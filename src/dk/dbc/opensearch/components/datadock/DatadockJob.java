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
 * \file
 * \brief
 */


package dk.dbc.opensearch.components.datadock;


import org.w3c.dom.Document;
import org.apache.log4j.Logger;
import dk.dbc.opensearch.common.types.IIdentifier;
import dk.dbc.opensearch.common.types.IJob;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * The purpose of the datadockJob is to hold the information about a
 * job for the datadock.
 */
public class DatadockJob implements IJob
{
    private Logger log = Logger.getLogger( DatadockJob.class );

    private static final String DEFAULT_LANGUAGE_CODE = "da";
    
    private String submitter;
    private String format;
    private String language;

    private IIdentifier identifier;
    private Document referenceData;


    /**
     * Constructor that initializes the DatadockJob
     * @param identifier the identifier of the data of the job
     * @param referenceData the data concerning the job
     */
    public DatadockJob( IIdentifier identifier, Document referenceData )
    {
        this.identifier = identifier;
        this.referenceData = referenceData;

        initValuesFromReferenceData();
    }

    /**
     * Gets the submitter
     * @return The submitter
     */
    public String getSubmitter()
    {
        return this.submitter;
    }


    /**
     * Gets the format
     * @return The format
     */
    public String getFormat()
    {
        return this.format;
    }

    /**
     * Gets the language
     * @return The language
     */
    public String getLanguage()
    {
	return this.language;
    }


    /**
     * Gets the identifier from the job
     * @return the identifier of the job
     */
    @Override
    public IIdentifier getIdentifier()
    {
        return identifier;
    }


    /**
     * gets the reference data (typically metadata) from the job. When we start
     * using JavaScript for business logic, this is to be used instead of the
     * field accessors
     */
    @Override
    public Document getReferenceData()
    {
        return referenceData;
    }

    
    private void initValuesFromReferenceData()
    {
        if ( this.referenceData == null )
        {
            String error = "ReferenceData is empty or null. Aborting";
            log.error( error );
            throw new IllegalStateException( error );
        }

        NodeList elementSet = this.referenceData.getElementsByTagName( "es:info" );

        if ( elementSet.getLength() == 0 )
        {
            elementSet = this.referenceData.getElementsByTagName( "info" );
            if ( elementSet.getLength() == 0 )
            {

                String error = "Failed to get either Document Element named 'info' or 'es:info' from referencedata";
                log.error( error );
                throw new IllegalArgumentException( error );
            }
        }
        
        Node info = elementSet.item( 0 );
        NamedNodeMap attributes = info.getAttributes();
        this.format = attributes.getNamedItem( "format" ).getNodeValue();
        this.submitter = attributes.getNamedItem( "submitter" ).getNodeValue();

	// If node "lang" is non-existing or empty, set it to a default value, otherwise set its correct value. 
	if ( attributes.getNamedItem( "lang" ) != null )
        {
	    String lang = attributes.getNamedItem( "lang" ).getNodeValue();
	    this.language =  lang.isEmpty() ? DEFAULT_LANGUAGE_CODE : lang;
	}
	else
        {
	    this.language = DEFAULT_LANGUAGE_CODE;
	}
    }
}
