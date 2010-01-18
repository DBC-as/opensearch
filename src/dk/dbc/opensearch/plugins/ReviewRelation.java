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

import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.common.javascript.JavaScriptWrapperException;
import dk.dbc.opensearch.common.javascript.NaiveJavaScriptWrapper;
import dk.dbc.opensearch.common.javascript.ScriptMethodsForReviewRelation;
import dk.dbc.opensearch.common.javascript.SimpleRhinoWrapper;
import dk.dbc.opensearch.common.pluginframework.IRelation;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import org.apache.log4j.Logger;


/**
 * Plugin for creating relations between reviews and there target
 */
public class ReviewRelation implements IRelation
{
    private static Logger log = Logger.getLogger( ReviewRelation.class );

    // private NaiveJavaScriptWrapper jsWrapper = null;
    private SimpleRhinoWrapper jsWrapper = null;
    private PluginType pluginType = PluginType.RELATION;

    private final String marterialevurderinger = "Materialevurdering:?";
    private final String anmeldelse = "Anmeldelse";
    private final String namespace = "review";
    private IObjectRepository objectRepository;
    private ScriptMethodsForReviewRelation scriptClass;


    /**
     * Constructor for the ReviewRelation plugin.
     */
    public ReviewRelation() throws PluginException
    {
        log.trace( "Constructor called" );
	// Creating the javascript:
	try 
	{
	    // jsWrapper = new NaiveJavaScriptWrapper( "review_relation.js" );
	    jsWrapper = new SimpleRhinoWrapper( "review_relation.js" );
	}
	catch( JavaScriptWrapperException jswe )
	{
	    String errorMsg = new String("An exception occured when trying to instantiate the NaiveJavaScriptWrapper");
	    log.fatal( errorMsg, jswe );
	    throw new PluginException( errorMsg, jswe );
	}
    }


    /**
     * The "main" method of this plugin.
     *
     * @param CargoContainer The CargoContainer to add the reviewOf relation to
     * and be the target of a hasReview relation on another object in the objectRepository
     *
     * @returns A CargoContainer containing relations
     *
     * @throws PluginException thrown if anything goes wrong during annotation.
     */
    public CargoContainer getCargoContainer( CargoContainer cargo ) throws PluginException
    {
        log.trace( "getCargoContainer() called" );
        if( objectRepository == null )
        {
            String msg = "no repository set";
            log.error( msg );
            throw new PluginException( msg );
        }
        boolean ok = false;
        ok = addReviewRelation( cargo );

        if ( ! ok )
        {
            log.error( String.format( "could not add review relation on pid %s", cargo.getIdentifier() ) );
        }

        return cargo;
    }


    private boolean addReviewRelation( CargoContainer cargo ) throws PluginException
    {
        //This mehtod should call a script with the cargocontainer as parameter
        //and expose a getPID and a makeRelation method that enables the script to
        //find the PID of the target of the review and create the hasReview
        //and reviewOf relations
        CargoObject co = cargo.getCargoObject( DataStreamType.OriginalData );
	String submitter = co.getSubmitter();
	String format    = co.getFormat();
       	String language  = co.getLang();
	String XML       = new String( co.getBytes() );

	//       	log.debug( String.format( "TESTING: [%s]", XML ) );

	String entryPointFunc = "main";
        // get the pid of the cargocontainer
        String pid = cargo.getIdentifierAsString();
	try 
	{
	    // Running the javascript (trying two different entrypoints):
	    jsWrapper.run( entryPointFunc, submitter, format, language, XML, pid );
	    // jsWrapper.run( "test2", "merskumspiben", "badehatten" );
	} 
	catch( JavaScriptWrapperException se ) 
	{
	    String errorMsg = String.format( "An excpetion occured when trying to run the javascript: %s with entrypoint function: %s", jsWrapper.getJavascriptName(), entryPointFunc );
	    log.fatal( errorMsg, se );
	    throw new PluginException( errorMsg, se );
	}

        //setting the dc.relation
        String relation = scriptClass.getDCRelation();
        cargo.getDublinCoreMetaData().setRelation( relation );
        return true;
    }


    public PluginType getPluginType()
    {
        return pluginType;
    }

    /**
     * Method to give the plugin an objectRepository
     * There is more that happens there than the name suggests. The scriptClass 
     * is constructed here and "put" onto the jsWrapper. This cannot be done in this 
     * plugin's constructor since it needs the objectRepository.
     * log is put onto here as well so that the putting is kept together
     */
    public void setObjectRepository( IObjectRepository objectRepository )
    {
        this.objectRepository = objectRepository;
        
        scriptClass = new ScriptMethodsForReviewRelation( objectRepository );
        jsWrapper.put( "scriptClass", scriptClass );
        jsWrapper.put( "log", log );
    }

}
