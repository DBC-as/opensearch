package dk.dbc.opensearch.common.types;

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

import org.apache.log4j.Logger;


public enum DataStreamType 
{	
	OriginalData ( "originalData", "original data" ),
	DublinCoreData( "dublinCoreData", "dublin core data" ),
	AdminData( "adminData", "Administration" ), 
    IndexableData( "indexableData", "data prepared for indexing" ),
    RelsExt( "relsExt", "relationship expressions" );
	
	
	private static Logger log = Logger.getLogger( DataStreamType.class );
	
	String name;
	String description;
	
	
	DataStreamType( String name, String description ) 
	{		
		this.name = name;
		this.description = description;
	}
	
	
	public String getName()
	{
		return this.name;
	}
	
	
	public String getDescription()
	{
		return this.description;
	}


	public static boolean validDataStreamType( String nametype )
    {
        DataStreamType DSN = DataStreamType.getDataStreamTypeFrom( nametype );
        log.debug( "checking dataStreamName" );
        
        if( DSN == null )
        {
        	return false;
        }
        
        return true;
    }
	
	
    /**
     * @param name the name of the DataStreamType
     * @return the DataStreamType matching {@code name} or null, if no match was found
     */
    public static DataStreamType getDataStreamTypeFrom( String name )
    {
        DataStreamType DSN = null;
        for (DataStreamType dsn : DataStreamType.values() )
        {
            if( name.equals( dsn.getName() ) )
            {
                DSN = dsn;
            }
        }
        
        return DSN;
    }   
}