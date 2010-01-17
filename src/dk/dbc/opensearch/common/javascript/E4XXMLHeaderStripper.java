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


package dk.dbc.opensearch.common.javascript;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

public class  E4XXMLHeaderStripper 
{

    private static Logger log = Logger.getLogger( E4XXMLHeaderStripper.class );
    
    private static final Pattern p = Pattern.compile( "<[?]xml[^>]*[?]>\\s*" );


    public static byte[] strip( byte[] XML )
    {
	return strip( new String( XML ) ).getBytes();
    }


    public static String strip( String XML )
    {
	log.trace( "Before: " + XML );

	// Hmmmm...... These seem not to work properly:	
	// String newXML = XML.replace( "<[?]xml[^>]*[?]>\\s*", "" );
	// String newXML = XML.replace( "<?xml[^>]*?>", "" );

	// Instead I'll use Pattern and Matcher from java.util.regex:
	Matcher m = p.matcher( XML );
	String newXML = m.replaceFirst( "" );

	log.trace( "After: " + newXML ) ;

	return newXML;
    } 


}