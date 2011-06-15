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
 * \file StringUtilsTest.java
 * \brief Test of the StringUtils class
 */


package dk.dbc.opensearch.common.string;

/** \brief UnitTest for StringUtils **/

import java.util.HashMap;
import org.junit.*;
import static org.junit.Assert.*;
/**
 * 
 */
public class StringUtilsTest
{
    static HashMap<String, String> replaceMap;

    @BeforeClass
    public static void setUp() throws Exception
    {
        replaceMap = new HashMap<String, String>();
        replaceMap.put("\uA732", "AA");
        replaceMap.put("\uA733", "aa");
    }

    @AfterClass
    public static void tearDown() throws Exception
    {
        replaceMap.clear();
    }
    /**
     * Tests if replace works at the start, the end and in the middle of words
     */
    @Test public void testReplacePlacement()
    {
        String testStr1 = "\uA732TEST";
        String testStr2 = "TEST\uA732";
        String testStr3 = "TE\uA732ST";
        String testStr4 = "\uA732TEST\uA732";
        String testStr5 = "T\uA732ES\uA732T";
        assertEquals( StringUtils.replace( testStr1, replaceMap ), "AATEST" );
        assertEquals( StringUtils.replace( testStr2, replaceMap ), "TESTAA" );
        assertEquals( StringUtils.replace( testStr3, replaceMap ), "TEAAST" );
        assertEquals( StringUtils.replace( testStr4, replaceMap ), "AATESTAA" );
        assertEquals( StringUtils.replace( testStr5, replaceMap ), "TAAESAAT" );
    }

    /**
     * Tests if replace works with multiple characters in the replacemapp
     */
    @Test public void testReplaceMultipleReplacements()
    {
        String testStr1 = "\uA732TEST";
        String testStr2 = "TEST\uA733";
        String testStr3 = "\uA732TEST\uA733";
        assertEquals( StringUtils.replace( testStr1, replaceMap ), "AATEST" );
        assertEquals( StringUtils.replace( testStr2, replaceMap ), "TESTaa" );
        assertEquals( StringUtils.replace( testStr3, replaceMap ), "AATESTaa" );

    }

    /**
     * Tests if wordMatches works at the start, end and middle of String
     */
    @Test public void testWordMatchesPlacement()
    {
        String testStr1 = "\uA732TEST";
        String testStr2 = "foo bar TEST\uA732 baz";
        String testStr3 = "\uA732TEST foo bar";
        String testStr4 = "foo bar TE\uA732ST";
        String testStr5 = "foo \uA732 bar";
        assertEquals( StringUtils.wordMatches( testStr1, replaceMap ), "AATEST" );
        assertEquals( StringUtils.wordMatches( testStr2, replaceMap ), "TESTAA" );
        assertEquals( StringUtils.wordMatches( testStr3, replaceMap ), "AATEST" );
        assertEquals( StringUtils.wordMatches( testStr4, replaceMap ), "TEAAST" );
        assertEquals( StringUtils.wordMatches( testStr5, replaceMap ), "AA" );
    }

    /**
     * Tests if wordMatches works with multiple characters in the replacemap,
     */
    @Test public void testWordMatchesMultipleReplacements()
    {
        String testStr1 = "\uA732foo \uA733bar";
        String testStr2 = "foo bar \uA733TEST\uA732 baz";
        String testStr3 = "\uA733\uA732 foo bar";
        String testStr4 = "\uA732 foo\uA733";
        String testStr5 = "foo \uA732 bar";
        assertEquals( StringUtils.wordMatches( testStr1, replaceMap ), "AAfoo aabar" );
        assertEquals( StringUtils.wordMatches( testStr2, replaceMap ), "aaTESTAA" );
        assertEquals( StringUtils.wordMatches( testStr3, replaceMap ), "aaAA" );
        assertEquals( StringUtils.wordMatches( testStr4, replaceMap ), "AA fooaa" );
        assertEquals( StringUtils.wordMatches( testStr5, replaceMap ), "AA" );
    }
}
