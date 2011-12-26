//Overriding the three editor functions that define a new command 
Editor.controller.doCommand = (function(){
    var ref = Editor.controller.doCommand;
    return function(cmd){
        if(cmd==="cmd_selenium_stop"){
            editor.selDebugger.doStop();
        }else{
            ref.apply(this,arguments);
        }
    };
}());

Editor.controller.isCommandEnabled = (function(){
    var ref = Editor.controller.isCommandEnabled;
    return function(cmd){
        if(cmd==="cmd_selenium_stop"){
            alert("about to stop selenium debugger");
            return editor.app.isPlayable() && (editor.selDebugger.state === Debugger.PLAYING || editor.selDebugger.state === Debugger.PAUSED);
        }else{
            return ref.apply(this,arguments);
        }
    };
}());

Editor.controller.supportCommand = (function(){
    var ref = Editor.controller.supportCommand;
    return function(cmd){
        if(cmd==="cmd_selenium_stop"){
            return true;
        }else{
            return ref.apply(this,arguments);
        }
    };
}());


//Overriding Debugger
// Debugger = (function(){
//     var ref = Debugger;
//     var prop,newDebugger = function(){
//         ref.apply(this,arguments);
//         var self = this;
//         this.init = (function(){
//             var initRef = self.init;
//             return function(){
//                 initRef.apply(this,arguments);
//                 this.runner.shouldAbortCurrentCommand = (function(){
//                     var ref = self.runner.shouldAbortCurrentCommand;
//                     return function(){
//                         var shouldAbort = ref.apply(this,arguments);
//                         if(shouldAbort){
//                             return true;
//                         }
//                         if(self.state === Debugger.STOP_REQUESTED) {
//                             self.log.debug("Stopping the current test execution");
//                             this.aborted = true;
//                             this.requiresCallBack = null;
//                             self.setState(Debugger.STOPPED);
//                             return true;  
//                         }
//                         return false;
//                     };
//                 }());
//             };
//         }());
//     };
//     for(prop in ref){
//         //if(typeof ref[prop]!=="function"){
//         newDebugger[prop]=ref[prop];
//         //}
//     }
//     newDebugger.prototype=ref.prototype;
//     return newDebugger;
// }());

// Debugger.prototype.start = (function(complete, useLastWindow) {
//var ref = Debugger.prototype.start;
//   var watchDog = new WatchDog(this);
//   ref.apply(this, arguments); 
//}());


// //Extend Debugger.STATES enum definition
// addToEnum(Debugger, "STATES", ["STOP_REQUESTED"]);
