
//importClass(Packages.dk.dbc.opensearch.common.metadata.DublinCoreElement);

use ( "DcCreator.use.js" );
use ( "Normalize.use.js" );

// Takes a string representation of a marcxchange posts dc-stream
// and makes into an xml object where values are selected from. 
// The selected values are put into an array which resides on the global 
// namespace for the script. See comment below for use
//\Todo: Find a better way to handle the return values 
//logging can be made through use of the "Log" object which is part of the namespace
// the methods Log.debug, Log.info, Log.error, Log.fatal are available

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
    var newObjectXml = new XML( newObject );
    var workObjectXml = new XML( workObject );
    var dc = new Namespace( "dc", "http://purl.org/dc/elements/1.1/" );

    var result = false;
    var newTitle = Normalize.removeSpecialCharacters( String(newObjectXml.dc::title).toLowerCase() );
    var workTitle = Normalize.removeSpecialCharacters( String(workObjectXml.dc::title).toLowerCase() );
    var newSource = Normalize.removeSpecialCharacters( String(newObjectXml.dc::source).toLowerCase() );
    var workSource = Normalize.removeSpecialCharacters( String(workObjectXml.dc::source).toLowerCase() );
    var newCreator = String(newObjectXml.dc::creator[0]).toLowerCase();
    var workCreator = String(workObjectXml.dc::creator[0]).toLowerCase();
    var newType = String(newObjectXml.dc::type).toLowerCase();
    var workType = String(workObjectXml.dc::type).toLowerCase();

    Log.debug( "RLO: start match\n");
    //check for a match. Return true if there is, false if not ;-)
    switch (newType) {
      case "anmeldelse":
        Log.debug( "RLO: Anmeldelse\n");
        result = false;
        break;
      case "artikel":
        Log.debug( "RLO: Artikel\n");
        if (newTitle === workTitle && newCreator === workCreator && workType.match(/artikel/)) {
          result = true;
        } else {
          result = false;
        }
        break;
      case "avis":
        Log.debug( "RLO: Avis\n");
        if (newTitle === workTitle && (workType === "avis" || workType === "avis (net)")) {
          result = true;
        } else {
          result = false;
        }
        break;
      case "avisartikel":
        Log.debug( "RLO: Avisartikel\n");
        if (newTitle === workTitle && newCreator === workCreator && workType.match(/artikel/)) {
          result = true;
        } else {
          result = false;
        }
        break;
      case "tidsskrift":
        Log.debug( "RLO: Tidsskrift\n");
        if (newTitle === workTitle && workType === "tidsskrift") {
          result = true;
        } else {
          result = false;
        }
        break;
      case "tidsskriftsartikel":
        Log.debug( "RLO: Tidsskriftsartikel\n");
        if (newTitle === workTitle && newCreator === workCreator && workType.match(/artikel/)) {
          result = true;
        } else {
          result = false;
        }
        break;
      case "cd (musik)": case "grammofonplade": case "musik (net)":
        Log.debug( "RLO: Musik (album)\n");
        if (newTitle === workTitle && newCreator === workCreator && workType.match(/cd \(musik\)|grammofonplade|musik \(net\)/)) {
          result = true;
        } else {
          result = false;
        }
        break;
      case "musiktrack (net)": 
        Log.debug( "RLO: Musik (track)\n");
        if (newTitle === workTitle && newCreator === workCreator && workType.match(/musiktrack/)) {
          result = true;
        } else {
          result = false;
        }
        break;
      default:
        Log.debug( "RLO: default\n");
				Log.debug( "New Creator: " + newCreator);
				Log.debug( "Work Creator: " + workCreator);
				Log.debug( "New Title: " + newTitle);
				Log.debug( "Work Title: " + workTitle);
				Log.debug( "New Source: " + newSource);
				Log.debug( "Work Source: " + workSource);
        if (newCreator === workCreator) {
          if (newSource !== "" && workSource !== "" && newSource === workSource && !workType.match(/anmeldelse|artikel|avis|tidsskrift|periodikum|cd \(musik\)|grammofonplade|kassetteb\u00e5nd|musik \(net\)|musiktrack/)) {
            result = true;
          } else if (newSource !== "" && workTitle !== "" && newSource === workTitle && !workType.match(/anmeldelse|artikel|avis|tidsskrift|periodikum|cd \(musik\)|grammofonplade|kassetteb\u00e5nd|musik \(net\)|musiktrack/)) {
            result = true;
          } else if (newTitle !== "" && workSource !== "" && newTitle === workSource && !workType.match(/anmeldelse|artikel|avis|tidsskrift|periodikum|cd \(musik\)|grammofonplade|kassetteb\u00e5nd|musik \(net\)|musiktrack/)) {
            result = true;
          } else if (newTitle !== "" && workTitle !== "" && newTitle === workTitle && !workType.match(/anmeldelse|artikel|avis|tidsskrift|periodikum|cd \(musik\)|grammofonplade|kassetteb\u00e5nd|musik \(net\)|musiktrack/)) {
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

function makeworkobject( cargoXML )
{
    Log.info( "RLO: Entering javascript makeworkobject" );

    var dc = DcCreator.createWorkDc ( cargoXML );

    Log.info( "RLO, DC: " + dc );

    Log.info( "RLO: Leaving javascript" );

    return dc;

}
