/**
 * \file OpensearchNamespaceContext.java
 * \brief The OpensearchNamespaceContext class
 * \package helpers;
 */

package dk.dbc.opensearch.common.helpers;


import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

import org.apache.commons.lang.NotImplementedException;

/**
 * Namespace context for opensearch. It is only getNamespaceURI which
 * is implemented correctly, and this method is used in Xpath
 * evaluation.
 */
public class OpensearchNamespaceContext implements NamespaceContext
{
    public OpensearchNamespaceContext(){}

    /**
     * @param prefix a String giving the prefix of the namespace for which to search 
     * @return the uri of the namespace that has the given prefix
     */
    public String getNamespaceURI( String prefix ){
        String uri = null;
        if ( prefix.equals( "docbook" ) ){
            uri = "http://docbook.org/ns/docbook";
        }
        return uri;
    }

    
    public Iterator< String > getPrefixes( String val ) {
        throw new NotImplementedException( "getPrefixes( String val) has not yet been implemented" );
        //return ( Iterator< String > ) null;
    }

    public String getPrefix( String uri ){
        throw new NotImplementedException( "getPrefix( String uri ) has not yet been implemented" );
        //return new String();
    }

}
