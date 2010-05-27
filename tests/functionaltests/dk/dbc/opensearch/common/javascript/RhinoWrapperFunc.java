package dk.dbc.opensearch.common.javascript;

import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class RhinoWrapperFunc
{

    private static Logger log = Logger.getLogger( RhinoWrapperFunc.class );

    public static void main( String[] args )
    {
	String filename = "RhinoWrapperFunc.js";
	SimpleRhinoWrapper js = null;
	try {
	    js = new SimpleRhinoWrapper( filename );
	} catch( FileNotFoundException fnfe ) {
	    log.error( String.format( "Could not find the file: %s", filename ) );
	}

	js.run( "my_func" );
	

    }

}