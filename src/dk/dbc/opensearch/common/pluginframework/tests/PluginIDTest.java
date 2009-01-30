/** \brief UnitTest for PluginID **/
package dk.dbc.opensearch.common.pluginframework.tests;

import static org.junit.Assert.*;
import org.junit.*;

import dk.dbc.opensearch.common.pluginframework.PluginID;

/**
 * 
 */
public class PluginIDTest {

    /**
     * Tests that the id values generated on the basis of the
     * information given to the plugin constructor are identical given
     * identical parameters.
     */
    @Test public void testUniqueHashValue() {
        PluginID plugin1 = new PluginID( "a", "b", "c" );
        PluginID plugin2 = new PluginID( "a", "b", "c" );

        Assert.assertEquals( plugin1.getPluginID() , plugin2.getPluginID() );
    }
    @Test public void testDistinctHashValues(){
        PluginID plugin1 = new PluginID( "a", "b", "c" );
        PluginID plugin3 = new PluginID( "c", "b", "a" );
        Assert.assertTrue(  plugin1.getPluginID() != ( plugin3.getPluginID() ) );
    }
    @Test public void testRetrievalOfFields(){

        PluginID plugin1 = new PluginID( "a", "b", "c" );

        Assert.assertEquals( "a", plugin1.getPluginSubmitter() );
        Assert.assertEquals( "b", plugin1.getPluginFormat() );
        Assert.assertEquals( "c", plugin1.getPluginTask() );
    }

}