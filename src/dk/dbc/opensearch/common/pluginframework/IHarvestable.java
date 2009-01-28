package dk.dbc.opensearch.common.pluginframework;

import java.io.FileNotFoundException;

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
     * Convenience method that returns the submitter (owner) of the submitted
     * material.
     * 
     * @returns submitter of the submitted material
     */
    String getSubmitter();

    
    /**
     * 
     */
    void setSubmitter();
    
    
    /**
     * Convenience method that returns the format of the submitted
     * material.
     *
     * @returns format of the submitted material
     */
    String getFormat();

    
    /**
     * 
     */
    void setFormat();
    

    /**
     * This method does the body of work and returns a CargoContainer
     * when finished. If the action fails, the methods throws.
     * 
     * \todo: please do consider if this exception is appropiate
     * 
     * @throws NoSuchFieldException 
     * @returns the input data as a cargocontainer 
     */
    CargoContainer getCargoContainer() throws FileNotFoundException;  
}