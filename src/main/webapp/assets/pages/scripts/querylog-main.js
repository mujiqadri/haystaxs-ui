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

            $('#fileupload').bind('fileuploaddone', function(e, data) {
                console.log("Done.");
            });
            $('#fileupload').bind('fileuploadfailed', function(e, data) {
                console.log("Failed");
            });
            $('#fileupload').bind('fileuploadalways', function(e, data) {
                console.log("Always");
            });
        }

    };

}();

jQuery(document).ready(function() {
    FormFileUpload.init();

    $('body').on('click', 'a.vtq', function(e) {
        e.preventDefault();
        showFullScreenPortlet('context-content-portlet', App.webAppPath + '/querylog/topqueries/' + $(this).attr("data-id"), 'Top Queries for ' + $(this).attr("data-id"));
    });

    $('body').on('click', 'a.vqc', function(e) {
        e.preventDefault();
        showFullScreenPortlet('context-content-portlet', App.webAppPath + '/querylog/querycategories/' + $(this).attr("data-id"), 'Count of Query Types for ' + $(this).attr("data-id"));
    });

    $('body').on('click', '.portlet > .portlet-title > .tools > a.close-portlet', function(e) {
        e.preventDefault();
        hideFullScreenPortlet('context-content-portlet');
    });
});
