
EXPORTED_SYMBOLS = [ "DbcAddiRelations" ];

const DbcAddiRelations = function() {
    
    var that = {};

    that.hasCreatorHomePage = function ( pid1, pid2 ){
        Log.info( "Start hasCreatorHomePage" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasCreatorHomePage", pid2 );
        Log.info( "End hasCreatorHomePage" );
    };

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
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcaddi#hasImage
    */
    that.isImageOf = function ( pid1, pid2 ){
        Log.info( "Start isImageOf" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcaddi#hasImage", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#isImageOf", pid2 );
        Log.info( "End isImageOf" );
    };

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcbib#isAnalysisOf
    */
    that.hasAnalysis = function ( pid1, pid2 ){
        Log.info( "Start hasAnalysis" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcaddi#isAnalysisOf", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasAnalysis", pid2 );
        Log.info( "End hasAnalysis" );
    };

    that.hasSoundClip = function ( pid1, pid2 ){
        Log.info( "Start hasSoundClip" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasSoundClip", pid2 );
        Log.info( "End hasSoundClip" );
    };

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcbibaddi#hasAnalysis
    */
    that.isAnalysisOf = function ( pid1, pid2 ){
        Log.info( "Start isAnalysisOf" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcaddi#hasAnalysis", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#isAnalysisOf", pid2 );
        Log.info( "End isAnalysisOf" );
    };

		that.hasDescriptionFromPublisher = function ( pid1, pid2 ){
        Log.info( "Start hasDescriptionFromPublisher" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasDescriptionFromPublisher", pid2 );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcaddi#isDescriptionFromPublisherOf", pid1 );
				Log.info( "End hasDescriptionFromPublisher" );
    };

    that.hasSubjectDescription = function ( pid1, pid2 ){
        Log.info( "Start hasSubjectDescription" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasSubjectDescription", pid2 );
        Log.info( "End hasSubjectDescription" );
    };

    /**
    * Will also set the inverse relation http://oss.dbc.dk/rdf/dbcbibaddi#hasReview
    */
    that.isReviewOf = function ( pid1, pid2 ){
        Log.info( "Start isReviewOf" );
        scriptClass.createRelation( pid2, "http://oss.dbc.dk/rdf/dbcaddi#hasReview", pid1);
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#isReviewOf", pid2 );
        Log.info( "End isReviewOf" );
    };

    that.hasOnlineAccess = function ( pid1, pid2 ){
        Log.info( "Start hasOnlineAccess" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasOnlineAccess", pid2 );
        Log.info( "End hasOnlineAccess" );
    };

    that.hasOpenUrl = function ( pid1, pid2 ){
        Log.info( "Start hasOpenUrl" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasOpenUrl", pid2 );
        Log.info( "End hasOpenUrl" );
    };

    that.hasCreatorDescription = function ( pid1, pid2 ){
        Log.info( "Start hasCreatorDescription" );
        scriptClass.createRelation( pid1, "http://oss.dbc.dk/rdf/dbcaddi#hasCreatorDescription", pid2 );
        Log.info( "End hasCreatorDescription" );
    };

return that;}();
