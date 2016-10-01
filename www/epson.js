/*global cordova, module*/

module.exports = {
  search : function(success_callback, error_callback ){
      cordova.exec(success_callback,error_callback,"EpsonPrinter","search",[]);
  }
}
