/*global cordova, module*/

module.exports = {
  search : function(timeout,success_callback, error_callback ){
      cordova.exec(success_callback,error_callback,"EpsonPrinter","search",[timeout]);
  },
  print : function(args,success_callback, error_callback ){
    console.log(angular.fromJson(args));
    // JSONArray explication
    // 1. printContent JSONArray ex: [[{"name":"TextSize","value":"24"},{"name":"Text","value":"This is a test","newLine":true},{"name":"Line","value":180},{"name":"Align","value":"LEFT"}]]
    // 2. printTemplate
    // 3. printMode
    // 4. printerSeries
    // 5. lang
    // 6. printTarget
      cordova.exec(success_callback,error_callback,"EpsonPrinter","print",[[[{"name":"TextSize","value":"24"},{"name":"Text","value":"This is a test","newLine":true},{"name":"Line","value":180},{"name":"Align","value":"LEFT"}]],1,1,6,0,"USB:/dev/bus/usb/002/004"]);
  }
}
