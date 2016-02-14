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
                acceptFileTypes: /(\.|\/)(gz)$/i
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
});
