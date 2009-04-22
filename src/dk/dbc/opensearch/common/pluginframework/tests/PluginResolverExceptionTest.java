/** \brief UnitTest for PluginResolverException */
package dk.dbc.opensearch.common.pluginframework.tests;

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

import static org.junit.Assert.*;
import org.junit.*;
import mockit.Mockit;
import static org.easymock.classextension.EasyMock.*;

import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.types.ThrownInfo;

import java.util.Vector;

/**
 * Class to test the PluginResolverException
 */
public class PluginResolverExceptionTest {

    PluginResolverException pre;
    String message = "message";
    Vector<ThrownInfo> exceptionVector = new Vector();

    /**
     *
     */
    @Before public void SetUp() {

    }

    /**
     *
     */
    @After public void TearDown() {

    }

    /**
     *
     */
    @Test public void pluginResolverExceptionTwoArgsConstructorTest() {
        ThrownInfo testInfo = new ThrownInfo( new NullPointerException( "test" ), message );
        exceptionVector.add( testInfo );
        pre = new PluginResolverException( exceptionVector, message );
        assertTrue( pre.getMessage().equals( message) );
        assertTrue( pre.getExceptionVector() == exceptionVector );
    }
    /**
     *
     */
    @Test public void pluginResolverExceptionOneArgConstructorTest() {

        pre = new PluginResolverException( message );
        assertTrue( pre.getMessage().equals( message) );
        assertTrue( pre.getExceptionVector() == null );
    }
}