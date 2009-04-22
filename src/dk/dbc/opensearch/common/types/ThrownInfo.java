package dk.dbc.opensearch.common.types;

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
 * \brief A class for handling throwables and additional information 
 * \package types
 * This is a class for wrapping and transporting an exception that need to be 
 * handed up through the system with some additional information than the 
 * stacktrace and message.
 * This class is made to facilitate the need of the 
 * dk.dbc.opensearch.common.pluignframework.PluginResolverException class to 
 * have a containerclass for a Throwable and some additional information.
 */
public class ThrownInfo {
    Throwable theThrown;
    String info;
    
    /**
     * the public constructor that sets the fields
     * @param theThrown, the Throwable that shall be contained
     * @param info, the additional info about the Throwable
     */
    public ThrownInfo( Throwable theThrown , String info ) {
    
        this.theThrown = theThrown;
        this.info = info; 
  }
    /**
     * returns the Throwable
     * @return theThrown
     */
    public Throwable getThrowable(){
        return theThrown;
    } 
    /**
     * returns the additional information about the Throwable
     * @return info
     */
    public String getInfo(){
        return info;
    }
}