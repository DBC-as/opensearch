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
 * \file
 * \brief
 */


package dk.dbc.opensearch.plugins;

import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.PID;
import dk.dbc.opensearch.common.helpers.OpensearchNamespaceContext;
import dk.dbc.opensearch.common.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;

import java.io.ByteArrayInputStream;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;


public class ForceFedoraPid implements IPluggable
{
    static Logger log = Logger.getLogger( ForceFedoraPid.class );
    private IObjectRepository repository;

    private ForceFedoraPidEnvironment env = null;


    public ForceFedoraPid( IObjectRepository repository ) throws PluginException
    {
	Map< String, String > tmpMap = new HashMap< String, String >();
	env = (ForceFedoraPidEnvironment)this.createEnvironment( repository, tmpMap );
    }


    @Override
    public PluginType getPluginType()
    {
        return PluginType.ANNOTATE;
    }


    @Override
    public CargoContainer runPlugin( CargoContainer cargo, Map<String, String> argsMap ) throws PluginException
    {
        return env.run( cargo ) ;
    }




    public static IPluginEnvironment createEnvironment( IObjectRepository repository, Map< String, String > args ) throws PluginException
    {
    	return new ForceFedoraPidEnvironment( repository, args );
    }


}
