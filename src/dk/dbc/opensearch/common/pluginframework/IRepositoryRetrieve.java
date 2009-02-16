package dk.dbc.opensearch.common.pluginframework;


import dk.dbc.opensearch.common.types.CargoContainer;


public interface IRepositoryRetrieve extends IPluggable
{
    void init( String fedoraHandler );
    
    CargoContainer getCargoContainer();
}