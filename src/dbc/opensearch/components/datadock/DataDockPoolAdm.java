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
        try{
        DataDockPool DDP = new DataDockPool(10);
        }catch(Exception e){
            e.printStackTrace();
        }
        // 20: get the properties defined at program start
        // Submitter, mimetype, lang, path. Antager at alle filer er på samme sprog  
        String Submitter = System.getProperty("submitter");
        String mimetype = System.getProperty("mimetype");
        String lang = System.getProperty("lang");
        String filepath = System.getProperty("filepath");
       // 30: read the files defined in the properties into an arraylist (fileList)
        String[] fileList;
        
        // 40: for the first 10 elements in fileList, create
        // CargoContainers and give them to the DDP.createAndJoinThread method
        // store the FutureTask in an array together with documenttitle
        // 50: pol the FutureTasks, when an answer is received, 
        // print it to std out and create a CargoContainer from the next 
        // element in fileList and overwrite the answering FutureTask 
        // with a new created with createAndJoinThread
        // 60 write the num of files stored in Fedora   
    }
}