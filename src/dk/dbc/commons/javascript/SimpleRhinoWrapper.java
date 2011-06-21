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


package dk.dbc.commons.javascript;


import dk.dbc.commons.types.Pair;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.mozilla.javascript.*;


/**
 * The purpose of the SimpleRhinoWrapper is to make a very simple
 * wrapper for javascript based on Rhino. The class has a very few
 * functions in order to keep the interface simple.  
 * <br> 
 * In order to use the SimpleRhinoWrapper you call the constructor
 * {@link SimpleRhinoWrapper#SimpleRhinoWrapper}.  This takes a
 * javascript filename as argument. The javascriptfile is then loaded,
 * and the javascript is evaluated, ready for being run.  You can also add
 * instantiated objects to the javascript-environment, which will then
 * be accessible to the javascript.  This is also done in the constructor.
 * When you are ready to run your
 * javascript, call {@link SimpleRhinoWrapper#run run}
 */
public class SimpleRhinoWrapper
{
    private static Logger log = Logger.getLogger( SimpleRhinoWrapper.class );

    
    private final ScriptableObject scope;
    private static ScriptableObject sharedScope;

    protected final static String JS_FILE_PATH_VALUE_STRING = "jsFilePath";

    // List of java script file names that have been loaded.
    // Used to prevent loading the same file more than once.
    private final static Set< String > knownScripts = new HashSet< String >();

    
    private static synchronized ScriptableObject getSharedScope( Context cx)
    {
        if (sharedScope == null)
        {
            sharedScope = cx.initStandardObjects( null, true ); // true => sealed standard objects
        }
        return sharedScope;
    }

    
    public SimpleRhinoWrapper( String jsFilePath, String jsFileName ) throws FileNotFoundException
    {
        this( jsFilePath, jsFileName, new ArrayList<Pair<String, Object>>() );
    }

    /**
     * Constructs an instance of a javascript environment, containing the given javascript.
     *
     * @param jsFileName A string containing the name of the javascript file to be read
     * @param jsFilePath A string containing the path of the javascript file to be read. 
     *                   The file-path will also be used to find other dependent javascripts.
     * @param objectList A list of properties to add to the javascript scope.
     *
     * @throws FileNotFoundException if {@code jsFileName} cannot be found.
     */
    public SimpleRhinoWrapper( String jsFilePath, String jsFileName, List< Pair< String, Object> > objectList ) throws FileNotFoundException
    {
        Context cx = Context.enter();
        try
        {
            // Initialize the standard objects (Object, Function, etc.)
            // This must be done before scripts can be executed. Returns
            // a scope object that we use in later calls.
            scope = getSharedScope( cx );
            if ( scope == null )
            {
                // This should never happen!
                String errorMsg = "An error occured when initializing standard objects for javascript";
                log.fatal( errorMsg );
                throw new IllegalStateException( errorMsg );
            }

            scope.associateValue( JS_FILE_PATH_VALUE_STRING, jsFilePath );
            String[] names = { "print", "use" };
            scope.defineFunctionProperties(names, JavaScriptHelperFunctions.class, ScriptableObject.DONTENUM);

            synchronized (knownScripts)
            {
                // Check that the java script file as not already been loaded in this scope
                if (!knownScripts.contains(jsFileName))
                {
                    FileReader inFile = new FileReader( jsFilePath + jsFileName); // can throw FileNotFindExcpetion
                    try
                    {
                        // Evaluate the javascript
                        Object o = cx.evaluateReader((Scriptable) scope, inFile, jsFileName, 1, null);
                    }
                    catch ( IOException ioe )
                    {
                        String errorMsg = "Could not run 'evaluateReader' on the javascript";
                        log.error( errorMsg, ioe );
                        throw new IllegalStateException( errorMsg, ioe );
                    }
                    catch ( RhinoException re )
                    {
                        log.debug( "Evaluate" );

                        logRhinoException( re );

                        throw re;
                    }
                    finally
                    {
                        // This is ugly - but necessary
                        log.debug( String.format( "Closing file: %s", jsFileName ) );
                        try
                        {
                            inFile.close();
                        }
                        catch( IOException ioe )
                        {
                            log.warn( String.format( "Could not close the file: \"%s\" I will regrettably leave it open." ) );
                        }
                    }
                }
            }
            // Add objects to scope:
            for ( Pair< String, Object > objectPair : objectList )
            {
                log.debug( String.format( "Adding property: %s", objectPair.getFirst() ) );
                scope.defineProperty( objectPair.getFirst(), objectPair.getSecond(), ScriptableObject.DONTENUM );
            }
        }
        finally
        {
            Context.exit();
        }
    }


    public boolean validateJavascriptFunction( String functionEntryPoint )
    {
        Context cx = Context.enter();
        try
        {
            Object fObj = scope.get( functionEntryPoint, scope );
            if ( !( fObj instanceof Function ) )
            {
                String errorMsg = String.format( "%s is undefined or not a function", functionEntryPoint );
                log.error( errorMsg );

                return false;
            }

            return true;
        }
        finally
        {
            Context.exit();
        }
    }


    public Object run( String functionEntryPoint, Object... args )
    {
        log.trace( String.format( "Entering run function with %s", functionEntryPoint ) );

        Context cx = Context.enter();
        try
        {
            Object fObj = scope.get( functionEntryPoint, scope );
            if ( !( fObj instanceof Function ) )
            {
                String errorMsg = String.format( "%s is undefined or not a function", functionEntryPoint );
                log.fatal( errorMsg );
                throw new IllegalStateException( errorMsg );
            }
            else
            {
                log.debug( String.format( "%s is defined or is a function", functionEntryPoint ) );
                Function f = (Function)fObj;

                try
                {
                    Object result = f.call(cx, scope, scope, args);
                    return result;
                }
                catch ( RhinoException re )
                {
                    log.debug( "Call" );
                    logRhinoException( re );

                    throw re;
                }
            }
        }
        finally
        {
            Context.exit();
        }
    }


    /**
     * This is a private function for logging a RhinoException.
     * It is seldom a good idea to call a function inside a catch-statement,
     * but in this case it is assumed to be in order.
     * The purpose is to have unified logging and only to have one place to 
     * maintain the logging even though the exception is caught severeal places in
     * the code.
     */
    //    private static void logRhinoException( RhinoException re )
    public static void logRhinoException( RhinoException re )
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





