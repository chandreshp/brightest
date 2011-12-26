Editor.controller.doCommand = (function(){
    var ref = Editor.controller.doCommand;
    return function(cmd){
        switch(cmd){
            case "cmd_export_results":
                editor.app.saveResults();
                break;
            default:
                ref.apply(this,arguments);
        }
    };
}());

Editor.controller.isCommandEnabled = (function(){
    var ref = Editor.controller.isCommandEnabled;
    return function(cmd){
        if(cmd==="cmd_export_results"){
            return true;
        }else{
            return ref.apply(this,arguments);
        }
    };
}());

Editor.controller.supportCommand = (function(){
    var ref = Editor.controller.supportCommand;
    return function(cmd){
        if(cmd==="cmd_export_results"){
            return true;
        }else{
            return ref.apply(this,arguments);
        }
    };
}());

//Extend Application
Application.prototype.saveResults = function() {
    this.log.debug("saveResults");
    this.getCurrentFormat().saveResults(null, editor.getResultHistoryStack());
    editor.getResultHistoryStack().shift();
};

//Extend Format
//AN: export results
Format.prototype.saveResults = function(file, selResults) {
    if (file === null || file === undefined) {
        file = showFilePicker(window, "Save Results as...",
                          Components.interfaces.nsIFilePicker.modeSave,
                          Format.TEST_CASE_DIRECTORY_PREF,
                          function(fp) { return fp.file; });
    }

    var filePath = file.path.toString();
    var rawResults = [];
    selResults.forEach(function(selResult) {
        var i,rawResult = selResult.getReport();
        testResultStack = selResult.getTestResult();        
        restultStr = ""; 
        for (i = 0; i < testResultStack.length; i++){ 
            restultStr +=  "**$$** " + testResultStack[i]; 
        }         
        rawResult = rawResult + restultStr;        
        //alert("resutStr = " + rawResult);
        rawResults.push(rawResult);
    });
	this.log.debug("saving resuls: file=" + filePath + " and results " + rawResults.join("\n\n"));
    FormatterAddons.saveResults(filePath, rawResults);
    rawResults.shift();
    if (/(.)+.html/.test(filePath)) {
        var fileUrl = "file://"+filePath;
        //AN: open up the file for html extensions
        try{
            content.document.location.href = fileUrl; 
        }catch(e) {
            setTimeout(function() {content.document.location.href = fileUrl;}, 100);
        }
    }
};