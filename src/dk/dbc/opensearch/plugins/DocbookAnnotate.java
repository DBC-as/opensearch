/**
 * \file FaktalinkAnnotate.java
 * \brief The FaktalinkAnnotate class
 * \package plugins;
 */

package dk.dbc.opensearch.plugins;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.io.IOException;
import java.net.UnknownHostException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Scanner;
import java.net.Socket;
import java.net.URL;

import java.net.HttpURLConnection;

import java.lang.StringBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;


import org.xml.sax.InputSource;

import dk.dbc.opensearch.common.types.CargoContainer;


import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.jdom.Namespace;
/**
 *
 */
public class DocbookAnnotate {

    static Logger log = Logger.getLogger("FaktalinkAnnotate");
    
    private CargoContainer cargo;
    

    /**
     *
     */
    public DocbookAnnotate() {
        System.out.println( "IM ANNOTATING");
    }


    public void init( CargoContainer cargo ){
        this.cargo = cargo;
    }

    public CargoContainer getCargoContainer()throws JDOMException, IOException//, ParserConfigurationException, SAXException
    {

        // 10: retrive docbook xml from CargoContainer 
        // 20: isolate submitter (serverChoice)  from CargoObjectInfo
        // String serverChoice = "";
        
        // 30: isolate title
        //String title = retriveTitle( docbookDocument );

        // 40: Build Query String and retrive result.
        //String xmlString = httpGet( formURL( title, serverChoice ) ); 
        
        // 50: Put it into a xml document
        
        //        SAXBuilder builder = new SAXBuilder( "org.apache.xerces.parsers.SAXParser" );
        //        Document annotateXML = builder.build( new InputSource( new StringReader( xmlString ) ) );

        // 60: Isolate record data

        //Namespace ns = Namespace.getNamespace("http://www.loc.gov/zing/srw/"); // ?????
        //Element e = new Element( "record" );
        //Element tmp  = document.getRootElement().getChild( "records", ns );
        //e.addContent(  tmp.getChild( "record", ns ).detach()  );
        
        // 70: Merge docbookXML (from cargoContainer) with AnnotateXML (Ting-xml)
        // 80: overwrite the docbookXML in the cargoContainer with the new XML
        // 90: return it

        return null;
    
    }


    /**
     * Retrives the title from a docbook Document.
     * 
     * @param docbookDocument the document to retrieve the title from
     * 
     * @returns String containing the isolated title
     *
     * @throws JDOMException if Node doesnt exist
     */
    public String retriveTitle( Document docbookDocument )throws JDOMException{
        
        XPath xpath = XPath.newInstance("/docbook:article/docbook:title"); 
        Element e = (Element) xpath.selectSingleNode( docbookDocument );
        return e.getText();

    }


    /**
     * Forms the URL to use for annotate query.
     * 
     * @param title the title to query. 
     * @param serverChoice This correspond to submitter field (eg. faktalink). Can be empty.
     */
    public String formURL( String title, String serverChoice ){

        int maxRecords = 1;

        String baseURL = "http://koncept.dbc.dk/~fvs/webservice.bibliotek.dk/";
        
        String preTitle = "?version=1.1&operation=searchRetrieve&query=dc.title+%3D+%28%22";
        String postTitle = "%22%29";
        
        String preServerChoice = "+and+cql.serverChoice+%3D+%28";
        String postServerChoice = "%29"; 

        String preRecords = "&startRecord=1&maximumRecords=";
        String postRecords = "&recordSchema=dcting&stylesheet=default.xsl&recordPacking=xml";

        String queryURL;
        if( serverChoice == "" ){
            queryURL = baseURL + preTitle + title + postTitle + 
                preRecords + maxRecords + postRecords;            
        }
        else{
            queryURL = baseURL + preTitle + title + postTitle + 
                preServerChoice + serverChoice + postServerChoice + preRecords + maxRecords + postRecords;
        }
        return queryURL;
    }


    /**
     *  Performs a http call and returns the answer
     *  
     *  @param URLstr The URL to use for hhtp call.
     *  
     *  @returns String containing the response.
     *  
     *  @throws IOException if we got a connection error.
     */
    public String httpGet( String URLstr ) throws IOException{
        URL url = new URL( URLstr );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        if (conn.getResponseCode() != 200) {
            throw new IOException(conn.getResponseMessage());
        }
        
        // Buffer the result into a string
        BufferedReader rd = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
        StringBuilder sb = new StringBuilder();
        String line;
        while ( ( line = rd.readLine() ) != null ) {
            sb.append( line );
        }
        rd.close();
        
        conn.disconnect();
        return sb.toString();        
    }
}
