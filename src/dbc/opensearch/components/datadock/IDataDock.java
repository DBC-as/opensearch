/** \file */
package dbc.opensearch.components.datadock.interfaces;

import dbc.opensearch.components.datadock.*;

/**
 * \brief The public interface for the OpenSearch DataDockService
 * DataDock is the primary accesspoint for the delivery of material to
 * be processed by lucene and saved in the Fedora repository.  The
 * DataDock interface allows clients to submit data that represents a
 * textual material to be stored in a Fedora repository and indexed by
 * Lucene. When submitted, the data is validated against a dictionary
 * of possible handlers using the supplied metadata. All methods throw
 * exceptions on errors.  
 * \todo a schema for errors returned should be defined
 * 
 */

public interface IDataDock {
    
    /**
     * This method recieves the data as well as metadata and is
     * responsible for validating that the correct information is
     * present and to either return an error message in form of an
     * exception or an integer representing an unique identifier
     * @param data The metadata and the data is collected in a
     * CargoDescription container \see CargoDescription
     * @returns an unique identifier for future references
     * @throws An error message in the form of a java exception
     * specifying the excact source of the rejection of the submitted
     * data
     */
    public int Submit( CargoContainer data );
}
