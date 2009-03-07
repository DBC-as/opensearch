/**
 * \file FileHarvest.java
 * \brief The FileHarvest class
 * \package components.harvest;
 */

package dk.dbc.opensearch.components.harvest;


import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.types.Pair;
//import com.mallardsoft.tuple.Pair;
import com.mallardsoft.tuple.Tuple;

import java.io.File;
import java.util.HashSet;
import java.util.Vector;

import org.apache.log4j.Logger;


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
    /**
     *
     */
    static Logger log = Logger.getLogger( FileHarvest.class );
    private File path;
    private Vector< Pair< File, Long > > submitters;
    private Vector< Pair< File, Long > > formats;
    private HashSet< File > jobSet;
    private HashSet< Pair< File, Long > > jobApplications;

    
    /**
     * Constructs the FileHarvest class, and starts polling the given path for files and subsequent file-changes.
     * 
     * @param path The path to the directory to harvest from.
     * 
     * @throws IllegalArgumentException if the path given is not a directory.
     */
    public FileHarvest( File path ) throws IllegalArgumentException 
    {
        log.debug( String.format( "Constructor( path='%s' )", path.getAbsolutePath() ) );
        
        if ( ! path.isDirectory() )
            throw new IllegalArgumentException( String.format( "'%s' is not a directory !", path.getAbsolutePath() ) );
        
        this.path = path;
        this.jobApplications = new HashSet< Pair< File, Long > >();
        this.submitters = new Vector< Pair< File, Long > >();
        this.formats = new Vector< Pair< File, Long > >();
        this.jobSet = new HashSet< File >();
    }

    
    /**
     * Starts The datadock. It initializes vectors and add found jobs to the application vector.
     */
    public void start()
    {
        log.debug( "start() called" );

        initVectors();        
        log.debug( "Vectors initialized" );
        
        for( Pair< File, Long > job : findAllJobs() )
        {
            //log.debug( String.format( "adding path='%s' to jobSet and jobApllications", Tuple.get1(job).getAbsolutePath() ) );
            log.debug( String.format( "adding path='%s' to jobSet and jobApllications", job.getFirst().getAbsolutePath() ) );
            //jobSet.add( Tuple.get1( job ) );
            jobSet.add( job.getFirst() );
        }
        
        jobApplications = findAllJobs();
    }

    
    /**
     * Shuts down the fileharvester
     */
    public void shutdown()
    {
        log.debug( "shutdown() called" );
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
     * 
     * @returns A vector of Datadockjobs containing the necessary information to process the jobs.
     */

    public Vector<DatadockJob> getJobs()
    {
        log.debug( "getJobs() called " );
        // validating candidates - if the filelength have remained the
        // same for two consecutive calls it is added to newJobs
        Vector< DatadockJob > newJobs = new Vector<DatadockJob>();
        HashSet< Pair< File, Long > > removeJobs = new HashSet< Pair< File, Long > >();
    
        for( Pair< File, Long > job : jobApplications )
        {
            //if( Tuple.get1( job ).length() == Tuple.get2( job) )
        	if( job.getFirst().length() == job.getSecond() )
            {
                //DatadockJob datadockJob = new DatadockJob( Tuple.get1( job ).toURI(),
                //                                           Tuple.get1( job ).getParentFile().getParentFile().getName(),
                //                                           Tuple.get1( job ).getParentFile().getName() );
                DatadockJob datadockJob = new DatadockJob( job.getFirst().toURI(),
                                                           job.getFirst().getParentFile().getParentFile().getName(),
                                                           job.getFirst().getParentFile().getName() );
                log.debug( String.format( "found new job: path='%s', submitter='%s', format='%s'",
                                          datadockJob.getUri().getRawPath(),
                                          datadockJob.getSubmitter(),
                                          datadockJob.getFormat() ) );
                newJobs.add( datadockJob );
                removeJobs.add( job );
            }
        }
        
        // removing confirmed jobs from applications
        for( Pair< File, Long > job : removeJobs )
        {
            //log.debug( String.format( "Removing job='%s' from applications", Tuple.get1( job ).getAbsolutePath() ) );
        	log.debug( String.format( "Removing job='%s' from applications", job.getFirst().getAbsolutePath() ) );
            jobApplications.remove( job );
        }

        // Finding new Jobs
        // Has anything happened ?
        boolean changed = false;
        for( Pair< File, Long > format : formats )
        {
            //if( Tuple.get1( format ).lastModified() > Tuple.get2( format ) )
        	if( format.getFirst().lastModified() > format.getSecond() )
            {
                changed = true;
            }
        }

        if( changed )
        {
            log.debug( "Files changed" );
            for( File newJob : findNewJobs() )
            {
                log.debug( String.format( "adding new job to applications: path='%s'", newJob.getAbsolutePath() ) );
                //jobApplications.add( Tuple.from( newJob, newJob.length() ) );
                //jobApplications.add( Tuple.from( newJob, newJob.length() ) );
                jobApplications.add( new Pair< File, Long >( newJob, newJob.length() ) );
            }

            // generating new snapshot
            submitters = new Vector<Pair<File, Long >>();
            formats = new Vector<Pair<File, Long >>();
            initVectors();
             
            jobSet = new HashSet< File >();
            for( Pair< File, Long > job : findAllJobs() )
            {                
//                log.debug( String.format( "adding path='%s' to jobSet", Tuple.get1( job ).getAbsolutePath() ) );
//                jobSet.add( Tuple.get1( job ) );
            }
        }
        
        return newJobs;
    }

    
    /**
     * Private method to initialize the local vectors representing the
     * polling directory.
     */
    private void initVectors()
    {
        log.debug( "initvectors() called" );
        
        log.debug( "Submitters:" );        
        for( File submitter : path.listFiles() )
        {
            if( submitter.isDirectory() )
            {
                log.debug( String.format( "adding submitter: path='%s'", submitter.getAbsolutePath() ) );
                //submitters.add( Tuple.from( submitter, submitter.lastModified() ) );
                submitters.add( new Pair< File, Long >( submitter, submitter.lastModified() ) );
            }
        }
        
        log.debug( "formats:" );        
        for( Pair<File, Long> submitter : submitters )
        {
            //for( File format : Tuple.get1( submitter ).listFiles() )
        	for( File format : submitter.getFirst().listFiles() )
            {
                //log.debug( String.format( "format: path='%s'", format.getAbsolutePath() ) );
        		//formats.add( Tuple.from( format, format.lastModified() ) );
        		formats.add( new Pair< File, Long >( format, format.lastModified() ) );
            }
        }
    }

    
    /**
     * Finds the new jobs in the poll Directory
     * 
     * @returns a hashset of new job files.
     */
    private HashSet<File> findNewJobs()
    {
        log.debug( "findNewJobs() called" );
        HashSet<File> currentJobs = new HashSet<File>();
        for( Pair<File, Long> job : findAllJobs() )
        {
            //currentJobs.add( Tuple.get1(job) );
        	currentJobs.add( job.getFirst() );
        }
        
        HashSet<File> newJobs = new HashSet<File>( jobSet );
        log.debug( String.format( "newjob size='%s', '%s'", newJobs.size(), newJobs.size() ) );
        newJobs.addAll( currentJobs );
        newJobs.removeAll( jobSet );

        for( File job : newJobs )
        {
            log.debug( String.format( "found job: '%s'", job.getAbsolutePath() ) );
        }
        
        return newJobs;
    }

    /**
     * Finds all jobs in the poll Directory
     * 
     * @returns a hashset of pairs containing new job files and their size.
     */
    private HashSet< Pair< File, Long > > findAllJobs()
    {
        log.debug( "findAllJobs() called" );
        HashSet< Pair< File, Long > > jobs = new HashSet< Pair< File, Long > >();
        
        for( Pair< File, Long > format : formats )
        {
        	//int l = Tuple.get1( format ).listFiles().length;
        	int l = format.getFirst().listFiles().length;
        	log.debug( "FileHarvest: fileList length:" + l + " Format: " + format.getFirst().getAbsolutePath() );
            //for( File job : Tuple.get1( format ).listFiles() )
        	for( File job : format.getFirst().listFiles() )
            {
                log.debug( String.format( "found job: '%s'", job.getAbsolutePath() ) );
                //jobs.add( Tuple.from( job, job.length() )  );
                jobs.add( new Pair< File, Long >( job, job.length() )  );
            }
        }
        
        return jobs;
    }
}
