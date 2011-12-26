SuiteTreeView.prototype.editProperties = function() {
    var testCase = this.getSelectedTestCase();
    var self = this;
    if (testCase) {
        window.openDialog('chrome://brightest-ide-extensions/content/testcase-properties-dialog/testcase-properties-dialog.xul', 'testCaseProperties', 'chrome,modal', testCase, function() {
                self.treebox.invalidateRow(self.currentTestCaseIndex);
            });
    }
};