/**
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


package dk.dbc.opensearch.tools.readindex;


import java.io.File;


public class ReadIndexMain
{

    /**
     * The Main method
     */
    static public void main( String[] args )
    {
        
        // index
        String index = System.getProperty( "index" );
        if ( index == null )
        {
            System.err.println( "index option must be specified" );
            System.err.println( usage() );
            System.exit( 1 );
        }

        File indexFile = new File( index );
        ReadIndex readIndex = new ReadIndex();
        String indexStr = "";
        
        try{
            indexStr = readIndex.readIndexFromFolder( indexFile );
        }catch( Exception exc )
        {
            System.err.println( "Caught error during reading: " + exc.getMessage() );
            exc.printStackTrace();
            System.exit( 1 );
        }

        System.out.println( indexStr );

    }

    private static String usage()
    {
        String usage = "usage:\n\n";
        usage += " java -jar -Dindex=[index] OpenSearch_READINDEX\n\n";
        usage += " [index]          The index directory to read\n";
        return usage;
    }
}