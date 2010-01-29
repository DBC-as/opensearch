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
package dk.dbc.opensearch.common.fedora.search;

import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ServerException;
import fedora.server.search.FieldSearch;
import fedora.server.search.FieldSearchQuery;
import fedora.server.search.FieldSearchResult;
import fedora.server.storage.DOReader;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.utilities.DCFields;
import java.util.Map;


/**
 * This FieldSearch implementation uses a dedicated Lucene index for indexing
 * fields from the modifying operations of fedora (delete, ingest, modify)
 */
public class FieldSearchLucene extends Module implements FieldSearch
{

    public FieldSearchLucene( Map<String, String> moduleParameters, Server server, String role ) throws ModuleInitializationException
    {
        super( moduleParameters, server, role );
    }


    @Override
    public void postInitModule() throws ModuleInitializationException
    {
        if( getParameter( "" ) == null )
        {
            throw new ModuleInitializationException( "parameter must be specified.", getRole() );
        }
        String maxResults = null;
        maxResults = getParameter( "" );
        if( maxResults.equals( "" ) )
        {
            throw new IllegalArgumentException( "" );
        }
    }


    @Override
    public void update( DOReader reader ) throws ServerException
    {
        String pid = reader.GetObjectPID();

        DatastreamXMLMetadata dcmd = null;
            try {
                dcmd = (DatastreamXMLMetadata) reader.GetDatastream("DC", null);
            } catch (ClassCastException cce) {
                throw new ObjectIntegrityException("Object "
                        + reader.GetObjectPID()
                        + " has a DC datastream, but it's not inline XML.");
            }

        DCFields dcFields = new DCFields( dcmd.getContentStream() );

        throw new UnsupportedOperationException( "Not supported yet." );
    }


    @Override
    public boolean delete( String string ) throws ServerException
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }


    @Override
    public FieldSearchResult findObjects( String[] strings, int i, FieldSearchQuery fsq ) throws ServerException
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }


    @Override
    public FieldSearchResult resumeFindObjects( String string ) throws ServerException
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }


}
