function verifyUrlAvailability(serviceUrl, successCallback, errorCallback) {
    var url = jQuery('#labsSiteURL').val();
    var ok = false;
    jQuery.ajax({
        type : 'GET',
        async : false,
        url : serviceUrl + '/' + url,
        success : function(msg) {
            if (typeof successCallback == "function") {
                successCallback(msg);
            }
            ok = true;
        },
        error : function(xhr) {
            if (typeof errorCallback == "function") {
                errorCallback(xhr);
            }
            ok = false;
        }
    });
    return ok;
}

function setCheckUrlButton(state) {
    if (state === 'complete') {
        jQuery('#verifyUrlAvailability').button(state);
        jQuery('#verifyUrlAvailability').removeClass('btn-primary');
        jQuery('#verifyUrlAvailability').addClass('btn-success');
    } else if (state === 'failed') {
        jQuery('#verifyUrlAvailability').button(state);
        jQuery('#verifyUrlAvailability').removeClass('btn-primary');
        jQuery('#verifyUrlAvailability').addClass('btn-warning');
    }
}

function isGoodCharacterForUrl(car) {
	return isGoodIntValueOfCharForUrl(car.charCodeAt(0));
}

function isGoodIntValueOfCharForUrl(intValue) {
    if ((intValue >= 32 && intValue <= 44) || intValue === 47
            || (intValue >= 58 && intValue <= 64)) {
        return false;
    }
	return true;
}

jQuery(document).ready(function() {
    jQuery('#labsSiteURL').keypress(function(e) {
        jQuery('#urlAvailability').val('false');
        var btnObj = jQuery('#verifyUrlAvailability');
        jQuery(btnObj).button('reset');
        jQuery(btnObj).removeClass('btn-warning');
        jQuery(btnObj).removeClass('btn-success');
        jQuery(btnObj).addClass('btn-primary');
        if (!isGoodIntValueOfCharForUrl(e.which)) {
            return false;
        }
    });
    jQuery('#labsSiteURL').on('paste', function() {
    	setTimeout(function() {
    		var url = jQuery('#labsSiteURL').val();
    		var newUrl = '';
    		jQuery.each(url, function(key, value) {
    			if (isGoodCharacterForUrl(value)) {
    				newUrl += value;
    			}
    		});
    		jQuery('#labsSiteURL').val(newUrl);
    	}, 100);
    });
    jQuery('#labsSiteURL').keyup(function(e) {
        if (jQuery(this).val() === '') {
            jQuery('#verifyUrlAvailability').addClass('disabled');
            jQuery('#verifyUrlAvailability').attr('disabled', true);
        } else {
            jQuery('#verifyUrlAvailability').removeClass('disabled');
            jQuery('#verifyUrlAvailability').removeAttr('disabled');
        }
    });
});
