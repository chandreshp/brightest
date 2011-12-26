//wrap copied from prototype, heavily uses
Function.prototype.wrap = function(wrapper) {
  var __method = this;
  return function() {
      return wrapper.apply(this,[__method.bind(this)].concat(Array.prototype.slice.call(arguments)));
  };
};

//namespace registration function
function registerNS(ns) {
    var nsParts = ns.split(".");
    var root = window;
    var i = 0;
    for( ; i < nsParts.length; i++)
    {
        if(typeof root[nsParts[i]] === "undefined") {
            root[nsParts[i]] = {};
        }
        root = root[nsParts[i]];
    }
}

registerNS("com.imaginea");

com.imaginea.Util = {
    addToEnum: function(clazz, enumName, names) {
        var i,map=clazz[enumName],max=0,maxLength;
        for(i in map){
            max=i>max?i:max;
        }
        maxLength = names.length;
        for (i = 1; i <= maxLength; i++) {
            clazz[names[i-1]] = i+max;
            map[i+max] = names[i-1];
        }
    }
};

//AN: additions for Brightest
var FormatterAddons = {
	cl : null,
	
    getTestSuite : function(filePath) {
        try{
            var aClass = java.lang.Class.forName("com.pramati.brightest.neo.TestRunner", true, FormatterAddons.cl);
            var myObj = aClass.newInstance();
            myObj.processFile(filePath);
            while (myObj.isProcessed() === false) {
                myObj.isProcessed();//can't have an empty block!
            }
            var jsSnippet = myObj.getJavascriptConstructForTestSuite();
            var testArtifact = eval("("+jsSnippet+")");
            return testArtifact;
        }catch(e) {
            alert(e);
        }
    },
    writeCommands : function(filePath, rawCommands) {
	    try{
            var aClass = java.lang.Class.forName("com.pramati.brightest.neo.TestRunner", true, FormatterAddons.cl);
            var myObj = aClass.newInstance();
            myObj.writeFile(filePath, rawCommands);
	    }catch(e) {
		    alert(e);
	    }
    },
    saveResults : function(filePath, rawResults) {
	    try{
            var aClass = java.lang.Class.forName("com.pramati.brightest.neo.TestRunner", true, FormatterAddons.cl);
            var myObj = aClass.newInstance();
            myObj.writeResults(filePath, rawResults);
	    }catch(e) {
		    alert(e);
	    }
    },
    // This function will be called to give the necessary privileges to your JAR files
    policyAdd : function (loader, urls) {
        try {
            //If have trouble with the policy try changing it to 
            //edu.mit.simile.javaFirefoxExtensionUtils.URLSetPolicy        
            var str = 'edu.mit.simile.javaFirefoxExtensionUtils.URLSetPolicy';
            var policyClass = java.lang.Class.forName(
                str,
                true,
                loader
            );
            var j,policy = policyClass.newInstance();
            policy.setOuterPolicy(java.security.Policy.getPolicy());
            java.security.Policy.setPolicy(policy);
            policy.addPermission(new java.security.AllPermission());
            for (j=0; j < urls.length; j++) {
                policy.addURL(urls[j]);
            }
        }catch(e) {
            alert(e+'::'+e.lineNumber);
        }
    }
};

// Adds ajax command callbacks for recording
Recorder.register = Recorder.register.wrap(
    function(originalRegister, observer, window){
        window = window.wrappedJSObject?window.wrappedJSObject:window;
        var retVal = originalRegister(observer,window);
        // make sure we define things only once
        if(!window.XMLHttpRequest.brighTestOverride){
            window.XMLHttpRequest.prototype.open = (function(){
                var ref2 = window.XMLHttpRequest.prototype.open;
                window.XMLHttpRequest.brighTestOverride = true;
                return function(){
                    if(Application.prototype.threadSynch === Application.prototype.threadMaster){
                        Application.prototype.ajaxCallCount = Application.prototype.ajaxCallCount + 1;
                        this.onreadystatechange = function(){
                            var ref = this.onreadystatechange;
                            return function(ev){
                                if(4===this.readyState && Application.prototype.playbackMode===true){
                                    Application.prototype.ajaxCallCount = Application.prototype.ajaxCallCount - 1;                        
                                }
                                originalRegister.handleEvent(ev);
                            };
                        }.apply(this);
                    }
                    ref2.apply(this,arguments);
                };
            }());
        }
        return retVal;
    });



Editor.prototype.appendForAjaxResponse = function(window) {
    this.log.debug("appendForAjaxResponse");
	var lastCommandIndex = this.lastCommandIndex;
	if (lastCommandIndex === null || lastCommandIndex === undefined || lastCommandIndex >= this.getTestCase().commands.length) {
		return;
	}
	this.lastCommandIndex = null;
	var lastCommand = this.getTestCase().commands[lastCommandIndex];
	if (lastCommand.type === 'command' && 
		!lastCommand.command.match(/^(assert|verify|store)/)) {
		lastCommand.command = lastCommand.command + "ForAjaxResponse";
		this.view.rowUpdated(lastCommandIndex);
        this.addCommand("waitForAjaxResponse", this.getOptions().timeout, null, this.lastWindow);
	}
    this.clearLastCommand();
};

SidebarEditor.prototype.appendForAjaxResponse = Editor.prototype.appendForAjaxResponse;
StandaloneEditor.prototype.appendForAjaxResponse = Editor.prototype.appendForAjaxResponse;

Editor.prototype.addCommand = Editor.prototype.addCommand.wrap(
    function(originalAdd, command,target,value,window,insertBeforeLastCommand) {
        Application.prototype.threadMaster = Math.random();
        Application.prototype.threadSynch = Application.prototype.threadMaster;
        Application.prototype.ajaxCallCount=0;
        setTimeout(function(){
            Application.prototype.threadSynch = 0;
        },0);
        var self = this;
        setTimeout(function(){
            if(Application.prototype.ajaxCallCount){
                self.appendForAjaxResponse();
            }
        },0);
        return originalAdd(command,target,value,window,insertBeforeLastCommand);
    }
);

SidebarEditor.prototype.addCommand = Editor.prototype.addCommand;
StandaloneEditor.prototype.addCommand = Editor.prototype.addCommand;

//Extend Editor
Editor.prototype.getResultHistoryStack=function(){
    if(!this.resultHistoryStack){
        this.resultHistoryStack = [];
    }
    return this.resultHistoryStack;
};
SidebarEditor.prototype.getResultHistoryStack = Editor.prototype.getResultHistoryStack;
StandaloneEditor.prototype.getResultHistoryStack = Editor.prototype.getResultHistoryStack;

Editor.prototype.getDSLManager=function(){
    if(!this.dslManager){
        this.dslManager = new com.imaginea.DSLManager(this);
        this.dslManager.load();
    }
    return this.dslManager;
};
SidebarEditor.prototype.getDSLManager = Editor.prototype.getDSLManager;
StandaloneEditor.prototype.getDSLManager = Editor.prototype.getDSLManager;

//Defining a new class ResultHistory to mantain history
ResultHistory = function ResultHistory(testCase) {
    this.testResultsStack =[];
    if (testCase.file) {
        this._testCaseFile = testCase.file.path.toString();
    } else {
        this._testCaseFile = testCase.title;
    }
    this.initialize();
};
ResultHistory.prototype = {
    setFailure : function(testCase, errorMessage) {
        this._failureIndex = testCase.debugContext.debugIndex;
        this._errorMessage = errorMessage;
    },
    getReport : function() {
        return this._testCaseFile + "~" + this._failureIndex + "~" + this._errorMessage;
    },
    isFailed  : function() {
        return (this._errorMessage && this._errorMessage.length > 0);
    },
    getTestResult : function(){
        return this.testResultsStack; 
    }
};

// FIXME no reason why this should not be done lazily
//initialize Java - get the path to JARs etc..
(function(){
    var extensionInfo = "brighTest@imaginea.com";

    // Get path to the java folder where the JAR files are in
    var extensionPath = null;
    var extensionFileUrl = "";
    function addPolicies() {
        var jarArray = ["javaFirefoxExtensionUtils.jar", "test-runner.jar","poi-3.2.jar","junit-4.8.2.jar" ];
        var i,urlArray = [];
        for (i = 0; i < jarArray.length; i++) {
            urlArray[i] = new java.net.URL(extensionFileUrl + jarArray[i]);
        }
        FormatterAddons.cl = java.net.URLClassLoader.newInstance(urlArray);
        
        //Set security policies using the above policyAdd() function
        FormatterAddons.policyAdd(FormatterAddons.cl, urlArray);
    }
    try{
        extensionPath = Components.classes["@mozilla.org/extensions/manager;1"].getService(Components.interfaces.nsIExtensionManager).getInstallLocation(extensionInfo).getItemLocation(extensionInfo);
        extensionFileUrl = "file:///" + extensionPath.path.replace(/\\/g,"/") + "/java/";
addPolicies();
}catch(e) {
Components.utils.import("resource://gre/modules/AddonManager.jsm"); 
                                                                   AddonManager.getAddonByID(extensionInfo, function(addon) {
                                                                       extensionPath = addon.getResourceURI("").QueryInterface(Components.interfaces.nsIFileURL).file;
                                                                       extensionFileUrl = "file:///" + extensionPath.path.replace(/\\/g,"/") + "/java/";
//extensionFileUrl = "jar:file:///" + extensionPath.path.replace(/\\/g,"/").replace(/@/g,"%40").replace(/ /g,"%20") + "!/java/";
                                                                                                                                  // alert(extensionFileUrl);
                                                                                                                                  addPolicies();
                                                                                                                                 });
                                                                                            } 
                                                                  }());

    //override the way file open works
    Editor.controller.doCommand = (function(){
        var ref = Editor.controller.doCommand;
        return function(cmd){
            if(cmd==="cmd_open"){
                editor.app._loadTestArtifact();
            }else{
                ref.apply(this,arguments);
            }
        };
    }());

    Application.prototype._loadTestArtifact = function(file, testCaseHandler) {
        this.log.debug("loadTestCase");
        try {
            var testArtifact = null;
            if (file) {
                testArtifact = this.getCurrentFormat().loadFile(file, false);
            } else {
                testArtifact = this.getCurrentFormat().load();
            }
            if (testArtifact instanceof TestCase) {
                if (testCaseHandler){
                    testCaseHandler(testArtifact);
                }
                this.setTestCase(testArtifact);
                this.recentTestCases.add(testArtifact.file.path);
                return true;
            }else if(testArtifact instanceof TestSuite){
                this.setTestSuite(testArtifact);
                this.addRecentTestSuite(testArtifact);
                return true;
            }
            return false;
        } catch (error) {
            alert("error loading test case: " + error);
            return false;
        }
    };

    //TODO - TRY TO AVOID OVERRIDING THE FOLLOWING FUNCTIONS
    Format.prototype.loadFile = function(file, isURL) {
	    this.log.debug("start loading: file=" + file);
        
        var filePath = file.path?file.path.toString():"";
        var testArtifact;
        
	    //AN: hack for xls files
        if (filePath.indexOf(".xls") > 0) {
            var commands = [];
		    this.log.debug("using java to read =" + filePath);
            //xls files always contain a suite, even if it contains a single test case
		    var testSuite = FormatterAddons.getTestSuite(filePath);
            testArtifact = testSuite.tests[0].content;
            testArtifact.file = testSuite.file;
        } else {
            var sis, text;
            if (isURL) {
                sis = FileUtils.openURLInputStream(file);
            } else {
                sis = FileUtils.openFileInputStream(file);
            }
            testArtifact = new TestCase();
	        text = this.getUnicodeConverter().ConvertToUnicode(sis.read(sis.available()));
	        this.getFormatter().parse(testArtifact, text);	
            sis.close();
            testArtifact.recordModifiedInCommands();
            testArtifact.file = file;
            if (!isURL) {
                testArtifact.lastModifiedTime = file.lastModifiedTime;
            }
        }
	    
        return testArtifact;
    };

    Format.prototype.saveAs = function(testCase, filename, exportTest) {
	    //log.debug("saveAs: filename=" + filename);
	    try {
		    var file = null;
		    if (filename === null || filename === undefined) {
                file = showFilePicker(window, "Save as...",
                                      Components.interfaces.nsIFilePicker.modeSave,
                                      exportTest ? Format.TEST_CASE_EXPORT_DIRECTORY_PREF : Format.TEST_CASE_DIRECTORY_PREF,
                                      function(fp) { return fp.file; });
		    } else {
			    file = FileUtils.getFile(filename);
		    }
		    if (file !== null && file !== undefined) {
			    testCase.file = file;
			    var filePath = file.path?file.path.toString():"";
			    
			    //AN: hack for xls files
			    if (filePath.indexOf(".xls") > 0 || (filePath.indexOf(".html") > 0  && exportTest)) {
				    this.log.debug("using java to write =" + filePath);
				    var rawCommands = [];
				    var commands = testCase.commands;
				    var i,command = null; 
				    for (i = 0; i < commands.length; i++) {
					    command = commands[i];
					    rawCommands[i] = command.command + "~"+command.target + "~"+command.value; 
				    }
				    FormatterAddons.writeCommands(filePath, rawCommands);
			    } else {
				    // save the directory so we can continue to load/save files from the current suite?
				    var outputStream = Components.classes["@mozilla.org/network/file-output-stream;1"].createInstance( Components.interfaces.nsIFileOutputStream);
				    outputStream.init(file, 0x02 | 0x08 | 0x20, 420/*0644 in octal*/, 0);
				    var converter = this.getUnicodeConverter();
				    var text = converter.ConvertFromUnicode(this.getFormatter().format(testCase, testCase.getTitle(), '', true));
				    outputStream.write(text, text.length);
				    var fin = converter.Finish();
				    if (fin.length > 0) {
					    outputStream.write(fin, fin.length);
				    }
				    outputStream.close();
			    }
			    this.log.info("saved " + file.path);
			    testCase.lastModifiedTime = file.lastModifiedTime;
			    testCase.clearModified();
			    
			    return true;
		    } else {
			    return false;
		    }
	    } catch (err) {
		    alert("error: " + err);
		    return false;
	    }
    };

    //To show the testcase details on clicking/selecting the test case
    SuiteTreeView.prototype.initialize = (function(){
        var ref = SuiteTreeView.prototype.initialize;
        return function(editor,tree){
            ref.apply(this,arguments);
            var self=this;
            tree.addEventListener("click", function(event) {
                var testCase = self.getSelectedTestCase();
                if (testCase){editor.app.showTestCaseFromSuite(testCase);}
            }, false);
        };
    }());

    SeleniumIDE.Preferences.load = (function(){
        var ref = SeleniumIDE.Preferences.load;
        return function(){
            var options = ref.apply(this,arguments);
            if(options.userExtensionsURL){
                options.userExtensionsURL = options.userExtensionsURL + ",chrome://brightest-ide-extensions/content/core/user-extensions.js";
            }else{
                options.userExtensionsURL = "chrome://brightest-ide-extensions/content/core/user-extensions.js";
            }
            return options;
        };
    }());

