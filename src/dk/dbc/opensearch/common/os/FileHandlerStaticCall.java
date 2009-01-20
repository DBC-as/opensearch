/**
 * \file FileHandlerStaticCall.java
 * \brief The  FileHandlerStaticCall class
 * \package os;
 */


package dk.dbc.opensearch.common.os;

import java.io.File;

/**
 * 
 */
public class FileHandlerStaticCall {
    
    public FileHandlerStaticCall(){}
    public String testStatic(){
        File f = FileHandler.getFile( "TEST" );
        return f.getAbsolutePath();
    }
   
}
