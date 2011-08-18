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

package dk.dbc.opensearch.pluginframework;


import dk.dbc.commons.types.Pair;
import dk.dbc.jslib.Environment;
import dk.dbc.jslib.FileSchemeHandler;
import dk.dbc.jslib.ModuleHandler;
import dk.dbc.jslib.SchemeURI;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class PluginEnvironmentUtils
{
    private static Logger log = LoggerFactory.getLogger( PluginEnvironmentUtils.class );

    
    /**
     * Initializes a JavaScript environment loaded with modules found in
     * supplied script.
     *
     * @param jsFileName {@link String} containing the name of the javascript file to be read.
     * @param objectList {@link List} of properties to add to the javascript scope.
     * @param scriptPath Search path as {@link String} for javascript file and other dependent
     *                   modules.
     *
     * @return environment as {@link Environment}
     *
     * @throws {@link PluginException} when unable to access javascript file.
     */
    public static Environment initializeJavaScriptEnvironment(
                                String jsFileName,
							    List< Pair< String, Object > > objectList,
							    String scriptPath ) throws PluginException
    {
        log.debug( String.format( "JavaScript environment file: %s path: %s", jsFileName, scriptPath ) );

        // Setup a module handler to allow loading modules from the filesystem
        ModuleHandler mh = new ModuleHandler();
        FileSchemeHandler fsh = new FileSchemeHandler( scriptPath );
        mh.registerHandler( "file", fsh );
        mh.addSearchPath( new SchemeURI( "file:." ) );
        // Create the environment and enable the module system.
        Environment env = new Environment();
        env.registerUseFunction( mh );

        // Add objects to scope:
        for ( Pair< String, Object > objectPair : objectList )
        {
            log.debug( String.format( "Adding property: %s", objectPair.getFirst() ) );
            env.put( objectPair.getFirst(), objectPair.getSecond() );
        }

        try
        {
            env.evalFile( scriptPath + jsFileName );
        }
        catch( IOException e )
        {
            String errorMsg = String.format( "Error accessing file: %s", jsFileName );
            log.error( errorMsg, e );
            throw new PluginException( errorMsg, e );
        }

        return env;
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

    /**
     * Validates if a JavaScript function object can be found matching the
     * specified function name in the given JavaScript environment.
     *
     * @param jsEnv JavaScript {@link Environment} to validate against.
     * @param functionEntryPoint name of JavaScript function as {@link String}.
     *
     * @return true if function object exists. False otherwise.
     */
    public static boolean validateJavaScriptFunction( Environment jsEnv, String functionEntryPoint )
    {
        Object fObj = jsEnv.get( functionEntryPoint );
        if ( !( fObj instanceof Function ) )
        {
            String errorMsg = String.format( "%s is undefined or not a function", functionEntryPoint );
            log.error( errorMsg );

            return false;
        }

        return true;
    }
}
