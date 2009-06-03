/** \brief UnitTest for PluginID **/

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

import dk.dbc.opensearch.common.pluginframework.PluginID;

import static org.junit.Assert.*;
import org.junit.*;


/**
 * 
 */
public class PluginIDTest 
{
    /**
     * Tests that the id values generated on the basis of the
     * information given to the plugin constructor are identical given
     * identical parameters.
     */
    @Test 
    public void testUniqueHashValue() 
    {
        PluginID plugin1 = new PluginID( "a", "b", "c" );
        PluginID plugin2 = new PluginID( "a", "b", "c" );

        Assert.assertEquals( plugin1.getPluginID() , plugin2.getPluginID() );
    }


    @Test
    public void testDistinctHashValues()
    {
        PluginID plugin1 = new PluginID( "a", "b", "c" );
        PluginID plugin3 = new PluginID( "c", "b", "a" );
        Assert.assertTrue(  plugin1.getPluginID() != ( plugin3.getPluginID() ) );
    }


    @Test 
    public void testRetrievalOfFields()
    {

        PluginID plugin1 = new PluginID( "a", "b", "c" );

        Assert.assertEquals( "a", plugin1.getPluginSubmitter() );
        Assert.assertEquals( "b", plugin1.getPluginFormat() );
        Assert.assertEquals( "c", plugin1.getPluginTask() );
    }
}