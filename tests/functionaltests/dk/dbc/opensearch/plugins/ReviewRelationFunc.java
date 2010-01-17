
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
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.FedoraObjectRepository;
import dk.dbc.opensearch.common.helpers.Log4jConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.fedora.ObjectRepositoryException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

import dk.dbc.opensearch.common.javascript.E4XXMLHeaderStripper;

public class ReviewRelationFunc 
{

    private static Logger log = Logger.getLogger( ReviewRelationFunc.class );

    // We have to omit the xml-literal or the XMLObject in e4x throws up :(
    //"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    private static String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	"<ting:container xmlns:ac=\"http://biblstandard.dk/ac/namespace/\" xmlns:marcx=\"http://www.bs.dk/standards/MarcXchange\" xmlns:dkabm=\"http://biblstandard.dk/abm/namespace/dkabm/\" xmlns:dkdcplus=\"http://biblstandard.dk/abm/namespace/dkdcplus/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:ting=\"http://www.dbc.dk/ting\" xmlns:oss=\"http://oss.dbc.dk/ns/osstypes\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
	"<dkabm:record>\n" +
	"<ac:identifier>89655900|870971</ac:identifier>\n" +
	"<ac:source>870971</ac:source>\n" +
	"<dc:title>[Anmeldelse]</dc:title>\n" +
	"<dc:creator xsi:type=\"oss:aut\">Morten Højsgaard</dc:creator>\n" +
	"<dc:creator xsi:type=\"oss:sort\">Højsgaard, Morten</dc:creator>\n" +
	"<dc:date>2009</dc:date>\n" +
	"<dc:type xsi:type=\"dkdcplus:BibDK-Type\">Anmeldelse</dc:type>\n" +
	"<dcterms:extent>S. 9</dcterms:extent>\n" +
	"<dc:language xsi:type=\"dcterms:ISO639-2\">dan</dc:language>\n" +
	"<dc:language>Dansk</dc:language>\n" +
	"<dcterms:isPartOf>Kristeligt dagblad 2007-08-16</dcterms:isPartOf>\n" +
	"<dcterms:isPartOf xsi:type=\"dkdcplus:ISSN\">0904-6054</dcterms:isPartOf>\n" +
	"</dkabm:record>\n" +
	"<collection>\n" +
	"<record type=\"Bibliographic\" format=\"danMARC2\">\n" +
	"<leader>000000000000000000000000</leader>\n" +
	"<datafield tag=\"001\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"a\">89655900</subfield>\n" +
	"<subfield code=\"b\">870971</subfield>\n" +
	"<subfield code=\"c\">20091127131741</subfield>\n" +
	"<subfield code=\"d\">20070816</subfield>\n" +
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
	"<subfield code=\"a\">26777631</subfield>\n" +
	"<subfield code=\"x\">ANM</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"016\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"a\">03243591</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"032\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"a\">ANU200949</subfield>\n" +
	"<subfield code=\"a\">DAN200734</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"245\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"a\">[Anmeldelse]</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"300\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"a\">S. 9</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"557\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"a\">Kristeligt dagblad</subfield>\n" +
	"<subfield code=\"j\">2009</subfield>\n" +
	"<subfield code=\"z\">0904-6054</subfield>\n" +
	"<subfield code=\"V\">2007-08-16</subfield>\n" +
	"<subfield code=\"v\">2007-08-16</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"700\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"&amp;\">ANM</subfield>\n" +
	"<subfield code=\"a\">Højsgaard</subfield>\n" +
	"<subfield code=\"h\">Morten</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"900\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"&amp;\">ANM</subfield>\n" +
	"<subfield code=\"a\">Thomsen Højsgêrd</subfield>\n" +
	"<subfield code=\"h\">Morten</subfield>\n" +
	"<subfield code=\"x\">se</subfield>\n" +
	"<subfield code=\"w\">Højsgaard, Morten</subfield>\n" +
	"<subfield code=\"z\">700</subfield>\n" +
	"</datafield>\n" +
	"<datafield tag=\"s10\" ind1=\"0\" ind2=\"0\">\n" +
	"<subfield code=\"a\">IDX</subfield>\n" +
	"</datafield>\n" +
	"</record>\n" +
	"</collection>\n" +
	"</ting:container>\n";


    private final static String data2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
	"<order>plop</order>";

    //    private final static String data3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<person>" +
    private final static String data3 = "<person>\n" +
	"<name>Bob Smith</name>\n" +
	"<likes>\n" +
	"<os>Linux</os>\n" +
	"<browser>Firefox</browser>\n" +
	"<language>JavaScript</language>\n" +
	"<language>Python</language>\n" +
	"</likes>\n" +
	"</person>";


    /*
      REFERENCEDATA:
      <?xml version="1.0" encoding="UTF-8"?>
      <es:referencedata>
      <es:info submitter="dbc" language="dan" format="anmeld"></es:info>
      </es:referencedata>
    */

    private final static String submitter = "dbc";
    private final static String language  = "dan";
    private final static String format    = "anmeld";


    public static void main( String[] args )
    {

	System.out.println( "Where is Waldo?" );

	
	// Setting up the logging or bail out.
	try 
	    {
		Log4jConfiguration.configure( "log4j_datadock.xml" );
	    } 
	catch (ConfigurationException ce )
	    {
		System.out.println( "ConfigurationException Caught. Exiting!");
		System.exit(1);
	    }
        ConsoleAppender startupAppender = new ConsoleAppender(new SimpleLayout());

	log.info( "Up and running" );
	

	CargoContainer c = new CargoContainer();
	
	DataStreamType dataStreamName = DataStreamType.OriginalData;
	String mimetype = "text/xml";
	String alias = "fakeAlias";

	try 
	{
	    byte[] XML = E4XXMLHeaderStripper.strip( data.getBytes( "UTF-8" ) );
	    c.add( dataStreamName, format, submitter, language, mimetype, alias, XML );
	    // c.add( dataStreamName, format, submitter, language, mimetype, alias, data.getBytes( "UTF-8" ) );
	    // c.add( dataStreamName, format, submitter, language, mimetype, alias, data2.getBytes( "UTF-8" ) );
	    // c.add( dataStreamName, format, submitter, language, mimetype, alias, data3.getBytes( "UTF-8" ) );
	}
	catch( UnsupportedEncodingException uee )
	{
	    System.err.println( uee );
	}
	catch( IOException ioe )
	{
	    System.err.println( ioe );
	}

	ReviewRelation reviewRelation = null;
	try 
	{
	    reviewRelation = new ReviewRelation();
	}
	catch( PluginException pe )
	{
	    log.fatal( "An exception occured when trying to instantiate the ReviewRelation", pe );
	}
        IObjectRepository repository = null;
        try
        {
            repository = new FedoraObjectRepository();
        }
        catch( ObjectRepositoryException oe )
        {
            System.out.println( "exception caught when initialising the ObjectRepository" + oe.getMessage() );     
            System.exit(1);
        }
        reviewRelation.setObjectRepository( repository );

	try
	{
	    // Run test to ensure that javascript is used uninitialized:
	    CargoContainer c2 = reviewRelation.getCargoContainer( c );

	    // Test again to ensure that the jsavascript is used initialized:
	    CargoContainer c3 = reviewRelation.getCargoContainer( c2 );

	}
	catch( PluginException pe )
	{
	    System.err.println( pe );
	}

	System.out.println( "Same place as Elvis!" );
    }

}