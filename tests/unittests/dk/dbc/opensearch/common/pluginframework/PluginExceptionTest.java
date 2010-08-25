/** \brief UnitTest for PluginException */

package dk.dbc.opensearch.common.pluginframework;

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

import dk.dbc.opensearch.common.pluginframework.PluginException;
import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.*;


/**
 * 
 */
public class PluginExceptionTest 
{
    PluginException pe;

    /**
     *
     */
    @Before 
    public void SetUp() { }


    /**
     *
     */
    @After 
    public void TearDown() 
    {
        pe = null;
    }


    /**
     * 
     */
    @Test 
    public void testConstructorNoMsg() 
    {
	// When only giving a cause and _no_ message, 
	// the underlying class will create a message based 
	// on the cause's message.
        pe = new PluginException( new IOException( "test" ) );
        assertTrue( pe.getCause().getClass() == IOException.class );
        assertTrue( pe.getCause().getMessage().equals( "test" ) );
	assertTrue( pe.getMessage().equals( "java.io.IOException: test" ) );
    }
    

    /**
     * 
     */
    @Test 
    public void testConstructorNoExp() 
    {
        pe = new PluginException( "test" );
        assertTrue( pe.getCause() == null );
        assertTrue( pe.getMessage().equals( "test" ) ); 
    }
    

    /**
     * 
     */
    @Test 
    public void testConstructor() 
    {
        pe = new PluginException( "test", new IOException( "test" ) );
        assertTrue( pe.getCause().getClass() == IOException.class );
        assertTrue( pe.getCause().getMessage().equals( "test" ) );
        assertTrue( pe.getMessage().equals( "test" ) ); 
    }
}