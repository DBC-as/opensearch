use ( "Log.use.js" );

EXPORTED_SYMBOLS = [ "DbcBibRelations" ];

var DbcBibRelations = function() {

    var that = {};

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcbib#hasTrack
    */
    that.isPartOfAlbum = function ( pid1, pid2 ){
        Log.info( "Start isPartOfAlbum" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcbib#hasTrack", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcbib#isPartOfAlbum", pid2 );
        Log.info( "End isPartOfAlbum" );
    };

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcbib#hasArticle
    */
    that.isPartOfManifestation = function ( pid1, pid2 ){
        Log.info( "Start isPartOfManifestation" );
        //scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcbib#hasArticle", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcbib#isPartOfManifestation", pid2 );
        Log.info( "End isPartOfManifestation" );
    };

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcbib#hasManifestation
    */
    that.isMemberOfWork = function ( pid1, pid2 ){
        Log.info( "Start isMemberOfWork" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcbib#hasManifestation", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcbib#isMemberOfWork", pid2 );
        Log.info( "End isMemberOfWork" );
    };

return that;}();