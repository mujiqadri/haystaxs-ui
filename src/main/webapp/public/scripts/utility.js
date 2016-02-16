/**
 * Created by Adnan on 9/4/2015.
 */
/// GLOBAL GENERIC UTILITY FUNCTIONS ///
function isEmpty(obj) {
    // Speed up calls to Object.prototype.hasOwnProperty
    var hasOwnProperty = Object.prototype.hasOwnProperty;

    // null and undefined are "empty"
    if (obj == null) return true;

    // Assume if it has a length property with a non-zero value
    // that that property is correct.
    if (obj.length > 0)    return false;
    if (obj.length === 0)  return true;

    // Otherwise, does it have any properties of its own?
    // Also Note that this doesn't handle
    // toString and valueOf enumeration bugs in IE < 9
    for (var key in obj) {
        if (hasOwnProperty.call(obj, key)) return false;
    }

    return true;
}

function numberWithCommas(x) {
    // http://stackoverflow.com/questions/2901102/how-to-print-a-number-with-commas-as-thousands-separators-in-javascript
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function nsGen(namespace) {
    return namespace.split('.').reduce(function (holder, name) {
        holder[name] = holder[name] || {};
        return holder[name];
    }, window);
}

// Doing this is DANGEROUS as it starts showing up in for in loops
Array.prototype.contains = function(f) {
    for(var i=0; i<this.length; i++) {
        if(f(this[i]))
            return(true);
    }
    return(false);
};

Array.prototype.first = function(f) {
    for(var i=0; i<this.length; i++) {
        if(f(this[i]))
            return(this[i]);
    }
    return(null);
}