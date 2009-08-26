package dk.dbc.opensearch.tools.relations;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.trippi.TrippiException;


public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {

        //System.out.println( String.format( "%s", System.getProperties() ) );

        String url = System.getProperty( "url");
        String user = System.getProperty( "user" );
        String pass = System.getProperty( "pass" );
        String query = System.getProperty( "query" );

        System.out.println( String.format( "url  = %s", url) );
        System.out.println( String.format( "user = %s", user ) );
        System.out.println( String.format( "pass = %s", pass ) );
        System.out.println( String.format( "query  = %s", query ) );


        if( ( url == null || user == null || pass == null || query == null ) )
        {
            System.out.println( usage() );
            System.exit( 1 );
        }

        ItqlTool fr = new ItqlTool( url, user, pass );
        try
        {
            fr.testGetObjectRelationships( query );
        }
        catch ( TrippiException ex )
        {
            Logger.getLogger( Main.class.getName() ).log( Level.SEVERE, ex.getMessage(), ex );
        }
        catch ( MalformedURLException ex )
        {
            Logger.getLogger( Main.class.getName() ).log( Level.SEVERE, ex.getMessage(), ex );
        }
        catch ( IOException ex )
        {
            Logger.getLogger( Main.class.getName() ).log( Level.SEVERE, ex.getMessage(), ex );
        }
    }

    private static String usage()
    {
        String usage = "usage:\n\n";
        usage += " java -Durl=[fedora url] -Duser=[fedora user name] -Dpass=[fedora password]\n\n";
        usage += " [url]     The url to the fedora base, eg. http://sempu.dbc.dk:8080/fedora\n";
        usage += " [user]    The username with which to log in to fedora\n";
        usage += " [pass]    The password with which to authorize the user in fedora\n";
        usage += " [pid]     The pid to search for relations to\n";
        return usage;
    }
}
