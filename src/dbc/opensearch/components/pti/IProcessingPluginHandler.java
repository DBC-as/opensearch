/**
 * \file IProcessingPluginHandler.java
 * \brief The PTI plugin interface
 * \package pti.interfaces
 */

package dbc.opensearch.components.pti.interfaces;

import dbc.opensearch.components.datadock.*;

/**
 * \ingroup pti
 * Interface for the PluginHandler 
 */
public interface IProcessingPluginHandler {

    /**
     * Register registers a Handler given the Handlers name and a
     * mimetype. The mimetype must be registered in the CargoMimetypes
     * and the HandlerName is an arbitrary string used for referencing
     * the handler.
     * \todo: need to find suitable exception type
     */
    void Register( CargoMimeType mimetype, String handlerName) throws Exception;

    /**
     * Queries the register for a handler based on mimetype.
     * @returns the name of the handler
     */
    String HasHandler( CargoMimeType mimetype );

    /**
     * Queries the register for a handler based on the handler name
     * @returns the mimetype for which the handler exists
     */
    CargoMimeType HasHandler( String handlerName );

    /**
     * Does the actual processing of the data. The metadata is checked
     * for suitable mimetypes for processing the data; an exception is
     * thrown if a handler cannot be found.
     */
    int ProcessCargo( CargoContainer data, CargoObjectInfo metadata );

    /**
     * Does the actual processing of the data. The metadata is checked
     * for suitable mimetypes for processing the data; an exception is
     * thrown if a handler cannot be found. This method accepts a
     * handlername and tries to process the data using the specified
     * handler. If the handler does not fit the metadata, an exception
     * is thrown and if the handler cannot be found, an exception is
     * thrown.
     * \todo: need to find suitable exception type
     */
    int ProcessCargo( CargoContainer data, CargoObjectInfo metadata, String HandlerName ) throws Exception;
}