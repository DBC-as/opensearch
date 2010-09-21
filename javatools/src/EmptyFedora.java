
package dk.dbc.opensearch.common.fedora;

import dk.dbc.opensearch.common.fedora.FedoraObjectFields;
import dk.dbc.opensearch.common.fedora.FedoraObjectRepository;
import dk.dbc.opensearch.common.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.common.fedora.OpenSearchCondition;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.common.types.TargetFields;

import fedora.server.types.gen.ObjectFields;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Log4j:
import dk.dbc.opensearch.common.helpers.Log4jConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

public class EmptyFedora 
{

    private static Logger log = Logger.getLogger( EmptyFedora.class );
    private static final String logConfiguration = "log4j_datadock.xml";
    private final static ConsoleAppender startupAppender = new ConsoleAppender( new SimpleLayout() );

    private FedoraObjectRepository repos = null;

    private static List unremovablePIDs = new ArrayList< String >();

    static {
	unremovablePIDs.add( "fedora-system:ContentModel-3.0" );
	unremovablePIDs.add( "fedora-system:FedoraObject-3.0" );
	unremovablePIDs.add( "fedora-system:ServiceDefinition-3.0" );
	unremovablePIDs.add( "fedora-system:ServiceDeployment-3.0" );
    }

    public EmptyFedora()
    {
	setupLogger();

	setupFedora();

	log.debug("Hej");

	List< String > PIDsStateI = getAllAtState("I");
	List< String > PIDsStateA = getAllAtState("A");
	List< String > PIDsStateD = getAllAtState("D");

	deleteObjects( PIDsStateI );
	deleteObjects( PIDsStateA );
	deleteObjects( PIDsStateD );

    }

    public static void main( String[] args ) 
    {
	EmptyFedora ef = new EmptyFedora();
    }


    private void deleteObjects( List< String > pids )
    {
	for ( String pid : pids )
	{
	    boolean delete = true;
	    for ( String doNotDeletePid : ( List< String > )unremovablePIDs ) 
	    {
		if ( pid.equals( doNotDeletePid ) )
		{
		    // Do not delete this pid!
		    delete = false;
		}
	    }
	    if ( delete )
	    {
		log.debug( String.format( "Deleting object with pid: %s", pid ) );
		try {
			repos.deleteObject( pid, "Deleted by EmptyFedora" );
		} catch ( ObjectRepositoryException orex ) {
			log.info( String.format( "Could not delete Object with pid: %s", pid ), orex );
		}
	    }
	}
    }

    private List<String> getAllAtState( String state )
    {
	List resList = new ArrayList< String >();

	String[] resultFields = {"pid"};

	List< OpenSearchCondition > conditions = new ArrayList< OpenSearchCondition >( 1 );
	conditions.add( new OpenSearchCondition( FedoraObjectFields.STATE, OpenSearchCondition.Operator.EQUALS, state ) );

	ObjectFields[] oFields = repos.searchRepository( resultFields, conditions, 1000);

	// String[] resultFields = {"pid"};
	// Pair pair = new Pair< TargetFields, String >( FedoraObjectFields.STATE, state);
	// List l = new ArrayList< Pair< TargetFields, String > >();
	// l.add(pair);
	// ObjectFields[] oFields = repos.searchRepository( resultFields, l, "has", 2000, null);

	int counter = 0;
	for (ObjectFields field : oFields )
	{
	    counter++;
	    log.trace( String.format( "Found pid: %s", field.getPid() ) );
	    if ( field != null )
	    {
		resList.add( field.getPid() ); 
	    }
	}
	log.debug( String.format( "Found %d objects", counter ) );

	return resList;
    }

    private void setupFedora()
    {
	try
	{
	    repos = new FedoraObjectRepository();
	}
	catch( ObjectRepositoryException orex )
	{
	    log.debug( String.format( "Error when connecting to Fedora: %s", orex ) );
	}

    }

    private static void setupLogger()
    {
        try
        {
            Log4jConfiguration.configure( logConfiguration );
        }
        catch( ConfigurationException ex )
        {
            System.out.println( String.format( "Logger could not be configured, will continue without logging: %s", ex.getMessage() ) );
        }

        log.removeAppender( "RootConsoleAppender" );
        log.addAppender( startupAppender );

    }

}