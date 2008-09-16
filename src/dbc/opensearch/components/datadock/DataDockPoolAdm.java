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

    public final static void main(String[] args){
        // 10: start the datadockpool
        DataDockPool DDP;
        try{
            DDP = new DataDockPool(10);
            //}catch(Exception e){
            //  e.printStackTrace();
            //}
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
            FutureTask[] FTList = new FutureTask[10];
            String doneString = "så er jeg færdig og du skal ikke kigge på mig";
            String estimateMessageString;
            int answersReceived = 0;

            fileNameList = dir.list();
            fileList = dir.listFiles();

            // 35: check if we got any files from the filepath
            if (fileList != null){
                // 37: should check that we arent taking directories, later...
                int numOfFiles = fileList.length;
                
                InputStream data;
          
                // create CargoContainers and give them to the DDP.createAndJoinThread method
                // store the FutureTask in an array together with documenttitle

                for(int filesSent = 0; filesSent < numOfFiles; filesSent++){
                    // create cc
                    try{
                        data = new FileInputStream(fileList[filesSent]);
                        CargoContainer cc = new CargoContainer(data, mimetype, lang, submitter);
                        FTList[filesSent] = DDP.createAndJoinThread(cc);
                        
                    }catch(Exception e){
                        System.out.print("\n");
                        e.printStackTrace();
                    }
                }
                System.out.print("All files given to the DataDockPool \n");
                //Loop that continues until all files have docked
                while(answersReceived < numOfFiles){
                    for(int x = 0; x < numOfFiles; x++){
                        //check if answer is received for this file
                        if(fileNameList[x].equals( doneString) ){
                            if(FTList[x].isDone()){
                                
                                estimateMessageString = String.format("The file: %s , will take approximately: %s to process \n", fileNameList[x],FTList[x].get() );
                                System.out.print(estimateMessageString);
                                fileNameList[x] = doneString;
                                answersReceived++;
                                
                            }
                        }

                    }
                }
                // 50: pol the FutureTasks, when an answer is received, 
                // print it to std out and create a CargoContainer from the next 
                // element in fileList and overwrite the answering FutureTask 
                // with a new created with createAndJoinThread
                // 60 write the num of files stored in Fedora
            }
            else{// we didnt get any files!!!!!! 
                System.out.print("No files at the specified filepath! \n");
            }   
        }catch(Exception e){
            System.out.print("Could not initialize the datapool \n");
            e.printStackTrace();
        }
    }
}