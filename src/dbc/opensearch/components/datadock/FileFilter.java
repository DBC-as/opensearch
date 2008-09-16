package  dbc.opensearch.components.datadock;

import java.io.*;
/**
 *Filter to remove directories and .files
 */
public  class FileFilter implements FilenameFilter{
    
    public boolean accept(File dir, String name){
        if (new File(dir, name).isDirectory()){
            return false;
        }
        return !name.startsWith(".");
    }
}
