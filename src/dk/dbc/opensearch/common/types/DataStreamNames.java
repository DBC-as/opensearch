package dk.dbc.opensearch.common.types;

import org.apache.log4j.Logger;

public enum DataStreamNames 
{	
	OriginalData ( "originalData", "original data" ),
            DublinCoreData( "dublinCoreData", "dublin core data" ),
            AdminData( "adminData", "Administration" );       
	static Logger log = Logger.getLogger( DataStreamNames.class );
	
	String name;
	String description;
	
	
	DataStreamNames( String name, String description ) 
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
        DataStreamNames DSN = DataStreamNames.getDataStreamNameFrom( nametype );
        log.debug( "checking dataStreamName" );
        
        if( DSN == null )
        	return false;
        
        return true;
    }
	
	
    /**
     * @param mime
     * @return
     */
    public static DataStreamNames getDataStreamNameFrom( String name )
    {
        DataStreamNames DSN = null;
        for (DataStreamNames dsn : DataStreamNames.values() )
        {
            if( name.equals( dsn.getName() ) )
            {
                DSN = dsn;
            }
        }
        
        return DSN;
    }   
}