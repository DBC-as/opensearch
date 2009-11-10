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
 * \file FedoraAuxiliary.java
 * \brief Main class for calling extended fedora functionality from the command line
 */


package dk.dbc.opensearch.common.fedora;


import dk.dbc.opensearch.common.fedora.IObjectRepository.Value;
import dk.dbc.opensearch.common.types.InputPair;

import java.io.IOException;
import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;


/**
 * Auxiliary class for operations on the fedora repository
 * \todo: decide what functionality this class should actually provide
 */
public class FedoraAuxiliaryMain
{

    static IObjectRepository objectRepository;
    //private static FedoraHandle fedoraHandle;


    Vector< String > types = new Vector< String >();

    private void init() throws ObjectRepositoryException
    {
        try
        {
            objectRepository = new FedoraObjectRepository();
        }
        catch( ObjectRepositoryException ore )
        {
            System.out.println( "Could not initialize objectRepository" );
            throw new ObjectRepositoryException( "Could not initialize FedoraObjectRepository (is fedora running?)" );
        }

        types.add( "deletepids" );
    }


    private void run( String arg ) throws ConfigurationException, ServiceException, MalformedURLException, IOException, ObjectRepositoryException
    {
       init();
        
        if ( types.contains( arg ) )
        {
            if ( arg.equals( "deletepids" ) )
            {
                deletePids();
            }
        }
        else
        {
            System.out.println( "%s is not a valid argument. Choose between: ");
            for ( int i = 0; i < types.size(); i++ )
            {
                System.out.print( types.get( i ) + " " );
            }
        }
    }

    
    public static void main( String[] args ) throws ConfigurationException, ServiceException, MalformedURLException, IOException, ObjectRepositoryException
    {
        String arg = args[0];
        
        FedoraAuxiliaryMain fam = new FedoraAuxiliaryMain();
        fam.run( arg );
    }


    static void deletePids() throws ConfigurationException, ServiceException, MalformedURLException, IOException, ObjectRepositoryException
    {

        System.out.println( "*** kalder testDeleteObjects ***" );
        String[] labels = { "anmeldelser", "anmeld", "forfatterw", "matvurd", "katalog", "danmarcxchange", "ebrary", "ebsco", "artikler", "dr_forfatteratlas", "dr_atlas", "dr_bonanza", "materialevurderinger", "docbook_forfatterweb", "docbook_faktalink", "format" };
        List< InputPair< FedoraObjectFields, Value > > resultSearchFields = new ArrayList< InputPair< FedoraObjectFields, Value > >();
        int maximumResult = 10000;
        for ( int i = 0; i < labels.length; i++ )
        {
            InputPair< FedoraObjectFields, Value > pair = new InputPair< FedoraObjectFields, Value >( FedoraObjectFields.LABEL , new Value( labels[i] ) );
            resultSearchFields.add( pair );
            List< String > pids = objectRepository.getIdentifiersUnqualified( resultSearchFields, maximumResult );
            System.out.println( "pids.length: " + pids.size() );
            
            for ( String pid : pids )
            {
                System.out.println( String.format( "Deleting object pid: %s", pid ) );
                objectRepository.deleteObject( pid, "Deleting from FedoraAuxiliaryMain" );
            }

            resultSearchFields.clear();
        }
    }


    /*static void DeleteObjectPids( String[] labels, int runsPerLabel ) throws ConfigurationException, ServiceException, MalformedURLException, IOException, ObjectRepositoryException
    {
        objectRepository = new FedoraObjectRepository();
        List<String> pids = new ArrayList<String>();
        List<String> fieldSearchList = Arrays.asList( labels );
        for ( String str : labels )
        {
            for ( int i = 0; i < runsPerLabel; i++ )
            {
                pids = objectRepository.getIdentifiers( str, fieldSearchList, "", 10000 );
            }
            
            if( pids.size() > 0 )
            {
                System.out.println( "testDeleteObjectPids - pids.length: " + pids.size() );
            }

            for( String pid: pids )
            {
                testDeleteObject( pid );
            }
        }

        System.out.println( String.format( "No of pids deleted: %s", pids.size() ) );
    }*/


    static void testDeleteObject( String pid ) throws ConfigurationException, ServiceException, MalformedURLException, IOException, ObjectRepositoryException
    {
        objectRepository = new FedoraObjectRepository();
        objectRepository.deleteObject( pid, "test delete" );
        System.out.println( String.format( "Object with pid: %s deleted", pid ) );
    }
}
