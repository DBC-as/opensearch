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
 * \file ForceFedoraPidTest
 * \brief unittest for the ForceFedoraPid plugin
 */


package dk.dbc.opensearch.plugins;


import dk.dbc.opensearch.common.fedora.PID;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;

import java.util.Map;

import static org.junit.Assert.*;
import org.junit.*;
import mockit.Mock;
import mockit.MockClass;
import mockit.Mocked;
import static mockit.Mockit.setUpMocks;
import static mockit.Mockit.tearDownMocks;


public class ForceFedoraPidTest {

    @Mocked IObjectRepository mockIObjectRepository;
    @Mocked Map<String, String> mockArgsMap;

    ForceFedoraPid forcePlugin = null;
    CargoContainer cargo;
    CargoContainer returnCargo;
    //static final String testPid1 = "test:1";
  
    static final String referenceData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><referencedata><info submitter=\"dbc\" format=\"ebrary\" lang=\"dk\"/></referencedata>";
    
    static final String invalidData = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?><ting:hest xmlns:ting=\"http://www.dbc.dk/ting\">&</ting:hest>";
   
    static final byte[] invaliddatabytes = invalidData.getBytes();
    static final String noData = "";
    static final byte[] noDataBytes = noData.getBytes();

    DataStreamType streamTypeOriginal = DataStreamType.OriginalData;
    DataStreamType streamTypeNotOriginal = DataStreamType.AdminData;
    // String indexAlias = "danmarcxchange";
    // String indexAliasNotValid = "None";
   
    @Before public void SetUp() throws PluginException 
    {
	forcePlugin = new ForceFedoraPid( mockIObjectRepository );
    }

  
    @After public void TearDown() 
    {
    }

    /**
     * tests that the plugin retrieves the lefthand string part until the
     * "|" and concatinates it with the submitter of the cargoContainer as the 
     * right hand side of the string and with a ":" between the concatinated
     * strings
     */
    @Test 
    public void runPluginTest() throws Exception 
    {
        String IDLeft = "84813133";
        String IDRight = "870971";
        String submitter = "dbc";
        String constructedID = submitter + ":" + IDLeft;       
        String data = createDataString( IDLeft + "|" + IDRight );
        byte[] databytes = data.getBytes();
        cargo = new CargoContainer();
        cargo.add( streamTypeOriginal,
                   "ebrary",
                   submitter,
                   "dk",
                   "text/xml",
                   databytes );

        //      cargo.setIndexingAlias( indexAlias, streamTypeOriginal );
	IPluginEnvironment env = forcePlugin.createEnvironment( mockIObjectRepository, mockArgsMap );
        returnCargo = forcePlugin.runPlugin( env, cargo );
     
        
        assertEquals( returnCargo.getIdentifierAsString(), constructedID );
    }

    /**
     * Tests the behaviour when no identigier is to be found in the data
     */

    @Test( expected = PluginException.class )
    public void runPluginNoIdentifierTest() throws Exception
    {
        String data = createDataString( "" );
        byte[] databytes = data.getBytes();
        cargo = new CargoContainer();
        cargo.add( streamTypeOriginal,   
                   "ebrary",
                   "dbc",
                   "dk",
                   "text/xml",
                   databytes );
        //   cargo.setIndexingAlias( indexAlias, streamTypeOriginal );

	IPluginEnvironment env = forcePlugin.createEnvironment( mockIObjectRepository, mockArgsMap );
        forcePlugin.runPlugin( env, cargo );
    }

    /**
     * Tests that the getPluginType method returns the correct type
     */
    @Test
    public void getPluginTypeTest() throws Exception
    {
        assertTrue( forcePlugin.getPluginType() == PluginType.ANNOTATE );
    }

    /**
     * Helper methods
     */

    /**
     * method for creating an xml string for use as data in the CargoContainers used
     * where the ac:identifier is a variable
     */
    public String createDataString( String identifier )
    {
        return "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?><ting:container xmlns:ting=\"http://www.dbc.dk/ting\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns=\"http://www.bs.dk/standards/MarcXchange\" xmlns:dkabm=\"http://biblstandard.dk/abm/namespace/dkabm/\" xmlns:ISO639-2=\"http://lcweb.loc.gov/standards/iso639-2/\" xmlns:dcmitype=\"http://purl.org/dc/dcmitype/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:ac=\"http://biblstandard.dk/ac/namespace/\" xmlns:dkdcplus=\"http://biblstandard.dk/abm/namespace/dkdcplus/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><dkabm:record><ac:identifier>"+ identifier +"</ac:identifier><ac:source>Artikelbasen</ac:source><dc:title>Testtitel</dc:title><dc:creator>Bente Bundg@ård</dc:creator><dc:subject>Orange Order</dc:subject><dc:subject xsi:type='dkdcplus:DK5'>93.8</dc:subject><dc:subject xsi:type='dkdcplus:DK5-Text'>Irlands historie</dc:subject><dc:subject xsi:type='dkdcplus:DBCF'>marcher</dc:subject><dc:subject xsi:type='dkdcplus:DBCF'>historie</dc:subject><dc:date>1999</dc:date><dc:type xsi:type='dkdcplus:BibDK-Type'>Avisartikel</dc:type><dc:format>S. 11</dc:format><dc:language xsi:type='dcterms:ISO639-2'>dan</dc:language><dc:language>Dansk</dc:language><dcterms:isPartOf>Information 1999-02-26</dcterms:isPartOf><dcterms:spatial xsi:type='dkdcplus:DBCF'>Nordirland</dcterms:spatial><dcterms:spatial xsi:type='dkdcplus:DBCF'>Portadown</dcterms:spatial></dkabm:record><collection><record format=\"danMARC2\" type=\"Bibliographic\"><leader>00687naai022002770004500</leader><datafield tag=\"001\" ind1=\"0\" ind2=\"0\"><subfield code=\"a\">8 481 313 3</subfield><subfield code=\"b\">870971</subfield><subfield code=\"c\">20051128145709</subfield><subfield code=\"d\">19990226</subfield><subfield code=\"f\">a</subfield></datafield><datafield tag=\"004\" ind1=\"0\" ind2=\"0\"><subfield code=\"r\">n</subfield><subfield code=\"a\">i</subfield></datafield><datafield tag=\"008\" ind1=\"0\" ind2=\"0\"><subfield code=\"t\">a</subfield><subfield code=\"u\">f</subfield><subfield code=\"a\">1999</subfield><subfield code=\"b\">dk</subfield><subfield code=\"l\">dan</subfield><subfield code=\"v\">0</subfield><subfield code=\"r\">an</subfield></datafield><datafield tag=\"009\" ind1=\"0\" ind2=\"0\"><subfield code=\"a\">a</subfield><subfield code=\"g\">xx</subfield></datafield><datafield tag=\"016\" ind1=\"0\" ind2=\"0\"><subfield code=\"a\">0 324 309 5</subfield></datafield><datafield tag=\"032\" ind1=\"0\" ind2=\"0\"><subfield code=\"a\">ABU200549</subfield><subfield code=\"a\">DAR199908</subfield></datafield><datafield tag=\"086\" ind1=\"0\" ind2=\"0\"><subfield code=\"a\">Irlands historie</subfield></datafield><datafield tag=\"245\" ind1=\"0\" ind2=\"0\"><subfield code=\"a\">Orangemændene viger ikke</subfield></datafield><datafield tag=\"300\" ind1=\"0\" ind2=\"0\"><subfield code=\"a\">S. 11</subfield></datafield><datafield tag=\"557\" ind1=\"0\" ind2=\"0\"><subfield code=\"a\">Information</subfield><subfield code=\"j\">1999</subfield><subfield code=\"V\">1999-02-26</subfield><subfield code=\"v\">1999-02-26</subfield></datafield><datafield tag=\"610\" ind1=\"0\" ind2=\"0\"><subfield code=\"a\">Orange Order</subfield></datafield><datafield tag=\"652\" ind1=\"0\" ind2=\"0\"><subfield code=\"m\">93.8</subfield></datafield><datafield tag=\"666\" ind1=\"0\" ind2=\"0\"><subfield code=\"0\"></subfield><subfield code=\"f\">marcher</subfield></datafield><datafield tag=\"666\" ind1=\"0\" ind2=\"0\"><subfield code=\"0\"></subfield><subfield code=\"f\">historie</subfield></datafield><datafield tag=\"666\" ind1=\"0\" ind2=\"0\"><subfield code=\"0\"></subfield><subfield code=\"e\">Nordirland</subfield></datafield><datafield tag=\"666\" ind1=\"0\" ind2=\"0\"><subfield code=\"0\"></subfield><subfield code=\"e\">Portadown</subfield></datafield><datafield tag=\"700\" ind1=\"0\" ind2=\"0\"><subfield code=\"a\">Bundg@ård</subfield><subfield code=\"h\">Bente</subfield></datafield></record></collection></ting:container>";
    }

}