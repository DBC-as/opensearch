/**
 * \file OpensearchNamespaceContext.java
 * \brief The OpensearchNamespaceContext class
 * \package helpers;
 */

package dk.dbc.opensearch.common.helpers;


import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;


/**
 * Namespace context for opensearch. It is only getNamespaceURI which
 * is implemented correctly, and this method is used in Xpath
 * evaluation.
 */
public class OpensearchNamespaceContext implements NamespaceContext
{
    public OpensearchNamespaceContext(){}

    public String getNamespaceURI( String prefix ){
        String uri = null;
        if ( prefix.equals( "docbook" ) ){
            uri = "http://docbook.org/ns/docbook";
        }
        return uri;
    }
    
    public Iterator< String > getPrefixes( String val ) {
        return ( Iterator< String > ) null;
    }
    public String getPrefix( String uri ){
        return new String();
    }

}
