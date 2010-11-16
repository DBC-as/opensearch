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
      resultArray[1] = String(XML_dc.dc::title);
    }
    if (String(XML_dc.dc::source) !== "") {
      resultArray[2] = "title";
      resultArray[3] = String(XML_dc.dc::source);
    }
    if (String(XML_dc.dc::title) !== "") {
      resultArray[4] = "source";
      resultArray[5] = String(XML_dc.dc::title);
    }
    if (String(XML_dc.dc::source) !== "") {
      resultArray[6] = "source";
      resultArray[7] = String(XML_dc.dc::source);
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
    var newTitle = String(newObjectXml.dc::title).toLowerCase();
    var workTitle = String(workObjectXml.dc::title).toLowerCase();
    var newSource = String(newObjectXml.dc::source).toLowerCase();
    var workSource = String(workObjectXml.dc::source).toLowerCase();
    var newCreator = String(newObjectXml.dc::creator).toLowerCase();
    var workCreator = String(workObjectXml.dc::creator).toLowerCase();
    var newType = String(newObjectXml.dc::type);
    var workType = String(workObjectXml.dc::type);
    var newContributors = [];
    var workContributors = [];
    var child;
    for each (child in newObjectXml.dc::contributor) {
      newContributors.push(String(child).toLowerCase());
    }
    for each (child in workObjectXml.dc::contributor) {
      workContributors.push(String(child).toLowerCase());
    }
    var newIdentifier = [];
    var workIdentifier = [];
    for each (child in newObjectXml.dc::identifier) {
      newIdentifier.push(String(child));
    }
    for each (child in workObjectXml.dc::identifier) {
      workIdentifier.push(String(child));
    }
    var newSubjects = [];
    var workSubjects = [];
    for each (child in newObjectXml.dc::subject) {
      newSubjects.push(String(child));
    }
    for each (child in workObjectXml.dc::subject) {
      workSubjects.push(String(child));
    }
    
    var newMaterial;
    switch (newType) {
      case "Anmeldelse": case "Anmeldelse (online)":
        newMaterial = "other";
        break;
      case "Artikel": case "Avisartikel": case "Tidsskriftsartikel": case "Artikel (online)":
        newMaterial = "article";
        break;
      case "Avis":
        newMaterial = "newspaper";
        break;
      case "Tidsskrift": case "Periodikum": case "Tidsskrift (online)":
        newMaterial = "journal";
        break;
      case "Dvd": case "Video": case "Blu-ray disc": case "Film (online)": case "PSP (film)":
        newMaterial = "movie";
        break;
      case "Cd": case "Grammofonplade": case "Musikalbum (online)":
        newMaterial = "music";
        break;
      case "Musiknummer (online)":
        newMaterial = "track";
        break;
      case "Bog": case "E-bog": case "Tegneserie": case "Graphic novel": case "Lydbog (cd)": case "Lydbog (b\u00e5nd)": case "Lydbog (cd-mp3)": case "Lydbog (online)": case "Kassettelydb\u00e5nd": case "Diskette":
        newMaterial = "literature";
        break;
      case "Playstation (spil)": case "Playstation 2 (spil)": case "Playstation 3 (spil)": case "Xbox (spil)": case "Xbox 360 (spil)": case "Nintendo DS (spil)": case "Wii (spil)": case "Pc-spil (Online)": case "Cd-rom": case "Dvd-rom": case "Gameboy (spil)": case "Gameboy Advance (spil)": case "PSP (spil)":
        newMaterial = "game";
        break;
      default:
        newMaterial = "other";
    }
    
    var workMaterial;
    switch (workType) {
      case "Anmeldelse": case "Anmeldelse (online)":
        workMaterial = "other";
        break;
      case "Artikel": case "Avisartikel": case "Tidsskriftsartikel": case "Artikel (online)":
        workMaterial = "article";
        break;
      case "Avis":
        workMaterial = "newspaper";
        break;
      case "Tidsskrift": case "Periodikum": case "Tidsskrift (online)":
        workMaterial = "journal";
        break;
      case "DVD": case "Video": case "Blu-ray disc": case "Film (online)": case "PSP (film)":
        workMaterial = "movie";
        break;
      case "CD": case "Grammofonplade": case "Musikalbum (online)":
        workMaterial = "music";
        break;
      case "Musiknummer (online)":
        workMaterial = "track";
        break;
      case "Bog": case "E-bog": case "Tegneserie": case "Graphic novel": case "Lydbog (cd)": case "Lydbog (b\u00e5nd)": case "Lydbog (cd-mp3)": case "Lydbog (online)": case "Kassettelydb\u00e5nd": case "Diskette":
        workMaterial = "literature";
        break;
      case "Playstation (spil)": case "Playstation 2 (spil)": case "Playstation 3 (spil)": case "Xbox (spil)": case "Xbox 360 (spil)": case "Nintendo DS (spil)": case "Wii (spil)": case "PC-spil (Online)": case "CD-rom": case "DVD-rom": case "Gameboy (spil)": case "Gameboy Advance (spil)": case "PSP (spil)":
        workMaterial = "game";
        break;
      default:
        workMaterial = "other";
    }

    Log.debug( "RLO: start match\n");
    //check for a match.
    switch (newMaterial) {
      case "other":
        Log.debug( "RLO: other\n");
        result = false;
        break;
      case "article":
        Log.debug( "RLO: article\n");
        if (newTitle === workTitle && newCreator === workCreator && workMaterial === "article") {
          result = true;
        } else {
          result = false;
        }
        break;
      case "newspaper":
        Log.debug( "RLO: newspaper\n");
        for (var a in newIdentifier) {
          Log.debug( "NEWIDENTIFIER: " + newIdentifier[a] );
          for (var b in workIdentifier) {
            Log.debug( "WORKIDENTIFIER: " + workIdentifier[a] );
            if (newIdentifier[a] === workIdentifier[b]) {
              Log.debug( "IDENTIFIERMATCH" );
              result = true;
            }
          }
        }
        if (result !== true && newTitle === workTitle && workMaterial === "newspaper") {
          Log.debug( "TITLEMATCH" );
          result = true;
        } else if (result !== true) {
          result = false;
        }
        break;
      case "journal":
        Log.debug( "RLO: journal\n");
        for (var a in newIdentifier) {
          for (var b in workIdentifier) {
            if (newIdentifier[a] === workIdentifier[b]) {
              Log.debug( "IDENTIFIERMATCH" );
              result = true;
            }
          }
        }
        if (result !== true && newTitle === workTitle && workMaterial === "journal") {
          Log.debug( "TITLEMATCH" );
          result = true;
        } else  if (result !== true) {
          result = false;
        }
        break;
      case "music":
        Log.debug("RLO: music\n");
        if (newTitle === workTitle && newCreator === workCreator && workType === "music") {
          result = true;
        } else if (newTitle === workTitle) {
          for (var a in newSubjects) {
            if (newSubjects[a] === "soundtracks" || newSubjects[a] === "filmmusik") {
              result = true;
            }
          }
        } else {
          result = false;
        }
        break;
      case "track": 
        Log.debug( "RLO: track\n");
        if (newTitle === workTitle && newCreator === workCreator && workMaterial === "track") {
          result = true;
        } else {
          result = false;
        }
        break;
      case "movie":
        Log.debug( "RLO: movie\n");
        if ((newTitle === workTitle || newTitle === workSource || workTitle === newTitle || workTitle === newSource) && newCreator === workCreator && workMaterial === "movie") {
          result = true;
        } else if (newTitle === workTitle && workMaterial === "literature") {
          for (var a in newContributors) {
            if (workCreator === newContributors[a]) {
              result = true;
            }
          }
        } else if (newTitle === workTitle) {
          for (var a in workSubjects) {
            if (workSubjects[a] === "soundtracks" || workSubjects[a] === "filmmusik") {
              result = true;
            } else if (workMaterial === "game") {
              result = true;
            }
          }
        } else {
          result = false;
        }
        break;
      case "literature":
        Log.debug( "RLO: literature\n");
        if ((newTitle === workTitle || newTitle === workSource || workTitle === newTitle || workTitle === newSource) && newCreator === workCreator && workMaterial === "literature") {
          result = true;
        } else if (newTitle === workTitle && workMaterial === "movie") {
          for (var a in workContributors) {
            if (newCreator === workContributors[a]) {
              result = true;
            }
          }
        } else if (newTitle === workTitle && workMaterial === "game") {
          result = true;
        } else {
          result = false;
        }
        break;
      case "game":
        Log.debug( "RLO: game\n");
        if ((newTitle === workTitle || newTitle === workSource || workTitle === newTitle || workTitle === newSource) && newCreator === workCreator && workMaterial === "game") {
          result = true;
        }
        else if (newTitle === workTitle && (workMaterial === "movie" || workMaterial === "literature")) {
          result = true;
        } else {
          result = false;
        }
        break;
      default:
        Log.debug( "RLO: default\n");
        result = false;
        break;
    }
    
  return result;

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
