/**
 * Created by adnan on 1/22/16.
 */

function normalizeForTreeView(tables) {
    var db = [];

    var schemas = [];

    for (var tableIndex in tables) {
        var table = tables[tableIndex];
        var schemaName = table["Schema Name"];
        var schemaNode = {};

        if (schemas.length === 0 || !_.contains(schemas, schemaName)) {
            schemas.push(schemaName);

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

        tableNode["text"] = "<b>Table:</b> " + table["Table Name"] + " (" + table["stats"]["Size On Disk (Compressed)"] + ")";
        tableNode["icon"] = "fa fa-archive"

        schemaNode["children"].push(tableNode);
    }

    for(var schemaNodeIndex in db) {
        var schemaNode = db[schemaNodeIndex];
        schemaNode["text"] = "<b>Schema:</b> " + schemaNode["_name_"] + " (" + schemaNode["_noOfTables_"] + " Tables)";
    }

    return (db);
}

$(function () {
    blockUI($('#db_tree'));

    loadViaAjax('/test/exploredb/json', null, 'json', null, null, null, function (result) {
        var jsonForDataModel = {};

        for (var k in result["tableHashMap"]) {
            jsonForDataModel[k] = result["tableHashMap"][k];
        }

        var dataModel = new Haystaxs.DataModel(jsonForDataModel);
        dataModel.initialize();

        var dbTreeJson = normalizeForTreeView(dataModel.allTables);

        //var jsonString = JSON.stringify();

        //$('html').html(jsonString);

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