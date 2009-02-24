package dk.dbc.opensearch.common.pluginframework;

import dk.dbc.opensearch.common.types.CargoContainer;

public interface IProcesser extends IPluggable
{
    CargoContainer getCargoContainer( CargoContainer cargo );
}