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

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.z3950.zing.cql.CQLAndNode;
import org.z3950.zing.cql.CQLBooleanNode;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLOrNode;
import org.z3950.zing.cql.CQLParseException;
import org.z3950.zing.cql.CQLParser;
import org.z3950.zing.cql.CQLRelation;
import org.z3950.zing.cql.CQLTermNode;

/**
 * This class handles the conversion of a subset of CQL-query to a Lucene-query.
 * <p>
 * The legal subset of CQL is:
 * <ul>
 * <li>The relation operator must be <TT>=</TT> (the equal sign).</li>
 * <li>Grouping using balanced parantheses.</li>
 * <li>Boolean operators: <TT>AND</TT> and <TT>OR</TT>. 
 * </ul>
 * The following query is legal:
 * <br>
 * <TT>title="Alice i eventyrland" AND ( type=dvd OR type="bog" )</TT>
 * <p>
 * For further information regarding CQL syntax please see: 
 * <a href="http://www.loc.gov/standards/sru/specs/cql.html">CQL Syntax</a>
 * <p>
 * For further information about CQL-Java please see:
 * <a href="http://zing.z3950.org/cql/java/index.html">CQL-Java</a>
 */
public class CQLToLuceneQueryConverter {

    public static final String AND_MODIFIER = "AND";
    public static final String OR_MODIFIER = "OR";


    /**
     * Function to handle the parsing. The CQL-query must correspond to 
     * the above stated specification.
     *
     * @param cql String containing a CQL-query
     * @return String containing the corresponding Lucene-query.
     * @throws IllegalArgumentException if <TT>cql</TT> cannot be parsed.
     * @throws NullPointerException if <TT>cql</TT> is <TT>null</TT>.
     */
    public static String convert( String cql ) {
	CQLParser p = new CQLParser();
	CQLNode root;
	try {
	    root = p.parse( cql );
	} catch ( CQLParseException e ) {
	    String errMsg = String.format( "Could not parse CQL [%s]", cql );
	    throw new IllegalArgumentException( errMsg, e );
	} catch ( IOException e ) {
	    String errMsg = String.format( "Could not parse CQL [%s]", cql );
	    throw new IllegalArgumentException( errMsg, e );
	}
	return convertCQLToLucene( root, 0, true );
    }

    
    private static String convertCQLToLucene( CQLNode node, int depth, boolean is_left_child ) {
	
	String res = "";
	if ( node instanceof CQLBooleanNode ) {
	    CQLBooleanNode booleanNode = (CQLBooleanNode)node;
	    String left = convertCQLToLucene( booleanNode.left, depth + 1 , true );
	    String right = convertCQLToLucene( booleanNode.right, depth + 1 , false );
	    // Note:
	    // The easy thing to do to get the modifier would be to just look in the field 'ms'
	    // in the CQLBooleanNode. But since the version is 0.7, this field is not there.
	    // Instead we will look at BooleanNode-types, since we only support AND and OR.
	    String modifier = "";
 	    if ( booleanNode instanceof CQLAndNode ) {
		modifier = AND_MODIFIER;
	    } else if ( booleanNode instanceof CQLOrNode ) {
		modifier = OR_MODIFIER;
	    } else {
		throw new IllegalArgumentException( String.format( "Unknown modifier in term: [%s]",
								   booleanNode.toCQL() ) );
	    }
	    res = left + " " + modifier + " " + right;
	}

	if ( node instanceof CQLTermNode ) {
	    CQLTermNode termNode = (CQLTermNode)node;
	    if ( is_left_child & depth > 1 ) {
		res += "( ";
	    }
	    String rel = convertRelation( termNode.getRelation() );
	    res += termNode.getQualifier() + rel + "\"" + termNode.getTerm() + "\"";
	    if ( !is_left_child & depth > 1 ) {
		res += " )";
	    }
	}

	return res;
    } 

    // Converts the relation "=" to ":".
    // Throws an exception if the relation is not "=".
    private static String convertRelation( CQLRelation relation ) {
	String rel = relation.getBase();
	if ( ! rel.equals( "=" ) ) {
	    throw new IllegalArgumentException( String.format( "The relation [%s] is illegal.", rel ) );
	}
	return ":";
    }
    

}