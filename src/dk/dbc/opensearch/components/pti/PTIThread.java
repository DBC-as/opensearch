/**
 * \file PTIThread.java
 * \brief The PTIThread Class
 * \package pti
 */
package dk.dbc.opensearch.components.pti;

import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.fedora.FedoraHandle;
import dk.dbc.opensearch.common.fedora.FedoraTools;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.IProcesser;
import dk.dbc.opensearch.common.pluginframework.IIndexer;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginResolver;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamNames;
import dk.dbc.opensearch.common.statistics.Estimate;
//import dk.dbc.opensearch.plugins.Retrieve;
import dk.dbc.opensearch.xsd.DigitalObject;
import dk.dbc.opensearch.xsd.Datastream;

import fedora.server.types.gen.MIMETypedStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.rpc.ServiceException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.compass.core.Compass;
import org.compass.core.CompassException;
import org.compass.core.CompassSession;
import org.compass.core.CompassTransaction;
import org.compass.core.Resource;
import org.compass.core.impl.DefaultCompassSession;

import org.compass.core.lucene.LuceneProperty;
import org.compass.core.marshall.MarshallingStrategy;
import org.compass.core.xml.AliasedXmlObject;
import org.compass.core.xml.dom4j.Dom4jAliasedXmlObject;
import org.compass.core.xml.javax.NodeAliasedXmlObject;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Field;
import org.apache.commons.configuration.ConfigurationException;

import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * \ingroup pti
 * \brief the PTIThread class is responsible for getting a dataobject from the
 * fedora repository, and index it with compass afterwards. If this
 * was succesfull the estimate values in the statistics db will be updated
 */
public class PTIThread extends FedoraHandle implements Callable<Long>{

    Logger log = Logger.getLogger("PTIThread");

    // private FedoraHandler fh;
    // private DefaultCompassSession session;
    private CompassSession session;
    private CargoContainer cc;
    private Date finishTime;
    private String fedoraPid;
    // private String datastreamItemID;
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
            log.debug( String.format( "CALL CALLED handle: '%s'", fedoraPid ) );

            MIMETypedStream ds = super.fea.getDatastreamDissemination(fedoraPid, DataStreamNames.AdminData.getName(), null);
            byte[] adminStream = ds.getStream();

            CargoContainer cc = new CargoContainer();

            log.debug( new String(adminStream));
            DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFact.newDocumentBuilder();
            ByteArrayInputStream bis = new ByteArrayInputStream( adminStream );
            Document admDoc = docBuilder.parse( new InputSource( bis ) );
            Element root = admDoc.getDocumentElement();
            NodeList streamsNL = root.getElementsByTagName( "streams" );
            Element streams = (Element)streamsNL.item(0);
            NodeList streamNL = streams.getElementsByTagName( "stream" );
            for(int i = 0; i < streamNL.getLength(); i++ ){
                Element stream = (Element)streamNL.item(i);
                String streamID = stream.getAttribute( "id" );

                MIMETypedStream dstream = super.fea.getDatastreamDissemination(fedoraPid, streamID, null);


                cc.add( DataStreamNames.getDataStreamNameFrom( stream.getAttribute( "streamNameType" ) ),
                        stream.getAttribute( "format" ),
                        stream.getAttribute( "submitter" ),
                        stream.getAttribute( "lang" ),
                        stream.getAttribute( "mimetype" ),
                        dstream.getStream() );
            }

            //CargoContainer cc = FedoraTools.constructCargoContainer( dot, fedoraPid );
            log.debug( String.format(" cc is null = %s", null == cc) );

            // Get the submitter and format from the CargoContainer
            CargoObject co = cc.getFirstCargoObject( DataStreamNames.OriginalData );
            log.debug( String.format(" co is null = %s", null == co) );
            String submitter =  co.getSubmitter();
            String format = co.getFormat();
            log.debug( String.format( "the submitter %s", submitter ) );
            log.debug( String.format( "the format %s", format ) );
            log.debug( String.format( "number og datastreams in cc: %s", cc.getItemsCount() ) );
            //CargoObject co1 = cc.getFirstCargoObject( DataStreamNames.AdminData );

            long result = 0l;

            // Get the job from the jobMap
            list = jobMap.get( new Pair< String, String >( submitter, format ) );
            //50: validate that there exists plugins for all the tasks
            PluginResolver pluginResolver = new PluginResolver();
            for( int i = 0; i < list.size(); i++ ){
                log.debug( String.format( " plugin to be found: %s",list.get(i) ) );
            }
            Vector< String > missingPlugins = pluginResolver.validateArgs( submitter, format, list );
            //60: execute the plugins
            if( ! missingPlugins.isEmpty() )
                {

                    Iterator iter = missingPlugins.iterator();
                    while( iter.hasNext())
                        {
                            log.debug( String.format( "no plugin for task: %s", (String)iter.next() ) );
                        }
                    log.debug( " kill thread" );
                    // kill thread/throw meaningful exception/log message
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
