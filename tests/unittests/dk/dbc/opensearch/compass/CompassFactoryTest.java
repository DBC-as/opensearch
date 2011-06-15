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
 * \file CompassFactoryTest.java
 * \brief Tests The Compass Factory
 */
package dk.dbc.opensearch.compass;


import dk.dbc.opensearch.config.CompassConfig;

import java.io.File;
import java.io.FileOutputStream;

import mockit.Mockit;

import org.compass.core.Compass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Tests The Compass Factory
 */
public class CompassFactoryTest {

    static String compassStr="<compass-core-config xmlns=\"http://www.compass-project.org/schema/core-config\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.compass-project.org/schema/core-config http://www.compass-project.org/schema/compass-core-config-2.2.xsd\"><compass name=\"default\"><connection><file path=\"indexes\"/></connection><transaction lockTimeout=\"30\" lockPollInterval=\"100\"/><converters><converter name=\"xmlContentMapping\" type=\"org.compass.core.converter.mapping.xsem.XmlContentMappingConverter\"></converter><converter name=\"lowercase\" type=\"dk.dbc.opensearch.compass.converters.LowercaseXmlConverter\"></converter><converter name=\"default\" type=\"dk.dbc.opensearch.compass.converters.DefaultConverter\"></converter><converter name=\"facet\" type=\"dk.dbc.opensearch.compass.converters.FacetConverter\"></converter><converter name=\"phrase\" type=\"dk.dbc.opensearch.compass.converters.PhraseConverter\"></converter><converter name=\"sort\" type=\"dk.dbc.opensearch.compass.converters.SortConverter\"></converter></converters><searchEngine><analyzer name=\"default\" type=\"CustomAnalyzer\" analyzerClass=\"dk.dbc.opensearch.lucene.DBCAnalyzer\"></analyzer></searchEngine><settings><setting name=\"compass.xsem.contentConverter.type\" value=\"jdom-stax\"/><setting name=\"compass.xsem.contentConverter.wrapper\" value=\"pool\"/><setting name=\"compass.xsem.namespace.ting.uri\" value=\"http://www.dbc.dk/ting\" /><setting name=\"compass.xsem.namespace.dc.uri\" value=\"http://purl.org/dc/elements/1.1/\" /><setting name=\"compass.xsem.namespace.docbook.uri\" value=\"http://docbook.org/ns/docbook\" /><setting name=\"compass.xsem.namespace.dkabm.uri\" value=\"http://biblstandard.dk/abm/namespace/dkabm/\" /><setting name=\"compass.xsem.namespace.ISO639-2.uri\" value=\"http://lcweb.loc.gov/standards/iso639-2/\" /><setting name=\"compass.xsem.namespace.dcmitype.uri\" value=\"http://purl.org/dc/dcmitype/\" /><setting name=\"compass.xsem.namespace.dcterms.uri\" value=\"http://purl.org/dc/terms/\" /><setting name=\"compass.xsem.namespace.ac.uri\" value=\"http://biblstandard.dk/ac/namespace/\" /><setting name=\"compass.xsem.namespace.dkdcplus.uri\" value=\"http://biblstandard.dk/abm/namespace/dkdcplus/\" /><setting name=\"compass.xsem.namespace.xsi.uri\" value=\"http://www.w3.org/2001/XMLSchema-instance\" /></settings></compass></compass-core-config>";
    static String cpmData = "<?xml version=\"1.0\"?><!DOCTYPE compass-core-mapping PUBLIC \"-//Compass/Compass Core Mapping DTD 2.0//EN\" \"http://www.compass-project.org/dtd/compass-core-mapping-2.2.dtd\"><compass-core-mapping><xml-object alias=\"docbook\" sub-index=\"opensearch-index\"><xml-id name=\"id\" xpath=\"/ting:container/ting:fedoraPid\"/><xml-property name=\"rec.id\" xpath=\"/ting:container/dkabm:record/ac:identifier | /ting:container/ting:fedoraPid\" value-converter=\"default\"/><xml-property name=\"dc.title\" xpath=\"/ting:container/docbook:article/docbook:title | /ting:container/dkabm:record/dcterms:alternative\" value-converter=\"default\"/><xml-property name=\"dc.creator\" xpath=\"/ting:container/docbook:article/docbook:info/docbook:author/docbook:personname/* | /ting:container/dkabm:record/dc:creator | /ting:container/dkabm:record/dc:contributor\" value-converter=\"default\"/><xml-property name=\"cql.anyIndexes\" xpath=\"/ting:container/docbook:article/docbook:title | /ting:container/docbook:article/docbook:info/docbook:abstract/docbook:para | /ting:container/docbook:article/docbook:info/docbook:subjectset/docbook:subject/docbook:subjectterm | /ting:container/docbook:article/docbook:section/docbook:title | /ting:container/docbook:article/docbook:section/docbook:para\" value-converter=\"default\"/><xml-property name=\"dc.description\" xpath=\"/ting:container/docbook:article/docbook:info/docbook:abstract/docbook:para\" value-converter=\"default\"/><xml-property name=\"dc.subject\" xpath=\"/ting:container/docbook:article/docbook:info/docbook:subjectset/docbook:subject/docbook:subjectterm | /ting:container/dkabm:record/dc:subject\" value-converter=\"default\"/><xml-property name=\"dc.type\" xpath=\"/ting:container/dkabm:record/dc:type\" value-converter=\"default\"/><xml-property name=\"dc.format\" xpath=\"/ting:container/dkabm:record/dc:format | /ting:container/dkabm:record/dcterms:extent\" value-converter=\"default\"/><xml-property name=\"dc.language\" xpath=\"/ting:container/dkabm:record/dc:language\" value-converter=\"default\"/><xml-property name=\"dc.date\" xpath=\"/ting:container/dkabm:record/dc:date\" value-converter=\"default\"/><xml-property name=\"dc.identifier\" xpath=\"/ting:container/dkabm:record/dc:identifier\" value-converter=\"default\"/><xml-property name=\"dc.publisher\" xpath=\"/ting:container/dkabm:record/dc:publisher\" value-converter=\"default\"/><xml-property name=\"ac.source\" xpath=\"/ting:container/dkabm:record/ac:source | ting:container/format\" value-converter=\"default\"/><xml-property name=\"phrase.title\" xpath=\"/ting:container/docbook:article/docbook:title | /ting:container/dkabm:record/dcterms:alternative\" index=\"un_tokenized\" value-converter=\"phrase\"/><xml-property name=\"phrase.creator\" xpath=\"/ting:container/docbook:article/docbook:info/docbook:author/docbook:personname/* | /ting:container/dkabm:record/dc:creator | /ting:container/dkabm:record/dc:contributor\" index=\"un_tokenized\" value-converter=\"phrase\"/><xml-property name=\"phrase.anyIndexes\" xpath=\"/ting:container/docbook:article/docbook:title | /ting:container/docbook:article/docbook:info/docbook:abstract/docbook:para | /ting:container/docbook:article/docbook:info/docbook:subjectset/docbook:subject/docbook:subjectterm | /ting:container/docbook:article/docbook:section/docbook:title | /ting:container/docbook:article/docbook:section/docbook:para\" index=\"un_tokenized\" value-converter=\"phrase\"/><xml-property name=\"phrase.description\" xpath=\"/ting:container/docbook:article/docbook:info/docbook:abstract/docbook:para\" index=\"un_tokenized\" value-converter=\"phrase\"/><xml-property name=\"phrase.subject\" xpath=\"/ting:container/docbook:article/docbook:info/docbook:subjectset/docbook:subject/docbook:subjectterm | /ting:container/dkabm:record/dc:subject\" index=\"un_tokenized\" value-converter=\"phrase\"/><xml-property name=\"phrase.type\" xpath=\"/ting:container/dkabm:record/dc:type\" index=\"un_tokenized\" value-converter=\"phrase\"/><xml-property name=\"phrase.language\" xpath=\"/ting:container/dkabm:record/dc:language\" index=\"un_tokenized\" value-converter=\"phrase\"/><xml-property name=\"phrase.date\" xpath=\"/ting:container/dkabm:record/dc:date\" index=\"un_tokenized\" value-converter=\"phrase\"/><xml-property name=\"phrase.source\" xpath=\"/ting:container/dkabm:record/dc:source\" index=\"un_tokenized\" value-converter=\"phrase\"/><xml-property name=\"phrase.identifier\" xpath=\"/ting:container/dkabm:record/dc:identifier\" index=\"un_tokenized\" value-converter=\"phrase\"/><xml-property name=\"phrase.publisher\" xpath=\"/ting:container/dkabm:record/dc:publisher\" index=\"un_tokenized\" value-converter=\"phrase\"/><xml-property name=\"facet.creator\" xpath=\"/ting:container/dkabm:record/dc:creator[not(@xsi:type='oss:sort')] | /ting:container/dkabm:record/dc:contributor\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"facet.type\" xpath=\"/ting:container/dkabm:record/dc:type\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"facet.subject\" xpath=\"/ting:container/dkabm:record/dc:subject[not(@xsi:type='dkdcplus:DK5')]\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"facet.date\" xpath=\"/ting:container/dkabm:record/dc:date\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"facet.language\" xpath=\"/ting:container/dkabm:record/dc:language[not(@*)]\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"facet.geographic\" xpath=\"/ting:container/dkabm:record/dcterms:spatial\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"facet.period\" xpath=\"/ting:container/dkabm:record/dcterms:temporal\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"facet.fiction\" xpath=\"/ting:container/dkabm:record/dc:subject[@xsi:type='dkdcplus:DBCS']\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"facet.nonFiction\" xpath=\"/ting:container/dkabm:record/dc:subject[@xsi:type='dkdcplus:DBCF']\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"facet.music\" xpath=\"/ting:container/dkabm:record/dc:subject[@xsi:type='dkdcplus:DBCM']\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"facet.dk5\" xpath=\"/ting:container/dkabm:record/dc:subject[@xsi:type='dkdcplus:DK5']\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"sort.date\" xpath=\"/ting:container/dkabm:record/dc:date[1]\" index=\"un_tokenized\" value-converter=\"sort\"/><xml-property name=\"sort.creator\" xpath=\"/ting:container/dkabm:record/dc:creator[@xsi:type='oss:sort'][1]\" index=\"un_tokenized\" value-converter=\"sort\"/><xml-property name=\"sort.title\" xpath=\"/ting:container/dkabm:record/dc:title[1]\" index=\"un_tokenized\" value-converter=\"sort\"/><xml-property name=\"creator\" xpath=\"/ting:container/dkabm:record/dc:creator | /ting:container/dkabm:record/dc:contributor\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"date\" xpath=\"/ting:container/dkabm:record/dc:date\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"description\" xpath=\"/ting:container/dkabm:record/dc:description | /ting:container/dkabm:record/dcterms:abstract\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"format\" xpath=\"/ting:container/dkabm:record/dc:format | /ting:container/dkabm:record/dcterms:extent\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"identifier\" xpath=\"/ting:container/dkabm:record/dc:identifier\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"language\" xpath=\"/ting:container/dkabm:record/dc:language[not(@*)]\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"publisher\" xpath=\"/ting:container/dkabm:record/dc:publisher\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"source\" xpath=\"/ting:container/dkabm:record/dc:source | /ting:container/dkabm:record/dcterms:isPartOf\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"subject\" xpath=\"/ting:container/dkabm:record/dc:subject\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"title\" xpath=\"/ting:container/dkabm:record/dc:title\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"type\" xpath=\"/ting:container/dkabm:record/dc:type\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"relation\" xpath=\"/ting:container/dkabm:record/dc:relation\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property name=\"rights\" xpath=\"/ting:container/dkabm:record/dc:rights\" index=\"un_tokenized\" value-converter=\"facet\"/><xml-property xpath=\"/ting:container/ting:fedoraPid\" index=\"un_tokenized\"/><xml-property xpath=\"/ting:container/ting:fedoraNormPid\" index=\"un_tokenized\"/><xml-property xpath=\"/ting:container/ting:original_format\" index=\"un_tokenized\"/><xml-property xpath=\"/ting:container/ting:submitter\" index=\"un_tokenized\"/></xml-object></compass-core-mapping>";

    static String compasConfigFilename;
    static String cpmFilename;

    public static class MockCompassConfig {
        static public String getConfigPath()
        {
            return compasConfigFilename;
        }
        static public String getModifiedXSEMPath()
        {
            return cpmFilename;
        }
    }

    @Before
    public void setUp() throws Exception
    {
        // write example compass configuration to temporary files
        File compassConf = File.createTempFile("opensearch_unittests_CompassFactory_", "_cfg");
        compassConf.deleteOnExit();
        FileOutputStream fop = new FileOutputStream(compassConf);
        if (compassConf.exists())
        {
            fop.write(compassStr.getBytes());
            fop.flush();
            fop.close();
        }
        compasConfigFilename = compassConf.getAbsolutePath();

        File cpmFile = File.createTempFile("opensearch_unittests_CompassFactory_", ".cpm.xml");
        //cpmFile.deleteOnExit();
        fop = new FileOutputStream(cpmFile);
        if (cpmFile.exists())
        {
            fop.write(cpmData.getBytes());
            fop.flush();
            fop.close();
        }
        cpmFilename = cpmFile.getAbsolutePath();

        MockCompassConfig mockCompassConfig = new MockCompassConfig();
        Mockit.redefineMethods(CompassConfig.class, mockCompassConfig);
    }

    @Test
    public void testSingletonPattern() throws Exception {
        System.out.println("getCompass");
        CompassFactory cf = new CompassFactory();
        Compass result1 = cf.getCompass();
        Compass result2 = cf.getCompass();
        assertSame(result1, result2);
    }
}