package dbc.examples.pdftoxml;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String pdfFileName = "example.pdf";
		String xmlFileName = "test.xml";
		PdfToXML transformer = new PdfToXML(pdfFileName, xmlFileName);
		transformer.Transform();

	}

}
