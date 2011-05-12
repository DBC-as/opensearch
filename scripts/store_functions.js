use ( "XmlUtil.use.js" );
use ( "XmlNamespaces.use.js" );


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
