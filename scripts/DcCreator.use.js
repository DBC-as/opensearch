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
  var oso = XmlNamespaces.oso;

  var that = {};

  that.createDcFromMarc = function ( xml ) {

    var originalXml = XmlUtil.fromString ( xml );
    var dcXml = DcCreator.createDcObject();

//  This part is to be used with next version of work relation
//    for each (child in originalXml.collection.record.datafield.(@tag=="245").subfield.(@code=="a")) {
//      dcXml.oai_dc::dc += DcCreator.createElement( String(child).replace(/\u00a4/, ""), "title", dc );
//    }
    // Used until next version of work relation
    if (originalXml.dkabm::record.dc::title[0] !== undefined) {
      dcXml.oai_dc::dc += DcCreator.createElement( String(originalXml.dkabm::record.dc::title[0]), "title", dc );
    }
    
	  if (originalXml.dkabm::record.dc::creator[0] !== undefined) {
      dcXml.oai_dc::dc += DcCreator.createElement( String(originalXml.dkabm::record.dc::creator[0]), "creator", dc );
    }

    for each (child in originalXml.dkabm::record.dc::language) {
      if (!String(child.@xsi::type).match("dcterms:ISO639-2") && !String(child.@xsi::type).match("oss:subtitles") && !String(child.@xsi::type).match("oss:spoken")) {
        dcXml.oai_dc::dc += DcCreator.createElement( String(child), "language", dc );
      }
    }

    var child;

    for each (child in originalXml.dkabm::record.dc::subject) {
      if (!String(child.@xsi::type).match("dkdcplus:DK5")) {
        dcXml.oai_dc::dc += DcCreator.createElement( String(child), "subject", dc );
      }
    }
    
    for each (child in originalXml.dkabm::record.dcterms::spatial) {
      dcXml.oai_dc::dc += DcCreator.createElement( String(child), "subject", dc );
    }
    
    for each (child in originalXml.dkabm::record.dcterms::temporal) {
      dcXml.oai_dc::dc += DcCreator.createElement( String(child), "subject", dc );
    }

    for each (child in originalXml.dkabm::record.dc::type) {
      if (String(child.@xsi::type).match("dkdcplus:BibDK-Type")) {
        dcXml.oai_dc::dc += DcCreator.createElement( String(child), "type", dc );
      }
    }

    for each (child in originalXml.dkabm::record.dc::contributor) {
      dcXml.oai_dc::dc += DcCreator.createElement( String(child), "contributor", dc );
    }

    for each (child in originalXml.dkabm::record.dc::source) {
      dcXml.oai_dc::dc += DcCreator.createElement( String(child), "source", dc );
    }

    for each (child in originalXml.dkabm::record.dc::identifier) {
      if (String(child.@xsi::type).match("dkdcplus:ISBN")) {
        dcXml.oai_dc::dc += DcCreator.createElement( "ISBN:" + String(child).replace( /-/g, ""), "identifier", dc );
      }
    }

    for each (child in originalXml.dkabm::record.dc::identifier) {
      if (String(child.@xsi::type).match("dkdcplus:ISSN")) {
        dcXml.oai_dc::dc += DcCreator.createElement( "ISSN:" + String(child).replace( /-/g, ""), "identifier", dc );
      }
    }

    for each (child in originalXml.dkabm::record.dc::identifier) {
      if (String(child.@xsi::type).match("oss:upc")) {
        dcXml.oai_dc::dc += DcCreator.createElement( "UPC:" + String(child).replace( /-/g, ""), "identifier", dc );
      }
    }

    for each (child in originalXml.dkabm::record.dcterms::isPartOf) {
      if (String(child.@xsi::type).match("dkdcplus:ISSN")) {
        dcXml.oai_dc::dc += DcCreator.createElement( "ISSN:" + String(child).replace( /-/g, ""), "relation", dc );
      }
    }

    for each (child in originalXml.dkabm::record.dcterms::isPartOf) {
      if (String(child.@xsi::type).match("dkdcplus:ISBN")) {
        dcXml.oai_dc::dc += DcCreator.createElement( "ISBN:" + String(child).replace( /-/g, ""), "relation", dc );
      }
    }

    for each (child in originalXml.collection.record.datafield.(@tag=="014").subfield.(@code=="a")) {
      dcXml.oai_dc::dc += DcCreator.createElement( String(child).replace( /-/g, ""), "relation", dc );
    }

    for each (child in originalXml.collection.record.datafield.(@tag=="016").subfield.(@code=="a")) {
      dcXml.oai_dc::dc += DcCreator.createElement( "PartOf:" + String(child).replace( /-/g, ""), "relation", dc );
    }

    var dcString = String(dcXml);

    return dcString;

  };

  that.createDcFromMarc.__doc__ = <doc type="method">
    <brief>Method that extracts data from a marcXchange record to create Dublin Core data</brief>
    <syntax>DcCreator.createDcFromMarc( xml )</syntax>
    <param name="xml">String containing the original XML (marcXchange)</param>
    <description></description>
    <returns>A string containing OAI Dublin Core XML</returns>
    <examples></examples>
  </doc>;

  that.createDcFromDkabm = function ( xml ) {

    var originalXml = XmlUtil.fromString ( xml );
    var dcXml = DcCreator.createDcObject();

    dcXml.oai_dc::dc += DcCreator.createElement( String(originalXml.dkabm::record.dc::title[0]), "title", dc );
    if (originalXml.dkabm::record.dc::creator[0] !== undefined) {
      dcXml.oai_dc::dc += DcCreator.createElement( String(originalXml.dkabm::record.dc::creator[0]), "creator", dc );
    }

    for each (child in originalXml.dkabm::record.dc::language) {
      if (!String(child.@xsi::type).match("dcterms:ISO639-2")) {
        dcXml.oai_dc::dc += DcCreator.createElement( String(child), "language", dc );
      }
    }

    var child;

    for each (child in originalXml.dkabm::record.dc::subject) {
      if (!String(child.@xsi::type).match("dkdcplus:DK5")) {
        dcXml.oai_dc::dc += DcCreator.createElement( String(child), "subject", dc );
      }
    }
    
    for each (child in originalXml.dkabm::record.dcterms::spatial) {
      dcXml.oai_dc::dc += DcCreator.createElement( String(child), "subject", dc );
    }
    
    for each (child in originalXml.dkabm::record.dcterms::temporal) {
      dcXml.oai_dc::dc += DcCreator.createElement( String(child), "subject", dc );
    }

    for each (child in originalXml.dkabm::record.dc::type) {
      if (String(child.@xsi::type).match("dkdcplus:BibDK-Type")) {
        dcXml.oai_dc::dc += DcCreator.createElement( String(child), "type", dc );
      }
    }

    for each (child in originalXml.dkabm::record.dc::contributor) {
      dcXml.oai_dc::dc += DcCreator.createElement( String(child), "contributor", dc );
    }

    for each (child in originalXml.dkabm::record.dc::source) {
      dcXml.oai_dc::dc += DcCreator.createElement( String(child), "source", dc );
    }

    for each (child in originalXml.dkabm::record.dc::identifier) {
      if (String(child.@xsi::type).match("dkdcplus:ISBN")) {
        dcXml.oai_dc::dc += DcCreator.createElement( "ISBN:" + String(child).replace( /-/g, ""), "identifier", dc );
      }
    }

    for each (child in originalXml.dkabm::record.dc::identifier) {
      if (String(child.@xsi::type).match("dkdcplus:ISSN")) {
        dcXml.oai_dc::dc += DcCreator.createElement( "ISSN:" + String(child).replace( /-/g, ""), "identifier", dc );
      }
    }

    for each (child in originalXml.dkabm::record.dc::identifier) {
      if (String(child.@xsi::type).match("oss:upc")) {
        dcXml.oai_dc::dc += DcCreator.createElement( "UPC:" + String(child).replace( /-/g, ""), "identifier", dc );
      }
    }

    for each (child in originalXml.dkabm::record.dcterms::isPartOf) {
      if (String(child.@xsi::type).match("oss:albumId")) {
        dcXml.oai_dc::dc += DcCreator.createElement( "albumId:" + String(child), "relation", dc );
      } else {
        dcXml.oai_dc::dc += DcCreator.createElement( String(child), "relation", dc );
      }
    }


    var dcString = String(dcXml);

    return dcString;

  };
  
  that.createDcFromDkabm.__doc__ = <doc type="method">
    <brief>Method that extracts data from a DKABM record to create Dublin Core data</brief>
    <syntax>DcCreator.createDcFromDkabm( xml )</syntax>
    <param name="xml">String containing the original XML (DKABM)</param>
    <description></description>
    <returns>A string containing OAI Dublin Core XML</returns>
    <examples></examples>
  </doc>;

  that.createDcObject = function () {

    var dcXml = XmlUtil.fromString( <dc/> );
    dcXml.setNamespace( oai_dc );
    dcXml.addNamespace( dc );
    dcXml.addNamespace( xsi );

    return dcXml;

  };
  
  that.createDcObject.__doc__ = <doc type="method">
    <brief>Method that creates an OAI Dublin Core XML Object, sets and adds namespaces.</brief>
    <syntax>DcCreator.createDcObject()</syntax>
    <description></description>
    <returns>An OAI Dublin Core XML Object</returns>
    <examples></examples>
  </doc>;

  that.createElement = function ( elementValue, elementName, namespace ) {
    var element = XmlUtil.fromString (<{elementName}>{elementValue}</{elementName}>);
    if (namespace !== undefined) {
      element.setNamespace( namespace );
    }

    return element;

  };

  that.createWorkDc = function ( xml ) {

    var originalXml = XmlUtil.fromString ( xml );

    Log.debug( "RLO, ORIGINALXML: " + originalXml);

    var dcXml = DcCreator.createDcObject();
 
 // Until next version of work relation
 //   if (String(originalXml.collection.record.datafield.(@tag=="245").subfield.(@code=="a")) !== "") {
 //     for each (child in originalXml.collection.record.datafield.(@tag=="245").subfield.(@code=="a")) {
 //       dcXml.oai_dc::dc += DcCreator.createElement( String(child).replace(/\u00a4/, ""), "title", dc );
 //     }
 //   } else {
      dcXml.oai_dc::dc += DcCreator.createElement( String(originalXml.dkabm::record.dc::title[0]), "title", dc );
 //   }
 
    if (originalXml.dkabm::record.dc::creator[0] !== undefined) {
      dcXml.oai_dc::dc += DcCreator.createElement( String(originalXml.dkabm::record.dc::creator[0]), "creator", dc );
    }

    var child;

    for each (child in originalXml.dkabm::record.dc::language) {
      if (!String(child.@xsi::type).match("dcterms:ISO639-2") && !String(child.@xsi::type).match("oss:subtitles") && !String(child.@xsi::type).match("oss:spoken")) {
        dcXml.oai_dc::dc += DcCreator.createElement( String(child), "language", dc );
      }
    }

    for each (child in originalXml.dkabm::record.dc::subject) {
      if (String(child).match("computerspil") || String(child).match("soundtracks")) {
        dcXml.oai_dc::dc += DcCreator.createElement( String(child), "subject", dc );
      }
    }
    
    for each (child in originalXml.dkabm::record.dcterms::spatial) {
      dcXml.oai_dc::dc += DcCreator.createElement( String(child), "subject", dc );
    }
    
    for each (child in originalXml.dkabm::record.dcterms::temporal) {
      dcXml.oai_dc::dc += DcCreator.createElement( String(child), "subject", dc );
    }

    for each (child in originalXml.dkabm::record.dc::type) {
      if (String(child.@xsi::type).match("dkdcplus:BibDK-Type")) {
        dcXml.oai_dc::dc += DcCreator.createElement( String(child), "type", dc );
      }
    }

    for each (child in originalXml.dkabm::record.dc::contributor) {
      dcXml.oai_dc::dc += DcCreator.createElement( String(child), "contributor", dc );
    }

    for each (child in originalXml.dkabm::record.dc::source) {
      dcXml.oai_dc::dc += DcCreator.createElement( String(child), "source", dc );
    }

    for each (child in originalXml.dkabm::record.dc::identifier) {
      if (String(child.@xsi::type).match("dkdcplus:ISBN")) {
        dcXml.oai_dc::dc += DcCreator.createElement( "ISBN:" + String(child).replace( /-/g, ""), "identifier", dc );
      }
    }

    for each (child in originalXml.dkabm::record.dc::identifier) {
      if (String(child.@xsi::type).match("dkdcplus:ISSN")) {
        dcXml.oai_dc::dc += DcCreator.createElement( "ISSN:" + String(child).replace( /-/g, ""), "identifier", dc );
      }
    }

    for each (child in originalXml.dkabm::record.dc::identifier) {
      if (String(child.@xsi::type).match("oss:upc")) {
        dcXml.oai_dc::dc += DcCreator.createElement( "UPC:" + String(child).replace( /-/g, ""), "identifier", dc );
      }
    }

    Log.debug( "RLO, DCXML: " + dcXml);

    var dcString = String(dcXml);

    Log.debug( "RLO, DCSTRING: " + dcString);

    return dcString;

  };
  
  that.createWorkDc.__doc__ = <doc type="method">
    <brief>Method that extracts data from a DKABM record to create Dublin Core data for a work object</brief>
    <syntax>DcCreator.createWorkDc( xml )</syntax>
    <param name="xml">String containing the original XML (DKABM)</param>
    <description></description>
    <returns>A string containing OAI Dublin Core XML</returns>
    <examples></examples>
  </doc>;

  that.createDcFromOso = function ( xml ) {

    var originalXml = XmlUtil.fromString ( xml );

    Log.debug( "RLO, ORIGINALXML: " + originalXml);

    var dcXml = DcCreator.createDcObject();

      dcXml.oai_dc::dc += DcCreator.createElement( String(originalXml.oso::object.oso::title), "title", dc );

      dcXml.oai_dc::dc += DcCreator.createElement( String(originalXml.oso::object.oso::creator), "creator", dc );

      dcXml.oai_dc::dc += DcCreator.createElement( String(originalXml.oso::object.oso::identifier), "identifier", dc );

      dcXml.oai_dc::dc += DcCreator.createElement( String(originalXml.oso::object.oso::type), "type", dc );

    Log.debug( "RLO, DCXML: " + dcXml);

    var dcString = String(dcXml);

    Log.debug( "RLO, DCSTRING: " + dcString);

    return dcString;

  };
  
  that.createDcFromOso.__doc__ = <doc type="method">
    <brief>Method that extracts data from Open Search Object XML to create Dublin Core data</brief>
    <syntax>DcCreator.createWorkDc( xml )</syntax>
    <param name="xml">String containing the original XML (Open Search Object)</param>
    <description></description>
    <returns>A string containing OAI Dublin Core XML</returns>
    <examples></examples>
  </doc>;

  return that;

}();
