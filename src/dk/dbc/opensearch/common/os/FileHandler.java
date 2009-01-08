/**
 * \file FileHandler.java
 * \brief The FileHandler class
 * \package tools;
 */

package dk.dbc.opensearch.tools;

import org.apache.log4j.Logger;
import java.util.Vector;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import dk.dbc.opensearch.tools.FileFilter;
import dk.dbc.opensearch.tools.XmlFileFilter;
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
    public FileHandler(){} 

    public static File getFile( String path )throws NullPointerException{
        return new File( path );
    }


    public static Vector<String> getFileList( String path, FilenameFilter[] fileFilter, boolean descend ){
        Vector<String> fileList = new Vector<String>();
        return  fileList;
    }
    public static InputStream readFile( String file )throws FileNotFoundException{
        InputStream is = null;
        return is;
    }

//     /**
//      * Retrieves a list of filenames from the given path, if they conforms to the filefilter
//      * @param path The path of the directory to retrieve filenames from
//      * @param fileFilter: The filefilter to use for identifying the files we want
//      * @param recursive: if true the method will descend recursively down into the specified directory and retrieve filenames
//      * @returns A string vector, with the matching filenames
//      * @throws IllegalArgumentException if the path does not exist.
//      */
//     public Vector<String> getFileList( String path, FileFilter fileFilter, boolean descend ){
//         log.debug( String.format( "getFileList(path=%s, filefilter, descend=%s) called", path, descend ) );
//         Vector<String> fileList = new Vector<String>();
        
//         log.debug( "Check if path exists" );
        
//         if(! new File ( path ).isDirectory() ){
//             throw new IllegalArgumentException( String.format( "Path: '%s' does not exist, or is not a directory", path ) );       
//         }
        
//         File[] files = ( new File ( path ) ).listFiles();
//         for( File f : files ){
//             if(descend && f.isDirectory() ){
//                 fileList.addAll( getFileList( f.getAbsolutePath(), fileFilter, descend ) );
//             }
//             else{
//                 String[] filename = f.list( fileFilter );
//                 if( filename.length == 1 ){
//                     fileList.add( filename[0] );
//                 }
//             }
//         }
//         return fileList;
//     }

//     /**
//      * Reads a file from disk
//      * @param file The path to the file that should be read
//      * @returns an inputstream with the file content
//      * @throws FileNotFoundException if the file does not exist
//      */
//     public InputStream readFile( String file )throws FileNotFoundException{
//         InputStream is = null;

//         File f = new File( file );
//         if(! new File ( file ).isFile() ){
//             throw new FileNotFoundException( String.format( "Path: '%s' does not exist, or is not a file", file ) );       
//         }
        
//         InputStream data = new FileInputStream( file );
//         return data;
//     }
}
