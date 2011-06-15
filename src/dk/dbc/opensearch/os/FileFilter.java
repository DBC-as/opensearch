/**
 * \file FileFilter.java
 * \brief The FileFilter class
 * \package tools
 */
package  dk.dbc.opensearch.common.os;


import java.io.File;
import java.io.FilenameFilter;


/**
 * \ingroup tools
 * \brief Filter to remove directories and .files
 */
public class FileFilter implements FilenameFilter
{    
    /**
     * This method returns true only if arguments dir+name evaluates to a
     * file name not starting with a '.'
     *
     * @param dir the path of the directory to be tested
     * @param name the dir- or filename on the path
     *
     * @return true iff path denotes a file not beginning with a '.'
     *
     * @throws NullPointerException if the dir- or filename is null
     */
    public boolean accept( File dir, String name ) throws NullPointerException
    {
    	boolean isDir = new File(dir, name).isDirectory();
    	
        if ( isDir )
        {
            return false;
        }
        else if( name.startsWith( "." ) )
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
