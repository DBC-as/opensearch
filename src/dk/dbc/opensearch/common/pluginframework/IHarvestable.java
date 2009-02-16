package dk.dbc.opensearch.common.pluginframework;


import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DatadockJob;

import java.io.IOException;

/**
 * @author mro
 *
 */
public interface IHarvestable extends IPluggable
{
	/*
	 * Initializes the plugin with a dk.dbc.opensearch.common.types.DatadockJob job description
	 */
	void init( DatadockJob job );


	/**
	 * @return the CargoContainer that results from the plugin activity 
	 * @throws IOException if the URI provided by the DatadockJob from the init call could not be read 
	 */
	public CargoContainer getCargoContainer() throws IOException;
}