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


package dk.dbc.opensearch.helpers;


import org.junit.Test;
import static org.junit.Assert.*;


public class CQLToLuceneQueryConverterTest {

    @Test( expected=NullPointerException.class )
    public void testNullCQL() 
    {
	String q = null;
	CQLToLuceneQueryConverter.convert( q );
    }

    @Test( expected=IllegalArgumentException.class )
    public void testSingleTermNotAllowed()
    {
	String q = "illegal";
	CQLToLuceneQueryConverter.convert( q );
    }

    @Test( expected=IllegalArgumentException.class )
    public void testMissingTerm()
    {
	String q = "illegal = ";
	CQLToLuceneQueryConverter.convert( q );
    }

    @Test( expected=IllegalArgumentException.class )
    public void testMissingQualifier()
    {
	String q = "=illegal";
	CQLToLuceneQueryConverter.convert( q );
    }

    @Test
    public void testSimpleTerm()
    {
	String q = "type=book";
	String e = "type:\"book\"";
	String res = CQLToLuceneQueryConverter.convert( q );
	assertEquals( res, e );
    }

    @Test
    public void testTermContainingWordAnd()
    {
	String q = "type=and";
	String e = "type:\"and\"";
	String res = CQLToLuceneQueryConverter.convert( q );
	assertEquals( res, e );
    }

    @Test( expected=IllegalArgumentException.class )
    public void testIllegalRelation()
    {
	String q = "type < book";
	CQLToLuceneQueryConverter.convert( q );
    }

    @Test
    public void testSimpleAndModifier()
    {
	String q = "type=book and creator=hest";
	String e = "type:\"book\" " + CQLToLuceneQueryConverter.AND_MODIFIER + " creator:\"hest\"";
	String res = CQLToLuceneQueryConverter.convert( q );
	assertEquals( res, e );
    }

    @Test
    public void testSimpleOrModifier()
    {
	String q = "type=book or creator=hest";
	String e = "type:\"book\" " + CQLToLuceneQueryConverter.OR_MODIFIER + " creator:\"hest\"";
	String res = CQLToLuceneQueryConverter.convert( q );
	assertEquals( res, e );
    }

    @Test( expected=IllegalArgumentException.class )
    public void testIllegalModifier()
    {
	String q = "type=book NOT creator=hest";
	CQLToLuceneQueryConverter.convert( q );
    }

    @Test
    public void testTermWithEqualitySign()
    {
	String q = "type=\"E = MC^2\"";
	String e = "type:\"E = MC^2\"";
	String res = CQLToLuceneQueryConverter.convert( q );
	assertEquals( res, e );
    }

    @Test
    public void testParantheses1()
    {
	String q = "a=\"A a\" and ( b = \"B b\" OR c = c )";
	String e = "a:\"A a\" " + CQLToLuceneQueryConverter.AND_MODIFIER + " ( b:\"B b\" " + CQLToLuceneQueryConverter.OR_MODIFIER + " c:\"c\" )";
	String res = CQLToLuceneQueryConverter.convert( q );
	assertEquals( res, e );
    }

    @Test
    public void testParantheses2()
    {
	String q = "(a=\"A a\" and  b = \"B b\") OR c = c";
	String e = "( a:\"A a\" " + CQLToLuceneQueryConverter.AND_MODIFIER + " b:\"B b\" ) " + CQLToLuceneQueryConverter.OR_MODIFIER + " c:\"c\"";
	String res = CQLToLuceneQueryConverter.convert( q );
	assertEquals( res, e );
    }

    @Test
    public void testParantheses3()
    {
	String q = "(a=\"A a\" and  b = \"B b\") OR ( c = c And d=\"D d d\")";
	String e = "( a:\"A a\" " + CQLToLuceneQueryConverter.AND_MODIFIER + " b:\"B b\" ) " + CQLToLuceneQueryConverter.OR_MODIFIER + " ( c:\"c\" " + CQLToLuceneQueryConverter.AND_MODIFIER + " d:\"D d d\" )";
	String res = CQLToLuceneQueryConverter.convert( q );
	assertEquals( res, e );
    }


}