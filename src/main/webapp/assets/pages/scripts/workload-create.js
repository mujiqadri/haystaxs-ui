/**
 * Created by adnan on 11/26/15.
 */
jQuery(document).ready(function() {
    if (jQuery().datepicker) {
        $('.date-picker').datepicker({
            orientation: "left",
            autoclose: true
        });
    }

    $('#create-workload-button').on('click', function() {

        debugger;

        var portletBody = $('#portlet-body-1');
        var dbName = $('#dbName').val();
        var fromDate = $('#fromDate').val();
        var toDate = $('#toDate').val();

        $.ajax({
            type: "POST",
            cache: false,
            url: "/haystaxs/workload/create",
            data: { "dbName": dbName, "fromDate": fromDate, "toDate": toDate },
            dataType: "html",
            success: function(res) {
                //App.unblockUI(el);
                portletBody.html(res);
                //App.initAjax() // reinitialize elements & plugins for newly loaded content
            },
            error: function(xhr, ajaxOptions, thrownError) {
                //App.unblockUI(el);
                var msg = 'Error on reloading the content. Please check your connection and try again.';
                /*if (error == "toastr" && toastr) {
                 toastr.error(msg);
                 } else if (error == "notific8" && $.notific8) {
                 $.notific8('zindex', 11500);
                 $.notific8(msg, {
                 theme: 'ruby',
                 life: 3000
                 });
                 } else {
                 alert(msg);
                 }*/
                portletBody.html(msg);
            }
        });
    });
});