/** \brief UnitTest for PluginType */

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
    @Test 
    public void testLegalConstructions()
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