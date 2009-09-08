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
 * \file
 * \brief
 */


package dk.dbc.opensearch.components.harvest;


import java.util.ArrayList;
import java.util.Iterator;


/**
 *
 */
public class HarvestFunc
{
    static ESHarvest esh;

    private static int counter = 0;

    public static void main( String[] args )
    {
        runTests();
    }


    static void runTests()
    {
        try
        {
            startESHarvestTest();
            getJobsNDataTest();
        }
        catch ( HarvesterIOException hioe )
        {
            System.out.println( "An internal Harvester error occured" );
            hioe.printStackTrace();
        }
    }


    private static void startESHarvestTest() throws HarvesterIOException
    {
        esh = new ESHarvest();
        esh.start();
    }


    private static void getJobsNDataTest() throws HarvesterIOException
    {
        byte[] data = null;
        //esh = new ESHarvest();
        //esh.start();
        //startESHarvestTest();

        ArrayList<IJob> jobL = esh.getJobs( 2 );
        System.out.println( String.format( " the joblist contained %s jobs", jobL.size() ) );
        Iterator iter = jobL.iterator();
        System.out.println( "got jobs:" );
        while( iter.hasNext() )
        {
            System.out.println("");
            IJob theJob = (IJob)iter.next();
            System.out.println( String.format( "job: %s", theJob.toString() ) );
            try
            {
                data = esh.getData( theJob.getIdentifier() );

                System.out.println(  String.format( "data gotten: %s", data.toString() ) );
            }
            catch( HarvesterUnknownIdentifierException huie )
            {
                huie.printStackTrace();
            }
            setStatusTest( theJob.getIdentifier() );
        }
    }
    
    /**
     * testing the updating of jobs stetting the status to SUCCESS and then to FAILURE
     */
    private static void setStatusTest( IIdentifier id ) throws HarvesterIOException
    {
        try
        {
            if ( counter % 2 == 0 )
            {
                esh.setStatus( id, JobStatus.SUCCESS );
            }
            else
            {
                esh.setStatus( id, JobStatus.FAILURE );
            }

            ++counter;
        }
        catch( HarvesterUnknownIdentifierException huie )
        {
            huie.printStackTrace();
        }
        catch( HarvesterInvalidStatusChangeException hisce )
        {
            System.out.println(hisce.getMessage() );
            hisce.printStackTrace();
        }
    }
}
