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
import dk.dbc.opensearch.common.fedora.FedoraObjectFields;
import dk.dbc.opensearch.common.fedora.FedoraObjectFieldsValue;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.common.fedora.PID;
import dk.dbc.opensearch.common.metadata.DBCBIB;
import dk.dbc.opensearch.common.metadata.DublinCore;
import dk.dbc.opensearch.common.metadata.DublinCoreElement;
import dk.dbc.opensearch.common.pluginframework.IRelation;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.IndexingAlias;
import dk.dbc.opensearch.common.types.ObjectIdentifier;

import dk.dbc.opensearch.common.types.InputPair;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * Plugin for annotating docbook carcoContainers
 */
public class MarcxchangeWorkRelation_1 implements IRelation
{
    private static Logger log = Logger.getLogger( MarcxchangeWorkRelation_1.class );

    private PluginType pluginType = PluginType.RELATION;

    private Vector<String> types;    
    private IObjectRepository objectRepository;
    private String workNamespace = "work:";

    private final ScriptEngineManager manager = new ScriptEngineManager();    
    
    /**
     * Constructor for the MarcxchangeWorlkRelation plugin.
     */
    public MarcxchangeWorkRelation_1() throws PluginException
    {
        log.debug( "MarcxchangeWorkRelation constructor called" );
    
        types = new Vector<String>();
        types.add( "Anmeldelse" );
        types.add( "Artikel" );
        types.add( "Avis" );
        types.add( "Avisartikel" );
        types.add( "Tidsskrift" );
        types.add( "Tidsskriftsartikel" );        
    }


    /**
     * The "main" method of this plugin. Request a relation from
     * a webservice. If a relation is available it is added to the
     * cargocontainer in a new stream typed RelsExtData
     *
     * @param CargoContainer The CargoContainer to add relations to
     *
     * @returns A CargoContainer containing relations
     * 
     * @throws PluginException thrown if anything goes wrong during annotation.
     */
    @Override
    public CargoContainer getCargoContainer( CargoContainer cargo ) throws PluginException
    {
        log.trace( "getCargoContainer() called" );

        if ( cargo == null )
        {
            log.error( "MarcxchangeWorkRelation getCargoContainer cargo is null" );
            throw new PluginException( new NullPointerException( "MarcxchangeWorkRelation getCargoContainer throws NullPointerException" ) );
        }

        boolean ok = false;
        try
        {
            ok = addWorkRelationForMaterial( cargo );
        }
        catch( ObjectRepositoryException ex )
        {
            String error = String.format( "Failed to add work relation for %s: %s", cargo.getIdentifier(), ex.getMessage() );
            log.error( error , ex);
            throw new PluginException( error, ex );
        }
        catch( ConfigurationException ex )
        {
            String error = String.format( "Failed to add work relation for %s: %s", cargo.getIdentifier(), ex.getMessage() );
            log.error( error );
            throw new PluginException( error, ex );
        }
        catch( MalformedURLException ex )
        {
            String error = String.format( "Failed to add work relation for %s: %s", cargo.getIdentifier(), ex.getMessage() );
            log.error( error );
            throw new PluginException( error, ex );
        }
        catch( IOException ex )
        {
            String error = String.format( "Failed to add work relation for %s: %s", cargo.getIdentifier(), ex.getMessage() );
            log.error( error );
            throw new PluginException( error, ex );
        }
        catch( ServiceException ex )
        {
            String error = String.format( "Failed to add work relation for %s: %s", cargo.getIdentifier(), ex.getMessage() );
            log.error( error );
            throw new PluginException( error, ex );
        }

        if ( ! ok )
        {
            log.error( String.format( "could not add work relation on pid %s", cargo.getIdentifier() ) );
        }

        return cargo;
    }


    public static String normalizeString(String s)
    {
        s = s.toLowerCase();
        String killchars = "~'-";
        
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < s.length(); ++i)
        {
            if (-1 == killchars.indexOf(s.charAt(i)))
            {
                res.append(s.charAt(i));
            }
        }

        return res.toString();      
    }


    synchronized private boolean addWorkRelationForMaterial( CargoContainer cargo ) throws PluginException, ObjectRepositoryException, ConfigurationException, MalformedURLException, IOException, ServiceException
    {
        DublinCore dc = cargo.getDublinCoreMetaData();

        if( dc == null )
        {
            String error = String.format( "CargoContainer with identifier %s contains no DublinCore data", cargo.getIdentifier() );
            log.error( error );
            throw new PluginException( error );
        }

        FedoraObjectFieldsValue dcTitle = new FedoraObjectFieldsValue( normalizeString( dc.getDCValue( DublinCoreElement.ELEMENT_TITLE ) ) );
        FedoraObjectFieldsValue dcType = new FedoraObjectFieldsValue( normalizeString( dc.getDCValue( DublinCoreElement.ELEMENT_TYPE ) ) );
        FedoraObjectFieldsValue dcCreator = new FedoraObjectFieldsValue( normalizeString( dc.getDCValue( DublinCoreElement.ELEMENT_CREATOR ) ) );
        FedoraObjectFieldsValue dcSource = new FedoraObjectFieldsValue( normalizeString( dc.getDCValue( DublinCoreElement.ELEMENT_SOURCE ) ) );
        String pidAsString = cargo.getIdentifier().getIdentifier();
        
        List< String > fedoraPids = new ArrayList< String >();        
        List< InputPair< FedoraObjectFields, FedoraObjectFieldsValue > > resultSearchFields = new ArrayList< InputPair< FedoraObjectFields, FedoraObjectFieldsValue > >();
        int maximumResults = 10000;

        if( ! types.contains( dcType.valuename() ) )
        {
            log.debug( String.format( "finding work relations for dcType %s", dcType.valuename() ) );
            if ( ! dcSource.valuename().equals( "" ) )
            {
                log.debug( String.format( "1 WR with dcSource '%s' and dcTitle '%s'", dcSource.valuename(), dcTitle.valuename() ) );
                InputPair< FedoraObjectFields, FedoraObjectFieldsValue > searchPair = new InputPair< FedoraObjectFields, FedoraObjectFieldsValue >( FedoraObjectFields.SOURCE, dcSource );
                resultSearchFields.add( searchPair );
                fedoraPids = objectRepository.getIdentifiers( resultSearchFields, pidAsString, maximumResults, workNamespace );
             
                if ( fedoraPids.size() == 0 && ! dcTitle.valuename().equals( "" ) )
                {
                    resultSearchFields.clear();
                    searchPair = new InputPair< FedoraObjectFields, FedoraObjectFieldsValue >( FedoraObjectFields.TITLE, dcSource );
                    resultSearchFields.add( searchPair );
                    fedoraPids = objectRepository.getIdentifiers( resultSearchFields, pidAsString, maximumResults, workNamespace );
                }
            }

            if ( ( fedoraPids == null || fedoraPids.size() == 0 ) && ! dcTitle.valuename().equals( "" ) )
            {
                log.debug( String.format( "2 WR with dcSource '%s' and dcTitle '%s'", dcSource.valuename(), dcTitle.valuename() ) );
                if ( ! dcSource.valuename().equals( "" ) )
                {
                    resultSearchFields.clear();
                    InputPair< FedoraObjectFields, FedoraObjectFieldsValue > searchPair = new InputPair< FedoraObjectFields, FedoraObjectFieldsValue >( FedoraObjectFields.SOURCE, dcTitle );
                    resultSearchFields.add( searchPair );
                    fedoraPids = objectRepository.getIdentifiers( resultSearchFields, pidAsString, maximumResults, workNamespace );
                }
                else
                {
                    resultSearchFields.clear();
                    InputPair< FedoraObjectFields, FedoraObjectFieldsValue > searchPair = new InputPair< FedoraObjectFields, FedoraObjectFieldsValue >( FedoraObjectFields.TITLE, dcTitle );
                    resultSearchFields.add( searchPair );
                    fedoraPids = objectRepository.getIdentifiers( resultSearchFields, pidAsString, maximumResults, workNamespace );
                }
            }

            if ( fedoraPids == null || fedoraPids.size() == 0 )
            {
                log.debug( String.format( "No matching posts found for '%s' or '%s'", dcTitle.valuename(), dcSource.valuename() ) );
            }
        }
        else
        {
            if ( ! ( dcTitle.valuename().equals( "" ) || dcCreator.valuename().equals( "" ) ) )
            {
                log.debug( String.format( "WR with dcTitle '%s' and dcCreator '%s'", dcTitle.valuename(), dcCreator.valuename() ) );
                resultSearchFields.clear();
                InputPair< FedoraObjectFields, FedoraObjectFieldsValue > searchTitlePair = new InputPair< FedoraObjectFields, FedoraObjectFieldsValue >( FedoraObjectFields.TITLE, dcTitle );
                InputPair< FedoraObjectFields, FedoraObjectFieldsValue > searchCreatorPair = new InputPair< FedoraObjectFields, FedoraObjectFieldsValue >( FedoraObjectFields.CREATOR, dcCreator );
                resultSearchFields.add( searchTitlePair );
                resultSearchFields.add( searchCreatorPair );

                fedoraPids = objectRepository.getIdentifiers( resultSearchFields, pidAsString, maximumResults, workNamespace );
            }
            else
            {
                log.debug( String.format( "No matching posts found for '%s' or '%s'", dcTitle, dcCreator ) );
            }
        }

        if ( fedoraPids != null && fedoraPids.size() > 0 )
        {
            log.debug( String.format( "Pid with matching title, source, or creator = %s", fedoraPids.get( 0 ) ) );
        }

        ObjectIdentifier workPid = null;       

        if ( fedoraPids == null || fedoraPids.size() == 0 )
        {
            log.debug( String.format("ja7w: No Work found creating new work ") );
                       
            workPid = CreateWorkObject( dc );
        }
        else // fedoraPids.size() > 0
        {
            workPid = new PID(fedoraPids.get( 0 )); 
        }
                 

        log.debug( String.format( "Trying to add %s to the collection %s", cargo.getIdentifier(), workPid ) );


        this.objectRepository.addObjectRelation( workPid, DBCBIB.HAS_MANIFESTATION , cargo.getIdentifierAsString() );
        this.objectRepository.addObjectRelation( cargo.getIdentifier(), DBCBIB.IS_MEMBER_OF_WORK, workPid.getIdentifier() );
        
        return true;
    }


    private ObjectIdentifier CreateWorkObject(  DublinCore oldDc ) throws PluginException
    {      
        try
        {
            // todo: Clean up work object xml and language.
            CargoContainer cargo = new CargoContainer( );                       
            
            DublinCore workDC = new DublinCore(  );
            
            
            ScriptEngine engine = manager.getEngineByName( "JavaScript" );            
            engine.put( "log", log );            
            engine.put( "objectRepository", objectRepository );
            
            String path = FileSystemConfig.getScriptPath();
            String jsFileName = path + "marcxchange_workrelation.js";
            
            log.debug( String.format( "ja7w: url = %s", jsFileName ) );
            engine.eval( new java.io.FileReader( jsFileName ) );
            
            Invocable inv = (Invocable)engine;
            
            engine.put( "plugin", this );
            Object res = inv.invokeFunction( "generate_new_work", cargo, oldDc, workDC );
            
            log.debug( String.format( "ja7w: res of type %s", res.toString() ) );
            String fakexml = (String)res;
            if (fakexml == null)
            {
                throw new PluginException( "Internal error generate_new_work did not return a String" );
                
            }

            cargo.add( DataStreamType.OriginalData, "format", "internal", "da", "text/xml", IndexingAlias.None, fakexml.getBytes() );
           
            cargo.addMetaData( workDC );
                       
            try
            {
                this.objectRepository.storeObject( cargo, "internal", "work");                
            }
            catch (Exception e)
            {
                log.error( "ja7w:error in objectRepository.storeCargocontiner for new work item", e );
                throw new PluginException("Unable to store new work object", e);
            }
            
            return cargo.getIdentifier();
        }
        catch (Exception e)
        {
            log.error( "ja7w:error in fs.storeCargocontiner for new work item", e );
            throw new PluginException("Unable to store new work object", e);
        }        
    }
    

    @Override
    public PluginType getPluginType()
    {
        return pluginType;
    }


    @Override
    public void setObjectRepository( IObjectRepository objectRepository )
    {
        this.objectRepository = objectRepository;
    }
}
