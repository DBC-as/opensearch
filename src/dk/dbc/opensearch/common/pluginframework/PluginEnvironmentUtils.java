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
 * \brief This class contains utilities commen for the PluginEnvironments, e.g. argument-validation.
 *
 */

package dk.dbc.opensearch.common.pluginframework;


import dk.dbc.opensearch.common.config.FileSystemConfig;
import dk.dbc.opensearch.common.javascript.SimpleRhinoWrapper;
import dk.dbc.opensearch.common.types.IPair;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


public final class PluginEnvironmentUtils
{


    private static Logger log = Logger.getLogger( PluginEnvironmentUtils.class );

    /**
     * Initializes a SimpleRhinoWrapper given a javascript filename and a list of objects used to load into the rhino-scope.
     * 
     */
    public static SimpleRhinoWrapper initializeWrapper( String jsFileName, List< IPair< String, Object > > objectList ) throws PluginException
    {

	SimpleRhinoWrapper wrapper = null;
        try 
	{
	    wrapper = new SimpleRhinoWrapper( FileSystemConfig.getScriptPath() + jsFileName, objectList );
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

	log.trace( "Checking wrapper (inner)" );
	if (wrapper == null) {
	    log.trace("Wrapper is null");
	    throw new PluginException( "After construction of RhinoWrapper it was still null." );
	} else {
	    log.trace("Wrapper is initialized");
	}

	return wrapper;

    }

    /**
     * Validates whether an argumentname is found in the argument-map. 
     * This is mostly a wrapper in order to not write the same log-lines 
     * each time you want to validate an argumentname.
     * 
     * @param argumentName argument to look for.
     * @param args map in which to look.
     *
     * @return true if argument is found in the map. False otherwise.
     */
    public static boolean validateMandatoryArgumentName( String argumentName, Map< String, String > args )
    {
	log.trace( String.format( "Validating argumentName: %s", argumentName ) );
	if ( ! args.containsKey( argumentName ) ) 
	{
	    // This is an error We cannot create the environment.
	    log.error( String.format( "Could not find mandatory argumentName: \"%s\"", argumentName ) );
	    return false;
	}
	log.trace( String.format( "Found value: %s", args.get( argumentName ) ) );
	return true;
    }


}