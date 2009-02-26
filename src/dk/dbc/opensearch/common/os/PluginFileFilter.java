/**
 * \file PluginFileFilter.java
 * \brief FilenameFilter class implementation that filters plugin files
 * \package tools
 */
package  dk.dbc.opensearch.common.os;


import java.io.File;
import java.io.FilenameFilter;


/**
 * \ingroup os
 * \brief Filter extract .plugin files
 */
public class PluginFileFilter implements FilenameFilter
{    
    /**
     *
     * @param dir the path of the directory to be tested
     * @param name the dir- or filename on the path
     *
     * @returns true if path denotes a file that ends with ".plugin"
     *
     * @throws NullPointerException if the dir- or filename is null
     */
    public boolean accept( File dir, String name ) throws NullPointerException
    {
        if( !( new File( name ).isDirectory()) && name.endsWith( ".plugin" ) )
            return true;
        else 
            return false;
    }
}
