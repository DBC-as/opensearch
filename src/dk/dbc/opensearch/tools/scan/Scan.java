/**
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


package dk.dbc.opensearch.tools.scan;


import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.apache.log4j.Logger;

import dk.dbc.opensearch.common.types.ImmutablePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class Scan {

    static Logger log = Logger.getLogger( Scan.class );

    
    /** 
     * Returns a List of Pairs of Strings and Integers that represents
     * terms bzw. number of occurences of that term in a given field
     * in an index.
     * 
     * @param index the index directory to look in. Must be a valid path on a local disk 
     * @param field the name of the field. This method uses the Term qualified TermEnum returned by http://hudson.zones.apache.org/hudson/job/Lucene-trunk/javadoc//org/apache/lucene/index/IndexReader.html#terms(org.apache.lucene.index.Term)
     * @param term the term to scan for occurences for
     * @param prefix optional term prefix. If given, the method limits terms 
     * 
     * @return A List of pairs of strings and integers
     */
    public static synchronized List< ImmutablePair< String, Integer> > termScan( String index, 
                                                                             String field, 
                                                                             String term, 
                                                                             String prefix ) throws IOException
    {
    	List< ImmutablePair< String, Integer > > scanResult = new ArrayList< ImmutablePair< String, Integer > >(); 
        IndexReader r = IndexReader.open( index );

        log.debug( String.format( " Looking for field '%s', term '%s'", field, term ) );

        TermEnum te = r.terms( new Term( field, term ) );

        log.debug( String.format( "Found field '%s', closest term '%s'", te.term().field(), te.term().text() ) );

        do {
            Term t = te.term();

            // do not register the term if the term was null or the
            // field of the term did not match the query field
            if ( null == t || ! t.field().equals( field ) )
            {
                break;
            }

            // do not register the term if prefix was given and the
            // term does not start with prefix
            if ( prefix != null && ! t.text().startsWith( prefix ) )
            {
                break;
            }

            Integer tf = te.docFreq();
            
            scanResult.add( new ImmutablePair< String, Integer>( t.text(), tf ) );
      
        } while ( te.next() );

        te.close();
        r.close();
        return scanResult;
        
    }
}

