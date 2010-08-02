EXPORTED_SYMBOLS = ['DcCreator'];

use ( "XmlUtil.use.js" );
use ( "XmlNamespaces.use.js" );

const DcCreator = function(){

  var dkabm = XmlNamespaces.dkabm;
  var dc = XmlNamespaces.dc;
  var ac = XmlNamespaces.ac;
  var dcterms = XmlNamespaces.dcterms;
  var xsi = XmlNamespaces.xsi;
  var ting = XmlNamespaces.ting;
  var oai_dc = XmlNamespaces.oai_dc;
  var marcx = XmlNamespaces.marcx;

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

  that.createDcFromDkabm = function ( xml ) {

    var originalXml = XmlUtil.fromString ( xml );
    var dcXml = DcCreator.createDcObject();

    dcXml.oai_dc::dc += DcCreator.createElement( String(originalXml.dkabm::record.dc::title[0]), "title", dc );
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
