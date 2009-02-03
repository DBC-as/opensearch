package dk.dbc.opensearch.common.pluginframework;

//import dk.dbc.opensearch.common.pluginframework.TasksNotValidatedException;
//import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.os.FileHandler;
//import dk.dbc.opensearch.common.pluginframework.PluginSchemaFactory;

import com.mallardsoft.tuple.Tuple;
import com.mallardsoft.tuple.Pair;

import java.util.Vector;

import java.lang.IllegalArgumentException;
import java.io.FileNotFoundException;
import java.io.File;
import java.lang.InstantiationException;
import java.lang.IllegalAccessException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
//import javax.xml.validation.SchemaFactory;
//import javax.xml.validation.Schema;
//import org.xml.sax.SAXException;

import org.apache.log4j.Logger;

/**
 *
 */
public class PluginResolver implements IPluginResolver
{
    static Logger log = Logger.getLogger( "PluginResolver" );

    //File schemaFile;
    //SchemaFactory schemaFactory; 
    //Schema validationSchema;
    PluginFinder PFinder;
    PluginLoader PLoader;
    ClassLoader pluginClassLoader;
    DocumentBuilderFactory docBuilderFactory;
    DocumentBuilder docBuilder;

    /**
     *
     */
    PluginResolver()throws NullPointerException, FileNotFoundException, PluginResolverException, ParserConfigurationException/*, SAXException*/ {

   
       
        /** \todo: beware: hardcoded value **/
        String path = "build/classes/dk/dbc/opensearch/plugins";

        /** \todo: beware another hardcoded value **/
        //String schemaPath = "config/opensearch_plugins.xsd";

        //schemaFile = FileHandler.getFile( schemaPath );
        //schemaFactory = PluginSchemaFactory.newInstance( "http://www.w3.org/2001/XMLSchema" );
        //validationSchema = schemaFactory.newSchema( schemaFile );
        docBuilderFactory = DocumentBuilderFactory.newInstance();
        //docBuilderFactory.setSchema( validationSchema );
        docBuilder = docBuilderFactory.newDocumentBuilder();
 
        pluginClassLoader = new PluginClassLoader();

        PFinder = new PluginFinder( docBuilder, path );
        PLoader = new PluginLoader( pluginClassLoader );
    }


    public IPluggable getPlugin( PluginID pluginID )throws FileNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException, PluginResolverException
    {
        //String key = submitter + format + task;
        String pluginClassName = PFinder.getPluginClassName( pluginID.getPluginID() );

        return PLoader.getPlugin( pluginClassName );
    }


    public Vector<String> validateArgs( String submitter, String format, String[] taskList )throws PluginResolverException
    {
        Vector<String> pluginNotFoundVector = null;
        String pluginClassName = "";

        //for each string in taskList
        for( int x = 0; x < taskList.length; x++){

            String hashSubject = submitter + format + taskList[x];
            int key = hashSubject.hashCode();
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
        PFinder.clearClassNameMap();
    }
}