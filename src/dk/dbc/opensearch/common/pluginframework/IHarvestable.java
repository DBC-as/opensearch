package dk.dbc.opensearch.common.pluginframework;

import java.io.FileNotFoundException;
import java.io.IOException;

import dk.dbc.opensearch.common.types.CargoContainer;

/**
 * Interface for all harvest plugins. Harvest plugins recieves a file
 * pointer and does the necessary work to put the data of the file
 * into a CargoContainer.
 * 
 */

public interface IHarvestable extends IPluggable
{
    /**
     * This method does the body of work and returns a CargoContainer
     * when finished. If the action fails, the methods throws.
     * 
     * \todo: please do consider if this exception is appropiate
     * @throws IOException 
     * @throws NullPointerException 
     * @throws IllegalArgumentException 
     * 
     * @throws FileNotFoundException if the file given to the plugin
     * cannot be found on the filesystem
     * @returns the input data as a cargocontainer 
     */
    CargoContainer getCargoContainer() throws FileNotFoundException, IllegalArgumentException, NullPointerException, IOException;  
}