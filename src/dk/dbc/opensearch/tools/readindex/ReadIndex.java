/**
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


package dk.dbc.opensearch.tools.readindex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
//import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader.FieldOption;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Source;
import java.io.StringWriter;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;
import org.apache.lucene.index.CorruptIndexException;

//import org.w3c.dom.Document;

/**
 * 
 */
public class ReadIndex {
  /**
   * 
   */
  public ReadIndex() {}


    public String readIndexFromFolder( File folder ) throws CorruptIndexException, IOException
    {
        String returnString = "";
        IndexReader iread = IndexReader.open( folder );
        int numberOfDocuments = iread.numDocs();
        returnString += String.format( "\nNumber of documents in index: %s\n",numberOfDocuments );

        for( int i = 0 ; i < numberOfDocuments; i++){
            returnString += String.format( "\nDocument number: %s\n", i );
            if( iread.isDeleted( i ) ){
                returnString += String.format( "Document number: %s is deleted", i );
            }
            else{
                org.apache.lucene.document.Document doc = iread.document( i );

                ArrayList<Field> fields = (ArrayList<Field>) doc.getFields();
                for( Field field : fields ){
                    String[] values = doc.getValues( field.name() );
                 
                    String valstr = "";
                    for( String value : values){
                        valstr += value+", ";
                    }
                    valstr = valstr.substring(0, valstr.length()-2 );
                    returnString += String.format( "Field: %s\nValues: %s\n", field, valstr );
                }
            }
        }
        return returnString;
    }

    public org.w3c.dom.Document readIndexFromFolderXML( File folder )throws ParserConfigurationException, TransformerException, TransformerConfigurationException, CorruptIndexException, IOException
    {       
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.newDocument();
        
        IndexReader iread = IndexReader.open( folder );
        int numberOfDocuments = iread.numDocs();

        Element root = doc.createElement( "index" );
        root.setAttribute( "number-of-documents", Integer.toString( numberOfDocuments ) );
        doc.appendChild( root );

        for( int i = 0 ; i < numberOfDocuments; i++){
            
            Element docnode = doc.createElement( "document" );
            docnode.setAttribute( "document-number", Integer.toString( i ) );
                    
    
            if( iread.isDeleted( i ) ){
                docnode.setAttribute( "delete", "yes" );
            }
            else{ 
                docnode.setAttribute( "delete", "no" );
            }
            root.appendChild( (Node) docnode );
            
            org.apache.lucene.document.Document document = iread.document( i );
            ArrayList<Field> fields = (ArrayList<Field>) document.getFields();
            
            for( Field field : fields ){
                
                Element fieldnode = doc.createElement( "field" );
                fieldnode.setAttribute( "name", field.toString() );
                docnode.appendChild( (Node) fieldnode );
                
                String[] values = document.getValues( field.name() );
                
                for( String value : values)
                {
                    Element valuenode = doc.createElement( "value" );
                    valuenode.setTextContent( value );
                    fieldnode.appendChild( (Node) valuenode );
                }
            }   
        }
        return doc;
    }
}