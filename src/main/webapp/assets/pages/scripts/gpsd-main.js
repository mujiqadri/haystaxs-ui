jQuery(document).ready(function() {
    $('body').on('click', 'button.delete-gpsd', function(e) {

        var clusterId = $(this).data("id");

        bootbox.confirm("Are you sure you want to delete this Cluster?", function (result) {
            if (result) {
                loadViaAjax("/cluster/delete/" + clusterId, null, "html", null, null, null,
                    function () {
                        window.location.reload();
                    });
            }
        });
    });

    loadViaAjax('/cluster/list', null, "html", $('#gpsd-list-container'), $('#gpsd-list-container').parent());
});
