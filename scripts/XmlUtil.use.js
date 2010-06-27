// Module to handle some small conversion issues with XML strings and objects.

// use( "UnitTest" );
// use( "Log" );

EXPORTED_SYMBOLS = [ "XmlUtil" ];
const XmlUtil = function() {
    var that = {};

    that.fromString = function( xmlString ) {
//       Log.trace("->XmlUtil.fromString()"); // AUTO::BUG#8976
        // I am really not even sure this is needed! XML.ignoreProcessingInstructions is true by default.
	// Log.debug( "XmlUtil.fromString: xmlString is:\n", xmlString );
        var foo = XML.ignoreProcessingInstructions;
//        XML.ignoreProcessingInstructions = true;
	xmlString = xmlString.replace( /^<\?xml.*\?>/, "" ); // string <?xml...> from head
	xmlString = xmlString.replace( /^\<\!DOCTYPE.*?\>/, "" ); // strip <!DOCTYPE...> from head
	// Log.debug( "XmlUtil.fromString: xmlString after replace:\n", xmlString );
        var res = new XML ( xmlString ); // This is needed for reasosn that are not totally clear to me );
// 	Log.debug( "XmlUtil.fromString: resulting xml:\n", res );
        return res;
    };
    that.fromString.__doc__ = <doc type="method"><brief>Create an XML object from a string</brief>
                                <description>This method safely creates an XML object from a string. If a preprocessing directive is present, it removes it. An error will be thrown, if the string is not wellformed XML.</description>
                                <param name="xmlString">A string containing XML, possibly with a preprocessing directive</param>
                                <returns>An XML object created from the string</returns>
                                <syntax>XmlUtil.fromString( xmlString );</syntax>
                              </doc>;

    that.__doc__ = <doc type="namespace"><brief>Utility methods for XML handling</brief></doc>;

    that.listFromString = function( xmlString ) {
//       Log.trace("->XmlUtil.fromString()"); // AUTO::BUG#8976
        // I am really not even sure this is needed! XML.ignoreProcessingInstructions is true by default.
        // Log.debug( "XmlUtil.fromString: xmlString is:\n", xmlString );
        var foo = XML.ignoreProcessingInstructions;
        XML.ignoreProcessingInstructions = true;
        xmlString = xmlString.replace( /^<\?xml.*\?>/, "" ); // string <?xml...> from head
        xmlString = xmlString.replace( /^\<\!DOCTYPE.*?\>/, "" ); // strip <!DOCTYPE...> from head
        // Log.debug( "XmlUtil.fromString: xmlString after replace:\n", xmlString );
        var res = new XMLList ( xmlString ); // This is needed for reasosn that are not totally clear to me );
//      Log.debug( "XmlUtil.fromString: resulting xml:\n", res );
        return res;
    };


    that.sortElements = function ( xml ) {
      Log.trace("->XmlUtil.sortElements()"); // AUTO::BUG#8976

    var elementList = xml.*;

    var elementArray = [];

    for each (var item in elementList) {
      elementArray.push(item.name());
    }

    elementArray.sort();

    var xmlOut = xml;

    delete xmlOut.*;

    for (var i in elementArray) {
      if(elementArray.hasOwnProperty(i)) {
        element = elementArray[i];
        for each (var child in elementList) {
          if ( child.name() === element ) {
            xmlOut.element = child;
          }
        }
      }
    }

    Log.debug( xmlOut );

    return xmlOut;

  };

  that.sortElements.__doc__ = <doc type="method"><brief>Method that sorts XML elements alfabethically (element name)</brief>
      <syntax>XmlUtil.sortElement( xml )</syntax>
              <param name="element">XML objekt to be sorted</param>
              <returns>XML object with alfabethically sorted elements</returns>
      <examples>
</examples>
              </doc>;

    return that;
}();

// UnitTest.addFixture( "Test XmlUtil", function() {
    
//    xmlStringWP = "<?xml version=\"1.0\"?>\n<root><some>node</some></root>";
//    xmlStringWOP = "<root>\n  <some>node</some>\n</root>";
//    xmlCWP  = XmlUtil.fromString( xmlStringWP );
//    xmlCWOP = XmlUtil.fromString( xmlStringWOP );
//    Assert.equal( "Ens uden pp", 'xmlStringWOP', xmlCWP.toXMLString() );
//    Assert.equal( "Ens med pp", 'xmlStringWOP', xmlCWOP.toXMLString() );
//    Assert.exception( "Invalid XML", 'XmlUtil.fromString( "<root>dette er ikke XML</toor>" )' );

//    delete this.xmlStringWP;
//    delete this.xmlStringWOP;
//    delete this.xmlCWP;
//    delete this.xmlCWOP;
//}); 
