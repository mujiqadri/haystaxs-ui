var QueryLogAnalysis = function() {
    return {
        dataForAjax: function(pageNo) {
            var result = {};

            result.forDate = $('#for-date').val();
            result.dbNameLike = $('#db-name-like').val();
            result.userNameLike = $('#user-name-like').val();
            /*result.startTime = $('#start-time').val();
            result.endTime = $('#end-time').val();*/
            result.sqlLike = $('#sql-like').val();
            result.duration = $('#duration-greater-than').val();
            result.queryType = $('#query-type').val();
            result.pgNo = pageNo;
            result.pgSize = $('#page-size').val();

            return result;
        }
    }
}();

jQuery(document).ready(function () {
    $('#search-queries').on('click', function(e) {
        e.preventDefault();

        var data = QueryLogAnalysis.dataForAjax(1);

        loadViaAjax('/querylog/analyze/search', data, $('#queries-list'));
    });

    $('body').on('click', 'a[data-pgno]', function(e) {
        e.preventDefault();

        var data = QueryLogAnalysis.dataForAjax($(this).attr("data-pgno"));

        loadViaAjax('/querylog/analyze/search', data, $('#queries-list'));
    });
});

