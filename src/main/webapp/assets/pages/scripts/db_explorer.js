/**
 * Created by adnan on 1/22/16.
 */

function normalizeForTreeView(tables) {
    var db = [];

    var uniqueSchemaNames = [];

    for (var tableIndex in tables) {
        var table = tables[tableIndex];
        var schemaName = table["Schema Name"];
        var schemaNode = {};

        if (uniqueSchemaNames.length === 0 || !_.contains(uniqueSchemaNames, schemaName)) {
            uniqueSchemaNames.push(schemaName);

            schemaNode["_name_"] = schemaName;
            schemaNode["_noOfTables_"] = 1;
            //schemaNode["text"] = "<b>Schema:</b> " + schemaName;
            schemaNode["icon"] = "fa fa-database";
            schemaNode["children"] = [];

            db.push(schemaNode);
        } else {
            schemaNode = db.filter(function (obj) {
                return (obj["_name_"] === schemaName);
            })[0];

            schemaNode["_noOfTables_"]++;
        }

        var tableNode = {};

        tableNode["text"] = "<b>Table:</b> " + table["Table Name"] + " (Size=" + table["stats"]["Size On Disk (Compressed)"] + "; " +
            "No of Rows=" + table["stats"]["No Of Rows"] + ")";
        tableNode["icon"] = "fa fa-archive";
        tableNode["sizeOnDisk"] = table["stats"]["SizeOnDisk"]["value"];

        schemaNode["children"].push(tableNode);
    }

    for (var schemaNodeIndex in db) {
        var schemaNode = db[schemaNodeIndex];
        schemaNode["children"].sort(function (a, b) {
            return (b["sizeOnDisk"] - a["sizeOnDisk"]);
        });
        var schemaSize = schemaNode["children"].reduce(function (prevVal, currVal, index) {
            if(index === 1)
                return (prevVal["sizeOnDisk"] + currVal["sizeOnDisk"]);
            else
                return (prevVal + currVal["sizeOnDisk"]);
        });
        schemaNode["text"] = "<b>Schema:</b> " + schemaNode["_name_"] + " (No of Tables=" + schemaNode["_noOfTables_"] + "; " +
            "Size=" + schemaSize.toFixed(4) + " GB)";
    }

    return (db);
}

$(function () {
    blockUI($('#db_tree'));

    var data = {};
    data["gpsd_id"] = $('#current-gpsd-id').val();

    loadViaAjax('/test/exploredb/json', data, 'json', null, null, null, function (result) {
        var jsonForDataModel = {};

        for (var k in result["tableHashMap"]) {
            jsonForDataModel[k] = result["tableHashMap"][k];
        }

        var dataModel = new Haystaxs.DataModel(jsonForDataModel);
        dataModel.initialize();

        var dbTreeJson = normalizeForTreeView(dataModel.allTables);

        /*var jsonString = JSON.stringify(result);
         $('html').html(jsonString);*/

        unBlockUI($('#db_tree'));

        $.jstree.defaults.core.themes.variant = "large";
        $('#db_tree').jstree({
            'core': {
                'data': dbTreeJson
            },
            "plugins": ["wholerow"]
        });
    });
});