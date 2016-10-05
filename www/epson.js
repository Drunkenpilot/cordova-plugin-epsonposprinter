/*global cordova, module*/

module.exports = {
  search : function(timeout,success_callback, error_callback ){
      cordova.exec(success_callback,error_callback,"EpsonPrinter","search",[timeout]);
  },
  print : function(success_callback, error_callback ){
      cordova.exec(success_callback,error_callback,"EpsonPrinter","print",[[[{"name":"TextSize","value":"24"},{"name":"Text","value":"This is a test","newLine":true},{"name":"Line","value":180},{"name":"Align","value":"LEFT"}]],[1]]);
  }
}
