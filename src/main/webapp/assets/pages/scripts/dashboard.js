function initAmChartSample() {
    if (typeof(AmCharts) === 'undefined' || $('#dashboard_amchart_1').size() === 0) {
        return;
    }

    var chartData = [{
        "date": "01-10-2015",
        "duration": 501,
        "select": 400000,
        "insert": 50000
    }, {
        "date": "05-10-2015",
        "duration": 715,
        "select": 250000,
        "insert": 23000
    }, {
        "date": "10-10-2015",
        "duration": 423,
        "select": 173023,
        "insert": 54000,
        "drop table": 21000
    }/*, {
     "date": "2012-01-13",
     "townName": "Salt Lake City",
     "townSize": 12,
     "distance": 425,
     "duration": 670,
     "latitude": 40.75,
     "alpha": 0.4
     }, {
     "date": "2012-01-14",
     "latitude": 36.1,
     "duration": 470,
     "townName": "Las Vegas",
     "townName2": "Las Vegas",
     "bulletClass": "lastBullet"
     }, {
     "date": "2012-01-15"
     }*/];

    var chart = AmCharts.makeChart("dashboard_amchart_1", {
        type: "serial",
        fontSize: 12,
        fontFamily: "Open Sans",
        dataDateFormat: "DD-MM-YYYY",
        dataProvider: chartData,

        addClassNames: true,
        startDuration: 1,
        color: "#6c7b88",
        marginLeft: 0,

        categoryField: "date",
        categoryAxis: {
            parseDates: true,
            minPeriod: "DD",
            autoGridCount: false,
            gridCount: 10,
            gridAlpha: 0.1,
            gridColor: "#FFFFFF",
            axisColor: "#555555",
            dateFormats: [{
                period: 'DD',
                format: 'DD'
            }, {
                period: 'WW',
                format: 'MMM DD'
            }, {
                period: 'MM',
                format: 'MMM'
            }, {
                period: 'YYYY',
                format: 'YYYY'
            }]
        },

        valueAxes: [{
            id: "a1",
            title: "Duration (Secs)",
            gridAlpha: 0,
            axisAlpha: 0
        }, {
            id: "a2",
            position: "right",
            gridAlpha: 0,
            axisAlpha: 0//,
            //labelsEnabled: false
        }, {
            id: "a3",
            title: "duration",
            position: "right",
            gridAlpha: 0,
            axisAlpha: 0,
            inside: true,
            labelsEnabled: false/*,
             duration: "mm",
             durationUnits: {
             DD: "d. ",
             hh: "h ",
             mm: "min",
             ss: ""
             }*/
        }],
        graphs: [{
            id: "g1",
            valueField: "duration",
            title: "Duration",
            type: "column",
            fillAlphas: 0.7,
            valueAxis: "a1",
            balloonText: "[[value]] miles",
            legendValueText: "[[value]] mi",
            legendPeriodValueText: "total: [[value.sum]] mi",
            lineColor: "#08a3cc",
            alphaField: "alpha",
        }, {
            id: "g2",
            valueField: "select",
            classNameField: "bulletClass",
            title: "Select",
            type: "line",
            valueAxis: "a2",
            lineColor: "#786c56",
            lineThickness: 1,
            legendValueText: "[[description]]/[[value]]",
            descriptionField: "townName",
            bullet: "round",
            bulletSizeField: "townSize",
            bulletBorderColor: "#02617a",
            bulletBorderAlpha: 1,
            bulletBorderThickness: 2,
            bulletColor: "#89c4f4",
            labelText: "[[townName2]]",
            labelPosition: "right",
            balloonText: "latitude:[[value]]",
            showBalloon: true,
            animationPlayed: true,
        }, {
            id: "g3",
            title: "Insert",
            valueField: "insert",
            type: "line",
            valueAxis: "a3",
            lineAlpha: 0.8,
            lineColor: "#e26a6a",
            balloonText: "[[value]]",
            lineThickness: 1,
            legendValueText: "[[value]]",
            bullet: "square",
            bulletBorderColor: "#e26a6a",
            bulletBorderThickness: 1,
            bulletBorderAlpha: 0.8,
            dashLengthField: "dashLength",
            animationPlayed: true
        }],

        chartCursor: {
            zoomable: false,
            categoryBalloonDateFormat: "DD",
            cursorAlpha: 0,
            categoryBalloonColor: "#e26a6a",
            categoryBalloonAlpha: 0.8,
            valueBalloonsEnabled: false
        },
        legend: {
            bulletType: "round",
            equalWidths: false,
            valueWidth: 120,
            useGraphSettings: true,
            color: "#6c7b88"
        }
    });
}

function initQueryLogDurationChart() {

    if (typeof(AmCharts) === 'undefined' || $('#querylog-duration-chart').size() === 0) {
        return;
    }

    var rawChartData = [];

    loadViaAjax('/dashboard/ql/chartdata', null, 'json', null, null, null, function (result) {
        rawChartData = result;
        var sequencer = rawChartData[rawChartData.length-1];
        makeChart(getGraphsArray(sequencer), rawChartData.slice(0, rawChartData.length-1));
    });

    var getGraphsArray = function(sequencer, topN) {
        var graphs = [];
        var durations = [];

        for(var type in sequencer) {
            if(type.endsWith('Duration') && type !== 'totalDuration') {
                var o = {};
                o[type] = sequencer[type];
                durations.push(o)
            }
        }

        var durationsSorted = durations.sort(function(a, b) { return(b[Object.keys(b)[0]] - a[Object.keys(a)[0]]); });

        for(var index in durationsSorted) {
            var qryTypeCount = durationsSorted[index][Object.keys(durationsSorted[index])[0]];
            if(qryTypeCount === 0) {
                continue;
            }
            if(topN && topN == index) {
                break;
            }

            var qryType = Object.keys(durationsSorted[index])[0];
            var qryTypeProperties = getQueryTypeProperties(qryType);

            // Dividing 360 Color segment by 16 = 22.5
            var hue = Math.floor(Math.random() * 22.5) + (22.5 * index);
            var pastel = 'hsl(' + hue + ', 100%, 51%)';

            var g = {
                valueField: qryType,
                title: qryTypeProperties["title"],
                type: "line",
                fillAlphas: 0.2,
                valueAxis: "va1",
                legendValueText: "[[value]]",
                lineColor: pastel,
                lineThickness: 1.5
            };

            graphs.push(g);
        }

        return(graphs);
    }

    var getQueryTypeProperties = function(qryType) {
        switch (qryType) {
            case "selectDuration": return({ title: "Select" });
            case "analyzeDuration": return({ title: "Analyze" });
            case "commitDuration": return({ title: "Commit" });
            case "createExternalTableDuration": return({ title: "Create Ext Tbl" });
            case "createTableDuration": return({ title: "Create Tbl" });
            case "deleteDuration": return({ title: "Delete" });
            case "dropTableDuration": return({ title: "Drop Tbl" });
            case "exclusiveLockDuration": return({ title: "Ex Lock" });
            case "insertDuration": return({ title: "Insert" });
            case "internalDuration": return({ title: "Internal" });
            case "othersDuration": return({ title: "Other" });
            case "showConfigurationDuration": return({ title: "Show Config" });
            case "showDuration": return({ title: "Show" });
            case "transactionOperationDuration": return({ title: "Tx Op" });
            case "truncateTableDuration": return({ title: "Truncate Tbl" });
            case "updateDuration": return({ title: "Update" });
        }
    }

    var makeChart = function(graphs, chartData) {
        debugger;
        var chart = AmCharts.makeChart("querylog-duration-chart", {
            type: "serial",
            fontSize: 12,
            fontFamily: "Open Sans",
            dataDateFormat: "DD-MM-YYYY",
            dataProvider: chartData,
            /*dataLoader: {
             "url": App.webAppPath + "/dashboard/ql/chartdata",
             "format": "json"
             },*/

            categoryField: "date",
            categoryAxis: {
                parseDates: true,
                minPeriod: "DD",
                autoGridCount: true,
                //gridCount: 10,
                gridAlpha: 0.1,
                gridColor: "#FFFFFF",
                axisColor: "#555555",
                dateFormats: [{
                    period: 'DD',
                    format: 'DD'
                }, {
                    period: 'WW',
                    format: 'MMM DD'
                }, {
                    period: 'MM',
                    format: 'MMM YYYY'
                }, {
                    period: 'YYYY',
                    format: 'YYYY'
                }]
            },

            valueAxes: [{
                id: "va1",
                title: "Duration (Secs)",
                gridAlpha: 0.2,
                axisAlpha: 0
            }],

            graphs: graphs/*[{
                id: "g1",
                valueField: "selectDuration",
                title: "Select",
                type: "line",
                fillAlphas: 0.75,
                valueAxis: "va1",
                legendValueText: "[[value]]",
                lineColor: "#44B4D5"
            }, {
                id: "g2",
                valueField: "dropTableDuration",
                title: "Drop Tbl",
                type: "line",
                fillAlphas: 0.75,
                valueAxis: "va1",
                legendValueText: "[[value]]",
                lineColor: "#AE70ED"
            }, {
                id: "g3",
                valueField: "createExternalTableDuration",
                title: "Ext Tbl",
                type: "line",
                fillAlphas: 0.75,
                valueAxis: "va1",
                legendValueText: "[[value]]",
                lineColor: "#FF3542"
            }]*/,

            chartCursor: {
                //zoomable: true,
                categoryBalloonDateFormat: "MMM DD",
                cursorAlpha: 0.25,
                //categoryBalloonColor: "#e26a6a",
                categoryBalloonAlpha: 0.75,
                valueBalloonsEnabled: false
            },

            legend: {
                valueAlign: "left",
                //enabled: false
            }
        });
    }
}

function initQueryLogCountChart() {
    if (typeof(AmCharts) === 'undefined' || $('#querylog-count-chart').size() === 0) {
        return;
    }

    var rawChartData = [];

    loadViaAjax('/dashboard/ql/chartdata', null, 'json', null, null, null, function (result) {
        rawChartData = result;
        var sequencer = rawChartData[rawChartData.length-1];
        makeChart(getGraphsArray(sequencer), rawChartData.slice(0, rawChartData.length-1));
    });

    var getGraphsArray = function(sequencer, topN) {
        var graphs = [];
        var counts = [];

        for(var type in sequencer) {
            if(type.endsWith('Count') && type !== 'totalCount') {
                var o = {};
                o[type] = sequencer[type];
                counts.push(o)
            }
        }

        var countsSorted = counts.sort(function(a, b) { return(b[Object.keys(b)[0]] - a[Object.keys(a)[0]]); });

        for(var index in countsSorted) {
            var qryTypeCount = countsSorted[index][Object.keys(countsSorted[index])[0]];
            if(qryTypeCount === 0) {
                continue;
            }
            if(topN && topN == index) {
                break;
            }

            var qryType = Object.keys(countsSorted[index])[0];
            var qryTypeProperties = getQueryTypeProperties(qryType);

            // Dividing 360 Color segment by 16 = 22.5
            var hue = Math.floor(Math.random() * 22.5) + (22.5 * index);
            var pastel = 'hsl(' + hue + ', 100%, 51%)';

            var g = {
                valueField: qryType,
                title: qryTypeProperties["title"],
                type: "line",
                fillAlphas: 0.2,
                valueAxis: "va1",
                legendValueText: "[[value]]",
                lineColor: pastel,
                lineThickness: 1.5
            };

            graphs.push(g);
        }

        return(graphs);
    }

    var getQueryTypeProperties = function(qryType) {
        switch (qryType) {
            case "selectCount": return({ title: "Select" });
            case "analyzeCount": return({ title: "Analyze" });
            case "commitCount": return({ title: "Commit" });
            case "createExternalTableCount": return({ title: "Create Ext Tbl" });
            case "createTableCount": return({ title: "Create Tbl" });
            case "deleteCount": return({ title: "Delete" });
            case "dropTableCount": return({ title: "Drop Tbl" });
            case "exclusiveLockCount": return({ title: "Ex Lock" });
            case "insertCount": return({ title: "Insert" });
            case "internalCount": return({ title: "Internal" });
            case "othersCount": return({ title: "Other" });
            case "showConfigurationCount": return({ title: "Show Config" });
            case "showCount": return({ title: "Show" });
            case "transactionOperationCount": return({ title: "Tx Op" });
            case "truncateTableCount": return({ title: "Truncate Tbl" });
            case "updateCount": return({ title: "Update" });
        }
    }

    var makeChart = function(graphs, chartData) {
        var chart = AmCharts.makeChart("querylog-count-chart", {
            type: "serial",
            fontSize: 12,
            fontFamily: "Open Sans",
            dataDateFormat: "DD-MM-YYYY",
            dataProvider: chartData,
            /*dataLoader: {
                "url": App.webAppPath + "/dashboard/ql/chartdata",
                "format": "json"
            },*/

            categoryField: "date",
            categoryAxis: {
                parseDates: true,
                minPeriod: "DD",
                autoGridCount: true,
                //gridCount: 10,
                gridAlpha: 0.1,
                gridColor: "#FFFFFF",
                axisColor: "#555555",
                dateFormats: [{
                    period: 'DD',
                    format: 'DD'
                }, {
                    period: 'WW',
                    format: 'MMM DD'
                }, {
                    period: 'MM',
                    format: 'MMM YYYY'
                }, {
                    period: 'YYYY',
                    format: 'YYYY'
                }]
            },

            valueAxes: [{
                id: "va1",
                title: "Count",
                gridAlpha: 0.2,
                axisAlpha: 0
            }],

            graphs: graphs,

            chartCursor: {
                zoomable: true,
                categoryBalloonDateFormat: "MMM DD",
                cursorAlpha: 0,
                //categoryBalloonColor: "#e26a6a",
                categoryBalloonAlpha: 0.8,
                valueBalloonsEnabled: false
            },

            legend: {
                //useGraphSettings: true,
                //valueWidth: 120
                valueAlign: "left"
            }
        });
    }
}

function initGpsdGrowthChart() {
    if (typeof(AmCharts) === 'undefined' || $('#gpsd-growth-chart').size() === 0) {
        return;
    }

    var chartData = [
        {
            date: "01-10-2015",
            customers_size: 1024,
            customers_rows: 10223,
            accounts_size: 2048,
            accounts_rows: 228343,
            sales_size: 125364,
            sales_rows: 1234567
        },
        {
            date: "01-11-2015",
            customers_size: 1345,
            customers_rows: 15253,
            accounts_size: 2566,
            accounts_rows: 328343,
            sales_size: 225364,
            sales_rows: 1934567
        }
    ];

    var chart = AmCharts.makeChart("gpsd-growth-chart", {
        type: "serial",
        fontSize: 12,
        fontFamily: "Open Sans",
        dataDateFormat: "DD-MM-YYYY",
        dataProvider: chartData,
        /*dataLoader: {
         "url": App.webAppPath + "/dashboard/ql/chartdata",
         "format": "json"
         },*/

        categoryField: "date",
        categoryAxis: {
            parseDates: true,
            minPeriod: "MM",
            autoGridCount: true,
            gridCount: 10,
            gridAlpha: 0.1,
            gridColor: "#FFFFFF",
            axisColor: "#555555",
            dateFormats: [{
                period: 'DD',
                format: 'DD'
            }, {
                period: 'WW',
                format: 'MMM DD'
            }, {
                period: 'MM',
                format: 'MMM'
            }, {
                period: 'YYYY',
                format: 'YYYY'
            }]
        },

        valueAxes: [{
            id: "a1",
            title: "No Of Rows",
            gridAlpha: 0,
            axisAlpha: 0
        }],

        graphs: [{
            id: "g1",
            valueField: "customers_rows",
            title: "Customer Rows",
            type: "column",
            fillAlphas: 0.75,
            valueAxis: "a1",
            legendValueText: "[[value]]",
            //legendPeriodValueText: "total: [[value.sum]] mi",
            lineColor: "#08a3cc",
            alphaField: "alpha",
            newStack: true
        }, {
            id: "g2",
            valueField: "customers_size",
            title: "Customers Size",
            type: "column",
            fillAlphas: 0.75,
            valueAxis: "a1",
            balloonText: "[[value]]",
            legendValueText: "[[value]]",
            //legendPeriodValueText: "total: [[value.sum]] mi",
            lineColor: "green",
            alphaField: "alpha",
            newStack: true
        },{
            id: "g3",
            valueField: "accounts_rows",
            title: "Customer Rows",
            type: "column",
            fillAlphas: 0.75,
            valueAxis: "a1",
            legendValueText: "[[value]]",
            //legendPeriodValueText: "total: [[value.sum]] mi",
            lineColor: "#yellow",
            alphaField: "alpha",
            newStack: true
        }, {
            id: "g4",
            valueField: "accounts_size",
            title: "Customers Size",
            type: "column",
            fillAlphas: 0.75,
            valueAxis: "a1",
            balloonText: "[[value]]",
            legendValueText: "[[value]]",
            //legendPeriodValueText: "total: [[value.sum]] mi",
            lineColor: "orange",
            alphaField: "alpha",
            newStack: true
        }],

        chartCursor: {
            zoomable: true,
            categoryBalloonDateFormat: "MMM DD",
            cursorAlpha: 0,
            categoryBalloonColor: "#e26a6a",
            categoryBalloonAlpha: 0.8,
            valueBalloonsEnabled: false
        },

        legend: {
            useGraphSettings: true,
            valueWidth: 120
        }
    });
}

function initHourlyQueriesChart() {
    if (typeof(AmCharts) === 'undefined' || $('#hourly-queries-chart').size() === 0) {
        return;
    }

    var rawChartData = [];

    loadViaAjax('/dashboard/ql/hourlyavgchartdata', null, 'json', null, null, null, function (result) {
        rawChartData = result;
        var sequencer = rawChartData[rawChartData.length-1];
        makeChart(getGraphsArray(sequencer), rawChartData.slice(0, rawChartData.length-1));
    });

    var getGraphsArray = function(sequencer, topN) {
        var graphs = [];
        var durations = [];

        for(var type in sequencer) {
            if(type.endsWith('Duration') && type !== 'totalDuration') {
                var o = {};
                o[type] = sequencer[type];
                durations.push(o)
            }
        }

        var durationsSorted = durations.sort(function(a, b) { return(b[Object.keys(b)[0]] - a[Object.keys(a)[0]]); });

        for(var index in durationsSorted) {
            var qryTypeCount = durationsSorted[index][Object.keys(durationsSorted[index])[0]];
            if(qryTypeCount === 0) {
                continue;
            }
            if(topN && topN == index) {
                break;
            }

            var qryType = Object.keys(durationsSorted[index])[0];
            var qryTypeProperties = getQueryTypeProperties(qryType);

            // Dividing 360 Color segment by 16 = 22.5
            var hue = Math.floor(Math.random() * 22.5) + (22.5 * index);
            var pastel = 'hsl(' + hue + ', 100%, 51%)';

            var g = {
                valueField: qryType,
                title: qryTypeProperties["title"],
                type: "column",
                fillAlphas: 1,
                valueAxis: "va1",
                legendValueText: "[[value]]",
                lineColor: pastel,
                newStack: true
            };

            graphs.push(g);
        }

        return(graphs);
    }

    var getQueryTypeProperties = function(qryType) {
        switch (qryType) {
            case "selectDuration": return({ title: "Select" });
            case "analyzeDuration": return({ title: "Analyze" });
            case "commitDuration": return({ title: "Commit" });
            case "createExternalTableDuration": return({ title: "Create Ext Tbl" });
            case "createTableDuration": return({ title: "Create Tbl" });
            case "deleteDuration": return({ title: "Delete" });
            case "dropTableDuration": return({ title: "Drop Tbl" });
            case "exclusiveLockDuration": return({ title: "Ex Lock" });
            case "insertDuration": return({ title: "Insert" });
            case "internalDuration": return({ title: "Internal" });
            case "othersDuration": return({ title: "Other" });
            case "showConfigurationDuration": return({ title: "Show Config" });
            case "showDuration": return({ title: "Show" });
            case "transactionOperationDuration": return({ title: "Tx Op" });
            case "truncateTableDuration": return({ title: "Truncate Tbl" });
            case "updateDuration": return({ title: "Update" });
        }
    }

    var chartData = [
        {
            date: "0",
            select: 12332,
            insert: 1234,
            update: 3245
        },
        {
            date: "1",
            select: 33211,
            insert: 2123,
            update: 444
        },
        {
            date: "2",
            select: 4432,
            insert: 121,
            update: 355
        },
        {
            date: "3",
            select: 1231,
            insert: 25,
            update: 12
        },
        {
            date: "4",
            select: 15243,
            insert: 367,
            update: 788
        }
    ];

    var makeChart = function(graphs, chartData) {
        var chart = AmCharts.makeChart("hourly-queries-chart", {
            type: "serial",
            fontSize: 12,
            fontFamily: "Open Sans",
            dataDateFormat: "JJ:NN",
            dataProvider: chartData,
            //handDrawn: true,
            /*dataLoader: {
             "url": App.webAppPath + "/dashboard/ql/chartdata",
             "format": "json"
             },*/

            categoryField: "date",
            categoryAxis: {
                parseDates: true,
                minPeriod: "hh",
                //autoGridCount: true,
                gridCount: 24,
                minHorizontalGap: 25,
                gridAlpha: 0.1,
                gridColor: "#FFFFFF",
                axisColor: "#555555",
                boldPeriodBeginning: false,
                labelRotation: "90",
                equalSpacing: true,
                dateFormats: [{
                    period: 'DD',
                    format: 'JJ:NN'
                }, {
                    period: 'WW',
                    format: 'MMM DD'
                }, {
                    period: 'MM',
                    format: 'MMM'
                }, {
                    period: 'YYYY',
                    format: 'YYYY'
                }, {
                    period: 'hh',
                    format: 'JJ:NN'
                }]
            },

            valueAxes: [{
                id: "va1",
                title: "Duration (Secs)",
                gridAlpha: 0.2,
                axisAlpha: 0
            }],

            graphs: graphs,

            chartCursor: {
                //zoomable: true,
                categoryBalloonDateFormat: "",
                cursorAlpha: 0.25,
                //categoryBalloonColor: "#e26a6a",
                categoryBalloonEnabled: false,
                categoryBalloonAlpha: 0,
                fullWidth: true,
                valueBalloonsEnabled: false
            },

            legend: {
                valueAlign: "left"
            }
        });
    }
}

$(function () {
    initQueryLogDurationChart();

    initQueryLogCountChart();

    initGpsdGrowthChart();

    initHourlyQueriesChart();

    //var cd = generateChartData();
});


function generateChartData() {
    var chartData = [];
    // current date
    var firstDate = new Date();
    // now set 500 minutes back
    firstDate.setMinutes(firstDate.getDate() - 1000);

    // and generate 500 data items
    for (var i = 0; i < 500; i++) {
        var newDate = new Date(firstDate);
        // each time we add one minute
        newDate.setMinutes(newDate.getMinutes() + i);
        // some random number
        var visits = Math.round(Math.random() * 40 + 10 + i + Math.random() * i / 5);
        // add data item to the array
        chartData.push({
            date: newDate,
            visits: visits
        });
    }
    return chartData;
}