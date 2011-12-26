//Overriding Debugger
Debugger = (function(){
    var prop,ref = Debugger;
    var newDebugger = function(){
        ref.apply(this,arguments);
        //Extend IDETestLoop
        //Since IDETestLoop is loaded as part of 
        //selenimu-runner which is loaded by Debugger
        //we need to take care of ensuring that we override 
        //the class only after it comes into existence
        var self = this;
        var nextDSLSubCommand = null;
        // the log window is not yet right, so use the error console
        var consoleLog = new Log("Debugger-Extension");
        this.init = (function(){
            var initRef = self.init;
            return function(){
                initRef.apply(self,arguments);
                // override only once
                if (!self.runner.TestLoop.prototype._valAdded) {
                    self.runner.TestLoop.prototype.resume = (function(){
                        var ref = self.runner.TestLoop.prototype.resume;

                        return function() {
                            if (this.currentCommand && this.currentCommand.commands) {
                                consoleLog.info("executing DSL Command ->" + this.currentCommand.name);
                                try{
                                    //we need the methods now, so convert the json to a proper object
                                    if (this.currentDSLCommand) {
                                        consoleLog.debug("Set the dsl command already");
                                    } else {
                                        this.currentDSLCommand = new com.imaginea.DSLCommand(this.currentCommand.name, this.currentCommand.commands);
                                    }
                                    this._executeNextDSLCommand();
                                } catch(e) {
                                    this.currentCommand = originalCommand;
                                    if (!this._handleCommandError(e)) {
                                        this.testComplete();
                                    } else {
                                        this.continueTest();
                                    }
                                }
                            } else {
                                ref.apply(this,arguments);
                            }
                            
                        };
                    }());
                    self.runner.IDETestLoop.prototype.resume = self.runner.TestLoop.prototype.resume;

                   self.runner.TestLoop.prototype._executeCurrentCommand = (function(){
                        var ref = self.runner.TestLoop.prototype._executeCurrentCommand;
                        return function(){
                            //AN : time delta 
                            var start = new Date().getTime();
                            ref.apply(this,arguments);
                        };
                    }());
                    self.runner.IDETestLoop.prototype._executeCurrentCommand = self.runner.TestLoop.prototype._executeCurrentCommand;
                    self.runner.IDETestLoop.prototype.initialize = (function(){
                        var ref = self.runner.IDETestLoop.prototype.initialize;
                        return function(){
                            ref.apply(this,arguments);
                            this.resultHistory = new ResultHistory(self.runner.testCase);
                        };
                    }());
                    //override commandError
                    self.runner.IDETestLoop.prototype.commandError = (function(){
                        var ref = self.runner.IDETestLoop.prototype.commandError;
                        return function(errorMessage){
                            ref.apply(this,arguments);
                            this.resultHistory.setFailure(self.runner.testCase, errorMessage);
                        };
                    }());
                    self.runner.IDETestLoop.prototype.commandComplete = (function(){
                        var ref = self.runner.IDETestLoop.prototype.commandComplete;
                        return function(result){
                            ref.apply(this,arguments);
                            //AN
                            this.resultHistory.addTestResult(result);            
                        };
                    }());
                    self.runner.TestLoop.prototype._valAdded = true;   
                    ResultHistory.prototype.initialize = function(){
                        editor.getResultHistoryStack().push(this);
                    };

                    ResultHistory.prototype.addTestResult = function(result){  
                        this.testResultsStack.push(self.runner.testCase.debugContext.debugIndex + "~" +  result.executionTime);    
                    };


                    consoleLog.info("Enhancing Debugger ");
                }//end of if val added 
            };
        }());
    };
    for(prop in ref){
        //if(typeof ref[prop]!=="function"){
        newDebugger[prop]=ref[prop];
        //}
    }
    newDebugger.prototype=ref.prototype;
    return newDebugger;
}());


//Enhancing TestCase to include attributes needed by BrighTest
TestCase.prototype.getDescription = function() {
    return this._description;
};

TestCase.prototype.getExpectedResult = function() {
    return this._expectedResult;
};

TestCase.prototype.getTags = function() {
    return this._tags;
};

TestCase.prototype.setAdditionalParams = function(description, expectedResult, tags) {
    this._description = description;
    this._expectedResult = expectedResult;
    this._tags = tags;
};

