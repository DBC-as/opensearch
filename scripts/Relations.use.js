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

    var identifier = String(albumXML.dkabm::record.ac::identifier);
   
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

  that.hasSoundClip = function( xml, pid) {

    Log.info ("Start hasSoundClip" );

    // Converting the xml-string to an XMLObject which e4x can handle:
    var trackXML = XmlUtil.fromString( xml );

    var url = String( "http://netmusik.shop2download.com/samples/" + trackXML.*.*.soundClip);
   
    var NS = "http://oss.dbc.dk/rdf/dbcaddi#";

    scriptClass.createRelation( pid, NS + "hasSoundClip", url);

    Log.info ("End hasSoundClip" );

  };

  return that;

}();
