/**
 * \file XmlFileFilter.java
 * \brief FilenameFilter class implementation that filters xml files
 * \package tools
 */
package  dk.dbc.opensearch.tools;

import java.io.File;
import java.io.FilenameFilter;
/**
 * \ingroup tools
 * \brief Filter extract .xml files
 */
public class XmlFileFilter implements FilenameFilter{
    
    /**
     * This method returns true only if arguments dir+name evals to a
     * filename not starting with a '.'
     *
     * @param dir the path of the directory to be tested
     * @param name the dir- or filename on the path
     *
     * @returns true if path denotes a file that ends with ".xml"
     *
     * @throws NullPointerException if the dir- or filename is null
     */
    public boolean accept(File dir, String name) throws NullPointerException{
         if( !dir.isDirectory() && name.endsWith( ".xml" ) ){
            return true;
        }else {
            return false;
        }
    }
}
