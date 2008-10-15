package dbc.opensearch.components.datadock;

import org.apache.log4j.Logger;
import org.apache.commons.configuration.ConfigurationException;

class DataDockPoolAdmMain {

    Logger log = Logger.getLogger( "DataDockPoolAdmMain" );
    // String mimetype;
//     String lang;
//     String submitter;
//     String format;
//     String filepath;
//     DataDockPoolAdm DDPA;
    
    public final static void Main( String[] args ){
        
        // Construct the DataDockPoolAdm 
        try{       
        DataDockPoolAdm DDPA = new DataDockPoolAdm();
        
        // get the arguments for the execution
        String mimetype = System.getProperty("mimetype");
        String lang = System.getProperty("lang");
        String submitter = System.getProperty("submitter");
        String format = System.getProperty("format");
        String filepath = System.getProperty("filepath");

        // start the DataDockPoolAdm        
        DDPA.start( mimetype, lang, submitter, format, filepath );  

        }catch(Exception e){
            e.printStackTrace();
        }
        
    }

}