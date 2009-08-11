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
 * \file KeywordLowerAnalyzer.java
 * \brief Used for lowercasing terms
 */


package dk.dbc.opensearch.common.compass;


import java.io.Reader;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;


public class KeywordLowerAnalyzer extends Analyzer
{

    Logger log = Logger.getLogger( KeywordLowerAnalyzer.class );

    public KeywordLowerAnalyzer() {}

    public TokenStream tokenStream( String fieldName, Reader reader )
    {        
        TokenStream result = new KeywordTokenizer( reader );
        result  = new LowerCaseFilter( result );
        return result;
    }
}
