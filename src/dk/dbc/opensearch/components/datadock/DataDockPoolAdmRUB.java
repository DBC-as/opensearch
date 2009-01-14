package dk.dbc.opensearch.components.datadock;

import dk.dbc.opensearch.common.types.CargoContainer;
import java.io.InputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileInputStream;
import org.apache.log4j.Logger;

public class DataDockPoolAdmRUB {

    static Logger log = Logger.getLogger("DataDockPoolAdmRUB");
    
    public final static void main(String[] args){
        log.debug( String.format( "start the datadockpool" ) );

        //InputStream is = new java.io.ByteArrayInputStream(xml.getBytes("UTF-8"));
        
        File DCfile = new File("/home/shm/RUB_data/139/dc.xml");
        File PDFfile = new File("/home/shm/RUB_data/139/Implementation_of_a_Water.pdf");

        String DCstr = null;

        try{
        
            FileReader rd = new FileReader( DCfile );
            char[] buf = new char[(int)DCfile.length()];
            rd.read(buf);
            DCstr = new String( buf );
            

            System.out.println("BUFFERLENGTH " + (int)DCfile.length() );
            
            //System.out.println( DCstr );

            InputStream PDFis = new FileInputStream(PDFfile);

            CargoContainer cc = new CargoContainer(PDFis, DCstr, "SUBMITTER", "FORMAT");
        
        }catch(Exception e){
            log.debug( String.format( "catched error: %s", e.getMessage() ) );
            System.exit(2);
        }
    }
}

