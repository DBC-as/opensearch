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

import org.apache.log4j.Logger;
import org.mozilla.javascript.*;

import dk.dbc.opensearch.common.config.FileSystemConfig; // Testing
import org.apache.commons.configuration.ConfigurationException; // Testing

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;

public class JavaScriptHelperFunctions // extends ScriptableObject
{

    private static Logger log = Logger.getLogger( JavaScriptHelperFunctions.class );

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
			   Object[] args, Function funObj) throws FileNotFoundException, ConfigurationException
    {
	if ( args.length < 1 ) {
	    log.debug( "No arguments given" );
	} else {
	    String jsFileName = Context.toString( args[0] );
	    log.debug( String.format( "The following script was given: %s. Trying to load it", jsFileName ) );

	    FileReader inFile = new FileReader( FileSystemConfig.getScriptPath() + jsFileName ); // can throw FileNotFindExcpetion
	    try
	    {
		//Object o = cx.evaluateReader((Scriptable)scope, inFile, jsFileName, 1, null);
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
	    

	}
	
    }



    // private static void Use(Context cx, Scriptable thisObj,
    // 			    Object[] args, Function funObj)
    // {
    // 	if ( args.length < 1 ) {
    // 	    log.debug( "No arguments given" );
    // 	} else {
    // 	    String s = Context.toString( args[0] );
    // 	    log.debug( String.format( "The following script was given: %s", s ) );
    // 	}
	
    // }

}
