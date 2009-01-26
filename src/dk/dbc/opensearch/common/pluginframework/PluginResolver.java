package dk.dbc.opensearch.common.pluginframework;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.apache.log4j.Logger;
import java.util.Vector;
import com.mallardsoft.tuple.Tuple;
import com.mallardsoft.tuple.Pair;

import java.lang.IllegalArgumentException;
import java.io.FileNotFoundException;
import java.lang.InstantiationException;
import java.lang.IllegalAccessException;
import javax.xml.parsers.ParserConfigurationException;
import dk.dbc.opensearch.common.pluginframework.TasksNotValidatedException;
/**
 *
 */
public class PluginResolver implements IPluginResolver {

    static Logger log = Logger.getLogger( "PluginResolver" );

    PluginFinder PFinder;
    PluginLoader PLoader;
    ClassLoader pluginClassLoader;
    DocumentBuilderFactory docBuilderFactory;
    DocumentBuilder docBuilder;
    /**
     *
     */
    PluginResolver()throws NullPointerException, FileNotFoundException {
        docBuilderFactory = DocumentBuilderFactory.newInstance();
        /**
         * \Todo: What to do with exceptions?
         */
        try{
            docBuilder = docBuilderFactory.newDocumentBuilder();
        }catch( ParserConfigurationException pce ){
        }
        pluginClassLoader = new PluginClassLoader();
        /** \todo: beware: hardcoded value **/
        String path = "build/classes/dk/dbc/opensearch/plugins";

        PFinder = new PluginFinder( docBuilder, path );
        PLoader = new PluginLoader( pluginClassLoader );

    }
    public IPluggable getPlugin( String submitter, String format, String task )throws FileNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException{
        String key = submitter + format + task;
        String pluginClassName = PFinder.getPluginClassName( key );

        return PLoader.getPlugin( pluginClassName );
    }
    public boolean validateArgs( String submitter, String format, String[] taskList )throws TasksNotValidatedException{

        String message = ""; //what should we say here?
        Pair<Throwable, String> infoPair;
        Vector<Pair> exceptionVector = null;
        boolean allFound = true;
        String pluginClassName = "";
        String notFound = "";
        //for each string in taskList

        for( int x = 0; x < taskList.length; x++){

            String key = submitter + format + taskList[x];

            try{
                pluginClassName = PFinder.getPluginClassName( key );
            }catch( FileNotFoundException fnfe ){
                notFound = String.format( "no plugin found for submitter: %s, format: %s task %s ", submitter, format, taskList[x] );
                infoPair = Tuple.from( (Throwable)fnfe, notFound );
                //build the exceptionVector
                exceptionVector.add( infoPair );
                allFound = false;
            }
        }
        if( allFound ){
            return true;
        }else{
            throw new TasksNotValidatedException( exceptionVector, message );
        }
    }
    public void clearPluginRegistration(){
        //clear the classNameMap in PluginFinder
    }
}