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


package dk.dbc.opensearch.config;


import org.apache.commons.configuration.ConfigurationException;


/**
 * Sub class of Config providing access to fedora settings in the 
 * configuration file. Method names should be explanatory enough.
 * 
 * See super class Config for description of methodology.
 */
public class FedoraConfig extends Config
{
    public FedoraConfig() throws ConfigurationException 
    {
        super();
    }


    /* HOST */
    private String getFedoraHost()
    {
        String ret = config.getString( "fedora.host" );
        return ret;
    }
    
	
    public static String getHost() throws ConfigurationException 
    {
        FedoraConfig fc = new FedoraConfig();		
        return fc.getFedoraHost();
    }
    
    
    /* PORT */
    private String getFedoraPort()
    {
        String ret = config.getString( "fedora.port" );
        return ret;
    }
    
    
    public static String getPort() throws ConfigurationException
    {
        FedoraConfig fc = new FedoraConfig();
        return fc.getFedoraPort();
    }
    
    
    /* USER */
    private String getFedoraUser()
    {
        String ret = config.getString( "fedora.user" );
        return ret;
    }
    
    
    public static String getUser() throws ConfigurationException
    {
        FedoraConfig fc = new FedoraConfig();
        return fc.getFedoraUser();
    }
    
    
    /* PASS PHRASE */
    private String getFedoraPassPhrase()
    {
        String ret = config.getString( "fedora.passphrase" );
        return ret;
    }
    
	
    public static String getPassPhrase() throws ConfigurationException
    {
        FedoraConfig fc = new FedoraConfig();
        return fc.getFedoraPassPhrase();
    }
}
