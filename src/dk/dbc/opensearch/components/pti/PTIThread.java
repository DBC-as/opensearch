/**
 * \file PTIThread.java
 * \brief The PTIThread Class
 * \package pti
 */
package dk.dbc.opensearch.components.pti;

import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.IProcesser;
import dk.dbc.opensearch.common.pluginframework.IIndexer;
import dk.dbc.opensearch.common.pluginframework.PluginResolver;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.common.statistics.Estimate;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

import javax.xml.parsers.ParserConfigurationException;

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

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Field;
import org.apache.commons.configuration.ConfigurationException;

/**
 * \ingroup pti
 * \brief the PTIThread class is responsible for getting a dataobject from the
 * fedora repository, and index it with compass afterwards. If this
 * was succesfull the estimate values in the statistics db will be updated
 */
public class PTIThread implements Callable<Long>{

    Logger log = Logger.getLogger("PTIThread");

    // private FedoraHandler fh;
    // private DefaultCompassSession session;
    private CompassSession session;
    private CargoContainer cc;
    private Date finishTime;
    private String fedoraHandle;
    // private String datastreamItemID;
    private Estimate estimate;
    private ArrayList< String > list;
    private HashMap< Pair< String, String >, ArrayList< String > > jobMap;
    private String submitter;
    private String format;

    /**
     * \brief Constructs the PTI instance with the given parameters
     *
     * @param fedoraHandle the handle identifying the data object
     * @param session the compass session this pti should communicate with
     * @param estimate used to update the estimate table in the database
     * @param jobMap information about the tasks that should be solved by the pluginframework
     */
    public PTIThread( String fedoraHandle, CompassSession session, Estimate estimate, HashMap< Pair< String, String >, ArrayList< String > > jobMap )
        {
            log.debug( String.format( "constructor(session, fedoraHandle=%s )", fedoraHandle ) );

            this.jobMap = jobMap;
            this.estimate = estimate;
            this.session = session;
            this.fedoraHandle = fedoraHandle;

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
    public Long call() throws CompassException, IOException, DocumentException, SQLException, ClassNotFoundException, InterruptedException, PluginResolverException, InstantiationException, ParserConfigurationException, IllegalAccessException {
        log.debug( String.format( "CALL CALLED handle: '%s'", fedoraHandle ) );

        long result = 1l;
        /**
         * We cannot have plugins handle the retrieving of the digital object,
         * since we dont know the format and submitter until we can access it
         * and therefore must have retreived it
         * \Todo: Are all digitalobject retrieved and made into a CorgaContainer in
         * the same way?
         */

        //10: Retrieve digitalobject from FedoraBase
        //20: Create the CargoContainer
        //30: Get the submitter and format from the CargoContainer
        //40: get the job from the jobMap
        list = this.jobMap.get( new Pair< String, String >( submitter, format ) );
        //50: validate that there exists plugins for all the tasks
        PluginResolver pluginResolver = new PluginResolver();
        Vector< String > missingPlugins = pluginResolver.validateArgs( submitter, format, list );
        //60: execute the plugins
        if( ! missingPlugins.isEmpty() )
            {
                System.out.println( " kill thread" );
                // kill thread/throw meaningful exception/log message
            }
        else
            {
                CargoContainer cc = null;

                for( String task : list)
                    {
                        IPluggable plugin = (IPluggable)pluginResolver.getPlugin( submitter, format, task );
                        switch ( plugin.getTaskName() )
                            {
                            case PROCESS:
                                IProcesser processPlugin = (IProcesser)plugin;
                                cc = processPlugin.getCargoContainer( cc );
                                break;
                            case INDEX:
                                IIndexer indexPlugin = (IIndexer)plugin;
                                result = indexPlugin.getProcessTime( cc, session );
                                //update statistics database
                                break;
                                //  case STORE:
                                //                                         IRepositoryStore repositoryStore = (IRepositoryStore)plugin;
                                //                                         repositoryStore.storeCargoContainer( cc, this.datadockJob );
                            }
                    }
            }


        return result;
    }

}
