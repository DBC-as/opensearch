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
 * \file FileHarvestLight.java
 * \brief The FileHarvestLight class
 */

package dk.dbc.opensearch.components.harvest;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.IJob;
import dk.dbc.opensearch.common.types.IIdentifier;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.os.StreamHandler;
import dk.dbc.opensearch.common.os.NoRefFileFilter;

import dk.dbc.opensearch.common.xml.XMLUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;

import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * this is a class for testuse!
 * It looks in the directory named harvest at the root of execution and reads
 * files from there. It starts by reading all files but the .ref files. The .ref files
 * contains referencedata about the other files so that xyz.ref descripes
 * the file xyz.someformat. Files without an associated .ref file will not be read.
 */
public class FileHarvestLight implements IHarvest
{
    static Logger log = Logger.getLogger( FileHarvestLight.class );

    
    private Vector<String> FileVector;
    private Iterator iter;
    private final FilenameFilter[] filterArray;
    // Some default values:
    private final String harvesterDirName = "Harvest";
    private final String successDirName = "success"; // will be made subdir of harvesterDirName
    private final String failureDirName = "failure"; // will be made subdir of harvesterDirName
    private final File dataFile;
    private final File successDir;
    private final File failureDir;

    /**
     *
     */
    public FileHarvestLight() throws HarvesterIOException
    {
        filterArray = new FilenameFilter[] { new NoRefFileFilter() };

        dataFile = FileHandler.getFile( harvesterDirName );
        if ( ! dataFile.exists() )
        {
            String errMsg = String.format( "Harvest folder %s does not exist!", dataFile );
            log.fatal( "FileHarvestLight: " + errMsg );
            throw new HarvesterIOException( errMsg );
        }

        successDir = createDirectoryIfNotExisting( dataFile, successDirName );
        failureDir = createDirectoryIfNotExisting( dataFile, failureDirName );
    }


    public void start()
    {
        //get the files in the dir
        FileVector = FileHandler.getFileList( harvesterDirName , filterArray, false );
        iter = FileVector.iterator();
    }


    public void shutdown()
    {
    }


    public List< IJob > getJobs( int maxAmount )
    {
        //Element root = null;
        String fileName;
        String refFileName;
        URI fileURI;
        byte[] referenceData = null;
        InputStream ISrefData = null;
        DocumentBuilderFactory docBuilderFactory;
        DocumentBuilder docBuilder = null;
        Document doc;

        docBuilderFactory = DocumentBuilderFactory.newInstance();
        try
        {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        }
        catch( ParserConfigurationException pce )
        {
            log.error( pce.getMessage() );
        }
        doc = docBuilder.newDocument();

        List<IJob> list = new ArrayList<IJob>();
        for( int i = 0; i < maxAmount && iter.hasNext() ; i++ )
        {
            fileName = (String)iter.next();
            refFileName = fileName.substring( 0, fileName.lastIndexOf( "." ) ) + ".ref";
            //System.out.println( String.format( "created ref name %s for file %s", refFileName, fileName ) );
            File refFile = FileHandler.getFile( refFileName );
            if ( refFile.exists() )
            {
                try
                {
                    ISrefData = FileHandler.readFile( refFileName );
                }
                catch( FileNotFoundException fnfe )
                {
                    log.error( String.format( "File for path: %s couldnt be read", refFileName ) );
                }
                try
                {
                    doc = XMLUtils.getDocument( new InputSource( ISrefData ) );
                }
                catch( ParserConfigurationException ex )
                {
                    log.error( ex.getMessage() );
                }
                catch( SAXException ex )
                {
                    log.error( ex.getMessage() );
                }
                catch( IOException ex )
                {
                    log.error( ex.getMessage() );
                }

                File theFile = FileHandler.getFile( fileName );

                list.add( (IJob) new Job( new FileIdentifier( theFile.toURI() ), doc ) );
            }
            else
            {
                log.warn( String.format( "the file: %s has no .ref file", fileName ) );
                i--;
            }
        }
        return list;

    }

    /**
     *  @deprecated This function is replaced with {@link #getCargoContainer}.
     */
    @Deprecated
    public byte[] getData( IIdentifier jobId ) throws HarvesterUnknownIdentifierException
    {
        FileIdentifier theJobId = (FileIdentifier)jobId;
        byte[] data;
        InputStream ISdata;

        try
        {
            ISdata = FileHandler.readFile( theJobId.getURI().getRawPath() );
        }
        catch( FileNotFoundException fnfe )
        {
            throw new HarvesterUnknownIdentifierException( String.format( "File for path: %s couldnt be read", theJobId.getURI().getRawPath() ) );
        }
        try
        {
            data = StreamHandler.bytesFromInputStream( ISdata, 0 );
        }
        catch( IOException ioe )
        {
            throw new HarvesterUnknownIdentifierException( String.format( "Could not construct byte[] from InputStream for file %s ", theJobId.getURI().getRawPath() ) );
        }
        return data;
    }

    public CargoContainer getCargoContainer( IIdentifier jobId ) throws HarvesterUnknownIdentifierException, HarvesterIOException
    {
        CargoContainer returnCargo = null;
        

        return returnCargo;
    }



    /**
     * Wrapper to setStatus.
     * Notice that the PID is ignored. 
     */
    public void setStatusSuccess( IIdentifier Id, String PID ) throws HarvesterUnknownIdentifierException, HarvesterInvalidStatusChangeException
    {
	// Ignoring the PID!
	FileIdentifier id = (FileIdentifier)Id;
	log.info( String.format("the file %s was handled successfully", id.getURI().getRawPath() ) );

	File dataFile = new File( id.getURI().getRawPath() );

	setStatus( dataFile, successDir );

    }
   
    /**
     * Wrapper to setStatus.
     * Notice that the failureDiagnostic is ignored. 
     */
    public void setStatusFailure( IIdentifier Id, String failureDiagnostic ) throws HarvesterUnknownIdentifierException, HarvesterInvalidStatusChangeException
    {
	FileIdentifier id = (FileIdentifier)Id;
	log.info( String.format("the file %s was handled unsuccessfully", id.getURI().getRawPath() ) );
	log.info( String.format("FailureDiagnostic: %s", failureDiagnostic ) );

	File dataFile = new File( id.getURI().getRawPath() );

	setStatus( dataFile, failureDir );
    }

    /*
     *  setStatus
     */
    private void setStatus( File dataFile, File destDir ) throws HarvesterUnknownIdentifierException, HarvesterInvalidStatusChangeException
    {
	File refFile = createRefFile( dataFile );

	log.trace( String.format( "dataFile absolute path: %s", dataFile.getAbsolutePath() ) );
	log.trace( String.format( "refFile absolute path : %s", refFile.getAbsolutePath() ) );

	moveFile( refFile, destDir );
	moveFile( dataFile, destDir );
    }

    private void moveFile( File f, File toDir )
    {
	// This method ought to check whether f actually is a file, 
	// and whether toDir actually is a directory.

	log.trace( String.format( "Called with filename: [%s]", f.getName() ) );
	log.trace( String.format( "Called with destination directory: [%s]", toDir.getName() ) );

	// Some tests for validity:
	if ( ! f.exists() )
	{
	    log.error( String.format( "The file: [%s] does not exist.", f.getAbsolutePath() ) );
	    return;
	}
	if ( ! f.isFile() ) 
	{
	    log.error( String.format( "[%s] is not a file.", f.getAbsolutePath() ) );
	    return;
	}
	if ( ! toDir.exists() )
	{
	    log.error( String.format( "The directory: [%s] does not exist.", toDir.getAbsolutePath() ) );
	    return;
	}
	if ( ! toDir.isDirectory() ) 
	{
	    log.error( String.format( "[%s] is not a directory.", toDir.getAbsolutePath() ) );
	    return;
	}
	

	boolean res = f.renameTo( new File( toDir, f.getName() ) );
	if (res) {
	    log.info( String.format( "File successfully moved: [%s]", f.getName() ) );
	} else {
	    log.error( String.format( "Could not move the file: [%s]", f.getName() ) );
	}
    }

    /*
     *  Private function for creating reference filenames from existing (currently xml) filenames.
     *  \note: This function has a problem: It searches for the last index of . (dot), it will
     *  therefore not correctly handle filnames as 'filename.tar.gz'.
     */
    private File createRefFile( File f )
    {
	final String refExtension = ".ref";

	String origFileName = f.getName();
	int dotPos = origFileName.lastIndexOf( "." );
	String strippedFileName = origFileName.substring( 0, dotPos ); // filename without extension, and without the dot!

	return new File ( new String( harvesterDirName + System.getProperty("file.separator") + strippedFileName + refExtension ) );
    }


    /*
     *  \todo: I'm not sure this is the right location for this function
     */
    private File createDirectoryIfNotExisting( File currentPath, String dirName ) throws HarvesterIOException
    {
	File path = FileHandler.getFile( currentPath + System.getProperty("file.separator") + dirName );
	if ( !path.exists() )
	{
	    log.info( String.format( "Creating directory: %s", dirName ) );
	    // create path:
	    if ( !path.mkdir() )
	    {
		String errMsg = String.format( "Could not create necessary directory: %s", dirName );
		log.error( errMsg );
		throw new HarvesterIOException( errMsg );
	    }
	}
	
	return path;
    }

}
