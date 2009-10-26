log.debug("ja7w: initial eval");

importClass(Packages.dk.dbc.opensearch.common.metadata.DublinCoreElement);

function generate_new_xml( workid, title, type, creator, source ) {
  res = "<ting:container><dkabm:record>\n"
    + "<ac:identifier>"+workid+"|work</ac:identifier>\n"
    + "<ac:source>internal</ac:source>\n"                                                                  
    + "<dc:title>"+title+"</dc:title>\n"
    + "<dc:source>"+source+"</dc:source>\n"
    + "<dc:type xsi:type=\"dkdcplus:BibDK-Type\">"+type+"</dc:type>\n"
    + "<dc:creator>"+creator+"</dc:creator>\n";
    
   //    <dc:creator>Joanne K. Rowling</dc:creator>   
    //<dc:subject xsi:type="dkdcplus:DK5">83</dc:subject>
    //<dc:type xsi:type="dkdcplus:BibDK-Type">Bog</dc:type>
    //<dc:format>923 sider</dc:format>
                                               
    res = res +"</dkabm:record>\n</ting:container>";
  return res;
}

function generate_new_work( workcargo, dc, workdc ) {
  log.debug("ja7w: in generate_new_work");

  var dcTitle = plugin.normalizeString( dc.getDCValue( DublinCoreElement.ELEMENT_TITLE ) );
  var dcType = plugin.normalizeString( dc.getDCValue( DublinCoreElement.ELEMENT_TYPE ) );
  var dcCreator = plugin.normalizeString( dc.getDCValue( DublinCoreElement.ELEMENT_CREATOR ) );
  var dcSource = plugin.normalizeString( dc.getDCValue( DublinCoreElement.ELEMENT_SOURCE ) );

  log.debug(" ja7w: found dc title : " + dcTitle );
  log.debug(" ja7w: found dc type : " + dcType );
  log.debug(" ja7w: found dc Creator : " + dcCreator );
  log.debug(" ja7w: found dc dcSource : " + dcSource );

  workdc.setTitle( dcTitle );
  workdc.setCreator( dcCreator );
  workdc.setType( dcType );
  workdc.setSource( dcSource );
  
  return generate_new_xml( workcargo.getIdentifier(), dcTitle, dcType, dcCreator, dcSource );
};
