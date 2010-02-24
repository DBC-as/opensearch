
//importClass(Packages.dk.dbc.opensearch.common.metadata.DublinCoreElement);


// Takes a string representation of a marcxchange posts dc-stream
// and makes into an xml object where values are selected from. 
// The selected values are put into an array which resides on the global 
// namespace for the script. See comment below for use
//\Todo: Find a better way to handle the return values 
function generateSearchPairs( dcXML, originalXML, resultArray )
{
    var XML_cargo = new XML( originalXML );
    var XML_dc = new XML( dcXML )
    //do stuff
    //right now its a dummy
 
    var dc = new Namespace( "dc", "http://purl.org/dc/elements/1.1/" );

    //put values in the pairArray that the calling java method looks in 
    //for searchpairs. Even indexes are fieldnames, uneven are values. Yes its hack'ish
    //but it works for now
    resultArray[0] = "title";
    resultArray[1] = XML_cargo.dc::title;
    resultArray[2] = "title";
    resultArray[3] = XML_cargo.dc::source;
    resultArray[4] = "source";
    resultArray[5] = XML_cargo.dc::title;
    resultArray[6] = "source";
    resultArray[7] = XML_cargo.dc::source;
} 


// Function that compares an object and a workobject. It gets their content
// in string representations.
function checkmatch( newObject, workObject )
{
    var newObjectXml = new XML( newObject );
    var workObjectXml = new XML( workObject );
    var dc = new Namespace( "dc", "http://purl.org/dc/elements/1.1/" );

    var result = false;

    print ("Type new: " + newObjectXml.dc::type + "\n");
    print ("Type work: " + workObjectXml.dc::type + "\n");

    //check for a match. Return true if there is, false if not ;-)
    switch (newObjectXml.dc::type) {
      case "Anmeldelse":
        result = false;
        break;
      case "Artikel":
        if (newObjectXml.dc::title === workObjectXml.dc::title && newObjectXml.dc::creator === workObjectXml.dc::creator && workObjectXml.dc::type === "Artikel|Avisartikel|Tidsskriftsartikel") {
          result = true;
        } else {
          result = false;
        }
        break;
      case "Avis":
        if (newObjectXml.dc::title === workObjectXml.dc::title && workObjectXml.dc::type === "Avis") {
          result = true;
        } else {
          result = false;
        }
        break;
      case "Avisartikel":
        if (newObjectXml.dc::title === workObjectXml.dc::title && newObjectXml.dc::creator === workObjectXml.dc::creator && workObjectXml.dc::type === "Artikel|Avisartikel|Tidsskriftsartikel") {
          result = true;
        } else {
          result = false;
        }
        break;
      case "Tidsskrift":
        if (newObjectXml.dc::title === workObjectXml.dc::title && workObjectXml.dc::type === "Tidsskrift") {
          result = true;
        } else {
          result = false;
        }
        break;
      case "Tidsskriftsartikel":
        if (newObjectXml.dc::title === workObjectXml.dc::title && newObjectXml.dc::creator === workObjectXml.dc::creator && workObjectXml.dc::type === "Artikel|Avisartikel|Tidsskriftsartikel") {
          result = true;
        } else {
          result = false;
        }
        break;
      default:
        if (newObjectXml.dc::source === workObjectXml.dc::source && workObjectXml.dc::type !== "Anmeldelse|Artikel|Avis|Avisartikel|Tidsskrift|Tidsskriftsartikel") {
          result = true;
        } else if (newObjectXml.dc::source === workObjectXml.dc::title && workObjectXml.dc::type !== "Anmeldelse|Artikel|Avis|Avisartikel|Tidsskrift|Tidsskriftsartikel") {
          result = true;
        } else if (newObjectXml.dc::title === workObjectXml.dc::source && workObjectXml.dc::type !== "Anmeldelse|Artikel|Avis|Avisartikel|Tidsskrift|Tidsskriftsartikel") {
          result = true;
        } else if (newObjectXml.dc::title === workObjectXml.dc::title && workObjectXml.dc::type !== "Anmeldelse|Artikel|Avis|Avisartikel|Tidsskrift|Tidsskriftsartikel") {
          result = true;
        } else {
          result = false;
        }
        break;
    }
    if (result === false) {
      return false;
    } else {
      return true;
    }
}


// Function that builds the originaldata and DC of a new workobject
// It gets a string representation of the content of the object it must
// be the workobject for and returns a string rep. of the content of the 
// workobject. The DC is given to the function and filled in as a sideeffect
function makeworkobject( cargoXML, workDC )
{
    var XML_cargo = new XML( cargoXML );
    ting = new Namespace ( "ting", "http://www.dbc.dk/ting" );
    dkabm = new Namespace ( "dkabm", "http://biblstandard.dk/abm/namespace/dkabm/" );
    dc = new Namespace ( "dc", "http://purl.org/dc/elements/1.1/" );

    var dc = new Namespace( "dc", "http://purl.org/dc/elements/1.1/" );
    //select the elements in the dc-xml that constitutes the work
    //do something with the xml and return it in string format
    print( "XML_cargo:"+ XML_cargo + "\n" );
   
//    var creator = XML_cargo.dc::creator;
//    var source = XML_cargo.dc::source;
//    var title = XML_cargo.dc::title; 
//    var type =  XML_cargo.dc::type;

    var xml = new XML (<container/>);
    xml.addNamespace( ting );
    xml.addNamespace( dkabm );
    xml.addNamespace( dc );
 
    xml.dkabm::record = "";
    xml.dkabm::record.dc::title = XML_cargo.dc::title;
    xml.dkabm::record.dc::creator = XML_cargo.dc::creator;
    xml.dkabm::record.dc::type = XML_cargo.dc::type;
    xml.dkabm::record.dc::source= XML_cargo.dc::source;

    print (xml + "\n");

    res = String(xml);

   workDC.setTitle( XML_cargo.dc::title );
    workDC.setCreator( XML_cargo.dc::creator );
    workDC.setType( XML_cargo.dc::type );
    workDC.setSource( XML_cargo.dc::source );

    return res;
}
