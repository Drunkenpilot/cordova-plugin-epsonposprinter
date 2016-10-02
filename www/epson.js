/*global cordova, module*/

module.exports = {
  search : function(args,success_callback, error_callback ){
      cordova.exec(success_callback,error_callback,"EpsonPrinter","search",[args]);
  },
  print : function(success_callback, error_callback ){
      cordova.exec(success_callback,error_callback,"EpsonPrinter","print",[]);
  }
}
