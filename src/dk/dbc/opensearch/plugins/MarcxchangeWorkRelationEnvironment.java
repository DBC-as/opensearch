/*
  This file is part of opensearch.
  Copyright © 2009, Dansk Bibliotekscenter a/s,
  Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043

  opensearch is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  opensearch is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * \file
 * \brief
 */


package dk.dbc.opensearch.plugins;


import dk.dbc.commons.types.Pair;
import dk.dbc.commons.javascript.E4XXMLHeaderStripper;
import dk.dbc.jslib.Environment;
import dk.dbc.opensearch.fedora.FcrepoModifier;
import dk.dbc.opensearch.fedora.FcrepoReader;
import dk.dbc.opensearch.fedora.FedoraObjectFields;
import dk.dbc.opensearch.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.fedora.OpenSearchCondition;
import dk.dbc.opensearch.fedora.PID;
import dk.dbc.opensearch.metadata.DBCBIB;
import dk.dbc.opensearch.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.pluginframework.PluginEnvironmentUtils;
import dk.dbc.opensearch.pluginframework.PluginException;
import dk.dbc.opensearch.types.CargoContainer;
import dk.dbc.opensearch.types.DataStreamType;
import dk.dbc.opensearch.types.ITargetField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarcxchangeWorkRelationEnvironment implements IPluginEnvironment
{

    private static Logger log = LoggerFactory.getLogger( MarcxchangeWorkRelationEnvironment.class );
    private final FcrepoReader reader;
    private final FcrepoModifier modifier;
    private Environment jsEnvironment;

    private final String searchFunc;
    private final String matchFunc;
    private final String createObjectFunc;

    // For validation:
    private final static String javascriptStr       = "javascript";
    private final static String searchFuncStr       = "searchfunction";
    private final static String matchFuncStr        = "matchfunction";
    private final static String createObjectFuncStr = "createobjectfunction";



    public MarcxchangeWorkRelationEnvironment( FcrepoReader reader, FcrepoModifier modifier,
					       Map<String, String> args,
					       String scriptPath ) throws PluginException
    {
        this.reader = reader;
        this.modifier = modifier;

        // Creates a list of objects to be used in the js-scope
        List<Pair<String, Object>> objectList = new ArrayList<Pair<String, Object>>();
        objectList.add( new Pair<String, Object>( "Log", log ) );

        this.validateArguments( args, objectList, scriptPath ); // throws PluginException in case of trouble!

        this.searchFunc = args.get( MarcxchangeWorkRelationEnvironment.searchFuncStr );
        this.matchFunc = args.get( MarcxchangeWorkRelationEnvironment.matchFuncStr );
        this.createObjectFunc = args.get( MarcxchangeWorkRelationEnvironment.createObjectFuncStr );

        this.jsEnvironment = PluginEnvironmentUtils.initializeJavaScriptEnvironment( 
                                    args.get( MarcxchangeWorkRelationEnvironment.javascriptStr ),
                                    objectList, scriptPath );
    }



    /**
     * The main method of the plugin. first it creates a list of work candidates, 
     * based upon a list of searchpairs then it checks if any candidates are an actual
     * match. If there are a match the post and the work are related. If not a new 
     * work is created based on the post, stored in the repository and then the post 
     * and the new work are related.
     * @param cargo a CargoContianer representing the post that are checked for a match
     * @param searchPairs a list of values and fields corresponding to searchable fields 
     * in the repository, used to generate the list of candidate works 
     * @return the unmodified CargoContainer.
     */
    public CargoContainer run( CargoContainer cargo, List< Pair< ITargetField, String > > searchPairs ) throws PluginException
    {

        long gwl_timer = System.currentTimeMillis();
        List<PID> pidList = getWorkList( searchPairs );
        gwl_timer = System.currentTimeMillis() - gwl_timer;
        log.info( String.format( "KULMULE getWorkList Timing: time: %s", gwl_timer ) );

        log.debug( String.format( "length of pidList: %s", pidList.size() ) );

        long cm_timer = System.currentTimeMillis();
        PID workPid = checkMatch( cargo, pidList );
        cm_timer = System.currentTimeMillis() - cm_timer;
        log.info( String.format( "KULMULE checkMatch Timing: time: %s", cm_timer ) );

        if( workPid == null )
        {
            log.info( "no matching work found creating and storing one" );
            long casw_timer = System.currentTimeMillis();
            workPid = createAndStoreWorkobject( cargo );
            casw_timer = System.currentTimeMillis() - casw_timer;
            log.info( String.format( "KULMULE createAndStoreWorkobject Timing: time: %s", casw_timer ) );
        }

        log.debug( "cargo pid: " + cargo.getIdentifier().getIdentifier() );
        log.debug( "work pid: " + workPid.getIdentifier() );
        log.debug( "Relating object and work" );
        long rpaw_timer = System.currentTimeMillis();
        relatePostAndWork( (PID) cargo.getIdentifier(), workPid );
        rpaw_timer = System.currentTimeMillis() - rpaw_timer;
        log.info( String.format( "KULMULE relatePostAndWork Timing: time: %s", rpaw_timer ) );

        return cargo;
    }


    /**
     * method that generates the list containing the fields to look in and the
     * corresponding values to match with
     * @param cargo the CargoContianer to generate searchpairs for
     * @return a list of SimplePairs containing a serachfield and the value to match
     */
    public List< Pair< ITargetField, String > > getSearchPairs( CargoContainer cargo ) throws PluginException
    {
        List< Pair< ITargetField, String > > searchList = new ArrayList< Pair< ITargetField, String > >();
        //calls a javascript, with the dc-stream and original data of the
        //cargo as argument, that returns an xml with the pairs to generate
        //start with dummy xml on the javascript-side

        // get the DC-Stream

        byte[] theStrippedDC = E4XXMLHeaderStripper.strip( cargo.getCargoObject( DataStreamType.DublinCoreData ).getBytes() );
        //create dc in string format

        String dcString = new String( theStrippedDC );

        log.debug( String.format( "the dc-string: %s", dcString ) );
        byte[] strippedOrgData = E4XXMLHeaderStripper.strip( cargo.getCargoObject( DataStreamType.OriginalData ).getBytes() );
        String orgData = new String ( strippedOrgData );

        //give the script the list to put pairs in
        String[] pairArray = new String[ 100 ];

        //execute the script that fills the pairsList
        jsEnvironment.callMethod( searchFunc, new Object[] { dcString, orgData, pairArray } );

        //go through the pairArray
        //create the ITargetField for the searchList
        int length = pairArray.length;
        log.debug( String.format( "length of search list: %s", length ) );
        for ( int i = 0; i < length; i += 2 )
        {
            if ( pairArray[ i ] != null )
            {
                searchList.add( new Pair< ITargetField, String >( (ITargetField)FedoraObjectFields.getFedoraObjectFields( pairArray[i] ), pairArray[ i + 1 ].toLowerCase() ) );
            }
        }

        log.debug( String.format( "length of search list: %s", searchList.size() ) );
        return searchList;
    }


    /**
     * method that finds the workobjects that match the cirterias specified in
     * the searchList
     * @param searchList, a list of targetfields and there names corresponding to
     * searchable fields in the repository. These are used for finding the the candidate 
     * works to check for a match
     * @return List<PID> a list of the work objects that should be checked for a match.
     */
    private List< PID > getWorkList( List< Pair< ITargetField, String > > searchList )
    {
        //for each pair in the searchList, search the repository
        //and add the results together in pidList
        //see if u can remove duples

        int num = 0;
        List<PID> pidList = new ArrayList<PID>();
        List<String> pidStringList = new ArrayList<String>();
        List<String> searchResultList = new ArrayList<String>();

        for( Pair<ITargetField, String> pair : searchList )
        {
            num++;

            List<OpenSearchCondition> conditions = new ArrayList<OpenSearchCondition>( 2 );
            // Add the search Condition:
            conditions.add( new OpenSearchCondition( pair.getFirst(), OpenSearchCondition.Operator.EQUALS, pair.getSecond() ) );
            // Add the default Namespace-condition:
            conditions.add( new OpenSearchCondition( FedoraObjectFields.PID, OpenSearchCondition.Operator.CONTAINS, "work:*" ) );

            Set<String> undeletedStates = new HashSet<String>();
            undeletedStates.add( "I" );
            undeletedStates.add( "A" );

            searchResultList = reader.getIdentifiersByState( conditions, 10000, undeletedStates );

            log.debug( String.format( "searchResultList: %s at search number: %s", searchResultList, num ) );

            //loop to not add duplets
            for( String result : searchResultList )
            {
                if( !(pidStringList.contains( result )) )
                {
                    pidStringList.add( result );
                }
            }
        }

        log.debug( String.format( "pidStringList: %s", pidStringList ) );

        //make PIDs out of the String representations
        for( String pidString : pidStringList )
        {
            log.debug( String.format( "pidString: %s", pidString ) );
            pidList.add( new PID( pidString ) );
        }

        //return the resulting list of PIDs
        return pidList;
    }


    /**
     * method that checks if the cargo has a match with an existing work object
     * the candidates are each checked by a method in javascript
     * @param cargo, the cargoContainer to find a workobject for
     * @param pidList, the list of pids to chack for matches
     * @return the pid of the matching work object, null if none is found
     */
    private PID checkMatch( CargoContainer cargo, List< PID > pidList ) throws PluginException
    {
        log.trace( "calling checkMatch" );
        boolean match = false;
        //get the xml for the cargo
        byte[] theStrippedDC = E4XXMLHeaderStripper.strip( cargo.getCargoObject( DataStreamType.DublinCoreData ).getBytes() );
        String dcString = new String( theStrippedDC );
        log.trace( String.format( "the dcstring: '%s'", dcString ) );

        CargoContainer tempCargo = null;
        byte[] strippedTempDC = null;
        String tempDCString = "";

        log.trace( String.format("length of pidlist: '%s'", pidList.size() ) );

        for( PID pid : pidList )
        {
            log.trace( "entering for loop" );
            //get the objects xml
            try
            {
                /**
                 * \Todo: Improvement,Get only the DC-stream instead of the whole object
                 */
                tempCargo = reader.getObject( pid.getIdentifier() );
            }
            catch( ObjectRepositoryException ore )
            {
                String errorMsg = String.format( "Couldnt get object with PID : %s from Fedora", pid.getIdentifier() );
                log.error( errorMsg );
                throw new PluginException( errorMsg, ore );
            }
            strippedTempDC =  E4XXMLHeaderStripper.strip( tempCargo.getCargoObject( DataStreamType.DublinCoreData ).getBytes() );
            tempDCString = new String( strippedTempDC );

            //Remove end-lines in the string.
            String replacedTempDCString = tempDCString.replace( "\n", "" );
            log.debug( String.format( " matching postdc: %s with workdc: %s", dcString, replacedTempDCString ) ); 
            //call the match test with the xmls until a match occurs
            match = (Boolean)jsEnvironment.callMethod( matchFunc, new Object[] { dcString, replacedTempDCString } );
            log.debug( String.format( "result of match on post: %s on work: %s is %s ", cargo.getIdentifier().getIdentifier(), pid.getIdentifier(), match ) );

            if( match )
            {
                return pid;
            }
            log.debug( String.format( "dcString:%s, didnt match this: %s", dcString, replacedTempDCString  ) );

        }

        //if no match occur, return null
        return null;
    }


    /**
     * method that creates a workobject from a CargoContainer. 
     * The javascript that gets the OriginalData of the cargocontainer 
     * defines which parts of the data to use. 
     * The workobject is stored in the objectrepository.
     * @param cargo, the post to creare a workobject from
     * @return the pid of the new workobject
     */
    private PID createAndStoreWorkobject( CargoContainer cargo ) throws PluginException
    {

        /**
         * \todo: Remove the DublinCore object and get a string back from the script instead
         * please see bug 11027
         */
        CargoContainer workCargo = new CargoContainer();
        //get the cargos data
        byte[] theData = cargo.getCargoObject( DataStreamType.OriginalData ).getBytes();
        String tempDataString = new String ( E4XXMLHeaderStripper.strip( theData ) );

        //call the javascript that creates a workobject xml from a cargo data
        log.info( "calling makeworkobject" );

        String workXml = (String)jsEnvironment.callMethod( createObjectFunc, new Object[] { tempDataString } );

        log.debug( "workXml :" + workXml );

        //use the xml to create the work object
        try
        {
            workCargo.add( DataStreamType.OriginalData, "format", "internal", "da", "text/xml",  workXml.getBytes() );
        }
        catch( IOException ioe )
        {
            String errorMsg = "Exception adding data to an empty CargoContainer";
            log.error( errorMsg, ioe );
            throw new PluginException( errorMsg, ioe);
        }

        try
        {
            log.trace( String.format( "Trying to add workdcstream: '%s'", workXml ) );
            workCargo.add( DataStreamType.DublinCoreData, "format", "internal","da", "text/xml", workXml.getBytes() );
        }
        catch( IOException ioe )
        {
            String error = String.format( "Error when to CargoContainer.add with the bytes:'%s'", workXml );
            log.error( error, ioe );
            throw new PluginException( error, ioe );
        }
        //store it in the objectrepository
        try
        {
            this.modifier.storeObject( workCargo, "internal", "work");
        }
        catch ( ObjectRepositoryException ore)
        {
            String errorMsg = "Exception when trying to store work object";
            log.error( errorMsg );
            throw new PluginException( errorMsg, ore);
        }
        //return the PID of the new workobject
        return (PID)workCargo.getIdentifier();
    }


    /**
     * method that relates a post to a work object and the inverse
     * @param cargoPid, the pid of the post
     * @param workPid, the pid of the work
     */
    private void relatePostAndWork( PID cargoPid, PID workPid ) throws PluginException
    {
        log.info( "entering relatePostAndWork" );
        //call the addRelation method on the objectRepository both ways

        try
        {
            this.modifier.addObjectRelation( workPid.getIdentifier(), DBCBIB.HAS_MANIFESTATION, cargoPid.getIdentifier() );

        }
        catch( ObjectRepositoryException ore )
        {
            String errorMsg = String.format( "Error when trying to add relation HAS_MANIFESTATION between workpid: %s and object: %s", workPid.getIdentifier(), cargoPid.getIdentifier() ) ;
            log.error( errorMsg );
            throw new PluginException( errorMsg, ore );

        }

        try{
            this.modifier.addObjectRelation( cargoPid.getIdentifier(), DBCBIB.IS_MEMBER_OF_WORK, workPid.getIdentifier() );
        }
        catch( ObjectRepositoryException ore )
        {
            String errorMsg = String.format( "Error when trying to add relation IS_MEMBER_OF_WORK  between object: %s and work: %s", cargoPid.getIdentifier(), workPid.getIdentifier() ) ;
            log.error( errorMsg );
            throw new PluginException( errorMsg, ore );
        }

        log.info( String.format( "post: %s related to work: %s", cargoPid.getIdentifier(), workPid.getIdentifier() ) );
    }



    /**
     * This function will validate the following arguments:
     * "javascript", "searchfunction", "matchfunction" and "createobjectfunction".
     */
    private void validateArguments( Map< String, String > args, 
				    List< Pair< String, Object > > objectList,
				    String scriptPath ) throws PluginException
    {
        log.info( "Validating Arguments - Begin" );

        // Validating entrys:
        if( !PluginEnvironmentUtils.validateMandatoryArgumentName( javascriptStr, args ) )
        {
            throw new PluginException( String.format( "Could not find argument: %s", javascriptStr ) );
        }
        if( !PluginEnvironmentUtils.validateMandatoryArgumentName( searchFuncStr, args ) )
        {
            throw new PluginException( String.format( "Could not find argument: %s", searchFuncStr ) );
        }
        if( !PluginEnvironmentUtils.validateMandatoryArgumentName( matchFuncStr, args ) )
        {
            throw new PluginException( String.format( "Could not find argument: %s", matchFuncStr ) );
        }
        if( !PluginEnvironmentUtils.validateMandatoryArgumentName( createObjectFuncStr, args ) )
        {
            throw new PluginException( String.format( "Could not find argument: %s", createObjectFuncStr ) );
        }

        Environment tmpJsEnv = PluginEnvironmentUtils.initializeJavaScriptEnvironment( args.get( javascriptStr ), objectList, scriptPath );

        // Validating JavaScript function entries.
        if( !PluginEnvironmentUtils.validateJavaScriptFunction( tmpJsEnv, args.get( MarcxchangeWorkRelationEnvironment.searchFuncStr ) ) )
        {
            throw new PluginException( String.format( "Could not use %s as function in javascript", args.get( MarcxchangeWorkRelationEnvironment.searchFuncStr ) ) );
        }
        if( !PluginEnvironmentUtils.validateJavaScriptFunction( tmpJsEnv, args.get( MarcxchangeWorkRelationEnvironment.matchFuncStr ) ) )
        {
            throw new PluginException( String.format( "Could not use %s as function in javascript", args.get( MarcxchangeWorkRelationEnvironment.matchFuncStr ) ) );
        }
        if( !PluginEnvironmentUtils.validateJavaScriptFunction( tmpJsEnv, args.get( MarcxchangeWorkRelationEnvironment.createObjectFuncStr ) ) )
        {
            throw new PluginException( String.format( "Could not use %s as function in javascript", args.get( MarcxchangeWorkRelationEnvironment.createObjectFuncStr ) ) );
        }

        log.info( "Validating Arguments - End" );
    }



}
