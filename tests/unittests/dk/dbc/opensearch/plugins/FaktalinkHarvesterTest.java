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


import dk.dbc.opensearch.common.pluginframework.PluginID;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;

import org.junit.*;
import org.junit.Test;


public class FaktalinkHarvesterTest
{
//     @Before
//     public void SetUp()
//     {
//         //mockHarvestFaktaLink = createMock( HarvestFaktalink.class );
//     }


//     @After
//     public void TearDown()
//     {
//         //reset( mockHarvestFaktaLink );
//     }


//     @Test
//     public void testPluginID() throws FileNotFoundException
//     {
//         String submitter = "DBC";
//         String format = "text/xml";
//         String task = "";

//         PluginID pid = new PluginID( submitter, format, task );

//         String teststring = "æøå";
//         InputStream data = new ByteArrayInputStream( teststring.getBytes( ) );
//         FaktalinkHarvester hfl = new FaktalinkHarvester();
//         //hfl.init( pid, data );

//         /** \todo: there is a discrepancy in this call and the method being called. Please check */
// //        boolean submitterRet = submitter.equals( pidRet.getPluginSubmitter() );
// //        assertTrue( submitterRet );

// //        boolean formatRet = format.equals( pidRet.getPluginFormat() );
// //        assertTrue( formatRet );

// //        boolean taskRet = task.equals( pidRet.getPluginTask() );
// //        assertTrue( taskRet );
//     }


//     @Ignore( "ignoring until the architecture has been stabilised after the CargoContainer refactoring" )
//     @Test
//     public void testCargoContainer() throws IllegalArgumentException, NullPointerException, IOException
//     {
//         String teststring = "æøå";
//         InputStream data = new ByteArrayInputStream( teststring.getBytes( "UTF-8" ) );

//         String submitter = "DBC";
//         String format = "text/xml";
//         String task = "";
//         String lang = "DA";

//         //mockHarvestFaktaLink = new HarvestFaktalink( "id", path, submitter, "format" );
//         PluginID pid = new PluginID( submitter, format, task );
//         FaktalinkHarvester hfl = new FaktalinkHarvester();
//         //hfl.init(pid, data);

//         // CargoContainer cc = mockHarvestFaktaLink.getCargoContainer();
//         //CargoContainer cc = hfl.getCargoContainer();

//         // the following methods are package private and not for casual consumption :)
//         // boolean submitterChecked = cc.checkSubmitter( submitter );
//         // assertTrue( submitterChecked );

//         // boolean langChecked = cc.checkLanguage( lang );
//         // assertTrue( langChecked );
//     }

    @Test 
    public void hatTest()
    {
        //do nothing
    }
}




















