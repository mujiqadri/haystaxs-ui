var QueryLogAnalysis = function() {
    return {
        dataForAjax: function(pageNo, orderBy) {
            var result = {};

            //result.timeSpan = $('#timespan').val();
            result.startDate = $('#start-date').val();
            result.endDate = $('#end-date').val();
            result.startTime = $('#start-time').val();
            result.endTime = $('#end-time').val();
            result.dbNameLike = $('#db-name-like').val() === "---ANY---" ? "" : $('#db-name-like').val();
            result.userNameLike = $('#user-name-like').val() === "---ANY---" ? "" : $('#user-name-like').val();
            result.sqlLike = $('#sql-like').val();
            result.duration = $('#duration-greater-than').val();
            result.queryType = $('#query-type').val();
            result.pgNo = pageNo;
            result.pgSize = $('#page-size').val();
            result.orderBy = orderBy;

            return result;
        }
    }
}();

var maxDateWithTimeZone, minDateWithTimeZone;

jQuery(document).ready(function () {
    minDateWithTimeZone = new Date($('#start-date').val() + " " + $('#start-time').val());
    maxDateWithTimeZone = new Date($('#end-date').val() + " " + $('#end-time').val());

    $('#search-queries').on('click', function(e) {
        e.preventDefault();

        // TODO: remember last order by
        var data = QueryLogAnalysis.dataForAjax(1, $('#order-by').val());

        /*if(data.startDate === "") {
            $('#start-date').focus();
            return;
        }
        if(data.endDate === "") {
            $('#end-date').focus();
            return;
        }*/

        loadViaAjax('/querylog/analyze/search', data, 'html', $('#queries-list'), null, null, function () {
            hljs.initHighlighting.called = false;
            hljs.initHighlighting();
        });
    });

    $('#timespan').on('change', function(e) {
        var selectedValue = $(this).val();
        var minDateVal = $('#minDate').val();
        var maxDateVal = $('#maxDate').val();
        var startDate = $('#start-date');
        var endDate = $('#end-date');
        var startTime = $('#start-time');
        var endTime = $('#end-time');

        //startTime.val("00:00");
        //endTime.val("23:59");

        var mdm = new moment(maxDateWithTimeZone);
        endTime.val(moment(maxDateWithTimeZone).format("HH:mm"));

        if(selectedValue === "ALL") {
            startDate.val(moment(minDateWithTimeZone).format("DD-MMM-YYYY"));
            endDate.val(moment(maxDateWithTimeZone).format("DD-MMM-YYYY"));
            startTime.val(moment(minDateWithTimeZone).format("HH:mm"));
        } else {
            switch(selectedValue) {
                case "12hr": mdm.subtract(12, 'h'); break;
                case "1w": mdm.subtract(1, "w"); break;
                case "2w": mdm.subtract(2, "w"); break;
                case "1m": mdm.subtract(1, "M"); break;
                case "3m": mdm.subtract(3, "M"); break;
                case "12m": mdm.subtract(12, "M"); break;
            }

            startDate.val(mdm.format('DD-MMM-YYYY'));
            startTime.val(mdm.format('HH:mm'));
        }
    });

    $('body').on('click', 'a[data-pgno]', function(e) {
        debugger;
        e.preventDefault();

        var currentPageNo = $('#current-page-no').val();
        var selectedPageNo = $(this).attr('data-pgno');
        var totalNoOfPages = $('#total-no-of-pages').val();

        if(selectedPageNo === currentPageNo) {
            return;
        }

        if(selectedPageNo === 'prev') {
            if(currentPageNo === '1') {
                return;
            } else {
                currentPageNo--;
                $('#current-page-no').val(currentPageNo);
            }
        } else if(selectedPageNo === 'next') {
            if(currentPageNo === totalNoOfPages) {
                return;
            } else {
                currentPageNo++;
                $('#current-page-no').val(currentPageNo);
            }
        } else {
            $('#current-page-no').val(selectedPageNo);
            currentPageNo = selectedPageNo;
        }

        var data = QueryLogAnalysis.dataForAjax(currentPageNo, $('#order-by').val());

        loadViaAjax('/querylog/analyze/search', data, 'html', $('#queries-list'), null, null, function () {
            hljs.initHighlighting.called = false;
            hljs.initHighlighting();
        });

        /*if($('#current-page-display')) {
            $('#current-page-display').text('Page ' + currentPageNo + ' of ' + totalNoOfPages);
        }*/
    });

    $('body').on('click', 'a[data-orderby]', function(e) {
        e.preventDefault();

        $('#order-by').val($(this).attr('data-orderby'));
        var data = QueryLogAnalysis.dataForAjax(1, $(this).attr('data-orderby'));

        loadViaAjax('/querylog/analyze/search', data, 'html', $('#queries-list'));
    });

    $('.timepicker-24').timepicker({
        autoclose: true,
        minuteStep: 5,
        showSeconds: false,
        showMeridian: false,
        template: ""
    });

    $('.date-picker').datepicker();

    hljs.configure({
        languages: ["SQL"]
    });
});

