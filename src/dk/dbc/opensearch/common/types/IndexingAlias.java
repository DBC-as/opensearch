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

/**
 * Type for telling what alias should be used to index the data
 */
public enum IndexingAlias 
{
    Article ( "article", "the docbook/ting xml alias" ),
    Danmarcxchange ( "danmarcxchange", "alias for marc posts" ),
    DC ( "dc","data from DR in Dublin Core format" ),
    None( "none", "Non-indexable content" );

    static Logger log = Logger.getLogger( IndexingAlias.class );

    private String name;
    private String description;

    IndexingAlias( String name, String description )
    {
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the canonical name of the type. This name is used as an
     * alias in the indexing process and must be unique within this
     * Enum.
     * @return the name of the type as a String
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the human-readable description of the type
     * @return the description of the type as a String
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param alias The alias to validate against the available types
     * 
     * @return true if the alias is in the types, false otherwise  
     */
    public static boolean validIndexingAlias( String alias )
    {
        log.debug( String.format( "Getting indexing alias from string %s", alias ) );
        IndexingAlias IA = IndexingAlias.getIndexingAlias( alias );
        
        if( IA == null )
        {
            return false;
        }
        return true;
    }
	
	
    /**
     * @param name, the name of the wanted IndexingAlias
     * @return the IndexingAlias that matched the name given or null if none matched
     */
    public static IndexingAlias getIndexingAlias( String name )
    {
        IndexingAlias IA = null;
        for ( IndexingAlias ia : IndexingAlias.values() )
        {
            if( name.equals( ia.getName() ) )
            {
                IA = ia;
            }
        }
        
        return IA;
    }   
}