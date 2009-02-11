package dk.dbc.opensearch.common.pluginframework;


import java.net.URI;


public interface IPluggableDD
{
    void init( URI uri, String submitter, String format );
}