// TODO: Gotta take care of the case wherre blue imp uploads files differently if added separately !
var maxNumberOfFiles = 1;

var FormFileUpload = function () {
    return {
        //main function to initiate the module
        init: function () {

            // Initialize the jQuery File Upload widget:
            $('#fileupload').fileupload({
                disableImageResize: false,
                autoUpload: false,
                disableImageResize: /Android(?!.*Chrome)|Opera/.test(window.navigator.userAgent),
                singleFileUploads: false,
                //getNumberOfFiles: function() { return 2; },
                maxNumberOfFiles: maxNumberOfFiles,
                acceptFileTypes: /(\.|\/)(zip|gz)$/i
                /*,
                 done: function(e, data) {
                 debugger;
                 }*/
                //,
                //maxFileSize: 5000000
                //,
                //acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
                // Uncomment the following to send cross-domain cookies:
                //xhrFields: {withCredentials: true},
            });

            $('#fileupload').bind('fileuploaddone', function (e, data) {
                console.log("Done.");
            });
            $('#fileupload').bind('fileuploadfailed', function (e, data) {
                console.log("Failed");
            });
            $('#fileupload').bind('fileuploadalways', function (e, data) {
                console.log("Always");
            });
        }

    };

}();

jQuery(document).ready(function () {
    FormFileUpload.init();

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
