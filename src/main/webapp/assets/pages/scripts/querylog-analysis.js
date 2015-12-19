var QueryLogAnalysis = function() {
    return {
        dataForAjax: function(pageNo, orderBy) {
            var result = {};

            result.startDate = $('#start-date').val();
            result.endDate = $('#end-date').val();
            result.startTime = $('#start-time').val();
            result.endTime = $('#end-time').val();
            result.dbNameLike = $('#db-name-like').val();
            result.userNameLike = $('#user-name-like').val();
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

jQuery(document).ready(function () {
    $('#search-queries').on('click', function(e) {
        e.preventDefault();

        // TODO: remember last order by
        var data = QueryLogAnalysis.dataForAjax(1, $('#order-by').val());

        loadViaAjax('/querylog/analyze/search', data, 'html', $('#queries-list'));
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

        loadViaAjax('/querylog/analyze/search', data, 'html', $('#queries-list'));

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
});

