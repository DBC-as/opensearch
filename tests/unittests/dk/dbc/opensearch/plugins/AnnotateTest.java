/**
 * \file AnnotateTest.java
 * \brief The AnnotateTest class
 * \package tests;
 */

package dk.dbc.opensearch.plugins;


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


import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.IndexingAlias;
import dk.dbc.opensearch.components.datadock.DatadockJob;
import dk.dbc.opensearch.plugins.DocbookAnnotate;
import dk.dbc.opensearch.common.helpers.OpensearchNamespaceContext;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;

import org.junit.*;
import static org.junit.Assert.*;
import mockit.Mock;
import mockit.MockClass;
import mockit.Mockit;

/**
 *
 */
public class AnnotateTest 
{
    DocbookAnnotate dbAnnotate;
    //OpensearchNamespaceContext osnsc;
    /**
     *
     */

    @Before 
    public void setUp()
    {
        // osnsc = createMock( OpensearchNamespaceContext.class );
    }

    @Test 
    public void testConstructor() throws Exception 
    {        
        dbAnnotate = new DocbookAnnotate();
    }
}
