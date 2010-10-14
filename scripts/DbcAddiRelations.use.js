// DO NOT EDIT THIS FILE!
// This file was auto-generated by generate_js_sigr.py,
// any edits will be overwritten by a re-run of the script
// Generated: Thu Oct 14 09:44:00 2010

EXPORTED_SYMBOLS = [ "DbcAddiRelations" ];

const DbcAddiRelations = function() {

    var that = {};

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcbib#isReviewOf
    */
    that.hasReview = function ( pid1, pid2 ){
        Log.info( "Start hasReview" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcbib#isReviewOf", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasReview", pid2 );
        Log.info( "End hasReview" );
    };

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcaddi#isSubjectDescriptionOf
    */
    that.hasSubjectDescription = function ( pid1, pid2 ){
        Log.info( "Start hasSubjectDescription" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcaddi#isSubjectDescriptionOf", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasSubjectDescription", pid2 );
        Log.info( "End hasSubjectDescription" );
    };

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcbib#isAnalysesOf
    */
    that.hasAnalyses = function ( pid1, pid2 ){
        Log.info( "Start hasAnalyses" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcbib#isAnalysesOf", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasAnalyses", pid2 );
        Log.info( "End hasAnalyses" );
    };


    that.hasTrailer = function ( pid1, pid2 ){
        Log.info( "Start hasTrailer" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasTrailer", pid2 );
        Log.info( "End hasTrailer" );
    };

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcbibaddi#hasReview
    */
    that.isReviewOf = function ( pid1, pid2 ){
        Log.info( "Start isReviewOf" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcbibaddi#hasReview", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#isReviewOf", pid2 );
        Log.info( "End isReviewOf" );
    };


    that.hasImageRepresentation = function ( pid1, pid2 ){
        Log.info( "Start hasImageRepresentation" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasImageRepresentation", pid2 );
        Log.info( "End hasImageRepresentation" );
    };


    that.hasSoundClip = function ( pid1, pid2 ){
        Log.info( "Start hasSoundClip" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasSoundClip", pid2 );
        Log.info( "End hasSoundClip" );
    };


    that.hasTutorial = function ( pid1, pid2 ){
        Log.info( "Start hasTutorial" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasTutorial", pid2 );
        Log.info( "End hasTutorial" );
    };


    that.hasUserCreatedContent = function ( pid1, pid2 ){
        Log.info( "Start hasUserCreatedContent" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasUserCreatedContent", pid2 );
        Log.info( "End hasUserCreatedContent" );
    };


    that.hasRingtone = function ( pid1, pid2 ){
        Log.info( "Start hasRingtone" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasRingtone", pid2 );
        Log.info( "End hasRingtone" );
    };

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcbibaddi#hasAnalyses
    */
    that.isAnalysesOf = function ( pid1, pid2 ){
        Log.info( "Start isAnalysesOf" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcbibaddi#hasAnalyses", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#isAnalysesOf", pid2 );
        Log.info( "End isAnalysesOf" );
    };

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcaddi#hasThemeMember
    */
    that.isPartOfTheme = function ( pid1, pid2 ){
        Log.info( "Start isPartOfTheme" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcaddi#hasThemeMember", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#isPartOfTheme", pid2 );
        Log.info( "End isPartOfTheme" );
    };


    that.hasFulltext = function ( pid1, pid2 ){
        Log.info( "Start hasFulltext" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasFulltext", pid2 );
        Log.info( "End hasFulltext" );
    };

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcaddi#isAuthorDescriptionOf
    */
    that.hasAuthorDescription = function ( pid1, pid2 ){
        Log.info( "Start hasAuthorDescription" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcaddi#isAuthorDescriptionOf", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasAuthorDescription", pid2 );
        Log.info( "End hasAuthorDescription" );
    };


    that.hasDemo = function ( pid1, pid2 ){
        Log.info( "Start hasDemo" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasDemo", pid2 );
        Log.info( "End hasDemo" );
    };

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcaddi#hasImage
    */
    that.isImageOf = function ( pid1, pid2 ){
        Log.info( "Start isImageOf" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcaddi#hasImage", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#isImageOf", pid2 );
        Log.info( "End isImageOf" );
    };

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcaddi#hasCover
    */
    that.isCoverOf = function ( pid1, pid2 ){
        Log.info( "Start isCoverOf" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcaddi#hasCover", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#isCoverOf", pid2 );
        Log.info( "End isCoverOf" );
    };

return that;}();