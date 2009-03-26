/**
 * \file PdfFileFilter.java
 * \brief The PdfFileFilter class
 * \package os;
 */

package dk.dbc.opensearch.common.os;


import java.io.File;
import java.io.FilenameFilter;

/**
 *  
 */
public class PdfFileFilter implements FilenameFilter
{
    /**
     * This method returns true only if arguments dir+name evals to a
     * filename not starting with a '.' and the suffix is .pdf
     *
     * @param dir the path of the directory to be tested
     * @param name the dir- or filename on the path
     *
     * @returns true if path denotes a file that ends with ".pdf"
     *
     * @throws NullPointerException if the dir- or filename is null
     */
    public boolean accept(File dir, String name) throws NullPointerException
    {
        
         if( dir == null )
         {
             throw new NullPointerException( "invalid directory" );
         }
        if( ! ( new File( dir, name ) ).isDirectory() && name.endsWith( ".pdf" ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
