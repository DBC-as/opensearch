package dk.dbc.opensearch.common.pluginframework;

import dk.dbc.opensearch.common.pluginframework.TasksNotValidatedException;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;

import com.mallardsoft.tuple.Tuple;
import com.mallardsoft.tuple.Pair;

import java.util.Vector;

import java.lang.IllegalArgumentException;
import java.io.FileNotFoundException;
import java.lang.InstantiationException;
import java.lang.IllegalAccessException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.apache.log4j.Logger;

/**
 *
 */
public class PluginResolver implements IPluginResolver
{
    static Logger log = Logger.getLogger( "PluginResolver" );

    PluginFinder PFinder;
    PluginLoader PLoader;
    ClassLoader pluginClassLoader;
    DocumentBuilderFactory docBuilderFactory;
    DocumentBuilder docBuilder;

    /**
     *
     */
    PluginResolver()throws NullPointerException, FileNotFoundException, PluginResolverException, ParserConfigurationException {
        docBuilderFactory = DocumentBuilderFactory.newInstance();
   
        /**
         * \Todo: What to do with exceptions?
         */
        // try
        // {
        docBuilder = docBuilderFactory.newDocumentBuilder();
        //}
        //catch( ParserConfigurationException pce )
        //{
        // do nothing???
        //}

        pluginClassLoader = new PluginClassLoader();
        /** \todo: beware: hardcoded value **/
        String path = "build/classes/dk/dbc/opensearch/plugins";

        PFinder = new PluginFinder( docBuilder, path );
        PLoader = new PluginLoader( pluginClassLoader );
    }


    public IPluggable getPlugin( String submitter, String format, String task )throws FileNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException, PluginResolverException
    {
        String key = submitter + format + task;
        String pluginClassName = PFinder.getPluginClassName( key );

        return PLoader.getPlugin( pluginClassName );
    }


    public Vector<String> validateArgs( String submitter, String format, String[] taskList )throws PluginResolverException
    {
        Vector<String> pluginNotFoundVector = null;
        String pluginClassName = "";

        //for each string in taskList
        for( int x = 0; x < taskList.length; x++){

            String key = submitter + format + taskList[x];

            try
                {
                    pluginClassName = PFinder.getPluginClassName( key );
                }
            catch( FileNotFoundException fnfe )
                {
                    //build the Vector for the tasks there are no plugins for
                    pluginNotFoundVector.add( taskList[x] );
                }
        }
        return pluginNotFoundVector;

    }


    public void clearPluginRegistration()
    {
        //clear the classNameMap in PluginFinder
    }
}