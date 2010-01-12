package dk.dbc.opensearch.common.fedora;

import dk.dbc.opensearch.common.types.TargetFields;
import dk.dbc.opensearch.common.types.InputPair;

import java.util.List;
import java.util.ArrayList;
/**
 * testing the IObjectRepository.getIdentifiers method
 * The String named value must be the part of the dc.identifier 
 * u are looking for! 
 */
public class GetIdentifiersFunc
{

    private static IObjectRepository repository;

    public static void main( String[] args )
    {
        System.out.println( "Entering main in GetIdentifiersFunc" );

        try
        {
            repository = new FedoraObjectRepository();
        }
        catch( Exception e )
        {
            System.out.println( "exception caught. Message: " + e.getMessage() );
            System.exit(1);
        }

        System.out.println( "initializing values" );
        TargetFields field = FedoraObjectFields.IDENTIFIER; 
        String value = "*:27768792";
        InputPair<TargetFields, String> thePair = new InputPair( field, value );
        List<InputPair<TargetFields, String>> searchList = new ArrayList<InputPair<TargetFields, String>>();
        searchList.add( thePair);

        System.out.println( "sending request to the repository" );
        List<String> results = repository.getIdentifiers( searchList, null, 50);

        int size = results.size();
        System.out.println( "Number of pids found: "+ size );

        for( int i = 0; i < size; i++)
        {
            System.out.println( results.get( i ) );
        }
        System.out.println( "Leaving main in GetIdentifiersFunc" );
    }
}