package dbc.examples.pdftoxml;

import java.io.*;

import org.pdfbox.util.*;
import org.pdfbox.pdmodel.PDDocument;
import javax.xml.stream.*;

public class PdfToXML {
	File xmlFile; //The XML file we write to
	String pdfFileName; //name of the .pdf to get text from
	XMLOutputFactory factory = XMLOutputFactory.newInstance();
	XMLStreamWriter writer;
	//BufferedReader reader;
	PDFTextStripper stripper;
	String pdfText;
	PDDocument PDDoc;

	//Constructor
	PdfToXML(String pdfFileName, String xmlFileName){

		this.pdfFileName = pdfFileName;
		xmlFile = new File(xmlFileName);

		try{
		writer = factory.createXMLStreamWriter(new FileOutputStream(xmlFile), "UTF-8" );
		}catch(XMLStreamException xmlse){
			xmlse.printStackTrace();
		}catch(FileNotFoundException fnfe){
			fnfe.printStackTrace();
		}
		try{
			stripper = new PDFTextStripper();
		}catch(IOException e){
			e.printStackTrace();
		}

	}
	//Strips the text from the pdf file and reads it into variable pdfText
	private void PDFStrip(){

		try{
			PDDoc = PDDocument.load(pdfFileName);
			pdfText = stripper.getText(PDDoc);
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * writes the variable pdfText to the XML document
	 */
	private void xmlWrite() {
		try{
		writer.writeStartDocument("UTF-8", "1.0"); //(encoding, version)
		writer.writeStartElement("Text");
		writer.writeCharacters(pdfText);
		/*for(; (tempContent = reader.readLine() ) != null; ){
				writer.writeCharacters(tempContent);
			}*/
		writer.writeEndElement();
		writer.writeEndDocument();
		}catch(XMLStreamException e){
		e.printStackTrace();}
	}
	public void Transform(){
		PDFStrip();
		xmlWrite();

	}
}
