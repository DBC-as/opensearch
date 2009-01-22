/**
 * \file FileHandler.java
 * \brief The FileHandler class
 * \package os;
 */

package dk.dbc.opensearch.common.os;

import org.apache.log4j.Logger;
import java.util.Vector;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.lang.IllegalArgumentException; 
import java.lang.NullPointerException;

/**
 * /brief Class to handle file operations.
 * The class is used to wrap file operations so that classes using files can be unittestet.
 */
public class FileHandler {

    static Logger log = Logger.getLogger( "FileHandler" );

    /**
     * The empty FileHandler constructor
     */
    //private FileHandler(){} 
    public FileHandler(){}

    /**
     * @param path: The path to the file
     * @throws NullPointerException if the path is not valid
     */
    public static File getFile( String path )throws NullPointerException{
        return new File( path );
    }
    
    /**
     * Retrieves a list of filenames from the given path, if they conforms to the filefilter
     * @param path The path of the directory to retrieve filenames from
     * @param fileFilter: The filefilter to use for identifying the files we want
     * @param recursive: if true the method will descend recursively down into the specified directory and retrieve filenames
     * @returns A string vector, with the matching filenames
     * @throws IllegalArgumentException if the path does not exist.
     */

    public static Vector<String> getFileList( String path, FilenameFilter[] fileFilters, boolean descend ) 
        throws IllegalArgumentException{
        
        log.debug( String.format( "getFileList( path=%s, filefilters[%s], descend=%s ) called", path, fileFilters.length, descend ) );
       
        log.debug( "Check if path exists" );        
        if(! new File ( path ).isDirectory() ){
            throw new IllegalArgumentException( String.format( "Path: '%s' does not exist, or is not a directory", path ) );    
        }
        
        Vector<String> fileNames = new Vector<String>();
        
        File dir = new File ( path );
        File[] files = dir.listFiles();
        
        for( File f: files ){
            log.debug( String.format( "Validating: '%s'", f.getAbsolutePath() ) );
            boolean validate = true;
            for( FilenameFilter fnf: fileFilters ){
                if(! fnf.accept(dir, f.getName() ) ){
                    validate = false;
                    break;
                }
            }
            
            if( validate ){                
                log.debug( String.format( "Validated: '%s'", f.getAbsolutePath() ) );
                fileNames.add( f.getAbsolutePath() );
            }
            if( descend && f.isDirectory() ){
                log.debug( String.format( "Descending into: '%s'", f.getAbsolutePath() ) );
                fileNames.addAll( getFileList( f.getAbsolutePath(), fileFilters, descend ) );
            }

        }

        return fileNames;
    }

    /**
     * Reads a file from disk
     * @param file The path to the file that should be read
     * @returns an inputstream with the file content
     * @throws FileNotFoundException if the file does not exist
     */
    public static InputStream readFile( String file )throws FileNotFoundException{
        InputStream is = null;

        File f = new File( file );
        if(! new File ( file ).isFile() ){
            throw new FileNotFoundException( String.format( "Error - '%s' is not a file", file ) );       
        }
        
        InputStream data = new FileInputStream( file );
        return data;
    }
}
