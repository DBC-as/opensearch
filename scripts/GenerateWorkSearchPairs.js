//the only function. Takes a string representation of a marcxchange post
// and makes into an xml object where values are selected for another 
// xml object which is sent back to the caller 
function generateSearchPairs( cargoXML )
{
    var XML_cargo = new XML( cargoXML );
    //do stuff
    //right now its a dummy
 
    pairArray[0] = "title";
    pairArray[1] = "Bastard*";
    pairArray[2] = "creator";
    pairArray[3] = "Elif*";

    //var pairsString = "<searchpairs><searchpair field=\"title\" value=<\"Bastard*\"></searchpair><searchpair field=\"creator\" value=\"Elif*\"></searchpair></searchpairs>";
    //return pairArray;
} 