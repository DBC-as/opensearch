package dk.dbc.opensearch.common.pluginframework;


import dk.dbc.opensearch.common.types.CargoContainer;

import java.io.IOException;


public interface IAnnotate extends IPluggable
{
    void init( CargoContainer cargo );
    
    CargoContainer getCargoContainer() throws IOException;
}