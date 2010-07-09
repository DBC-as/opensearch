EXPORTED_SYMBOLS = ['DcCreator'];

use ( "XmlUtil.use.js" );

const DcCreator = function(){

  var oai_dc = new Namespace( "oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/" );
  var marcx = new Namespace ( "marcx", "http://www.bs.dk/standards/MarcXchange" );
  var dkabm = new Namespace( "dkabm", "http://biblstandard.dk/abm/namespace/dkabm/" );
  var dc = new Namespace( "dc", "http://purl.org/dc/elements/1.1/" );
  var ac = new Namespace( "ac", "http://biblstandard.dk/ac/namespace/" );
  var dcterms = new Namespace( "dcterms", "http://purl.org/dc/terms/" );
  var xsi = new Namespace( "xsi", "http://www.w3.org/2001/XMLSchema-instance" );
  var ting = new Namespace( "ting", "http://www.dbc.dk/ting" );

  var that = {};

  that.createDcFromMarc = function ( xml ) {

    var originalXml = XmlUtil.fromString ( xml );
    var dcXml = DcCreator.createDcObject();

    dcXml.oai_dc::dc += DcCreator.createElement( String(originalXml.collection.record.datafield.(@tag=="245").subfield.(@code=="a")), "title", dc );
    dcXml.oai_dc::dc += DcCreator.createElement( String(originalXml.dkabm::record.dc::creator[0]), "creator", dc );
    dcXml.oai_dc::dc += DcCreator.createElement( String(originalXml.dkabm::record.dc::type), "type", dc );
    dcXml.oai_dc::dc += DcCreator.createElement( String(originalXml.dkabm::record.dcterms::isPartOf), "relation", dc );
    dcXml.oai_dc::dc += DcCreator.createElement( String(originalXml.dkabm::record.dc::source), "source", dc );

    for each (child in originalXml.dkabm::record.dc::language) {
      if (!String(child.@type).match("dcterms:ISO639-2")) {
        dcXml.oai_dc::dc += DcCreator.createElement( String(child), "language", dc );
      }
    }

    var child;

    for each (child in originalXml.dkabm::record.dc::subject) {
      if (!String(child.@type).match("dkdcplus:DK5")) {
        dcXml.oai_dc::dc += DcCreator.createElement( String(child), "subject", dc );
      }
    }

    for each (child in originalXml.dkabm::record.dc::identifier) {
      if (String(child.@type).match("dkdcplus:ISBN")) {
        dcXml.oai_dc::dc += DcCreator.createElement( "ISBN:" + String(child).replace( /-/g, ""), "identifier", dc );
      }
    }

    for each (child in originalXml.dkabm::record.dc::identifier) {
      if (String(child.@type).match("dkdcplus:ISSN")) {
        dcXml.oai_dc::dc += DcCreator.createElement( "ISSN:" + String(child).replace( /-/g, ""), "identifier", dc );
      }
    }

    var dcString = String(dcXml);

    return dcString;

  };

  that.createDcObject = function () {

    var dcXml = XmlUtil.fromString( <dc/> );
    dcXml.setNamespace( oai_dc );
    dcXml.addNamespace( dc );
    dcXml.addNamespace( xsi );

    return dcXml;

  };

  that.createElement = function ( elementValue, elementName, namespace ) {
    var element = XmlUtil.fromString (<{elementName}>{elementValue}</{elementName}>);
    if (namespace !== undefined) {
      element.setNamespace( namespace );
    }
      

    return element;

  };

  return that;

}();
