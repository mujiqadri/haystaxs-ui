var QueryLogAnalysis = function() {
    return {
        dataForAjax: function(pageNo, orderBy) {
            var result = {};

            result.forDate = $('#for-date').val();
            result.astLike = $('#ast-like').val();
            result.duration = $('#duration-greater-than').val();
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

    $('#search-asts').on('click', function(e) {
        e.preventDefault();

        if($("#for-date").val() === "") {
            $("#for-date").focus();
            return;
        }

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

        loadViaAjax('/querylog/ast/analyze/search', data, 'html', $('#ast-list'), null, null, function () {
            hljs.initHighlighting.called = false;
            hljs.initHighlighting();
        });
    });

    $('body').on('click', 'a[data-pgno]', function(e) {
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

        loadViaAjax('/querylog/ast/analyze/search', data, 'html', $('#ast-list'), null, null, function () {
            hljs.initHighlighting.called = false;
            hljs.initHighlighting();
        });
    });

    $('.date-picker').datepicker();

    hljs.configure({
        languages: ["SQL", "JSON"]
    });

    hljs.initHighlighting.called = false;
    hljs.initHighlighting();
});

