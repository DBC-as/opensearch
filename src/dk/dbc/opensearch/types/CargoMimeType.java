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
 * \file CargoMimeType.java
 * \brief The CargoMimetype enum
 * \package datadock
 */


package dk.dbc.opensearch.common.types;


import org.apache.log4j.Logger;


/**
 * \ingroup datadock
 * \brief Enum to control the possible values of mimetypes that we can
 * handle. This is a subset of the official mimetypes as listed in
 * /etc/mime.types.
 */
public enum CargoMimeType
{

    /** represents known mimetypes. All handler registrations must use
     * mimetypes defined here. Mimetypes from /etc/mime.types
     */
    TEXT_XML( "text/xml", "XML Document" ),
    APPLICATION_PDF( "application/pdf", "PDF Document" ),
    APPLICATION_RDF( "application/rdf+xml", "RDF Document" );
    static Logger log = Logger.getLogger( CargoMimeType.class );
    private final String mimetype;
    private final String description;

    CargoMimeType( String mimetype, String description )
    {
        this.mimetype = mimetype;
        this.description = description;
    }


    /**
     * Returns The description of the mimetype.
     *
     * @return The description of the mimetype.
     */
    public String getDescription()
    {
        return this.description;
    }


    /**
     * use instanceOfCargoMimeType.getMimeType() to get the (official)
     * name of the mimetype
     *
     * @return The mimetype
     */
    public String getMimeType()
    {
        return this.mimetype;
    }


    /**
     * Checks for the validity of the mimetype
     * @param mimetype the mimetype to check the validity off
     * @return true if mimetype is valid, false otherwise
     */
    public static boolean validMimetype( String mimetype )
    {
        CargoMimeType CMT = CargoMimeType.getMimeFrom( mimetype );
        log.trace( "checking mimetype" );

        if( CMT == null )
        {
            return false;
        }

        return true;
    }


    /**
     * @param mime the mimetype to look up a CargoMimeType from
     * @return the CargoMimeType that matched {@code mime}, or null if no match
     * was found
     */
    public static CargoMimeType getMimeFrom( String mime )
    {
        CargoMimeType CMT = null;
        for( CargoMimeType cmt : CargoMimeType.values() )
        {
            if( mime.equals( cmt.getMimeType() ) )
            {
                CMT = cmt;
            }
        }

        return CMT;
    }


}
