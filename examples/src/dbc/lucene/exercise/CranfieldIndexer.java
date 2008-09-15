/**
 * 
 */
package dbc.examples.lucene.exercise;


import java.io.BufferedReader;
import java.io.IOException;
//import java.io.File;

import org.apache.lucene.index.IndexWriter;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Hits;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryParser.ParseException;
/**
 * @author stm
 *
 */
public class CranfieldIndexer {

	//
	private IndexWriter indexWriter;

	//abstract class to hold the instance analyzer object
	private Analyzer analyzer;
	// string holding the information of the index-files, which will be filebased
	private String index_directory;

    private Query query;
	public void indexSingleFileBuffer( BufferedReader bufIn ){
		try{
			String in = bufIn.readLine();
			String prev = in;

			Document doc = new Document();
// 			System.out.println( in + '\n' + prev );
			while ( in != null ){
				in = bufIn.readLine();
//				System.out.printf("in.startsWith( \".\" ) == %b, prev.startsWith( \".\" ) == %b\n", in.startsWith( "." ), prev.startsWith( "." ));
				if( prev.startsWith( "." ) && !in.startsWith( "." )){
					char C = prev.charAt( 1 );
					StringBuffer holder = new StringBuffer();
					while ( in != null && ! in.startsWith( "." ) ){
//                         System.out.print( "." );
						// we preserve linebreaks. Not sure if this should be so...
						holder.append( in+"\n" );
						in = bufIn.readLine();
					}
                    //System.out.printf( "Capacity = %s, Size = %s\n", holder.capacity(), holder.length() );
                    //System.out.printf( "Overhead = %.2f %% \n", (float)holder.length()/(float) holder.capacity()*100);
//                    holder.trimToSize();
//                    System.out.printf( "Capacity = %s, Size = %s\n", holder.capacity(), holder.length() );
                    
					switch (C) {
					//T is abstract
					case 'T':
						doc.add( new Field( CranfieldDocumentStruct.FieldName.ABSTRACT.toString(), holder.toString(), Field.Store.YES, Field.Index.TOKENIZED ) );
					
						break;
					//A is Author
					case 'A':
						doc.add( new Field( CranfieldDocumentStruct.FieldName.AUTHOR.toString(), holder.toString(), Field.Store.YES, Field.Index.TOKENIZED ) );
						
						break;
						
					//B is publication information
					case 'B':
						doc.add( new Field( CranfieldDocumentStruct.FieldName.PUB.toString(), holder.toString(), Field.Store.YES, Field.Index.TOKENIZED ) );
						
						break;
						
					//W is text
					case 'W':
						doc.add( new Field( CranfieldDocumentStruct.FieldName.TEXT.toString(), holder.toString(), Field.Store.YES, Field.Index.TOKENIZED ) );
						
						break;
					default:
						break;
					}
				}
				prev = in;
				in = bufIn.readLine();					
				
			}
			if ( doc != null ){
				this.indexWriter.addDocument( doc );
				this.indexWriter.close();
			}
		}catch( IOException ioe ){
			System.out.printf( "%s", ioe.toString() );
		}
	}//end indexSingleFileBuffer


    /**
     * simpleSearch conducts a simple search (heh), provided a search
     * term and an instance of a Searcher
     */
    public Hits simpleSearch( String queryString, Searcher searcher) throws IOException{

        // we only search in the fulltext field
        QueryParser qp = new QueryParser(CranfieldDocumentStruct.FieldName.TEXT.toString(), analyzer);

        try{
            // construct the query
            query = qp.parse( queryString );
        } catch (ParseException e) {
            System.out.println("Parse Exception" + e);            
        }
        //hopefully get the hits and return them
        return searcher.search( query );
        
    }
	
	/**
	 * Default constructor, using a directory
	 * @param directory: the directory in which the indexes should be stored
	 */
	public CranfieldIndexer( String directory) throws NullPointerException{
		// using the standard analyzer
		analyzer = new StandardAnalyzer();

		if ( directory != null ){
			index_directory = directory;
		}else{
			throw new NullPointerException( "A Directory was expected, null was recieved" );
		}
		try{
			indexWriter = new IndexWriter( index_directory, analyzer, true );
		}catch( IOException ioe ){
			System.out.printf( "There was an error in constructing the IndexWriter, possibly the directory permissions are not ok:\n %s ", ioe.toString() );
		}

	}
}
