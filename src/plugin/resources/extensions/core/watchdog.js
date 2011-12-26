function WatchDog(selDebugger) {
	this.log = new Log("WatchDog");
	this.selDebugger = selDebugger;
	this.id = new Date().getTime();
	this.runid = this.id;
	var self = this;
    this.selDebugger.addObserver({
        stateUpdated: function(state) {
			if (state === Debugger.PAUSED) {
                self.id = 0;
            } if (state === Debugger.STOPPED) {
                self.id = 0;
            }
        }
    });
    
	if (this.selDebugger.editor.app.options.testtimeout) {
		var testTimeoutValue = self.selDebugger.editor.app.options.testtimeout * 60 * 1000;
	    setTimeout(function() {
	        self.log.error("timeout happened current values are "+self.selDebugger.state+" " +createTime+" " + self.time); 
	        if (self.selDebugger.state === Debugger.PLAYING && self.id === self.runid) {
                self.selDebugger.doStop();
	        } 
	    }, testTimeoutValue);
	}
    
}

