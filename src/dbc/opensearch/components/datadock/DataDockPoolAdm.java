/**
 * \file DataDockPoolAdm.java
 * \brief The DataDockPoolAdm class
 * \package datadock
 */

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
import java.lang.IllegalArgumentException; 
import java.util.concurrent.ExecutionException;


/**
 * \ingroup datadock
 * \brief This class administrates the start up of DataDockPool, giving it
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
    FedoraHandler fedoraHandler;
    DataDockPool DDP;

    String[] fileNameList;
    /**
    * \todo: fileList and fileNameList should be a vector instead
    **/
    File[] fileList;
    FutureTask[] FTList = null;
    //    int answersReceived = 0;
    // int numOfFiles = 0;

    public DataDockPoolAdm( Estimate estimate, Processqueue processqueue, FedoraHandler fedoraHandler )throws ConfigurationException, ClassNotFoundException, MalformedURLException, UnknownHostException, ServiceException, IOException {

        log.debug( "Entering the constructor" );
        this.processqueue = processqueue;
        this.estimate = estimate;
        this.fedoraHandler = fedoraHandler;
        log.debug( "Exiting the constructor" );
        this.filepath = null;
    }
    
    public void start( String mimetype, String lang, String submitter, String format, String filepath )throws FileNotFoundException, InterruptedException, ExecutionException, IOException, ConfigurationException, ClassNotFoundException {

       
        log.info( String.format( "Creating DataDockPool with %s threads", 10 ) );
        DataDockPool DDP = new DataDockPool( 10, estimate, processqueue, fedoraHandler );

        // passes it on in a method call so it can be mocked for testing
        privateStart(DDP, mimetype, lang, submitter, format, filepath);

    }
    private void privateStart( DataDockPool DDP, String mimetype, String lang, String submitter, String format, String filepath ) throws FileNotFoundException, InterruptedException, IOException, ConfigurationException, ClassNotFoundException {
        log.debug( "Entering the privateStart method" );
        this.DDP = DDP;
        
        log.debug( String.format( "Getting properties for the CargoContainer" ) );
        this.mimetype = mimetype;
        this.lang = lang;
        this.submitter = submitter;
        this.format = format;
        
        // 10: call the reader and filter method
        log.debug( String.format( "read the files defined in the properties into an arraylist (fileList)" ) );

        readFiles( filepath, fileNameList, fileList);
        
        // 20: create the list of FutureTasks
        createFutureTaskList( fileList);
        
        log.debug( "Calling the checkThreads method" );
        
        // 30: check the futureTaskList
        checkThreads( FTList );
        
        log.debug( "Exiting the privateStart method" );
    }
    
    /**
     * Reads the files from the specified path into the array fileList
     * and the names of the files into the array fileNameList
     */
    private void readFiles( String filepath, String[] fileNameList, File[] fileList){   
        
        File testFile = new File( filepath );
//         log.debug( String.format("Is the filepath a directory: '%s'", testFile.isDirectory() ) );
//         log.debug( String.format("Is the filepath a file: '%s'", testFile.isFile() ) );
//         log.debug( String.format("Does the filepath end with *.xml: '%s'", filepath.endsWith("*.xml"  ) ) );
        if(!( testFile.isDirectory() || testFile.isFile() || filepath.endsWith( "*.xml" ) ) ){
            log.debug( "throws exception" );
            throw new IllegalArgumentException( String.format( "the filepath: '%s' defines noting usefull ", filepath ) );
        }
        this.fileList = fileList;
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
                fileNameList = new String[]{file.getName()};
                //fileNameList[0] = file.getName();
                fileList = new File[]{file}; // and this
                // fileList[0] = file;
                
            }else{//we assume its a path ending with *.xml signaling that we must take 
                // all .xml files in the directory
                log.debug(String.format("Extracting from the dirpath form index '%s' to index '%s'", 0, filepath.lastIndexOf('/') ));
                
                String dirpathString = filepath.substring( 0, filepath.lastIndexOf('/') );
                log.debug(String.format("dirpathString = '%s'", dirpathString) );
                File dirpath = new File(dirpathString);
                fileNameList = dirpath.list( new XmlFileFilter() );
                fileList = dirpath.listFiles( new XmlFileFilter() );
                log.debug( String.format( "Det er '%s' at der er noget i fileList", fileList != null ) );
                
            }                        
        }
        this.filepath = filepath;
        this.fileList = fileList;
        this.fileNameList = fileNameList;
    }
    
    /**
    * creates the cargoContainers and gives them to the createAndJoinThread on the DDP
    * the returned FutureTask are put on the FTList
    */
    private void createFutureTaskList( File[] fileList ) throws FileNotFoundException, IOException, ConfigurationException, ClassNotFoundException {
       
        int numOfFiles = 0;
 
        log.debug( String.format( "check if we got any files from the filepath" ) );
        if( fileList == null ){
            throw new IllegalArgumentException( String.format( "no files on specified path: %s", filepath ) );
        }
        
        numOfFiles = fileList.length;
        log.info(String.format( "\n number of files = %s \n", numOfFiles ) );
        FTList = new FutureTask[ numOfFiles ];
        
        log.debug( String.format( "create CargoContainers and give them to the DDP.createAndJoinThread method" ) );
        log.debug( String.format( "store the FutureTask in an array together with documenttitle" ) );
        
        for(int filesSent = 0; filesSent < numOfFiles; filesSent++){
           
            CargoContainer cc = createCargoContainerFromFile(fileList[filesSent], mimetype, lang, submitter, format );
          
            FTList[filesSent] = DDP.createAndJoinThread(cc);
            log.info( String.format( "Calling createAndJoin %s. time", filesSent + 1 ) );
            
        }
        log.info( "All files given to the DataDockPool" );
    }

    /**
     * checks the FutureTask whether they are done or running. When a FututreTask
     * is done the result is "given to the user"
     */ 
    private void checkThreads( FutureTask[] FTList) throws InterruptedException {
        
        log.debug( "entering checkThreads" );
        
        /** \todo: what the *&^%$ is this? very simple: a unique string to 
         * match on, this code is NOT intended to be reused! It is a transcription
         * of a Korean sentence meaning "I do not love you"
         */
        String doneString = "narn noRl saRang hATi arNar";
        int numOfFiles = FTList.length;
        int answersReceived = 0;
        String estimateMessageString;

        while(answersReceived < numOfFiles){
            //         log.info("\n In the while loop \n");
            for(int x = 0; x < numOfFiles; x++){
                //check if answer is received for this file
                if(fileNameList[x].equals( doneString) ){
                    // go to next element
                }else{
                    //log.info( "\n calling isDone \n" );
                    if( FTList[x].isDone() ){
                        try{
                            // log.debug( "A thread is done or it terminated with an exception" );
                            estimateMessageString = String.format("The file: %s , will take approximately: %s to process \n", fileNameList[x],FTList[x].get() );
                            log.info( estimateMessageString );
                        }catch(ExecutionException ee){
                            //Catching exception from the thread
                            Throwable cause = ee.getCause();
                            log.fatal( String.format( "Caught thread error associated with file: '%s' ", fileNameList[x] ) );
                            RuntimeException re = new RuntimeException(cause);
                            throw re;
                        }
                        
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
   
    /**
    * Creates a CargoContainer from a file, and other info needed
    * \todo: Do we need this method? 
    */
    private CargoContainer createCargoContainerFromFile( File file, String mimetype, String lang, String submitter, String format ) throws IOException, FileNotFoundException {
        InputStream data = new FileInputStream( file );
        CargoContainer cc = new CargoContainer( data, mimetype, lang, submitter, format );
        return cc;
    }

}
