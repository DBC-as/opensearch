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

import java.lang.reflect.InvocationTargetException;

public class SimpleRhinoWrapper
{

    private static Logger log = Logger.getLogger( SimpleRhinoWrapper.class );

    private static String jsFileName = null;

    private Context cx = Context.enter();
    private ScriptableObject scope = null;
    private Script script = null;


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
	    script = cx.compileReader(scope, in, jsFileName, 1, null);
	} catch ( IOException ioe ) {
	    System.err.println( "Could not run 'evaluateReader'" );
	    System.exit(1);
	}
 
    }							  

    public void put( String key, Object value )
    {
	log.warn("Not yet implemented");
	/*	
	// ScriptableObject.defineClass( scope, value );
	try
	{
	    ScriptableObject.defineClass( (Scriptable)scope, ScriptMethodsForReviewRelation.class );
	    Scriptable tmp = cx.newObject( scope, key );
	} 
	catch( IllegalAccessException iae ) 
	{
	    String errorMsg = String.format( "An exception was cought while trying to add the class %s to the javascript", key );
	    log.fatal( errorMsg, iae );
	    // throw new JavaScriptWrapperException( errorMsg, iae );
	}
	catch( InstantiationException ie ) 
	{
	    String errorMsg = String.format( "An exception was cought while trying to add the class %s to the javascript", key );
	    log.fatal( errorMsg, ie );
	    // throw new JavaScriptWrapperException( errorMsg, ie );
	}
	catch( InvocationTargetException ite ) 
	{
	    String errorMsg = String.format( "An exception was cought while trying to add the class %s to the javascript", key );
	    log.fatal( errorMsg, ite );
	    // throw new JavaScriptWrapperException( errorMsg, ite );
	}
	*/
    }
    
    public void run( String functionEntryPoint, Object... args ) throws JavaScriptWrapperException
    {
	log.warn("Not yet implemented");

	// script.exec( cx, scope );
	
	// scope.callMethod( cx, scope, "test", null );

	// String func = "test2";
	String func = functionEntryPoint;

	Object fObj = scope.get(func, scope);
	// Object fObj = scope.get(func, script);
	if (!(fObj instanceof Function)) {
	    System.out.println(func + " is undefined or not a function.");
	} else {
	    // Object functionArgs[] = { "jukeboxen", "en foliehat"  };
	    Function f = (Function)fObj;
	    // Object result = f.call(cx, scope, scope, functionArgs);
	    Object result = f.call(cx, scope, scope, args);
	    //	    String report = "f('my args') = " + Context.toString(result);
	    //	    System.out.println(report);
	}

	
    }

    public String getJavascriptName()
    {
	return jsFileName != null ? jsFileName : "";
    }

}