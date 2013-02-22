$(document).ready(function() {
  // Dropdown for navbar nav
  // ===============================
/* Migration bootstrap 2.0 - conflit
	$("body").bind("click", function(e) {
    $('.dropdown-toggle, .menu').parent("li").removeClass("open");
  });

  $(".dropdown-toggle, .menu").click(function(e) {
    var $li = $(this).parent("li").toggleClass('open');
    return false;
  });
 */
  // Alert message close button handling
  $(".alter-message, .close").click(function(e) {
    $(this).parent().fadeOut();
  });

  initRequiredFields();

  setTimeout("hideAlerts()", 5000);

  $(document).controls();

  if (jQuery('#waitingPopup')){
 	jQuery('#waitingPopup').dialog2({autoOpen : false, closeOnOverlayClick : false, removeOnClose : false, showCloseHandle : false});
  }

  new EllipsisText().init();

  initMinHeightBody();
  
  $.ajaxSetup ({
	    // Disable caching of AJAX responses
	    cache: false
	});
  
});

function initMinHeightBody(){
	$("body").css("min-height", screen.availHeight);
}

$(function() {
	if($('#nav_up') && $('#nav_down'))
		var $elem = $('#FKtopContent');

		$('#nav_up').fadeIn('slow');
		$('#nav_down').fadeIn('slow');

		$(window).bind('scrollstart', function(){
			$('#nav_up,#nav_down').stop().animate({'opacity':'0.2'});
		});
		$(window).bind('scrollstop', function(){
			$('#nav_up,#nav_down').stop().animate({'opacity':'1'});
		});

		$('#nav_down').click(
			function (e) {
				$('html, body').animate({scrollTop: $elem.height()}, 800);
			}
		);
		$('#nav_up').click(
			function (e) {
				$('html, body').animate({scrollTop: '0px'}, 800);
			}
		);
});

$(function () {
	$("#pageHeaderTitle[rel=popover]")
		.popover({offset: 10, html: true})
	}
)

var hasError = false;
function initRequiredFields(){
	var controlGroupClass = ".control-group";
	$(".required-fields").bind("click", function(event) {
		  var form = null;
		  /*get the form*/
		  if($(this).attr("form-id")){
			  form = $("#" + $(this).attr("form-id"));
		  }
		  else{
			  alert("Vous devez définir un attribut form-id sur l'élément .required, correspondant à l'identifiant du formulaire");
		  }
		  hasError = false;
		  $(form).children().find(".required").each(function(i, element) {
			    var elementInputClass = $(element).parents(controlGroupClass);
				if($(element) && $(element).val().length < 1){
					if($(element).attr("required-error-text")){
						if (elementInputClass.children("span").html() == null){
							elementInputClass.prepend("<span class='help-inline'><strong>" + $(element).attr("required-error-text") + "</strong></span><br />");
						}
					}
					else{
						if (elementInputClass.children("span").html() == null){
							elementInputClass.prepend("<span class='help-inline'><strong>Element obligatoire !</strong></span><br />");
						}
					}
					$(element).parents(controlGroupClass).addClass("error");
					hasError = true;
				}
				else{
					$(element).parents(controlGroupClass).removeClass("error");
					if (elementInputClass.children("span").html() != null){
						elementInputClass.children("span").remove();
					}
				}
			});
		  if (hasError){
			  event.preventDefault();
		  }
	  });
}

function hideAlerts() {
  $(".alert").each(function()
  {
    if (!$(this).hasClass("no-fade")) {
      $(this).fadeOut();
    }

  });
}

function EllipsisText() {
  this.init = doEllipsisTextAll;
}

function doEllipsisTextAll() {
	jQuery('.ellipsisText').each(function(i) {
		doEllipsisText(this);
    });
}

function doEllipsisTextId(id) {
	var ellip = jQuery('#' + id + '.ellipsisText');
	doEllipsisText(ellip);
}

function doEllipsisTextFind(element) {
	jQuery(element).find(".ellipsisText").each(function(index, obj) {
			doEllipsisText(obj);
		});
}

function doEllipsisText(element) {
	var ellip = jQuery(element);
	//alert(ellip.html());
	if(ellip){
        jQuery(ellip).html('<span class="ellipsis_text">' + jQuery(ellip).html() + '</span>');
        var option = jQuery(ellip).attr('ellipsisTextOptions');
        if (option) {
          jQuery(ellip).ThreeDots(eval('(' + option + ')'));
        } else {
          jQuery(ellip).ThreeDots();
        }
	}
}

function popupCenter(page,largeur,hauteur,options) {
  var top=(screen.height-hauteur)/2;
  var left=(screen.width-largeur)/2;
  window.open(page,"","top="+top+",left="+left+",width="+largeur+",height="+hauteur+","+options);
}

function refreshCustomViewsContent(docType, response, ckeObj, ckeip_html) {
	if (docType === 'HtmlPage') {
		var contentId = jQuery(ckeObj).attr('id'); // s_2_r_0_c_2
		if (contentId != 'undefined') {
			var viewClassName = contentId + '_view'; // s_2_r_0_c_2_view
			jQuery('.' + viewClassName).html(ckeip_html);
		}
	}
}

function scrollToRowAfterCkeip(response, ckeObj, ckeip_html) {
    var rowId = jQuery(ckeObj).closest('div.row-fluid').attr('id');
    if (typeof rowId == 'undefined') {
        rowId = '';
    } else {
        rowId = '#' + rowId;
    }
    if (rowId != '') {
        window.location.href = rowId;
    }
}

function changeImgError() {
  	$(".imgComment").each(function(i){
  		$(this).error(function(){
			$(this).attr("src", "http://intralm2.fr.corp.leroymerlin.com/contact/skin/images/lm-man.png");
		});
  	});
}

/*
function dateAsString(date) {
    dateObj = new Date(date);
    Date.prototype.toDateString = function () {return isNaN (this) ? 'NaN' : [this.getDate() > 9 ? this.getDate() : '0' + this.getDate(), this.getMonth() > 8 ? this.getMonth() + 1 : '0' + (this.getMonth() + 1), this.getFullYear()].join('/')}
    Date.prototype.toHourString = function () {return isNaN (this) ? 'NaN' : [this.getHours() > 9 ? this.getHours() : '0' + this.getHours(), this.getMinutes() > 9 ? this.getMinutes() : '0' + this.getMinutes(), this.getSeconds() > 9 ? this.getSeconds() : '0' + this.getSeconds()].join(':')}
    return dateObj.toDateString() + ' - ' + dateObj.toHourString();
}
*/

function dateAsString(date) {
    var mois = new Array("janv.","f&eacute;v.","mars","avr.","mai","juin","juil.","ao&ucirc;t","sept.","oct.","nov.","déc.");
    dateObj = new Date(date);
    Date.prototype.toDateString = function () {return isNaN (this) ? 'NaN' : [this.getDate() > 9 ? this.getDate() : '0' + this.getDate(), mois[this.getMonth()], this.getFullYear()].join(' ')}
    return dateObj.toDateString();
}

function stopEventPropagation(evt){
	if (evt.stopPropagation) {
		evt.stopPropagation();
	}
	else {
		evt.cancelBubble = true;
	}
}

function openModifiyCSSLine(url, cssName, userClassInput){
	jQuery("#div-modifyCSSLine").dialog2('open');
	jQuery('#form-modifyCSSLine').attr('action', url + '/@modifyCSS');
	jQuery("#cssName").val(cssName);
	$("#userClassSelect").val(userClassInput).select2({
		placeholder: "Ajouter du style",
		formatNoMatches: function(trem){
				return '';
			}
		}
	);
	$("#userClassSelect").on("change", function(event) {
		$("#userClass").val($("#userClassSelect").select2("val"));
	});
}

function displayCssClass(section){
    jQuery("#displayCssClass_" + section).show();
    jQuery("#herfDisplayCssClass_" + section).hide();
}
