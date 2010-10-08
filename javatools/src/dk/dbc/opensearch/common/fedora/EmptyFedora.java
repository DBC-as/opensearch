
package dk.dbc.opensearch.common.fedora;

import dk.dbc.opensearch.common.fedora.FedoraObjectFields;
import dk.dbc.opensearch.common.fedora.FedoraObjectRepository;
import dk.dbc.opensearch.common.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.common.fedora.OpenSearchCondition;
import dk.dbc.opensearch.common.types.Pair;

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

    private final static int MAX_TO_REMOVE = 2004; // the last 4 is to compensate for the four fedora system objects.

    static {
	unremovablePIDs.add( "fedora-system:ContentModel-3.0" );
	unremovablePIDs.add( "fedora-system:FedoraObject-3.0" );
	unremovablePIDs.add( "fedora-system:ServiceDefinition-3.0" );
	unremovablePIDs.add( "fedora-system:ServiceDeployment-3.0" );
    }

    public EmptyFedora( boolean log )
    {
	if ( log )
	{
	    setupLogger();
	}

	setupFedora();

	List< String > allObjects = getAllObjects();
	if ( allObjects.size() > MAX_TO_REMOVE )
	{
	    System.out.println( String.format( "Found %s objects, but I am not allowed to remove objects if there are more than %s in the repository. Sorry.", allObjects.size(), MAX_TO_REMOVE ) );
	    return;
	}

	deleteObjects( allObjects );

    }

    public static void main( String[] args ) 
    {
	// Handle command line arguments:
	// \todo: the above

	EmptyFedora ef = new EmptyFedora( true );
    }


    /**
     *  Deletes all pids in the given List, except the pids stated in
     *  the unremovablePids-list, which are fedoras internal objects.
     * 
     *  @param pids A list of pids represented as Strings.
     */
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


    /**
     * Retrieves all objects in the repository
     * Assuming the fedora-field State only can contain one-character values.
     * 
     * @return a list of Strings containing all the pids in the repository.
     */
    private List<String> getAllObjects( )
    {
	
	List resList = new ArrayList< String >();

	String[] resultFields = {"pid"};

	List< OpenSearchCondition > conditions = new ArrayList< OpenSearchCondition >( 1 );
	conditions.add( new OpenSearchCondition( FedoraObjectFields.STATE, OpenSearchCondition.Operator.CONTAINS, "?" ) );

	ObjectFields[] oFields = repos.searchRepository( resultFields, conditions, 3000);

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

    /**
     *  Set fedora up, based on the FedoraObjectRepository.
     *  Reads all values needed to setup fedora from the standard config file.
     */
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

    /**
     *  Sets log4j up, using the values given in the standard config files.
     */
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