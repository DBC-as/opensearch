package dk.dbc.opensearch.common.types;

import org.apache.log4j.Logger;

/**
 * Type for telling what alias should be used to index the data
 */
public enum IndexingAlias 
{
    Article ( "article", "the docbook/ting xml alias" ),
        Danmarcxchange ( "danmarcxchange", "alias for marc posts" ),
        DC ( "dc","data fra DR" );

    static Logger log = Logger.getLogger( IndexingAlias.class );

    String name;
    String description;
    IndexingAlias( String name, String Description )
        {
            this.name = name;
            this.description = description;
        }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }
    
    public static boolean validIndexingAlias( String alias )
    {
        IndexingAlias IA = IndexingAlias.getIndexingAlias( alias);
        log.debug( "indexing alias" );
        
        if( IA == null )
        	return false;
        
        return true;
    }
	
	
    /**
     * @param String name, the name of the wanted IndexingAlias
     * @return the IndexingAlias
     */
    public static IndexingAlias getIndexingAlias( String name )
    {
        IndexingAlias IA = null;
        for (IndexingAlias ia : IndexingAlias.values() )
        {
            if( name.equals( ia.getName() ) )
            {
                IA = ia;
            }
        }
        
        return IA;
    }   
}