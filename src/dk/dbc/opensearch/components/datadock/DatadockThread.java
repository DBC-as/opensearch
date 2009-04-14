/**
 * \file DataDock.java
 * \brief The DataDock class
 * \package datadock
 */
package dk.dbc.opensearch.components.datadock;


/*
*GNU, General Public License Version 3. If any software components linked 
*together in this library have legal conflicts with distribution under GNU 3 it 
*will apply to the original license type.
*
*Software distributed under the License is distributed on an "AS IS" basis,
*WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
*for the specific language governing rights and limitations under the
*License.
*
*Around this software library an Open Source Community is established. Please 
*leave back code based upon our software back to this community in accordance to 
*the concept behind GNU. 
*
*You should have received a copy of the GNU Lesser General Public
*License along with this library; if not, write to the Free Software
*Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
***** END LICENSE BLOCK ***** */


import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.fedora.FedoraHandle;
import dk.dbc.opensearch.common.fedora.FedoraTools;
import dk.dbc.opensearch.common.pluginframework.IAnnotate;
import dk.dbc.opensearch.common.pluginframework.IHarvestable;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginResolver;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.types.Pair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.Callable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.SAXException;


/**
 * \ingroup datadock
 * \brief The public interface for the OpenSearch DataDockService
 * DataDock, together with DataDockPool, is the primary accesspoint
 * for the delivery of material to be saved in the Fedora repository
 * and processed by lucene. The DataDock interface allows clients to
 * submit data that represents a textual material to be stored in a
 * Fedora repository and indexed by Lucene. When submitted, the data
 * is validated against a dictionary of possible handlers using the
 * supplied metadata and object-information. All methods throw
 * exceptions on errors.
 *
 * \todo a schema for errors returned should be defined
 *
 * DataDock is the central class in the datadock component. This class
 * offers the service of receiving data, metadata and objectinfo for
 * later processing with lucene. The primary responsibility of the
 * DataDock is to validate incoming data and construct data-carrying
 * objects for use within OpenSearch. Furthermore, DataDock starts the
 * process of fedora storing, data processing, indexing and
 * search-capabilities
 */
public class DatadockThread extends FedoraHandle implements Callable<Float>
{
    private Logger log = Logger.getLogger( DatadockThread.class );

    private CargoContainer cc;
    private Processqueue queue;
    private HashMap< Pair< String, String >, ArrayList< String > > jobMap;

    private String result;
    private Estimate estimate;
    private DatadockJob datadockJob;
    private String submitter;
    private String format;
    private ArrayList< String > list;


    /**
     *\todo: Wheet out in the Exceptions
     *
     * DataDock is initialized with a DatadockJob containing information
     * about the data to be 'docked' into to system
     *
     * @throws ConfigurationException if the FedoraHandler could not be initialized. \see dk.dbc.opensearch.tools.FedoraHandler
     * @throws ClassNotFoundException if the database could not be initialised in the Estimation class \see dk.dbc.opensearch.tools.DBConnection
     *
     * @param datadockJob the information about the data to be docked
     * @param estimate the estimation database handler
     * @param processqueue the processqueue handler
     * @param jobMap information about the tasks that should be solved by the pluginframework
     *
     * @throws ClassNotFoundException if the database could not be initialised in the Estimation class \see dk.dbc.opensearch.tools.Estimate
     * @throws ConfigurationException if the FedoraHandler could not be initialized. \see dk.dbc.opensearch.tools.FedoraHandler
     * @throws ParserConfigurationException
     * @throws PluginResolverException
     * @throws NullPointerException
     * @throws SAXException
     */
    public DatadockThread( DatadockJob datadockJob, Estimate estimate, Processqueue processqueue, HashMap< Pair< String, String >, ArrayList< String > > jobMap) throws ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException, ServiceException
    {
        super();
        log.debug( String.format( "Entering DatadockThread Constructor" ) );

        this.jobMap = jobMap;
        //log.debug(String.format("the jobMap: %s", this.jobMap.toString()));
        this.datadockJob = datadockJob;

        // Each pair identifies a plugin by p1:submitter and p2:format
        submitter = datadockJob.getSubmitter();
        format = datadockJob.getFormat();

        log.debug( String.format("submitter: %s, format: %s", submitter, format ) );
        log.debug( String.format( "Calling jobMap.get( new Pair< String, String >( %s, %s ) )", submitter, format ) );

        log.debug( "printing jobMap" );
        log.debug( jobMap.toString() );
        list = this.jobMap.get( new Pair< String, String >( submitter, format ) );

        if( list == null )
        {
            throw new NullPointerException( String.format( "The returned list from the jobmap.get( Pair< %s, %s> ) is null", submitter, format ) );
        }
        
        log.debug( String.format( "list has elements" ) );

        queue = processqueue;

        this.estimate = estimate;
        log.debug( String.format( "DataDock Construction finished" ) );
    }

    
    /**
     * call() is the thread entry method on the DataDock. Call operates
     * on the DataDock object, and all data critical for its success
     * is given at DataDock initialization. This method is used with
     * java.util.concurrent.FutureTask, which upon finalisation
     * (completion, exception or termination) will return an
     * estimation of how long time it will take to bzw. index and save
     * in fedora the data given with the CargoContainer.
     * \see dk.dbc.opensearch.tools.Estimation
     *
     * @returns an estimate on the completion-time of indexing and fedora submission
     * @throws PluginResolverException if the PluginResolver encountered problems, see dk.dbc.opensearch.common.pluginframework.PluginResolverException and dk.dbc.opensearch.common.pluginframework.PluginResolver
     * @throws FileNotFoundException if the PluginResolver.getPlugin cant find the file its searcing for
     * @throws IOException if the FedoraHandler could not read data from the CargoContainer
     * @throws ParserConfigurationException if the PluginResolver or CargoContainer has troubles with the DocumentBuilder and DocumentBuilderFactory
     * @throws InstantationException if the PluginResolver cant instantiate a plugin
     * @throws IllegalAccessException if the PluginResolver cant access the desired plugin
     * @throws ClassNotFoundException if the PluginResolver cant find the desired plugin class
     * @throws SAXException when the a harvest or an annotate plugin have problems with the data
     * @throws MarshalException
     * @throws ValidationException
     * @throws IllegalStateException
     * @throws ServiceException
     * @throws IOException
     * @throws ParseException
     */
    public Float call() throws PluginResolverException, IOException, FileNotFoundException, ParserConfigurationException, InstantiationException, IllegalAccessException, ClassNotFoundException, SAXException, MarshalException, ValidationException, IllegalStateException, ServiceException, IOException, ParseException, XPathExpressionException, PluginException, SQLException, TransformerException, TransformerConfigurationException, ConfigurationException
    {
        // Must be implemented due to class implementing Callable< Float > interface.
        // Method is to be extended when we connect to 'Posthuset'

        // Validate plugins
        PluginResolver pluginResolver = new PluginResolver();
        Vector< String > missingPlugins = pluginResolver.validateArgs( submitter, format, list );

        if( ! missingPlugins.isEmpty() )
        {
            log.error( "Thread killed due to invalid plugin call");
            log.error( String.format( "couldnt find the following plugins: %s", missingPlugins.toString() ) );
            // kill thread/throw meaningful exception/log message
        }
        else
        {
            for( String task : list)
            {
                IPluggable plugin = (IPluggable)pluginResolver.getPlugin( submitter, format, task );
                switch ( plugin.getTaskName() )
                {
                case HARVEST:
                    IHarvestable harvestPlugin = (IHarvestable)plugin;
                    cc = harvestPlugin.getCargoContainer( datadockJob );
                    if( cc.getItemsCount() < 1 )
                    {
                        /**
                         * no data in the cargocontainer, so no
                         * reason to continue
                         */
                        log.error( String.format( "no data in the cargocontainer for file: %s", cc.getFilePath() ) );
                        throw new IllegalStateException( String.format( "no data in the cargocontainer for file: %s", cc.getFilePath() ) );
                    }
                    //make estimate
                    break;
                case ANNOTATE:
                    IAnnotate annotatePlugin = (IAnnotate)plugin;
                    cc = annotatePlugin.getCargoContainer( cc );
                    //break;
                    //case STORE:
                    //IRepositoryStore repositoryStore = (IRepositoryStore)plugin;
                    //result = repositoryStore.storeCargoContainer( cc, this.datadockJob );
                }
            }
        }

        // obtain mimetype and length from CargoContainer
        String mimeType = null;
        long length = 0;
        for( CargoObject co : cc.getData() )
        {
            if( co.getDataStreamName() == DataStreamType.OriginalData )
            {
                mimeType = co.getMimeType();
            }
            
            length += co.getContentLength();
        }

        //This should be caught in the harvest plugin!!!
        if( cc.getItemsCount() < 1 )
        {
            log.error( String.format( "The cargocontainer for file %s has no data!", cc.getFilePath() ) );
        }
        
        // Store the CargoContainer in the fedora repository
        byte[] foxml = FedoraTools.constructFoxml( cc, datadockJob.getPID(), datadockJob.getFormat() );
        String logm = String.format( "%s inserted", datadockJob.getFormat() );

        // Beware of this innocent looking log line, it writes the
        //binary content of the stored data to the log
        String pid = super.fem.ingest( foxml, "info:fedora/fedora-system:FOXML-1.1", logm);

        log.info( String.format( "Submitted data, returning pid %s", pid ) );

        // push to processqueue job to processqueue and get estimate
        queue.push( pid );
        Float est = estimate.getEstimate( mimeType, length );
        log.debug( String.format( "Got estimate of %s", est ) );
        
        return est;
    }
}