use ( "XmlUtil" );
use ( "XmlNamespaces" );

EXPORTED_SYMBOLS = ['DeleteObjects'];

var DeleteObjects = function() {

	var marcx = XmlNamespaces.marcx;
  var dkabm = XmlNamespaces.dkabm;
  var dc = XmlNamespaces.dc;
  var ac = XmlNamespaces.ac;
  var dcterms = XmlNamespaces.dcterms;
  var xsi = XmlNamespaces.xsi;
  var ting = XmlNamespaces.ting;
  var oso = XmlNamespaces.oso;
	var oss = XmlNamespaces.oss;

  var that = {};
	
	that.checkDeleteObjectMarc = function( xml ) {

    Log.info ("Start checkDeleteObjectMarc" );

    var originalXml = XmlUtil.fromString( xml );
  	if (String(originalXml.marcx::collection.record.datafield.(@tag=="004").subfield.(@code=="r")) === "d") {
    	return true;
		} else {
			return false;
		}

  };
	
	that.checkDeleteObjectDkabm = function( xml ) {

    Log.info ("Start checkDeleteObjectDkabm" );

    var originalXml = XmlUtil.fromString( xml );
		if (String(originalXml.dkabm::record.ac::activity.ac::action) === "delete-out-of-scope" ) {
     	return true;
		} else {
			return false;
		}

  };
	
	that.checkDeleteObjectOupsStatus = function( xml ) {

    Log.info ("Start checkDeleteObjectOupsStatus" );

    var originalXml = XmlUtil.fromString( xml );
		if (String(originalXml.ting::originalData.status) === "d") {
     	return true;
		} else {
			return false;
		}

  };
	
	
	return that;

}();
