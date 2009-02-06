/**
 * \file Harvester.java
 * \brief The Harvester class
 * \package harvest;
 */

package dk.dbc.opensearch.components.harvest;

/**
 * Harvester interface. Harvester is the interface for the datadock
 * harvester service. The harvester is an eventdriven component - and
 * three methods need to be implemented. After construction of the
 * harvester - the start method is called - so all startup logic
 * should be placed here. When the datadock is up and running, it will
 * call the getJobs method at intervals until the shutdown method is
 * called.
 */

import dk.dbc.opensearch.common.types.DatadockJob;
import java.util.Vector;
import java.net.URI;

public interface Harvester{
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
     * The getJobs method. Called consecutively by the datadock when
     * it is up and running.
     * 
     * @return getJobs Returns a vector of DatadockJobs - representing
     * the new jobs registered since the last call to this method.
     */
    Vector<DatadockJob> getJobs();
}
