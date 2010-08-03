use ( "DcCreator.use.js" );

//importClass(Packages.dk.dbc.opensearch.common.metadata.DublinCoreElement);

// Takes a string representation of a marcxchange posts dc-stream
// and makes into an xml object where values are selected from. 
// The selected values are put into an array which resides on the global 
// namespace for the script. See comment below for use
//\Todo: Find a better way to handle the return values 
//logging can be made through use of the "log" object which is part of the namespace
// the methods log.debug, log.info, log.error, log.fatal are available
function generateSearchPairs( dcXML, originalXML, resultArray )
{
    var XML_org = new XML( originalXML );
    var XML_dc = new XML( dcXML )
    //do stuff
    //right now its a dummy
 
    var dc = new Namespace( "dc", "http://purl.org/dc/elements/1.1/" );

    //put values in the pairArray that the calling java method looks in 
    //for searchpairs. Even indexes are fieldnames, uneven are values. Yes its hack'ish
    //but it works for now
    if (String(XML_dc.dc::title) !== "") {
      resultArray[0] = "title";
      resultArray[1] = XML_dc.dc::title;
    }
    if (String(XML_dc.dc::source) !== "") {
      resultArray[2] = "title";
      resultArray[3] = XML_dc.dc::source;
    }
    if (String(XML_dc.dc::title) !== "") {
      resultArray[4] = "source";
      resultArray[5] = XML_dc.dc::title;
    }
    if (String(XML_dc.dc::source) !== "") {
      resultArray[6] = "source";
      resultArray[7] = XML_dc.dc::source;
    }
} 


// Function that compares an object and a workobject. It gets their content
// in string representations.
function checkmatch( newObject, workObject )
{

Log.debug("RLO: workXML = " + workObject + ", end\n");
Log.debug("RLO: newXML = " + newObject + ", end\n");
    var newObjectXml = new XML( newObject );
    var workObjectXml = new XML( workObject );
    var dc = new Namespace( "dc", "http://purl.org/dc/elements/1.1/" );
    //var oai_dc = new Namespace( "oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/" );

    var result = false;
    var newTitle = String(newObjectXml.dc::title);
    var workTitle = String(workObjectXml.dc::title);
    var newSource = String(newObjectXml.dc::source);
    var workSource = String(workObjectXml.dc::source);
    var newCreator = String(newObjectXml.dc::creator);
    var workCreator = String(workObjectXml.dc::creator);
    //var workType = String(workObjectXml.dc::creator);
    var newType = String(newObjectXml.dc::type);
    var workType = String(workObjectXml.dc::type);


    // var newTitle = String(newObjectXml.dc::title);
    // var workTitle = String(workObjectXml.dc::title);
    // var newSource = String(newObjectXml.dc::source);
    // var workSource = String(workObjectXml.dc::source);
    // var newCreator = String(newObjectXml.dc::creator);
    // var workCreator = String(workObjectXml.dc::creator);
    // var newType = String(newObjectXml.dc::type);
    // var workType = String(workObjectXml.dc::type);

    Log.debug( "RLO: start match\n");
    //check for a match. Return true if there is, false if not ;-)
    Log.debug( "RLO: newType = " + newType + "\n" );
    Log.debug( "RLO: worktype = " + workType + "\n");
    Log.debug( "RLO: workcreator = " + workCreator + "\n" );
    switch (newType) {
      case "Anmeldelse":
        Log.debug( "RLO: Anmeldelse\n");
        result = false;
        break;
      case "Artikel":
        Log.debug( "RLO: Artikel\n");
        if (newTitle === workTitle && newCreator === workCreator && workType.match("Artikel|Avisartikel")) {
          result = true;
        } else {
          result = false;
        }
        break;
      case "Avis":
        Log.debug( "RLO: Avis\n");
        if (newTitle === workTitle && workType === "Avis") {
          result = true;
        } else {
          result = false;
        }
        break;
      case "Avisartikel":
        Log.debug( "RLO: Avisartikel\n");
        if (newTitle === workTitle && newCreator === workCreator && workType.match("Artikel|Avisartikel")) {
          result = true;
        } else {
          result = false;
        }
        break;
      case "Tidsskrift":
        Log.debug( "RLO: Tidsskrift\n");
        if (newTitle === workTitle && workType === "Tidsskrift") {
          result = true;
        } else {
          result = false;
        }
        break;
      case "Tidsskriftsartikel":
        Log.debug( "RLO: Tidsskriftsartikel\n");
        if (newTitle === workTitle && newCreator === workCreator && workType.match("Artikel|Avisartikel|Tidsskriftsartikel")) {
          result = true;
        } else {
          result = false;
        }
        break;
      case "CD": case "Grammofonplade": case "netmusik (album)":
        Log.debug( "RLO: CD\n");
        if (newTitle === workTitle && newCreator === workCreator && workType.match("CD|Grammofonplade|netmusik (album)")) {
          result = true;
        } else {
          result = false;
        }
        break;
      case "netmusik (track)": 
        Log.debug( "RLO: netmusik (track)\n");
        if (newTitle === workTitle && newCreator === workCreator && workType.match("netmusik (track)")) {
          result = true;
        } else {
          result = false;
        }
        break;
      default:
        Log.debug( "RLO: default\n");
        if (newCreator === workCreator) {
Log.debug("RLO: newcreator = workcreator \n");
          if (newSource !== "" && workSource !== "" && newSource === workSource && !workType.match("Anmeldelse|Artikel|Avis|Avisartikel|Tidsskrift|Tidsskriftsartikel|CD|Grammofonplade|Kassettelydb\u00e5nd|netmusik (album)| netmusik (track)")) {
            result = true;
          } else if (newSource !== "" && workTitle !== "" && newSource === workTitle && !workType.match("Anmeldelse|Artikel|Avis|Avisartikel|Tidsskrift|Tidsskriftsartikel|CD|Grammofonplade|Kassettelydb\u00e5nd|netmusik (album)|netmusik (track)")) {
            result = true;
          } else if (newTitle !== "" && workSource !== "" && newTitle === workSource && !workType.match("Anmeldelse|Artikel|Avis|Avisartikel|Tidsskrift|Tidsskriftsartikel|CD|Grammofonplade|Kassettelydb\u00e5nd|netmusik (album)| netmusik (track)")) {
            result = true;
          } else if (newTitle !== "" && workTitle !== "" && newTitle === workTitle && !workType.match("Anmeldelse|Artikel|Avis|Avisartikel|Tidsskrift|Tidsskriftsartikel|CD|Grammofonplade|netmusik (album)|netmusik (track)")) {
            result = true;
          } else {
            result = false;
          }
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

// Function that builds the originaldata and DC of a new workobject
// It gets a string representation of the content of the object it must
// be the workobject for and returns a string rep. of the content of the 
// workobject. The DC is given to the function and filled in as a sideeffect
function makeworkobject( cargoXML, workDC )
{
//     var XML_cargo = new XML( cargoXML );
//     var ting = new Namespace ( "ting", "http://www.dbc.dk/ting" );
//     var dkabm = new Namespace ( "dkabm", "http://biblstandard.dk/abm/namespace/dkabm/" );
//     var dc = new Namespace ( "dc", "http://purl.org/dc/elements/1.1/" );
// 
//     var dc = new Namespace( "dc", "http://purl.org/dc/elements/1.1/" );
    //select the elements in the dc-xml that constitutes the work
    //do something with the xml and return it in string format
//     print( "XML_cargo:"+ XML_cargo + "\n" );
   
//    var creator = XML_cargo.dc::creator;
//    var source = XML_cargo.dc::source;
//    var title = XML_cargo.dc::title; 
//    var type =  XML_cargo.dc::type;

//     var xml = new XML (<container/>);
//     xml.addNamespace( ting );
//     xml.addNamespace( dkabm );
//     xml.addNamespace( dc );
//  
//     xml.dkabm::record = "";
//     xml.dkabm::record.dc::title = XML_cargo.dc::title;
//     xml.dkabm::record.dc::creator = XML_cargo.dc::creator;
//     xml.dkabm::record.dc::type = XML_cargo.dc::type;
//     xml.dkabm::record.dc::source= XML_cargo.dc::source;
// 
//     print (xml + "\n");
// 
//     var res = String(xml);
// 
//     workDC.setTitle( XML_cargo.dc::title );
//     workDC.setCreator( XML_cargo.dc::creator );
//     workDC.setType( XML_cargo.dc::type );
//     workDC.setSource( XML_cargo.dc::source );
// 
//     return res;


    Log.info( "RLO: Entering javascript makeworkobject" );

    var dc = DcCreator.createWorkDc ( cargoXml ); 

    Log.info( "RLO: Leaving javascript" );

    return dc;

}
