var dslManagerDialog;

function DSLManagerDialog() {
    this.editor = window.arguments[0];
    this.dslManager = this.editor.getDSLManager();
    this.load();
}

DSLManagerDialog.prototype = {
    save: function() {
    },
    load:function() {
    }
}

DSLManagerDialog.acceptChanges = function() {
    dslManagerDialog.save();
}

DSLManagerDialog.cancelChanges = function() {
    return true;
}

DSLManagerDialog.create = function() {
    dslManagerDialog = new DSLManagerDialog();
}

