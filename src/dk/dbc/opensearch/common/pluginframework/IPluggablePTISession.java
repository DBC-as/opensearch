package dk.dbc.opensearch.common.pluginframework;


import dk.dbc.opensearch.common.types.CargoContainer;

import org.compass.core.CompassSession;


public interface IPluggablePTISession extends IPluggable
{
    void init( CargoContainer cargo, CompassSession session );
    
    float getEstimate();
}