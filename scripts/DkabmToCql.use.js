EXPORTED_SYMBOLS= ['DkabmToCql'];

use ( "XmlNamespaces.use.js" );
use ( "XmlUtil.use.js" );

var DkabmToCql = function() {
	
	var dkabm = XmlNamespaces.dkabm;
  var ac = XmlNamespaces.ac;
  var dkdcplus = XmlNamespaces.dkdcplus;
  var oss = XmlNamespaces.oss;
  var dc = XmlNamespaces.dc;
  var dcterms = XmlNamespaces.dcterms;
  var xsi = XmlNamespaces.xsi;
	var ting = XmlNamespaces.ting;
	
  var that = {};
	
	that.createCqlIndex = function ( xml ) {
		
		var inputXml = XmlUtil.fromString( xml );
		var index = [];
		
		DkabmToCql.createId( inputXml, index );
		DkabmToCql.createRecId( inputXml, index );
		DkabmToCql.createDcTitle( inputXml, index );
		DkabmToCql.createDcCreator( inputXml, index );
		DkabmToCql.createCqlAnyIndexes( inputXml, index );
		DkabmToCql.createDcDescription( inputXml, index );
		DkabmToCql.createDcSubject( inputXml, index );
		DkabmToCql.createDcType( inputXml, index );
		DkabmToCql.createDcFormat( inputXml, index );
		DkabmToCql.createDcLanguage( inputXml, index );
		DkabmToCql.createDcDate( inputXml, index );
		DkabmToCql.createDcSource( inputXml, index );
		DkabmToCql.createDcIdentifier( inputXml, index );
		DkabmToCql.createDcPublisher( inputXml, index );
		DkabmToCql.createDcRelation( inputXml, index );
		DkabmToCql.createDcRights( inputXml, index );
		DkabmToCql.createAcSource( inputXml, index );
		DkabmToCql.createPhraseTitle( inputXml, index );
		DkabmToCql.createPhraseCreator( inputXml, index );
		DkabmToCql.createPhraseAnyIndexes( inputXml, index );
		DkabmToCql.createPhraseDescription( inputXml, index );
		DkabmToCql.createPhraseSubject( inputXml, index );
		DkabmToCql.createPhraseType( inputXml, index );
		DkabmToCql.createPhraseLanguage( inputXml, index );
		DkabmToCql.createPhraseDate( inputXml, index );
		DkabmToCql.createPhraseSource( inputXml, index );
		DkabmToCql.createPhraseIdentifier( inputXml, index );
		DkabmToCql.createPhrasePublisher( inputXml, index );
		DkabmToCql.createFacetAcSource( inputXml, index );
		DkabmToCql.createFacetCategory( inputXml, index );
		DkabmToCql.createFacetGenreCategory( inputXml, index );
		DkabmToCql.createFacetCreator( inputXml, index );
		DkabmToCql.createFacetType( inputXml, index );
		DkabmToCql.createFacetSubject( inputXml, index );
		DkabmToCql.createFacetDate( inputXml, index );
		DkabmToCql.createFacetLanguage( inputXml, index );
		DkabmToCql.createSortDate( inputXml, index );
		DkabmToCql.createSortCreator( inputXml, index );
		DkabmToCql.createSortTitle( inputXml, index );
		DkabmToCql.createRankTitle( inputXml, index );
		DkabmToCql.createRankCreator( inputXml, index );
		DkabmToCql.createRankSubject( inputXml, index );
		DkabmToCql.createRankType( inputXml, index );
		DkabmToCql.createRankLanguage( inputXml, index );
		DkabmToCql.createRankDate( inputXml, index );
		DkabmToCql.createRankSource( inputXml, index );
		DkabmToCql.createRankPublisher( inputXml, index );
		DkabmToCql.createCreator( inputXml, index );
		DkabmToCql.createDate( inputXml, index );
		DkabmToCql.createDescription( inputXml, index );
		DkabmToCql.createFormat( inputXml, index );
		DkabmToCql.createIdentifier( inputXml, index );
		DkabmToCql.createLanguage( inputXml, index );
		DkabmToCql.createPublisher( inputXml, index );
		DkabmToCql.createSource( inputXml, index );
		DkabmToCql.createSubject( inputXml, index );
		DkabmToCql.createTitle( inputXml, index );
		DkabmToCql.createType( inputXml, index );
		DkabmToCql.createRelation( inputXml, index );
		DkabmToCql.createRights( inputXml, index );
		
		return index;
		
	}
	
	that.createId = function( inputXml, index ) {

    Log.trace( "Entering: createId module" );

    var child;

    var field = {};

    for each (child in inputXml.ting::fedoraPid) {
      field = {
        "name": "id",
        "value": String(child),
        "converter": ""
      }
      index.push( field );
    }

    Log.trace( "Leaving: createId module" );

    return index;

  };


  that.createId.__doc__ = <doc type="method">
    <brief>Method that creates id index fields from input data</brief>
    <syntax>DkabmToCql.createId( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createId( inputXml, index )</examples>
  </doc>;


  that.createRecId = function( inputXml, index ) {

    Log.trace( "Entering: createRecId module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.ac::identifier) {
      field = {
        "name": "rec.id",
        "value": String(child),
        "converter": "sort"
      }
      index.push( field );
    }

    for each (child in inputXml.ting::fedoraPid) {
      field = {
        "name": "rec.id",
        "value": String(child),
        "converter": "sort"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createRecId module" );

    return index;

  };


  that.createRecId.__doc__ = <doc type="method">
    <brief>Method that creates rec.id index fields from input data</brief>
    <syntax>DkabmToCql.createRecId( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createRecId( inputXml, index )</examples>
  </doc>;


  that.createDcTitle = function( inputXml, index ) {

    Log.trace( "Entering: createDcTitle module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::title) {
      field = {
        "name": "dc.title",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createDcTitle module" );

    return index;

  };


  that.createDcTitle.__doc__ = <doc type="method">
    <brief>Method that creates dc.title index fields from input data</brief>
    <syntax>DkabmToCql.createDcTitle( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createDcTitle( inputXml, index )</examples>
  </doc>;


  that.createDcCreator = function( inputXml, index ) {

    Log.trace( "Entering: createDcCreator module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::creator) {
      field = {
        "name": "dc.creator",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    for each (child in inputXml.dkabm::record.dc::contributor) {
      field = {
        "name": "dc.creator",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createDcCreator module" );

    return index;

  };


  that.createDcCreator.__doc__ = <doc type="method">
    <brief>Method that creates dc.creator index fields from input data</brief>
    <syntax>DkabmToCql.createDcCreator( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createDcCreator( inputXml, index )</examples>
  </doc>;


  that.createCqlAnyIndexes = function( inputXml, index ) {

    Log.trace( "Entering: createCqlAnyIndexes module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.*) {
      field = {
        "name": "cql.anyIndexes",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createCqlAnyIndexes module" );

    return index;

  };


  that.createCqlAnyIndexes.__doc__ = <doc type="method">
    <brief>Method that creates cql.anyIndexes index fields from input data</brief>
    <syntax>DkabmToCql.createCqlAnyIndexes( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createCqlAnyIndexes( inputXml, index )</examples>
  </doc>;


  that.createDcDescription = function( inputXml, index ) {

    Log.trace( "Entering: createDcDescription module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::description) {
      field = {
        "name": "dc.description",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    for each (child in inputXml.dkabm::record["dcterms::abstract"]) {
      field = {
        "name": "dc.description",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createDcDescription module" );

    return index;

  };


  that.createDcDescription.__doc__ = <doc type="method">
    <brief>Method that creates dc.description index fields from input data</brief>
    <syntax>DkabmToCql.createDcDescription( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createDcDescription( inputXml, index )</examples>
  </doc>;


  that.createDcSubject = function( inputXml, index ) {

    Log.trace( "Entering: createDcSubject module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::subject) {
      field = {
        "name": "dc.subject",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createDcSubject module" );

    return index;

  };


  that.createDcSubject.__doc__ = <doc type="method">
    <brief>Method that creates dc.subject index fields from input data</brief>
    <syntax>DkabmToCql.createDcSubject( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createDcSubject( inputXml, index )</examples>
  </doc>;


  that.createDcType = function( inputXml, index ) {

    Log.trace( "Entering: createDcType module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::type) {
      field = {
        "name": "dc.type",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createDcType module" );

    return index;

  };


  that.createDcType.__doc__ = <doc type="method">
    <brief>Method that creates dc.type index fields from input data</brief>
    <syntax>DkabmToCql.createDcType( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createDcType( inputXml, index )</examples>
  </doc>;


  that.createDcFormat = function( inputXml, index ) {

    Log.trace( "Entering: createDcFormat module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::format) {
      field = {
        "name": "dc.format",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    for each (child in inputXml.dkabm::record.dcterms::extent) {
      field = {
        "name": "dc.format",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createDcFormat module" );

    return index;

  };


  that.createDcFormat.__doc__ = <doc type="method">
    <brief>Method that creates dc.format index fields from input data</brief>
    <syntax>DkabmToCql.createDcFormat( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createDcFormat( inputXml, index )</examples>
  </doc>;


  that.createDcLanguage = function( inputXml, index ) {

    Log.trace( "Entering: createDcLanguage module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::language) {
      field = {
        "name": "dc.language",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createDcLanguage module" );

    return index;

  };


  that.createDcLanguage.__doc__ = <doc type="method">
    <brief>Method that creates dc.language index fields from input data</brief>
    <syntax>DkabmToCql.createDcLanguage( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createDcLanguage( inputXml, index )</examples>
  </doc>;


  that.createDcDate = function( inputXml, index ) {

    Log.trace( "Entering: createDcDate module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::date) {
      field = {
        "name": "dc.date",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createDcDate module" );

    return index;

  };


  that.createDcDate.__doc__ = <doc type="method">
    <brief>Method that creates dc.date index fields from input data</brief>
    <syntax>DkabmToCql.createDcDate( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createDcDate( inputXml, index )</examples>
  </doc>;


  that.createDcSource = function( inputXml, index ) {

    Log.trace( "Entering: createDcSource module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::source) {
      field = {
        "name": "dc.source",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createDcSource module" );

    return index;

  };


  that.createDcSource.__doc__ = <doc type="method">
    <brief>Method that creates dc.source index fields from input data</brief>
    <syntax>DkabmToCql.createDcSource( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createDcSource( inputXml, index )</examples>
  </doc>;


  that.createDcIdentifier = function( inputXml, index ) {

    Log.trace( "Entering: createDcIdentifier module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::identifier) {
      field = {
        "name": "dc.identifier",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createDcIdentifier module" );

    return index;

  };


  that.createDcIdentifier.__doc__ = <doc type="method">
    <brief>Method that creates dc.identifier index fields from input data</brief>
    <syntax>DkabmToCql.createDcIdentifier( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createDcIdentifier( inputXml, index )</examples>
  </doc>;


  that.createDcPublisher = function( inputXml, index ) {

    Log.trace( "Entering: createDcPublisher module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::publisher) {
      field = {
        "name": "dc.publisher",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createDcPublisher module" );

    return index;

  };


  that.createDcPublisher.__doc__ = <doc type="method">
    <brief>Method that creates dc.publisher index fields from input data</brief>
    <syntax>DkabmToCql.createDcPublisher( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createDcPublisher( inputXml, index )</examples>
  </doc>;


  that.createDcRelation = function( inputXml, index ) {

    Log.trace( "Entering: createDcRelation module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::relation) {
      field = {
        "name": "dc.relation",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createDcRelation module" );

    return index;

  };


  that.createDcRelation.__doc__ = <doc type="method">
    <brief>Method that creates dc.relation index fields from input data</brief>
    <syntax>DkabmToCql.createDcRelation( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createDcRelation( inputXml, index )</examples>
  </doc>;


  that.createDcRights = function( inputXml, index ) {

    Log.trace( "Entering: createDcRights module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::rights) {
      field = {
        "name": "dc.rights",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createDcRights module" );

    return index;

  };


  that.createDcRights.__doc__ = <doc type="method">
    <brief>Method that creates dc.rights index fields from input data</brief>
    <syntax>DkabmToCql.createDcRights( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createDcRights( inputXml, index )</examples>
  </doc>;


  that.createAcSource = function( inputXml, index ) {

    Log.trace( "Entering: createAcSource module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.ac::source) {
      field = {
        "name": "ac.source",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    for each (child in inputXml.ting::container.format) {
      field = {
        "name": "ac.source",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createAcSource module" );

    return index;

  };


  that.createAcSource.__doc__ = <doc type="method">
    <brief>Method that creates ac.source index fields from input data</brief>
    <syntax>DkabmToCql.createAcSource( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createAcSource( inputXml, index )</examples>
  </doc>;


  that.createPhraseTitle = function( inputXml, index ) {

    Log.trace( "Entering: createPhraseTitle module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::title) {
      field = {
        "name": "phrase.title",
        "value": String(child),
        "converter": "phrase"
      }
      index.push( field );
    }

    for each (child in inputXml.dkabm::record.dcterms::alternative) {
      field = {
        "name": "phrase.title",
        "value": String(child),
        "converter": "phrase"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createPhraseTitle module" );

    return index;

  };


  that.createPhraseTitle.__doc__ = <doc type="method">
    <brief>Method that creates phrase.title index fields from input data</brief>
    <syntax>DkabmToCql.createPhraseTitle( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createPhraseTitle( inputXml, index )</examples>
  </doc>;


  that.createPhraseCreator = function( inputXml, index ) {

    Log.trace( "Entering: createPhraseCreator module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::creator) {
      field = {
        "name": "phrase.creator",
        "value": String(child),
        "converter": "phrase"
      }
      index.push( field );
    }

    for each (child in inputXml.dkabm::record.dc::contributor) {
      field = {
        "name": "phrase.creator",
        "value": String(child),
        "converter": "phrase"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createPhraseCreator module" );

    return index;

  };


  that.createPhraseCreator.__doc__ = <doc type="method">
    <brief>Method that creates phrase.creator index fields from input data</brief>
    <syntax>DkabmToCql.createPhraseCreator( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createPhraseCreator( inputXml, index )</examples>
  </doc>;


  that.createPhraseAnyIndexes = function( inputXml, index ) {

    Log.trace( "Entering: createPhraseAnyIndexes module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.*) {
      field = {
        "name": "phrase.anyIndexes",
        "value": String(child),
        "converter": "phrase"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createPhraseAnyIndexes module" );

    return index;

  };


  that.createPhraseAnyIndexes.__doc__ = <doc type="method">
    <brief>Method that creates phrase.anyIndexes index fields from input data</brief>
    <syntax>DkabmToCql.createPhraseAnyIndexes( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createPhraseAnyIndexes( inputXml, index )</examples>
  </doc>;


  that.createPhraseDescription = function( inputXml, index ) {

    Log.trace( "Entering: createPhraseDescription module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::description) {
      field = {
        "name": "phrase.description",
        "value": String(child),
        "converter": "phrase"
      }
      index.push( field );
    }

    for each (child in inputXml.dkabm::record["dcterms::abstract"]) {
      field = {
        "name": "phrase.description",
        "value": String(child),
        "converter": "phrase"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createPhraseDescription module" );

    return index;

  };


  that.createPhraseDescription.__doc__ = <doc type="method">
    <brief>Method that creates phrase.description index fields from input data</brief>
    <syntax>DkabmToCql.createPhraseDescription( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createPhraseDescription( inputXml, index )</examples>
  </doc>;


  that.createPhraseSubject = function( inputXml, index ) {

    Log.trace( "Entering: createPhraseSubject module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::subject) {
      field = {
        "name": "phrase.subject",
        "value": String(child),
        "converter": "phrase"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createPhraseSubject module" );

    return index;

  };


  that.createPhraseSubject.__doc__ = <doc type="method">
    <brief>Method that creates phrase.subject index fields from input data</brief>
    <syntax>DkabmToCql.createPhraseSubject( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createPhraseSubject( inputXml, index )</examples>
  </doc>;


  that.createPhraseType = function( inputXml, index ) {

    Log.trace( "Entering: createPhraseType module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::type) {
      field = {
        "name": "phrase.type",
        "value": String(child),
        "converter": "phrase"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createPhraseType module" );

    return index;

  };


  that.createPhraseType.__doc__ = <doc type="method">
    <brief>Method that creates phrase.type index fields from input data</brief>
    <syntax>DkabmToCql.createPhraseType( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createPhraseType( inputXml, index )</examples>
  </doc>;


  that.createPhraseLanguage = function( inputXml, index ) {

    Log.trace( "Entering: createPhraseLanguage module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::language) {
      field = {
        "name": "phrase.language",
        "value": String(child),
        "converter": "phrase"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createPhraseLanguage module" );

    return index;

  };


  that.createPhraseLanguage.__doc__ = <doc type="method">
    <brief>Method that creates phrase.language index fields from input data</brief>
    <syntax>DkabmToCql.createPhraseLanguage( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createPhraseLanguage( inputXml, index )</examples>
  </doc>;


  that.createPhraseDate = function( inputXml, index ) {

    Log.trace( "Entering: createPhraseDate module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::date) {
      field = {
        "name": "phrase.date",
        "value": String(child),
        "converter": "phrase"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createPhraseDate module" );

    return index;

  };


  that.createPhraseDate.__doc__ = <doc type="method">
    <brief>Method that creates phrase.date index fields from input data</brief>
    <syntax>DkabmToCql.createPhraseDate( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createPhraseDate( inputXml, index )</examples>
  </doc>;


  that.createPhraseSource = function( inputXml, index ) {

    Log.trace( "Entering: createPhraseSource module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::source) {
      field = {
        "name": "phrase.source",
        "value": String(child),
        "converter": "phrase"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createPhraseSource module" );

    return index;

  };


  that.createPhraseSource.__doc__ = <doc type="method">
    <brief>Method that creates phrase.source index fields from input data</brief>
    <syntax>DkabmToCql.createPhraseSource( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createPhraseSource( inputXml, index )</examples>
  </doc>;


  that.createPhraseIdentifier = function( inputXml, index ) {

    Log.trace( "Entering: createPhraseIdentifier module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::identifier) {
      field = {
        "name": "phrase.identifier",
        "value": String(child),
        "converter": "phrase"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createPhraseIdentifier module" );

    return index;

  };


  that.createPhraseIdentifier.__doc__ = <doc type="method">
    <brief>Method that creates phrase.identifier index fields from input data</brief>
    <syntax>DkabmToCql.createPhraseIdentifier( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createPhraseIdentifier( inputXml, index )</examples>
  </doc>;


  that.createPhrasePublisher = function( inputXml, index ) {

    Log.trace( "Entering: createPhrasePublisher module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::publisher) {
      field = {
        "name": "phrase.publisher",
        "value": String(child),
        "converter": "phrase"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createPhrasePublisher module" );

    return index;

  };


  that.createPhrasePublisher.__doc__ = <doc type="method">
    <brief>Method that creates phrase.publisher index fields from input data</brief>
    <syntax>DkabmToCql.createPhrasePublisher( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createPhrasePublisher( inputXml, index )</examples>
  </doc>;


  that.createFacetAcSource = function( inputXml, index ) {

    Log.trace( "Entering: createFacetAcSource module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.ac::source) {
      field = {
        "name": "facet.acSource",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createFacetAcSource module" );

    return index;

  };


  that.createFacetAcSource.__doc__ = <doc type="method">
    <brief>Method that creates facet.acSource index fields from input data</brief>
    <syntax>DkabmToCql.createFacetAcSource( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createFacetAcSource( inputXml, index )</examples>
  </doc>;


  that.createFacetCategory = function( inputXml, index ) {

    Log.trace( "Entering: createFacetCategory module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dcterms::audience) {
      field = {
        "name": "facet.category",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createFacetCategory module" );

    return index;

  };


  that.createFacetCategory.__doc__ = <doc type="method">
    <brief>Method that creates facet.category index fields from input data</brief>
    <syntax>DkabmToCql.createFacetCategory( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createFacetCategory( inputXml, index )</examples>
  </doc>;


  that.createFacetGenreCategory = function( inputXml, index ) {

    Log.trace( "Entering: createFacetGenreCategory module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::subject) {
			if (String(child) == "fiktion") {
	  		field = {
	  			"name": "facet.genreCategory",
	  			"value": String(child),
	  			"converter": "facet"
	  		}
	  		index.push(field);
	  	}
    }

    for each (child in inputXml.dkabm::record.dc::subject) {
			if (String(child) == "fiktion") {
		  	field = {
		  		"name": "facet.genreCategory",
		  		"value": String(child),
		  		"converter": "facet"
		  	}
		  	index.push(field);
	  	}
    }

    Log.trace( "Leaving: createFacetGenreCategory module" );

    return index;

  };


  that.createFacetGenreCategory.__doc__ = <doc type="method">
    <brief>Method that creates facet.genreCategory index fields from input data</brief>
    <syntax>DkabmToCql.createFacetGenreCategory( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createFacetGenreCategory( inputXml, index )</examples>
  </doc>;


  that.createFacetCreator = function( inputXml, index ) {

    Log.trace( "Entering: createFacetCreator module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::creator) {
			//if (String(child.@xsi::type) != "oss:sort") {
	      field = {
	        "name": "facet.creator",
	        "value": String(child),
	        "converter": "facet"
	      }
	      index.push( field );
			//}
    }

    for each (child in inputXml.dkabm::record.dc::contributor) {
      field = {
        "name": "facet.creator",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createFacetCreator module" );

    return index;

  };


  that.createFacetCreator.__doc__ = <doc type="method">
    <brief>Method that creates facet.creator index fields from input data</brief>
    <syntax>DkabmToCql.createFacetCreator( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createFacetCreator( inputXml, index )</examples>
  </doc>;


  that.createFacetType = function( inputXml, index ) {

    Log.trace( "Entering: createFacetType module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::type) {
      field = {
        "name": "facet.type",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createFacetType module" );

    return index;

  };


  that.createFacetType.__doc__ = <doc type="method">
    <brief>Method that creates facet.type index fields from input data</brief>
    <syntax>DkabmToCql.createFacetType( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createFacetType( inputXml, index )</examples>
  </doc>;


  that.createFacetSubject = function( inputXml, index ) {

    Log.trace( "Entering: createFacetSubject module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::subject) {
			//if(String(child.@xsi::type) != "dkdcplus:genre") {
	      field = {
	        "name": "facet.subject",
	        "value": String(child),
	        "converter": "facet"
	      }
	      index.push( field );
			//}
    }

    Log.trace( "Leaving: createFacetSubject module" );

    return index;

  };


  that.createFacetSubject.__doc__ = <doc type="method">
    <brief>Method that creates facet.subject index fields from input data</brief>
    <syntax>DkabmToCql.createFacetSubject( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createFacetSubject( inputXml, index )</examples>
  </doc>;


  that.createFacetDate = function( inputXml, index ) {

    Log.trace( "Entering: createFacetDate module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::date) {
      field = {
        "name": "facet.date",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createFacetDate module" );

    return index;

  };


  that.createFacetDate.__doc__ = <doc type="method">
    <brief>Method that creates facet.date index fields from input data</brief>
    <syntax>DkabmToCql.createFacetDate( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createFacetDate( inputXml, index )</examples>
  </doc>;


  that.createFacetLanguage = function( inputXml, index ) {

    Log.trace( "Entering: createFacetLanguage module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::language) {
      field = {
        "name": "facet.language",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createFacetLanguage module" );

    return index;

  };


  that.createFacetLanguage.__doc__ = <doc type="method">
    <brief>Method that creates facet.language index fields from input data</brief>
    <syntax>DkabmToCql.createFacetLanguage( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createFacetLanguage( inputXml, index )</examples>
  </doc>;


  that.createSortDate = function( inputXml, index ) {

    Log.trace( "Entering: createSortDate module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::date[1]) {
      field = {
        "name": "sort.date",
        "value": String(child),
        "converter": "sort"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createSortDate module" );

    return index;

  };


  that.createSortDate.__doc__ = <doc type="method">
    <brief>Method that creates sort.date index fields from input data</brief>
    <syntax>DkabmToCql.createSortDate( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createSortDate( inputXml, index )</examples>
  </doc>;


  that.createSortCreator = function( inputXml, index ) {

    Log.trace( "Entering: createSortCreator module" );

    var child;

    var field = {};
		
		var count = 0;

    for each (child in inputXml.dkabm::record.dc::creator) {
			//if (String(child.@xsi::type) == "oss::sort" && count === 0) {
	      field = {
	        "name": "sort.creator",
	        "value": String(child),
	        "converter": "sort"
	      }
	      index.push( field );
				count++;
			//}
    }

    Log.trace( "Leaving: createSortCreator module" );

    return index;

  };


  that.createSortCreator.__doc__ = <doc type="method">
    <brief>Method that creates sort.creator index fields from input data</brief>
    <syntax>DkabmToCql.createSortCreator( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createSortCreator( inputXml, index )</examples>
  </doc>;


  that.createSortTitle = function( inputXml, index ) {

    Log.trace( "Entering: createSortTitle module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::title[1]) {
      field = {
        "name": "sort.title",
        "value": String(child),
        "converter": "sort"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createSortTitle module" );

    return index;

  };


  that.createSortTitle.__doc__ = <doc type="method">
    <brief>Method that creates sort.title index fields from input data</brief>
    <syntax>DkabmToCql.createSortTitle( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createSortTitle( inputXml, index )</examples>
  </doc>;


  that.createRankTitle = function( inputXml, index ) {

    Log.trace( "Entering: createRankTitle module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::title) {
      field = {
        "name": "rank.title",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createRankTitle module" );

    return index;

  };


  that.createRankTitle.__doc__ = <doc type="method">
    <brief>Method that creates rank.title index fields from input data</brief>
    <syntax>DkabmToCql.createRankTitle( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createRankTitle( inputXml, index )</examples>
  </doc>;


  that.createRankCreator = function( inputXml, index ) {

    Log.trace( "Entering: createRankCreator module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::creator) {
      field = {
        "name": "rank.creator",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createRankCreator module" );

    return index;

  };


  that.createRankCreator.__doc__ = <doc type="method">
    <brief>Method that creates rank.creator index fields from input data</brief>
    <syntax>DkabmToCql.createRankCreator( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createRankCreator( inputXml, index )</examples>
  </doc>;


  that.createRankSubject = function( inputXml, index ) {

    Log.trace( "Entering: createRankSubject module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::subject) {
      field = {
        "name": "rank.subject",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createRankSubject module" );

    return index;

  };


  that.createRankSubject.__doc__ = <doc type="method">
    <brief>Method that creates rank.subject index fields from input data</brief>
    <syntax>DkabmToCql.createRankSubject( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createRankSubject( inputXml, index )</examples>
  </doc>;


  that.createRankType = function( inputXml, index ) {

    Log.trace( "Entering: createRankType module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::type) {
      field = {
        "name": "rank.type",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createRankType module" );

    return index;

  };


  that.createRankType.__doc__ = <doc type="method">
    <brief>Method that creates rank.type index fields from input data</brief>
    <syntax>DkabmToCql.createRankType( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createRankType( inputXml, index )</examples>
  </doc>;


  that.createRankLanguage = function( inputXml, index ) {

    Log.trace( "Entering: createRankLanguage module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::language) {
      field = {
        "name": "rank.language",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createRankLanguage module" );

    return index;

  };


  that.createRankLanguage.__doc__ = <doc type="method">
    <brief>Method that creates rank.language index fields from input data</brief>
    <syntax>DkabmToCql.createRankLanguage( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createRankLanguage( inputXml, index )</examples>
  </doc>;


  that.createRankDate = function( inputXml, index ) {

    Log.trace( "Entering: createRankDate module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::date) {
      field = {
        "name": "rank.date",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createRankDate module" );

    return index;

  };


  that.createRankDate.__doc__ = <doc type="method">
    <brief>Method that creates rank.date index fields from input data</brief>
    <syntax>DkabmToCql.createRankDate( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createRankDate( inputXml, index )</examples>
  </doc>;


  that.createRankSource = function( inputXml, index ) {

    Log.trace( "Entering: createRankSource module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::source) {
      field = {
        "name": "rank.source",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createRankSource module" );

    return index;

  };


  that.createRankSource.__doc__ = <doc type="method">
    <brief>Method that creates rank.source index fields from input data</brief>
    <syntax>DkabmToCql.createRankSource( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createRankSource( inputXml, index )</examples>
  </doc>;


  that.createRankPublisher = function( inputXml, index ) {

    Log.trace( "Entering: createRankPublisher module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::publisher) {
      field = {
        "name": "rank.publisher",
        "value": String(child),
        "converter": "default"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createRankPublisher module" );

    return index;

  };


  that.createRankPublisher.__doc__ = <doc type="method">
    <brief>Method that creates rank.publisher index fields from input data</brief>
    <syntax>DkabmToCql.createRankPublisher( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createRankPublisher( inputXml, index )</examples>
  </doc>;


  that.createCreator = function( inputXml, index ) {

    Log.trace( "Entering: createCreator module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::creator) {
      field = {
        "name": "creator",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    for each (child in inputXml.dkabm::record.dc::contributor) {
      field = {
        "name": "creator",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createCreator module" );

    return index;

  };


  that.createCreator.__doc__ = <doc type="method">
    <brief>Method that creates creator index fields from input data</brief>
    <syntax>DkabmToCql.createCreator( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createCreator( inputXml, index )</examples>
  </doc>;


  that.createDate = function( inputXml, index ) {

    Log.trace( "Entering: createDate module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::date) {
      field = {
        "name": "date",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createDate module" );

    return index;

  };


  that.createDate.__doc__ = <doc type="method">
    <brief>Method that creates date index fields from input data</brief>
    <syntax>DkabmToCql.createDate( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createDate( inputXml, index )</examples>
  </doc>;


  that.createDescription = function( inputXml, index ) {

    Log.trace( "Entering: createDescription module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::description) {
      field = {
        "name": "description",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    for each (child in inputXml.dkabm::record["dcterms::abstract"]) {
      field = {
        "name": "description",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createDescription module" );

    return index;

  };


  that.createDescription.__doc__ = <doc type="method">
    <brief>Method that creates description index fields from input data</brief>
    <syntax>DkabmToCql.createDescription( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createDescription( inputXml, index )</examples>
  </doc>;


  that.createFormat = function( inputXml, index ) {

    Log.trace( "Entering: createFormat module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::format) {
      field = {
        "name": "format",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    for each (child in inputXml.dkabm::record.dcterms::extent) {
      field = {
        "name": "format",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createFormat module" );

    return index;

  };


  that.createFormat.__doc__ = <doc type="method">
    <brief>Method that creates format index fields from input data</brief>
    <syntax>DkabmToCql.createFormat( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createFormat( inputXml, index )</examples>
  </doc>;


  that.createIdentifier = function( inputXml, index ) {

    Log.trace( "Entering: createIdentifier module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::identifier) {
      field = {
        "name": "identifier",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createIdentifier module" );

    return index;

  };


  that.createIdentifier.__doc__ = <doc type="method">
    <brief>Method that creates identifier index fields from input data</brief>
    <syntax>DkabmToCql.createIdentifier( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createIdentifier( inputXml, index )</examples>
  </doc>;


  that.createLanguage = function( inputXml, index ) {

    Log.trace( "Entering: createLanguage module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::language) {
      field = {
        "name": "language",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createLanguage module" );

    return index;

  };


  that.createLanguage.__doc__ = <doc type="method">
    <brief>Method that creates language index fields from input data</brief>
    <syntax>DkabmToCql.createLanguage( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createLanguage( inputXml, index )</examples>
  </doc>;


  that.createPublisher = function( inputXml, index ) {

    Log.trace( "Entering: createPublisher module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::publisher) {
      field = {
        "name": "publisher",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createPublisher module" );

    return index;

  };


  that.createPublisher.__doc__ = <doc type="method">
    <brief>Method that creates publisher index fields from input data</brief>
    <syntax>DkabmToCql.createPublisher( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createPublisher( inputXml, index )</examples>
  </doc>;


  that.createSource = function( inputXml, index ) {

    Log.trace( "Entering: createSource module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::source) {
      field = {
        "name": "source",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createSource module" );

    return index;

  };


  that.createSource.__doc__ = <doc type="method">
    <brief>Method that creates source index fields from input data</brief>
    <syntax>DkabmToCql.createSource( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createSource( inputXml, index )</examples>
  </doc>;


  that.createSubject = function( inputXml, index ) {

    Log.trace( "Entering: createSubject module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::subject) {
      field = {
        "name": "subject",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createSubject module" );

    return index;

  };


  that.createSubject.__doc__ = <doc type="method">
    <brief>Method that creates subject index fields from input data</brief>
    <syntax>DkabmToCql.createSubject( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createSubject( inputXml, index )</examples>
  </doc>;


  that.createTitle = function( inputXml, index ) {

    Log.trace( "Entering: createTitle module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::title) {
      field = {
        "name": "title",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createTitle module" );

    return index;

  };


  that.createTitle.__doc__ = <doc type="method">
    <brief>Method that creates title index fields from input data</brief>
    <syntax>DkabmToCql.createTitle( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createTitle( inputXml, index )</examples>
  </doc>;


  that.createType = function( inputXml, index ) {

    Log.trace( "Entering: createType module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::type) {
      field = {
        "name": "type",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createType module" );

    return index;

  };


  that.createType.__doc__ = <doc type="method">
    <brief>Method that creates type index fields from input data</brief>
    <syntax>DkabmToCql.createType( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createType( inputXml, index )</examples>
  </doc>;


  that.createRelation = function( inputXml, index ) {

    Log.trace( "Entering: createRelation module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::relation) {
      field = {
        "name": "relation",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createRelation module" );

    return index;

  };


  that.createRelation.__doc__ = <doc type="method">
    <brief>Method that creates relation index fields from input data</brief>
    <syntax>DkabmToCql.createRelation( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createRelation( inputXml, index )</examples>
  </doc>;


  that.createRights = function( inputXml, index ) {

    Log.trace( "Entering: createRights module" );

    var child;

    var field = {};

    for each (child in inputXml.dkabm::record.dc::rights) {
      field = {
        "name": "rights",
        "value": String(child),
        "converter": "facet"
      }
      index.push( field );
    }

    Log.trace( "Leaving: createRights module" );

    return index;

  };


  that.createRights.__doc__ = <doc type="method">
    <brief>Method that creates rights index fields from input data</brief>
    <syntax>DkabmToCql.createRights( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.createRights( inputXml, index )</examples>
  </doc>;


  that.create = function( inputXml, index ) {

    Log.trace( "Entering: create module" );

    var child;

    var field = {};

    for each (child in inputXml.ting::fedoraPid) {
      field = {
        "name": "",
        "value": String(child),
        "converter": ""
      }
      index.push( field );
    }

    Log.trace( "Leaving: create module" );

    return index;

  };


  that.create.__doc__ = <doc type="method">
    <brief>Method that creates  index fields from input data</brief>
    <syntax>DkabmToCql.create( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.create( inputXml, index )</examples>
  </doc>;


  that.create = function( inputXml, index ) {

    Log.trace( "Entering: create module" );

    var child;

    var field = {};

    for each (child in inputXml.ting::fedoraNormPid) {
      field = {
        "name": "",
        "value": String(child),
        "converter": "lowercase"
      }
      index.push( field );
    }

    Log.trace( "Leaving: create module" );

    return index;

  };


  that.create.__doc__ = <doc type="method">
    <brief>Method that creates  index fields from input data</brief>
    <syntax>DkabmToCql.create( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.create( inputXml, index )</examples>
  </doc>;


  that.create = function( inputXml, index ) {

    Log.trace( "Entering: create module" );

    var child;

    var field = {};

    for each (child in inputXml.ting::original_format) {
      field = {
        "name": "",
        "value": String(child),
        "converter": ""
      }
      index.push( field );
    }

    Log.trace( "Leaving: create module" );

    return index;

  };


  that.create.__doc__ = <doc type="method">
    <brief>Method that creates  index fields from input data</brief>
    <syntax>DkabmToCql.create( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.create( inputXml, index )</examples>
  </doc>;


  that.create = function( inputXml, index ) {

    Log.trace( "Entering: create module" );

    var child;

    var field = {};

    for each (child in inputXml.ting::submitter) {
      field = {
        "name": "",
        "value": String(child),
        "converter": ""
      }
      index.push( field );
    }

    Log.trace( "Leaving: create module" );

    return index;

  };


  that.create.__doc__ = <doc type="method">
    <brief>Method that creates  index fields from input data</brief>
    <syntax>DkabmToCql.create( inputXml, index )</syntax>
    <param name="inputXml">Xml object containing input data</param>
    <param name="index">The index (object) to add the new index fields to</param>
    <description></description>
    <returns>The updated index object</returns>
    <examples>DkabmToCql.create( inputXml, index )</examples>
  </doc>;

	return that;

}();
