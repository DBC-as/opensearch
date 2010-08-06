use ( "XmlUtil.use.js" );
use ( "XmlNamespaces.use.js" );

EXPORTED_SYMBOLS = ['Relations'];

const Relations = function() {

  var dkabm = XmlNamespaces.dkabm;
  var dc = XmlNamespaces.dc;
  var ac = XmlNamespaces.ac;
  var dcterms = XmlNamespaces.dcterms;
  var xsi = XmlNamespaces.xsi;
  var ting = XmlNamespaces.ting;

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

      var NS = "http://oss.dbc.dk/rdf/dkbib#";

      scriptClass.createRelation( pid, NS + "isReviewOf", result);
      scriptClass.createRelation( result, NS + "hasReview", pid);
    }

    Log.debug ( "REVIEW: " + i );

    if (i === 0) {
      var title = String(reviewXML.dkabm::record.dc::title).replace(/Anbefaling af: (.*) af .*/, "$1");

      var results = FedoraPIDSearch.title( title );

      for ( var i = 0; i < results.length; ++i ) {
        var result = results[i];

        Log.info( "result: " + result );

        var NS = "http://oss.dbc.dk/rdf/dkbib#";

        scriptClass.createRelation( pid, NS + "isReviewOf", result);
        scriptClass.createRelation( result, NS + "hasReview", pid);
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

      var NS = "http://oss.dbc.dk/rdf/dkbib#";

      scriptClass.createRelation( pid, NS + "hasReview", result);
      scriptClass.createRelation( result, NS + "isReviewOf", pid);
    }

    Log.info ("End hasReview" );

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

      var NS = "http://oss.dbc.dk/rdf/dbcbib#";

      scriptClass.createRelation( pid, NS + "isPartOfManifestation", result);
      scriptClass.createRelation( result, NS + "hasArticle", pid);
    }

    if (i === 0) {
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

        var NS = "http://oss.dbc.dk/rdf/dbcbib#";

        scriptClass.createRelation( pid, NS + "isPartOfManifestation", result);
        scriptClass.createRelation( result, NS + "hasArticle", pid);
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

      var NS = "http://oss.dbc.dk/rdf/dbcbib#";

      scriptClass.createRelation( pid, NS + "hasArticle", result);
      scriptClass.createRelation( result, NS + "isPartOfManifestation", pid);
    }

    if (i === 0) {
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

        var NS = "http://oss.dbc.dk/rdf/dbcbib#";

        scriptClass.createRelation( pid, NS + "hasArticle", result);
        scriptClass.createRelation( result, NS + "isPartOfManifestation", pid);
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

      var NS = "http://oss.dbc.dk/rdf/dbcbib#";

      scriptClass.createRelation( pid, NS + "isPartOfAlbum", result);
      scriptClass.createRelation( result, NS + "hasTrack", pid);
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

      var NS = "http://oss.dbc.dk/rdf/dbcbib#";

      scriptClass.createRelation( pid, NS + "hasTrack", result);
      scriptClass.createRelation( result, NS + "isPartOfAlbum", pid);
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

    var results = FedoraPIDSearch.creator( creator );

    for ( var i = 0; i < results.length; ++i ) {
      var result = results[i];

      Log.info( "result: " + result );

      var NS = "http://oss.dbc.dk/rdf/dbcaddi#";

      scriptClass.createRelation( pid, NS + "isAuthorDescriptionOf", result);
      scriptClass.createRelation( result, NS + "hasAuthorDescription", pid);
    }

    Log.info ("End isAuthorDescriptionOf" );

  };

  that.hasAuthorDescription = function ( xml, pid ) {

    Log.info ("Start hasAuthorDescription" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var manifestationXML = XmlUtil.fromString( xml );

    var type = String(manifestationXML.dkabm::record.dc::type);
    Log.info( "Type: " + type );

    var types = ["Artikel", "Avisartikel", "Billedbog", "Bog", "CD (musik)", "Kassettelydb\u00e5nd", "Lydbog (b\u00e5nd)", "Lydbog (cd)", "Lydbog (mp3)", "Lydb\u00e5nd (bog)", "Netdokument", "Tegneserie", "Tidsskriftsartikel"];

    for (var a in types) {
      if (type === types[a]) {
        var creator = String(manifestationXML.dkabm::record.dc::creator[0]);

        Log.info( "Creator: " + creator );
        Log.info( "pid: " + pid );

        var results = FedoraPIDSearch.title( creator );

        for ( var i = 0; i < results.length; ++i ) {
          var result = results[i];

          Log.info( "result: " + result );

          var NS = "http://oss.dbc.dk/rdf/dbcaddi#";

          scriptClass.createRelation( pid, NS + "hasAuthorDescription", result);
          scriptClass.createRelation( result, NS + "isAuthorDescriptionOf", pid);
        }
      }
    }

    Log.info ("End hasAuthorDescription" );

  };

  that.isSubjectDescriptionOf = function ( xml, pid ) {

    Log.info ("Start isSubjectDescriptionOf" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var authorXML = XmlUtil.fromString( xml );

    var title = String(authorXML.dkabm::record.dc::title);

    Log.info( "Title: " + title );    
    Log.info( "pid: " + pid );

    var results = FedoraPIDSearch.subject( title );

    for ( var i = 0; i < results.length; ++i ) {
      var result = results[i];

      Log.info( "result: " + result );

      var NS = "http://oss.dbc.dk/rdf/dbcaddi#";

      if (String(result) !== String(pid)) {
        scriptClass.createRelation( pid, NS + "isSubjectDescriptionOf", result);
        scriptClass.createRelation( result, NS + "hasSubjectDescription", pid);
      }
    }

    Log.info ("End isSubjectDescriptionOf" );

  };

  that.hasSubjectDescription = function ( xml, pid ) {

    Log.info ("Start hasSubjectDescription" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var manifestationXML = XmlUtil.fromString( xml );

    for each(child in manifestationXML.dkabm::record.dc::subject) {

      var subject = String(child);

      Log.info( "Subject: " + subject );
      Log.info( "pid: " + pid );

      var results = FedoraPIDSearch.title( subject );

      for ( var i = 0; i < results.length; ++i ) {
        var result = results[i];

        Log.info( "result: " + result );

        var NS = "http://oss.dbc.dk/rdf/dbcaddi#";

        if (String(result) !== String(pid)) {
          scriptClass.createRelation( pid, NS + "hasSubjectDescription", result);
          scriptClass.createRelation( result, NS + "isSubjectDescriptionOf", pid);
        }
      }

    }

    Log.info ("End hasSubjectDescription" );

  };

  that.hasSoundClip = function( xml, pid ) {

    Log.info ("Start hasSoundClip" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var trackXML = XmlUtil.fromString( xml );

    var url = String( "http://bibzoom.shop2download.com/samples/" + trackXML.*.*.soundClip);

    var NS = "http://oss.dbc.dk/rdf/dbcaddi#";

    scriptClass.createRelation( pid, NS + "hasSoundClip", url);

    Log.info ("End hasSoundClip" );

  };

  return that;

}();
