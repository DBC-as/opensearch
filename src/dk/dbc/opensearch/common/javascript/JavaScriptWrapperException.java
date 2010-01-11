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

package dk.dbc.opensearch.common.javascript;

import dk.dbc.opensearch.common.types.OpenSearchException;

/**
 * 
 */
public class JavaScriptWrapperException extends Exception{

    static final long serialVersionUID = 5343215433265961281L;    

    /**
     * Constructor wrapping the original exception.
     *
     * Constructs the Exception with null as the message.
     *
     * @param e
     *            The originating exception
     */
    public JavaScriptWrapperException( Exception e )
    {
        super( e );
    }
    
    
    /**
     * Constructor with wrapped exception and message explaining the cause from
     * the plugin point of view.
     *
     * @param msg
     *            The message to annotate the HarvesterIOException from the Catch of
     *            the Exception e
     * @param e
     *            The original Exception that was caught
     */
    public JavaScriptWrapperException( String msg, Exception e )
    {
        super( msg, e );
    }

    
    /**
     * Constructor for creating an Exception that origins from a plugin
     *
     * @param msg
     *            The reason for the throwing of the Exception
     */
    public JavaScriptWrapperException( String msg )
    {
        super( msg );
    }
    
}
