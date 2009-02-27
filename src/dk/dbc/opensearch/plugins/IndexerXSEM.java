package dk.dbc.opensearch.plugins;

import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import dk.dbc.opensearch.common.pluginframework.IIndexer;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;

import org.apache.log4j.Logger;

import org.compass.core.CompassSession;
import org.compass.core.CompassTransaction;
import org.compass.core.xml.AliasedXmlObject;
import org.compass.core.xml.dom4j.Dom4jAliasedXmlObject;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class IndexerXSEM implements IIndexer 
{
	Logger log = Logger.getLogger( IndexerXSEM.class );
	
	
	@Override
	public long getProcessTime(CargoContainer cargo, CompassSession session) 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	
	@Override
	public PluginType getTaskName() 
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public long stub( CargoContainer cc, Estimate estimate, CompassSession session ) throws DocumentException, SQLException, ClassNotFoundException
	{	
		long processTime = 0;
		Date finishTime = new Date();
		
        // Construct doc and Start Transaction
        log.debug( "Starting transaction on running CompassSession" );
        
        log.debug( String.format( "Trying to read CargoContainer data from .getData into a dom4j.Document type" ) );
        SAXReader saxReader = new SAXReader();
        
        ArrayList< CargoObject > list = cc.getData();
        
        for( CargoObject co : list )
        {
        	String mimeType = co.getMimeType();
        	if( mimeType == "text/xml" ) // check with static cpm... class to be implemented
        	{
        		byte[] bytes = co.getBytes();
		        //Document doc = saxReader.read( cc.getData() );
        		ByteArrayInputStream is = new ByteArrayInputStream( bytes );
        		Document doc = saxReader.read( is );
		
		        // this log line is _very_ verbose, but useful in a tight situation
		        // log.debug( String.format( "Constructing AliasedXmlObject from Document. RootElement:\n%s", doc.getRootElement().asXML() ) );
		
        		/** \todo: Dom4jAliasedXmlObject constructor might throw some unknown exception */
		        AliasedXmlObject xmlObject = new Dom4jAliasedXmlObject( co.getFormat(), doc.getRootElement() );
		
		        log.debug( String.format( "Constructed AliasedXmlObject with alias %s", xmlObject.getAlias() ) );
		
		        log.debug( String.format( "Indexing document" ) );
		
		        // getting transaction object and saving index
		        log.debug( String.format( "Getting transaction object" ) );
		        CompassTransaction trans;
		
		        log.debug( "Beginning transaction" );
		        trans = session.beginTransaction();
		
		        log.debug( "Saving aliased xml object to the index" );
		        session.save( xmlObject );
		        log.debug( "Committing index on transaction" );
		        trans.commit();
		        
		        log.debug( String.format( "Transaction wasCommitted() == %s", trans.wasCommitted() ) );
		        session.close();
		
		        log.info( String.format( "Document indexed and stored with Compass" ) );
		
		        log.debug( "Obtain processtime, and writing to statisticDB table in database" );
		        
		        processTime += finishTime.getTime() - co.getTimestamp();		        
		
		        estimate.updateEstimate( co.getMimeType(), co.getContentLength(), processTime );
		        
		        log.info( String.format("Updated estimate with mimetype = %s, streamlength = %s, processtime = %s", co.getMimeType(), co.getContentLength(), processTime ) );
        	}
        }
        
        return processTime;	
	}
}
