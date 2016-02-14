/**
 * Created by adnan on 2/14/16.
 */
var AddCluster = function() {

    var handleAddCluster = function() {
        $.validator.addMethod('IsIPV4ORFQDN', function(value) {
            return value.match(/^(?:[0-9]{1,3}\.){3}[0-9]{1,3}$/) ||
                    value.match(/^(?=.{1,254}$)((?=[a-z0-9-]{1,63}\.)(xn--+)?[a-z0-9]+(-[a-z0-9]+)*\.)+[a-z]{2,63}$/);
        }, 'Please enter an IP address OR FQDN');

        $('#add-cluster-form').validate({
            errorElement: 'span', //default input error message container
            errorClass: 'help-block', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            ignore: "",
            rules: {
                "friendlyName": {
                    required: true
                },
                "dbName": {
                    required: true
                },
                "host": {
                    required: true,
                    IsIPV4ORFQDN: true
                },
                "userName": {
                    required: true
                },
                "password": {
                    required: true
                },
                "port": {
                    required: true,
                    digits: true,
                    min: 1000,
                    max: 65535
                }
            },

            messages: { // custom messages for radio buttons and checkboxes
                /*tnc: {
                    required: "Please accept TNC first."
                }*/
            },

            invalidHandler: function(event, validator) { //display error alert on form submit

            },

            highlight: function(element) { // hightlight error inputs
                $(element)
                    .closest('.form-group').addClass('has-error'); // set error class to the control group
            },

            success: function(label) {
                label.closest('.form-group').removeClass('has-error');
                label.remove();
            },

            errorPlacement: function(error, element) {
                /*if (element.attr("name") == "tnc") { // insert checkbox errors after the container
                    error.insertAfter($('#register_tnc_error'));
                } else*/ if (element.closest('.input-icon').size() === 1) {
                    error.insertAfter(element.closest('.input-icon'));
                } else {
                    error.insertAfter(element);
                }
            },

            submitHandler: function(form) {
                $('#add-cluster-submit').prop('disabled', true);
                form.submit();
            }
        });

        $('.register-form input').keypress(function(e) {
            if (e.which == 13) {
                if ($('#add-cluster-form').validate().form()) {
                    $('#add-cluster-form').submit();
                }
                return false;
            }
        });

        $('#register-submit-btn').on('click', function(e){

        });
    };

    return {
        //main function to initiate the module
        init: function() {
            //handleLogin();
            //handleForgetPassword();
            handleAddCluster();
        }
    };
}();

jQuery(document).ready(function() {
    AddCluster.init();
    $('#cluster-friendly-name').focus();
});
