/**
 * \file FileHandler.java
 * \brief The FileHandler class
 * \package os;
 */

package dk.dbc.opensearch.common.os;

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


import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.lang.IllegalArgumentException; 
import java.lang.NullPointerException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * /brief Class to handle file operations.
 * The class is used to wrap file operations so that classes using files can be unittested.
 */
public class FileHandler 
{
    static Logger log = Logger.getLogger( FileHandler.class );

    /**
     * The empty FileHandler constructor
     *
     * \todo: what's the point of the constructor here?
     */
    private FileHandler() { }

    
    /**
     * @param path The path to the file
     * @throws NullPointerException if the path is not valid
     */
    public static File getFile( String path )throws NullPointerException
    {
    	log.debug( "Calling getFile(), path: " + path );
    	return new File( path );
    }
    
    
    /**
     * Retrieves a list of filenames from the given path, if they conforms to the filefilter
     * @param path The path of the directory to retrieve filenames from
     * @param fileFilters The filefilter to use for identifying the files we want
     * @param descend if true the method will descend recursively down into the specified directory and retrieve filenames
     * @return A string vector, with the matching filenames
     * @throws IllegalArgumentException if the path does not exist.
     */
    public static List<String> getFileList( String path, FilenameFilter[] fileFilters, boolean descend ) throws IllegalArgumentException
    {
        /** \todo: what happens if the fileFilters is null? Or the path?**/
        log.debug( String.format( "getFileList( path=%s, filefilters[%s], descend=%s ) called", path, fileFilters.length, descend ) );
       
        log.debug( "Check if path exists" );        
        if( ! new File ( path ).isDirectory() )
        {
            throw new IllegalArgumentException( String.format( "Path: '%s' does not exist, or is not a directory", path ) );    
        }
        
        List<String> fileNames = new ArrayList<String>();
        
        File dir = new File ( path );
        File[] files = dir.listFiles();
        
        for( File f: files )
        {
            log.debug( String.format( "Validating: '%s'", f.getAbsolutePath() ) );
            boolean validate = true;
            for( FilenameFilter fnf: fileFilters )
            {
                if( ! fnf.accept(dir, f.getName() ) )
                {
                    validate = false;
                    break;
                }
            }
            
            if( validate )
            {                
                log.debug( String.format( "Validated: '%s'", f.getAbsolutePath() ) );
                fileNames.add( f.getAbsolutePath() );
            }
            if( descend && f.isDirectory() )
            {
                log.debug( String.format( "Descending into: '%s'", f.getAbsolutePath() ) );
                fileNames.addAll( getFileList( f.getAbsolutePath(), fileFilters, descend ) );
            }
        }
        
        return fileNames;
    }

    
    /**
     * Reads a file from disk
     * @param file The path to the file that should be read
     * @return an inputstream with the file content
     * @throws FileNotFoundException if the file does not exist
     */
    public static FileInputStream readFile( String file ) throws FileNotFoundException
    {
    	if(! new File ( file ).isFile() )
    	{
            throw new FileNotFoundException( String.format( "Error - '%s' is not a file", file ) );
    	}
    	
        FileInputStream data = new FileInputStream( file );
        return data;
    }
}