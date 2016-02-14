/**
 * Created by adnan on 2/12/16.
 */
$(function () {
    var activeClusterId = $('#active-cluster').val();
    //$('#active-cluster').on('focus')
    $('#active-cluster').on('change', function (e) {
        var selectedClusterName = $('#active-cluster option:selected').text();
        var selectedClusterId = $(this).val();

        bootbox.confirm("Change Active Cluster to " + selectedClusterName, function (result) {
            if (result) {
                loadViaAjax("/misc/changeactivecluster", {clusterId: selectedClusterId}, "html", null, null, null,
                    function () {
                        window.location.reload();
                    });
            } else {
                $('#active-cluster').val(activeClusterId);
            }
        });
    });

    $('#refresh-cluster-data').on('click', function(e) {
       loadViaAjax('/cluster/refresh');
    });
});
