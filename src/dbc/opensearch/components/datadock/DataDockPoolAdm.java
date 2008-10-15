package dbc.opensearch.components.datadock;

import dbc.opensearch.tools.FileFilter;
import dbc.opensearch.tools.XmlFileFilter;

import dbc.opensearch.tools.Estimate;
import dbc.opensearch.tools.Processqueue;
//import dbc.opensearch.tools.FedoraClientFactory;
import dbc.opensearch.tools.FedoraHandler;
//import fedora.client.FedoraClient;


import java.util.concurrent.*;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Logger;
import java.io.*;
import org.apache.commons.configuration.ConfigurationException; 
import java.lang.ClassNotFoundException; 
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import javax.xml.rpc.ServiceException;
import java.io.IOException;
import java.lang.InterruptedException; 
import java.util.concurrent.ExecutionException;


/**
 * This class administrates the start up of DataDockPool, giving it
 * CargoContainers to process and it maintains an array of documenttitles
 * and the related FutureTasks that will contain estinmates
 *
 */
public class DataDockPoolAdm {
    static Logger log = Logger.getLogger( "DataDockPoolAdm" );
    String mimetype;
    String lang;
    String submitter;
    String format;
    String filepath;
    Estimate estimate;

    Processqueue processqueue;
    //FedoraClientFactory fedoraClientFactory;
    //FedoraClient fedoraClient;
    FedoraHandler fedoraHandler;

    DataDockPool DDP;
    String[] fileNameList;
    File[] fileList;
    FutureTask[] FTList = null;
    
    public DataDockPoolAdm( Estimate estimate, Processqueue processqueue, FedoraHandler fedoraHandler )throws ConfigurationException, ClassNotFoundException, MalformedURLException, UnknownHostException, ServiceException, IOException {

        log.debug( "Entering the constructor" );
        this.processqueue = processqueue;
        this.estimate = estimate;
        this.fedoraHandler = fedoraHandler;
        log.debug( "Exiting the constructor" );
    }
    
    public void start ( String mimetype, String lang, String submitter, String format, String filepath )throws InterruptedException, ExecutionException{

        log.debug( String.format( "start the DataDockPool" ) );

        //DataDockPool DDP;
        //try{
            log.info( String.format( "Creating DataDockPool with %s threads", 10 ) );
            DDP = new DataDockPool(10, estimate, processqueue, fedoraHandler );
            
            log.debug( String.format( "Getting properties for the CargoContainer" ) );
            this.mimetype = mimetype;
            this.lang = lang;
            this.submitter = submitter;
            this.format = format;
            this.filepath = filepath;
            
            /** \todo: what the *&^%$ is this? very simple: a unique string to 
             * match on, this code is NOT intended to be reused! It is a transcription
             * of a Korean sentence meaning "I do not love you"
             */
            String doneString = "narn noRl saRang hATi arNar";
            String estimateMessageString;
            int answersReceived = 0;

            log.debug( String.format( "read the files defined in the properties into an arraylist (fileList)" ) );
            // 10: check whether the filepath String is a dir, file or a filetype specification such as "dir/*.xml"
            // 12: if the filepath is a dir, then get all from that dir
            if ( new File( filepath ).isDirectory() ){
                File dir = new File(filepath);
                // The FileFilter let only files through  that aren't directories 
                // and if their names doesnt start with "."
                fileNameList = dir.list( new FileFilter() );
                fileList = dir.listFiles( new FileFilter() );
                
            }else{
                // 15: if it is a filename get that file
                if( new File ( filepath ).isFile() ){
                    File file = new File(filepath);
                    fileNameList = new String[1]; //there must be a better way to do this
                    fileNameList[0] = file.getName();
                    fileList = new File[1]; // and this
                    fileList[0] = file;
                    
                }else{//we assume its a path ending with *.xml signaling that we must take 
                    // all .xml files in the directory
                    log.debug(String.format("Extracting from the dirpath form index '%s' to index '%s'", 0, filepath.lastIndexOf('/') ));


                    String dirpathString = filepath.substring( 0, filepath.lastIndexOf('/') );
                    log.debug(String.format("dirpathString = '%s'", dirpathString) );
                    File dirpath = new File(dirpathString);
                    fileNameList = dirpath.list( new XmlFileFilter() );
                    fileList = dirpath.listFiles( new XmlFileFilter() );
                    
                }                        
            }
            
            log.debug( String.format( "check if we got any files from the filepath" ) );
            if( fileList == null ){
                throw new IllegalArgumentException( String.format( "no files on specified path: %s", filepath ) );
            }
            
            int numOfFiles = fileList.length;
            log.info(String.format( "\n number of files = %s \n", numOfFiles ) );
            FTList = new FutureTask[ numOfFiles ];
            
            InputStream data;
            
            log.debug( String.format( "create CargoContainers and give them to the DDP.createAndJoinThread method" ) );
            log.debug( String.format( "store the FutureTask in an array together with documenttitle" ) );
            
            for(int filesSent = 0; filesSent < numOfFiles; filesSent++){
                try{
                    data = new FileInputStream(fileList[filesSent]);
                    CargoContainer cc = new CargoContainer(data, mimetype, lang, submitter, format);
                    FTList[filesSent] = DDP.createAndJoinThread(cc);
                    log.info( String.format( "Calling createAndJoin %s. time", filesSent + 1 ) );
                    
                }catch(Exception thise){
                    System.out.print("\n Exception in DataDockPoolAdm \n");
                    thise.printStackTrace();
                    System.exit(1);
                }
            }
            log.info( "All files given to the DataDockPool" );
            //Loop that continues until all files have docked
            log.info("entering while loop in DataDockPoolAdm");
           
            while(answersReceived < numOfFiles){
                //         log.info("\n In the while loop \n");
                for(int x = 0; x < numOfFiles; x++){
                    //check if answer is received for this file
                    if(fileNameList[x].equals( doneString) ){
                        // go to next element
                    }else{
                        //log.info( "\n calling isDone \n" );
                        if( FTList[x].isDone() ){
                            
                            estimateMessageString = String.format("The file: %s , will take approximately: %s to process \n", fileNameList[x],FTList[x].get() );
                            log.info(estimateMessageString);
                            /**
                             * Changing the corresponding String in the nameList 
                             * to the donestring, yes its crappy, but it works for now 
                             */                          
                            fileNameList[x] = doneString; 
                            answersReceived++;   
                            log.info(String.format("%s files to go",numOfFiles - answersReceived));
                        }
                    }
                    
                }
            }
            log.info( String.format( "%s files stored in Fedora", numOfFiles ) );
            
    }
}