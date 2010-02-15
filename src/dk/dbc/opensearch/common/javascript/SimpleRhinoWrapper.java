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

//import dk.dbc.opensearch.common.config.FileSystemConfig;
//import org.apache.commons.configuration.ConfigurationException;

/**
 * The purpose of the SimpleRhinoWrapper is to make a very simple wrapper for javascript based 
 * on Rhino. The class has a very few functions in order to keep the interface simple.
 * <br>
 * In order to use the SimpleRhinoWrapper you call the constuctor {@link SimpleRhinoWrapper#SimpleRhinoWrapper}. 
 * This takes a javascript filename as argument. The javascriptfile is then loaded, and the javascript is 
 * evaluated, ready for being run.
 * You can add instantiated objects to the javascript-environment, which then is accessible to the javascript.
 * This is done using the function {@link SimpleRhinoWrapper#put put}. 
 * When you are ready to run your javascript you call {@link SimpleRhinoWrapper#run run}
 */
public class SimpleRhinoWrapper
{

    private static Logger log = Logger.getLogger( SimpleRhinoWrapper.class );

    private Context cx = Context.enter();
    private ScriptableObject scope = null;


    /**
     * Constructs an instans of a javascript environment, containing the given javascript.
     *
     * @param inFile A FileReader containing the javascript file to be read
     */
    public SimpleRhinoWrapper( FileReader inFile )
    {

	// Initialize the standard objects (Object, Function, etc.)
	// This must be done before scripts can be executed. Returns
	// a scope object that we use in later calls.                                                            
	scope = cx.initStandardObjects();
	if (scope == null) 
	{
	    // This should never happen!
	    String errorMsg = new String( "An error occured when initializing standard objects for javascript" );
	    log.fatal( errorMsg );
	    throw new IllegalStateException( errorMsg );
	}
	
	String[] names = { "print" };
	scope.defineFunctionProperties(names, JavaScriptHelperFunctions.class, ScriptableObject.DONTENUM);



	// Evaluate the javascript
	String jsFileName = "script name is unknown"; // We dont know the name of the script! :(
	try {
	    Object o = cx.evaluateReader((Scriptable)scope, inFile, jsFileName, 1, null);
	} catch ( IOException ioe ) {
	    String errorMsg = new String( "Could not run 'evaluateReader' on the javascript" );
	    log.error( errorMsg, ioe );
	    throw new IllegalStateException( errorMsg, ioe );
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
    
    public Object run( String functionEntryPoint, Object... args )
    {

	Object fObj = scope.get(functionEntryPoint, scope);
	Object result = null;
	if (!(fObj instanceof Function)) {
	    String errorMsg = String.format( "% is undefined or not a function", functionEntryPoint );
	    log.fatal( errorMsg );
	    throw new IllegalStateException( errorMsg );
	} else {
	    Function f = (Function)fObj;
	    result = f.call(cx, scope, scope, args);
	}
	
	return result;
    }


}