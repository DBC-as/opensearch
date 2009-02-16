package dk.dbc.opensearch.common.pluginframework;


import dk.dbc.opensearch.common.types.CargoContainer;

import java.io.IOException;
import java.net.URI;


public interface IHarvestable extends IPluggable
{
	public void init( URI uri, String submitter, String format );
    
    public CargoContainer getCargoContainer() throws IOException;
}