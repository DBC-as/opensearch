//the only function. Takes a string representation of a marcxchange post
// and makes into an xml object where values are selected for another 
// xml object which is sent back to the caller 

//Should be part of a file containing the other functions used
//in making the workrelations
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


function checkmatch( newObject, workObject )
{
    var XML_DC_newObject = new XML( newObject );
    var XML_DC_workOject = new XML( workObject );

    //check for a match. Return true if there is, false if not ;-)

    return false;
}

function makeworkobject( cargoXML )
{
    var XML_cargo = new XML( cargoXML );
    var ns = new Namespace( "dc", "http://purl.org/dc/elements/1.1" );
    //select the elements in the dc-xml that constitutes the work
    //do something with the xml and return it in string format
    print( "XML_cargo:"+ XML_cargo + "\n" );
  
    print( "humle:" + XML_cargo.humle +"\n" );
    //XML_cargo.dc.appendChild(<identifier/>);
    //print( "XML_cargo:"+ XML_cargo + "\n" );
    
    print( "title:" + XML_cargo.ns::title +"\n" );
    //var title = XML_cargo.ns::title;
    print( "XML_cargo:"+ XML_cargo + "\n" );
    //print( "title :"+ title +"\n" );
    
    //title = XML_cargo.dc.title;
    //print( "title: " + title + "\n" );
    title = "Bastarden fra Istanbul";
    source = "Bastarden fra Istanbul";
    type = "bog";
    creator ="Elif Shafak";

    res = "<ting:container><dkabm:record>\n"    
    + "<dc:source>internal</dc:source>\n"                                                    
    + "<dc:title>"+title+"</dc:title>\n"
    + "<dc:source>"+source+"</dc:source>\n"
    + "<dc:type xsi:type=\"dkdcplus:BibDK-Type\">"+type+"</dc:type>\n"
    + "<dc:creator>"+creator+"</dc:creator>\n";    

    res = res +"</dkabm:record>\n</ting:container>";

//     var xml_res = new XML( res );
//     print("xml_res:\n" + xml_res +"\n");
    //return title;
    return res;
}