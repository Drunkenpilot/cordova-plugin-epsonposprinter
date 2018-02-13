/*global cordova, module*/
<<<<<<< HEAD
/**
 * @exports EpsonPrinter
 */
/**
 * @module EpsonPrinter
 */


var exec = require('cordova/exec');
var EpsonPrnter = {
    search: function(timeout, success_callback, error_callback) {
        exec(success_callback, error_callback, "EpsonPrinter", "search", [timeout]);
    },
    print: function(args, success_callback, error_callback) {
        args = angular.fromJson(args);
        printContent = args.printContent;
        printTemplate = args.printTemplate;
        printMode = args.printMode;
        printerSeries = args.printerSeries;
        lang = args.lang;
        printTarget = args.printTarget;
        // JSONArray explication
        // 1. printContent JSONArray ex: [{"name":"TextSize","value":"24"},{"name":"Text","value":"This is a test","newLine":true},{"name":"Line","value":180},{"name":"Align","value":"LEFT"}]
        // 2. printTemplate
        // 3. printMode
        // 4. printerSeries
        // 5. lang
        // 6. printTarget
        exec(success_callback, error_callback, "EpsonPrinter", "print", [
            [printContent], printTemplate, printMode, printerSeries, lang, printTarget
        ]);
    }
};


function EpsonPrnter() {}
EpsonPrnter.prototype.search = function(timeout, success_callback, error_callback) {
    exec(success_callback, error_callback, "EpsonPrinter", "search", [timeout]);
};

EpsonPrnter.prototype.print = function(args, success_callback, error_callback) {
=======

module.exports = {
  search : function(timeout,success_callback, error_callback ){
      cordova.exec(success_callback,error_callback,"EpsonPrinter","search",[timeout]);
  },
  print : function(args,success_callback, error_callback ){
>>>>>>> parent of fefb124... update
    args = angular.fromJson(args);
    printContent = args.printContent;
    printTemplate = args.printTemplate;
    printMode = args.printMode;
    printerSeries = args.printerSeries;
    lang = args.lang;
    printTarget = args.printTarget;
    // JSONArray explication
    // 1. printContent JSONArray ex: [{"name":"TextSize","value":"24"},{"name":"Text","value":"This is a test","newLine":true},{"name":"Line","value":180},{"name":"Align","value":"LEFT"}]
    // 2. printTemplate
    // 3. printMode
    // 4. printerSeries
    // 5. lang
    // 6. printTarget
<<<<<<< HEAD
    exec(success_callback, error_callback, "EpsonPrinter", "print", [
        [printContent], printTemplate, printMode, printerSeries, lang, printTarget
    ]);
};

EpsonPrnter.install = function() {
    if (!window.plugins) {
        window.plugins = {};
    }

    window.plugins.EpsonPrnter = new Inject();
    return window.plugins.EpsonPrnter;
};

cordova.addConstructor(EpsonPrnter.install);

module.exports = EpsonPrnter;
=======
      cordova.exec(success_callback,error_callback,"EpsonPrinter","print",[[printContent],printTemplate,printMode,printerSeries,lang,printTarget]);
  }
}
>>>>>>> parent of fefb124... update
