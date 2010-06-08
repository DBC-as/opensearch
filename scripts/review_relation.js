
// I have decided to call the main function, well... main :)
function main( submitter, format, language, xml_review, pid )
{

    Log.info( "Entering javascript" );

    // var fedora_search = new scriptClass.FedoraSearchJS();
    // var tmp = new dk.dbc.opensearch.common.javascript.FedoraSearchJS();
    //    var tmp = new FedoraSearch();

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

    var results = FedoraPIDSearch.pid( "*:" + identifier ); // wildcardsearch (only possible in PID).

    for ( var i = 0; i < results.length; ++i ) {
	var result = results[i];

	Log.info( "result: " + result );

	// NOTE:
	// NS is not supposed to be in this javascript.
	// It is supposed to be part of the function called when 
	// adding a relation through the auto-generated RDF-relation-javascript.
	// Until the auto-generated JS is done, the NS will be present here.
	var NS = "http://oss.dbc.dk/rdf/dkbib#";

	scriptClass.createRelation( pid, NS + "isReviewOf", result);
	scriptClass.createRelation( result, NS + "hasReview", pid);
    }


    Log.info( "Leaving javascript" );

}