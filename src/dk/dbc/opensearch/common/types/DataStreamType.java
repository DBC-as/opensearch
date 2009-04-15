package dk.dbc.opensearch.common.types;

import org.apache.log4j.Logger;

public enum DataStreamType 
{	
	OriginalData ( "originalData", "original data" ),
    DublinCoreData( "dublinCoreData", "dublin core data" ),
    AdminData( "adminData", "Administration" ),      
	IndexableData( "indexableData", "data prepared for indexing" );
	
	static Logger log = Logger.getLogger( DataStreamType.class );
	
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


	public static boolean validDataStreamNameType( String nametype )
    {
        DataStreamType DSN = DataStreamType.getDataStreamNameFrom( nametype );
        log.debug( "checking dataStreamName" );
        
        if( DSN == null )
        	return false;
        
        return true;
    }
	
	
    /**
     * @param mime
     * @return
     */
    public static DataStreamType getDataStreamNameFrom( String name )
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