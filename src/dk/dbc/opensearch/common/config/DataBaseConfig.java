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
 * \file DataBaseConfig.java
 * \brief
 */


package dk.dbc.opensearch.common.config;


import org.apache.commons.configuration.ConfigurationException;


/**
 * Sub class of Config providing access to database settings in the
 * configuration file. Method names should be explanatory enough.
 *
 * See super class Config for description of methodology.
 *
 */
public class DataBaseConfig extends Config
{
    public DataBaseConfig() throws ConfigurationException
    {
        super();
        log.trace( "DataBaseConfig constructor called" );
    }


    /* POSTGRESQL_DRIVER */
    private String getDataBasePostgresqlDriver()
    {
        String ret = config.getString( "database.postgresql_driver" );
        return ret;
    }


    public static String getPostgresqlDriver() throws ConfigurationException
    {
        DataBaseConfig dbc = new DataBaseConfig();
        return dbc.getDataBasePostgresqlDriver();
    }


    /* POSTGRESQL_URL */
    private String getDataBasePostgresqlUrl()
    {
        String ret = config.getString( "database.postgresql_url" );
        return ret;
    }


    public static String getPostgresqlUrl() throws ConfigurationException
    {
        DataBaseConfig dbc = new DataBaseConfig();
        return dbc.getDataBasePostgresqlUrl();
    }


    /* ORACLE_DRIVER */
    private String getDataBaseOracleDriver()
    {
        String ret = config.getString( "database.oracle_driver" );
        return ret;
    }


    public static String getOracleDriver() throws ConfigurationException
    {
        DataBaseConfig dbc = new DataBaseConfig();
        return dbc.getDataBaseOracleDriver();
    }


    /* ORACLE_URL */
    private String getDataBaseOracleUrl()
    {
        String ret = config.getString( "database.oracle_url" );
        return ret;
    }


    public static String getOracleUrl() throws ConfigurationException
    {
        DataBaseConfig dbc = new DataBaseConfig();
        return dbc.getDataBaseOracleUrl();
    }
    
    /* ORACLE_DATABASE_NAME */
    private String getDatabaseOracleDataBaseName()
    {
        String ret = config.getString( "database.oracle_database_name" );
        return ret;
    }

    public static String getOracleDataBaseName() throws ConfigurationException
    {
        DataBaseConfig dbc = new DataBaseConfig();
        return dbc.getDatabaseOracleDataBaseName();
    }

    /* ORACLE_CACHE_NAME */
    private String getDataBaseOracleCacheName()
    {
        String ret = config.getString( "database.oracle_cache_name" );
        return ret;
    }

    public static String getOracleCacheName() throws ConfigurationException
    {
        DataBaseConfig dbc = new DataBaseConfig();
        return dbc.getDataBaseOracleCacheName();
    }

    /* ORACLE_MIN_LIMIT */
    private String getDataBaseOracleMinLimit()
    {
        String ret = config.getString( "database.oracle_min_limit" );
        return ret;
    }

    public static String getOracleMinLimit() throws ConfigurationException
    {
        DataBaseConfig dbc = new DataBaseConfig();
            return dbc.getDataBaseOracleMinLimit();
    }

    /* ORACLE_MAX_LIMIT */
    private String getDataBaseOracleMaxLimit()
    {
        String ret = config.getString( "database.oracle_max_limit" );
        return ret;
    }

    public static String getOracleMaxLimit() throws ConfigurationException
    {
        DataBaseConfig dbc = new DataBaseConfig();
            return dbc.getDataBaseOracleMaxLimit();
    }

    /* ORACLE_INITIAL_LIMIT */
    private String getDataBaseOracleInitialLimit()
    {
        String ret = config.getString( "database.oracle_initial_limit" );
        return ret;
    }

    public static String getOracleInitialLimit() throws ConfigurationException
    {
        DataBaseConfig dbc = new DataBaseConfig();
        return dbc.getDataBaseOracleInitialLimit();
    }

    /* ORACLE_CONNECTION_WAIT_TIMEOUT */
    private String getDataBaseOracleConnectionWaitTimeout()
    {
        String ret = config.getString( "database.oracle_connection_wait_timeout" );
        return ret;
    }

    public static String getOracleConnectionWaitTimeout() throws ConfigurationException
    {
        DataBaseConfig dbc = new DataBaseConfig();
        return dbc.getDataBaseOracleConnectionWaitTimeout();
    }

    /* ORACLE_USERID */
    private String getDataBaseOracleUserID()
    {
        String ret = config.getString( "database.oracle_userID" );
        return ret;
    }


    public static String getOracleUserID() throws ConfigurationException
    {
        DataBaseConfig dbc = new DataBaseConfig();
        return dbc.getDataBaseOracleUserID();
    }


    /* ORACLE_PASSWD */
    private String getDataBaseOraclePassWd()
    {
        String ret = config.getString( "database.oracle_passwd" );
        return ret;
    }


    public static String getOraclePassWd() throws ConfigurationException
    {
        DataBaseConfig dbc = new DataBaseConfig();
        return dbc.getDataBaseOraclePassWd();
    }

    /* POSTGRESQL_USERID */
    private String getDataBasePostgresqlUserID()
    {
        String ret = config.getString( "database.postgresql_userID" );
        return ret;
    }


    public static String getPostgresqlUserID() throws ConfigurationException
    {
        DataBaseConfig dbc = new DataBaseConfig();
        return dbc.getDataBasePostgresqlUserID();
    }


    /* POSTGRESQL_PASSWD */
    private String getDataBasePostgresqlPassWd()
    {
        String ret = config.getString( "database.postgresql_passwd" );
        return ret;
    }


    public static String getPostgresqlPassWd() throws ConfigurationException
    {
        DataBaseConfig dbc = new DataBaseConfig();
        return dbc.getDataBasePostgresqlPassWd();
    }
}
