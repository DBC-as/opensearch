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
 * \file StringUtils.java
 * \brief Contains methods for string manipulation
 */

package dk.dbc.commons.string;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * Contains methods for string manipulation
 */
public class StringUtils
{
    static Logger log = Logger.getLogger(StringUtils.class);

    /**
     * Tests whether one of the String keys form the replaceMap is contained in the String str
     * @param str The string to search
     * @param replaceMap The map to get matchkeys from
     * @return true if str contains String key from replaceMap
     */
    public static Boolean contains( String str, HashMap<String, String> replaceMap )
    {
        String regexp = "";
        for ( String key : replaceMap.keySet() )
        {
            regexp += String.format( "(%s)|", key );
        }
        regexp = regexp.substring( 0, regexp.length() - 1 );

        Pattern pattern = Pattern.compile( regexp );
        Matcher matcher = pattern.matcher( str );
        boolean found = false;
        while( matcher.find() )
        {
            found = true;
            break;
        }
        return found;
    }

    /**
     * Replaces occurences of the replaceMap keys in str with the corresponding values in replaceMap.
     * @param str The String to replace characters in
     * @param replaceMap a map where keys are the chars to replace and the value the replacement charecter
     * @return String with replaced characters
     */
    public static String replace( String str, HashMap<String, String> replaceMap )
    {
        // Building regexp
        String regexp = "";
        for ( String key : replaceMap.keySet() )
        {
            regexp += String.format( "(%s)|", key );
        }
        regexp = regexp.substring( 0, regexp.length() - 1 );

        Pattern pattern = Pattern.compile( regexp );
        Matcher matcher = pattern.matcher( str );

        StringBuffer sb = new StringBuffer();
        while( matcher.find() )
        {
            matcher.appendReplacement( sb, replaceMap.get( matcher.group() ) );
        }
        matcher.appendTail( sb );

        return sb.toString();
    }


    /**
     * Finds and returns a string of words containing the keys from the str.
     * The returned words is normalized with replaceMap.
     * @param str The String to identify words from
     * @param replaceMap a map where keys are the chars to replace and the value the replacement character
     * @return normalized string with words containing the keys from str.
     */
    public static String wordMatches(String str, HashMap<String, String> replaceMap)
    {
        // Build regexp
        String regexp = "";
        for ( String key : replaceMap.keySet() )
        {
            regexp += String.format("(([\\S&&[^\\p{Punct}]]*)(%s)([\\S&&[^\\p{Punct}]]*))+|", key);
        }
        regexp = regexp.substring( 0, regexp.length() - 1 );

        Pattern pattern = Pattern.compile( regexp );
        Matcher matcher = pattern.matcher( str );
        HashSet<String> matchSet = new HashSet<String>();

        while (matcher.find())
        {
            matchSet.add( matcher.group() );
        }

        String retStr = "";
        Iterator<String> iter = matchSet.iterator();
        while( iter.hasNext() )
        {
            retStr += String.format( "%s ", iter.next() );
        }
        return replace(retStr, replaceMap).trim();
    }


    public static String normalizeString(String s)
    {
        s = s.toLowerCase();
        String killchars = "~'-";

        StringBuffer res = new StringBuffer();
        for (int i = 0; i < s.length(); ++i)
        {
            if (-1 == killchars.indexOf(s.charAt(i)))
            {
                res.append(s.charAt(i));
            }
        }

        return res.toString();
    }
}
