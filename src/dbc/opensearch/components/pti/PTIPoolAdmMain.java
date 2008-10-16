package dbc.opensearch.components.pti;

import org.compass.core.Compass;
import org.apache.log4j.Logger;

import dbc.opensearch.tools.Processqueue;
import dbc.opensearch.tools.Estimate;
import dbc.opensearch.tools.FedoraHandler;
import dbc.opensearch.tools.FedoraClientFactory;
import dbc.opensearch.tools.CompassFactory;
import fedora.client.FedoraClient;

public class PTIPoolAdmMain {

    static Logger log = Logger.getLogger("PTIPoolAdmMain");
    static PTIPoolAdm ptiPoolAdm;
        

    public final static void main(String[] args){
    
        FedoraClientFactory fedoraClientFactory = new FedoraClientFactory();

        CompassFactory compassFactory = new CompassFactory();
        

        try{
            Compass compass = compassFactory.getCompass();
            FedoraClient fedoraClient = fedoraClientFactory.getFedoraClient();
            FedoraHandler fedoraHandler = new FedoraHandler( fedoraClient );
            Estimate estimate = new Estimate();
            Processqueue processqueue = new Processqueue();
            
            /** \todo: the number of threads should be configurable */
            ptiPoolAdm = new PTIPoolAdm( 10, processqueue, estimate, fedoraHandler, compass );

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