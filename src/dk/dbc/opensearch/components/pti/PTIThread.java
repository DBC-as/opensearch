/**
 * \file PTIThread.java
 * \brief The PTIThread Class
 * \package pti
 */
package dk.dbc.opensearch.components.pti;


import dk.dbc.opensearch.common.fedora.FedoraHandle;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.IProcesser;
import dk.dbc.opensearch.common.pluginframework.IIndexer;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginResolver;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.IndexingAlias;
import dk.dbc.opensearch.common.statistics.Estimate;

import fedora.server.types.gen.MIMETypedStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.compass.core.CompassException;
import org.compass.core.CompassSession;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.MarshalException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * \ingroup pti
 * \brief the PTIThread class is responsible for getting a dataobject from the
 * fedora repository, and index it with compass afterwards. If this
 * was succesfull the estimate values in the statistics db will be updated
 */
public class PTIThread extends FedoraHandle implements Callable<Long>
{
    Logger log = Logger.getLogger( PTIThread.class );

    private CompassSession session;
    private String fedoraPid;
    private Estimate estimate;
    private ArrayList< String > list;
    private HashMap< Pair< String, String >, ArrayList< String > > jobMap;


    /**
     * \brief Constructs the PTI instance with the given parameters
     *
     * @param fedoraPid the handle identifying the data object
     * @param session the compass session this pti should communicate with
     * @param estimate used to update the estimate table in the database
     * @param jobMap information about the tasks that should be solved by the pluginframework
     */
    public PTIThread( String fedoraPid, CompassSession session, Estimate estimate, HashMap< Pair< String, String >, ArrayList< String > > jobMap ) throws ServiceException, MalformedURLException, IOException
    {
    	super();

    	log.debug( String.format( "constructor(session, fedoraPid=%s )", fedoraPid ) );

    	this.jobMap = jobMap;
    	this.estimate = estimate;
    	this.session = session;
    	this.fedoraPid = fedoraPid;

    	log.debug( "constructor done" );
    }

    
    /**
     * call is the main function of the PTI class. It reads the data
     * pointed to by the fedorahandler given to the class in the
     * constructor and indexes it with compass, and finally returning
     * a float, representing the processtime for the data pointed to
     * by the fedorahandle.
     *
     * @return the processtime
     *
     * @throws CompassException something went wrong with the compasssession
     * @throws IOException something went wrong initializing the fedora client
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws PluginResolverException when the PluginResolver encounters problem that are not neccesarily showstoppers
     * @throws InstantiationException when the PluginResolver cant instantiate a plugin
     * @throws ParserConfigurationException when the PluginResolver has problems parsing files
     * @throws IllegalAccessException when the PluginiResolver cant access a plugin that should be loaded
     * */
    public Long call() throws CompassException, IOException, SQLException, ClassNotFoundException, InterruptedException, PluginResolverException, InstantiationException, ParserConfigurationException, IllegalAccessException, MarshalException, ServiceException, ValidationException, PluginException, SAXException
    {
        log.debug( String.format( "Entering with handle: '%s'", fedoraPid ) );

        MIMETypedStream ds = super.fea.getDatastreamDissemination(fedoraPid, DataStreamType.AdminData.getName(), null);
        byte[] adminStream = ds.getStream();

        CargoContainer cc = new CargoContainer();

        //log.debug( new String(adminStream));
        DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFact.newDocumentBuilder();
        ByteArrayInputStream bis = new ByteArrayInputStream( adminStream );
        Document admDoc = docBuilder.parse( new InputSource( bis ) );
        Element root = admDoc.getDocumentElement();
        Element indexingAliasElem = (Element)root.getElementsByTagName( "indexingalias" ).item( 0 );
        String indexingAliasName = indexingAliasElem.getAttribute( "name" );
        cc.setIndexingAlias( IndexingAlias.getIndexingAlias( indexingAliasName )  );

        NodeList streamsNL = root.getElementsByTagName( "streams" );
        Element streams = (Element)streamsNL.item(0);
        NodeList streamNL = streams.getElementsByTagName( "stream" );
        for(int i = 0; i < streamNL.getLength(); i++ ){
            Element stream = (Element)streamNL.item(i);
            String streamID = stream.getAttribute( "id" );

            MIMETypedStream dstream = super.fea.getDatastreamDissemination(fedoraPid, streamID, null);


            cc.add( DataStreamType.getDataStreamNameFrom( stream.getAttribute( "streamNameType" ) ),
                    stream.getAttribute( "format" ),
                    stream.getAttribute( "submitter" ),
                    stream.getAttribute( "lang" ),
                    stream.getAttribute( "mimetype" ),
                    dstream.getStream() );
        }

        // Get the submitter and format from the CargoContainer
        CargoObject co = cc.getFirstCargoObject( DataStreamType.OriginalData );
        String submitter =  co.getSubmitter();
        String format = co.getFormat();
        
        long result = 0l;

        // Get the job from the jobMap
        list = jobMap.get( new Pair< String, String >( submitter, format ) );
        if(list == null)
        {
            log.fatal( String.format( "no jobs for submitter: %s format: %s", submitter, format ) );
            throw new NullPointerException( String.format( "no jobs for submitter: %s format: %s", submitter, format ) );
        }
        
        //50: validate that there exists plugins for all the tasks
        PluginResolver pluginResolver = new PluginResolver();
        for( int i = 0; i < list.size(); i++ )
            log.debug( String.format( " plugin to be found: %s",list.get(i) ) );
        
        Vector< String > missingPlugins = pluginResolver.validateArgs( submitter, format, list );
        //60: execute the plugins
        if( ! missingPlugins.isEmpty() )
        {
            Iterator< String > iter = missingPlugins.iterator();
            while( iter.hasNext())
            {
                log.debug( String.format( "no plugin for task: %s", (String)iter.next() ) );
            }
            
            log.debug( " kill thread" );
        }
        else
        {
            log.debug( "Entering switch" );

            for( String task : list)
            {
                IPluggable plugin = (IPluggable)pluginResolver.getPlugin( submitter, format, task );
                switch ( plugin.getTaskName() )
                {
                	case PROCESS:
                        log.debug( "calling processerplugin");
                        IProcesser processPlugin = (IProcesser)plugin;
                        cc = processPlugin.getCargoContainer( cc );
                        break;
                    case INDEX:
                        log.debug( "calling indexerplugin");
                        IIndexer indexPlugin = (IIndexer)plugin;
                        result = indexPlugin.getProcessTime( cc, session );
                        //update statistics database
                        break;
                }
            }
        }
        
        log.debug( result );

        return result;
    }
}
