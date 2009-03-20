/** \brief UnitTest for PluginType */
package dk.dbc.opensearch.common.pluginframework.tests;

import dk.dbc.opensearch.common.pluginframework.PluginType;

import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 */
public class PluginTypeTest
{

    PluginType pt;
    boolean outcome;
    /**
     * Tests the constructions of the PluginType
     */
    @Test public void testLegalConstructions()
    {
        pt = PluginType.HARVEST;

        switch( pt )
            {
            case HARVEST: outcome = true;
                break;
            default: outcome = false;
            }
        assertTrue( outcome );
        pt = PluginType.ANNOTATE;
        switch( pt )
            {
            case ANNOTATE: outcome = true;
                break;
            default: outcome = false;
            }
        assertTrue( outcome );
        pt = PluginType.STORE;
        switch( pt )
            {
            case STORE: outcome = true;
                break;
            default: outcome = false;
            }
        assertTrue( outcome );
        pt = PluginType.INDEX;
        switch( pt )
            {
            case INDEX: outcome = true;
                break;
            default: outcome = false;
            }
        assertTrue( outcome );
        pt = PluginType.PROCESS;
        switch( pt )
            {
            case PROCESS: outcome = true;
                break;
            default: outcome = false;
            }
        assertTrue( outcome );
        pt = PluginType.RETRIEVE;
        switch( pt )
            {
            case RETRIEVE: outcome = true;
                break;
            default: outcome = false;
            }
        assertTrue( outcome );
    }
}