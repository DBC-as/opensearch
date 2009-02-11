package dk.dbc.opensearch.common.pluginframework;


import java.io.IOException;

import dk.dbc.opensearch.common.types.CargoContainer;


public interface IPluggableGeneralPurpose extends IPluggable
{
    void init( CargoContainer cargo );
    
    CargoContainer getCargoContainer() throws IOException;
}