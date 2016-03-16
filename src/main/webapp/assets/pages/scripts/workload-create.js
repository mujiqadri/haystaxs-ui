var CreateWorkload = function () {

    var initializeDatePicker = function () {
        if (jQuery().datepicker) {
            $('.date-picker').datepicker({
                orientation: "left",
                autoclose: true
            });
        }
    };

    var fetchProgress = function(workloadId) {
        loadViaAjax('/workload/progress', { workloadId: workloadId }, 'json', null, null, null, function(result) {
            if(result.result === 'success') {
                $('#workload-progress-bar').width(result.message + '%');
                $('#workload-progress-bar-text').text(result.message + '% completed...');
                if(result.message < 100) {
                    setTimeout(fetchProgress(workloadId), 1000);
                } else {
                    $('#create-workload-button').addClass('hidden');
                    $('#goto-workloads').removeClass('hidden');
                }
            }
        });
    }

    return {
        init: function () {
            initializeDatePicker();
        },
        fetchProgress: fetchProgress
    }
}();

jQuery(document).ready(function () {

    CreateWorkload.init();

    $('#create-workload-button').on('click', function () {
        var portletBody = $('#portlet-body-1');
        //var dbName = $('#dbName').val();
        var fromDate = $('#fromDate').val();
        var toDate = $('#toDate').val();

        if (!isEmpty(fromDate) && !isEmpty(toDate)) {
            $(this).prop('disabled', true);

            var data = {/*"dbName": dbName, */"fromDate": fromDate, "toDate": toDate};
            loadViaAjax('/workload/create', data, 'json', null, null, null
                , function (result) {
                    if(result.result === 'success') {
                        $('#info-area').html('Processing...');
                        $('#workload-id').val(result.additionalInfo);

                        $('#workload-progress-bar-holder').removeClass('hidden');
                        CreateWorkload.fetchProgress(result.additionalInfo);
                    } else {
                        $('#info-area').html('There was an error processing the workload, please check system logs...');
                    }
                }, function (xhrObject) {
                }, "POST");
        }
    });
});