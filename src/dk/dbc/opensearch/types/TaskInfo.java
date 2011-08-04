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


package dk.dbc.opensearch.types;

import dk.dbc.opensearch.types.IIdentifier;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * The purpose of the TaskInfo is to hold information about a
 * job for the datadock. The TaskInfo is created from an XML refererence data
 * Document. The XML reference must look like this:
 * <pre>
 * {@code
 * <?xml version="1.0" encoding="UTF-8"?>
 *     <referencedata>
 *         <info submitter="123456" format="someFormat" language="se" mimetype="pdf"/>
 *     </referencedata>
 * }
 * </pre>
 * The XML currently lacks both an XSD and a namespace (concerning the lack of
 * namespace please see <A href="http://bugs.dbc.dk/show_bug.cgi?id=10681">bug#10681</A>)
 * Submitter and format in the above mentioned attributes in the info-tag are mandatory, whereas 
 * language and mimetype are optional. For further information about the attributes please see {@link #TaskInfo}
 */
public final class TaskInfo
{
    private Logger log = LoggerFactory.getLogger( TaskInfo.class );

    private static final String DEFAULT_LANGUAGE_CODE = "da";
    private static final String DEFAULT_MIMETYPE_CODE = "text/xml";
    
    private String submitter;
    private String format;
    private String language;
    private String mimetype;

    private final IIdentifier identifier;


    /**
     * Constructor that initializes the TaskInfo
     * As stated above, the referenceData must contain a specific XML Document.
     * In the XML Document an info-tag must be present, containing the following attributes:
     * "submitter", "format", "language", "mimetype".
     * "submitter" and "format" are mandatory and must contain values, whereas "language" and "mimetype" are optional.
     *
     * <ul>
     * <li> If no "language" attribute or an empty "language" attribute is given, 
     * the "language" value will default to "da".
     * <li> If no "mimetype" attribute or an empty "mimetype" attribute is given, 
     * the "mimetype" value will default to "text/xml".
     * </ul>
     *
     * @param identifier the identifier of the data of the job
     * @param referenceData the data concerning the job
     * 
     * @throws IllegalArgumentException in any of the three cases described below:
     * <ul>
     * <li> If any other than the four above stated attributes are given.
     * <li> If the XML does not contain reference and info elements.
     * <li> If either the identifer or the referenceData are null.
     * </ul>
     */
    public TaskInfo( IIdentifier identifier, Document referenceData ) throws IllegalArgumentException
    {
        if ( identifier == null )
        {
            String error = "Identifier can not be null.";
            log.error( error );
            throw new IllegalArgumentException( error );
        }

        if ( referenceData == null )
        {
            String error = "ReferenceData can not be null.";
            log.error( error );
            throw new IllegalArgumentException( error );
        }

        this.identifier = identifier;

        initValuesFromReferenceData( referenceData );
    }

    /**
     * Retrieves the submitter
     * @return The submitter
     */
    public String getSubmitter()
    {
        return this.submitter;
    }


    /**
     * Retrieves the format
     * @return The format
     */
    public String getFormat()
    {
        return this.format;
    }

    /**
     * Retrieves the language
     *
     * For information about default value for language, please see {@link TaskInfo}
     *
     * @return The language
     */
    public String getLanguage()
    {
	return this.language;
    }


    /**
     * Retrieves the mimetype
     * 
     * For information about default value for mimetype, please see {@link TaskInfo}
     *
     * @return The mimetype
     */
    public String getMimeType()
    {
	return this.mimetype;
    }


    /**
     * Retrieves the identifier from the job
     * @return the identifier of the job
     */
    public IIdentifier getIdentifier()
    {
        return identifier;
    }


    /** 
     * This private function initialises the class' private values with the ones 
     * given in the referenceData.
     */   
    private void initValuesFromReferenceData( Document referenceData )
    {

        NodeList elementSet = referenceData.getElementsByTagName( "es:info" );

        if ( elementSet.getLength() == 0 )
        {
            elementSet = referenceData.getElementsByTagName( "info" );
            if ( elementSet.getLength() == 0 )
            {

                String error = "Failed to get either Document Element named 'info' or 'es:info' from referencedata";
                log.error( error );
                throw new IllegalArgumentException( error );
            }
        }

	// Checking for each attribute name , one by one:
        Node info = elementSet.item( 0 );
        NamedNodeMap attributes = info.getAttributes();
        this.format = attributes.getNamedItem( "format" ).getNodeValue();
        this.submitter = attributes.getNamedItem( "submitter" ).getNodeValue();

	// If node "language" is non-existing or empty, set it to a default value, otherwise set its correct value. 
	if ( attributes.getNamedItem( "language" ) != null )
        {
	    String lang = attributes.getNamedItem( "language" ).getNodeValue();
	    this.language =  lang.isEmpty() ? DEFAULT_LANGUAGE_CODE : lang;
	}
	else
        {
	    this.language = DEFAULT_LANGUAGE_CODE;
	}

	// If node "mimetype" is non-existing or empty, set it to a default value, otherwise set its correct value. 
	if ( attributes.getNamedItem( "mimetype" ) != null )
        {
	    String mType = attributes.getNamedItem( "mimetype" ).getNodeValue();
	    this.mimetype =  mType.isEmpty() ? DEFAULT_MIMETYPE_CODE : mType;
	}
	else
        {
	    this.mimetype = DEFAULT_MIMETYPE_CODE;
	}

	// Here we test that only known attributenames are in the info-tag:
	// \todo: When we get an XSD this ought to be obsolete.
	Set< String > legalAttributeValues = new HashSet< String >(4);
	legalAttributeValues.add("submitter");
	legalAttributeValues.add("format");
	legalAttributeValues.add("language");
	legalAttributeValues.add("mimetype");
	for ( int i = 0; i < attributes.getLength(); i++)
	{
	    if ( !legalAttributeValues.contains( attributes.item( i ).getNodeName() ) )
	    {
	    	String errMsg = String.format( "Unknown attributename [%s] found in referencedata.", attributes.item(i).getNodeName() ); 
	    	log.error( errMsg );
	    	throw new IllegalArgumentException( errMsg );
	    }
	}
	
    }
}
