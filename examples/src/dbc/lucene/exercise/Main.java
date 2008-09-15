/**
 * 
 */
package dbc.examples.lucene.exercise;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.queryParser.ParseException;


/**
 * @author stm
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
// 		CranfieldIndexer ci = new CranfieldIndexer( "cranfield_index" );
		if ( args.length == 0 ){
			print_usage();
			System.exit( 1 );			
		}
		
		boolean index_mode = false;
		boolean query_mode = false;
		for (int i=0;  i < args.length; i++) {
			if ( args[i].charAt(0) == '-'){
				switch (args[i].charAt( 1 )) {
				case 'q':
					query_mode = true;
					break;
				case 'f':
					index_mode = true;
					break;
				default:
					break;
				}
			}
			else{
				if( index_mode ){
                    Main.init_indexer( args[1] );
				}else if( query_mode ){
                    Main.init_searcher( args[1] );
				}
			}
		}
	}
	
    private static void init_indexer( String filename ){
        BufferedReader bufR = null;
        try{
            bufR = new BufferedReader( new FileReader( filename ) );
        }catch ( IOException ioe ){
            System.out.printf( "Could not open file %s: %s", filename, ioe.toString() );
        }
        CranfieldIndexer ci = new CranfieldIndexer( "cranfieldindex" );
        if ( bufR != null ){
            ci.indexSingleFileBuffer( bufR );
        }
    }

    private static void init_searcher( String query ){
        try {
            Searcher searcher = new IndexSearcher( "cranfieldindex" );

            CranfieldIndexer ci = new CranfieldIndexer( "cranfieldindex" );

            Hits h = ci.simpleSearch( query, searcher );

            System.out.println("Hits: " + h.length() );
            
            System.out.println( "Hits:\n" );

            // for(int i = 0; i < h.length; i++) {
            //     System.out.println( h[i].toString() );              
            // }

        } catch (IOException e) {
            System.out.println("IO Exception" + e);
        // } catch (ParseException e) {
        //     System.out.println("Parse Exception" + e);            
        }
    }
    
	private static void print_usage(){
		System.out.println( "usage: Main [-q query] [-f filetoindex]" );
	}
}
