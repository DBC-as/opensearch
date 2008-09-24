package dbc.opensearch.components.datadock.tests.datadocktest;
import dbc.opensearch.components.datadock.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.File;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;

public class DataDockShmTest {
    public static void main(String[] args){

        String filename = System.getProperty( "file", ""); 
        if ( filename == "" ){
            System.out.println( "No filename given, stopping." );
            System.exit(0);
        }
        else{
            System.out.println( "Using file "+filename );
        }

        byte[] bytesOfFile = null;

        try{
            File file = new File( filename );
            bytesOfFile = new byte[(int)file.length()];
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            dis.readFully(bytesOfFile);
            dis.close();
        }catch(Exception e){
            System.out.println("caugth error: "+e.getMessage() );
            e.printStackTrace();
        }

        ByteArrayInputStream BAIS = new ByteArrayInputStream( bytesOfFile );
        String mime = "text/xml";
        String lang = "dk"; /// fix enum und so weiter
        String submitter = "stm"; /// fix enum und so weiter
        String format = "faktalink";

        CargoContainer cc = null;
        DataDock dd = null;
        try{
            System.out.print("Creating CargoContainer");
            cc = new CargoContainer( BAIS, mime, lang, submitter, format );
            System.out.print("Creating DataDock");
            dd = new DataDock(cc);
        }catch(Exception e){
            System.out.println("caugth error: "+e.getMessage() );
            e.printStackTrace();
        }
        
        Float estimate=null;
        try{
            estimate = dd.call(); 
        }catch(Exception e){
            System.out.println("caugth error: "+e.getMessage() );
            e.printStackTrace();
        }
        System.out.println("Estimate = "+estimate);
    }
}
