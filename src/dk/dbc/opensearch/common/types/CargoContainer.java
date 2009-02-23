/**
 * \file CargoContainer.java
 * \brief The CargoContainer class
 * \package common.types
 */
package dk.dbc.opensearch.common.types;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;


/**
 * \ingroup common.types
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
    
    
    /**
     * Constructor initializes internal representation of data, i.e., ArrayList of CargoObjects
     */
    public CargoContainer()
    {
    	this.data = new ArrayList< CargoObject >();
    }
    
    
    /**
     * Add CargoObject to internal data representation.
     * 
     * @param format
     * @param submitter
     * @param language
     * @param mimetype
     * @param data
     * @return TRUE if add operation finishes successfully.
     * @throws IOException
     */
    public void add( DataStreamNames dataStreamName, String format, String submitter, String language, String mimetype, byte[] data ) throws IOException
    {
    	CargoObject co = new CargoObject( dataStreamName, mimetype, language, submitter, format, data );
    	this.data.add( co );    	
    }


    /**
     * Getter for internal data, the validity of which is guaranteed by the Constructor.
     * 
     * @return ArrayList of CargoObjects.
     */
    public ArrayList< CargoObject > getData()
    {
    	return data;
    }


    public CargoObject getFirstCargoObject( DataStreamNames dsn)
    {
        CargoObject rco = null;
        for( CargoObject co : data ){
            if( co.getDataStreamName() == dsn ){
                rco = co;
        }
    }
        return rco;
    }

        
    
    /**
     * Getter for size, i.e., No. of CargoObjects in internal data representation.
     * 
     * @return
     */
    public int getItemsCount()
    {
    	return data.size();
    }
}