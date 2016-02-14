jQuery(document).ready(function() {
    $('body').on('click', 'button.delete-gpsd', function(e) {
        e.preventDefault();
        alert('(This is a Mock. Gonna delete this GPSD, You Sure ?')
    });

    loadViaAjax('/cluster/list', null, "html", $('#gpsd-list-container'), $('#gpsd-list-container').parent());
});
