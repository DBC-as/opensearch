/**
 * \file CargoContainer.java
 * \brief The CargoContainer class
 * \package datadock
 */
package dk.dbc.opensearch.common.types;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;


/**
 * \ingroup types
 * \brief CargoContainer is a data structure used throughout OpenSearch for carrying information
 *  submitted for indexing. CargoContainer retains data in a private data structure consisting of 
 *  CargoObject objects. All verification and work with theses objects are done through the 
 *  CargoObject class.  
 */
public class CargoContainer 
{
	Logger log = Logger.getLogger( CargoContainer.class );

	
    /** The internal representation of the data contained in the CC*/
    private ArrayList< CargoObject > data;
    
    
    public void add( String format, String submitter, String language, String mimetype, InputStream data ) throws IOException
    {
    	CargoObject co = new CargoObject( mimetype, language, submitter, format, data );    	
    	this.data.add( co );    	
    }


    public ArrayList< CargoObject > getData()
    {
    	return data;
    }
    
    
    public int getItemsCount()
    {
    	return data.size();
    }
}