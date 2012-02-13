var locatorDialog;

function LocatorDialog() {
    this.locatorListUIElement = document.getElementById('locatorBuilder-list');
    this.LocatorBuilders = window.arguments[0];
    this.editor = window.arguments[1];
    this.log = window.arguments[2];
    this.currentUIModel = [];
    this.load();
}

LocatorDialog.prototype = { 
    changeOrder: function() {
        this.LocatorBuilders.order = this.currentUIModel.slice();
    },
    load: function() {
        try{
            var locatorIdentifier;
            var locatorBuilderOrder = this.LocatorBuilders.order; 

            if (!(locatorBuilderOrder)) {
                // if there was no order defined before use the builder map to construct the same
                for (locatorIdentifier in this.LocatorBuilders.builderMap) {
                    this.currentUIModel.push(locatorIdentifier);
                }
            } else {
                this.currentUIModel = locatorBuilderOrder.slice();
            }
            this.refreshFromModel();
        } catch(error) {
            this.log.error(error);
        }
    },
    moveSelectedElements: function(upwards) {
        var selectedItem;
        var selectedItemPosition;
        var listBox = this.locatorListUIElement;
        var selectedItems = listBox.selectedItems;
        var newPosition = -1;
        var tmpValue;

        if ( selectedItems && selectedItems.length > 0) {
            selectedItem = selectedItems[0];
            selectedItemPosition = listBox.getIndexOfItem(selectedItem)
            if (upwards) {
                // if selected elemenet is the first element, ignore
                if (selectedItemPosition != 0) {
                    newPosition = selectedItemPosition - 1;
                }
            } else {
                if ((selectedItemPosition + 1) < this.locatorListUIElement.itemCount) {
                    newPosition = selectedItemPosition + 1;
                }
            }
            
            if (newPosition > -1) {
                // change the values in model
                tmpValue = this.currentUIModel[newPosition];
                this.currentUIModel[newPosition]  = this.currentUIModel[selectedItemPosition];
                this.currentUIModel[selectedItemPosition] = tmpValue;

                // change the values in UI
                listBox.removeItemAt(selectedItemPosition);
                listBox.insertItemAt(newPosition, this.currentUIModel[newPosition], this.currentUIModel[newPosition]);

                // revert to the selection
                listBox.selectItem(listBox.getItemAtIndex(newPosition));
            }
        } else {
            alert("Please select the item you want to move");
        }
    },
    refreshFromModel: function() {
        var counter = 0;
        var currrentItem;

        for (counter = 0; counter < this.currentUIModel.length; counter++) {
            currentItem =this.currentUIModel[counter];
            this.locatorListUIElement.appendItem(currentItem, currentItem);
        }
    }
}




LocatorDialog.acceptChanges = function() {
    locatorDialog.changeOrder();
}

LocatorDialog.cancelChanges = function() {
    return true;
}

LocatorDialog.create = function() {
    locatorDialog = new LocatorDialog();
}

LocatorDialog.moveUp = function() {
    try{
        locatorDialog.moveSelectedElements(true);
    }catch(error) {
        alert(error);
    }
}

LocatorDialog.moveDown = function() {
    locatorDialog.moveSelectedElements(false);
}
