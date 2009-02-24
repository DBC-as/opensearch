package dk.dbc.opensearch.common.pluginframework;


import dk.dbc.opensearch.common.types.CargoContainer;

import org.compass.core.CompassSession;


public interface IIndexer extends IPluggable
{
    long getProcessTime( CargoContainer cargo, CompassSession session );
}