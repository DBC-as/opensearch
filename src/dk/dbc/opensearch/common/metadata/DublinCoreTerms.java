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


package dk.dbc.opensearch.common.metadata;

/**
 *
 */
public enum DublinCoreTerms {
    TERM_ACCESSRIGHTS( "accessRights" ),
    TERM_ALTERNATIVE( "alternative" ),
    TERM_AUDIENCE( "audience" ),
    TERM_AVAILABLE( "available" ),
    TERM_BIBLIOGRAPHICCITATION( "bibliographicCitation" ),
    TERM_CONFORMSTO( "conformsTo" ),
    TERM_CREATED( "created" ),
    TERM_DATEACCEPTED( "dateAccepted" ),
    TERM_DATECOPYRIGHTED( "dateCopyrighted" ),
    TERM_DATESUBMITTED( "dateSubmitted" ),
    TERM_EDUCATIONLEVEL( "educationLevel" ),
    TERM_EXTENT( "extent" ),
    TERM_HASFORMAT( "hasFormat" ),
    TERM_HASPART( "hasPart" ),
    TERM_HASVERSION( "hasVersion" ),
    TERM_ISFORMATOF( "isFormatOf" ),
    TERM_ISPARTOF( "isPartOf" ),
    TERM_ISREFERENCEDBY( "isReferencedBy" ),
    TERM_ISREPLACEDBY( "isReplacedBy" ),
    TERM_ISREQUIREDBY( "isRequiredBy" ),
    TERM_ISSUED( "issued" ),
    TERM_ISVERSIONOF( "isVersionOf" ),
    TERM_LICENSE( "license" ),
    TERM_MEDIATOR( "mediator" ),
    TERM_MEDIUM( "medium" ),
    TERM_MODIFIED( "modified" ),
    TERM_REFERENCES( "references" ),
    TERM_REPLACES( "replaces" ),
    TERM_REQUIRES( "requires" ),
    TERM_RIGHTSHOLDER( "rightsHolder" ),
    TERM_SPATIAL( "spatial" ),
    TERM_TABLEOFCONTENTS( "tableOfContents" ),
    TERM_TEMPORAL( "temporal" ),
    TERM_VALID( "valid" );

    private String name;

    DublinCoreTerms( String localName )
    {
        this.name = localName;
    }
    public String localName()
    {
        return this.name;
    }
    public static boolean hasLocalName( String name )
    {
        for( DublinCoreTerms dcee: DublinCoreTerms.values() )
        {
            if( dcee.localName().equals( name ) ){
                return true;
            }
        }
        return false;
    }
}
