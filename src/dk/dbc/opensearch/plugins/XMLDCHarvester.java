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

/**
 * \file XMLDCHarvester.java
 * \brief creates cargoContainer from XML data, and add dc metadata
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
 * \Todo: There are hardcoded values in the constructor
 */


/**
 * The MarcxchangeHarvester plugin creates a {@link CargoContainer} with
 * DublinCore metadata from a marcxchange XML formatted inputdata using a javascript.
 * The plugin does no explicit validation on the incoming material, and only
 * tries to construct dublin core metadata elements from the input data. If
 * this fails, an empty metadata element will be added to the CargoContainer,
 * which will also contain the (incorrect) data given to the plugin.
 */
public class XMLDCHarvester implements IPluggable
{
    private static Logger log = Logger.getLogger( XMLDCHarvester.class );

    private PluginType pluginType = PluginType.HARVEST;


    public XMLDCHarvester( IObjectRepository repository ) throws PluginException
    {
    }

    @Override
    public CargoContainer runPlugin( IPluginEnvironment ienv, CargoContainer cargo ) throws PluginException
    {
	if ( !( ienv instanceof XMLDCHarvesterEnvironment) )
	{
	    String errMsg = String.format( "The given PluginEnvironment is of incorrect type. Expected: %s, got: %s", "XMLDCHarvesterEnvironment", ienv.getClass().getName() );
	    log.error( errMsg );
	    throw new PluginException( errMsg );
	}

	XMLDCHarvesterEnvironment env = (XMLDCHarvesterEnvironment)ienv;

	return env.myRun( cargo );

    }

    @Override
    public PluginType getPluginType()
    {
        return pluginType;
    }

    @Override
    public IPluginEnvironment createEnvironment( IObjectRepository repository, Map< String, String > args ) throws PluginException
    {
    	return new XMLDCHarvesterEnvironment( repository, args );
    }

}