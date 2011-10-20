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
 * \file NoRefFileFilter.java
 * \brief The NoRefFileFilter class
 * \package os;
 */


package dk.dbc.opensearch.os;


import java.io.File;
import java.io.FilenameFilter;

/**
 * \ingroup tools
 * \brief Filter extract .ref files .these files carries the referencedata
 * for files to be given to the Datadock
 */
public class NoRefFileFilter implements FilenameFilter
{    
    /**
     * @param dir the path of the directory to be tested
     * @param name the dir- or filename on the path
     *
     * @return true if path denotes a file that ends with ".ref"
     *
     * @throws NullPointerException if the dir- or filename is null
     */
    public boolean accept( File dir, String name ) throws NullPointerException
    {
        if( dir == null )
        {
            throw new NullPointerException( "invalid directory" );
        }

        if( name.endsWith( ".ref" ) )
        {
            return false;
        }
        else
        {
            if( (new File( dir, name ) ).isDirectory() )
            {
                return false;
            }
        }

        return true;
    }
}