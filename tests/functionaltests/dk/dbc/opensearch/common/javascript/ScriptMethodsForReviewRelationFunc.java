package dk.dbc.opensearch.common.javascript;

import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.FedoraObjectRepository;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.metadata.DublinCore;
import dk.dbc.opensearch.common.metadata.DublinCoreElement;
import dk.dbc.opensearch.common.fedora.ObjectRepositoryException;

import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
/**
 * 
 */
public class ScriptMethodsForReviewRelationFunc 
{
    private static Logger log = Logger.getLogger( ScriptMethodsForReviewRelationFunc.class );
    private static IObjectRepository repository;
    private static ScriptMethodsForReviewRelation methodClass;

    private final static String submitter = "dbc";
    private final static String language  = "dan";
    private final static String format    = "anmeld"; 

    private static String data =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
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
    
    public static void main( String[] args )
    {
        CargoContainer cc = new CargoContainer();

        DataStreamType dataStreamName = DataStreamType.OriginalData;
	String mimetype = "text/xml";

        try 
	{
	    cc.add( dataStreamName, format, submitter, language, mimetype, data.getBytes( "UTF-8" ) );   
	}
	catch( UnsupportedEncodingException uee )
	{
	    System.err.println( uee );
	}
	catch( IOException ioe )
	{
	    System.err.println( ioe );
	}

        try
        {
            repository = new FedoraObjectRepository();
        }
        catch( ObjectRepositoryException oe )
        {
        } 
       
//         methodClass = new ScriptMethodsForReviewRelation( repository );

//         String test = "testing testing";
//         methodClass.setDCRelation( test );

//         System.out.println( String.format( "test : %s equals %s is %s", test, methodClass.getDCRelation(), test.equals( methodClass.getDCRelation() ) ) );

//         cc.getDublinCoreMetaData().setRelation( test );
//         DublinCore dc = (DublinCore)cc.getMetaData( DataStreamType.DublinCoreData );
//         String result = dc.getDCValue( DublinCoreElement.ELEMENT_RELATION );

//         System.out.println( String.format( "test : %s equals result: %s is %s", test, result, test.equals( result ) ) );


    }
}