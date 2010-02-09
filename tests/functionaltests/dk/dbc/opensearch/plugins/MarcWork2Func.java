/*
 *
 *This file is part of opensearch.
 *Copyright © 2009, Dansk Bibliotekscenter a/s,
 *Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043
 *
 *opensearch is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation, either version 3 of the License, or
 *(at your option) any later version.
 *
 *opensearch is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
 */

package dk.dbc.opensearch.plugins;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.IIdentifier;
import dk.dbc.opensearch.components.datadock.DatadockJob;
import dk.dbc.opensearch.common.javascript.E4XXMLHeaderStripper;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.types.IIdentifier;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

/**
 * 
 */
public class MarcWork2Func 
{  
    private static class TestIdentifier implements IIdentifier
    {
        private String ref;

        TestIdentifier( String theRef )
        {
            ref = theRef;
        }

        @Override
        public String toString()
        {
            return ref;
        }

    }

    private static String anm1_data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	"<ting:container xmlns:ac=\"http://biblstandard.dk/ac/namespace/\" xmlns:marcx=\"http://www.bs.dk/standards/MarcXchange\" xmlns:dkabm=\"http://biblstandard.dk/abm/namespace/dkabm/\" xmlns:dkdcplus=\"http://biblstandard.dk/abm/namespace/dkdcplus/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:ting=\"http://www.dbc.dk/ting\" xmlns:oss=\"http://oss.dbc.dk/ns/osstypes\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
	"<dkabm:record>\n" +
        "<ac:identifier>33911777|870971</ac:identifier>\n" +
        "<ac:source>870971</ac:source>\n" +
        "<dc:title>[Anmeldelse]</dc:title>\n" +
        "<dc:creator xsi:type=\"oss:aut\">Tom Hermansen</dc:creator>\n" +
        "<dc:creator xsi:type=\"oss:sort\">Hermansen, Tom</dc:creator>\n" +
        "<dcterms:abstract>Vurdering: 6/6</dcterms:abstract>\n" +
        "<dc:date>2009</dc:date>\n" +
        "<dc:type xsi:type=\"dkdcplus:BibDK-Type\">Anmeldelse</dc:type>\n" +
        "<dc:type xsi:type=\"oss:pgvaerk\">bog</dc:type>\n" +
        "<dcterms:extent>Sektion 1(østudgave), s. 18</dcterms:extent>\n" +
        "<dc:language xsi:type=\"dcterms:ISO639-2\">dan</dc:language>\n" +
        "<dc:language>Dansk</dc:language>\n" +
        "<dcterms:isPartOf>Jyllands-posten 2009-10-23</dcterms:isPartOf>\n" +
        "<dcterms:isPartOf xsi:type=\"dkdcplus:ISSN\">0109-1182</dcterms:isPartOf>\n" +
	"</dkabm:record>\n" +
	"<collection>\n" +
        "<record type=\"Bibliographic\" format=\"danMARC2\">\n" +
	"<leader>000000000000000000000000</leader>\n" +
	"<datafield tag=\"001\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"a\">33911777</subfield>\n" +
	"<subfield code=\"b\">870971</subfield>\n" +
	"<subfield code=\"c\">20091208094417</subfield>\n" +
	"<subfield code=\"d\">20091208</subfield>\n" +
	"<subfield code=\"f\">a</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"004\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"r\">n</subfield>\n" +
	"<subfield code=\"a\">i</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"008\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"t\">a</subfield>\n" +
	"<subfield code=\"u\">f</subfield>\n" +
	"<subfield code=\"a\">2009</subfield>\n" +
	"<subfield code=\"b\">dk</subfield>\n" +
	"<subfield code=\"d\">s</subfield>\n" +
	"<subfield code=\"l\">dan</subfield>\n" +
	"<subfield code=\"v\">0</subfield>\n" +
	"<subfield code=\"r\">an</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"009\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"a\">a</subfield>\n" +
	"<subfield code=\"g\">xx</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"014\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"a\">28022859</subfield>\n" +
	"<subfield code=\"x\">ANM</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"016\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"a\">03243796</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"032\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"a\">ANU200951</subfield>\n" +
	"<subfield code=\"a\">DAN200951</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"245\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"a\">[Anmeldelse]</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"300\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"a\">Sektion 1(østudgave), s. 18</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"504\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"a\">Vurdering: 6/6</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"557\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"a\">Jyllands-posten</subfield>\n" +
	"<subfield code=\"j\">2009</subfield>\n" +
	"<subfield code=\"z\">0109-1182</subfield>\n" +
	"<subfield code=\"V\">2009-10-23</subfield>\n" +
	"<subfield code=\"v\">2009-10-23</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"700\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"&amp;\">ANM</subfield>\n" +
	"<subfield code=\"a\">Hermansen</subfield>\n" +
	"<subfield code=\"h\">Tom</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"n01\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"a\">e1ba48a1</subfield>\n" +
	"<subfield code=\"b\">000011</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"s10\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"a\">IDX</subfield>\n" +
	"</datafield>\n" +
        "</record>\n" +
	"</collection>\n" +
	"</ting:container>";

    private final static String anm1_submitter = "dbc";
    private final static String anm1_language  = "dan";
    private final static String anm1_format    = "anmeld";

    public static void main( String[] args )
    {
        //build the DatadockJob and its needed components 
        IIdentifier theIdentifier = new TestIdentifier( "test" );

        String refString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><es:referencedata><es:info submitter=\"dbc\" language=\"dan\" format=\"anmeld\"></es:info></es:referencedata>";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document refDataDoc = null;

        try
        {
            System.out.println( refString );
            DocumentBuilder builder = factory.newDocumentBuilder();
            refDataDoc= builder.parse( new InputSource( new ByteArrayInputStream( refString.getBytes() ) ) );
        }
        catch( ParserConfigurationException pce )
        {
            System.err.println( pce );
        }
        catch( SAXException se )
        {
            System.err.println( se ); 
        }
        catch( IOException ioe)
        {
            System.err.println( ioe );
        }

        DatadockJob ddj = new DatadockJob( theIdentifier, refDataDoc );

        //create the cargocontainer and needed data for it
        CargoContainer testCargo = new CargoContainer();
	
	DataStreamType dataStreamName = DataStreamType.OriginalData;
	String mimetype = "text/xml";
	String alias = "fakeAlias";

        MarcxchangeWorkRelation_2 marcWork2Plugin;
        XMLDCHarvester dcPlugin = null;
        try{
            dcPlugin = new XMLDCHarvester();
        }
        catch( PluginException pe )
        {
              System.err.println( pe );
        }
        marcWork2Plugin = new MarcxchangeWorkRelation_2();
        try
        {
            testCargo = dcPlugin.getCargoContainer( ddj, anm1_data.getBytes( "UTF-8" ), alias );
            testCargo = marcWork2Plugin.getCargoContainer( testCargo ); 
        }
        catch( PluginException pe )
        {
            System.err.println( pe );
        }
        catch( UnsupportedEncodingException uee )
        {
            System.err.println( uee );
        }   
    }
}