package dk.dbc.opensearch.common.pluginframework;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.InstantiationException;
import java.lang.IllegalAccessException;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.apache.log4j.Logger;

import dk.dbc.opensearch.common.helpers.FileSystemConfig;

/**
 *
 */
public class PluginResolver implements IPluginResolver
{
    static Logger log = Logger.getLogger( PluginResolver.class );

    static String path;

    static DocumentBuilderFactory docBuilderFactory;
    static DocumentBuilder docBuilder;
    static ClassLoader pluginClassLoader;
    static PluginFinder PFinder;
    static PluginLoader PLoader;
    static boolean constructed = false;
    

    /**
     * @throws IOException 
     *
     */
    public PluginResolver() throws NullPointerException, PluginResolverException, ParserConfigurationException/*, SAXException*/, IOException 
    {      
        if( ! constructed )
        {
            docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilder = docBuilderFactory.newDocumentBuilder();
            
            pluginClassLoader = new PluginClassLoader();
            PLoader = new PluginLoader( pluginClassLoader );
            path = FileSystemConfig.getFileSystemTrunkPath() + "src/dk/dbc/opensearch/plugins";
            
            PFinder = new PluginFinder( docBuilder, path );
            
            constructed = true;            
        }
    }


    public IPluggable getPlugin( String submitter, String format, String task ) throws FileNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException, PluginResolverException
    {  
    	int key = ( submitter + format + task ).hashCode();
        String pluginClassName = PFinder.getPluginClassName( key );
        
        //PFinder.printClassNameMap();

        return PLoader.getPlugin( pluginClassName );
    }


    public Vector<String> validateArgs( String submitter, String format, ArrayList< String > taskList ) throws PluginResolverException
    {
        Vector< String > pluginNotFoundVector = new Vector< String >();

        // Loop through list of tasks finding tasks without matching plugin.
        for( int i = 0; i < taskList.size(); i++ )
        {
            String hashSubject = submitter + format + taskList.get( i ).toString();
            int key = hashSubject.hashCode();
            
            try
            {
            	PFinder.getPluginClassName( key );
            }
            catch( FileNotFoundException fnfe )
            {
            	// Add "missing" plugins to return Vector
            	pluginNotFoundVector.add( taskList.get( i ) );
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