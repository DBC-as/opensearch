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
package dk.dbc.opensearch.common.fedora;

import dk.dbc.opensearch.common.helpers.OpensearchNamespaceContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import org.apache.log4j.Logger;


/**
 *
 */
public final class FedoraNamespaceContext implements NamespaceContext
{

    private static Logger log = Logger.getLogger( OpensearchNamespaceContext.class );

    public enum FedoraNamespace
    {

        XML( XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI ),
        RDF( "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
        RDFS( "rdfs", "http://www.w3.org/2000/01/rdf-schema#" ),
        DC( "dc", "http://purl.org/dc/elements/1.1/" ),
        OAI_DC( "oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/"),
        FEDORA( "fedora", "info:fedora/" ),
        FEDORARELSEXT( "rel", "info:fedora/fedora-system:def/relations-external#" ),
        FEDORAMODEL( "fedora-model", "info:fedora/fedora-system:def/model#" ),
        FEDORAVIEW( "fedora-view", "info:fedora/fedora-system:def/view#" ),
        FOXML( "foxml", "info:fedora/fedora-system:def/foxml#" ),
        WORK( "work", FedoraNamespace.FEDORA.getURI() );
        private String prefix;
        private String uri;

        FedoraNamespace( String prefix, String URI )
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


        public String getElementURI( String element )
        {
            return this.uri + element;
        }


    }

    /**
     * finds an {@link FedoraNamespace} given a prefix
     * @param prefix the prefix to look in the enums for
     * @return FedoraNamespace type if found, null otherwise
     */
    public FedoraNamespace getNamespace( String prefix )
    {
        FedoraNamespace ns = null;
        for( FedoraNamespace osns : FedoraNamespace.values() )
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
    public FedoraNamespaceContext()
    {
    }


    /**
     * @param prefix a String giving the prefix of the namespace for which to search
     * @return the uri of the namespace that has the given prefix
     */
    @Override
    public String getNamespaceURI( String prefix )
    {
        FedoraNamespace namespace = this.getNamespace( prefix );
        return namespace.uri;
    }


    /**
     * returns an {@link Iterator} of prefixes that matches
     * {@code namespaceURI}
     *
     * @param namespaceURI the uri to search for prefixes for
     * @return an Iterator containing prefixes
     */
    @Override
    public Iterator<String> getPrefixes( String namespaceURI )
    {
        List<String> prefixes = new ArrayList<String>();

        for( FedoraNamespace ns : FedoraNamespace.values() )
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
