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
 * 
 */
package dk.dbc.opensearch.config;


import org.apache.commons.configuration.ConfigurationException;


/**
 * Sub class of Config providing access to file system settings in the 
 * configuration file. Method names should be explanatory enough.
 * 
 * See super class Config for description of methodology.
 *
 */
public class FileSystemConfig extends Config
{
    public FileSystemConfig() throws ConfigurationException 
    {
        super();
    }


    /* Ensuring a '/' at the end of path */
    private String sanitize( String path )
    {
        if ( path.endsWith( "/" ) )
            return path;
        else
            return path + "/";
    }
	
	
    /* TRUNK */
    private String getFileSystemTrunkPath()
    {
        String ret = config.getString( "filesystem.trunk" );
        return sanitize( ret );
    }
	
	
    public static String getTrunkPath() throws ConfigurationException 
    {
        FileSystemConfig f = new FileSystemConfig();
        return f.getFileSystemTrunkPath();
    } 
	
	
    /* CONFIG PATH */
    public static String getConfigPath() throws ConfigurationException
    {
        FileSystemConfig f = new FileSystemConfig();
        String ret = f.getFileSystemTrunkPath();
	
        return ret + "config/"; 
    }
	
    
    public static String getScriptPath() throws ConfigurationException 
    {
        FileSystemConfig f = new FileSystemConfig();
        String ret = f.getFileSystemTrunkPath();
    
        return ret + "scripts/";                
    }
	
    /* PLUGINS */
    private String getFileSystemPluginsPath()
    {
        String ret = config.getString( "filesystem.plugins" );
        return sanitize( ret );
    }
	
    public static String getPluginsPath() throws ConfigurationException
    {		
        FileSystemConfig fc = new FileSystemConfig();		
        return fc.getFileSystemPluginsPath();
    }
	
	/* JOBS DATADOCK XSD FILE */
    private String getFileSystemDatadockJobsXsdPath()
    {
        String ret = config.getString( "filesystem.datadock-jobs-xsd" );
        return sanitize( ret );
    }
    
    public static String getDataDockJobsXsdPath() throws ConfigurationException
    {		
        FileSystemConfig fc = new FileSystemConfig();		
        return fc.getFileSystemDatadockJobsXsdPath();
    }

	/* JOBS PTI XSD FILE */
    private String getFileSystemPTIJobsXsdPath()
    {
        String ret = config.getString( "filesystem.pti-jobs-xsd" );
        return sanitize( ret );
    }

    public static String getPTIJobsXsdPath() throws ConfigurationException
    {
        FileSystemConfig fc = new FileSystemConfig();
        return fc.getFileSystemPTIJobsXsdPath();
    }
}
