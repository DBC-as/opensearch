package dbc.opensearch.components.datadock;

import java.util.concurrent.*;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Logger;
import java.io.*;

/**
 * This class administrates the start up of DataDockPool, giving it
 * CargoContainers to process and it maintains an array of documenttitles
 * and the related FutureTasks that will contain estinmates
 *
 */
public class DataDockPoolAdm {
    private static final Logger log = Logger.getRootLogger();
    
    public final static void main(String[] args){
        // 10: start the datadockpool
        DataDockPool DDP;
        try{
            DDP = new DataDockPool(10);
            // 20: get the properties defined at program start
            // Submitter, mimetype, lang, path. Antager at alle filer er på samme sprog
            String submitter = System.getProperty("submitter");
            String mimetype = System.getProperty("mimetype");
            String lang = System.getProperty("lang");
            String filepath = System.getProperty("filepath");
            // 30: read the files defined in the properties into an arraylist (fileList)
            File dir = new File(filepath);
            //int[] activeFilesList = int[10];
            String[] fileNameList;
            File[] fileList;
            FutureTask[] FTList = null;
            String doneString = "så er jeg færdig og du skal ikke kigge på mig";
            String estimateMessageString;
            int answersReceived = 0;

            fileNameList = dir.list( new FileFilter() );
            fileList = dir.listFiles( new FileFilter() );
       
            // 35: check if we got any files from the filepath
            if( fileList == null ){
                throw new IllegalArgumentException( String.format( "no files on specified path: %s", filepath ) );
            }

            // 37: should check that we arent taking directories, later...
            int numOfFiles = fileList.length;
            log.info(String.format( "\n number of files = %s \n", numOfFiles ) );
            FTList = new FutureTask[ numOfFiles ];

            InputStream data;

            // create CargoContainers and give them to the DDP.createAndJoinThread method
            // store the FutureTask in an array together with documenttitle

            for(int filesSent = 0; filesSent < numOfFiles; filesSent++){
                // create cc
                try{
                    data = new FileInputStream(fileList[filesSent]);
                    CargoContainer cc = new CargoContainer(data, mimetype, lang, submitter);
                    FTList[filesSent] = DDP.createAndJoinThread(cc);
                    log.info( String.format( "Calling createAndJoin %s. time", filesSent + 1 ) );
                    log.info( String.format( "Number of futureTasks in FTList = %s", FTList.length ) );
                }catch(Exception e){
                    System.out.print("\n Exception in DataDockPoolAdm \n");
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            System.out.print("All files given to the DataDockPool \n");
            //Loop that continues until all files have docked
            log.info("\n entering while loop in DataDockPoolAdm \n");
            log.info(String.format("answers = %s numOfFiles = %s ",answersReceived, numOfFiles));
            while(answersReceived < numOfFiles){
                log.info("\n In the while loop \n");
                for(int x = 0; x < numOfFiles; x++){
                    //check if answer is received for this file
                    if(fileNameList[x].equals( doneString) ){
                        // go to next element
                        log.info("file done");
                    }else{
                        log.info( "\n calling isDone \n" );
                        if( FTList[x].isDone() ){
                            
                            estimateMessageString = String.format("The file: %s , will take approximately: %s to process \n", fileNameList[x],FTList[x].get() );
                            log.info(estimateMessageString);
                            fileNameList[x] = doneString;
                            answersReceived++;   
                        }
                    }

                }
                // 50: pol the FutureTasks, when an answer is received,
                // print it to std out and create a CargoContainer from the next
                // element in fileList and overwrite the answering FutureTask
                // with a new created with createAndJoinThread
                // 60 write the num of files stored in Fedora
            }
        }catch(Exception e){
            log.info("\n Could not initialize the DataDockPool \n");
            e.printStackTrace();
        }
    }
}