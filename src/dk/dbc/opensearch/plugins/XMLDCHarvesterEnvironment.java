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
import dk.dbc.opensearch.common.javascript.E4XXMLHeaderStripper;
import dk.dbc.opensearch.common.javascript.SimpleRhinoWrapper;
import dk.dbc.opensearch.common.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.common.pluginframework.PluginEnvironmentUtils;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import org.apache.log4j.Logger;


public class XMLDCHarvesterEnvironment implements IPluginEnvironment
{

    private static Logger log = Logger.getLogger( XMLDCHarvesterEnvironment.class );

    private SimpleRhinoWrapper jsWrapper = null;

    private static final String javascriptStr = "javascript";
    private static final String entryFuncStr  = "entryfunction";
    private final String entryFunc;

    public XMLDCHarvesterEnvironment( IObjectRepository repository, Map<String, String> args ) throws PluginException
    {
        log.trace( "Constructor called" );

        List<Pair<String, Object>> objectList = new ArrayList<Pair<String, Object>>();
        objectList.add( new Pair<String, Object>( "Log", log ) );

        this.validateArguments( args, objectList ); // throws PluginException in case of trouble!

        this.jsWrapper = PluginEnvironmentUtils.initializeWrapper( args.get( XMLDCHarvesterEnvironment.javascriptStr ), objectList );
        this.entryFunc = args.get( XMLDCHarvesterEnvironment.entryFuncStr );
    }

    /**
     * Main method that constructs dc data and adds it to the cargocontainer 
     * The dc is created from data in the originaldata.
     * The dc:identifier is taken directly from the cargocontainers identifier, 
     * so this method is dependant on that such a identifier previously have 
     * been added to the cargocontainer
     * @param cargo a CargoContainer with the originaldata
     * @return a cargocontainer with a dc stream added.
     */

    public CargoContainer myRun( CargoContainer cargo ) throws PluginException
    {
        CargoObject co = cargo.getCargoObject( DataStreamType.OriginalData );
        String xml = new String( E4XXMLHeaderStripper.strip( co.getBytes() ) ); // stripping: <?xml...?>
        
        String replacedXml = xml.replace( "\n", "");
        //log.debug( String.format( "the xml string of the original data: '%s'", replacedXml )  );

        String dcXmlString = (String)jsWrapper.run( entryFunc, replacedXml );

        log.debug( " The dc string: " + dcXmlString );
        try
        {
            cargo.add( DataStreamType.DublinCoreData,
                       "dublinCoreData",
                       "dbc",
                       "da",
                       "text/xml",
                       dcXmlString.getBytes() );
        }
        catch( IOException ioe )
        {
            String error = String.format( "could not add dublincore data: '%s'to the cargocontainer with id: '%s'", dcXmlString, cargo.getIdentifier().getIdentifier() );
            log.error( error, ioe );
            throw new PluginException( error, ioe );
        }

        return cargo;

    }


    /**
     * This function will validate the following argumentnames: "javascript" and "entryfunction".
     * All other argumentnames will be silently ignored.
     * Currently the "entryfunction" is not tested for validity.
     * 
     * @param Map< String, String > the argumentmap containing argumentnames as keys and arguments as values
     * @param List< Pair< String, Object > > A list of objects used to initialize the RhinoWrapper.
     *
     * @throws PluginException if an argumentname is not found in the argumentmap or if one of the arguments cannot be used to instantiate the pluginenvironment.
     */
    private void validateArguments( Map< String, String > args, List< Pair< String, Object > > objectList ) throws PluginException
    {
        log.info( "Validating Arguments - Begin" );

        // Validating existence of mandatory entrys:
        if( !PluginEnvironmentUtils.validateMandatoryArgumentName( javascriptStr, args ) )
        {
            throw new PluginException( String.format( "Could not find argument: %s", javascriptStr ) );
        }
        if( !PluginEnvironmentUtils.validateMandatoryArgumentName( entryFuncStr, args ) )
        {
            throw new PluginException( String.format( "Could not find argument: %s", entryFuncStr ) );
        }

        // Validating that javascript can be used in the SimpleRhinoWrapper:
        SimpleRhinoWrapper tmpWrapper = PluginEnvironmentUtils.initializeWrapper( args.get( javascriptStr ), objectList );

        // Validating function entries:
        if( !tmpWrapper.validateJavascriptFunction( args.get( XMLDCHarvesterEnvironment.entryFuncStr ) ) )
        {
            throw new PluginException( String.format( "Could not use %s as function in javascript", args.get( XMLDCHarvesterEnvironment.entryFuncStr ) ) );
        }

        log.info( "Validating Arguments - End" );
    }
}
