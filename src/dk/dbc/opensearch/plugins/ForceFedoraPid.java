package dk.dbc.opensearch.plugins;

import java.io.ByteArrayInputStream;

import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import dk.dbc.opensearch.common.pluginframework.IAnnotate;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;

import dk.dbc.opensearch.common.helpers.OpensearchNamespaceContext;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;


public class ForceFedoraPid implements IAnnotate {

    static Logger log = Logger.getLogger( ForceFedoraPid.class );

	@Override
	public PluginType getPluginType() {
		return PluginType.ANNOTATE;
	}

	private String getDCIdentifierFromOriginal( CargoContainer cargo ) throws PluginException {
        CargoObject co = cargo.getCargoObject( DataStreamType.OriginalData );

        NamespaceContext nsc = new OpensearchNamespaceContext();

        if( co == null )
        {
            String error = "Could not retrieve CargoObject with original data from CargoContainer";
            log.error( error );
            throw new PluginException( String.format( error ) );
        }

        byte[] b = co.getBytes();
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext( nsc );
        XPathExpression xPathExpression;
        
        try
        {
            xPathExpression = xpath.compile( "/ting:container/dkabm:record/ac:identifier" );
        }
        catch( XPathExpressionException e )
        {
            throw new PluginException( String.format( "Could not compile xpath expression '%s'", "/docbook:article/docbook:title" ), e );
        }
        InputSource docbookSource = new InputSource( new ByteArrayInputStream( b ) );

        // Find title of the docbook document
        String title;
        try
        {
            title = xPathExpression.evaluate( docbookSource ).replaceAll( "\\s", "+" );
        }
        catch( XPathExpressionException xpe )
        {
            throw new PluginException( "Could not evaluate xpath expression to find title", xpe );
        }

        
        log.trace( String.format("title found [%s]", title) );
       
        String newID = co.getSubmitter() + ":" + title.substring(0,title.indexOf('|') );
        if( newID.length() > 64 ) {
        	log.warn( String.format("Constructed ID %s to long shortning to %s", newID, newID.substring(0,64) ));
        	newID = newID.substring(0,64);
        }
        
        return newID;
	}
	@Override
	public CargoContainer getCargoContainer(CargoContainer cargo) throws PluginException {
		String s=getDCIdentifierFromOriginal( cargo );
		if( s != null && s.length()>3) {	
			log.info( String.format("Forcing Store ID to %s", s) );
			cargo.setDCIdentifier( s );
		} 
		return cargo;
	}
}
