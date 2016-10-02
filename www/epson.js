/*global cordova, module*/

module.exports = {
  search : function(timeout,success_callback, error_callback ){
      cordova.exec(success_callback,error_callback,"EpsonPrinter","search",[timeout]);
  },
  print : function(success_callback, error_callback ){
      cordova.exec(success_callback,error_callback,"EpsonPrinter","print",[]);
  }
}
