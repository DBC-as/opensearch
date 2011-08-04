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


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.mozilla.javascript.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JavaScriptHelperFunctions // extends ScriptableObject
{
    private static Map< Scriptable, Set< String > > loadedScripts = new HashMap< Scriptable, Set< String > >();

    private static Logger log = LoggerFactory.getLogger( JavaScriptHelperFunctions.class );


    // This function is taken from rhinos examplecode,
    // from the function Shell.java
    public static void print(Context cx, Scriptable thisObj,
			     Object[] args, Function funObj)
    {
        for (int i=0; i < args.length; i++)
        {
            if (i > 0)
            {
                System.out.print(" ");
            }

            // Convert the arbitrary JavaScript value into a string form.
            String s = Context.toString(args[i]);

            System.out.print(s);
            log.debug( "Text: " + s );
        }
        // System.out.print();
    }


    public static void use(Context cx, Scriptable thisObj, 
			   Object[] args, Function funObj) 
	throws FileNotFoundException
    {
        if ( args.length < 1 )
        {
            // "silently" return
    	    log.warn( "No arguments given" );
            return;
        }

        String jsFileName = Context.toString( args[0] );
        log.debug( String.format( "The following script was given: %s. Trying to load it", jsFileName ) );
	
        if ( ! loadedScripts.containsKey( thisObj ) )
        {
            // Create new entry in Map:
            log.trace( String.format( "Adding the Object: %s", thisObj.hashCode() ) );

            // Adding the Scope and a new Set:
            Set< String > scriptSet = new HashSet< String >();
            scriptSet.add( jsFileName );
            log.trace( String.format( "Added the script: %s to Object: %s", jsFileName, thisObj.hashCode() ) );
            loadedScripts.put( thisObj, scriptSet );
        }
        else
        {
            // Entry already in Map:
            Set< String > scriptSet = loadedScripts.get( thisObj );
            if ( scriptSet.add( jsFileName ) )
            {
		// Script is new:
		log.trace( String.format( "Added the script: %s to Object: %s", jsFileName, thisObj.hashCode() ) );
            }
            else
            {
		// Script is already loaded - do nothing!
		log.trace( String.format( "The script: %s seems to already be associated with Object: %s", jsFileName, thisObj.hashCode() ) );
            return;
            }
        }

	ScriptableObject so = (ScriptableObject)thisObj;
	String jsFilePath = (String)so.getAssociatedValue( SimpleRhinoWrapper.JS_FILE_PATH_VALUE_STRING );
	log.debug( String.format( "jsFilePath: %s", jsFilePath ) ); 

        // Load script into Scope
        FileReader inFile = new FileReader( jsFilePath  + jsFileName ); // can throw FileNotFoundExcpetion
        try
        {
            Object o = cx.evaluateReader((Scriptable)thisObj, inFile, jsFileName, 1, null);
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
            SimpleRhinoWrapper.logRhinoException( re );
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
