use ( "XmlUtil.use.js" );
use ( "XmlNamespaces.use.js" );
use ( "DbcAddiRelations.use.js" );
use ( "DbcBibRelations.use.js" );
use ( "Normalize.use.js" );


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
		var child;

    var identifier = String(reviewXML.*.*.*.(@tag=='014').*.(@code=='a'));

    Log.info( "Identifier: " + identifier );
    Log.info( "pid: " + pid );

    var results = FedoraPIDSearch.pid( "*:" + identifier ); // wildcardsearch (only possible in PID).

    for ( var i = 0; i < results.length; ++i ) {
      var result = results[i];

      if (!String(result).match(/work:.*/)) {
        DbcAddiRelations.isReviewOf( pid, result);
      }
    }

    if (i === 0) {
			if ( String(reviewXML.dkabm::record.ac::source).match(/Litteratursiden/) && String(reviewXML.dkabm::record.dc::type).match(/Anmeldelse/) ) {
				var reviewedCreator = String(reviewXML.dkabm::record.dc::subject[0]);	
				reviewedCreator = Normalize.removeSpecialCharacters(reviewedCreator);	
				var reviewedTitle = String(reviewXML.dkabm::record.dc::subject[1]);
				reviewedTitle = Normalize.removeSpecialCharacters(reviewedTitle);
				var query = "creator = " + reviewedCreator + " AND title = " + reviewedTitle;
				Log.debug( "query: " + query );
			
				var results = FedoraCQLSearch.search( query );
			
				for ( var j = 0; j < results.length; ++j ) {
					var result = results[j]
				
					Log.info( "result: " + result );
				
					if (!String(result).match(/work:.*/)) {
						DbcAddiRelations.isReviewOf( pid, result );
					}
				}				
			} else if (String(reviewXML.dkabm::record.dcterms::references.@xsi::type) === "dkdcplus:ISBN") {
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

			var creator = String(katalogXML.dkabm::record.dc::creator[0]).replace(/(.*)\(.*\)/, "$1");
			creator = creator.replace(/(.*) $/, "$1");
			creator = Normalize.removeSpecialCharacters(creator);
			var title = String(katalogXML.dkabm::record.dc::title[0]);
			title = Normalize.removeSpecialCharacters(title);
			var query = "subject = " + creator + " AND subject = " + title + " AND type = anmeldelse";
			
			Log.info( "query: " + query );
			
			var results = FedoraCQLSearch.search( query );
	
      for ( var i = 0; i < results.length; ++i ) {
				var result = results[i];

      	Log.info( "result: " + result );				

      	if (String(result).match(/150005:.*/)) {
      		DbcAddiRelations.isReviewOf( result, pid );
      	}
    	}
    }

    Log.info ("End hasReview" );

  };
  
	that.isAnalysisOf = function ( xml, pid ) {

    Log.info ("Start isAnalysisOf" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var analysisXML = XmlUtil.fromString( xml );
            
    var child;
    var personName;
    var personNames = [];
    var analysedTitle;
    var analysedTitles = [];
    var query;
                        
     //Litteraturtolkninger under development
//    if (String(analysisXML.dkabm::record.ac::source).match(/Litteraturtolkninger/)) {
    
		 //find firstname, lastname and birth year of the creators of the analysed works
//			for each (child in analysisXML.*.*.*.(@tag=='600')) {
//      	var first = String(child.*.(@code=='h')); 
//        var last = String(child.*.(@code=='a'));
//        var born = String(child.*.(@code=='c'));
//        last = " " + last;
//        personName = first + last;
//        if (born !== ""){
//        	personName = personName + " \(" + born + "\)";
//        }
//        Log.info("kwc1 personName: " + personName);
//        personNames.push (personName);
//      }
      //find titles of the analysed works
//      for each (child in analysisXML.*.*.*.(@tag=='666').*.(@code=='t')) {
//      	analysedTitle = String(child);
//        analysedTitle = Normalize.removeSpecialCharacters(analysedTitle); //normalizing because the field title in dc stream in which we search is normalized
//        analysedTitles.push (analysedTitle);
//      }
		
//      for (var x = 0; x < personNames.length; ++x ) {
//       for (var y = 0; y < analysedTitles.length; ++y){
//          query = "creator \u003D " + personNames[x] + " AND " + "title \u003D " + analysedTitles[y]; 
//					var results = FedoraCQLSearch.search(query);
//					
//					//search for creator without birth year + title, if creator has birth year
//					if (String(personNames[x]).match(/\(/)){
//						var personNameNoBirth = String(personNames[x].split("\(",1));
//						personNameNoBirth = personNameNoBirth.replace(/\s+$/, '');
//						query = "creator \u003D " + personNameNoBirth + " AND " + "title \u003D " + analysedTitles[y];
//						var extraResults = FedoraCQLSearch.search(query);
//						
						//add relations based on the results
//          	for (var xx = 0; xx < extraResults.length; ++xx) {
//          		var extraResult = extraResults[xx]; 
//							Log.info("kwc43 extraResult: " + extraResult);           
//	            if (!String(extraResult).match(/work:.*/)) {
//	            	DbcAddiRelations.isAnalysisOf(pid, extraResult);
//  	          }
//    	      }  
//					}
					//if no match on normal title, search for part title
//          if (results.length < 1) {
//	          query = "creator \u003D " + personNames[x] + " AND " + "title \u003D PART TITLE: " + analysedTitles[y];
//						results = FedoraCQLSearch.search(query);
						
					//search for creator without birth year + part title, if creator has birth year
//						if (String(personNames[x]).match(/\(/)){
//							var personNameNoBirth = String(personNames[x].split("\(",1));
//							personNameNoBirth = personNameNoBirth.replace(/\s+$/, '');
//							query = "creator \u003D " + personNameNoBirth + " AND " + "title \u003D PART TITLE: " + analysedTitles[y];
//							var extraResults = FedoraCQLSearch.search(query);

						//add relations based on the results
//          		for (var yy = 0; yy < extraResults.length; ++yy) {
//	         			var extraResult = extraResults[yy]; 
//	            	if (!String(extraResult).match(/work:.*/)) {
//		            	DbcAddiRelations.isAnalysisOf(pid, extraResult);
//	 	          	}
//	   	      	}  
//						}
//						}
						//add relations based on the results
//	          for (var ii = 0; ii < results.length; ++ii) {
//	          	var result = results[ii]; //
//	            if (!String(result).match(/work:.*/)) {
//	            	DbcAddiRelations.isAnalysisOf(pid, result);
//	            }
//	          }     
//	        }
//	      }
			            
      //Litteratursiden - first line to use when litteraturtolkninger is ready		
//		} else if ( String(analysisXML.dkabm::record.ac::source).match(/Litteratursiden/) && String(analysisXML.dkabm::record.dc::title).match(/Analyse af/) ) {
		//Litteratursiden
		if ( String(analysisXML.dkabm::record.ac::source).match(/Litteratursiden/) && String(analysisXML.dkabm::record.dc::title).match(/Analyse af/) ) {
			var analysedCreator = String(analysisXML.dkabm::record.dc::subject[0]);
			analysedCreator = Normalize.removeSpecialCharacters(analysedCreator);
			var analysedTitle = String(analysisXML.dkabm::record.dc::subject[1]);
			analysedTitle = Normalize.removeSpecialCharacters(analysedTitle);
			var query = "creator = " + analysedCreator + " AND title = " + analysedTitle;
			Log.info( "query: " + query );
			
			var results = FedoraCQLSearch.search( query );
			
			for ( var j = 0; j < results.length; ++j ) {
				var result = results[j]
				
				Log.info( "result: " + result );
				
				if (!String(result).match(/work:.*/)) {
					DbcAddiRelations.isAnalysisOf( pid, result );
				}
			}
    }

    Log.info ("End isAnalysisOf" );

  };
	
	that.hasAnalysis = function ( xml, pid) {

    Log.info ("Start hasAnalysis" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var katalogXML = XmlUtil.fromString( xml );

		var creator = String(katalogXML.dkabm::record.dc::creator[0]).replace(/(.*)\(.*\)/, "$1");
		creator = Normalize.removeSpecialCharacters(creator);
		creator = creator.replace(/(.*) $/, "$1");
		var title = String(katalogXML.dkabm::record.dc::title[0]);
		title = Normalize.removeSpecialCharacters(title);
		var query = "subject = " + creator + " AND subject = " + title + " AND ( label = analyse OR label = littolk )";
	
		Log.info("query: " + query);
			
		var results = FedoraCQLSearch.search( query );
			
    for ( var i = 0; i < results.length; ++i ) {
      var result = results[i];

      Log.info( "result: " + result );				
      if (String(result).match(/150005:.*/) || String(result).match(/870974:.*/)) {
        DbcAddiRelations.hasAnalysis( pid, result );
      }
    }

    Log.info ("End hasAnalysis" );

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

    var creatorNoBirth = "NOBIRTH:" + String(creatorXML.dkabm::record.dc::title[0]);
		var creator = String(creatorXML.dkabm::record.dc::title[0]);
		creator = Normalize.removeSpecialCharacters(creator);
		
		Log.debug("CREATORNOBIRTH (TITLE): " + creatorNoBirth);
		Log.debug("CREATOR (TITLE): " + creator);

    Log.info( "Creator: " + creator );    
    Log.info( "pid: " + pid );

    if (creator !== "undefined") {
      var results = FedoraPIDSearch.creator(creatorNoBirth);
      
			if (results.length === 0) {
				results = FedoraPIDSearch.creator(creator);
			}
			
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
    
    var creator = String(manifestationXml.dkabm::record.dc::creator[0]).replace(/ \(f\. .*\)/, "");
		creator = Normalize.removeSpecialCharacters(creator);
		
		Log.debug ("CREATOR: " + creator);
        
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
	
	that.isDescriptionFromPublisherOf = function ( xml, pid ) {

    Log.info ("Start isDescriptionFromPublisherOf" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var inputXml = XmlUtil.fromString( xml );
    
		var title = String(inputXml.dkabm::record.dc::title[0]).replace(/Forlagets beskrivelse af: (.*) af .*/, "$1");
		title = Normalize.removeSpecialCharacters(title);
		var creator = String(inputXml.dkabm::record.dc::title[0]).replace(/Forlagets beskrivelse af: .* af (.*)/, "NOBIRTH:$1").replace(/  /g, " ").replace(/ m\.fl\./, "");
		creator = Normalize.removeSpecialCharacters(creator);
		
	  Log.info( "isDescriptionFromPublisherOf PID: " + pid );
    Log.info( "isDescriptionFromPublisherOf TITLE: " + title );
		Log.info( "isDescriptionFromPublisherOf CREATOR: " + creator );
    	
		var type = "";
		
		Log.debug ("TYPE OF PUBLICATION: " + String(inputXml.ting::originalData.wformat));
		
		switch (String(inputXml.ting::originalData.wformat)){
			case "Netlydbog":
				type = "( type = Lydbog (b\u00E5nd) OR type = Lydbog (net) OR type = Lydbog (cd) OR type = Lydbog (cd-mp3) )"
				break;
			case "eReolen":
				type = "( type = Ebog OR type = Bog OR type = Bog stor skrift OR type = Netdokument )"
				break;
			default: 
				break;
		}	
		
		var query = "title = " + title + " AND ( creator = " + creator + " OR contributor = " + creator.replace(/NOBIRTH:/, "") + " )" + " AND " + type;
		Log.debug(pid + " - IS DESCRIPTION FROM PUBLISHER QUERY: " + query);
				
		var results = FedoraCQLSearch.search( query );
			
    for (var i = 0; i < results.length; ++i) {
      var result = results[i];
        
      Log.info("IS DESCRIPTION FROM PUBLISHER RESULT: " + result);
        
      if (!String(result).match(/work:.*/)) {
        DbcAddiRelations.hasDescriptionFromPublisher(result, pid);
      }
    }
		
		var child;
		
		for each (child in inputXml.dkabm::record.ac::identifier) {
      var identifier = "ISBN:" + String(child).replace(/\|150039/, "");
    } 
		
		query = "identifier = " + identifier;
		Log.debug("IS DESCRIPTION FROM PUBLISHER IDENTIFIER QUERY: " + query);
				
		results = FedoraCQLSearch.search( query );
			
    for (var i = 0; i < results.length; ++i) {
      var result = results[i];
        
      Log.info("IS DESCRIPTION FROM PUBLISHER RESULT: " + result);
        
      if (!String(result).match(/work:.*/)) {
        DbcAddiRelations.hasDescriptionFromPublisher(result, pid);
      }
    }

    Log.info ("End isDescriptionFromPublisherOf" );

  };

  that.hasDescriptionFromPublisher = function ( xml, pid ) {

    Log.info ("Start hasDescriptionFromPublisher" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var inputXml = XmlUtil.fromString( xml );
    
		var title = String(inputXml.dkabm::record.dc::title[0]);
		title = Normalize.removeSpecialCharacters(title);
		
		var child;
		var result;

		var creator = String(inputXml.dkabm::record.dc::creator[0]).replace(/ \(f\. .*\)/, "");
		creator = Normalize.removeSpecialCharacters(creator);
	
	  Log.info( "hasDescriptionFromPublisher PID: " + pid );
    Log.info( "hasDescriptionFromPublisher TITLE: " + title );
		Log.info( "hasDescriptionFromPublisher CREATOR: " + creator );
		
		var type = "";
		
		switch (String(inputXml.dkabm::record.dc::type)) {
			case "Lydbog (b\u00E5nd)": case "Lydbog (net)": case "Lydbog (cd)": case "Lydbog (cd-mp3)":
				type = "PUBLISHERDESCRIPTION:Netlydbog";
				break;
			case "Ebog": case "Bog": case "Bog stor skrift": case "Netdokument":
				type = "PUBLISHERDESCRIPTION:eReolen";
				break;
			default:
				return;
		}
		
		var query = "subject = " + creator + " AND subject = " + title + " AND type = " + type;
		Log.debug("QUERY: " + query);
				
		var results = FedoraCQLSearch.search( query );
    
    for (var i = 0; i < results.length; ++i) {
      result = results[i];
        
      Log.info("result: " + result);
        
      if (String(result).match(/150039:.*/)) {
        DbcAddiRelations.hasDescriptionFromPublisher(pid, result);
      }
    }
		
		for each (child in inputXml.dkabm::record.dc::contributor) {
			creator = String(child).replace(/ \(f\. .*\)/, "");
			
			Log.info( "hasDescriptionFromPublisher CONTRIBUTOR: " + creator );
			
			query = "subject = " + creator + " AND subject = " + title + " AND type = " + type;
			Log.debug("QUERY: " + query);
				
			results = FedoraCQLSearch.search( query );
    
    	for (i = 0; i < results.length; ++i) {
      	result = results[i];
        
      	Log.info("result: " + result);
        
      	if (!String(result).match(/150039:.*/)) {
        	DbcAddiRelations.hasDescriptionFromPublisher(pid, result);
      	}
    	}
		}
		
		for each (child in inputXml.dkabm::record.dc::identifier) {
      if (String(child.@xsi::type).match("dkdcplus:ISBN")) {
        Log.debug ( "Attribute: " + String(child.@xsi::type));
        Log.debug ( "Child: " + child );
        var identifier = String(child).replace(/ /g, "");
      }
    } 
		
		query = "identifier = 150039:" + identifier;
		Log.debug("IDENTIFIER QUERY: " + query);
				
		results = FedoraCQLSearch.search( query );
			
    for (i = 0; i < results.length; ++i) {
      result = results[i];
        
      Log.info("result: " + result);
        
      if (!String(result).match(/work:.*/)) {
        DbcAddiRelations.hasDescriptionFromPublisher( pid, result );
      }
    }
    
    Log.info ("End hasDescriptionFromPublisher" );

  };

  that.isSubjectDescriptionOf = function ( xml, pid ) {

    Log.info ("Start isSubjectDescriptionOf" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var subjectXML = XmlUtil.fromString( xml );

    var title = String(subjectXML.dkabm::record.dc::title[0]);
		title = Normalize.removeSpecialCharacters(title);

    Log.info( "Title: " + title );    
    Log.info( "pid: " + pid );

    if (title !== "undefined") {
      var results = FedoraPIDSearch.subject(title);
      
      for (var i = 0; i < results.length; ++i) {
        var result = results[i];
        
        Log.info("result: " + result);
        
        if (!String(result).match(/work:.*/) && !String(result).match(/150012:.*/) && !String(result).match(/150017:.*/) && !String(result).match(/150033:.*/) && !String(result).match(/150040:.*/)) {
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
	
	        if (String(result).match(/150012:.*/) || String(result).match(/150017:.*/) || String(result).match(/150033:.*/) || String(result).match(/150040:.*/)) {
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
    if (String(manifestationXML.dkabm::record.ac::source).match(/Litteratursiden|Faktalink|Forfatterweb|Spil og Medier|Netlydbog|eReolen/)) {
      for each (child in manifestationXML.dkabm::record.dc::identifier) {
        if (String(child.@xsi::type).match("dcterms:URI")) {
          DbcAddiRelations.hasOnlineAccess( pid, String(child) );
        }
      }
		} else if (String(manifestationXML.dkabm::record.ac::source).match(/Gale/)) {
				DbcAddiRelations.hasOnlineAccess ( pid, "[URL]/ic/scic/ReferenceDetailsPage/ReferenceDetailsWindow?displayGroupName=Reference&disableHighlighting=false&prodId=SCIC&action=e&windowstate=normal&catId=&documentId=GALE%7C" + String(manifestationXML.dkabm::record.dc::identifier) + "&mode=view[URL-suffix]");			
    } else if (String(manifestationXML.dkabm::record.ac::source).match(/Ebsco|Ebrary/)){
      	DbcAddiRelations.hasOnlineAccess ( pid, "[URL]" + String(manifestationXML.dkabm::record.ac::identifier).replace(/\|.*/, ""));
		} else if (String(manifestationXML.dkabm::record.ac::source).match(/Samfundsfaget|Religionsfaget|Dansk historie|Danske Dyr|Verdens Dyr/)) {
				DbcAddiRelations.hasOnlineAccess ( pid, "[URL]" + String(manifestationXML.dkabm::record.dc::identifier));
		} else if (String(manifestationXML.dkabm::record.ac::source).match(/Oxford Reference Online: Premium Collection|Oxford Reference Online: Literature Collection|Oxford Reference Online: Western Collection/)) {
				for each (child in manifestationXML.dkabm::record.dc::identifier) {
				   if (String(child.@xsi::type).match("dcterms:URI")) {	
							DbcAddiRelations.hasOnlineAccess ( pid, "[URL]" + child);
					}
				}
// 1001 Fortællinger under development
//		} else if (String(manifestationXML.dkabm::record.ac::identifier).match(/t[0-9]+\|150031/)) {
//				for each (child in manifestationXML.dkabm::record.dc::identifier) {
//        if (String(child.@xsi::type).match("dcterms:URI")) {
//          DbcAddiRelations.hasOnlineAccess( pid, String(child) );
//        }
//      }
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
			} 
// 1001 Fortællinger under development
//			else if (String(manifestationXML.dkabm::record.ac::identifier).match(/s[0-9]+\|150031/)) {
//				var identifier = String(manifestationXML.dkabm::record.ac::identifier).replace( /(.*)\|(.*)/, "$2:$1speak");

//    		Log.info( "Identifier hasOnlineAccess: " + identifier );
//    		Log.info( "pid hasOnlineAccess: " + pid );

//    		var results = FedoraPIDSearch.pid( identifier );

//      	for ( var i = 0; i < results.length; ++i ) {
//        	var result = results[i];

//        	Log.info( "result: " + result );

//        	if (!String(result).match(/work:.*/)) {
//          	DbcAddiRelations.hasOnlineAccess( pid, result );
//        	}
//      	}
//				identifier = String(manifestationXML.dkabm::record.ac::identifier).replace( /(.*)\|(.*)/, "$2:$1text");

//    		Log.info( "Identifier hasOnlineAccess: " + identifier );
//    		Log.info( "pid hasOnlineAccess: " + pid );

//    		var results = FedoraPIDSearch.pid( identifier );

//      	for ( var i = 0; i < results.length; ++i ) {
//        	var result = results[i];

//        	Log.info( "result: " + result );

//        	if (!String(result).match(/work:.*/)) {
//          	DbcAddiRelations.hasOnlineAccess( pid, result );
//        	}
//      	}
//			}
		
		var infomedia = 0;	
		for each (child in manifestationXML.*.*.*.(@tag=='538').*.(@code=='i')) {
      if (String(child) === "Infomedia" && infomedia === 0) {
        var url = String("[useraccessinfomedia]?action=getArticle&faust=" + manifestationXML.*.*.*.(@tag=='001').*.(@code=='a') + "&libraryCode=[libraryCode]&userId=[userId]&userPinCode=[userPinCode]");
     		DbcAddiRelations.hasOnlineAccess( pid, url );
				infomedia++;
      }
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
		} 
// 1001 Fortællinger under development		
//		else if (String(imageXML.dkabm::record.ac::identifier).match(/.*\|150031/)) {
//			identifier = String(imageXML.oso::object.oso::identifier).replace( /(.*)speak\|(.*)/, "$2:$1");

//	    Log.info( "Identifier: " + identifier );
//	    Log.info( "pid: " + pid );
	
//	    var results = FedoraPIDSearch.pid( identifier );

//	    for ( var i = 0; i < results.length; ++i ) {
//	      var result = results[i];
	
//	      Log.info( "result: " + result );

//	      if (!String(result).match(/work:.*/)) {
//	        DbcAddiRelations.hasOnlineAccess( result, pid );
//	      }
//	    }
//			identifier = String(imageXML.oso::object.oso::identifier).replace( /(.*)text\|(.*)/, "$2:$1");

//    	Log.info( "Identifier: " + identifier );
//    	Log.info( "pid: " + pid );

//    	var results = FedoraPIDSearch.pid( identifier );

//    	for ( var i = 0; i < results.length; ++i ) {
//      	var result = results[i];

//      	Log.info( "result: " + result );

//      	if (!String(result).match(/work:.*/)) {
//        	DbcAddiRelations.hasOnlineAccess( result, pid );
//      	}
//	    }
//		}	

	    Log.info ("End isOnlineAccessOf" );

  };
  
  that.hasFilmstribenFSSOnlineAccess = function( xml, pid ) {

    Log.info ("Start hasFilmstribenFSSOnlineAccess" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var manifestationXML = XmlUtil.fromString( xml );

    if (String(manifestationXML.*.*.*.(@tag=='032').*.(@code=='x')).match(/FSS/)) {
      var id = String(manifestationXML.*.*.*.(@tag=='856')[0].*.(@code=='u')).replace(/http:\/\/www.filmstriben.dk\/\?showfilm=(.*)/, "$1");
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
      var id = String(manifestationXML.*.*.*.(@tag=='856')[0].*.(@code=='u')).replace(/http:\/\/www.filmstriben.dk\/\?showfilm=(.*)/, "$1");
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
      var id = String(manifestationXML.*.*.*.(@tag=='856')[0].*.(@code=='u')).replace(/http:\/\/www.filmstriben.dk\/\?showfilm=(.*)/, "$1");
      var url = String("http://www.filmstriben.dk/bibliotek/filmdetails.aspx?id=" + id);
      DbcAddiRelations.hasOnlineAccess( pid, url );
    }
    
    Log.info ("End hasFilmstribenFSBOnlineAccess" );

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
