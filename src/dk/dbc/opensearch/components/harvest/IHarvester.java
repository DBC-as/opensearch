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

package dk.dbc.opensearch.components.harvest;


import dk.dbc.opensearch.common.types.IJob;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import org.apache.commons.configuration.ConfigurationException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Harvester interface. Harvester is the interface for the datadock
 * harvester service. The harvester is an eventdriven component - and
 * three methods need to be implemented. After construction of the
 * harvester - the start method is called - so all startup logic
 * should be placed here. When the datadock is up and running, it will
 * call the getJobs method at intervals until the shutdown method is
 * called.
 */
public interface IHarvester
{
    /**
     * The start method. Called by the datadock just after
     * construction of the instance.
     */
    void start() throws ParserConfigurationException, SAXException, IOException;
    
    
    /**
     * The shutdown method. Called by the datadock when closing down
     * the harvester.
     */
    void shutdown();
    
    
    /**
     * The getJobs method. Called consecutively by the datadock when
     * it is up and running.
     * 
     * @return getJobs Returns a vector of DatadockJobs - representing
     * the new jobs registered since the last call to this method.
     * @throws IOException 
     * @throws FileNotFoundException 
     * @throws ConfigurationException 
     */
    Vector< IJob > getJobs() throws FileNotFoundException, IOException, ConfigurationException;
}
