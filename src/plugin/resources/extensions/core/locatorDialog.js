var locatorDialog;

function LocatorDialog() {
    this.locatorListUIElement = document.getElementById('locatorBuilder-list');
    this.load();
}


LocatorDialog.acceptChanges = function() {
    locatorDialog.changeOrder();
}

LocatorDialog.cancel = function() {
    locatorDialog.reset();
    locatorDialog = null;
    return true;
}

LocatorDialog.create = function() {
    locatorDialog = new LocatorDialog();
}

LocatorDilaog.moveUp = function() {
    if (locatorListUIElement.selectedItems && locatorListUIElement.selectedItems.length > 0) {
    } else {
        alert("Please select the item you want to move");
    }
}

LocatorDialog.prototype = {
    changeOrder: function() {
    },
    reset: function() {
    },
    load: function() {
        var locatorIdentifier = "works";
        this.locatorListUIElement.appendItem(locatorIdentifier, locatorIdentifier);
        for (locatorIdentifier in LocatorBuilders.builderMap) {
            this.locatorListUIElement.appendItem(locatorIdentifier, locatorIdentifier);
        }
    }
}

