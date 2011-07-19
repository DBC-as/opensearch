use ( "DeleteObjects.use.js" );

// A function can be implemented here to determine if the object for a record
// should be marked as deleted in the database.
// The function is called from the Store plugin.
// If it returns true, the object is marked as deleted.
// If it returns false, or if the workflow does not define a script file and
// a function name for the submitter/format pair, the post is stored as usual

function isDeleteObjectMarc( submitter, format, language, xml, pid ) {
	var result = DeleteObjects.checkDeleteObjectMarc( xml );
	return result;
}

function isDeleteObjectDkabm( submitter, format, language, xml, pid ) {
  var result = DeleteObjects.checkDeleteObjectMarc( xml );
	return result;
}

function isDeleteObjectOupsStatus( submitter, format, language, xml, pid ) {
	var result = DeleteObjects.checkDeleteObjectOupsStatus( xml );
	return result;
}
