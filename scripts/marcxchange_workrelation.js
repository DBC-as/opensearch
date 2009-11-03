log.debug("ja7w: initial eval");

importClass(Packages.dk.dbc.opensearch.common.metadata.DublinCoreElement);

function generate_new_xml( title, type, creator, source ) {
  res = "<ting:container><dkabm:record>\n"    
    + "<dc:source>internal</dc:source>\n"                                                                  
    + "<dc:title>"+title+"</dc:title>\n"
    + "<dc:source>"+source+"</dc:source>\n"
    + "<dc:type xsi:type=\"dkdcplus:BibDK-Type\">"+type+"</dc:type>\n"
    + "<dc:creator>"+creator+"</dc:creator>\n";    

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
  
  return generate_new_xml( dcTitle, dcType, dcCreator, dcSource );
};
