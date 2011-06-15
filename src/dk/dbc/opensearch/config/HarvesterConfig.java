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
 * \file
 * \brief
 */


package dk.dbc.opensearch.config;


import org.apache.commons.configuration.ConfigurationException;


/**
 * Sub class of Config providing access to harvester settings in the 
 * configuration file. Method names should be explanatory enough.
 * 
 * See super class Config for description of methodology.
 *
 */
public class HarvesterConfig extends Config
{
    public HarvesterConfig() throws ConfigurationException 
    {
        super();
    }


    /* FOLDER */
    private String getHarvesterFolder()
    {
        String ret = config.getString( "harvester.toharvest" );
        return ret;
    }
	
	
    public static String getFolder() throws ConfigurationException 
    {
        HarvesterConfig hc = new HarvesterConfig();
        return hc.getHarvesterFolder();
    }
	
	
    /* PROGRESS FOLDER */
    private String getHarvesterProgressFolder()
    {
        String ret = config.getString( "harvester.harvestprogress" );
        return ret;
    }


    public static String getProgressFolder() throws ConfigurationException
    {
        HarvesterConfig hc = new HarvesterConfig();
        return hc.getHarvesterProgressFolder();
    }


    /* DONE FOLDER */
    private String getHarvesterDoneFolder()
    {
        String ret = config.getString( "harvester.harvestdone" );
        return ret;
    }
    
	
    public static String getDoneFolder() throws ConfigurationException 
    {
        HarvesterConfig hc = new HarvesterConfig();
        return hc.getHarvesterDoneFolder();
    }
    
	
    /* FAILURE FOLDER */
    private String getHarvesterFailureFolder()
    {
        String ret = config.getString( "harvester.harvestfailure" );
        return ret;
    }


    public static String getFailureFolder() throws ConfigurationException
    {
        HarvesterConfig hc = new HarvesterConfig();
        return hc.getHarvesterFailureFolder();
    }


    /* MAX TO HARVEST */
    private int getHarvesterMaxToHarvest()
    {
        int ret = config.getInt( "harvester.maxtoharvest" );
        return ret;
    }
    
    
    public static int getMaxToHarvest() throws ConfigurationException 
    {
        HarvesterConfig hc = new HarvesterConfig();
        return hc.getHarvesterMaxToHarvest();
    }


    /* Use priority field */
    private boolean getHarvesterPriorityFlag()
    {
        boolean ret = config.getBoolean( "harvester.usepriorityfield", false );
        return ret;
    }


    public static boolean getPriorityFlag() throws ConfigurationException
    {
        HarvesterConfig hc = new HarvesterConfig();
        return hc.getHarvesterPriorityFlag();
    }
}
