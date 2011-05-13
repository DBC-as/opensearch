use ( "XmlUtil.use.js" );
use ( "XmlNamespaces.use.js" );

// A function can be implemented here to determine if the object for a post
// should be marked as deleted in the database.
// The function is called from the Store plugin.
// If it returns true, the object is marked as deleted.
// If it returns false, or if the workflow does not define a script file and
// a function name for the submitter/format pair, the post is stored as usual

function isDeletePostMarc( submitter, format, language, xml, pid )
{
  var originalXml = XmlUtil.fromString( xml );
  if (String(originalXml.collection.record.datafield.(@tag=="004").subfield.(@code=="r")) === "d")
    return true;

  return false;
}

function isDeletePostDkabm( submitter, format, language, xml, pid )
{
  var originalXml = XmlUtil.fromString( xml );
  if (String(originalXml.ting::originalData.status) === "d")
    return true;

  return false;
}

function isDeletePost( submitter, format, language, xml, pid )
{
  var originalXml = XmlUtil.fromString( xml );
  if (String(originalXml.collection.record.datafield.(@tag=="004").subfield.(@code=="r")) === "d")
    return true;
  if (String(originalXml.ting::originalData.status) === "d")
    return true;
  if (String(originalXml.dkabm::record.ac::activity.ac::action) === "delete-out-of-scope" )
    return true;

  return false;
}
