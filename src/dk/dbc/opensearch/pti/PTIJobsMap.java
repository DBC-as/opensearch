/*
 *
 * NOTE TO DEVELOPERS: 
 * The two plugins IndexerXSEM and NormalizeDocument are _strongly_ dependend on this class. 
 * It is therefore currently not trivial to split the plugins from the PTI.
 * Nothing will be done about it presently, since the PTI supposedly is dying a slow death!
 *
 */

package dk.dbc.opensearch.pti;


import dk.dbc.commons.types.Pair;
import dk.dbc.opensearch.config.FileSystemConfig;
import dk.dbc.opensearch.config.PtiConfig;
import dk.dbc.opensearch.pluginframework.JobMapCreator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


public class PTIJobsMap extends JobMapCreator
{
    static Logger log = Logger.getLogger( PTIJobsMap.class );

    private static boolean initiated = false;
    private static ArrayList< String > ptiPluginsList = new ArrayList< String >();
    private static HashMap< Pair< String, String >, ArrayList< String > > ptiJobMap;
    private static HashMap< String, String> ptiAliasMap; 

    public PTIJobsMap() {}

    public static ArrayList< String > getPtiPluginsList( String submitter, String format ) throws ConfigurationException, IllegalArgumentException, IllegalStateException, IOException, SAXException, ParserConfigurationException
    {
        if( !initiated || ptiJobMap.isEmpty() )
        {
            initiate();
        }

        ptiPluginsList = ptiJobMap.get( new Pair< String, String >( submitter, format ) );
        return ptiPluginsList;
    }

    public static String getAlias( String submitter, String format ) throws ConfigurationException, IOException, SAXException, ParserConfigurationException
    {
        if( !initiated || ptiAliasMap.isEmpty() )
        {
            initiate();
        }  

        String alias = ptiAliasMap.get( submitter + format );

        if( alias == null )
        {
            String error = String.format( "submitter: '%s' and format: '%s' returned no alias", submitter, format );
            log.error( error );
            throw new IllegalStateException( error );
        }

        return alias;
    }

    private static void initiate() throws ConfigurationException, IOException, SAXException, ParserConfigurationException
    {
        String XMLPath = PtiConfig.getPath();
        String XSDPath = FileSystemConfig.getPTIJobsXsdPath();
        
        JobMapCreator.validateXsdJobXmlFile( XMLPath, XSDPath );
        JobMapCreator.init( XMLPath );
        
        ptiJobMap = jobMap;
        ptiAliasMap = aliasMap;
        initiated = true; 
    }
}
