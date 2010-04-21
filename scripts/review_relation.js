
// I have decided to call the main function, well... main :)
function main( submitter, format, language, xml_review, pid )
{

    Log.info( "Entering javascript" );

    // Writing out the parameters:
    Log.info( "submitter: " + submitter );
    Log.info( "format:    " + format );
    Log.info( "language:  " + language );
    // Omitting printing of xml since it just takes up to much space:
    // log.info( "XML: \n" + xml_review  );

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
    
    Log.info( "Identifier: " + identifier );    
    Log.info( "pid: " + pid );

    var results = scriptClass.getPID( identifier );

    for ( var i = 0; i < results.length; ++i ) {
	result = results[i];

	Log.info( "result: " + result );

	scriptClass.createRelation( pid, "isReviewOf", result);
	scriptClass.createRelation( result, "hasReview", pid);
    }


    Log.info( "Leaving javascript" );

}