/*   
This file is part of opensearch.
Copyright © 2009, Dansk Bibliotekscenter a/s, 
Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043

opensearch is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

opensearch is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
 */


package dk.dbc.opensearch.plugins;

import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Plugin for annotating docbook data from {@link CargoContainer}
 */
public class DocbookAnnotate implements IPluggable
{

    static Logger log = Logger.getLogger( DocbookAnnotate.class );
    private PluginType pluginType = PluginType.ANNOTATE;

    // DocbookAnnotateEnvironment env = null;

    /**
     * Constructor for the DocbookAnnotate plugin.
     */
    public DocbookAnnotate( IObjectRepository repository ) throws PluginException
    {
        // log.trace( "DocbookAnnotate Constructor() called" );
	// Map< String, String > tmpMap = new HashMap< String, String >();
        // env = (DocbookAnnotateEnvironment)this.createEnvironment( repository, tmpMap );
    }


    /**
     * The "main" method of this plugin. Request annotation data from
     * a webservice. If annotationdata is available it added to the
     * cargocontainer in a new stream typed DublinCoreData
     *
     * @param cargo The CargoContainer to annotate
     *
     * @return An annotated CargoContainer
     * 
     * @throws PluginException thrown if anything goes wrong during annotation.
     */
    @Override
    public CargoContainer runPlugin( IPluginEnvironment ienv, CargoContainer cargo ) throws PluginException
    {
        log.trace( "DocbookAnnotate runPlugin() called" );

	if ( !( ienv instanceof DocbookAnnotateEnvironment) )
	{
	    String errMsg = String.format( "The given PluginEnvironment is of incorrect type. Expected: %s, got: %s", "DocbookAnnotateEnvironment", ienv.getClass().getName() );
	    log.error( errMsg );
	    throw new PluginException( errMsg );
	}

	DocbookAnnotateEnvironment env = (DocbookAnnotateEnvironment)ienv;

        if( cargo == null )
        {
            log.error( "DocbookAnnotate getCargoContainer cargo is null" );
            throw new PluginException( new IllegalStateException( "CargoContainer was null. Cannot operate without a CargoContainer instance" ) );
        }

	return env.run( cargo );

    }



    @Override
    public PluginType getPluginType()
    {
        return pluginType;
    }

    @Override
    public IPluginEnvironment createEnvironment( IObjectRepository repository, Map< String, String > args ) throws PluginException
    {
    	return new DocbookAnnotateEnvironment( repository, args );
    }

}
