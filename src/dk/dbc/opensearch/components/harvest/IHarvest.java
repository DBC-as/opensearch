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

package dk.dbc.opensearch.components.harvest;

public interface IHarvest
{
     /**
     * The start method. Called by the datadock just after
     * construction of the instance.
     */
    void start();
    
    
    /**
     * The shutdown method. Called by the datadock when closing down
     * the harvester.
     */
    void shutdown();

    /**
     * The getJobs method. Must be called to get information about which 
     * jobs the requester can work with
     */

    IJobList getJobs( int maxAmount );

    /**
     * Method for getting the data associated with a job
     */


    byte[] getData( IIdentifier jobId ) throws UnknownIdentifierException;

    /**
     * method for telling the harvester how the treatment of the job went.
     * The options are: 0 = failure, 1 = success, 2 = retry.
     * Retry means that the job will get into the pool of jobs the requester can work with 
     */

    void setStatus( IIdentifier jobId, int status ) throws UnknownIdentifierException, InvalidStatusChangeException;

}
