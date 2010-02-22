
//importClass(Packages.dk.dbc.opensearch.common.metadata.DublinCoreElement);


// Takes a string representation of a marcxchange post
// and makes into an xml object where values are selected for another 
// xml object which is sent back to the caller 
function generateSearchPairs( cargoXML )
{
    var XML_cargo = new XML( cargoXML );
    //do stuff
    //right now its a dummy
 
    //put values in the pairArray that the calling java method looks in 
    //for searchpairs. Even indexes are fieldnames, uneven are values. Yes its hack'ish
    //but it works for now
    pairArray[0] = "title";
    pairArray[1] = "bastard*";
    pairArray[2] = "creator";
    pairArray[3] = "elif*";
} 


// Function that compares an object and a workobject. It gets their content
// in string representations.
function checkmatch( newObject, workObject )
{
    var XML_DC_newObject = new XML( newObject );
    var XML_DC_workOject = new XML( workObject );

    //check for a match. Return true if there is, false if not ;-)

    return false;
}


// Function that builds the originaldata and DC of a new workobject
// It gets a string representation of the content of the object it must
// be the workobject for and returns a string rep. of the content of the 
// workobject. The DC is given to the function and filled in as a sideeffect
function makeworkobject( cargoXML, workDC )
{
    var XML_cargo = new XML( cargoXML );
    var dc = new Namespace( "dc", "http://purl.org/dc/elements/1.1/" );
    //select the elements in the dc-xml that constitutes the work
    //do something with the xml and return it in string format
    print( "XML_cargo:"+ XML_cargo + "\n" );
   
    var creator = XML_cargo.dc::creator;
    var source = XML_cargo.dc::source;
    var title = XML_cargo.dc::title; 
    var type =  XML_cargo.dc::type;

    print( "type2: " +type+ "\n" );

    res = "<ting:container><dkabm:record>\n"    
    + "<dc:source>internal</dc:source>\n"                                                    
    + "<dc:title>"+title+"</dc:title>\n"
    + "<dc:source>"+source+"</dc:source>\n"
    + "<dc:type xsi:type=\"dkdcplus:BibDK-Type\">"+type+"</dc:type>\n"
    + "<dc:creator>"+creator+"</dc:creator>\n";    

    res = res +"</dkabm:record>\n</ting:container>";

    workDC.setTitle( title );
    workDC.setCreator( creator );
    workDC.setType( type );
    workDC.setSource( source );

    return res;
}