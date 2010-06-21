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
 * \brief FedoraRelsExt is a helper class that constructs rels-ext xml fragments
 */

 
package dk.dbc.opensearch.common.fedora;


import javax.xml.parsers.ParserConfigurationException;
import dk.dbc.opensearch.common.fedora.FedoraNamespaceContext.FedoraNamespace;
import dk.dbc.opensearch.common.metadata.IPredicate;
import dk.dbc.opensearch.common.metadata.MetaData;

import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.SimplePair;
import dk.dbc.opensearch.common.types.OpenSearchTransformException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.log4j.Logger;


/**
 * Class that constructs, or builds upon already constructed, fedora
 * rels-ext statements. This class is not thread-safe and threads sharing
 * instances could accidentally make duplicate relations on the same rels-ext
 * stream.
 */
public class FedoraRelsExt implements MetaData
{    
    private Set<String> relations;

    // note that the first element (the object) is always `pid` for fedora rels-ext statements
    private Collection< SimplePair<QName, QName> > triples;
    public static final DataStreamType type = DataStreamType.RelsExtData;

    private static FedoraNamespace rdf = new FedoraNamespaceContext().getNamespace( "rdf" );
    private static FedoraNamespace rdfs = new FedoraNamespaceContext().getNamespace( "rdfs" );
    private static FedoraNamespace oai_dc = new FedoraNamespaceContext().getNamespace( "oai_dc" );

    private static FedoraNamespace dc = new FedoraNamespaceContext().getNamespace( "dc" );
    private static FedoraNamespace rels = new FedoraNamespaceContext().getNamespace( "rel" );

    private static Logger log = Logger.getLogger( FedoraRelsExt.class );

    /**
     * Constructs an empty RELS-EXT document. {@code id} is used as
     * identifier for the RDF Node in the document
     * 
     * @throws ParserConfigurationException 
     */
    public FedoraRelsExt( ) throws ParserConfigurationException
    {        
        relations = new HashSet<String>();
        triples = new ArrayList< SimplePair< QName, QName >>();
    }


    /**
     * Adds a relation given by {@code predicate} to {@code object}.
     * <p/>
     * Specifically, this method takes the {@code prefix} and {@code localPart}
     * of the {@code predicate} and forms the reference to the RDF Node with
     * the namespace uri and prefix. For making a isMemberOfCollection relation:
     * <p/>
     * <pre>
     * {@code
     * addRelationship( new QName( FedoraNamespace.FEDORARELSEXT.getURI(),
     *                             "isMemberOfCollection",
     *                             FedoraNamespace.FEDORA.getPrefix() ),
     *                  new QName( "",
     *                             "1",
     *                             FedoraNamespace.WORK.getPrefix() ) );
     * }
     * </pre>
     * <p/>
     * Which would result in the following xml serialization (excerpt):
     * <p/>
     * <pre>
     * {@code
     * ...
     * <fedora:isMemberOfCollection rdf:resource="work:1"/>
     * ...
     * }
     * 
     * </pre> 
     * <p/>
     * The same relation cannot be added more than once. The
     * method will return false if this is tried. Specifically 'same
     * relation' is defined as calling {@code addRelationship} with
     * identical {@link QName}s in {@code predicate} and {@code
     * object}.
     * 
     * @param predicate QName representing the relation type that should be added
     * @param object QName representing the object of the relation.
     * @return true if the relationship does not already exists and could be added, false otherwise
     */
    public boolean addRelationship( QName predicate, QName object )
    {
        boolean added = relations.add( new Integer( predicate.hashCode() ).toString()+new Integer( object.hashCode() ).toString() );
        if( added )
        {
            triples.add( new SimplePair<QName, QName>(predicate, object) );
        }
        return added;
    }

    
    public boolean addRelationship( IPredicate pred, String subject ) {
        QName predicate = pred.getPredicate();
        QName sub = new QName("", subject, "");
        
        return addRelationship( predicate, sub );
    }

    /**
     * Serializes the RELS-EXT Document to a Node representation, ie. without the XML Declaration.
     * @param out the {@link OutputStream} to write the serialization to.
     * <p/>
     * The serialized fragment is encoded with UTF-8 by default.
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    @Override
    public void serialize( OutputStream out, String identifier ) throws OpenSearchTransformException
    {

        // Create an output factory
        XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlw;
        // Fedora 3.2 depends on Namespace repairs.       
        xmlof.setProperty( "javax.xml.stream.isRepairingNamespaces", new Boolean( true ) );
        try
        {
            xmlw = xmlof.createXMLStreamWriter( out );

            xmlw.writeStartDocument();
            xmlw.writeStartElement( rdf.getPrefix(), "RDF", rdf.getURI() );

            xmlw.writeNamespace( dc.getPrefix(), dc.getURI() );

            // hack, as fedora rels-ext ns spec does not conform to
            // their own prefix guidelines:
            xmlw.writeNamespace( "fedora", rels.getURI() );

            xmlw.writeNamespace( oai_dc.getPrefix(), oai_dc.getURI() );

            xmlw.writeNamespace( rdf.getPrefix(), rdf.getURI() );

            xmlw.writeNamespace( rdfs.getPrefix(), rdfs.getURI() );

            xmlw.writeStartElement( rdf.getURI(), "Description" );
            
            
            //...but won't forget to check if we have an identifier at all
            if( null == identifier || identifier.isEmpty() )
            {
                String error = String.format( "Refusing to construct RELS-EXT with no identifier. Won't ever work" );
                log.error( error );
                throw new IllegalArgumentException( error );
            }

            xmlw.writeAttribute( rdf.getPrefix(), rdf.getURI(), "about", String.format( "info:fedora/%s", identifier ) );
            for( SimplePair<QName, QName> set : triples )
            {
                QName key = set.getFirst();
                QName val = set.getSecond();

                xmlw.writeStartElement( key.getPrefix(), key.getLocalPart(), key.getNamespaceURI() );
                
                String subjectId;
                if( val.getPrefix().isEmpty() ) {
                    subjectId= val.getLocalPart();
                } else {
                    subjectId= val.getPrefix()+":"+val.getLocalPart();
                }
                
                xmlw.writeCharacters( subjectId );
                xmlw.writeEndElement();
            }

            xmlw.writeEndElement();//closes "rdf:Description" element
            xmlw.writeEndElement();//closes "rdf:RDF" element
            xmlw.writeEndDocument();//closes document
            xmlw.flush();
        }
        catch( XMLStreamException ex )
        {
            String error = String.format( "Could not write to stream writer %s", ex.getMessage() );
            log.error( error, ex );
            throw new OpenSearchTransformException( error, ex );
        }
    }


    @Override    
    public DataStreamType getType()
    {
        return type;
    }


}
