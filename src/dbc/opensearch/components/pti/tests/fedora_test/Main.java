package dbc.opensearch.components.pti.tests.fedora_test;

import dbc.opensearch.components.datadock.CargoContainer;
import dbc.opensearch.components.pti.FedoraHandler;
import org.apache.log4j.Logger;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import org.apache.commons.configuration.ConfigurationException;

import fedora.server.errors.ServerException;
import javax.xml.stream.XMLStreamException;

/**
 *
 */
public class Main {
    static Logger log = Logger.getLogger("Main");

    public static void main(String[] args) throws java.io.IOException, java.io.UnsupportedEncodingException{
        
        FedoraHandler fh = null;
        try{
                fh = new FedoraHandler();
            }
            catch (ConfigurationException cex){
                
                System.out.println( "We encountered an exception:" );
                System.out.println( cex.getMessage() );
                cex.printStackTrace();
            }
        
        

log.debug( String.format( "Constructed fedoraHandler, with %s, %s", args[0], args[1] ) );
        if ( args.length == 0 ){
            print_usage();
            System.exit( 1 );
        }
        if ( args[0].equals( "-q" ) ){
            log.debug( String.format( "getting datastream with pid %s", args[1] ) );
            getDatastreamAndPrint( fh, args[1] );
        }else if( args[0].equals( "-i" ) ){
            log.debug( String.format( "option %s, submitting datastream %s", args[0], args[1] ) );
            FileInputStream in = null;

            try {
                in = new FileInputStream( args[1] );
                log.debug( String.format( "FileinputStream.available()=%s", in.available() ) );
                CargoContainer cargo = new CargoContainer( in, "text/xml", "da", "stm" );

                fh.submitDatastream( cargo, "faktalink", "XML", "Test ingest af faktalink" );
            // }catch( ServerException e ){
            //     System.out.println( e.getMessage() );
            //     System.out.println( e.getCode() );
            //     e.printStackTrace();

            // }catch( XMLStreamException xse ){
            //     System.out.println( xse.getMessage() );
            //     xse.printStackTrace();

            }catch( Exception e){
                System.out.println( "We encountered an exception:" );
                System.out.println( e.getMessage() );
                e.printStackTrace();
                if (e instanceof org.apache.axis.AxisFault) {
                    StringBuffer authzDetail = new StringBuffer("");
                    org.w3c.dom.Element[] getFaultDetails =
                        ((org.apache.axis.AxisFault) e).getFaultDetails();
                    if (getFaultDetails != null) {
                        for (org.w3c.dom.Element detail : getFaultDetails) {
                            if ("Authz".equals(detail.getLocalName())
                                && detail.hasChildNodes()) {
                                org.w3c.dom.NodeList nodeList = detail.getChildNodes();
                                for (int j = 0; j < nodeList.getLength(); j++) {
                                    authzDetail.append(nodeList.item(j).getNodeValue());
                                }
                            }
                        }
                    }
                    if (authzDetail.length() > 0) {
                        System.out.println( "\nAuthorization Details:\n"+authzDetail.toString());
                    }
                }

            }finally{
                if( in != null ){
                    in.close();
                }
            }
        }
    }

    private static void print_usage(){
        System.out.println( "usage: Main [-q namespace:id][-i file_to_ingest]" );
    }

    private static void getDatastreamAndPrint( FedoraHandler fh, String id ) throws java.io.IOException, java.io.UnsupportedEncodingException{
        log.debug( String.format( "Entering getDatastreamAndPrint" ) );
        CargoContainer cargo = fh.getDatastream( id );
        //        byte[] db = cargo.getDataBytes();
        //System.out.println( new String( db, "UTF-8" ) );
    }
}