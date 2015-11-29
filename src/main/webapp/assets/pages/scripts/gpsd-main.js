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
                acceptFileTypes: /(\.|\/)(sql)$/i
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
                console.log("Done !! ");
            });
            $('#fileupload').bind('fileuploadfail', function(e, data) {
                console.log("Failed !!");
            });
            $('#fileupload').bind('fileuploadalways', function(e, data) {
                console.log("Always !! ");
            });

            // Enable iframe cross-domain access via redirect option:
            /*$('#fileupload').fileupload(
             'option',
             'redirect',
             window.location.href.replace(
             /\/[^\/]*$/,
             '/cors/result.html?%s'
             )
             );*/

            // Upload server status check for browsers with CORS support:
            /*if ($.support.cors) {
             $.ajax({
             type: 'HEAD'
             }).fail(function () {
             $('<div class="alert alert-danger"/>')
             .text('Upload server currently unavailable - ' +
             new Date())
             .appendTo('#fileupload');
             });
             }*/

            // Load & display existing files:
            //$('#fileupload').addClass('fileupload-processing');
            /*$.ajax({
             // Uncomment the following to send cross-domain cookies:
             //xhrFields: {withCredentials: true},
             url: $('#fileupload').attr("action"),
             dataType: 'json',
             context: $('#fileupload')[0]
             }).always(function () {
             $(this).removeClass('fileupload-processing');
             }).done(function (result) {
             $(this).fileupload('option', 'done')
             .call(this, $.Event('done'), {result: result});
             });*/
        }

    };

}();

jQuery(document).ready(function() {
    FormFileUpload.init();

    $('body').on('click', 'a.vf', function(e) {
        e.preventDefault();
        showFullScreenPortlet('context-content-portlet', '/haystaxsui/gpsd/file/' + $(this).attr("data-id"), 'GPSD File Content');
    });

    $('body').on('click', 'button.delete-gpsd', function(e) {
        e.preventDefault();
        alert('(This is a Mock. Gonna delete this GPSD, You Sure ?')
    });

    $('body').on('click', '.portlet > .portlet-title > .tools > a.close-portlet', function(e) {
        e.preventDefault();
        hideFullScreenPortlet('context-content-portlet');
    });
});
