Editor.controller.doCommand = (function(){
    var ref = Editor.controller.doCommand;
    return function(cmd){
        switch(cmd){
            case "cmd_export_results":
                editor.app.saveResults();
                break;
            case "cmd_save_as":
                editor.app.saveNewTestSuite();
                break;
            case "cmd_show_dsl_manager":
                editor.getDSLManager().show();
                break;
            case "cmd_show_locators":
                editor.app.showLocators();
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
        } else if(cmd==="cmd_save_as"){
            return true;
        } else if(cmd==="cmd_show_dsl_manager"){
            return true;
        } else if(cmd==="cmd_show_locators"){
            return true;
        } else{
            return ref.apply(this,arguments);
        }
    };
}());

Editor.controller.supportsCommand = (function(){
    var ref = Editor.controller.supportsCommand;
    return function(cmd){
        if(cmd==="cmd_export_results"){
            return true;
        } else if(cmd==="cmd_save_as"){
            return true;
        } else if(cmd==="cmd_show_dsl_manager"){
            return true;
        }  else if(cmd==="cmd_show_locators"){
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
	this.log.debug("saving results: file=" + filePath + " and results " + rawResults.join("\n\n"));
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

//Extend Application for showing Locators
Application.prototype.showLocators = function() {
    try{
        window.openDialog("chrome://brightest-ide-extensions/content/tools-menu/locator-dialog.xul", "toolsMenu","chrome,modal", LocatorBuilders, editor, this.log);
    } catch(error) {
        this.log.error(error);
    }
}

com.imaginea.DSLManager.prototype.show = function() {
        window.openDialog("chrome://brightest-ide-extensions/content/tools-menu/dsl-manager-dialog.xul", "toolsMenu","chrome,modal", editor, editor.app.log);
}
