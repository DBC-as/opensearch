/**
 *
 *This file is part of opensearch.
 *Copyright © 2009, Dansk Bibliotekscenter a/s,
 *Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043
 *
 *opensearch is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation, either version 3 of the License, or
 *(at your option) any later version.
 *
 *opensearch is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * \file FileHarvest.java
 * \brief The FileHarvest class
 * \package components.harvest;
 */

package dk.dbc.opensearch.components.harvest;


import dk.dbc.opensearch.common.config.DatadockConfig;
import dk.dbc.opensearch.common.config.HarvesterConfig;
import dk.dbc.opensearch.common.helpers.XMLFileReader;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.common.os.FileHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * FileHarvest class. Implements the IHarvester interface and acts as a
 * fileharvester for the datadock. It implements the methods start,
 * shutdown and getJobs. It is an eventdriven class.
 *
 * This file harvester assumes some things about path given as an argument:
 *
 * The path has to be a directory with the following structure:
 *
 * polling path
 * | - submitter1
 * | | -format1
 * | | | - job1
 * | | | - job2
 * | | -format2
 * | | | - job3
 * | - submitter2
 * .
 * .
 * .
 *
 * There are no restrictions on the number of submitters, formats or
 * jobs - and the jobs can be files or directorys.
 *
 * The harvester only returns a job after the second consecutive time
 * it has been found and its filesize is unchanged.
 */
public class FileHarvest implements IHarvester
{
    static Logger log = Logger.getLogger( FileHarvest.class );


    private File path;
    private Vector< InputPair< File, Long > > submitters;
    private Vector< InputPair< File, Long > > formats;
    private Vector< InputPair< String, String > > submittersFormatsVector;
    private String datadockJobsFilePath;
    private String toHarvestFolder;
    private String harvestDoneFolder;
    private int max;


    /**
     * Constructs the FileHarvest class, and starts polling the given path for
     * files and subsequent file-changes.
     *
     * @param path The path to the directory to harvest from.
     *
     * @throws IllegalArgumentException if the path given is not a directory.
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws ConfigurationException
     */
    public FileHarvest( ) throws IllegalArgumentException, SAXException, IOException, ConfigurationException, FileNotFoundException
    {
        this.submitters = new Vector< InputPair< File, Long > >();
        this.formats = new Vector< InputPair< File, Long > >();

        // Getting path for the jobs file for the building of the submitterformatvector
        datadockJobsFilePath = DatadockConfig.getPath();

        toHarvestFolder = HarvesterConfig.getFolder();
        path = FileHandler.getFile( toHarvestFolder );
        if ( ! path.exists() )
        {
            String errMsg = String.format( "Harvest folder '%s' does not exist!", path );
            log.error( "FileHarvest: " + errMsg );
            throw new FileNotFoundException( errMsg );
        }

        harvestDoneFolder = HarvesterConfig.getDoneFolder();
        File harvestDonePath = FileHandler.getFile( harvestDoneFolder );
        if ( ! harvestDonePath.exists() )
        {
            String errMsg = String.format( "'Harvest done folder' '%s' does not exist!", harvestDonePath );
            log.error( "FileHarvest: " + errMsg );
            throw new FileNotFoundException( errMsg );
        }

        max = HarvesterConfig.getMaxToHarvest();
    }


    /**
     * Starts The datadock. It initializes vectors and add found jobs to the application vector.
     */
    public void start() throws ParserConfigurationException, SAXException, IOException
    {
        log.debug( "start() called" );

        initVectors();
        log.debug( "Vectors initialized" );
    }


    /**
     * Shuts down the fileharvester
     */
    public void shutdown()
    {
        log.debug( "shutdown() called" );
    }



    /**
     * Private method to initialize the local vectors representing the
     * polling directory.
     */
    private void initVectors() throws ParserConfigurationException, SAXException, IOException
    {
        log.debug( "initvectors() called" );

        File datadockJobsFile = FileHandler.getFile( datadockJobsFilePath );
        NodeList jobNodeList = XMLFileReader.getNodeList( datadockJobsFile, "job" );
        submittersFormatsVector = new Vector< InputPair< String, String > >();
        int jobNodeListLength = jobNodeList.getLength();
        for( int i = 0; i < jobNodeListLength; i++ )
        {
            Element pluginElement = (Element)jobNodeList.item( i );
            String formatAtt = pluginElement.getAttribute( "format" );
            String submitterAtt = pluginElement.getAttribute( "submitter" );
            InputPair< String, String > submitterFormatPair = new InputPair< String, String >( submitterAtt, formatAtt );
            if( ! submittersFormatsVector.contains( submitterFormatPair ) )
            {
                log.debug( String.format( "Adding submitter and format to Vector submitterFormatPair: %s and %s", submitterAtt, formatAtt ) );
                submittersFormatsVector.add( submitterFormatPair );
            }
            else
            {
                log.warn( String.format( "The format: %s with submitter; %s was not added to the vector, the jobs file contains redundant/erronous information", formatAtt, submitterAtt ) );
            }
        }

        log.debug( "submitterFormatsVector: \n" + submittersFormatsVector.toString() );
        log.debug( "Submitters:" );

        for( File submitter : path.listFiles() )
        {
            if( submitter.isDirectory() )
            {

                log.debug( String.format( "adding submitter: path='%s'", submitter.getAbsolutePath() ) );
                submitters.add( new InputPair< File, Long >( submitter, submitter.lastModified() ) );
            }
        }

        log.debug( "formats:" );
        for( InputPair<File, Long> submitter : submitters )
        {
            File submitterFile = submitter.getFirst();
            for( File format : submitterFile.listFiles() )
            {
                if ( checkSubmitterFormat( submitterFile, format ) )
                {
                    log.debug( String.format( "format: path='%s'", format.getAbsolutePath() ) );
                    formats.add( new InputPair< File, Long >( format, format.lastModified() ) );
                }
            }
        }
    }


    private boolean checkSubmitterFormat( File submitterFile, File formatFile )
    {
        String submitterFilePath = submitterFile.getAbsolutePath().substring( submitterFile.getAbsolutePath().lastIndexOf( "/" ) + 1 );
        log.debug( "FileHarvest.checkSubmitterFormat -> submitter: " + submitterFilePath );
        String formatFilePath = formatFile.getAbsolutePath().substring( formatFile.getAbsolutePath().lastIndexOf( "/") + 1 );
        log.debug( "FileHarvest.checkSubmitterFormat -> format: " + formatFilePath );

        InputPair< String, String > pair = new InputPair< String, String >( submitterFilePath, formatFilePath );
        boolean contains = submittersFormatsVector.contains( pair );
        log.debug( "FileHarvest.checkSubmitterFormat -> contains: " + contains );
        if ( contains )
        {
            return true;
        }
        else
        {
            log.debug( "FileHarvest.checkSubmitterFormat -> Vector: " + submittersFormatsVector.toString() );
            return false;
        }
    }


    /**
     * getJobs. Locate jobs and returns them.  First off, the
     * candidates already registered analyzed. if their filesize has
     * remained the same as last time it is removed from the
     * applications vector and added to the newJobs vector and
     * returned when the method exits.
     *
     * afterwards it finds new jobs and adds them to the applications
     * vector, and generate a new snapshot of the harvest directory.
     * @throws ConfigurationException
     *
     * @returns A vector of Datadockjobs containing the necessary information to process the jobs.
     */
    public Vector< DatadockJob > getJobs() throws FileNotFoundException, IOException, ConfigurationException
    {
        Vector< DatadockJob > jobs = new Vector< DatadockJob>();
        HashSet< InputPair< File, Long > > newJobs = getNewJobs();
        for( InputPair< File, Long > job : newJobs )
        {
            URI uri = job.getFirst().toURI();
            String grandParentFile = job.getFirst().getParentFile().getParentFile().getName();
            String parentFile = job.getFirst().getParentFile().getName();
            DatadockJob datadockJob = new DatadockJob( uri, grandParentFile, parentFile );
            log.debug( String.format( "found new job: path='%s', submitter='%s', format='%s'", 
                                      datadockJob.getUri().getRawPath(),
                                      datadockJob.getSubmitter(),
                                      datadockJob.getFormat() ) );
            jobs.add( datadockJob );
        }

        return jobs;
    }


    /** 
     * Returns a HashSet of InputPairs with file objects. The size of
     * the HashSet is determined by the getMaxToHarvest configuration
     * values, which has no default.
     * \todo: make getMaxToHarvest default to something sane
     * 
     * 
     * @return 
     */
    private HashSet< InputPair< File, Long > > getNewJobs() throws FileNotFoundException, IOException, ConfigurationException
    {
        log.debug( "Calling FileHarvest.getNewJobs");
        HashSet< InputPair< File, Long > > jobs = new HashSet< InputPair< File, Long > >();

        //int max = HarvesterConfig.getMaxToHarvest();
        log.debug( "FileHarvest.getNewJobs: Vector formats: " + formats.toString() );
        for( InputPair< File, Long > format : formats )
        {
            File[] files = format.getFirst().listFiles();
            int l = files.length;
            int i = 0;
            while( i < l && i < max )
            {
                File job = files[i];
                String path = job.getPath();
                String newPath = path.replace( toHarvestFolder, harvestDoneFolder );
                String destFldrStr = newPath.substring( 0, newPath.lastIndexOf( "/" ) );
                File destFldr = FileHandler.getFile( destFldrStr );
                File dest = FileHandler.getFile( newPath );
                move( job, destFldr, dest );
                jobs.add( new InputPair< File, Long >( dest, dest.length() )  );
                i++;
            }
        }

        // System.out.println( "FileHarvest.getNewsJobs done harvesting first files max: " + max );
        log.debug( "FileHarvest.getNewsJobs done harvesting first files max: " + max );

        return jobs;
    }


    private void move( File src, File destFldr, File dest ) throws FileNotFoundException, IOException
    {
        log.debug( "Creating new destFldr: " + destFldr.getAbsolutePath().toString() );
        boolean ok = false;
        if ( ! destFldr.exists() )
        {
            //System.out.println( "hep" );
            ok = destFldr.mkdirs();
        }
        else
        {
            ok = true;
        }

        if ( ok )
        {
            log.debug( "destFldr created: " + destFldr.getPath().toString() );
            ok = src.renameTo( dest );
            if ( ! ok )
            {
                log.warn( String.format( "Could not rename file: %s to %s", src.getAbsolutePath().toString(), dest.getAbsolutePath().toString() ) );
                throw new IOException( "IOException thrown in FileHarvest.move: Could not create new file: " + src.getAbsolutePath().toString() );
            }
        }
        else
        {
            log.warn( "Could not create destination folder for old files: " + destFldr.getAbsolutePath().toString() );
            throw new IOException( "IOException thrown in FileHarvest move: Could not create destination folder for old files:" + destFldr.getAbsolutePath().toString() );

        }
    }
}
