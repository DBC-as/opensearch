/**
 * \file PluginResolverException 
 * \brief
 * \package pluginframework
 */
package dk.dbc.opensearch.common.pluginframework;

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


import dk.dbc.opensearch.common.types.ThrownInfo;
import java.util.Vector;


/**
 * This class is a custom Exception for handling the many exceptions that can be 
 * thrown from the PluginResolvers components that not nessecarily should halt the 
 * executing. It therefore contains a Vector of <ThrownInfo> 
 * that can be examined where the PluginResolver is being called from.
 * To get the class of the Exception from the Throwable object call getClass()
 * The info from the ThrownInfo tells what object the exception is concerned 
 * with or caused by.     
 */
public class PluginResolverException extends Exception 
{
    Vector<ThrownInfo> exceptionVector;
    String message;

    
    /**
     * @param exceptionVector is the Vector containing the ThrownInfos, that each 
     * contains a Throwable and aditional information.
     * @param message is the general message about the Exception, stating
     * what the collection of Throwables are regarding. 
     */
    public PluginResolverException( Vector<ThrownInfo> exceptionVector, String message ) 
    {
        this.exceptionVector = exceptionVector;
        this.message = message;
    }

    
    /**
     * Constructor for sending a single message when the flow of the 
     * PluginResolvers components is as expected and no Exceptions where 
     * caused, but there are values that are not computed or retrived as 
     * expected. The exceptionVector will be null when the exception is 
     * constructed this way.
     * @param message is the general message about the Exception, stating 
     * what the collection of Throwables are regarding. 
     */
    public PluginResolverException( String message ) 
    {
        this.exceptionVector = null;
        this.message = message;
    }

    
    /**
     * The standard method for retrieving the overall information about 
     * the Exception.  
     * @return String, the overall information about the Exception.
     */
    public String getMessage()
    {
        return message;
    }

    
    /**
     * The method for retrieving the Vector containing the Throwables and 
     * eachs paired information. The returned Vector should allways be 
     * checked for being null before used.
     * @return Vector<ThrownInfo> the Vector with the Throwables 
     * and information about them.
     */
    public Vector<ThrownInfo> getExceptionVector()
    {
        return exceptionVector;
    }
}