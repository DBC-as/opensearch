/**
 * \file PTIPoolAdmMain.java
 * \brief The PTIPoolAdmMain Class
 * \package pti
 */
package dbc.opensearch.components.pti;

import org.compass.core.Compass;
import org.apache.log4j.Logger;

import dbc.opensearch.tools.Processqueue;
import dbc.opensearch.tools.Estimate;
import dbc.opensearch.tools.FedoraHandler;
import dbc.opensearch.tools.FedoraClientFactory;
import dbc.opensearch.tools.CompassFactory;
import fedora.client.FedoraClient;
/**
 * \defgroup components Components
 * \brief The components of opensearch
 * \defgroup tools Tools
 * \brief The tools classes for facilitating the componentents and testing.
 * 
 * \defgroup datadock Datadock
 * \ingroup components 
 * \brief The datadock recieves incoming data and stores it in the repository.
 * \defgroup pti PTI
 * \ingroup components 
 * \brief The PTI component validates and indexes the newly arrived data.
 * \mainpage
 * \section Overview
 * \section Components
 * \subsection Datadock
 * \subsection PTI
 * \section Tools
 * \subsection Fedorahandler
 * \subsection Processqueue
 * \subsection Estimate
 */

/**
 * \ingroup pti
 * \brief the main class for the PTI component
 */
public class PTIPoolAdmMain {

    static Logger log = Logger.getLogger("PTIPoolAdmMain");
    static PTIPoolAdm ptiPoolAdm;
        

    public final static void main(String[] args){
    
        FedoraClientFactory fedoraClientFactory = new FedoraClientFactory();

        CompassFactory compassFactory = new CompassFactory();
        int numberOfThreads = 10;

        try{
            Compass compass = compassFactory.getCompass();
            FedoraClient fedoraClient = fedoraClientFactory.getFedoraClient();
            FedoraHandler fedoraHandler = new FedoraHandler( fedoraClient );
            Estimate estimate = new Estimate();
            Processqueue processqueue = new Processqueue();
            /** \todo: the number of threads should be configurable */           
            PTIPool ptiPool = new PTIPool( numberOfThreads, fedoraHandler);  
           
            ptiPoolAdm = new PTIPoolAdm( ptiPool, processqueue, estimate, compass );

        }catch(Exception e){
            log.fatal( String.format( "Could not initialize the PTIPool" ) );
            System.out.println("caught error: "+e.getMessage() );
            e.printStackTrace();
        }try{
            ptiPoolAdm.mainLoop();
        }catch( Exception ee ){
            log.fatal( String.format( "Could not start the threads or a runtime error occured" ) );
            System.out.println("caught error: "+ee.getMessage() );
            ee.printStackTrace();

        }
        
    }
}