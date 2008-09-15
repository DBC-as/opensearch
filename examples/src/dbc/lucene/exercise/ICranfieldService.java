/**
 * 
 */
package dbc.examples.lucene.exercise;

import java.util.regex.*;
/**
 * @author stm
 * @see dbc.lucene.exercise.CranfieldIndexer for more background frizzle
 * This interfaces describes all the public accessible methods of the Cranfield Indexer Service. 
 *  
 */
public interface ICranfieldService {

	/**
	 * GetSubstringCount returns, given a string, an int representing the count of substrings that match the provided match string. 
	 * @param instring is a String object containing text to be matched with the match String
	 * @param match is a String object containing the text that should be matched in the instring String.
	 * @return An integer specifying how many matches of the String match the String instring contained.
	 */
	public int GetSubstringCount( String instring, String match);
	
	/**
	 * This version of GetSubstring accepts a regular expression and matches that against the provided string, returning the number of hits of the regular expression, that the String contained.
	 * @param instring
	 * @param regex
	 * @return An integer specifying how many matches of the regular expression the provided string contained. 
	 */
	public int GetSubstring( String instring, Pattern regex);
	
}
