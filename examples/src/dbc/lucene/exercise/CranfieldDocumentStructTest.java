package dbc.examples.lucene.exercise.tests;

import org.junit.Assert;
import org.junit.Test;

import dbc.examples.lucene.exercise.CranfieldDocumentStruct;
/**
 * @author stm
 *
 */
public class CranfieldDocumentStructTest {
	@Test
	public void TestStructSanity(){
		Assert.assertEquals(CranfieldDocumentStruct.FieldName.ABSTRACT.toString(), "abstract");
		Assert.assertEquals(CranfieldDocumentStruct.FieldName.AUTHOR.toString(), "author");
		Assert.assertEquals(CranfieldDocumentStruct.FieldName.PUB.toString(), "publication_data");
		Assert.assertEquals(CranfieldDocumentStruct.FieldName.TEXT.toString(), "text");
	}
}
