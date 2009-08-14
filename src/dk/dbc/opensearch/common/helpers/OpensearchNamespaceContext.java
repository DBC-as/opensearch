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
package dk.dbc.opensearch.common.helpers;

import java.util.ArrayList;
import java.util.Iterator;

import java.util.List;
import javax.xml.namespace.NamespaceContext;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;


/**
 * Namespace context for opensearch.
 */
public class OpensearchNamespaceContext implements NamespaceContext
{

    private static Logger log = Logger.getLogger( OpensearchNamespaceContext.class );

    enum OpenSearchNamespace
    {

        DOCBOOK( "docbook", "http://docbook.org/ns/docbook" ),
        DKABM( "dkabm", "http://biblstandard.dk/abm/namespace/dkabm/" ),
        TING( "ting", "http://www.dbc.dk/ting" ),
        DC( "dc", "http://purl.org/dc/elements/1.1/" ),
        XSI( "xsi", "http://www.w3.org/2001/XMLSchema-instance" ),
        ISO6392( "ISO639-2", "http://lcweb.loc.gov/standards/iso639-2/" ),
        DCMI( "dcmitype", "http://purl.org/dc/dcmitype/" ),
        DCTERMS( "dcterms", "http://purl.org/dc/terms/" ),
        AC( "ac", "http://biblstandard.dk/ac/namespace/" ),
        DKDCPLUS( "dkdcplus", "http://biblstandard.dk/abm/namespace/dkdcplus/" );

        private String prefix;
        private String uri;

        OpenSearchNamespace( String prefix, String URI )
        {
            this.prefix = prefix;
            this.uri = URI;
        }


        /**
         *
         * @return prefix of the enum
         */
        public String getPrefix()
        {
            return this.prefix;
        }


        /**
         *
         * @return URI of the enum
         */
        public String getURI()
        {
            return this.uri;
        }


    }

    /**
     * finds an {@link OpenSearchNamespace} given a prefix
     * @param prefix the prefix to look in the enums for
     * @return OpenSearchNamespace type if found, null otherwise
     */
    OpenSearchNamespace getNamespace( String prefix )
    {
        OpenSearchNamespace ns = null;
        for( OpenSearchNamespace osns : OpenSearchNamespace.values() )
        {
            if( osns.prefix.equals( prefix ) )
            {
                ns = osns;
            }
        }
        return ns;
    }


    /**
     * Empty constructor
     */
    public OpensearchNamespaceContext()
    {
    }


    /**
     * @param prefix a String giving the prefix of the namespace for which to search 
     * @return the uri of the namespace that has the given prefix
     */
    @Override
    public String getNamespaceURI( String prefix )
    {
        OpenSearchNamespace namespace = this.getNamespace( prefix );
        return namespace.uri;

//        log.debug( String.format( "OpensearchNamespaceContext prefix: '%s'", prefix ) );
//        String uri = null;
//        if( prefix.equals( "docbook" ) )
//        {
//            uri = "http://docbook.org/ns/docbook";
//            log.debug( String.format( "OpensearchNamespaceContext uri = '%s'", uri.toString() ) );
//        }
//        else if( prefix.equals( "dkabm" ) )
//        {
//            /** \todo: is this uri correct? 'http://biblstandard.dk/abm/namespace/dkabm/' from dk.dbc.opensearch.common.helpers */
//            uri = "http://biblstandard.dk/abm/namespace/dkabm/";
//            log.debug( String.format( "OpensearchNamespaceContext uri = '%s'", uri.toString() ) );
//        }
//        else if( prefix.equals( "ting" ) )
//        {
//            uri = "http://www.dbc.dk/ting";
//            log.debug( String.format( "OpensearchNamespaceContext uri = '%s'", uri.toString() ) );
//        }
//        else if( prefix.equals( "dc" ) )
//        {
//            uri = "http://purl.org/dc/elements/1.1/";
//            log.debug( String.format( "OpensearchNamespaceContext uri = '%s'", uri.toString() ) );
//        }
//        else if( prefix.equals( "xsi" ) )
//        {
//            uri = "http://www.w3.org/2001/XMLSchema-instance";
//            log.debug( String.format( "OpensearchNamespaceContext uri = '%s'", uri.toString() ) );
//        }
//        else if( prefix.equals( "ISO639-2" ) )
//        {
//            uri = "http://lcweb.loc.gov/standards/iso639-2/";
//            log.debug( String.format( "OpensearchNamespaceContext uri = '%s'", uri.toString() ) );
//        }
//        else if( prefix.equals( "dcmitype" ) )
//        {
//            uri = "http://purl.org/dc/dcmitype/";
//            log.debug( String.format( "OpensearchNamespaceContext uri = '%s'", uri.toString() ) );
//        }
//        else if( prefix.equals( "dcterms" ) )
//        {
//            uri = "http://purl.org/dc/terms/";
//            log.debug( String.format( "OpensearchNamespaceContext uri = '%s'", uri.toString() ) );
//        }
//        else if( prefix.equals( "ac" ) )
//        {
//            uri = "http://biblstandard.dk/ac/namespace/";
//            log.debug( String.format( "OpensearchNamespaceContext uri = '%s'", uri.toString() ) );
//        }
//        else if( prefix.equals( "dkdcplus" ) )
//        {
//            uri = "http://biblstandard.dk/abm/namespace/dkdcplus/";
//            log.debug( String.format( "OpensearchNamespaceContext uri = '%s'", uri.toString() ) );
//        }
//        else
//        {
//            log.error( String.format( "OpensearchNamespaceContext uri = '%s'; no match for prefix: '%s'", "NOTHING", prefix ) );
//        }
//
//        return uri;
    }


    /**
     * returns an {@link Iterator<String>} of prefixes that matches
     * {@code namespaceURI}
     *
     * @param namespaceURI the uri to search for prefixes for
     * @return an Iterator containing prefixes
     */
    @Override
    public Iterator<String> getPrefixes( String namespaceURI )
    {
        List<String> prefixes = new ArrayList<String>();

        for( OpenSearchNamespace ns : OpenSearchNamespace.values() )
        {
            if( ns.uri.equals( namespaceURI ) )
            {
                prefixes.add( ns.prefix );
            }
        }

        return prefixes.iterator();
    }


    /**
     * Gets the {@code prefix} associated with {@code uri}
     * @param uri the {@code uri} to find a {@code prefix} for
     * @return the {@code prefix} that matched {@code uri}
     */
    @Override
    public String getPrefix( String uri )
    {
        return this.getPrefix( uri );
    }


}
