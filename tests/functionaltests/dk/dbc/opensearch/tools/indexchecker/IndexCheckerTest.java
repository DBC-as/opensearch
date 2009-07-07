/**
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


package dk.dbc.opensearch.tools.indexchecker;

/** \brief UnitTest for indexChecker **/


import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.components.datadock.DatadockJob;
import dk.dbc.opensearch.tools.readindex.ReadIndex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ClassNotFoundException;
import java.lang.InterruptedException;
import java.lang.StringBuilder;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.*;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;
import dk.dbc.opensearch.components.datadock.DatadockJobsMap;
import java.lang.NoSuchFieldException;
import java.lang.IllegalAccessException;
import java.net.URL;
import dk.dbc.opensearch.common.config.FileSystemConfig;

/**
 *
 */
public class IndexCheckerTest
{

    /**
     *
     */

    @Test public void testindexes() throws ClassNotFoundException, ConfigurationException, ExecutionException, FileNotFoundException, InterruptedException, IOException, MalformedURLException, ParserConfigurationException, PluginResolverException, SAXException, ServiceException, TransformerConfigurationException, TransformerException, URISyntaxException, NoSuchFieldException, IllegalAccessException
    {
        System.out.println( "IndexChecker running" );

        String trunk = FileSystemConfig.getTrunkPath();
        File testFolder = new File( new File( trunk, "tests"), "testdata" ); 

        IndexChecker indexChecker = new IndexChecker();
        assertTrue( indexChecker.runTests( testFolder ) );
    }
}




