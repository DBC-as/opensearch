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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;


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
    private String dir = "Harvest";
    private File path;
    private FilenameFilter[] filterArray;
    /**
     *
     */
    public FileHarvestLight() throws FileNotFoundException
    {
        filterArray = new FilenameFilter[] { new NoRefFileFilter() };

        path = FileHandler.getFile( dir );
        if( ! path.exists() )
        {
            String errMsg = String.format( "Harvest folder %s does not exist!", path );
            log.error( "FileHarvest: " + errMsg );
            throw new FileNotFoundException( errMsg );
        }

    }


    public void start()
    {
        //get the files in the dir
        FileVector = FileHandler.getFileList( dir , filterArray, false );
        iter = FileVector.iterator();

    }


    public void shutdown()
    {
    }


    public List< IJob > getJobs( int maxAmount )
    {
        Element root = null;
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

        ArrayList<IJob> list = new ArrayList();
        for( int i = 0; i < maxAmount && iter.hasNext() ; i++ )
        {
            fileName = (String)iter.next();
            refFileName = fileName.substring( 0, fileName.lastIndexOf( "." ) ) + ".ref";
            //System.out.println( String.format( "created ref name %s for file %s", refFileName, fileName ) );
            File refFile = FileHandler.getFile( refFileName );
            if( refFile.exists() )
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
                    root = XMLUtils.getDocumentElement( new InputSource( ISrefData ) );
                }
                catch( Exception e )
                {
                    log.error( e.getMessage() );
                }
                                
                doc.importNode( (Node)root, true );

                File theFile = FileHandler.getFile( fileName );

                list.add((IJob) new Job( new FileIdentifier( theFile.toURI() ), doc ) );
            }
            else
            {
                log.warn( String.format( "the file: %s has no .ref file", fileName ) );
                i--;
            }
        }
        return list;

    }

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

    public void setStatus( IIdentifier jobId, JobStatus status ) throws HarvesterUnknownIdentifierException, HarvesterInvalidStatusChangeException
    {
        FileIdentifier ID = (FileIdentifier)jobId;
        //System.out.println( String.format("the file %s was given status %s", ID.getURI().getRawPath() ,status) );
        log.info( String.format("the file %s was given status %s", ID.getURI().getRawPath() ,status) );
    }
}
