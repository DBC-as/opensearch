
package dk.dbc.opensearch.common.javascript;

import dk.dbc.opensearch.common.fedora.FedoraObjectFields;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.types.TargetFields;
import dk.dbc.opensearch.common.types.Pair;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class JSFedoraPIDSearch {

    private Logger log = Logger.getLogger( JSFedoraPIDSearch.class );
    private IObjectRepository repository;

    public JSFedoraPIDSearch( IObjectRepository repository ) 
    {
        this.repository = repository;
    }


    public String[] pid( String searchValue )
    {
        log.info( String.format( "PID called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.PID, searchValue );
    }
    public String[] label( String searchValue )
    {
        log.info( String.format( "LABEL called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.LABEL, searchValue );
    }
    public String[] state( String searchValue )
    {
        log.info( String.format( "STATE called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.STATE, searchValue );
    }
    public String[] ownerid( String searchValue )
    {
        log.info( String.format( "OWNERID called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.OWNERID, searchValue );
    }
    public String[] cdate( String searchValue )
    {
        log.info( String.format( "CDATE called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.CDATE, searchValue );
    }
    public String[] mdate( String searchValue )
    {
        log.info( String.format( "MDATE called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.MDATE, searchValue );
    }
    public String[] title( String searchValue )
    {
        log.info( String.format( "TITLE called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.TITLE, searchValue );
    }
    public String[] creator( String searchValue )
    {
        log.info( String.format( "CREATOR called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.CREATOR, searchValue );
    }
    public String[] subject( String searchValue )
    {
        log.info( String.format( "SUBJECT called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.SUBJECT, searchValue );
    }
    public String[] description( String searchValue )
    {
        log.info( String.format( "DESCRIPTION called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.DESCRIPTION, searchValue );
    }
    public String[] publisher( String searchValue )
    {
        log.info( String.format( "PUBLISHER called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.PUBLISHER, searchValue );
    }
    public String[] contributor( String searchValue )
    {
        log.info( String.format( "CONTRIBUTOR called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.CONTRIBUTOR, searchValue );
    }
    public String[] date( String searchValue )
    {
        log.info( String.format( "DATE called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.DATE, searchValue );
    }
    public String[] type( String searchValue )
    {
        log.info( String.format( "TYPE called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.TYPE, searchValue );
    }
    public String[] format( String searchValue )
    {
        log.info( String.format( "FORMAT called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.FORMAT, searchValue );
    }
    public String[] identifier( String searchValue )
    {
        log.info( String.format( "IDENTIFIER called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.IDENTIFIER, searchValue );
    }
    public String[] source( String searchValue )
    {
        log.info( String.format( "SOURCE called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.SOURCE, searchValue );
    }
    public String[] language( String searchValue )
    {
        log.info( String.format( "LANGUAGE called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.LANGUAGE, searchValue );
    }
    public String[] relation( String searchValue )
    {
        log.info( String.format( "RELATION called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.RELATION, searchValue );
    }
    public String[] coverage( String searchValue )
    {
        log.info( String.format( "COVERAGE called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.COVERAGE, searchValue );
    }
    public String[] rights( String searchValue )
    {
        log.info( String.format( "RIGHTS called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.RIGHTS, searchValue );
    }
    public String[] dcmdate( String searchValue )
    {
        log.info( String.format( "DCMDATE called with: %s ", searchValue ) );
	return single_field_search( (TargetFields)FedoraObjectFields.DCMDATE, searchValue );
    }


    private String[] single_field_search( TargetFields targetField, String searchValue )
    {
	log.info( String.format( "Entering with targetfield=%s and value=%s", targetField, searchValue ) );

        //call the IObjectRepository.getIdentifiers method with the above values,
        //no cutIdentifier and the number of submitters in the maximumResults 
        List<Pair<TargetFields, String>> searchFields = new ArrayList<Pair<TargetFields, String>>();
        searchFields.add( new Pair<TargetFields, String>( targetField, searchValue ) );

	// \note: 10000 below is a hardcodet estimate on max amount of results:
        List<String> resultList = repository.getIdentifiers( searchFields, null, 10000 );

	// Convert the List of Strings to a String array in order to satisfy javascripts internal types:
	String[] sa = new String[resultList.size()];
	int counter = 0;
	for( String str : resultList ) 
	{
	    log.info( String.format( "returning pid: %s", str ) );
	    sa[counter++] = str;
	}
        log.info( String.format( "returned %s results", counter ) );
	return sa;

    }




}