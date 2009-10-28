
log.debug("ja7o: test parsing ");

// import meta packages.. to get IS_MEMBER_OF_*

importPackage(Packages.dk.dbc.opensearch.common.metadata);

subject_prefix="info:fedora/";

/*
 * 
 * Folkebib handling. 
 * 
 */
function doit_folkebib_getsubject( prefix,  format ) {
	if( format == "katalog") {
		return prefix + "catalog";
	} else {				
		return prefix + format;
	}
};

var folkebibmap= new Object; 

folkebibmap["775100"] = "aakb_";
folkebibmap["710100"] = "kkb_";

function get_folkebib_prefix( submitter ) {
	if( folkebibmap[ submitter ] == undefined ) {
	        log.error("Unknown folkebib submitter :" );
		throw new PluginException("Unknown folkebib submitter :" + submitter);
	}
	return folkebibmap[ submitter ];
};

function doit_folkebib(pid, submitter, format ) {
	subject = subject_prefix + doit_folkebib_getsubject( get_folkebib_prefix(submitter) , format );
	  
	objectRepository.addObjectRelation( pid, DBCBIB.IS_MEMBER_OF_COlECTION, subject);
}

/*
 *
 * handling of subjects 
 *
 */

var dbcmap = new Object;

dbcmap["anmeldelser"] = "free" ;
dbcmap["anmeld"] = "free" ;
dbcmap["materialevurderinger"] = "materialevurderinger" ;
dbcmap["matvurd"] = "materialevurderinger" ;
dbcmap["forfatterweb"] = "forfatterweb" ;
dbcmap["forfatterw"] = "forfatterweb" ;
dbcmap["faktalink"] = "faktalink" ;
dbcmap["dr_forfatteratlas"] = "free" ;
dbcmap["dr_atlas"] = "free" ;
dbcmap["dr_bonanza"] = "free" ;
dbcmap["louisiana"] = "louisiana" ;
dbcmap["artikler"] = "artikler" ;
dbcmap["dsd"] = "free" ;
dbcmap["pg"] = "pg";

function lookup_dbcmap( format ) {
  if( dbcmap[ format ] == undefined ) {
    log.error("Unknown folkebib submitter :" + submitter);
    throw new PluginException("Unknown folkebib submitter :" + submitter);
  } else {
    return subject_prefix + dbcmap[ format ];
  }
}

function doit_dbc( pid, submitter, format ) {
  subject = subject_prefix + lookup_dbcmap( format ) ;
  objectRepository.addObjectRelation( pid, DBCBIB.IS_MEMBER_OF_COlECTION, subject );
  // add ekstra pg data.
  if( "pg" == format ) {
	  objectRepository.addObjectRelation( pid, DBCBIB.IS_MEMBER_OF_COlECTION, subject_prefix + "Children" );
	  objectRepository.addObjectRelation( pid, DBCBIB.IS_MEMBER_OF_COlECTION, subject_prefix + "free" );	  
  }  
}

function doit( pid, submitter, format) {
// Add the relation.  
  log.debug("ja7o: entering doit");
  if( submitter.charAt(0) == "7") {
	  doit_folkebib( pid, submitter, format );
	  return ;
  } else if( submitter == "dbc" ) {
    doit_dbc( pid, submitter, format );
  } else {
	log.warn("Unknown submitter - no owner relations set. ")    
  }
}             
