/// DOM LEVEL EVENT HANDLERS ///
function onInputDataFileChanged() {
    var theDataFile = $("#input-file")[0].files[0];
    $("#selected-filename").removeClass("text-danger").addClass("text-normal").text(theDataFile.name).attr("data-original-title", theDataFile.name);
    var fr = new FileReader();
    fr.onload = function () {
        try {
            var backendJSON = JSON.parse(fr.result);
            dataModel = new Haystaxs.DataModel(backendJSON);
        }
        catch (ex) {
            $("#selected-filename").addClass("text-danger").text("JSON file expected !");
            throw(ex);
        }

        dataLoadSuccessful();
    };

    fr.readAsText(theDataFile);
}

function onShowAllColumnsChecked() {
    UIElements.showAllColumns = $("#show-all-columns").prop("checked");
    UIElements.populateColumnsGrid(Visualizer.lastClickedNodeData);
}

function onToggleInfoPanel() {
    var toggleButton = $("#toggle-info-pane-size");

    if (toggleButton.hasClass("fa-arrow-left")) {
        toggleButton.removeClass("fa-arrow-left");
        toggleButton.addClass("fa-arrow-right");

        $("#visualizer-panel-container").removeClass("col-lg-9");
        $("#visualizer-panel-container").addClass("col-lg-7");

        $("#info-panel-container").removeClass("col-lg-3");
        $("#info-panel-container").addClass("col-lg-5");

        Visualizer.width = 720;
    } else {
        toggleButton.removeClass("fa-arrow-right");
        toggleButton.addClass("fa-arrow-left");

        $("#visualizer-panel-container").removeClass("col-lg-7");
        $("#visualizer-panel-container").addClass("col-lg-9");

        $("#info-panel-container").removeClass("col-lg-5");
        $("#info-panel-container").addClass("col-lg-3");

        Visualizer.width = 948;
    }

    Visualizer.updateLayout(dataModel);
}

/// UTILITY FUNCTIONS PERTAINING TO HAYSTACK ///
function dataLoadSuccessful() {
    try {
        dataModel.initialize();

        // Update UI based on filters
        $('#schema-select')
            .find('option')
            .remove()
            .end()
            .append(sprintf('<option value="%s">ALL_SCHEMAS</option>', Haystaxs.DataModel.SHOW_ALL_SCHEMAS));

        $.each(dataModel.allSchemas, function (i, item) {
            $('#schema-select').append($('<option>', {
                value: item,
                text: item
            }));
        });

        $("#tables-to-display").val(dataModel.filters.numberOfTablesToDisplay);
    }
    catch (ex) {
        $("#selected-filename").addClass("text-danger").text("JSON format incorrect !");
        throw(ex);
    }

    console.log("Data Load Complete.");

    applyFilters();
}
function setFiltersAndDisplayOptions() {
    dataModel.filters.schemaToDisplay = UIElements.schemaSelectDropdown.val();
    dataModel.filters.numberOfTablesToDisplay = parseInt(UIElements.numberOfTablesToDisplayTextbox.val());
    dataModel.filters.greaterThan = parseFloat(UIElements.displayBasedOnFilterTextbox.val());
    //dataModel.display.accordingTo = UIElements.displayAccordingToRadioButton.val();
    dataModel.display.accordingTo = $("input[name='display-according-to']:checked").val();
}
function applyFilters() {
    setFiltersAndDisplayOptions();
    dataModel.applyFiltersAndDisplayOptions();
    UIElements.refresh();
    Visualizer.updateLayout(dataModel);
}

/// CODE TO RUN ON DOCUMENT LOAD ///
$(function () {
    UIElements.initialize();

    initializeFullScreenOptions();

    initializeToolWindow();

    loadViaAjax('/workload/json/' + $('#workload-id').val(), null, "json", null, null, null, function(result) {
        dataModel = new Haystaxs.DataModel(result);
        dataLoadSuccessful();
    });
});

function initializeToolWindow() {
    $('#info-panel-container').dialog({
        closeOnEscape: false,
        dialogClass: 'above-all-else',
        maxWidth: 500,
        width: 500,
        height: 700,
        maxHeight: 700,
        position: { my: "left top", at: "right top", of: $('#visualizer-panel-container') },
        resizable: false
    }).dialogExtend({
        "closable" : false,
        "collapsable" : true
    });
}

function initializeFullScreenOptions() {
    var clusterVisualPortlet = $('#cluster-visual-portlet-body');
    Visualizer.width = clusterVisualPortlet.width() - 10;
    $('body').on('click', '.portlet > .portlet-title .fullscreen', function(e) {
        if(dataModel) {
            Visualizer.width = clusterVisualPortlet.width() - 10;
            if($(this).hasClass('on')) {
                Visualizer.height = clusterVisualPortlet.height() - 10;
            } else {
                Visualizer.height = visualizerOriginalHeight;
            }
            Visualizer.updateLayout(dataModel);
        }
    });
}

/// GLOBAL VARIABLES ///
var dataModel = undefined;

var visualizerOriginalHeight = 500;