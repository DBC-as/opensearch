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

import java.util.ArrayList;

/**
 * Interface that defines the operations of a data-harvester The
 * development goals of the harvester application is to make it a
 * service. Implementations should consider this and aim at modelling
 * towards this.
 */

public interface IHarvest
{
     /**
     * The start method. This method initiates the service after an
     * instance has been constructed.
     */
    void start();
    
    
    /**
     * The shutdown method. Should be used to shutdown the harvester
     * completely and not after each request.
     */
    void shutdown();

    /**
     * This method delivers information about which jobs the requestor
     * can recieve. A {@link IJobList} should contain
     * information on how the requestor can or must obtain data from
     * the harvester.
     * 
     * @param maxAmount specifies the maximum amount of jobs to be written to the {@link IJobList}
     * @returns an {@link IjobList} containing information about jobs that the requestor can obtain.
     */
    ArrayList<IJob> getJobs( int maxAmount );


    /**
     * Given an {@link IIdentifier} the requestor can obtain the data
     * associated with the {@code jobId}. {@code jobId} is usually
     * obtained from a {@link IJobList}, which in turn can be obtained
     * from {@link #getJobs(int)}.
     * 
     * @param jobId an {@link IIdentifier} that uniquely identifies a job with in the {@link IHarvester}
     * @returns a byte[] containing the data identified by the {@code jobId}
     * 
     * @throws UnknownIdentifierException if the {@link IIdentifier} is not known to the {@link IHarvester}. I.e. if the jobId can not be found
     */
    byte[] getData( IIdentifier jobId ) throws UnknownIdentifierException;


    /**
     * This method lets the requestor/client set the status of a job
     * identified by {@code jobId}. A status can only be set once for
     * a given job; a {@link Job} that has not had its status set,
     * will be unset. Trying to set a status more than once will
     * result in an error condition (signalled by an 
     * {@link InvalidStatusChangeException}).
     * 
     * @see JobStatus for more information on the states of jobs in the {@link IHarvester}
     * 
     * @param jobId an {@link IIdentifier} that uniquely identifies a job with in the {@link IHarvester}
     * @param status a {@link JobStatus} value indicating the client status of the job.
     * 
     * @throws UnknownIdentifierException if the {@code jobId} could not be found in the {@link IHarvester}
     * @throws InvalidStatusChangeException if the client tries to set the status more than once on a given {@code jobId}
     */
    void setStatus( IIdentifier jobId, JobStatus status ) throws UnknownIdentifierException, InvalidStatusChangeException;
}
