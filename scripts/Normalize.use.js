EXPORTED_SYMBOLS = ['Normalize'];

var Normalize = function(){

  var that = {};

  that.removeSpecialCharacters = function ( string ) {

    var newString = string.replace(/\?|!|\*|'|\u00A4/g, "");
		
		return newString

  };

  that.removeSpecialCharacters.__doc__ = <doc type="method">
    <brief>Method that removes certain special characters from a string</brief>
    <syntax>Normalize.removeSpecialCharacters( string )</syntax>
    <param name="string">String to be normalized</param>
    <description></description>
    <returns>A normalized string without special characters</returns>
    <examples>Normalize.removeSpecialCharacters( "Hvem er du?" )</examples>
  </doc>;

  return that;

}();
