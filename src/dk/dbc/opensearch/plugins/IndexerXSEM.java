package dk.dbc.opensearch.plugins;


import dk.dbc.opensearch.common.config.FedoraConfig;
import dk.dbc.opensearch.common.pluginframework.IIndexer;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.types.CPMAlias;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Field;
import org.compass.core.CompassException;
import org.compass.core.CompassSession;
import org.compass.core.CompassTransaction;
import org.compass.core.Resource;
import org.compass.core.lucene.LuceneProperty;
import org.compass.core.marshall.DefaultMarshallingStrategy;
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
    
    
    public long getProcessTime(CargoContainer cargo, CompassSession session, String fedoraHandle ) throws PluginException
    {
        long processTime = 0;
        try
        {
            processTime = getProcessTime( session, cargo, fedoraHandle );
        }
        catch( CompassException ce )
        {
        	throw new PluginException( "Could not commit index on CompassSession", ce );
        }

        return processTime;
    }


    private long getProcessTime( CompassSession session, CargoContainer cc, String fedoraHandle ) throws PluginException, CompassException
    {
        long processTime = 0;
        Date finishTime = new Date();

        //right know we index all stream in a cc with the same alias. If it should be
        //different for each stream, each CargoObject should have a IndexingAlias.
        String indexingAlias = cc.getIndexingAlias().getName();

        // Construct doc and Start Transaction
        log.debug( "Starting transaction on running CompassSession" );

        log.debug( String.format( "Trying to read CargoContainer data from .getData into a dom4j.Document type" ) );
        SAXReader saxReader = new SAXReader();

        CPMAlias cpmAlias = null;
        log.debug( String.format( "number of streams in cc: %s", cc.getItemsCount() ) );
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
        log.info( "cpmAlias constructed" );
        for( CargoObject co : list )
        {
            //String format = co.getFormat();
            //format = "article";
            //log.debug( String.format( "format of CargoObject: %s", format ) );
            boolean isValidAlias = false;
            try {
                isValidAlias = cpmAlias.isValidAlias( indexingAlias );
            } catch ( ParserConfigurationException pce ) {
                log.error( "parserconfexception");
                throw new PluginException( String.format( "Could not contruct the objects for reading/parsing the configuration file for the XSEM mappings" ), pce );
            } catch ( SAXException se ) {
                log.error( "saxexp ");
                throw new PluginException( String.format( "Could not parse XSEM mappings file" ), se );
            } catch (IOException ioe) {
                log.error("ioexp");
                throw new PluginException( String.format( "Could not open or read XSEM mappings file" ), ioe );
            }catch(Exception e){
                log.fatal("exp: %s with message %s thrown");
                StackTraceElement[] trace = e.getStackTrace();
                for( int i = 0; i < trace.length; i++ )
                    {
                        log.fatal( trace[i].toString() );
                    }
            }
            
            if( ! isValidAlias )
            {
            	throw new PluginException( String.format( "The format %s has no alias in the XSEM mapping file", indexingAlias ) );
            }
            else
            {                
                byte[] bytes = co.getBytes();
                //log.debug( new String( bytes ) );
                ByteArrayInputStream is = new ByteArrayInputStream( bytes );
                Document doc = null;
                try {
                    doc = saxReader.read( is );
                } catch (DocumentException de) {
                    throw new PluginException( String.format( "Could not parse InputStream as an XML Instance from alias=%s, mimetype=%s", indexingAlias, co.getMimeType() ), de );
                }

                // this log line is _very_ verbose, but useful in a tight situation
                // log.debug( String.format( "Constructing AliasedXmlObject from Document. RootElement:\n%s", doc.getRootElement().asXML() ) );

                /** \todo: Dom4jAliasedXmlObject constructor might throw some unknown exception */
                AliasedXmlObject xmlObject = new Dom4jAliasedXmlObject( indexingAlias, doc.getRootElement() );
                //AliasedXmlObject xmlObject = new Dom4jAliasedXmlObject( co.getFormat(), doc.getRootElement() );

                log.info( String.format( "Constructed AliasedXmlObject with alias %s", xmlObject.getAlias() ) );

                // getting transaction object and saving index
                log.debug( String.format( "Getting transaction object" ) );
                CompassTransaction trans = null;
                
                try
                {
                    log.debug( "Beginning transaction" );
                    trans = session.beginTransaction();
                }catch( CompassException ce )
                {
                    log.fatal( String.format( "Could not initiate transaction on the CompassSession" ) );
                    throw new PluginException( "Could not initiate transaction on the CompassSession", ce );
                }

                log.info( String.format( "Saving aliased xml object with alias %s to the index", xmlObject.getAlias() ) );

                /** \todo: when doing this the right way, remember to modify the initial value of the HashMap*/
                HashMap< String, String> fieldMap = new HashMap< String, String >( 2 );

                fieldMap.put( "fedoraPid", fedoraHandle );
                fieldMap.put( "original_format", co.getFormat() );

                

                try{
                    session.save( xmlObject );
                    Resource resObject = updateAliasedXmlObject( session, xmlObject, fieldMap );
                    session.delete( xmlObject );
                    session.save( resObject );
                    //session.flush();
                }catch( Exception e ){
                    log.fatal( String.format( "class of thrown exception: %s, message: %s ", e.getClass(), e.getMessage() ) );
                    log.fatal( String.format( "the file not being indexed is: %s",cc.getFilePath() ) );
                    throw new PluginException(e);
                }
                log.debug( "Committing index on transaction" );
                
                trans.commit();

                /** todo: does trans.wasCommitted have any side-effects? Such as waiting for the transaction to finish before returning?*/
                log.debug( String.format( "Transaction wasCommitted() == %s", trans.wasCommitted() ) );
                session.close();

                log.info( String.format( "Document indexed and stored with Compass" ) );

                log.debug( "Obtain processtime, and writing statistics into the database" );

                processTime += finishTime.getTime() - co.getTimestamp();

                updateEstimationDB(  co, processTime );
            }
        }

        return processTime;
    }

    /**
     * Adds fields to a Compass index. New fields are given in the
     * HashMap, which contains the field names resp. the field values.
     * 
     * @param sess The compass session which we are operating on
     * @param xmlObj The AliasedXmlObject that we retrieve our index from
     * @param fieldMap HashMap containing the field names resp. field values that are to be written to the index
     * 
     * @return the updated index as a Compass Resource
     * 
     * Please note that the AliasedXmlObject is deleted completely
     * from the session and the returned Resource takes its place and
     * contains its information. See the todo in the code.
     */
    private Resource updateAliasedXmlObject( CompassSession sess, AliasedXmlObject xmlObj, HashMap< String, String> fieldMap )
    {

        String alias = xmlObj.getAlias();

        log.debug( String.format( "Preparing for insertion of new fields in index with alias %s", alias ) );

        Resource xmlObj2 = sess.loadResource( alias, xmlObj );

        // \todo do we need to remove the xmlObject from the index?
        log.debug( String.format( "Deleting old xml object" ) );
      

        for( String key : fieldMap.keySet() )
        {
            log.debug( String.format( "Setting new index field '%s' to '%s'", key, fieldMap.get( key ) ) );
            LuceneProperty newField = new LuceneProperty( new Field( key, new StringReader( fieldMap.get( key ) ) ) );
            xmlObj2.addProperty( newField );            
        }

        return xmlObj2;











        /////////////////////
        // DefaultMarshallingStrategy marshallingStrategy = 
        //     new DefaultMarshallingStrategy( sess.getMapping(),
        //                                     sess.getSearchEngine(),
        //                                     sess.getMapping().getConverterLookup(),
        //                                     sess );
        // // save the index
        // sess.save( xmlObj );

        // /** The following code is a hack; in order to write a field,
        //  * we must
        //  * 10: convert the XmlAliasedObject to a Resource,
        //  * 20 :contruct (ie. save) the Resource in the session,  
        //  * 30: retrieve the Resource from the session, 
        //  * 40: add fields to the Resource object and 
        //  * 50: return the modified Resource to be 
        //  * (60: saved again on the session.) 
        //  */

        // // 10: convert the XmlAliasedObject to a Resouce
        // String alias = xmlObj.getAlias();
        // // 20: contruct the Resource
        // Resource resource = marshallingStrategy.marshall( alias, xmlObj );

        // //        log.debug( String.format( "Preparing for insertion of fedora handle '%s' into the index with alias %s", fedoraHandle, alias ) );

        // // 25: save the Resource in the session
        // sess.save( resource);

        // // 30: retrieve the index as a Resource
        // Resource xmlObj2 = sess.loadResource( alias, xmlObj );

        // // \todo do we need to remove the xmlObject from the index?
        // log.debug( String.format( "Deleting old xml object" ) );
        // ses.delete( xmlObj );

        // log.debug( String.format( "Setting field fedoraHandle='%s'", fedoraHandle ) );

        // // 40: Add field to the index
        // for( String key : fieldMap.keySet() )
        // {
        //     log.debug( String.format( "Setting new index field '%s' to '%s'", key, fieldMap.get( key ) ) );
        //     LuceneProperty fedoraField = new LuceneProperty( new Field( key, new StringReader( fieldMap.get( key ) ) ) );
        //     xmlObj2.addProperty( fedoraField );            
        // }

        // // 50: return the modified index
        // return xmlObj2;
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
