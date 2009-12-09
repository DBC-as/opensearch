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
 * \file XMLHarvesterTest.java
 * \brief Unittest for XMLHarvester class
 */


package dk.dbc.opensearch.plugins;

import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.IIdentifier;
import dk.dbc.opensearch.common.xml.XMLUtils;
import dk.dbc.opensearch.components.datadock.DatadockJob;
import java.io.IOException;

import org.junit.*;
import static org.junit.Assert.*;
import mockit.Mock;
import mockit.MockClass;
import mockit.Mocked;
import org.w3c.dom.Document;
import static mockit.Mockit.setUpMocks;
import static mockit.Mockit.tearDownMocks;



public class XMLHarvesterTest 
{


    String alias = "docbook";
    static String submitter = "dbc";
    static String format = "matvurd";

    static final String referenceData = String.format( "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><referencedata><info submitter=\"%s\" format=\"%s\" lang=\"dk\"/></referencedata>", submitter, format);
    static final String data = "<?xml version='1.0' encoding='iso-8859-1'?><ting:container xmlns:ting=\"http://www.dbc.dk/ting\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:docbook=\"http://docbook.org/ns/docbook\" xmlns:dkabm=\"http://biblstandard.dk/abm/namespace/dkabm/\" xmlns:ISO639-2=\"http://lcweb.loc.gov/standards/iso639-2/\" xmlns:dcmitype=\"http://purl.org/dc/dcmitype/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:ac=\"http://biblstandard.dk/ac/namespace/\" xmlns:dkdcplus=\"http://biblstandard.dk/abm/namespace/dkdcplus/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><dkabm:record><ac:identifier>26477115|870976</ac:identifier><ac:source>Materialevurderinger</ac:source><dc:title>Aschehougs bog om kunst [Materialevurdering]</dc:title><dc:title>Aschehougs bog om</dc:title><dc:title>En Dorling Kindersley bog</dc:title><dc:creator>Robert Cumming</dc:creator><dc:subject xsi:type='dkdcplus:DK5'>70.9</dc:subject><dc:subject xsi:type='dkdcplus:DK5-Text'>Kunsthistorie i alm.</dc:subject><dc:subject xsi:type='dkdcplus:DBCF'>kunst</dc:subject><dc:subject xsi:type='dkdcplus:DBCF'>malerkunst</dc:subject><dc:subject xsi:type='dkdcplus:DBCF'>skulptur</dc:subject><dc:subject xsi:type='dkdcplus:DBCF'>kunsthistorie</dc:subject><dc:description>Rygtitel Kunst</dc:description><dc:description>På omslaget: Kunstnere, malerier, skulpturer, stilarter</dc:description><dc:description>Indhold: Hvad er kunst? ; Tidlig kunst ; Gotik og tidlig renæssance ; Højrenæssance og manierisme ; Barokperioden ; Fra rokoko til nyklassicisme ; Romantisk og akademisk kunst ; Modernisme ; Kunst efter 1970</dc:description><dkdcplus:version>1. udgave 1. oplag</dkdcplus:version><dc:publisher>[Kbh.] Aschehoug</dc:publisher><dc:date>2006</dc:date><dc:type xsi:type='dkdcplus:BibDK-Type'>Materialevurdering: Bog</dc:type><dc:format>512 sider</dc:format><dc:identifier xsi:type='dkdcplus:ISBN'>87-11-26612-0</dc:identifier><dc:source>Art</dc:source><dc:language xsi:type='dcterms:ISO639-2'>dan</dc:language><dc:language>Dansk</dc:language></dkabm:record><docbook:article><docbook:title>Lektørudtalelse: Cumming, Robert: Aschehougs bog om kunst - 2006</docbook:title><docbook:info xml:id='FAUST-30624815'><docbook:author><docbook:personname><docbook:firstname>Kirsten Marie</docbook:firstname><docbook:surname>Hansen</docbook:surname></docbook:personname></docbook:author></docbook:info><docbook:section><docbook:para><docbook:info><docbook:title></docbook:title></docbook:info>Aschehougs bog om kunst er en dansk oversættelse af en Dorling Kindersley håndbog om kunst. Den indledes med et afsnit om kunst samt en beskrivelse af forskellige teknikker og materialer. Herefter starter en gennemgang af kunstens historie i den vestlige verden opdelt i kapitler efter perioder fra 3000 f.Kr. til kunsten efter 1970. Hvert kapitel begynder med en præsentation af den pågældende periode, hvorefter en række af periodens mest markante kunstnere kort beskrives. Ved hver kunstner redegøres for levetid, nationalitet, brug af teknikker m.m. Til slut er der ordliste og indeks. Bogen er rigt illustreret og minder i layout om Politikens visuelle guides. Der står lidt om mange emner. De enkelte kunstnere beskrives ikke så grundigt, men som appetitvækker er det en meget flot bog. Formatet kvalificerer den til en plads i fjällräven under ferieturen. Som følge af den store mængde information på begrænset plads, er skriften visse steder lidt for lille, men det er også min eneste anke til et ellers indbydende værk, som nok fortrinsvis appellerer til voksne, men som takket være korte artikler og tilgængeligt sprog også vil kunne bruges fra folkeskolens ældste klasser. Der findes andre et-binds kunsthistorier bl.a. Kunstens historie, Politikens guide til kunsten og Kunstens verdenshistorie, mange er dog ret digre og dermed ikke bekvemme at medbringe</docbook:para></docbook:section></docbook:article></ting:container>";
    static final String invalidData = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?><ting:hest xmlns:ting=\"http://www.dbc.dk/ting\">&</ting:hest>";

    static final byte[] databytes = data.getBytes();

    
    DatadockJob datadockJob;
    XMLHarvester harvestPlugin;
    CargoContainer cargoContainer;

    @Mocked IIdentifier mockIdentifier;
    
    @MockClass( realClass = CargoContainer.class )
    public static class MockCargoContainer
    {
        @Mock public static long add( DataStreamType dataStreamName,
                     String format,
                     String submitter,
                     String language,
                     String mimetype,
                     String alias,
                     byte[] data ) throws IOException
        {
            throw new IOException( "test" );
        }
    }
    
    @Before
    public void setUp() throws Exception
    {
        Document xmldata = XMLUtils.documentFromString( referenceData );
        datadockJob = new DatadockJob( mockIdentifier, xmldata );
    }

    @After
    public void tearDown()
    {
        tearDownMocks();
        datadockJob = null;
    }

    @Test
    public void getCargoContainerTest()throws Exception
 {
     harvestPlugin = new XMLHarvester();
     cargoContainer = harvestPlugin.getCargoContainer(datadockJob, databytes, alias );

     //There is data in the returned CargoContainer
     assertEquals( 1, cargoContainer.getCargoObjectCount() );
     //The added data has been given the correct DataStreamType
     assertEquals( true, cargoContainer.hasCargo( DataStreamType.OriginalData) );
     //the pid given by the mocked fedoraHandle
     assertEquals( null, cargoContainer.getIdentifier() );

     assertEquals( alias, cargoContainer.getIndexingAlias( DataStreamType.OriginalData ) );
    }
        @Test( expected = IOException.class )
    public void cargoContainerCantAddDataTest() throws Exception
    {
        setUpMocks(MockCargoContainer.class);
        harvestPlugin = new XMLHarvester();
        try
        {
            cargoContainer = harvestPlugin.getCargoContainer( datadockJob, databytes, alias );
        }
        catch( PluginException pe )
        {
            throw pe.getException();
        }
    }

    @Test
    public void getPluginType()
    {
        harvestPlugin = new XMLHarvester();
        if ( PluginType.HARVEST != harvestPlugin.getPluginType() )
        {
            fail("HarvestPlugin returned wrong type");
        }
    }
}
