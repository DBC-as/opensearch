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

import dk.dbc.opensearch.common.config.FileSystemConfig;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.javascript.E4XXMLHeaderStripper;
import dk.dbc.opensearch.common.javascript.JSFedoraPIDSearch;
import dk.dbc.opensearch.common.javascript.ScriptMethodsForReviewRelation;
import dk.dbc.opensearch.common.javascript.SimpleRhinoWrapper;
import dk.dbc.opensearch.common.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.common.types.Pair;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


public class ReviewRelationEnvironment implements IPluginEnvironment
{

    private static Logger log = Logger.getLogger( ReviewRelationEnvironment.class );

    private SimpleRhinoWrapper jsWrapper = null;

    private final String marterialevurderinger = "Materialevurdering:?";
    private final String anmeldelse = "Anmeldelse";
    private final String namespace = "review";
    private IObjectRepository objectRepository;

    private ScriptMethodsForReviewRelation scriptClass;
    private String script = null;

    public ReviewRelationEnvironment( IObjectRepository repository, Map<String, String> args ) throws PluginException
    {

        this.objectRepository = repository;
        String jsFileName;
	// Creating the javascript:
        if( script == null )
        {
            jsFileName = new String( "review_relation.js" );
	}
        else
        {
            jsFileName = new String( script );
        }

        JSFedoraPIDSearch fedoraPIDSearch = new JSFedoraPIDSearch( objectRepository );
        scriptClass = new ScriptMethodsForReviewRelation( objectRepository );
	List< Pair< String, Object > > objectList = new ArrayList< Pair< String, Object > >();
	objectList.add( new InputPair< String, Object >( "Log", log ) );
	objectList.add( new InputPair< String, Object >( "scriptClass", scriptClass ) );
	objectList.add( new InputPair< String, Object >( "FedoraPIDSearch", fedoraPIDSearch ) );

        try 
	{
	    jsWrapper = new SimpleRhinoWrapper( FileSystemConfig.getScriptPath() + jsFileName, objectList );
	}
	catch( FileNotFoundException fnfe )
	{
	    String errorMsg = String.format( "Could not find the file: %s", jsFileName );
	    log.error( errorMsg, fnfe );
	    throw new PluginException( errorMsg, fnfe );
	}
	catch( ConfigurationException ce )
	{
	    String errorMsg = String.format( "A ConfigurationExcpetion was cought while trying to construct the path+filename for javascriptfile: %s", jsFileName );
	    log.fatal( errorMsg, ce );
	    throw new PluginException( errorMsg, ce );
	}
    }


    public boolean addReviewRelation( CargoContainer cargo )
    {
        //This mehtod should call a script with the cargocontainer as parameter
        //and expose a getPID and a makeRelation method that enables the script to
        //find the PID of the target of the review and create the hasReview
        //and reviewOf relations
        CargoObject co = cargo.getCargoObject( DataStreamType.OriginalData );
	String submitter = co.getSubmitter();
	String format    = co.getFormat();
       	String language  = co.getLang();
	String XML       = new String( E4XXMLHeaderStripper.strip( co.getBytes() ) ); // stripping: <?xml...?>


	String entryPointFunc = "main";
        String pid = cargo.getIdentifierAsString(); // get the pid of the cargocontainer
	jsWrapper.run( entryPointFunc, submitter, format, language, XML, pid );

        return true;
    }

}