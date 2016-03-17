var pivotNormalChartData = function (data, valueField) {
    var distinctQueryTypes = [];
    var distinctDates = [];

    for(var i=0;i<data.length;i++) {
        if(data[i].queryType !== "" && distinctQueryTypes.indexOf(data[i].queryType) === -1) {
            distinctQueryTypes.push(data[i].queryType);
        }
        if(distinctDates.indexOf(data[i].date) === -1) {
            distinctDates.push(data[i].date);
        }
    }

    var pivotedData = [];

    for(var i=0;i<distinctDates.length;i++){
        var datum = {
            date: distinctDates[i]
        };

        for(var j=0;j<distinctQueryTypes.length;j++) {
            var qtFound = false;

            for(var k=0;k<data.length;k++) {
                if(data[k].date === distinctDates[i] && data[k].queryType === distinctQueryTypes[j]) {
                    if(valueField === "count")
                        datum[distinctQueryTypes[j]] = data[k].count;
                    else
                        datum[distinctQueryTypes[j]] = data[k].duration;

                    qtFound = true;
                    break;
                }
            }

            if(!qtFound) {
                datum[distinctQueryTypes[j]] = 0;
            }
        }

        pivotedData.push(datum);
    }

    return(pivotedData);
}
var getGraphsArray = function(data, topN) {
    var distinctQueryTypes = [];
    var graphs = [];

    for(var i=0;i<data.length;i++) {
        if(data[i].queryType !== "" && distinctQueryTypes.indexOf(data[i].queryType) === -1) {
            distinctQueryTypes.push(data[i].queryType);
        }
    }

    for(var i=0;i<distinctQueryTypes.length;i++) {
        // Dividing 360 Color segment by 16 = 22.5
        var hue = Math.floor(Math.random() * 22.5) + (22.5 * i);
        var pastel = 'hsl(' + hue + ', 100%, 51%)';

        var g = {
            valueField: distinctQueryTypes[i],
            title: distinctQueryTypes[i],
            type: "line",
            fillAlphas: 0.50,
            valueAxis: "va1",
            legendValueText: "[[value]]",
            lineColor: pastel,
            lineThickness: 1.5
        };

        graphs.push(g);
    }

    return graphs;
}

function initQueryLogDurationChart(data) {

    if (typeof(AmCharts) === 'undefined' || $('#querylog-duration-chart').size() === 0) {
        return;
    }

    var rawChartData = [];

    blockUI($('#querylog-duration-chart-holder'));

    loadViaAjax('/dashboard/ql/chartdata', data, 'json', null, null, null, function (result) {
        rawChartData = result;
        //var sequencer = rawChartData[rawChartData.length - 1];
        var pd = pivotNormalChartData(result, "duration");
        makeChart(getGraphsArray(result), pd);
        unBlockUI($('#querylog-duration-chart-holder'));
    });

    var makeChart = function (graphs, chartData) {
        var chart = AmCharts.makeChart("querylog-duration-chart", {
            type: "serial",
            fontSize: 12,
            fontFamily: "Open Sans",
            dataDateFormat: "YYYY-MM-DD",
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

        chart.validateData();
    }
}

function initQueryLogCountChart(data) {
    if (typeof(AmCharts) === 'undefined' || $('#querylog-count-chart').size() === 0) {
        return;
    }

    var rawChartData = [];

    blockUI($('#querylog-count-chart-holder'));

    loadViaAjax('/dashboard/ql/chartdata', data, 'json', null, null, null, function (result) {
        rawChartData = result;
        //var sequencer = rawChartData[rawChartData.length - 1];
        var pd = pivotNormalChartData(result, "count");
        makeChart(getGraphsArray(result), pd);
        unBlockUI($('#querylog-count-chart-holder'));
    });

    var makeChart = function (graphs, chartData) {
        var chart = AmCharts.makeChart("querylog-count-chart", {
            type: "serial",
            fontSize: 12,
            fontFamily: "Open Sans",
            dataDateFormat: "YYYY-MM-DD",
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

function initHourlyQueriesChart(data) {
    if (typeof(AmCharts) === 'undefined' || $('#hourly-queries-chart').size() === 0) {
        return;
    }

    var rawChartData = [];

    blockUI($('#hourly-queries-chart-holder'));

    data = data || {};
    if (!data["sqlWindowOp"]) {
        data["sqlWindowOp"] = "avg";
    }

    loadViaAjax('/dashboard/ql/hourlyavgchartdata', data, 'json', null, null, null, function (result) {
        rawChartData = result;
        var sequencer = rawChartData[rawChartData.length - 1];
        makeChart(getGraphsArray(sequencer), rawChartData.slice(0, rawChartData.length - 1));
        unBlockUI($('#hourly-queries-chart-holder'));
    });

    var getGraphsArray = function (sequencer, topN) {
        var graphs = [];
        var durations = [];

        for (var type in sequencer) {
            if (type.endsWith('Duration') && type !== 'totalDuration') {
                var o = {};
                o[type] = sequencer[type];
                durations.push(o)
            }
        }

        var durationsSorted = durations.sort(function (a, b) {
            return (b[Object.keys(b)[0]] - a[Object.keys(a)[0]]);
        });

        for (var index in durationsSorted) {
            var qryTypeCount = durationsSorted[index][Object.keys(durationsSorted[index])[0]];
            if (qryTypeCount === 0) {
                continue;
            }
            if (topN && topN == index) {
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

        /*graphs.push({
         valueField: "totalDuration",
         //title: qryTypeProperties["title"],
         type: "line",
         fillAlphas: 0,
         valueAxis: "va1",
         //legendValueText: "[[value]]",
         lineColor: "black",
         //newStack: true
         });*/

        return (graphs);
    }

    var getQueryTypeProperties = function (qryType) {
        switch (qryType) {
            case "selectDuration":
                return ({title: "Select"});
            case "analyzeDuration":
                return ({title: "Analyze"});
            case "commitDuration":
                return ({title: "Commit"});
            case "createExternalTableDuration":
                return ({title: "Create Ext Tbl"});
            case "createTableDuration":
                return ({title: "Create Tbl"});
            case "deleteDuration":
                return ({title: "Delete"});
            case "dropTableDuration":
                return ({title: "Drop Tbl"});
            case "exclusiveLockDuration":
                return ({title: "Ex Lock"});
            case "insertDuration":
                return ({title: "Insert"});
            case "internalDuration":
                return ({title: "Internal"});
            case "othersDuration":
                return ({title: "Other"});
            case "showConfigurationDuration":
                return ({title: "Show Config"});
            case "showDuration":
                return ({title: "Show"});
            case "transactionOperationDuration":
                return ({title: "Tx Op"});
            case "truncateTableDuration":
                return ({title: "Truncate Tbl"});
            case "updateDuration":
                return ({title: "Update"});
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

    var makeChart = function (graphs, chartData) {
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
                valueAlign: "left",
                enabled: graphs.length > 0
            }
        });
    }
}

function initHourlyComparisonQueriesChart(data) {
    if (typeof(AmCharts) === 'undefined' || $('#hourly-queries-comparison-chart').size() === 0) {
        return;
    }

    var rawChartData;

    blockUI($('#hourly-queries-comparison-chart-holder'));

    data = data || {};
    if (!data["sqlWindowOp"]) {
        data["sqlWindowOp"] = "avg";
    }

    loadViaAjax('/dashboard/ql/hourlyavgchartdata', data, 'json', null, null, null, function (result_1) {
        rawChartData = result_1;
        //var sequencer = rawChartData_Timeline1[rawChartData_Timeline1.length-1];
        var graphs = [];
        graphs.push({
            valueField: "totalDuration",
            title: "Selected Timeline",
            type: "line",
            fillAlphas: 0,
            valueAxis: "va1",
            legendValueText: "[[value]]",
            lineColor: "red"
        });

        // TODO: Compute previous timeline
        var timespanSelectedValue = $('#timespan').val();
        var comparisonExists = true;
        var mdmMax = new moment(maxDateWithTimeZone);
        var mdmMin;
        switch (timespanSelectedValue) {
            case "ALL":
                comparisonExists = false;
                break;
            case "12hr":
                mdmMax.subtract(12, 'h');
                mdmMin = mdmMax.clone();
                mdmMin.subtract(12, 'h');
                break;
            case "1w":
                mdmMax.subtract(1, 'w');
                mdmMin = mdmMax.clone();
                mdmMin.subtract(1, 'w');
                break;
            case "2w":
                mdmMax.subtract(2, 'w');
                mdmMin = mdmMax.clone();
                mdmMin.subtract(2, 'w');
                break;
            case "1m":
                mdmMax.subtract(1, "M");
                mdmMin = mdmMax.clone();
                mdmMin.subtract(1, 'M');
                break;
            case "3m":
                mdmMax.subtract(3, "M");
                mdmMin = mdmMax.clone();
                mdmMin.subtract(3, 'M');
                break;
            case "12m":
                mdmMax.subtract(12, "M");
                mdmMin = mdmMax.clone();
                mdmMin.subtract(12, 'M');
                break;
        }
        if (comparisonExists) {
            data = dataForAjax(mdmMin.format("DD-MMM-YYYY"), mdmMax.format("DD-MMM-YYYY"), data);
            loadViaAjax('/dashboard/ql/hourlyavgchartdata', data, 'json', null, null, null, function (result_2) {
                for (index in rawChartData) {
                    rawChartData[index]["totalDurationPrev"] = result_2[index]["totalDuration"];
                }

                graphs.push({
                    valueField: "totalDurationPrev",
                    title: "Previous Timeline",
                    type: "line",
                    fillAlphas: 0,
                    valueAxis: "va1",
                    legendValueText: "[[value]]",
                    lineColor: "blue"
                });

                makeChart(graphs, rawChartData.slice(0, rawChartData.length - 1));
                unBlockUI($('#hourly-queries-comparison-chart-holder'));
            });
        } else {
            makeChart(graphs, rawChartData.slice(0, rawChartData.length - 1));
            unBlockUI($('#hourly-queries-comparison-chart-holder'));

        }
    });

    var makeChart = function (graphs, chartData) {
        var chart = AmCharts.makeChart("hourly-queries-comparison-chart", {
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
                valueAlign: "left",
                enabled: graphs.length > 0
            }
        });
    }
}

function dataForAjax(minDate, maxDate, data) {
    var result = data || {};

    result.fromDate = minDate ? minDate : $('#start-date').val();
    result.toDate = maxDate ? maxDate : $('#end-date').val();
    result.dbName = $('#db-name-like').val() === "---ANY---" ? "" : $('#db-name-like').val();
    result.userName = $('#user-name-like').val() === "---ANY---" ? "" : $('#user-name-like').val();

    return result;
}

function hourlyQueriesSqlWindowOp_Change() {
    var data = {};

    data["sqlWindowOp"] = $(this).val();

    initHourlyQueriesChart(data);
}

function hourlyQueriesComparisonSqlWindowOp_Change() {
    var data = {};

    data["sqlWindowOp"] = $(this).val();

    initHourlyComparisonQueriesChart(data);
}

function generateSampleChartData() {
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

$(function () {
    if ($('#query-logs-processed').val() === 'true') {
        minDateWithTimeZone = new Date($('#start-date').val() + " 00:00");
        maxDateWithTimeZone = new Date($('#end-date').val() + " 23:59");

        $('#filter-chart-data').on('click', function (e) {
            e.preventDefault();

            var data = dataForAjax();

            initQueryLogDurationChart(data);
            initQueryLogCountChart(data);
            initHourlyQueriesChart(data);
            initHourlyComparisonQueriesChart(data);
        });
        $('#timespan').on('change', function (e) {
            var selectedValue = $(this).val();
            var startDate = $('#start-date');
            var endDate = $('#end-date');

            var mdm = new moment(maxDateWithTimeZone);

            if (selectedValue === "ALL") {
                startDate.val(moment(minDateWithTimeZone).format("DD-MMM-YYYY"));
                endDate.val(moment(maxDateWithTimeZone).format("DD-MMM-YYYY"));
            } else {
                switch (selectedValue) {
                    case "12hr":
                        mdm.subtract(12, 'h');
                        break;
                    case "1w":
                        mdm.subtract(1, "w");
                        break;
                    case "2w":
                        mdm.subtract(2, "w");
                        break;
                    case "1m":
                        mdm.subtract(1, "M");
                        break;
                    case "3m":
                        mdm.subtract(3, "M");
                        break;
                    case "12m":
                        mdm.subtract(12, "M");
                        break;
                }

                startDate.val(mdm.format('DD-MMM-YYYY'));
            }
        });
        $('#hourly-queries-sqlwindowop').on('change', hourlyQueriesSqlWindowOp_Change);
        $('#hourly-queries-comparison-sqlwindowop').on('change', hourlyQueriesComparisonSqlWindowOp_Change);

        initQueryLogDurationChart();
        initQueryLogCountChart();
        initHourlyQueriesChart();
        initHourlyComparisonQueriesChart();
    }
});

