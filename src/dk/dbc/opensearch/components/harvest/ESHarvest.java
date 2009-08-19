/*
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

package dk.dbc.opensearch.components.harvest;

/**
 *
 */
public class ESHarvest implements IHarvest
{
    /**
     *
     */
    public ESHarvest() 
    {

    }
    public void start()
    {
        //create the DBconnection
System.out.println( "Dummy harvester started..." );
    }

    public void shutdown()
    {
        //close the DBconnection
System.out.println( "dummy hearvester shutdown" );
    }

    public IJobList getJobs( int maxAmount )
    {

        System.out.println( String.format( "The dummy harvester was requested for %s jobs", maxAmount ) );
        //Ask for suppliedrecords with recordstatus 2
        //set the recordstatus to 3, any retries to queued
        //get the referenceData xml and identifier
        //put them into an IJoblist 

        return null;
    }

    public byte[] getData( IIdentifier jobId ) throws UnknownIdentifierException
    {
        System.out.println( String.format( "identifier %s called on the Dummy harvester", jobId ) );
        //get the data associated with the identifier from the record field
        return null;
    }

    public void setStatus( IIdentifier jobId, int status ) throws UnknownIdentifierException, InvalidStatusChangeException
    {
        System.out.println( String.format( "Dummy harvester was requested to set status %s on data identified by the identifier %s", status, jobId ) );
        //check if the status associated with the identifier has previously been set
        //if not set it to what the parameter says
        //if success -> xxxx: invalid
        //if failure -> success: ok
        //if failure -> failure: invalid
    }

}