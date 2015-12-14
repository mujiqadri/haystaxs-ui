/**
Custom module for you to write your own javascript functions
**/
function showFullScreenPortlet(portletId, url, title) {
    var portlet = $('#' + portletId);

    var height = App.getViewPort().height -
        portlet.children('.portlet-title').outerHeight() -
        parseInt(portlet.children('.portlet-body').css('padding-top')) -
        parseInt(portlet.children('.portlet-body').css('padding-bottom'));
    height = height * 83 /100;

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

    App.blockUI({
        target: portletBody,
        boxed: true
    });

    $.ajax({
        type: "GET",
        cache: false,
        url: url,
        dataType: "html",
        success: function(res) {
            App.unblockUI(portletBody);
            portletBody.html(res);
            //App.initAjax() // reinitialize elements & plugins for newly loaded content
        },
        error: function(xhr, ajaxOptions, thrownError) {
            App.unblockUI(portletBody);
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

function loadViaAjax(url, data, targetEl) {
    App.blockUI({
        target: targetEl,
        boxed: true
    });

    $.ajax({
        type: "GET",
        cache: false,
        url: App.webAppPath + url,
        data: data,
        dataType: "html",
        success: function(res) {
            App.unblockUI(targetEl);
            targetEl.html(res);
            //App.initAjax() // reinitialize elements & plugins for newly loaded content
        },
        error: function(xhr, ajaxOptions, thrownError) {
            App.unblockUI(targetEl);
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
            targetEl.html(msg);
        }
    });
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

    /*$('.portlet.box>.tools>.expand').on('click', function (e) {
        e.preventBubble();
    });*/

    $('.portlet.box').has('.tools>.expand,.tools>.collapse').css('cursor', 'pointer').on('click', function (e) {
        var expandCollapseLink = $(this).find('.tools>.expand,.tools>.collapse');

        var el = $(this).closest(".portlet").children(".portlet-body");

        if (expandCollapseLink.hasClass("collapse")) {
            expandCollapseLink.removeClass("collapse").addClass("expand");
            el.slideUp(200);
        } else {
            expandCollapseLink.removeClass("expand").addClass("collapse");
            el.slideDown(200);
        }
    });
});

/***
Usage
***/
//Custom.doSomeStuff();