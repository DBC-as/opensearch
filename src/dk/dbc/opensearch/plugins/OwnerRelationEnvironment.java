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

import dk.dbc.opensearch.common.config.FileSystemConfig;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.FedoraRelsExt;
import dk.dbc.opensearch.common.javascript.SimpleRhinoWrapper;
import dk.dbc.opensearch.common.metadata.DBCBIB;
import dk.dbc.opensearch.common.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.common.types.Pair;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

public class OwnerRelationEnvironment implements IPluginEnvironment
{
    
    private static Logger log = Logger.getLogger( OwnerRelationEnvironment.class );

    private SimpleRhinoWrapper jsWrapper = null;
    private IObjectRepository repository;

    OwnerRelationEnvironment( IObjectRepository repository, Map< String, String > args ) throws PluginException
    {

        this.repository = repository;

	String jsFileName = new String( "owner_relation.js" );

	// Creates a list of objects to be used in the js-scope
	List< Pair< String, Object > > objectList = new ArrayList< Pair< String, Object > >();
	objectList.add( new InputPair< String, Object >( "Log", log ) );
	objectList.add( new InputPair< String, Object >( "IS_OWNED_BY", DBCBIB.IS_OWNED_BY ) );
	objectList.add( new InputPair< String, Object >( "IS_AFFILIATED_WITH", DBCBIB.IS_AFFILIATED_WITH ) );

        try
        {
	    jsWrapper = new SimpleRhinoWrapper( FileSystemConfig.getScriptPath() + jsFileName, objectList );
        }
        catch( FileNotFoundException fnfe )
        {
            String errorMsg = String.format( "Could not find the file: %s", jsFileName );
            log.error( errorMsg, fnfe );
            throw new PluginException( errorMsg, fnfe );
        }
        catch( ConfigurationException ce )
        {
            String errorMsg = String.format( "A ConfigurationExcpetion was cought while trying to construct the path+filename for javascriptfile: %s", jsFileName );
            log.fatal( errorMsg, ce );
            throw new PluginException( errorMsg, ce );
        }

    }



    public CargoContainer setOwnerRelations( CargoContainer cargo ) throws PluginException
    {
        CargoObject co = null;
        if ( ! cargo.hasCargo( DataStreamType.OriginalData ) )
        {
            String error = String.format( "CargoContainer with id '%s' has no OriginalData to contruct relations from, aborting", cargo.getIdentifier() );
            log.error( error );
            throw new PluginException( new IllegalStateException( error ) );
        }
        else
        {
            co = cargo.getCargoObject( DataStreamType.OriginalData );
        }

        String submitter = co.getSubmitter();
        String format = co.getFormat();

        if ( null == submitter || submitter.isEmpty() || null == format || format.isEmpty() )
        {
            String error = String.format( "CargoContainer with id '%s' has no information on submitter or format", cargo.getIdentifier() );
            log.error( error );
            throw new PluginException( new IllegalStateException( error ) );
        }

        FedoraRelsExt rels = (FedoraRelsExt) cargo.getMetaData( DataStreamType.RelsExtData );
        if ( null == cargo.getIdentifier() )
        {
            log.warn( String.format( "CargoContainer has no identifier, this will be a problem in the RELS-EXT generation/validation" ) );
        }

        if ( null == rels )
        {
            try
            {
                rels = new FedoraRelsExt();
            }
            catch ( ParserConfigurationException pce )
            {
                String errorMsg = new String( "Could not create a new FedoraRelsExt.");
                log.error( errorMsg, pce );
                throw new PluginException( errorMsg, pce );
            }
        }


        log.debug( String.format( "Trying to add owner relation for rels '%s'; submitter '%s'; format '%s'", rels.toString(), submitter, format ) );

	String entryPointFunc = "addOwnerRelation";
	rels = ( FedoraRelsExt ) jsWrapper.run( entryPointFunc,
						rels,
						submitter,
						format );        

        log.debug( String.format( "rels: '%s'", rels.toString() ) );

        cargo.addMetaData( rels );

        return cargo;

    }



}
