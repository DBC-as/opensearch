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


package dk.dbc.opensearch.common.javascript;

import org.mozilla.javascript.*;
import java.io.*;

import org.apache.log4j.Logger;

import dk.dbc.opensearch.common.config.FileSystemConfig;
import org.apache.commons.configuration.ConfigurationException;

public class SimpleRhinoWrapper
{

    private static Logger log = Logger.getLogger( SimpleRhinoWrapper.class );

    private static String jsFileName = null;

    private Context cx = Context.enter();
    private ScriptableObject scope = null;
    private Script script = null;


    /**
     * Constructs an instans of a javascript environment.
     *
     * @param scriptName The name of the javascript file to associate with the environment.
     */
    public SimpleRhinoWrapper( String scriptName ) throws JavaScriptWrapperException
    {
	// Find javascript-file:
        try
        {
            jsFileName = FileSystemConfig.getScriptPath() + scriptName;
        }
        catch( ConfigurationException ce )
        {
            String errorMsg = String.format( "A ConfigurationExcpetion was cought while trying to construct the path+filename for javascriptfile: %s", scriptName );
            log.fatal( errorMsg, ce );
            throw new JavaScriptWrapperException( errorMsg, ce );
        }

	// Initialize the standard objects (Object, Function, etc.)
	// This must be done before scripts can be executed. Returns
	// a scope object that we use in later calls.                                                            
	scope = cx.initStandardObjects();
	if (scope == null) 
	{
	    // This should never happen!
	    String errorMsg = new String( "An error occured when initializing standard objects for javascript" );
	    log.fatal( errorMsg );
	    throw new JavaScriptWrapperException( errorMsg );
	}
	
	//	JavaScriptHelperFunctions helperFunctions = new JavaScriptHelperFunctions();
	String[] names = { "print" };
	scope.defineFunctionProperties(names, JavaScriptHelperFunctions.class, ScriptableObject.DONTENUM);


	// Create FileReader for the javascriptfile
	FileReader in = null;
	try {
	    in = new FileReader( jsFileName );
	} catch ( FileNotFoundException fnfe ) {
	    String errorMsg = String.format( "Could not find file: %s", jsFileName );
	    log.fatal( errorMsg, fnfe );
	    throw new JavaScriptWrapperException( errorMsg, fnfe );
	}
	
	// Compile the javascript
	try {
	    Object o = cx.evaluateReader((Scriptable)scope, in, jsFileName, 1, null);
	    script = cx.compileReader(in, jsFileName, 1, null);
	} catch ( IOException ioe ) {
	    System.err.println( "Could not run 'evaluateReader'" );
	    // System.exit(1);
	}
 
    }							  

    /**
     * Sets an instans of an object in the Javascript environment making it accesible for the script
     *
     * @param key   The name by which the object should be associated in the javascript 
     * @param value The object
     */
    public void put( String key, Object value )
    {
	scope.defineProperty( key, value, ScriptableObject.DONTENUM );
    }
    
    public Object run( String functionEntryPoint, Object... args ) throws JavaScriptWrapperException
    {

	Object fObj = scope.get(functionEntryPoint, scope);
	Object result = null;
	if (!(fObj instanceof Function)) {
	    String errorMsg = String.format( "% is undefined or not a function", functionEntryPoint );
	    log.fatal( errorMsg );
	    throw new JavaScriptWrapperException( errorMsg );
	} else {
	    Function f = (Function)fObj;
	    result = f.call(cx, scope, scope, args);
	}
	
	return result;
    }

    public String getJavascriptName()
    {
	return jsFileName != null ? jsFileName : "";
    }

}