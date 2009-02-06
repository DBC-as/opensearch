package dk.dbc.opensearch.common.pluginframework;

//import dk.dbc.opensearch.common.pluginframework.TasksNotValidatedException;
//import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.os.FileHandler;
//import dk.dbc.opensearch.common.pluginframework.PluginSchemaFactory;

//import com.mallardsoft.tuple.Tuple;
//import com.mallardsoft.tuple.Pair;

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

//import java.lang.reflect.Method;

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
    PluginFinder pFinder;
    PluginLoader pLoader;
    ClassLoader pluginClassLoader;
    DocumentBuilderFactory docBuilderFactory;
    DocumentBuilder docBuilder;

    /**
     *
     */
    public PluginResolver()throws NullPointerException, FileNotFoundException, PluginResolverException, ParserConfigurationException/*, SAXException*/ {

   
       
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

        pFinder = new PluginFinder( docBuilder, path );
        pLoader = new PluginLoader( pluginClassLoader );
    }


    public IPluggable getPlugin( PluginID pluginID )throws FileNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException, PluginResolverException
    {
        int key = pluginID.getPluginID();
        //System.out.print(PFinder.getClass() +"\n");
        //Method[] methods = PFinder.getClass().getDeclaredMethods();
        //for(int x = 0; x < methods.length; x++){
        //System.out.println( methods[x].getName() );
        //}
        String pluginClassName = pFinder.getPluginClassName( key );

        return pLoader.getPlugin( pluginClassName );
    }


    public Vector<String> validateArgs( String submitter, String format, String[] taskList )throws PluginResolverException
    {
        Vector<String> pluginNotFoundVector = new Vector();
        String pluginClassName = "";

        //for each string in taskList
        for( int x = 0; x < taskList.length; x++){

            String hashSubject = submitter + format + taskList[x];
            int key = hashSubject.hashCode();
            try
                {
                    pluginClassName = pFinder.getPluginClassName( key );
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
        pFinder.clearClassNameMap();
    }
}