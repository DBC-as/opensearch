use ( "XmlUtil" );
use ( "XmlNamespaces" );
use ( "DbcAddiRelations" );
use ( "DbcBibRelations" );
use ( "Dcterms" );
use ( "Normalize" );


EXPORTED_SYMBOLS = ['Relations'];

var Relations = function() {

  var dkabm = XmlNamespaces.dkabm;
  var dc = XmlNamespaces.dc;
  var ac = XmlNamespaces.ac;
  var dcterms = XmlNamespaces.dcterms;
  var xsi = XmlNamespaces.xsi;
  var ting = XmlNamespaces.ting;
  var oso = XmlNamespaces.oso;
	var oss = XmlNamespaces.oss;

  var that = {};

  that.isReviewOf = function ( xml, pid ) {

    Log.info ("Start isReviewOf" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var reviewXML = XmlUtil.fromString( xml );

    var identifier = String(reviewXML.*.*.*.(@tag=='014').*.(@code=='a'));

    Log.info( "Identifier: " + identifier );
    Log.info( "pid: " + pid );

    var results = FedoraPIDSearch.pid( "*:" + identifier ); // wildcardsearch (only possible in PID).

    for ( var i = 0; i < results.length; ++i ) {
      var result = results[i];

      Log.info( "result: " + result );

      if (!String(result).match(/work:.*/)) {
        DbcAddiRelations.isReviewOf( pid, result);
      }
    }

    if (i === 0) {
      if (String(reviewXML.dkabm::record.dcterms::references.@xsi::type) === "dkdcplus:ISBN") {
        var relation = "ISBN:" + String(reviewXML.dkabm::record.dcterms::references);

        var results = FedoraPIDSearch.identifier( relation );

        for ( var i = 0; i < results.length; ++i ) {
          var result = results[i];

          Log.info( "result: " + result );

          if (!String(result).match(/work:.*/)) {
            DbcAddiRelations.isReviewOf( pid, result );
          }
        }
      }
    }

    Log.info ("End isReviewOf" );

  };

  that.hasReview = function ( xml, pid) {

    Log.info ("Start hasReview" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var katalogXML = XmlUtil.fromString( xml );

    var identifier = String(katalogXML.*.*.*.(@tag=='001').*.(@code=='a'));

    Log.info( "Identifier: " + identifier );    
    Log.info( "pid: " + pid );

    var results = FedoraPIDSearch.relation( identifier );

    for ( var i = 0; i < results.length; ++i ) {
      var result = results[i];

      Log.info( "result: " + result );

      if (!String(result).match(/work:.*/)) {
        DbcAddiRelations.isReviewOf( result, pid );
      }
    }

    if (i === 0) {
      if (String(katalogXML.dkabm::record.dc::identifier.@xsi::type).match(/dkdcplus:ISBN/)) {
        var identifier = "ISBN:" + String(katalogXML.dkabm::record.dc::identifier);

        Log.info( "Identifier: " + identifier );    
        Log.info( "pid: " + pid );

        var results = FedoraPIDSearch.relation( identifier );

        for ( var i = 0; i < results.length; ++i ) {
          var result = results[i];

          Log.info( "result: " + result );

          if (String(result).match(/150005:.*/)) {
            DbcAddiRelations.isReviewOf( result, pid );
          }
        }
      }
    }

    Log.info ("End hasReview" );

  };
  
  that.isAnalysisOf = function ( xml, pid ) {

    Log.info ("Start isAnalysisOf" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var analysisXML = XmlUtil.fromString( xml );
		
		//lav en linje der matcher på source, og hvis litteraturtolkninger så gør en ting, ellers gør noget andet - eller lav en match-case
		var child;
		var personName;
		var personNames = [];
		var analysedTitle; 
		var analysedTitles = [];
		var query;
				
	  if (String(analysisXML.dkabm::record.ac::source).match(/Litteraturtolkninger/)) {
        //fremfind efternavn og fornavne
        //for each (child in analysisXML.*.*.*.(@tag=='600').*.(@code=='a')) //efternavn
        for each (child in analysisXML.*.*.*.(@tag=='600')) {
          var first = String(child.*.(@code=='h'));  //skal child nu laves om til XmlUtil.fromString for at kunne tage fat i subfield??
          var last = String(child.*.(@code=='a'));
					var born = String(child.*.(@code=='c'));
					last = " " + last;
          personName = first + last;
					if (born !== ""){
						personName = personName + " \(" + born + "\)";
					}
					Log.info("kwc4 personName: " + personName);
          personNames.push (personName);
        }
				
				for each (child in analysisXML.*.*.*.(@tag=='666').*.(@code=='t')) {
					analysedTitle = String(child);
					analysedTitles.push (analysedTitle);
				}
				for (var j = 0; j < analysedTitles.length; ++j){
					Log.info("kwc7 analysedTitle: " + analysedTitles[j]);
				}

        for (var i = 0; i < personNames.length; ++i ) {
					
					Log.info("kwc5 personName: " + personNames[i]);

					
        }
				  var xx,yy;
			//		for (xx = 0, yy = 0; xx < personNames.length && yy < analysedTitles.length; ++xx, ++yy ) {
			//			query = "\"creator \u003D " + personNames[xx] + " AND " + "title \u003D " + analysedTitles[yy] + "\""; 
						
					for (xx = 0; xx < analysedTitles.length; ++xx ) {
					query = "title \u003D " + analysedTitles[xx];  
					
         	
					Log.info("kwc6 query: " + query);
					var results = FedoraCQLSearch.search(query); 
				  //var results = FedoraPIDSearch.creator( personNames[i] );

        		for (var ii = 0; ii < results.length; ++ii) {
          		var result = results[ii]; //

          		Log.info("kwc1 result: " + result);            

          		if (!String(result).match(/work:.*/)) {
          		DbcAddiRelations.isAnalysisOf(pid, result);
          		}
        		}	  
 				}
			
			
		}  else if (String(analysisXML.dkabm::record.dcterms::references.@xsi::type) === "dkdcplus:ISBN") {
      var relation = "ISBN:" + String(analysisXML.dkabm::record.dcterms::references);

      var results = FedoraPIDSearch.identifier( relation );

      for ( var i = 0; i < results.length; ++i ) {
        var result = results[i];

        Log.info( "result: " + result );

        if (!String(result).match(/work:.*/)) {
          DbcAddiRelations.isAnalysisOf( pid, result );
        }
      }
    }

    Log.info ("End isAnalysisOf" );

  };
  
  that.references = function ( xml, pid ) {

    Log.info ("Start references" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var referenceXML = XmlUtil.fromString( xml );

    var child;
    for each (child in referenceXML.ting::originalData.link) {
      var relation = "150026:" + String(child.@objectExtId).replace(/:/, "");
    
      var results = FedoraPIDSearch.identifier( relation );
    
      for ( var i = 0; i < results.length; ++i ) {
        var result = results[i];
    
        Log.info( "result: " + result );
    
        if (!String(result).match(/work:.*/)) {
          Dcterms.references( pid, result );
        }
      }
    }

    Log.info ("End references" );

  };
  
  that.isReferencedBy = function ( xml, pid ) {

    Log.info ("Start isReferencedBy" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var referenceXML = XmlUtil.fromString( xml );

    var relation = String(referenceXML.oso::object.oso::identifier).replace(/(.*)\|.*/, "$1");
  
      var results = FedoraPIDSearch.relation( relation );
  
      for ( var i = 0; i < results.length; ++i ) {
        var result = results[i];
  
        Log.info( "result: " + result );
  
        if (!String(result).match(/work:.*/)) {
          Dcterms.references( result, pid );
        }
      }

    Log.info ("End isReferencedBy" );

  };
  
  that.hasCreator = function ( xml, pid ) {

    Log.info ("Start creator" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var referenceXML = XmlUtil.fromString( xml );

    if (String(referenceXML.ting::originalData.link.@objectType) === "Kunstner") {
      var relation = "150026:" + String(referenceXML.ting::originalData.link.@objectExtId).replace(/:/, "");
  
      var results = FedoraPIDSearch.identifier( relation );
  
      for ( var i = 0; i < results.length; ++i ) {
        var result = results[i];
  
        Log.info( "result: " + result );
  
        if (!String(result).match(/work:.*/)) {
          Dcterms.creator( pid, result );
        }
      }
    }

    Log.info ("End creator" );

  };
  
  that.isCreatorOf = function ( xml, pid ) {

    Log.info ("Start isCreatorOf" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var referenceXML = XmlUtil.fromString( xml );

    var creator = String(referenceXML.ting::originalData.navn).replace(/(.*), (.*)/, "$2 $1");

    var results = FedoraPIDSearch.creator( creator );

    for ( var i = 0; i < results.length; ++i ) {
      var result = results[i];

      Log.info( "result: " + result );

      if (String(result).match(/150026:.*/)) {
        Dcterms.creator( result, pid );
      }
    }

    Log.info ("End isCreatorOf" );

  };

  that.isPartOfManifestation = function ( xml, pid) {

    Log.info ("Start isPartOfManifestation" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var articleXML = XmlUtil.fromString( xml );

    var relation = String(articleXML.*.*.*.(@tag=='016').*.(@code=='a'));

    Log.info( "Relation: " + relation );
    Log.info( "pid: " + pid );

    var results = FedoraPIDSearch.pid( "*:" + relation );

    for ( var i = 0; i < results.length; ++i ) {
      var result = results[i];

      Log.info( "result: " + result );

      if (!String(result).match(/work:.*/)) {
        DbcBibRelations.isPartOfManifestation( pid, result );
      }
    }


    var child;

    for each (child in articleXML.dkabm::record.dcterms::isPartOf) {
      if (String(child.@xsi::type).match("dkdcplus:ISSN")) {
        Log.debug ( "Attribute: " + String(child.@xsi::type));
        Log.debug ( "Child: " + child );
        var identifier = "ISSN:" + String(child).replace(/-/g, "");
      }
    }

    Log.info( "Identifier: " + identifier );
    Log.info( "pid: " + pid );

    var results = FedoraPIDSearch.identifier( identifier );

    for ( var i = 0; i < results.length; ++i ) {
      var result = results[i];

      Log.info( "result: " + result );

      if (!String(result).match(/work:.*/)) {
        DbcBibRelations.isPartOfManifestation( pid, result );
      }
    }

    Log.info ("End isPartOfManifestation" );

  };

  that.hasArticle = function ( xml, pid) {

    Log.info ("Start hasArticle" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var manifestationXML = XmlUtil.fromString( xml );

    var identifier = "PartOf:" + String(manifestationXML.*.*.*.(@tag=='001').*.(@code=='a'));

    Log.info( "Identifier: " + identifier );    
    Log.info( "pid: " + pid );

    var results = FedoraPIDSearch.relation( identifier );

    for ( var i = 0; i < results.length; ++i ) {
      var result = results[i];

      Log.info( "result: " + result );

      if (!String(result).match(/work:.*/)) {
        DbcBibRelations.isPartOfManifestation( result, pid );
      }
    }

    var child;

    for each (child in manifestationXML.dkabm::record.dc::identifier) {
      if (String(child.@xsi::type).match("dkdcplus:ISSN")) {
        Log.debug ( "Attribute: " + String(child.@xsi::type));
        Log.debug ( "Child: " + child );
        var identifier = "ISSN:" + String(child).replace(/-/g, "");
      }
    }

    Log.info( "Identifier: " + identifier );    
    Log.info( "pid: " + pid );

    var results = FedoraPIDSearch.relation( identifier );

    for ( var i = 0; i < results.length; ++i ) {
      var result = results[i];

      Log.info( "result: " + result );

      if (!String(result).match(/work:.*/)) {
        DbcBibRelations.isPartOfManifestation( result, pid );
      }
    }

    Log.info ("End hasArticle" );

  };


  that.isPartOfAlbum = function ( xml, pid) {

    Log.info ("Start isPartOfAlbum" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var trackXML = XmlUtil.fromString( xml );

    var child;

    for each (child in trackXML.dkabm::record.dcterms::isPartOf) {
      if (String(child.@xsi::type).match("dkdcplus:albumId")) {
        Log.debug ( "Attribute: " + String(child.@xsi::type));
        Log.debug ( "Child: " + child);
        var identifier = child;
      }
    }

    Log.info( "Identifier: " + identifier );    
    Log.info( "pid: " + pid );

    var results = FedoraPIDSearch.pid( "150014:" + identifier );

    for ( var i = 0; i < results.length; ++i ) {
      var result = results[i];

      Log.info( "result: " + result );

      if (!String(result).match(/work:.*/)) {
        DbcBibRelations.isPartOfAlbum( pid, result );
      }
    }

    Log.info ("End isPartOfAlbum" );

  };

  that.hasTrack = function ( xml, pid) {

    Log.info ("Start hasTrack" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var albumXML = XmlUtil.fromString( xml );

    var identifier = "albumId:" + String(albumXML.dkabm::record.ac::identifier).replace(/\|150014/, "");

    Log.info( "Identifier: " + identifier );    
    Log.info( "pid: " + pid );

    var results = FedoraPIDSearch.relation( identifier );

    for ( var i = 0; i < results.length; ++i ) {
      var result = results[i];

      Log.info( "result: " + result );

      if (!String(result).match(/work:.*/)) {
        DbcBibRelations.isPartOfAlbum( result, pid );
      }
    }

    Log.info ("End hasTrack" );

  };
  
  that.isCreatorDescriptionOf = function ( xml, pid ) {

    Log.info ("Start isCreatorDescriptionOf" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var creatorXML = XmlUtil.fromString( xml );
    
    var child;

    var creator = String(creatorXML.dkabm::record.dc::title[0]);

    Log.info( "Creator: " + creator );    
    Log.info( "pid: " + pid );

    if (creator !== "undefined") {
      var results = FedoraPIDSearch.creator(creator);
      
      for (var i = 0; i < results.length; ++i) {
        var result = results[i];
        
        Log.info("result: " + result);
        
        if (!String(result).match(/work:.*/)) {
          DbcAddiRelations.hasCreatorDescription(result, pid);
        }
      }
    }

    Log.info ("End isCreatorDescriptionOf" );

  };

  that.hasCreatorDescription = function ( xml, pid ) {

    Log.info ("Start hasCreatorDescription" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var manifestationXml = XmlUtil.fromString( xml );
    
    var creator = String(manifestationXml.dkabm::record.dc::creator[0]);
        
    if (creator !== "undefined") {
      var results = FedoraPIDSearch.title( Normalize.removeSpecialCharacters( creator ) );

      for ( var i = 0; i < results.length; ++i ) {
        var result = results[i];

        Log.info( "result: " + result );

        if ((String(result).match(/150016:.*/) || String(result).match(/150005:.*/)) && !String(result).match(/image/)) {
          DbcAddiRelations.hasCreatorDescription( pid, result );
        }
      }
		}
    
    Log.info ("End hasCreatorDescription" );

  };

  that.isSubjectDescriptionOf = function ( xml, pid ) {

    Log.info ("Start isSubjectDescriptionOf" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var subjectXML = XmlUtil.fromString( xml );

    var title = String(subjectXML.dkabm::record.dc::title[0]);

    Log.info( "Title: " + title );    
    Log.info( "pid: " + pid );

    if (title !== "undefined") {
      var results = FedoraPIDSearch.subject(title);
      
      for (var i = 0; i < results.length; ++i) {
        var result = results[i];
        
        Log.info("result: " + result);
        
        if (!String(result).match(/work:.*/) && !String(result).match(/150012:.*/) && !String(result).match(/150033:.*/)) {
          DbcAddiRelations.hasSubjectDescription(result, pid);
        }
      }
    }

    Log.info ("End isSubjectDescriptionOf" );

  };

  that.hasSubjectDescription = function ( xml, pid ) {

    Log.info ("Start hasSubjectDescription" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var manifestationXML = XmlUtil.fromString( xml );

    var child;

    for each(child in manifestationXML.dkabm::record.dc::subject) {
      if (!String(child.@xsi::type).match("dkdcplus:genre")) {
	      var subject = String(child);
	
	      Log.info( "Subject: " + subject );
	      Log.info( "pid: " + pid );
	
	      var results = FedoraPIDSearch.title( Normalize.removeSpecialCharacters( subject ) );
	
	      for ( var i = 0; i < results.length; ++i ) {
	        var result = results[i];
	
	        Log.info( "result: " + result );
	
	        if (String(result).match(/150017:.*/) || String(result).match(/150012:.*/) || String(result).match(/150033:.*/)) {
	          DbcAddiRelations.hasSubjectDescription( pid, result );
	        }
	      }
			}
    }

    Log.info ("End hasSubjectDescription" );

  };

  that.isImageOf = function ( xml, pid ) {

    Log.info ("Start isImageOf" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var imageXML = XmlUtil.fromString( xml );

    var identifier = String(imageXML.oso::object.oso::identifier).replace( /(.*)image\|(.*)/, "$2:$1");

    Log.info( "Identifier isImageOf: " + identifier );
    Log.info( "pid isImageOf: " + pid );

    var results = FedoraPIDSearch.pid( identifier );

    for ( var i = 0; i < results.length; ++i ) {
      var result = results[i];

      Log.info( "result: " + result );

      if (!String(result).match(/work:.*/)) {
        DbcAddiRelations.isImageOf( pid, result );
      }
    }

    Log.info ("End isImageOf" );

  };

  that.hasImage = function ( xml, pid ) {

    Log.info ("Start hasImage" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var manifestationXML = XmlUtil.fromString( xml );

    var identifier = String(manifestationXML.dkabm::record.ac::identifier).replace( /(.*)\|(.*)/, "$2:$1image");

    Log.info( "Identifier hasImage: " + identifier );
    Log.info( "pid hasImage: " + pid );

    var results = FedoraPIDSearch.pid( identifier );

      for ( var i = 0; i < results.length; ++i ) {
        var result = results[i];

        Log.info( "result: " + result );

        var NS = "http://oss.dbc.dk/rdf/dbcaddi#";

        if (!String(result).match(/work:.*/)) {
          DbcAddiRelations.isImageOf( result, pid );
        }
      }

    Log.info ("End hasImage" );

  };

  that.hasSoundClip = function( xml, pid ) {

    Log.info ("Start hasSoundClip" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var trackXML = XmlUtil.fromString( xml );

    var url = String( "http://static.shop2download.com/samples/" + trackXML.*.*.*.soundClip);

    DbcAddiRelations.hasSoundClip( pid, url );

    Log.info ("End hasSoundClip" );

  };
  
  that.hasOnlineAccess = function( xml, pid ) {

    Log.info ("Start hasOnlineAccess" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var manifestationXML = XmlUtil.fromString( xml );
    
    var child;
    if (String(manifestationXML.dkabm::record.ac::source).match(/Litteratursiden|Faktalink|Forfatterweb|Spil og Medier/)) {
      for each (child in manifestationXML.dkabm::record.dc::identifier) {
        if (String(child.@xsi::type).match("dcterms:URI")) {
          DbcAddiRelations.hasOnlineAccess( pid, String(child) );
        }
      }
		} else if (String(manifestationXML.dkabm::record.ac::source).match(/Gale/)) {
				DbcAddiRelations.hasOnlineAccess ( pid, "[URL]/ic/scic/ReferenceDetailsPage/ReferenceDetailsWindow?displayGroupName=Reference&disableHighlighting=false&prodId=SCIC&action=e&windowstate=normal&catId=&documentId=GALE%7C" + String(manifestationXML.dkabm::record.dc::identifier) + "&mode=view[URL-suffix]");			
    } else if (String(manifestationXML.dkabm::record.ac::source).match(/Ebsco|Ebrary/)){
      	DbcAddiRelations.hasOnlineAccess ( pid, "[URL]" + String(manifestationXML.dkabm::record.ac::identifier).replace(/\|.*/, ""));
		} else if (String(manifestationXML.dkabm::record.ac::source).match(/Samfundsfaget|Religionsfaget|Dansk historie/)) {
				DbcAddiRelations.hasOnlineAccess ( pid, "[URL]" + String(manifestationXML.dkabm::record.dc::identifier));
		} else if (String(manifestationXML.dkabm::record.ac::identifier).match(/t[0-9]+\|150031/)) {
				for each (child in manifestationXML.dkabm::record.dc::identifier) {
        if (String(child.@xsi::type).match("dcterms:URI")) {
          DbcAddiRelations.hasOnlineAccess( pid, String(child) );
        }
      }
    } else if (String(manifestationXML.dkabm::record.ac::source).match(/bibzoom \(tracks\)/i)) {
				var identifier = String(manifestationXML.dkabm::record.ac::identifier).replace( /(.*)\|(.*)/, "$2:$1dlink");

    		Log.info( "Identifier: " + identifier );
    		Log.info( "pid: " + pid );

    		var results = FedoraPIDSearch.pid( identifier );

      	for ( var i = 0; i < results.length; ++i ) {
        	var result = results[i];

        	Log.info( "result: " + result );

        	if (!String(result).match(/work:.*/)) {
          	DbcAddiRelations.hasOnlineAccess( pid, result );
        	}
      	}
				identifier = String(manifestationXML.dkabm::record.ac::identifier).replace( /(.*)\|(.*)/, "$2:$1slink");

    		Log.info( "Identifier: " + identifier );
    		Log.info( "pid: " + pid );

    		var results = FedoraPIDSearch.pid( identifier );

      	for ( var i = 0; i < results.length; ++i ) {
        	var result = results[i];

        	Log.info( "result: " + result );

        	if (!String(result).match(/work:.*/)) {
          	DbcAddiRelations.hasOnlineAccess( pid, result );
        	}
      	}
			} else if (String(manifestationXML.dkabm::record.ac::identifier).match(/s[0-9]+\|150031/)) {
				var identifier = String(manifestationXML.dkabm::record.ac::identifier).replace( /(.*)\|(.*)/, "$2:$1speak");

    		Log.info( "Identifier hasOnlineAccess: " + identifier );
    		Log.info( "pid hasOnlineAccess: " + pid );

    		var results = FedoraPIDSearch.pid( identifier );

      	for ( var i = 0; i < results.length; ++i ) {
        	var result = results[i];

        	Log.info( "result: " + result );

        	if (!String(result).match(/work:.*/)) {
          	DbcAddiRelations.hasOnlineAccess( pid, result );
        	}
      	}
				identifier = String(manifestationXML.dkabm::record.ac::identifier).replace( /(.*)\|(.*)/, "$2:$1text");

    		Log.info( "Identifier hasOnlineAccess: " + identifier );
    		Log.info( "pid hasOnlineAccess: " + pid );

    		var results = FedoraPIDSearch.pid( identifier );

      	for ( var i = 0; i < results.length; ++i ) {
        	var result = results[i];

        	Log.info( "result: " + result );

        	if (!String(result).match(/work:.*/)) {
          	DbcAddiRelations.hasOnlineAccess( pid, result );
        	}
      	}
			}
		
		if (String(manifestationXML.*.*.*.(@tag=='538').*.(@code=='i')) === "Infomedia") {
      var url = String("[useraccessinfomedia]?action=getArticle&faust=" + manifestationXML.*.*.*.(@tag=='001').*.(@code=='a') + "&libraryCode=[libraryCode]&userId=[userId]&userPinCode=[userPinCode]");
      DbcAddiRelations.hasOnlineAccess( pid, url );
    }

    Log.info ("End hasOnlineAccess" );

  };

  that.isOnlineAccessOf = function ( xml, pid ) {

    Log.info ("Start isOnlineAccessOf" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var imageXML = XmlUtil.fromString( xml );

		if (String(imageXML.dkabm::record.ac::source).match(/bibzoom \(tracks\)/i)) {
    	var identifier = String(imageXML.oso::object.oso::identifier).replace( /(.*)dlink\|(.*)/, "$2:$1");

    	Log.info( "Identifier: " + identifier );
    	Log.info( "pid: " + pid );

    	var results = FedoraPIDSearch.pid( identifier );

    	for ( var i = 0; i < results.length; ++i ) {
      	var result = results[i];

      	Log.info( "result: " + result );

      	if (!String(result).match(/work:.*/)) {
        	DbcAddiRelations.hasOnlineAccess( result, pid );
      	}
    	}
			identifier = String(imageXML.oso::object.oso::identifier).replace( /(.*)slink\|(.*)/, "$2:$1");

    	Log.info( "Identifier: " + identifier );
    	Log.info( "pid: " + pid );

    	var results = FedoraPIDSearch.pid( identifier );

    	for ( var i = 0; i < results.length; ++i ) {
      	var result = results[i];

      	Log.info( "result: " + result );

      	if (!String(result).match(/work:.*/)) {
        	DbcAddiRelations.hasOnlineAccess( result, pid );
      	}
    	}
		} else if (String(imageXML.dkabm::record.ac::identifier).match(/.*\|150031/)) {
			identifier = String(imageXML.oso::object.oso::identifier).replace( /(.*)speak\|(.*)/, "$2:$1");

	    Log.info( "Identifier: " + identifier );
	    Log.info( "pid: " + pid );
	
	    var results = FedoraPIDSearch.pid( identifier );

	    for ( var i = 0; i < results.length; ++i ) {
	      var result = results[i];
	
	      Log.info( "result: " + result );

	      if (!String(result).match(/work:.*/)) {
	        DbcAddiRelations.hasOnlineAccess( result, pid );
	      }
	    }
			identifier = String(imageXML.oso::object.oso::identifier).replace( /(.*)text\|(.*)/, "$2:$1");

    	Log.info( "Identifier: " + identifier );
    	Log.info( "pid: " + pid );

    	var results = FedoraPIDSearch.pid( identifier );

    	for ( var i = 0; i < results.length; ++i ) {
      	var result = results[i];

      	Log.info( "result: " + result );

      	if (!String(result).match(/work:.*/)) {
        	DbcAddiRelations.hasOnlineAccess( result, pid );
      	}
	    }
		}	

	    Log.info ("End isOnlineAccessOf" );

  };
  
  that.hasFilmstribenFSSOnlineAccess = function( xml, pid ) {

    Log.info ("Start hasFilmstribenFSSOnlineAccess" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var manifestationXML = XmlUtil.fromString( xml );

    if (String(manifestationXML.*.*.*.(@tag=='032').*.(@code=='x')).match(/FSS/)) {
      var id = String(manifestationXML.*.*.*.(@tag=='856').*.(@code=='u')).replace(/http:\/\/www.filmstriben.dk\/\?showfilm=(.*)/, "$1");
      var url = String("http://www.filmstriben.dk/skole/filmdetails.aspx?id=" + id);
      DbcAddiRelations.hasOnlineAccess( pid, url );
    }
    
    Log.info ("End hasFilmstribenFSSOnlineAccess" );

  };
  
  that.hasFilmstribenFSFOnlineAccess = function( xml, pid ) {

    Log.info ("Start hasFilmstribenFSFOnlineAccess" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var manifestationXML = XmlUtil.fromString( xml );

    if (String(manifestationXML.*.*.*.(@tag=='032').*.(@code=='x')).match(/FSF/)) {
      var id = String(manifestationXML.*.*.*.(@tag=='856').*.(@code=='u')).replace(/http:\/\/www.filmstriben.dk\/\?showfilm=(.*)/, "$1");
      var url = String("http://www.filmstriben.dk/fjernleje/filmdetails.aspx?id=" + id);
      DbcAddiRelations.hasOnlineAccess( pid, url );
    }
    
    Log.info ("End hasFilmstribenFSFOnlineAccess" );

  };

  that.hasFilmstribenFSBOnlineAccess = function( xml, pid ) {

    Log.info ("Start hasFilmstribenFSBOnlineAccess" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var manifestationXML = XmlUtil.fromString( xml );

    if (String(manifestationXML.*.*.*.(@tag=='032').*.(@code=='x')).match(/FSB/)) {
      var id = String(manifestationXML.*.*.*.(@tag=='856').*.(@code=='u')).replace(/http:\/\/www.filmstriben.dk\/\?showfilm=(.*)/, "$1");
      var url = String("http://www.filmstriben.dk/bibliotek/filmdetails.aspx?id=" + id);
      DbcAddiRelations.hasOnlineAccess( pid, url );
    }
    
    Log.info ("End hasFilmstribenFSBOnlineAccess" );

  };

  that.hasSpecificOnlineAccess = function( xml, pid ) {

    Log.info ("Start hasSpecificOnlineAccess" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var manifestationXML = XmlUtil.fromString( xml );

    if (String(manifestationXML.*.*.*.(@tag=='032').*.(@code=='x')).match(/FSF/)) {
      var id = String(manifestationXML.*.*.*.(@tag=='856').*.(@code=='u')).replace(/http:\/\/www.filmstriben.dk\/\?showfilm=(.*)/, "$1");
      var url = String("http://www.filmstriben.dk/fjernleje/filmdetails.aspx?id=" + id);
      DbcAddiRelations.hasRemoteAccess( pid, url );
    }
    if (String(manifestationXML.*.*.*.(@tag=='032').*.(@code=='x')).match(/FSB/)) {
      var id = String(manifestationXML.*.*.*.(@tag=='856').*.(@code=='u')).replace(/http:\/\/www.filmstriben.dk\/\?showfilm=(.*)/, "$1");
      var url = String("http://www.filmstriben.dk/bibliotek/filmdetails.aspx?id=" + id);
      DbcAddiRelations.hasOnSiteAccess( pid, url );
    }

    Log.info ("End hasSpecificOnlineAccess" );

  };
  
  that.hasCreatorHomePage = function( xml, pid ) {

    Log.info ("Start hasCreatorHomePage" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var manifestationXML = XmlUtil.fromString( xml );

    if (String(manifestationXML.*.*.*.(@tag=='529').*.(@code=='a')).match(/MySpace/)) {
      var url = String(manifestationXML.*.*.*.(@tag=='529').*.(@code=='u'));
      DbcAddiRelations.hasCreatorHomePage( pid, url );
    }

    Log.info ("End hasCreatorHomePage" );

  };
	
	that.hasOpenUrl = function( xml, pid ) {

    Log.info ("Start hasOpenUrl" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var manifestationXML = XmlUtil.fromString( xml );

    if (String(manifestationXML.oss::openurl) !== "") {
      var url = String(manifestationXML.oss::openurl);
      DbcAddiRelations.hasOpenUrl( pid, url );
    }

    Log.info ("End hasOpenUrl" );

  };

  return that;

}();
