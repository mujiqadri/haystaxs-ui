/**
Custom module for you to write your own javascript functions
**/
function showFullScreenPortlet(portletId, url, title) {
    var portlet = $('#' + portletId);

    var height = App.getViewPort().height -
        portlet.children('.portlet-title').outerHeight() -
        parseInt(portlet.children('.portlet-body').css('padding-top')) -
        parseInt(portlet.children('.portlet-body').css('padding-bottom'));

    //$(this).addClass('on');
    portlet.removeClass('hidden');
    portlet.addClass('portlet-fullscreen');
    $('body').addClass('page-portlet-fullscreen');
    portlet.children('.portlet-body').css('height', height);

    var portletTitle = portlet.find('.caption').first();
    portletTitle.empty();

    if(title) {
        portletTitle.html(title);
    }

    var portletBody = portlet.find('.portlet-body').first();
    portletBody.empty();

    $.ajax({
        type: "GET",
        cache: false,
        url: url,
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
}

function hideFullScreenPortlet(portletId) {
    var portlet = $('#' + portletId);

    portlet.addClass('hidden');

    portlet.removeClass('portlet-fullscreen');
    $('body').removeClass('page-portlet-fullscreen');
    portlet.children('.portlet-body').css('height', 'auto');
}

var Custom = function () {

    // private functions & variables

    var myFunc = function(text) {
        alert(text);
    }

    var doAjaxCallInternal = function(e) {
        var el = $(e.currentTarget);

        $.ajax({
            type: "GET",
            cache: false,
            url: url,
            dataType: "html",
            success: function(res) {
                App.unblockUI(el);
                el.html(res);
                App.initAjax() // reinitialize elements & plugins for newly loaded content
            },
            error: function(xhr, ajaxOptions, thrownError) {
                App.unblockUI(el);
                var msg = 'Error on reloading the content. Please check your connection and try again.';
                if (error == "toastr" && toastr) {
                    toastr.error(msg);
                } else if (error == "notific8" && $.notific8) {
                    $.notific8('zindex', 11500);
                    $.notific8(msg, {
                        theme: 'ruby',
                        life: 3000
                    });
                } else {
                    alert(msg);
                }
            }
        });
    }

    // public functions
    return {

        //main function
        init: function () {
            //initialize here something.            
        },

        //some helper function
        doAjaxCall: function (e) {
            doAjaxCallInternal(e);
        }
    };

}();

jQuery(document).ready(function() {    
   Custom.init(); 
});

/***
Usage
***/
//Custom.doSomeStuff();