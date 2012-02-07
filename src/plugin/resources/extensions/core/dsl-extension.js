
com.imaginea.DSLManager = function (editor) {
    this.editor = editor;
    this.commandStack = [];
    this.dirty = false;
    this.editor.app.addObserver(
        {
            optionsChanged: function(options) {
                this.load();
            }
        });
};

com.imaginea.DSLManager.prototype = {
    add : function(dslCommand) {
        this.dirty = true;
        this.commandStack.push(dslCommand);
        this.save();
    },
    getAllCommandNames : function() {
        var commandNames = [];
        var i = 0;
        for (; i < this.commandStack.length; i++) {
            var dslCommand = this.commandStack[i];
            commandNames.push(dslCommand.name);
        }
        return commandNames;
    },
    getCommand : function(dslCmdName) {
        var dslCmd = null;
        var i = 0;
        for (; i < this.commandStack.length; i++) {
            var dslCommand = this.commandStack[i];
            if (dslCommand.name === dslCmdName) {
                dslCmd = dslCommand;
                break;
            }
        }
        return dslCmd;
        
    },
    load : function() {
        this.dirty = false;
        var filename = this.editor.getOptions().dslLocationPaths;
        if (filename === null || filename === "") {
            alert("Please configure a location to save the DSL");
        } else {
            var file = FileUtils.getFile(filename);
            if (file.exists) {
                var text = FileUtils.readFile(file);
                try {
                    this.commandStack = JSON.parse(text);
                    if (this.commandStack === null) {
                        this.commandStack = [];
                    }
                } catch(e) {
                    this.commandStack = [];
                }
            }
            
        }

    },
    save : function() {
        if (this.dirty) {
            var filename = this.editor.getOptions().dslLocationPaths;
            if (filename === null || filename === "") {
                alert("Please configure the path of the dsl file in options");
                return;
            } else {
                var file = FileUtils.getFile(filename);
                if (file.exists) {
                    var stream = FileUtils.openFileOutputStream(file);
                    var text = JSON.stringify(this.commandStack, null, 4);
	                var conv = FileUtils.getUnicodeConverter('UTF-8');

	                text = conv.ConvertFromUnicode(text);
	                stream.write(text, text.length);
	                var fin = conv.Finish();
	                if (fin.length > 0) {
		                stream.write(fin, fin.length);
	                }
                    stream.close();
                }
            }
        }
    }
};

com.imaginea.DSLCommand = function(name, commands) {
    this.name = name;
    this.commands = commands;
    this.counter = -1;
    this.nextCommand = function() {
        this.counter++;
        return this.commands[this.counter];
    };
    this.isAtLastCommand = function() {
        return this.counter === (this.commands.length - 1 );
    };
};

