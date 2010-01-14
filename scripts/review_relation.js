
// I have decided to call he main function, well... main :)
function main( submitter, format, language, xml_review )
{
    // This is lame but necessary :(
    xml_review = xml_review.replace(/<\?xml[^>]*\?>/, "");


    print( "Entering javascript\n===================\n" );

    // Writing out the parameters:
    print( "submitter: " + submitter + "\n" );
    print( "format:    " + format + "\n" );
    print( "language:  " + language + "\n" );
    // Omitting xml since it just takes up to much space:
    //    print( "XML: " + xml_review + "\n" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var XML_review2 = new XML( xml_review );

    // A note about xpath and e4x:
    //
    // This is a normal Xpath-expression:
    // var xpath = "/*/*/*/*[@tag='014']/*[@code='a']";
    //
    // e4x does it a little different:
    // value = XML_review2.*.*.*.(@tag=='014').*.(@code=='a');
    //

    var identifier = XML_review2.*.*.*.(@tag=='014').*.(@code=='a');
    
    print( "Identifier: " + identifier + "\n" );    

    // var result = scriptClass.getPID("Balle");

    print( "===================\nLeaving javascript\n\n\n" );

}