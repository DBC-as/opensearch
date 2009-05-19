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


package dk.dbc.opensearch.tools.testindexer;


import dk.dbc.opensearch.common.compass.CompassFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.io.File;

import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.compass.core.Compass;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassEnvironment;
import org.compass.core.converter.mapping.xsem.XmlContentMappingConverter;
import org.compass.core.xml.dom4j.converter.SAXReaderXmlContentConverter;


public class IndexerMain{

    static public void main(String[] args) throws ConfigurationException, MalformedURLException, ServiceException, IOException
    {

        
        URI job;
        String submitter;
        String format;
        URL mappingFile;
        String indexDir;
        
        // test instances
        job  = new File("/home/shm/opensearch/kode/trunk/dist/artb9.xml").toURI();
        submitter = "dbc";
        format = "artikler";
        mappingFile = new File("/home/shm/opensearch/kode/trunk/dist/xml.cpm.xml").toURL();
        indexDir = "/home/shm/opensearch/kode/trunk/INDEXXX";

        // the indexer
        Indexer indexer = new Indexer();
        try{
            indexer.index( job, submitter, format, mappingFile, indexDir );
        }catch(Exception e){
            System.err.println( "Caught error: " + e.getMessage() );
            e.printStackTrace();
            System.exit(1);
        }
        
        System.out.println( "Indexing done");    
    }
}