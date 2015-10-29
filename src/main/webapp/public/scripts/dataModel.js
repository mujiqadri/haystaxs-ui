/**
 * Created by Adnan on 9/2/2015.
 */

var Haystaxs = Haystaxs || {};

Haystaxs.DataModel = (function () {
    function DataModel(backendJSON) {
        if (!backendJSON)
            throw new Error("DataModel needs a JSON generated by the backend to initialize.");

        // TODO: How do I turn these into private variables per object ?
        this.baseJSON = backendJSON;
        this.allSchemas = [];
        this.allTables = [];
        this.allColumns = [];
        this.allDKs = [];
        this.allPartitionedColumns = [];
        this.allJoins = [];

        this.nodes = [];
        this.links = [];

        /// FILTER THE TABLES THAT WILL BE REPRESENTED AS NODES IN THE GRAPH
        this.filters = {
            // TODO: This could be a multi select (csv)
            schemaToDisplay: DataModel.SHOW_ALL_SCHEMAS,
            numberOfTablesToDisplay: 0,
            greaterThan: -1
        };
        /// FILTER THE TABLES THAT WILL BE REPRESENTED AS NODES IN THE GRAPH
        this.display = {
            accordingTo: "score"
        };

        this.totalNodeCountBeforeFilter = 0;
    } /// End DataModel Constructor ///

    function normalizeTable(baseTable) {
        return ({
            "Object ID": baseTable.oid,
            "Database Name": baseTable.database,
            "Schema Name": baseTable.schema,
            "Table Name": baseTable.tableName,
            "dkArray": baseTable.dkArray,
            stats: {},
            columns: [],
            DKs: [],
            joins: []
        });
    }

    function normalizeStats(baseTable) {
        return ({
            "Skew": baseTable.stats.skew,
            "Is Columnar": baseTable.stats.isColumnar,
            "Is Compressed": baseTable.stats.isCompressed,
            "Storage Mode": baseTable.stats.storageMode,
            "Compression Type": baseTable.stats.compressType,
            "Compression Level": baseTable.stats.compressLevel,
            "No Of Rows": baseTable.stats.noOfRows,
            "SizeOnDisk": {value: baseTable.stats.sizeOnDisk, displayOnUI: false},
            "SizeUncompressed": {value: baseTable.stats.sizeUnCompressed, displayOnUI: false},
            "Size On Disk (Compressed)": baseTable.stats.sizeForDisplayCompressed,
            "Size Uncompressed": baseTable.stats.sizeForDisplayUnCompressed,
            "Compression Ratio": baseTable.stats.compressionRatio,
            "No Of Columns": baseTable.stats.noOfColumns,
            "Is Partitioned": baseTable.stats.isPartitioned,
            "Model Score": {value: baseTable.stats.modelScore, displayOnUI: true},
            "Usage Frequency": {value: baseTable.stats.usageFrequency, displayOnUI: true},
            "Execution Time": {value: baseTable.stats.executionTime, displayOnUI: true},
            "Workload Score": {value: baseTable.stats.workloadScore, displayOnUI: true}
        });
    }

    function normalizeColumn(baseColumn, parentTable) {
        return ({
            "Column Name": baseColumn.column_name,
            "Ordinal Position": baseColumn.ordinal_position,
            "Data Type": baseColumn.data_type,
            "Is DK": baseColumn.isDK,
            "Character Max Length": baseColumn.character_maximum_length,
            "Numeric Precision": baseColumn.numeric_precision,
            "Numeric Precision Radix": baseColumn.numeric_precision_radix,
            "Numeric Scale": baseColumn.numeric_scale,
            "Usage Frequency": baseColumn.usageFrequency,
            "Where Usage": baseColumn.whereUsage,
            "Where Conditions and Frequencies": (function() {
                if(_.keys(baseColumn.whereConditionValue).length !== _.keys(baseColumn.whereConditionFreq).length) {
                    console.error("Where column values do not match frequencies. [" + parentTable["Table Name"] + ":" + baseColumn.column_name + "]");
                }

                var wccaf = [];

                d3.entries(baseColumn.whereConditionValue).forEach(function(entry) {
                    var o = {};

                    o.key = entry.key;
                    o.value = entry.value;
                    o.frequency = baseColumn.whereConditionFreq[entry.key];

                    wccaf.push(o);
                });

                wccaf.sort(function (a, b) {
                    return(a.frequency < b.frequency ? 1 : a.frequency > b.frequency ? -1 : 0);
                });

                return(wccaf);
            })(),
            //"Where Condition Frequency": baseColumn.whereConditionFreq,
            "Resolved Table Name": baseColumn.resolvedTableName,
            "Resolved Schema Name": baseColumn.resolvedSchemaName,
            // Note: This circular reference makes it NON-Stringifyable by JSON.stringify()
            parentTable: parentTable
        });
    }

    function normalizeJoin(baseJoin, parentTable) {
        return {
            "Left Schema Name": baseJoin.leftSchema,
            "Left Table Name": baseJoin.leftTable,
            "Right Schema Name": baseJoin.rightSchema,
            "Right Table Name": baseJoin.rightTable,
            "Join Tuples": (function () {
                var jt = [];

                for (var jtKey in baseJoin.joinTuples) {
                    var baseJoinTuple = baseJoin.joinTuples[jtKey];
                    jt.push({
                        "Key": jtKey,
                        "Left Column": baseJoinTuple.leftcolumn,
                        "Right Column": baseJoinTuple.rightcolumn
                    });
                }

                return (jt);
            })(),
            "Level": baseJoin.level,
            "Join Usage Score": baseJoin.joinUsageScore,
            // Note: This circular reference makes it NON-Stringifyable by JSON.stringify()
            parentTable: parentTable
        };
    }

    function normalizeSchema() {
        var uniqueTableID = 1;
        var uniqueColumnID = 1;
        var uniqueJoinID = 1;

        for (var tKey in this.baseJSON) {
            var baseTable = this.baseJSON[tKey];
            this.filters.numberOfTablesToDisplay++;

            if (this.allSchemas.length === 0 || !_.contains(this.allSchemas, baseTable.schema)) {
                this.allSchemas.push(baseTable.schema);
            }

            var normalizedTable = normalizeTable(baseTable);
            normalizedTable.uniqueID = uniqueTableID++;

            for (var tPropKey in baseTable) {
                if (typeof(baseTable[tPropKey]) === "object") {
                    switch (tPropKey) {
                        case "stats":
                            normalizedTable.stats = normalizeStats(baseTable);
                            break;
                        case "columns":
                            for (var cKey in baseTable.columns) {
                                var baseColumn = baseTable.columns[cKey];
                                //if(baseColumn.resolvedTableName || baseColumn.resolvedSchemaName) console.log("Resolved Schema & Table Names not found for ", baseTable.tableName  , ":", baseColumn.column_name);
                                var normalizedColumn = normalizeColumn(baseColumn, normalizedTable);
                                normalizedColumn.uniqueID = uniqueColumnID++;
                                this.allColumns.push(normalizedColumn);
                                normalizedTable.columns.push(normalizedColumn);
                            }
                            break;
                        case "dk":
                            for (var cKey in baseTable.dk) {
                                var baseColumn = baseTable.dk[cKey];

                                var normalizedDK = normalizedTable.columns.filter(function (column) {
                                    if (column["Column Name"] === baseColumn.column_name)
                                        return (true);
                                    return (false);
                                })[0];

                                this.allDKs.push(normalizedDK);
                                normalizedTable.DKs.push(normalizedDK);
                            }
                            break;
                        case "partitionColumn":
                            if (baseTable.partitionedColumn)
                                throw new Error("Partitioned columns found !");
                            break;
                        case "joins":
                            for (var jKey in baseTable.joins) {
                                var baseJoin = baseTable.joins[jKey];

                                var normalizedJoin = normalizeJoin(baseJoin, normalizedTable);
                                normalizedJoin.uniqueID = uniqueJoinID++;
                                this.allJoins.push(normalizedJoin);
                                normalizedTable.joins.push(normalizedJoin);
                            }
                            break;
                        default:
                            throw new Error("Property " + tPropKey + " not expected in base JSON");
                    }
                }
            }

            this.allTables.push(normalizedTable);
        }

        this.allJoins.forEach(function (join) {
            join.leftTable = this.allTables.filter(function (table) {
                if (tableUniqueName(table) === tableUniqueName2(join["Left Schema Name"], join["Left Table Name"])) {
                    return (true);
                }
                return (false);
            })[0];

            join.rightTable = this.allTables.filter(function (table) {
                if (tableUniqueName(table) === tableUniqueName2(join["Right Schema Name"], join["Right Table Name"])) {
                    return (true);
                }
                return (false);
            })[0];
        }, this);
    }

    function nodeUniqueName(node) {
        return (tableUniqueName(node.baseTable));
    }

    function tableUniqueName(table) {
        return (tableUniqueName2(table["Schema Name"], table["Table Name"]));
    }

    // TODO: Gotta figure out method overloading techniques in JS
    function tableUniqueName2(schemaName, tableName) {
        return (schemaName + ":" + tableName);
    }

    function buildNodes() {
        this.nodes.splice(0, this.nodes.length);

        var nodeUniqueID = 1;
        this.totalNodeCountBeforeFilter = 0;
        _.each(this.allTables, function (table, index, list) {
            var node = new DataModel.NodeData(nodeUniqueID++);
            ++this.totalNodeCountBeforeFilter;

            node.baseTable = table;
            switch (this.display.accordingTo.toLowerCase()) {
                case "size":
                    node.hs_weight = table.stats.SizeOnDisk.value;
                    break;
                case "score":
                    node.hs_weight = table.stats["Model Score"].value;
                    break;
                case "time":
                    node.hs_weight = table.stats["Execution Time"].value;
                    break;
            }

            if (node.hs_weight <= parseFloat(this.filters.greaterThan)) {
                return;
            }

            if (this.filters.schemaToDisplay === DataModel.SHOW_ALL_SCHEMAS || this.filters.schemaToDisplay === node.baseTable["Schema Name"]) {
                this.nodes.push(node);
            }
        }, this);

        // Display only requested number of nodes based on descending order of score / size / time
        this.nodes.sort(function (aa, bb) {
            var a = aa.hs_weight, b = bb.hs_weight;
            return b < a ? -1 : b > a ? 1 : b >= a ? 0 : NaN;
        });
        this.nodes.splice(this.filters.numberOfTablesToDisplay, this.nodes.length);

        // Set the node positions to be inside the canvas for less initial bounce
        // NOTE: I dunno if this should be here, but then where ?
        this.nodes.forEach(function (node) {
            //if(!node.x)
            node.x = Math.floor((Math.random() * Visualizer.width / 2 + Visualizer.width / 4));
            //if(!node.y)
            node.y = Math.floor((Math.random() * Visualizer.height / 2 + Visualizer.height / 4));
        });
    }

    function buildLinks() {
        this.links.splice(0, this.links.length);

        var linkUniqueID = 1;
        _.each(this.allJoins, function (join, index, list) {
            var link = new DataModel.LinkData(linkUniqueID++);

            var leftTableNode = this.nodes.filter(function (node) {
                if (nodeUniqueName(node) === join["Left Schema Name"] + ":" + join["Left Table Name"])
                    return (true);
                return (false);
            })[0];

            var rightTableNode = this.nodes.filter(function (node) {
                if (nodeUniqueName(node) === join["Right Schema Name"] + ":" + join["Right Table Name"])
                    return (true);
                return (false);
            })[0];

            if (leftTableNode && rightTableNode && nodeUniqueName(leftTableNode) !== nodeUniqueName(rightTableNode)) {
                link.baseJoin = join;
                link.source = leftTableNode;
                link.target = rightTableNode;

                this.links.push(link);
            } else if (leftTableNode && rightTableNode) {
                console.log("Self join found. [" + leftTableNode.baseTable["Schema Name"] + ":" + leftTableNode.baseTable["Table Name"] + "]");
            }
        }, this);
    }

    DataModel.prototype.initialize = function () {
        normalizeSchema.call(this);
    };
    DataModel.prototype.applyFiltersAndDisplayOptions = function () {
        buildNodes.call(this);
        buildLinks.call(this);
    };
    DataModel.prototype.linksToNode = function (node) {
        return (this.links.filter(function (link) {
            if (link.source.uniqueID === node.uniqueID ||
                link.target.uniqueID === node.uniqueID) {
                return (true);
            }
            return (false);
        }));
    };
    DataModel.prototype.nodesLinkedToNode = function (node, includeSelf) {
        var self = this;

        return (this.nodes.filter(function (item) {
            if (includeSelf && item.uniqueID === node.uniqueID) {
                return (true);
            }

            var linksToThisNode = self.linksToNode(node);
            for (var i = 0; i < linksToThisNode.length; i++) {
                if (linksToThisNode[i].source.uniqueID === item.uniqueID ||
                    linksToThisNode[i].target.uniqueID === item.uniqueID) {
                    return (true);
                }
            }
            return (false);
        }));
    };
    DataModel.prototype.findTable = function (uniqueID) {
        return (this.allTables.filter(function (table) {
            if (table.uniqueID === uniqueID)
                return (true);
            return (false);
        })[0]);
    }

/// CONSTANTS & STATIC VARIABLES ///
    DataModel.SHOW_ALL_SCHEMAS = "HS_SHOW_ALL_SCHEMAS";

/// Constructors, defined as static methods ///
    DataModel.NodeData = function (uid) {
        this.uniqueID = uid;
    };
    DataModel.LinkData = function (uid) {
        this.uniqueID = uid;
    };

    return (DataModel);
})();
