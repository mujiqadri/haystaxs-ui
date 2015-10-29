/**
 * Created by Adnan on 8/26/2015.
 */
var UIElements = {
    schemaSelectDropdown: null,
    numberOfTablesToDisplayTextbox: null,
    displayBasedOnFilterTextbox: null,
    displayAccordingToRadioButton: null,
    displayAccordingToRadioButton_Checked: null,

    tableInfoTabs: null,
    dkInfoPanel: null,
    columnAdditionalInfoPanel: null,
    joinAdditionalInfoPanel: null,

    selectedTableLabel: null,
    selectedColumnLabel: null,

    tableSelectDropdown: null,

    summaryGrid: null,
    dkInfoGrid: null,
    columnsGrid: null,
    columnAdditonalInfoGrid: null,
    joinsGrid: null,
    joinAdditionalInfoGrid: null,

    showAllColumns: true,

    numberOfColumnsInTable: 0,
    numberOfColumnsCurrentlyShowing: 0,

    clearAllGrids: function () {
        UIElements.selectedTableLabel.text("");
        UIElements.summaryGrid.bootgrid("clear");
        UIElements.dkInfoGrid.bootgrid("clear");
        UIElements.columnsGrid.bootgrid("clear");
        UIElements.joinsGrid.bootgrid("clear");

        //UIElements.selectedColumnLabel.text("");
        //UIElements.columnAdditonalInfoGrid.bootgrid("clear");
        UIElements.joinAdditionalInfoGrid.bootgrid("clear");
    },

    populateTableSummaryGrid: function (d) {
        var entries = [];

        d3.entries(d.baseTable.stats).forEach(function (entry) {
            // Todo: there has to be a better way to do this :P
            if (typeof(entry.value) !== "object" ||
                (typeof(entry.value) === "object" && entry.value.displayOnUI)) {
                entries.push(
                    {
                        name: entry.key,
                        value: function () {
                            if (typeof(entry.value) === "object") {
                                return (entry.value.value);
                            } else {
                                if (entry.key == "No Of Rows") {
                                    return (numberWithCommas(entry.value));
                                }
                                return (entry.value);
                            }
                        }()
                    });
            }
        });

        UIElements.summaryGrid.bootgrid("append", entries);
    },
    populateDistKeyGrid: function (d) {
        var columns = [];

        d.baseTable.DKs.forEach(function (column) {
            columns.push({
                // The property names are in this format so that bootgrid can find and bind em
                name: column["Column Name"],
                "usage-freq": column["Usage Frequency"],
                "where-usage": column["Where Usage"]//,
                //"additionalData": column.whereConditionValue
            });
        });

        UIElements.dkInfoGrid.bootgrid("append", columns);
    },
    populateColumnsGrid: function (d) {
        // param d here is the nodeData passed from visualizer nodeClick
        UIElements.columnsGrid.bootgrid("clear");

        var columns = [];
        UIElements.numberOfColumnsInTable = 0;
        UIElements.numberOfColumnsCurrentlyShowing = 0;

        d.baseTable.columns.forEach(function (column) {
            UIElements.numberOfColumnsInTable++;

            if (column["Usage Frequency"] > 0 || column["Where Usage"] > 0 || UIElements.showAllColumns) {
                UIElements.numberOfColumnsCurrentlyShowing++;

                columns.push({
                    // The property names are in this format so that bootgrid can find and bind em
                    name: column["Column Name"],
                    "usage-freq": column["Usage Frequency"],
                    "where-usage": column["Where Usage"],
                    "originalObject": column
                });
            }
        });

        UIElements.columnsGrid.bootgrid("append", columns);
    },
    populateJoinsGrid: function (d) {
        var joins = [];

        d.baseTable.joins.forEach(function (join) {
            if (dataModel.links.contains(function (link) {
                    if (link.baseJoin.uniqueID === join.uniqueID)
                        return (true);
                    return (false);
                })) {
                joins.push({
                    // The property names are in this format so that bootgrid can find and bind em
                    "left-schema": join["Left Schema Name"],
                    "left-table": join["Left Table Name"],
                    "right-schema": join["Right Schema Name"],
                    "right-table": join["Right Table Name"],
                    "join-usage-score": join["Join Usage Score"],
                    "originalObject": join
                });
            }
        });

        UIElements.joinsGrid.bootgrid("append", joins);
    },

    /// UI Hooks ///
    joinsGridRowClicked: function (e, columns, poco) {
        UIElements.joinAdditionalInfoGrid.bootgrid("clear");

        var rows = [];

        poco.originalObject["Join Tuples"].forEach(function (tuple) {
            rows.push({
                "left-column": tuple["Left Column"],
                "right-column": tuple["Right Column"]
            });
        });

        UIElements.joinAdditionalInfoGrid.bootgrid("append", rows);
    },
    // NOTE: Not needed anymore due to popover on column
    columnsGridRowClicked: function (e, columns, poco) {
        /*UIElements.selectedColumnLabel.text("[" + poco.originalObject["Column Name"] + "]");*/

        // TODO: need to discuss with Muji
        /*UIElements.columnAdditonalInfoGrid.bootgrid("clear");

         var rows = [];

         d3.entries(poco.additionalData).forEach(function (entry) {
         rows.push({
         "where-clause": entry.value
         });
         });

         UIElements.columnAdditonalInfoGrid.bootgrid("append", rows);*/
    },
    gridDataLoaded: function (e) {
        if ($(e.target).attr("id") === "columns-grid") {
            $("i[data-column-id]")
                .on("mouseover", UIElements.onMouseOverColumnIcon)
                .on("mouseout", UIElements.onMouseOutColumnIcon);

            $("#columns-grid").tooltip("destroy");
            $("#columns-grid").tooltip({
                selector: "th, span[data-isname]",
                container: "body",
                html: true,
                title: function () {
                    var jqElem = $(this);

                    return (jqElem.text());
                }
            });

            $("#num-of-cols-showing").text("(" + UIElements.numberOfColumnsCurrentlyShowing + " of " + UIElements.numberOfColumnsInTable + ")");
        } else if ($(e.target).attr("id") === "joins-grid") {
            $("#joins-grid").tooltip("destroy");
            $("#joins-grid").tooltip({
                selector: "th, span[data-table-unique-id]",
                container: "body",
                html: true,
                title: function () {
                    var jqElem = $(this);

                    if (jqElem.data("table-unique-id") === undefined) {
                        return (jqElem.text());
                    } else {
                        var tuID = jqElem.data("table-unique-id");
                        var table = dataModel.findTable(tuID);

                        var html = '<div class="text-left" style="color: yellow;font-size:1.2em">' +
                            "Name: " + table["Table Name"] + "<br>" +
                            "Rows: " + numberWithCommas(table.stats["No Of Rows"]) + "<br>" +
                            "Size on Disk: " + table.stats["Size On Disk (Compressed)"] + "<br>" +
                            "Size Uncompressed:" + table.stats["Size Uncompressed"] +
                            "</div>";

                        return (html);
                    }
                }
            });
        }
    },
    /// UI Hooks ///

    resetTableSelect: function () {
        UIElements.tableSelectDropdown.select2("val", "000");
    },
    initTableSelectDropdown: function () {
        var tableSelect = UIElements.tableSelectDropdown;

        try {
            if (tableSelect.data("select2")) {
                tableSelect.select2("destroy");
                tableSelect
                    .find('option')
                    .remove();
            }
            tableSelect.select2({
                data: dataModel.nodes.map(function (node) {
                    return ({id: node.baseTable["Table Name"], text: node.baseTable["Table Name"]});
                }),
                theme: "bootstrap",
                placeholder: {id: "000", text: "Select a table"},
                width: "270px"
            });

            tableSelect.select2("val", "");
        }
        catch (ex) {
            throw ex;
        }
    },

    onSelectedTableChanged: function (e) {
        //console.log("Selected table changed to", UIElements.tableSelectDropdown.val());
        var nodeData = dataModel.nodes.first(function (node) {
            if (node.baseTable["Table Name"] === UIElements.tableSelectDropdown.val()) {
                return (true);
            }
            return (false);
        });

        if (nodeData)
            UIElements.onNodeClicked(nodeData);
        else if (UIElements.tableSelectDropdown.val())
            throw new Error("This should not be happening !");
    },
    onDisplayAccordingToChanged: function (e) {
        setFiltersAndDisplayOptions();
        dataModel.applyFiltersAndDisplayOptions();
        UIElements.refresh();
        Visualizer.updateLayout(dataModel);
    },
    onNodeClicked: function (d) {
        UIElements.clearAllGrids();

        UIElements.selectedTableLabel.text("[" + d.baseTable["Table Name"] + "]");

        UIElements.populateTableSummaryGrid(d);

        UIElements.populateDistKeyGrid(d);

        UIElements.populateColumnsGrid(d);

        UIElements.populateJoinsGrid(d);
    },

    refresh: function () {
        UIElements.clearAllGrids();

        UIElements.initTableSelectDropdown();

        UIElements.displayBasedOnFilterTextbox.attr("data-original-title", sprintf("%s is greater than", $("input[name='display-according-to']:checked").val()));
        $("#showing-table-count").html(sprintf("<strong>(</strong>Showing %i of %i tables<strong>)</strong>", dataModel.nodes.length, dataModel.totalNodeCountBeforeFilter));
    },
    onMouseOverColumnIcon: function (e) {
        var element = $(this);
        var columnUniqueID = parseInt($(this).data("column-id"));

        var column = dataModel.allColumns.first(function (col) {
            return (col.uniqueID === columnUniqueID);
        });

        element.popover({
            html: true,
            placement: "bottom",
            container: "body",
            content: (function () {
                var str = '<table class="table table-condensed table-hover table-striped">';
                str += '<thead>';
                str += '<tr>';
                str += '<th>Where Condition</th>';
                str += '<th>Frequency</th>';
                str += '</tr>';
                str += '</thead>';
                for (var i = 0; i < column["Where Conditions and Frequencies"].length; i++) {
                    str += '<tr>';
                    str += '<td>' + column["Where Conditions and Frequencies"][i].value + '</td>';
                    str += '<td class="text-center">' + column["Where Conditions and Frequencies"][i].frequency + '</td>';
                    str += '</tr>';
                }
                str += '</table>';

                return (str);
            })()
        }).popover("show");
    },
    onMouseOutColumnIcon: function (e) {
        $(this).popover("hide");
    },

    initialize: function () {
        UIElements.schemaSelectDropdown = $("#schema-select");
        UIElements.numberOfTablesToDisplayTextbox = $("#tables-to-display");
        UIElements.displayBasedOnFilterTextbox = $("#display-based-on-filter");
        UIElements.displayAccordingToRadioButton = $("input[name='display-according-to']").on("change", UIElements.onDisplayAccordingToChanged);

        UIElements.tableInfoTabs = $("#table-info-tabs a");

        UIElements.selectedTableLabel = $("#selected-table-label");
        UIElements.selectedColumnLabel = $("#selected-column-label");

        UIElements.dkInfoPanel = $("#dk-info-panel");
        //UIElements.columnAdditionalInfoPanel = $("#column-additional-info-panel");
        UIElements.joinAdditionalInfoPanel = $("#join-additional-info-panel");

        UIElements.tableSelectDropdown = $("#table-select");
        UIElements.tableSelectDropdown.change(UIElements.onSelectedTableChanged);

        var bootgridCommonConfig = {
            navigation: 0,
            labels: {
                noResults: ""
            },
            rowCount: [50]
        };
        var bootgridPageableConfig = Object.create(bootgridCommonConfig);

        bootgridPageableConfig.navigation = 2;
        bootgridPageableConfig.templates =
        {
            footer: "<div id=\"{{ctx.id}}\" class=\"{{css.footer}}\"><div class=\"row\"><div class=\"col-sm-12\" style=\"text-align: center\"><p class=\"{{css.pagination}}\"></p></div></div><div class=\"row\"><div class=\"col-sm-12\" style=\"text-align: center\"><p class=\"{{css.infos}}\"></p></div></div></div>"
        };
        bootgridPageableConfig.rowCount = [10];

        var columnsBootgridConfig = Object.create(bootgridPageableConfig);
        columnsBootgridConfig.formatters =
        {
            "name": function (column, row) {
                return ("<span data-isname>" + row.name + "</span>");
            },
            "whereInfo": function (column, row) {
                return (row["where-usage"] + (row["where-usage"] ? '<i class="fa fa-info-circle pull-right" style="font-size: 1.2em; cursor: pointer; padding-top: 1px" data-column-id="'+ row.originalObject.uniqueID +'"></i>' : ''));
            }
        };

        var joinsBootgridConfig = Object.create(bootgridPageableConfig);
        joinsBootgridConfig.formatters =
        {
            "tableSummary": function (col, row) {
                var join = row.originalObject;

                if (col.id === "left-table") {
                    return ('<span data-table-unique-id="' + join.leftTable["uniqueID"] + '">' + join["Left Table Name"] + '</span>');
                } else if (col.id === "right-table") {
                    return ('<span data-table-unique-id="' + join.rightTable["uniqueID"] + '">' + join["Right Table Name"] + '</span>');
                }
            }
        };

        UIElements.summaryGrid = $("#summary-grid").bootgrid(bootgridCommonConfig);
        UIElements.dkInfoGrid = $("#dk-info-grid").bootgrid(bootgridCommonConfig);

        UIElements.columnsGrid = $("#columns-grid").bootgrid(columnsBootgridConfig);
        UIElements.columnsGrid.on("click.rs.jquery.bootgrid", UIElements.columnsGridRowClicked);
        UIElements.columnsGrid.on("loaded.rs.jquery.bootgrid", UIElements.gridDataLoaded);

        UIElements.columnAdditonalInfoGrid = $("#column-additional-info-grid").bootgrid(bootgridCommonConfig);

        UIElements.joinsGrid = $("#joins-grid").bootgrid(joinsBootgridConfig);
        UIElements.joinsGrid.on("click.rs.jquery.bootgrid", UIElements.joinsGridRowClicked);
        UIElements.joinsGrid.on("loaded.rs.jquery.bootgrid", UIElements.gridDataLoaded);

        UIElements.joinAdditionalInfoGrid = $("#join-additional-info-grid").bootgrid(bootgridCommonConfig);

        UIElements.tableInfoTabs.on('show.bs.tab', onTableInfoTabChanged);

        // Initialize all tooltips
        $('[data-toggle="tooltip"]').tooltip();

        $("#selected-filename").on("click", function () {
            $("#input-file").click();
        });

        $("#apply-filters").on("click", applyFilters);

        $("#toggle-info-pane-size").on("click", onToggleInfoPanel);

        $("#input-file").on("change", onInputDataFileChanged);
    }
};
