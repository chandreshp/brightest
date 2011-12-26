Editor.controller.doCommand = (function(){
    var ref = Editor.controller.doCommand;
    return function(cmd){
        switch(cmd){
            case "cmd_analyze_tests":
                editor.app.analyzeTests();
                break;
            default:
                ref.apply(this,arguments);
        }
    };
}());

Editor.controller.isCommandEnabled = (function(){
    var ref = Editor.controller.isCommandEnabled;
    return function(cmd){
        if(cmd==="cmd_analyze_tests"){
            return true;
        }else{
            return ref.apply(this,arguments);
        }
    };
}());

Editor.controller.supportCommand = (function(){
    var ref = Editor.controller.supportCommand;
    return function(cmd){
        if(cmd==="cmd_analyze_tests"){
            return true;
        }else{
            return ref.apply(this,arguments);
        }
    };
}());

//Extend Application
Application.prototype.analyzeTests = function() {
    this.log.debug("analyzeTests");
    this.getCurrentFormat().analyzeTests(this.getTestCase());
    this.notify("analysisStarted");
};

//Extend Format
Format.prototype.analyzeTests = function(testCase) {
    var reportPanel = editor.document.getElementById("brightestAnalysisReport");
    var analysisResultsDiv = reportPanel.contentDocument.getElementById("analysisResults");
    var analysisResult = "<h2> Analyzed </h2> " + testCase.file.path + " <br/> <h3>Suggestions</h3>";
    analysisResult += "<ul>";
    analysisResult += "<li>" + "Mock Report for now" + "</li>";
    analysisResult += "</ul>";

    analysisResultsDiv.innerHTML = analysisResult;
    editor.document.getElementById("mainDisplayDeck").selectedIndex = 1;
    //editor.toggleView(editor.sourceView);
};
