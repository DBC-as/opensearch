
EXPORTED_SYMBOLS = ['DbcAddiRelations'];

const DbcAddiRelations = function() {

    var that = {};

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcaddi#hasThemeMember
    */
    DbcAddiRelations.isPartOfTheme = function ( pid1, pid2 ){
        Log.info( "Start isPartOfTheme" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcaddi#hasThemeMember", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#isPartOfTheme", pid2 );
        Log.info( "End isPartOfTheme" );
    };


    DbcAddiRelations.hasRingtone = function ( pid1, pid2 ){
        Log.info( "Start hasRingtone" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasRingtone", pid2 );
        Log.info( "End hasRingtone" );
    };


    DbcAddiRelations.hasUserCreatedContent = function ( pid1, pid2 ){
        Log.info( "Start hasUserCreatedContent" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasUserCreatedContent", pid2 );
        Log.info( "End hasUserCreatedContent" );
    };


    DbcAddiRelations.hasFulltext = function ( pid1, pid2 ){
        Log.info( "Start hasFulltext" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasFulltext", pid2 );
        Log.info( "End hasFulltext" );
    };


    DbcAddiRelations.hasImageRepresentation = function ( pid1, pid2 ){
        Log.info( "Start hasImageRepresentation" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasImageRepresentation", pid2 );
        Log.info( "End hasImageRepresentation" );
    };


    DbcAddiRelations.hasDemo = function ( pid1, pid2 ){
        Log.info( "Start hasDemo" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasDemo", pid2 );
        Log.info( "End hasDemo" );
    };

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcbibaddi#hasReview
    */
    DbcAddiRelations.isReviewOf = function ( pid1, pid2 ){
        Log.info( "Start isReviewOf" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcbibaddi#hasReview", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#isReviewOf", pid2 );
        Log.info( "End isReviewOf" );
    };


    DbcAddiRelations.hasTutorial = function ( pid1, pid2 ){
        Log.info( "Start hasTutorial" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasTutorial", pid2 );
        Log.info( End hasTutorial );
    };


    DbcAddiRelations.hasTrailer = function ( pid1, pid2 ){
        Log.info( "Start hasTrailer" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasTrailer", pid2 );
        Log.info( "End hasTrailer" );
    };

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcaddi#hasCover
    */
    DbcAddiRelations.isCoverOf = function ( pid1, pid2 ){
        Log.info( "Start isCoverOf" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcaddi#hasCover", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#isCoverOf", pid2 );
        Log.info( "End isCoverOf" );
    };

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcaddi#isSubjectDescriptionOf
    */
    DbcAddiRelations.hasSubjectDescription = function ( pid1, pid2 ){
        Log.info( "Start hasSubjectDescription" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcaddi#isSubjectDescriptionOf", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasSubjectDescription", pid2 );
        Log.info( "End hasSubjectDescription" );
    };


    DbcAddiRelations.hasSoundClip = function ( pid1, pid2 ){
        Log.info( "Start hasSoundClip" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasSoundClip", pid2 );
        Log.info( "End hasSoundClip" );
    };

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcbib#isReviewOf
    */
    DbcAddiRelations.hasReview = function ( pid1, pid2 ){
        Log.info( "Start hasReview" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcbib#isReviewOf", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasReview", pid2 );
        Log.info( "End hasReview" );
    };

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcaddi#isAuthorDescriptionOf
    */
    DbcAddiRelations.hasAuthorDescription = function ( pid1, pid2 ){
        Log.info( "Start hasAuthorDescription" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcaddi#isAuthorDescriptionOf", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasAuthorDescription", pid2 );
        Log.info( "End hasAuthorDescription" );
    };

that}();