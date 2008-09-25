package  dbc.opensearch.tools;

import java.io.File;
import java.io.FilenameFilter;
/**
 *Filter extract .xml files
 */
public class XmlFileFilter implements FilenameFilter{
    
    /**
     * This method returns true only if arguments dir+name evals to a
     * filename not starting with a '.'
     * @param dir the path of the directory to be tested
     * @param name the dir- or filename on the path
     * @throws NullPointerException if the dir- or filename is null
     * @returns true if path denotes a file that ends with ".xml"
     */
    public boolean accept(File dir, String name) throws NullPointerException{
        if( name.endsWith( ".xml" ) ){
            return true;
        }
            return false;
        
    }
}
