jQuery(document).ready(function () {
    if (jQuery().datepicker) {
        $('.date-picker').datepicker({
            orientation: "left",
            autoclose: true
        });
    }

    $('body').on('click', 'a.vtq', function (e) {
        e.preventDefault();
        showFullScreenPortlet('context-content-portlet', App.webAppPath + '/querylog/topqueries/' + $(this).attr("data-id"), 'Top 10 Queries for ' + $(this).attr("data-id"));
    });

    $('body').on('click', 'a.vqc', function (e) {
        e.preventDefault();
        showFullScreenPortlet('context-content-portlet', App.webAppPath + '/querylog/querycategories/' + $(this).attr("data-id"), 'Count of Query Types for ' + $(this).attr("data-id"));
    });

    $('body').on('click', '.portlet > .portlet-title > .tools > a.close-portlet', function (e) {
        e.preventDefault();
        hideFullScreenPortlet('context-content-portlet');
    });

    $('#btnFilterQueryLogs').on('click', function (e) {
        e.preventDefault();

        var fromDate = $('#fromDate').val();
        var toDate = $('#toDate').val();

        loadViaAjax('/querylog/list', {"fromDate": fromDate, "toDate": toDate}, "html", $('#querylog-list-portlet-body'));
    });

    $('body').on('click', 'a[data-pgno]', function(e) {
        var fromDate = $('#fromDate').val();
        var toDate = $('#toDate').val();

        if (!isEmpty(fromDate) && !isEmpty(toDate)) {
            // What todo about this case ?
        }

        loadViaAjax('/querylog/list', {"fromDate": fromDate, "toDate": toDate, "pgNo": $(this).attr("data-pgno"), "pgSize": 10}, "html",
            $('#querylog-list-portlet-body'));
    });

    loadViaAjax('/querylog/list', null, "html", $('#querylog-list-portlet-body'), $('#querylog-list-portlet-body').parent());
});
