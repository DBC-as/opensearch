/**
 * 
 */
package dbc.examples.lucene.exercise;

/**
 * @author stm
 *
 */

// \todo: there really should be a more effecient way to reference
// these structs than going though .ToString() but alas, the source of
// java wisdom lies not on my path
public class CranfieldDocumentStruct {
		
	public static enum FieldName{
		ABSTRACT{
			@Override public String toString(){
				return "abstract";				
			}
		},
		AUTHOR{
			@Override public String toString(){
				return "author";				
			}
		},
		PUB{
			@Override public String toString(){
				return "publication_data";				
			}
		},
		TEXT{
			@Override public String toString(){
				return "text";				
			}
		};

	}


}
