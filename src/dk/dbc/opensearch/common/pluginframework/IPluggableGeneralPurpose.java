package dk.dbc.opensearch.common.pluginframework;


import dk.dbc.opensearch.common.fedora.FedoraHandler;
import dk.dbc.opensearch.common.types.CargoContainer;

import org.compass.core.CompassSession;


public interface IPluggableGeneralPurpose extends IPluggable
{
    void init( CargoContainer cargo );
}