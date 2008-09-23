package dbc.opensearch.components.pti;

import org.apache.log4j.Logger;

public class PTIPoolAdmMain {

    static Logger log = Logger.getLogger("PTIPoolAdmMain");
    static PTIPoolAdm ptiPoolAdm;

    public final static void main(String[] args){
        try{
            /** \todo: the number of threads should be configurable */
             ptiPoolAdm = new PTIPoolAdm( 10 );
        }catch(Exception e){
            log.fatal( String.format( "Could not initialize the PTIPool" ) );
            System.out.println("caught error: "+e.getMessage() );
            e.printStackTrace();
        }try{
            ptiPoolAdm.mainLoop();
        }catch( Exception ee ){
            log.fatal( String.format( "Could not start the threads" ) );
            System.out.println("caught error: "+ee.getMessage() );
            ee.printStackTrace();

        }
        
    }
}