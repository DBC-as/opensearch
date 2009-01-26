package dk.dbc.opensearch.common.pluginframework;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.apache.log4j.Logger;

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
    public IPluggable getPlugin( String submitter, String format, String task )throws FileNotFoundException, InstantiationException, IllegalAccessException{
        String key = submitter + format + task;
        String pluginClassName = PFinder.getPluginClassName( key );

        return PLoader.getPlugin( pluginClassName );
    }
    public boolean validateArgs( String submitter, String format, String[] task )throws TasksNotValidatedException{
        //for each string in taskList
        String key = submitter + format + task;
        Pair<Throwable, String> infoPair; 
        Vector<Pair> exceptionVector;
        String pluginClassName = "";
        
        try{
        pluginClassName = PFinder.getPluginClassName( key );
        }catch( FileNotFoundException fnfe ){
            //build the exceptionVector
        }
        if( pluginClassName.equals( "" ) ){
            return true;
        }
        throw new TasksNotValidatedException(exceptionVector ,message);
    }
    public void clearPluginRegistration(){
        //clear the classNameMap in PluginFinder
    }
}