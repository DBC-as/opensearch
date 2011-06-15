/**
 * 
 */
package dk.dbc.opensearch.config;

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

import org.apache.commons.configuration.ConfigurationException;


/**
 * @author mro
 * 
 * Sub class of Config providing access to pid manager settings in the 
 * configuration file. Method names should be explanatory enough.
 * 
 * See super class Config for description of methodology.
 *
 */
public class PidManagerConfig extends Config
{
	public PidManagerConfig() throws ConfigurationException 
	{
		super();
	}


	/* NUMBER OF PIDS TO RETRIEVE */
	private int getDCIdentifierManagerNumberOfPidsToRetrieve()
	{
		int ret = config.getInt( "pidmanager.num-of-pids-to-retrieve" );
		return ret;
	}
	
	
	public static int getNumberOfPidsToRetrieve() throws ConfigurationException
	{
		PidManagerConfig pmc = new PidManagerConfig();
		return pmc.getDCIdentifierManagerNumberOfPidsToRetrieve();
	} 
}
