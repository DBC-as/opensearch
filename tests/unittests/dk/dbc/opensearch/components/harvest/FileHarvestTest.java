/*
  This file is part of opensearch.
  Copyright © 2009, Dansk Bibliotekscenter a/s,
  Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043

  opensearch is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  opensearch is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * \file FileHarvestTest.java
 * \brief The FileHarvestTest class
 * \package tests;
 */


package dk.dbc.opensearch.components.harvest;


import dk.dbc.opensearch.common.config.DatadockConfig;
import dk.dbc.opensearch.common.config.HarvesterConfig;
import dk.dbc.opensearch.common.types.IJob;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.xml.XMLUtils;

import java.io.File;
import java.io.IOException;

import java.util.List;
import javax.xml.stream.*;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockClass;
import mockit.Mocked;
import static mockit.Mockit.setUpMocks;
import static mockit.Mockit.tearDownMocks;

import org.junit.*;
import static org.junit.Assert.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/** 
 * bug 9383, testmethods are being ignored
 */
public class FileHarvestTest 
{

    @Mocked static NodeList mockNodeList;
    @Mocked static File mockFile;

    Element mockElement;

    @MockClass( realClass = XMLUtils.class )
    public static class MockXMLUtils
    {
        @Mock public static NodeList getNodeList( File xmlFile, String tagName )
        {
            return mockNodeList;
        }
    }

    @MockClass( realClass=DatadockConfig.class )
    public static class MockDatadockConfig
    {
        public static String getPath()
        {
            return "path";
        }
    }

    @MockClass( realClass = FileHandler.class )
    public static class MockFileHandler
    {
        @Mock public static File getFile( String path )
        {
            return mockFile;
        }
    }

    @MockClass( realClass = HarvesterConfig.class )
    public static class MockHC
    {
        @Mock public static String getFolder()
        {
            String retval = null;
            try
            {
                retval = File.createTempFile( "temp", "dir" ).getAbsolutePath();
            }
            catch( IOException ex )
            {
                System.out.println( String.format( "Oops: %s", ex.getMessage() ) );
            }
            return retval;
        }

        @Mock public static String getDoneFolder()
        {
            return new File( "done" ).getAbsolutePath();
        }

        @Mock public static String getProgressFolder()
        {
            return new File( "progress" ).getAbsolutePath();
        }

        @Mock public static String getFailureFolder()
        {
            return new File( "failure" ).getAbsolutePath();
        }

        @Mock public static int getMaxToHarvest()
        {
            return 100;
        }
    }

//
//    //has a getMaxToHarvest method that is needed only in 1 test case
//    @MockClass( realClass = HarvesterConfig.class )
//    public static class MockHC2
//    {
//        @Mock public static String getFolder()
//        {
//            return harvestdir.getAbsolutePath();
//        }
//
//        @Mock public static String getDoneFolder()
//        {
//            return destDir.getAbsolutePath();
//        }
//
//        @Mock public static String getProgressFolder()
//        {
//            return progressDir.getAbsolutePath();
//        }
//
//        @Mock public static String getFailureFolder()
//        {
//            return failureDir.getAbsolutePath();
//        }
//
//        @Mock public static int getMaxToHarvest()
//        {
//            return 2;
//        }
//
//    }


    XMLOutputFactory factory;
    XMLStreamWriter writer;

    @Before public void SetUp() throws Exception
    {
        factory = XMLOutputFactory.newInstance();
        setUpMocks( MockDatadockConfig.class );
    }


    @After public void TearDown()
    {
        tearDownMocks();
    }


    /**
     * Test a happy path where the FileHarvest is initialized, started and asked for jobs
     */
    @Ignore
    @Test
    public void testHappyRunPath() throws Exception
    {
        new Expectations()
        {
            {
                new File( "done" ).exists();
                returns( Boolean.TRUE );
            }
        };
        IHarvest fileHarvest = new FileHarvest();
        fileHarvest.start();

        List<IJob> result1 = fileHarvest.getJobs( 1 );

        assertTrue( result1.size() == 1 );

        List<IJob> result2 = fileHarvest.getJobs( 1 );

        assertTrue( result2.size() == 0 );

        fileHarvest.shutdown();
    }


    /**
     * This test gives the same submitter format pair twice to the initVectors
     * method. Only the first should be put into the submittersFormatsVector.
     * Can only verify the behaviour in the coverage report. The else case of
     * the test only results in a warning in the log.
     * It also tests the iniVectors methods treatment of non directory files in the
     * harvest directory, i.e. files that shouldnt be there. This case is ignored so
     * no way to verify except for the coverage report
     * Tests the case where the file system has a dir under a submitter dir, that is
     * not present in the in the submittersFormatVector build on basis of the
     * datadock_jobs file. So the submitter, format pair is not in the vector
     */
    @Ignore
    @Test
    public void testIfClausesWithoutElseStatement() throws Exception
    {
        IHarvest fileHarvest = new FileHarvest();
        fileHarvest.start();
        fileHarvest.shutdown();
    }


    /**
     * Test the case of the getNewJobs method when there are more files in the folder
     * than the max config value specifies
     * We verify this by having 3 files that should be harvested, but only get 1
     * because thats the max to harvest at a time
     * This est is invalid since we tell the harvester how many files we want through the  
     * argument maxAmount in the getJobs method
     */
    @Ignore
    @Test
    public void testGetNewJobsMax() throws Exception
    {
        IHarvest fileHarvest = new FileHarvest();
        fileHarvest.start();
        //System.out.println( "calling getjobs 1" );
        List<IJob> result1 = fileHarvest.getJobs( 2 );
        assertTrue( result1.size() == 2 );
        //System.out.println( "calling getjobs 2" );
        result1 = fileHarvest.getJobs( 30 );
        assertTrue( result1.size() == 1 );
        //System.out.println( "calling getjobs 3" );
        result1 = fileHarvest.getJobs( 30 );
        assertTrue( result1.size() == 0 );
      
        fileHarvest.shutdown();
    }


    /**
     * Test that files in directories other than those specified through the
     * datadock_jobs file are not harvested.
     */
    @Ignore
    @Test
    public void testcheckSubmitterFormat() throws Exception
    {
        IHarvest fileHarvest = new FileHarvest();
        fileHarvest.start();

        fileHarvest.shutdown();
    }


    /**
     * tests the situation where the move method encounters problems with the
     * creation of the doneHarvestDir
     */
    @Ignore
    @Test
    public void testMoveMethodIOExceptionNoDoneHarvestDir() throws Exception
    {
        IHarvest fileHarvest = new FileHarvest();
        fileHarvest.start();

        List<IJob> result1 = fileHarvest.getJobs( 100 );

        fileHarvest.shutdown();
    }


    /**
     * Tests the move methods throwing of an IOException when not able to move 
     * a file to the destFldr.
     */
    @Ignore
    @Test( expected = IOException.class )
    public void testMoveNotAbleToRename() throws Exception
    {
        IHarvest fileHarvest = new FileHarvest();
        fileHarvest.start();

        List<IJob> result1 = fileHarvest.getJobs( 30 );

        fileHarvest.shutdown();
    }
}
