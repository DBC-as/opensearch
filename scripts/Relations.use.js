use ( "XmlUtil.use.js" );
use ( "XmlNamespaces.use.js" );
use ( "DbcAddiRelations.use.js" );
use ( "DbcBibRelations.use.js" );

EXPORTED_SYMBOLS = ['Relations'];

var Relations = function() {

  var dkabm = XmlNamespaces.dkabm;
  var dc = XmlNamespaces.dc;
  var ac = XmlNamespaces.ac;
  var dcterms = XmlNamespaces.dcterms;
  var xsi = XmlNamespaces.xsi;
  var ting = XmlNamespaces.ting;
  var oso = XmlNamespaces.oso;

  var that = {};

  that.isReviewOf = function ( xml, pid ) {

    Log.info ("Start isReviewOf" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var reviewXML = XmlUtil.fromString( xml );

    var identifier = reviewXML.*.*.*.(@tag=='014').*.(@code=='a');

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

    if (String(analysisXML.dkabm::record.dcterms::references.@xsi::type) === "dkdcplus:ISBN") {
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
      if (String(child.@xsi::type).match("oss:albumId")) {
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

  that.isAuthorDescriptionOf = function ( xml, pid ) {

    Log.info ("Start isAuthorDescriptionOf" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var authorXML = XmlUtil.fromString( xml );

    var creator = String(authorXML.dkabm::record.dc::title);

    Log.info( "Creator: " + creator );    
    Log.info( "pid: " + pid );

    if (creator !== "undefined") {
      var results = FedoraPIDSearch.creator(creator);
      
      for (var i = 0; i < results.length; ++i) {
        var result = results[i];
        
        Log.info("result: " + result);
        
        if (!String(result).match(/work:.*/)) {
          DbcAddiRelations.hasAuthorDescription(result, pid);
        }
      }
    }

    Log.info ("End isAuthorDescriptionOf" );

  };

  that.hasAuthorDescription = function ( xml, pid ) {

    Log.info ("Start hasAuthorDescription" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var manifestationXML = XmlUtil.fromString( xml );

    var type = String(manifestationXML.dkabm::record.dc::type);
    Log.info( "Type: " + type );

    var types = ["Artikel", "Avisartikel", "Billedbog", "Bog", "CD (musik)", "Kassettelydb\u00e5nd", "Lydbog (b\u00e5nd)", "Lydbog (cd)", "Lydbog (online)", "Lydbog (cd-mp3)", "Netdokument", "Tegneserie", "Tidsskriftsartikel", "CD", "Punktskrift"];

    for (var a in types) {
      if (type === types[a]) {
        var creator = String(manifestationXML.dkabm::record.dc::creator[0]);

        Log.info( "Creator: " + creator );
        Log.info( "pid: " + pid );
        
        if (creator !== "undefined") {
          var results = FedoraPIDSearch.title( creator );

          for ( var i = 0; i < results.length; ++i ) {
            var result = results[i];

            Log.info( "result: " + result );

            if (String(result).match(/150016:.*/)) {
              DbcAddiRelations.hasAuthorDescription( pid, result );
            }
          }
        }
      }
    }

    Log.info ("End hasAuthorDescription" );

  };
  
  that.isCreatorDescriptionOf = function ( xml, pid ) {

    Log.info ("Start isCreatorDescriptionOf" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var subjectXml = XmlUtil.fromString( xml );

    Log.info ("End isCreatorDescriptionOf" );

  };

  that.hasCreatorDescription = function ( xml, pid ) {

    Log.info ("Start hasCreatorDescription" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var creatorXml = XmlUtil.fromString( xml );
    
    var creator = String(creatorXml.dkabm::record.dc::creator);
    
    if (creator !== "undefined") {
      var results = FedoraPIDSearch.subject(creator);
      
      for (var i = 0; i < results.length; ++i) {
        var result = results[i];
        
        Log.info("result: " + result);
        
        if (!String(result).match(/work:.*/)) {
          DbcAddiRelations.hasCreatorDescription(result, pid);
        }
      }
    }
    
    Log.info ("End hasCreatorDescription" );

  };

  that.isSubjectDescriptionOf = function ( xml, pid ) {

    Log.info ("Start isSubjectDescriptionOf" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var authorXML = XmlUtil.fromString( xml );

    var title = String(authorXML.dkabm::record.dc::title);

    Log.info( "Title: " + title );    
    Log.info( "pid: " + pid );

    if (title !== "undefined") {
      var results = FedoraPIDSearch.subject(title);
      
      for (var i = 0; i < results.length; ++i) {
        var result = results[i];
        
        Log.info("result: " + result);
        
        if (!String(result).match(/work:.*/)) {
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

      var subject = String(child);

      Log.info( "Subject: " + subject );
      Log.info( "pid: " + pid );

      var results = FedoraPIDSearch.title( subject );

      for ( var i = 0; i < results.length; ++i ) {
        var result = results[i];

        Log.info( "result: " + result );

        var NS = "http://oss.dbc.dk/rdf/dbcaddi#";

        if (String(result).match(/150017:.*/)) {
          DbcAddiRelations.hasSubjectDescription( result, pid );
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

    Log.info( "Identifier: " + identifier );
    Log.info( "pid: " + pid );

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

    Log.info( "Identifier: " + identifier );
    Log.info( "pid: " + pid );

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

    var url = String( "http://bibzoom.shop2download.com/samples/" + trackXML.*.*.*.soundClip);

    DbcAddiRelations.hasSoundClip( pid, url );

    Log.info ("End hasSoundClip" );

  };

  that.hasFullText = function( xml, pid ) {

    Log.info ("Start hasFullText" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var manifestationXML = XmlUtil.fromString( xml );

    if (String(manifestationXML.*.*.*.(@tag=='538').*.(@code=='i')) === "Infomedia") {
      var url = String("[useraccessinfomedia]?action=getArticle&faust=" + manifestationXML.*.*.*.(@tag=='001').*.(@code=='a') + "&libraryCode=[libraryCode]&userId=[userId]&userPinCode=[userPinCode]");
      DbcAddiRelations.hasFulltext( pid, url );
    }
    
    var child;
    if (String(manifestationXML.dkabm::record.ac::source).match(/Faktalink|Forfatterweb/)) {
      for each (child in manifestationXML.dkabm::record.dc::identifier) {
        if (String(child.@xsi::type).match("dcterms:URI")) {
          DbcAddiRelations.hasFulltext( pid, String(child) );
        }
      }
    }

    Log.info ("End hasFullText" );

  };
  
  that.hasOnlineAccess = function( xml, pid ) {

    Log.info ("Start hasOnlineAccess" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var manifestationXML = XmlUtil.fromString( xml );
    
    var child;
    if (String(manifestationXML.dkabm::record.ac::source).match(/Litteratursiden/)) {
      for each (child in manifestationXML.dkabm::record.dc::identifier) {
        if (String(child.@xsi::type).match("dcterms:URI")) {
          DbcAddiRelations.hasOnlineAccess( pid, String(child) );
        }
      }
    } else {
      DbcAddiRelations.hasOnlineAccess ( pid, "[URL]" + String(manifestationXML.dkabm::record.ac::identifier).replace(/\|.*/, ""));
    }

    Log.info ("End hasOnlineAccess" );

  };
  
  that.hasFilmstribenOnlineAccess = function( xml, pid ) {

    Log.info ("Start hasFilmstribenOnlineAccess" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var manifestationXML = XmlUtil.fromString( xml );

    if (String(manifestationXML.*.*.*.(@tag=='032').*.(@code=='x')).match(/FSS/)) {
      var id = String(manifestationXML.*.*.*.(@tag=='856').*.(@code=='u')).replace(/http:\/\/www.filmstriben.dk\/\?showfilm=(.*)/, "$1");
      var url = String("http://www.filmstriben.dk/skole/filmdetails.aspx?id=" + id);
      DbcAddiRelations.hasOnlineAccess( pid, url );
    }
    
    Log.info ("End hasFilmstribenOnlineAccess" );

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

  return that;

}();
