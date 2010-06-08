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

import java.io.*;

import org.apache.log4j.Logger;
import org.mozilla.javascript.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ArrayList;
import dk.dbc.opensearch.common.types.Pair;


/**
 * The purpose of the SimpleRhinoWrapper is to make a very simple
 * wrapper for javascript based on Rhino. The class has a very few
 * functions in order to keep the interface simple.  
 * <br> 
 * In order to use the SimpleRhinoWrapper you call the constructor
 * {@link SimpleRhinoWrapper#SimpleRhinoWrapper}.  This takes a
 * javascript filename as argument. The javascriptfile is then loaded,
 * and the javascript is evaluated, ready for being run.  You can add
 * instantiated objects to the javascript-environment, which will then
 * be accessible to the javascript.  This is done by using the function
 * {@link SimpleRhinoWrapper#put put}.  When you are ready to run your
 * javascript, call {@link SimpleRhinoWrapper#run run}
 */
public class SimpleRhinoWrapper
{
    private static Logger log = Logger.getLogger( SimpleRhinoWrapper.class );
    private ScriptableObject scope = null;

    public SimpleRhinoWrapper( String jsFileName ) throws FileNotFoundException
    {
	//	List< Pair< String, Object > > emptyList = new ArrayList< Pair< String, Object > >();
	this( jsFileName, new ArrayList< Pair< String, Object > >() );
    }

    /**
     * Constructs an instance of a javascript environment, containing the given javascript.
     *
     * @param jsFileName A string containing the name and path of the javascript file to be read
     */
    public SimpleRhinoWrapper( String jsFileName, List< Pair< String, Object> > objectList ) throws FileNotFoundException
    {
	FileReader inFile = new FileReader( jsFileName ); // can throw FileNotFindExcpetion

	Context cx = getThreadLocalContext();

        // Initialize the standard objects (Object, Function, etc.)
        // This must be done before scripts can be executed. Returns
        // a scope object that we use in later calls.
        scope = cx.initStandardObjects( null, true ); // true => sealed standard objects
        if ( scope == null )
        {
            // This should never happen!
            String errorMsg = new String( "An error occured when initializing standard objects for javascript" );
            log.fatal( errorMsg );
            throw new IllegalStateException( errorMsg );
        }

        String[] names = { "print" };
        scope.defineFunctionProperties(names, JavaScriptHelperFunctions.class, ScriptableObject.DONTENUM);

	// try 
	// {
	//     ScriptableObject.defineClass( (Scriptable)scope, FedoraSearchJS.class );
	// } 
	// catch( IllegalAccessException iae )
	// {
	// }
	// catch( InstantiationException ie )
	// {
	// }
	// catch( InvocationTargetException ite )
	// {
	// }


        // Evaluate the javascript
        try
        {
            Object o = cx.evaluateReader((Scriptable)scope, inFile, jsFileName, 1, null);

	    // // TEST:
	    // String loadMe = "RegExp; getClass; java; Packages; JavaAdapter;";
	    // o = cx.evaluateString((Scriptable)scope, loadMe, "lazyLoad", 1, null);

        } 
        catch ( IOException ioe )
        {
            String errorMsg = new String( "Could not run 'evaluateReader' on the javascript" );
            log.error( errorMsg, ioe );
            throw new IllegalStateException( errorMsg, ioe );
        }
	catch ( RhinoException re )
	{
	    log.debug( "Evaluate" );

	    logRhinoException( re );

	    throw re;
	}

	// Add objects to scope:
	for ( Pair< String, Object > objectPair : objectList )
	{
	    log.debug( String.format( "Adding property: %s", objectPair.getFirst() ) );
	    scope.defineProperty( objectPair.getFirst(), objectPair.getSecond(), ScriptableObject.DONTENUM );
	}

	// Seal scope:
	scope.sealObject();

    }							  

    
    public Object run( String functionEntryPoint, Object... args )
    {
        log.trace( String.format( "Entering run function with %s", functionEntryPoint ) );

	// synchronized(this) 
	// {
	//     if ( ! scope.isSealed() )
	//     {
	// 	scope.sealObject();
	//     }
	// }

        Object fObj = scope.get( functionEntryPoint, scope );
        Object result = null;
        if ( !( fObj instanceof Function ) )
        {
            String errorMsg = String.format( "%s is undefined or not a function", functionEntryPoint );
            log.fatal( errorMsg );
            throw new IllegalStateException( errorMsg );
        }
        else
        {
            log.debug( String.format( "%s is defined or is a function", functionEntryPoint ) );
	    Context cx = getThreadLocalContext();
            Function f = (Function)fObj;
	    try 
	    {
		result = f.call(cx, scope, scope, args);
	    }
	    catch ( RhinoException re )
	    {
		log.debug( "Call" );

		logRhinoException( re );

		throw re;
	    }

        }
        return result;
    }


    private Context getThreadLocalContext() 
    {
	log.trace( "Entering getThreadLocalContext" );
	Context cx = Context.getCurrentContext();
	if ( cx == null )
	{
	    cx = Context.enter();
	    
	    if ( cx == null ) 
	    {
		throw new NullPointerException( "The retrieved Context is null" );
	    }
	}

	log.trace( "Leaving getThreadLocalContext" );
	return cx;
    }


    /**
     * This is a private function for logging a RhinoException.
     * It is seldom a good idea to call a function inside a catch-statement,
     * but in this case it is assumed to be in order.
     * The purpose is to have unified logging and only to have one place to 
     * maintain the logging even though the exception is caught severeal places in
     * the code.
     */
    private void logRhinoException( RhinoException re )
    {

	if ( re instanceof EcmaError )
        {
	    EcmaError ee = (EcmaError)re;
	    log.error( String.format( "An EcmaError was caught (details): %s", ee.details() ) );
	    log.error( String.format( "An EcmaError was caught (typename): %s", ee.getName() ) );
	    log.error( String.format( "An EcmaError was caught (message): %s", ee.getMessage() ) );
	}
	else if ( re instanceof JavaScriptException )
	{
	    JavaScriptException jse = (JavaScriptException)re;
	    log.error( String.format( "JavaScriptException (details): %s", jse.details() ) );
	    log.error( String.format( "JavaScriptException (value): %s", jse.getValue() ) );
	}

	// print for all exceptions:
	log.error( String.format( "RhinoException (source_name) %s", re.sourceName() ) );
	log.error( String.format( "RhinoException (line_number) %s", re.lineNumber() ) );
	log.error( String.format( "RhinoException (column_number) %s", re.columnNumber() ) );
	log.error( String.format( "RhinoException (line_source) %s", 
				  re.lineSource() != null ? re.lineSource() : "null" ) );
	log.error( String.format( "RhinoException (scriptStackTrace) %s", re.getScriptStackTrace() ) );
    }


}
