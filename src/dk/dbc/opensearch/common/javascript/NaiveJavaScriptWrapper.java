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

import dk.dbc.opensearch.common.config.FileSystemConfig;
import java.io.FileNotFoundException;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

/**
 * \todo: This class still lacks exception-throwing!
 * \todo: This class still lacks documentation!
 * \todo: This class still lacks testing!
 */

public class NaiveJavaScriptWrapper 
{
    private static Logger log = Logger.getLogger( NaiveJavaScriptWrapper.class );

    private final ScriptEngineManager manager = new ScriptEngineManager();
    private Invocable inv = null;
    private ScriptEngine engine = null;
    private String jsFileName = null;
    
    public NaiveJavaScriptWrapper( String scriptName )
    {
	try
	{
	    jsFileName = FileSystemConfig.getScriptPath() + scriptName;
	}
	catch( ConfigurationException ce )
	{
	    String errorMsg = String.format( "A ConfigurationExcpetion was cought", ce.getMessage() );
	    log.fatal( errorMsg, ce );
	    // \todo: throw new
	}
	
	engine = manager.getEngineByName( "JavaScript" );
    }

    public void put( String key, Object value )
    {
	engine.put( key, value );
    }
    
    public void run( String entryPointFunc, Object... args )
    {
	if ( inv == null )
	{
	    try
	    {
		engine.eval( new java.io.FileReader( jsFileName ) );
	    }
	    catch( FileNotFoundException fnfe )
	    {
		String errorMsg = String.format( "File was not found: %s  Error: ", jsFileName, fnfe.getMessage() );
		log.fatal( errorMsg, fnfe );
		// \todo: throw new
	    }
	    catch( ScriptException se )
	    {
		String errorMsg = String.format( "A ScriptException was cought: %s", se.getMessage() );
		log.fatal( errorMsg, se );
		// \todo: throw new
	    }
	    
	    inv = (Invocable)engine;
	}

	try 
	{
	    inv.invokeFunction( entryPointFunc, args );
	}
	catch( ScriptException se )
	{
	    String errorMsg = new String( "Could not run script" );
	    log.fatal( errorMsg , se );
	    //		throw new PluginException( errorMsg, se );
	}
	catch( NoSuchMethodException nsme )
	{
	    String errorMsg = new String( "The method, \"test\" could not be found in the script." );
	    log.fatal( errorMsg , nsme );
	    //		throw new PluginException( errorMsg, nsme );
	}
	
    }
}
