package dk.dbc.opensearch.plugins;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import dk.dbc.opensearch.common.pluginframework.IIndexer;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.types.CPMAlias;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;

import org.apache.log4j.Logger;
import org.apache.commons.configuration.ConfigurationException;

import org.compass.core.CompassException;
import org.compass.core.CompassSession;
import org.compass.core.CompassTransaction;
import org.compass.core.xml.AliasedXmlObject;
import org.compass.core.xml.dom4j.Dom4jAliasedXmlObject;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;


public class IndexerXSEM implements IIndexer
{
    Logger log = Logger.getLogger( IndexerXSEM.class );

    
    public PluginType getTaskName()
    {
        return PluginType.INDEX;
    }
    
    
    public long getProcessTime(CargoContainer cargo, CompassSession session) throws PluginException
    {
        long processTime = 0;
        try
        {
        	processTime = getProcessTime( session, cargo );
        }
        catch( CompassException ce )
        {
        	throw new PluginException( "Could not commit index on CompassSession", ce );
        }

        return processTime;
    }


    private long getProcessTime( CompassSession session, CargoContainer cc ) throws PluginException, CompassException
    {
        long processTime = 0;
        Date finishTime = new Date();

        // Construct doc and Start Transaction
        log.debug( "Starting transaction on running CompassSession" );

        log.debug( String.format( "Trying to read CargoContainer data from .getData into a dom4j.Document type" ) );
        SAXReader saxReader = new SAXReader();

        CPMAlias cpmAlias = null;
        ArrayList< CargoObject > list = cc.getData();
        try {
        	cpmAlias = new CPMAlias();			
        } catch( ParserConfigurationException pce ) {
        	throw new PluginException( String.format( "Could not construct CPMAlias object for reading/parsing xml.cpm file -- values used for checking cpm aliases" ), pce );
        } catch (SAXException se) {
        	throw new PluginException( String.format( "Could not parse XSEM mappings file" ), se );
		} catch (IOException ioe) {
			throw new PluginException( String.format( "Could not open or read XSEM mappings file" ), ioe );
		}
        
        for( CargoObject co : list )
        {
            String format = co.getFormat();
            boolean isValidAlias = false;
            try {
                isValidAlias = cpmAlias.isValidAlias( format );
            } catch ( ParserConfigurationException pce ) {
                throw new PluginException( String.format( "Could not contruct the objects for reading/parsing the configuration file for the XSEM mappings" ), pce );
            } catch ( SAXException se ) {
                throw new PluginException( String.format( "Could not parse XSEM mappings file" ), se );
            } catch (IOException ioe) {
                throw new PluginException( String.format( "Could not open or read XSEM mappings file" ), ioe );
            }
            
            if( ! isValidAlias )
            {
            	throw new PluginException( String.format( "The format %s has no alias in the XSEM mapping file", format ) );
            }
            else
            {
                byte[] bytes = co.getBytes();
                ByteArrayInputStream is = new ByteArrayInputStream( bytes );
                Document doc = null;
                try {
                    doc = saxReader.read( is );
                } catch (DocumentException de) {
                    throw new PluginException( String.format( "Could not parse InputStream as an XML Instance from format=%s, mimetype=%s", co.getFormat(), co.getMimeType() ), de );
                }

                // this log line is _very_ verbose, but useful in a tight situation
                // log.debug( String.format( "Constructing AliasedXmlObject from Document. RootElement:\n%s", doc.getRootElement().asXML() ) );

                /** \todo: Dom4jAliasedXmlObject constructor might throw some unknown exception */
                AliasedXmlObject xmlObject = new Dom4jAliasedXmlObject( co.getFormat(), doc.getRootElement() );

                log.debug( String.format( "Constructed AliasedXmlObject with alias %s", xmlObject.getAlias() ) );

                log.debug( String.format( "Indexing document" ) );

                // getting transaction object and saving index
                log.debug( String.format( "Getting transaction object" ) );
                CompassTransaction trans = null;
                
                try
                {
                    log.debug( "Beginning transaction" );
                    trans = session.beginTransaction();
                }catch( CompassException ce )
                {
                    throw new PluginException( "Could not initiate transaction on the CompassSession", ce );
                }

                log.debug( "Saving aliased xml object to the index" );
                session.save( xmlObject );
                log.debug( "Committing index on transaction" );
                
                trans.commit();

                log.debug( String.format( "Transaction wasCommitted() == %s", trans.wasCommitted() ) );
                session.close();

                log.info( String.format( "Document indexed and stored with Compass" ) );

                log.debug( "Obtain processtime, and writing to statisticDB table in database" );

                processTime += finishTime.getTime() - co.getTimestamp();

                updateEstimationDB(  co, processTime );
            }
        }

        return processTime;
    }

    
    /**
     * 
     */
    private void updateEstimationDB( CargoObject co, long processTime ) throws PluginException
    {
        Estimate est = null;

        // updating the database with the new estimations
        try
        {
            est = new Estimate();
            est.updateEstimate( co.getMimeType(), co.getContentLength(), processTime );
            
            log.info( String.format("Updated estimate with mimetype = %s, streamlength = %s, processtime = %s", 
                                    co.getMimeType(), co.getContentLength(), processTime ) );
        }
        catch( SQLException sqle )
        {
            throw new PluginException( String.format( "Could not update database with estimation %s", processTime ), sqle );
        }
        catch( ConfigurationException ce )
        {
            throw new PluginException( "Estimate could not be setup correctly", ce );
        }
        catch( ClassNotFoundException cnfe )
        {
            throw new PluginException( "Could not configure database in Estimation class", cnfe );
        }
    }
}
