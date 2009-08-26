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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import fedora.common.xml.format.FedoraRELSExt1_0Format;
import dk.dbc.opensearch.common.fedora.FedoraNamespaceContext.FedoraNamespace;
import dk.dbc.opensearch.common.types.CargoMimeType;
import fedora.utilities.XmlTransformUtility;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;

import org.w3c.dom.Element;


/**
 * Class that constructs, or builds upon already constructed, fedora
 * rels-ext statements
 */
public class FedoraRelsExt
{
    private DocumentBuilder builder;
    private Document doc;
    private Element relsext;
    private Element fedoraObject;

    public FedoraRelsExt( String id ) throws ParserConfigurationException
    {
        // initializing state for the class. Not optimal, but'll have to do for now
        createEmptyRelsExt( id );
    }


    public void addRelationship( QName predicate, QName object )
    {
        Element triple = doc.createElement( predicate.getPrefix()+":"+predicate.getLocalPart() );
        triple.setAttribute( "rdf:resource", object.getNamespaceURI()+object.getPrefix()+":"+object.getLocalPart() );
        fedoraObject.appendChild( triple );
    }

    public void serialize( OutputStream out ) throws TransformerConfigurationException, TransformerException
    {
        Transformer idTransform;
        TransformerFactory xformFactory = XmlTransformUtility.getTransformerFactory();
        idTransform = xformFactory.newTransformer();
        idTransform.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );

        Source input = new DOMSource( doc );
        Result output = new StreamResult( out );
        idTransform.transform( input, output );
    }


    private void createEmptyRelsExt( String id ) throws ParserConfigurationException
    {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware( true );

        builder = dbFactory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();
        String relsextURI = FedoraRELSExt1_0Format.getInstance().uri;

        doc = impl.createDocument( "", "foxml:datastream", null );
        relsext = doc.getDocumentElement();

        relsext.setAttribute( "ID", "RELS-EXT" );
        relsext.setAttribute( "CONTROL_GROUP", FoxmlDocument.ControlGroup.X.toString() );
        Element dsv = doc.createElement( "foxml:datastreamVersion" );
        dsv.setAttribute( "FORMAT_URI", relsextURI );
        dsv.setAttribute( "ID", "RELS-EXT.0" );
        dsv.setAttribute( "MIMETYPE", CargoMimeType.APPLICATION_RDF.getMimeType() );
        dsv.setAttribute( "LABEL", "RDF Statements about this object" );
        relsext.appendChild( dsv );
        Element xmlContent = doc.createElement( "foxml:xmlContent");
        dsv.appendChild( xmlContent );
        Element rdf = doc.createElement( "rdf:RDF" );
        rdf.setAttribute( "xmlns:rdf", FedoraNamespace.RDF.getURI() );
        rdf.setAttribute( "xmlns:rdfs", FedoraNamespace.RDFS.getURI() );
        rdf.setAttribute( "xmlns:dc", FedoraNamespace.DC.getURI() );
        rdf.setAttribute( "xmlns:oai_dc", FedoraNamespace.OAI_DC.getURI() );
        rdf.setAttribute( "xmlns:fedora", FedoraNamespace.FEDORARELSEXT.getURI() );
        xmlContent.appendChild( rdf );
        fedoraObject = doc.createElement( "rdf:Description" );
        fedoraObject.setAttribute(  "rdf:about", id );
        rdf.appendChild( fedoraObject );

    }



}
