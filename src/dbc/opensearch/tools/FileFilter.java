package  dbc.opensearch.tools;

import java.io.File;
import java.io.FilenameFilter;
/**
 *Filter to remove directories and .files
 */
public class FileFilter implements FilenameFilter{
    
    /**
     * This method returns true only if arguments dir+name evals to a
     * filename not starting with a '.'
     * @param dir the path of the directory to be tested
     * @param name the dir- or filename on the path
     * @throws NullPointerException if the dir- or filename is null
     * @returns true iff path denotes a file not beginning with a '.'
     */
    public boolean accept(File dir, String name) throws NullPointerException{
        if (new File(dir, name).isDirectory()){
            return false;
        }else if( name.startsWith( "." ) ){
            return false;
        }else{
            return true;
        }
    }
}
