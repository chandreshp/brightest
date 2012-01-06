Selenium.prototype.doClickForAjaxResponse = function(locator, text) {

    var window = selenium.browserbot.getCurrentWindow();
    window = window.wrappedJSObject?window.wrappedJSObject:window;

    // decorate to intercept the ajax requests
    if(!window.XMLHttpRequest.brighTestOverride) {
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
                                    Application.prototype.addedAjaxResponse += this.responseText;
                                }
                                if (ref) {
                                    ref.handleEvent(ev);
                                }
                            };
                        }.apply(this);
                    }
                    ref2.apply(this,arguments);
                };
            }());
    };

    Application.prototype.threadMaster = Math.random();
    Application.prototype.threadSynch = Application.prototype.threadMaster;
    Application.prototype.ajaxCallCount=0;
    Application.prototype.playbackMode=true;
   Application.prototype.addedAjaxResponse = "";
    Application.prototype.responseCounter = 0;

    setTimeout(function(){
        Application.prototype.threadSynch = 0;
    },0);
    setTimeout(function(){
        if(Application.prototype.ajaxCallCount){
            self.appendForAjaxResponse();
        }
    },0);


    // All locator-strategies are automatically handled by "findElement"
    var element = this.page().findElement(locator);

    var options = { // defaults
    clientX: 0, clientY: 0, button: 0,
    ctrlKey: false, altKey: false, shiftKey: false,
    metaKey: false, bubbles: true, cancelable: true
    // create event object:
    }, event = element.ownerDocument.createEvent("MouseEvents");

    // initialize the event object
    event.initMouseEvent("click", options.bubbles, options.cancelable,
                       element.ownerDocument.defaultView,  options.button,
                       options.clientX, options.clientY, options.clientX,
                       options.clientY, options.ctrlKey, options.altKey,
                       options.shiftKey, options.metaKey, options.button,
                       element);
    // dispatch!
    element.dispatchEvent(event);
    
    /*while(){
        //wait
    }*/
    //var self = this;
    //setTimeout(function(){
        //this.doWaitForCondition("if(Application.prototype.ajaxCallCount===0)Application.prototype.playbackMode=false;Application.prototype.ajaxCallCount===0","10000");
    //},0);
    //Application.prototype.playbackMode=false;
};

Selenium.prototype.doWaitForAjaxResponse = function(timeout, text) {
    /**
   * @param timeout a timeout in milliseconds, after which this command will return with an error
   * @param text the text to search for in the response
   */
    var self = this;
    var start = new Date();
    var condition = function () {
        var conditionMet = false;

        if (text) {
            if (Application.prototype.addedAjaxResponse) { 
                conditionMet =  (Application.prototype.addedAjaxResponse.indexOf(text) != -1);
            }
        } else {
            conditionMet = (Application.prototype.ajaxCallCount === 0);
        }
        
        if (conditionMet) {
            Application.prototype.ajaxCallCount = 0;
        }
        return conditionMet;
    };

    return Selenium.decorateFunctionWithTimeout(condition, timeout);
};


Selenium.prototype.doTypeRepeated = function(locator, text) {
    // All locator-strategies are automatically handled by "findElement"
    var element = this.page().findElement(locator);

    // Create the text to type
    var valueToType = text + text;

    // Replace the element text with the new text
    this.page().replaceText(element, valueToType);
};

Selenium.prototype.doTypeForAjaxResponse = function(locator, text) {
    Application.prototype.threadMaster = Math.random();
    Application.prototype.threadSynch = Application.prototype.threadMaster;
    Application.prototype.ajaxCallCount=0;
    Application.prototype.playbackMode=true;
    setTimeout(function(){
        Application.prototype.threadSynch = 0;
    },0);
    setTimeout(function(){
        if(Application.prototype.ajaxCallCount){
            self.appendForAjaxResponse();
        }
    },0);

   // All locator-strategies are automatically handled by "findElement"
    var element = this.page().findElement(locator);
    // Replace the element text with the new text
    this.page().replaceText(element, text);
};

LocatorBuilders.order = ['id', 'link', 'name', 'dom:name', 'xpath:link', 'xpath:img', 'xpath:attributes', 'xpath:href', 'dom:index', 'xpath:position'];