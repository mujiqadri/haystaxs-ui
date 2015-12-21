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

function initQueryLogChart() {
    if (typeof(AmCharts) === 'undefined' || $('#querylog-chart').size() === 0) {
        return;
    }

    var chart = AmCharts.makeChart("querylog-chart", {
        type: "serial",
        fontSize: 12,
        fontFamily: "Open Sans",
        dataDateFormat: "DD-MM-YYYY",
        //dataProvider: chartData,
        dataLoader: {
            "url": App.webAppPath + "/dashboard/ql/chartdata",
            "format": "json"
        },

        categoryField: "date",
        categoryAxis: {
            parseDates: true,
            minPeriod: "DD",
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
            title: "Duration (Secs)",
            gridAlpha: 0/*,
            axisAlpha: 0*/
        }, {
            id: "a2",
            title: "Query Count",
            position: "right"
        }],

        graphs: [{
            id: "g1",
            valueField: "totalDuration",
            title: "Total Duration",
            type: "column",
            fillAlphas: 0.2,
            valueAxis: "a1",
            balloonText: "[[value]] Seconds",
            legendValueText: "[[value]] Secs",
            //legendPeriodValueText: "total: [[value.sum]] mi",
            lineColor: "#08a3cc",
            alphaField: "alpha"
        }, {
            id: "g2",
            valueField: "totalCount",
            classNameField: "bulletClass",
            title: "Total Count",
            type: "line",
            valueAxis: "a2",
            bullet: "round",
            bulletSizeField: "townSize",
            bulletBorderColor: "green",
            bulletBorderAlpha: 1,
            bulletBorderThickness: 2,
            bulletColor: "lightgreen",
            balloonText: "[[value]]",
            legendValueText: "[[value]]",
            //legendPeriodValueText: "total: [[value.sum]] mi",
            lineColor: "green",
            alphaField: "alpha"
        }, {
            id: "g4",
            valueField: "selectDuration",
            classNameField: "bulletClass",
            title: "Select Duration",
            type: "line",
            valueAxis: "a1",
            bullet: "round",
            bulletSizeField: "townSize",
            bulletBorderColor: "yellow",
            bulletBorderAlpha: 1,
            bulletBorderThickness: 2,
            bulletColor: "lightyellow",
            balloonText: "[[value]]",
            legendValueText: "[[value]]",
            //legendPeriodValueText: "total: [[value.sum]] mi",
            lineColor: "yellow",
            alphaField: "alpha"
        }, {
            id: "g3",
            valueField: "selectCount",
            classNameField: "bulletClass",
            title: "Select Count",
            type: "line",
            valueAxis: "a2",
            bullet: "round",
            bulletSizeField: "townSize",
            bulletBorderColor: "red",
            bulletBorderAlpha: 1,
            bulletBorderThickness: 2,
            bulletColor: "orange",
            balloonText: "[[value]]",
            legendValueText: "[[value]]",
            //legendPeriodValueText: "total: [[value.sum]] mi",
            lineColor: "red",
            alphaField: "alpha"
        }],

        chartCursor: {
            zoomable: false,
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

$(function() {
   initQueryLogChart();
});
