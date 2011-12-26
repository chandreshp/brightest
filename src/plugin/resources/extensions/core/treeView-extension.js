//Enhancing TreeView to handle DSL
TreeView.prototype.addToDSL = function() {
    if (!this.treebox.focused){return;}
    var count = this.selection.getRangeCount();
    if (count > 0) {
        var dslPathName = this.editor.getOptions().dslLocationPaths;
        if (dslPathName && dslPathName.length > 0) {
            this.log.debug("About to add to "+dslPathName);
        } else {
            alert("Please choose the DSL path");
            return;
        }


        var copyCommands = [];
        var deleteRanges = [];
        var currentIndex = this.tree.currentIndex, i=0;
        for (; i < count; i++) {
            var start = {};
            var end = {};
            this.selection.getRangeAt(i, start, end);
            var v, deleteCommands = {start: start.value, commands:[]};
            for (v = start.value; v <= end.value; v++) {
                var command = this.getCommand(v);
                //we can do a empty selection, so return in this case
                if (!(command.command && command.command.length > 0)) {
                    alert("At least one normal command should have been selected");
                    return;
                }
                copyCommands.push(command.createCopy());
                deleteCommands.commands.push(command);
            }
            deleteRanges.push(deleteCommands);
        }

        var prompts = Components.classes["@mozilla.org/embedcomp/prompt-service;1"]
            .getService(Components.interfaces.nsIPromptService);
        var check = {value: false};                  // default the checkbox to false
        var input = {value: "command"};                  // default the edit field to Bob
        var result = prompts.prompt(null, "DSL Command Name", "Enter the Command", input, null, check);
        if (result) {
            var tokens = input.value.split("|");
            this.executeAction(new TreeView.DeleteCommandAction(this, deleteRanges));
            var dslCommand = new com.imaginea.DSLCommand(tokens[0],copyCommands);
            this.editor.getDSLManager().add(dslCommand);
            this.insertAt(this.tree.currentIndex, new Command(tokens[0],tokens[1],tokens[2]));
        }       
    }
};

TreeView.prototype.replaceWithDSL = function() {
    if (!this.treebox.focused){ return;}
    var count = this.selection.getRangeCount();
    if (count > 0) {
        var copyCommands = [];
        var deleteRanges = [];
        var i = 0, v, currentIndex = this.tree.currentIndex;
        for (; i < count; i++) {
            var start = {};
            var end = {};
            this.selection.getRangeAt(i, start, end);
            var deleteCommands = {start: start.value, commands:[]};
            for (v = start.value; v <= end.value; v++) {
                var command = this.getCommand(v);
                copyCommands.push(command.createCopy());
                deleteCommands.commands.push(command);
            }
            deleteRanges.push(deleteCommands);
        }

        var prompts = Components.classes["@mozilla.org/embedcomp/prompt-service;1"]
            .getService(Components.interfaces.nsIPromptService);
        var dslCmds = this.editor.getDSLManager().getAllCommandNames(); //["login","navigateTo", "createQuote"];
        var selectedDsl = {value:0};              // default the edit field to Bob
        var result = prompts.select(null, "DSL Command", "Select DSL Command", dslCmds.length, dslCmds, selectedDsl);
        if (result) {
            var tokens = dslCmds[selectedDsl.value].split("|");
            this.executeAction(new TreeView.DeleteCommandAction(this, deleteRanges));
            this.insertAt(this.tree.currentIndex, new Command(tokens[0],tokens[1],tokens[2]));
        }       
    }
};

