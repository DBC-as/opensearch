/**
 * \file DataDockPoolAdmMain.java
 * \brief The DataDockPoolAdmMain class
 * \package datadock
 */
package dbc.opensearch.components.datadock;

import org.apache.log4j.Logger;
import org.apache.commons.configuration.ConfigurationException;
import dbc.opensearch.tools.Estimate;
import dbc.opensearch.tools.Processqueue;
import dbc.opensearch.tools.FedoraClientFactory;
import dbc.opensearch.tools.FedoraHandler;
import fedora.client.FedoraClient;
/**
 * \ingroup datadock
 * \brief The main class of the datadock component.
 */
class DataDockPoolAdmMain {

    Logger log = Logger.getLogger( "DataDockPoolAdmMain" );
    // String mimetype;
//     String lang;
//     String submitter;
//     String format;
//     String filepath;
//     DataDockPoolAdm DDPA;
    
    public final static void main( String[] args ){
        
       
        try{ 
            // Construct the dependencies for the DataDockPoolAdm
            Processqueue processqueue = new Processqueue();
            Estimate estimate = new Estimate();
            FedoraClientFactory fedoraClientFactory = new FedoraClientFactory();
            FedoraClient fedoraClient = fedoraClientFactory.getFedoraClient();
            FedoraHandler fedoraHandler = new FedoraHandler( fedoraClient );      
            // Construct the DataDockPoolAdm        
            DataDockPoolAdm DDPA = new DataDockPoolAdm ( estimate, processqueue, fedoraHandler );
            
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
        System.out.print( "\n Progam is done \n" );
        System.exit(1);
        
    }

}