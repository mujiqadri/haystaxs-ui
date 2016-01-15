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
    portlet.addClass('portlet-fullscreen-small');
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

    portlet.removeClass('portlet-fullscreen-small');
    $('body').removeClass('page-portlet-fullscreen');
    portlet.children('.portlet-body').css('height', 'auto');
}

function loadViaAjax(url, data, dataType, resultTargetEl, blockTargetEl, async, successCallback, failureCallback, httpMethod) {
    var result = {};

    blockTargetEl = blockTargetEl || resultTargetEl;

    if(blockTargetEl) {
        blockUI(blockTargetEl);
    }

    $.ajax({
        type: httpMethod ? httpMethod : "GET",
        cache: false,
        url: App.webAppPath + url,
        data: data,
        dataType: dataType ? dataType : "html",
        async: (async === false) ? async : true,
        success: function(res) {
            if(blockTargetEl) {
                unBlockUI(blockTargetEl);
            }
            if(resultTargetEl) {
                resultTargetEl.html(res);
            }

            if(dataType === "json") {
                result = res;
            }

            if(successCallback) {
                successCallback(res);
            }
        },
        error: function(xhr, ajaxOptions, thrownError) {
            var msg = 'Error on reloading the content. Please check your connection and try again.';

            if(blockTargetEl) {
                unBlockUI(blockTargetEl);
            }
            if(resultTargetEl) {
                resultTargetEl.html(msg);
            }

            if(failureCallback) {
                failureCallback(xhr, thrownError);
            }

            console.log(xhr.responseText);
        }
    });

    return result;
}

function blockUI(el) {
    el.block({
        message: 'Loading...',
        css: {
            border: '3px solid #aaa',
            padding: '15px',
            backgroundColor: '#000',
            '-webkit-border-radius': '10px',
            '-moz-border-radius': '10px',
            opacity: .5,
            color: '#fff'
        }
    });
}

function unBlockUI(el) {
    el.unblock();
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

    $('.portlet.box>.portlet-title').has('.tools>.expand,.tools>.collapse').css('cursor', 'pointer').on('click', function (e) {
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