package dk.dbc.opensearch.common.pluginframework;


import dk.dbc.opensearch.common.fedora.FedoraHandler;


public interface IPluggablePTI extends IPluggable
{
    void init( String str, FedoraHandler fh );
}