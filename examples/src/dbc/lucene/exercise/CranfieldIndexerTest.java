/**
 * 
 */
package dbc.examples.lucene.exercise.tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.FileReader;

import org.junit.Before;
import org.junit.Test;

import dbc.examples.lucene.exercise.*;
/**
 * @author stm
 *
 */
public class CranfieldIndexerTest {

	private BufferedReader bufR;
	private final String fileContents = ".I 1\n" +
			".T\n" +
			"experimental investigation of the aerodynamics of a\n"+
			"wing in a slipstream\n" + 
			".A\n"+
			"brenckman,m.\n"+
			".B\n"+
			"j. ae. scs. 25, 1958, 324.\n"+
			".W\n" +
			"experimental investigation of the aerodynamics of a\n"+
			"wing in a slipstream .\n"+
			"  an experimental study of a wing in a propeller slipstream was\n"+
			"made in order to determine the spanwise distribution of the lift\n"+
			"increase due to slipstream at different angles of attack of the wing\n";
	private StringReader sr;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		sr = new StringReader( fileContents );
		//bufR = new BufferedReader( new FileReader( "lib/cran.all.1400" ) );
		bufR = new BufferedReader( sr );
				
	}

	/**
	 * Test method for {@link dbc.lucene.exercise.CranfieldIndexer#indexSingleFileBuffer(java.io.BufferedReader)}.
	 */
	@Test
	public void testIndexSingleFileBuffer() {
		long ctm1 = System.currentTimeMillis();
		CranfieldIndexer ci = new CranfieldIndexer( "cranfieldindex" );
		long ctm2 = System.currentTimeMillis();
		ci.indexSingleFileBuffer( bufR );
		long ctm3 = System.currentTimeMillis();

		System.out.printf("\n\nclass init:\t %s secs\ntotal:\t\t %s secs", (ctm2-ctm1)/1000.0, (ctm3-ctm1)/1000.0);
	}

	/**
	 * Test method for {@link dbc.lucene.exercise.CranfieldIndexer#CranfieldIndexer(java.lang.String)}.
	 */
	@Test
	public void testCranfieldIndexer() {
		//fail("Not yet implemented"); // TODO
	}

}
