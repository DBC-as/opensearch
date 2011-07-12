EXPORTED_SYMBOLS = ['Normalize'];

use ( "XmlUtil.use.js" );
use ( "XmlNamespaces.use.js" );

var Normalize = function(){

  var that = {};

  that.removeSpecialCharacters = function ( string ) {

    var newString = string.replace(/[[-_[\]{}()*+?!%.,\\^$|#]/g, "");
		
		return newString

  };

  that.removeSpecialCharacters.__doc__ = <doc type="method">
    <brief>Method that extracts data from Open Search Object XML to create Dublin Core data</brief>
    <syntax>DcCreator.removeSpecialCharacters( string )</syntax>
    <param name="string">String to be normalized</param>
    <description></description>
    <returns>A normalized string without special characters</returns>
    <examples>DcCreator.removeSpecialCharacters( "Hvem er du?" )</examples>
  </doc>;

  return that;

}();