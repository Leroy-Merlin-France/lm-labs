

function deleteNews(url, path){
	jQuery.ajax({
		type: "DELETE",
		url: url,
		data: '',
		success: function(msg){
			document.location.href=path;
		},
		error: function(msg){
			alert( msg.responseText );
		}
	});
}

function manageDisplayEdit(){
	if ($("#editNews")[0].style.display == "block"){
		hideEdit();
	}
	else{
		initFields();
		initEditDateNews();
		displayEdit();
	}
}	    

function initEditDateNews(){
	$(function() {
		var jQueryNewsStart = $( "#newsStartPublication" );
		var jQueryNewsEnd = $( "#newsEndPublication" );
		var dates;
		var dateSelectedStartPublication = false;
		var dateNewsStartPublication = null;
		dates = jQueryNewsStart.datetimepicker({
				dateFormat: "dd/mm/yy",
				timeFormat: 'hh:mm',
				separator: ' à ',
				timeText: "Horaire",
				hourText: "Heure",
				minuteText: "Minute",
				changeMonth: true,
				numberOfMonths: 2,
				hourGrid: 4,
				minuteGrid: 10,
				firstDay: 1,
				beforeShow: function( input ) {
					dateSelectedStartPublication = false;
					dateNewsStartPublication = $(input).val();
			    },
				onSelect: function( selectedDate ) {
					dateSelectedStartPublication = true;
					dateNewsStartPublication = selectedDate;
					var option = this.id == "newsStartPublication" ? "minDate" : "maxDate";
					var	instance = $( this ).data( "datepicker" );
					var	date = $.datepicker.parseDate(
							instance.settings.dateFormat ||
							$.datepicker._defaults.dateFormat,
							selectedDate, instance.settings );
					dates.not( this ).datepicker( "option", option, date );
				},
				onClose: function( selectedDate, inst ) {
					if (!dateSelectedStartPublication && (typeof(dateNewsStartPublication)=='undefined' || dateNewsStartPublication == '')){
						$( this ).datepicker( "setDate" , null );
					}
				},
			});	
		 

	var dateSelectedEndPublication = false;
	var dateNewsEndPublication = null;
	dates.add( jQueryNewsEnd.datetimepicker({
			dateFormat: "dd/mm/yy",
			timeFormat: 'hh:mm',
			separator: ' à ',
			timeText: "Horaire",
			hourText: "Heure",
			minuteText: "Minute",
			changeMonth: true,
			numberOfMonths: 2,
			hourGrid: 4,
			minuteGrid: 10,
			firstDay: 1,
			hour: 23,
			minute: 59,
			beforeShow: function( input ) {
				dateSelectedEndPublication = false;
				dateNewsEndPublication = $(input).val();
		    },
		    onSelect: function( selectedDate ) {
		    	if(!isEqualsDateWithoutTime(selectedDate ,dateNewsEndPublication)){
		    		var dDateSelected = jQueryNewsEnd.datetimepicker("getDate");
					dDateSelected.setHours(23);
					dDateSelected.setMinutes(59);
					jQueryNewsEnd.datetimepicker("setDate", dDateSelected);
		    	}
		    	dateSelectedEndPublication = true;
		    	dateNewsEndPublication = selectedDate;
		    	var option = this.id == "newsStartPublication" ? "minDate" : "maxDate";
				var	instance = $( this ).data( "datepicker" );
				var	date = $.datepicker.parseDate(
						instance.settings.dateFormat ||
						$.datepicker._defaults.dateFormat,
						selectedDate, instance.settings );

				var dDatetmp = jQueryNewsStart.datetimepicker("getDate");
				dates.not( this ).datepicker( "option", option, date );
				jQueryNewsStart.datetimepicker("setDate", dDatetmp);
			},
			onClose: function( selectedDate, inst ) {
				if (!dateSelectedEndPublication && (typeof(dateNewsEndPublication)=='undefined' || dateNewsEndPublication == '')){
					$( this ).datepicker( "setDate" , null );
				}
			},
		})
	);
	
	
});
}

function isEqualsDateWithoutTime(date1, date2){
	if (!date1 || !date2 || date1 == '' || date2 == ''){
		return false;
	}
	return date1.substring(0, 10) == date2.substring(0, 10);
}

function displayEdit(){
	$("#editNews")[0].style.display= "block";
	$("#editNews")[0].style.overflow= "hidden";
}

function hideEdit(){
	$("#editNews")[0].style.display= "none";
}

function initFields(){
	$("#newsTitle").val("");
	$("#newsContent").val("");
	$("#newsStartPublication").val("");
	$("#newsEndPublication").val("");
	$("#newsAccroche").val("");
	$("#newsId").val("");
	CKEDITOR.instances.newsContent.setData("");
}

function initCheckeditor(){
	CKEDITOR.replace( 'newsContent' );
}

function removeCheckeditor(){
	CKEDITOR.remove(CKEDITOR.instances.newsContent);
}

var isOpen = false;
function openPropsNews(){
	jQuery("#editprops").slideDown("slow", function(){
		jQuery('.ellipsisText[rel=news]').each(function(i) {
			doEllipsisText(this);
	    });
	});
	jQuery("#btnModifyPropsNews").html("<i class='icon-eye-close'></i>Fermer les propriétés");
	isOpen = true;
}

function closePropsNews(){
	jQuery("#editprops").slideUp("slow");
	jQuery("#btnModifyPropsNews").html("<i class='icon-eye-open'></i>Modifier les propriétés");
	isOpen = false;
}

function actionPropsNews(){
	if(isOpen == false){
		openPropsNews();
	}
	else{
		closePropsNews();
	}
}

var cropCoords;
var jcrop_api;
var widthSummary;
var heightSummary;

$(document).ready(function() {
	jQuery("#divCropPicture").dialog2({
		width : '900px',
		height : '420px',
		overflowy : 'auto',
		overflowx : 'auto',
		autoOpen : false,
		closeOnOverlayClick : true,
		removeOnClose : false,
		showCloseHandle : true,
		buttons: {
			"Annuler": function() { jQuery(this).dialog2("close"); },
		    "Réinitialisation": function() { resetCropCoords(); },
		    "Valider": {
				click: function() { saveCropCoords(); },
				primary: true
			} 
		},
	});
	
	if (jQuery("#cropSummaryPicture").val() != ""){
		cropCoords = eval('(' + jQuery("#cropSummaryPicture").val() + ')');
	}
	
	jQuery("#div-modifyCSSLine").dialog2({
	    autoOpen : false,
		closeOnOverlayClick : true,
		removeOnClose : false,
		showCloseHandle : true
	  });
});

function openCropPicture(){
	jQuery("#divCropPicture").dialog2("open");
	widthSummary = jQuery("#summaryPicture").width();
	heightSummary = jQuery("#summaryPicture").height();
	$('#summaryPicture').Jcrop({
		onSelect: setCoords,
		onChange: setCoords,
		aspectRatio: 4/3,
		setSelect: extractTabCropCoords(),
		minSize: [ 120, 90 ]
	},function(){
		  jcrop_api = this;
		  setCoords(cropCoords);
	});
}

function extractTabCropCoords(){
	if (jQuery("#cropSummaryPicture").val() != ""){
		return [
				cropCoords.x,
				cropCoords.y,
				cropCoords.x2,
				cropCoords.y2
			];
	}
	else{
		var width = jQuery("#summaryPicture").width();
		var height = jQuery("#summaryPicture").height();
		return [
				(width / 2) - 60,
				(height / 2) - 45,
				(width / 2) + 60,
				(height / 2) + 45
			];
	}
}

function setCoords(c){
	cropCoords = c;
	var rx = 120 / cropCoords.w;
	var ry = 90 / cropCoords.h;

	$('#summaryPicturePreview').css({
		width: Math.round(rx * widthSummary) + 'px',
		height: Math.round(ry * heightSummary) + 'px',
		marginLeft: '-' + Math.round(rx * cropCoords.x) + 'px',
		marginTop: '-' + Math.round(ry * cropCoords.y) + 'px'
	});
};

function saveCropCoords(){
	jQuery("#cropSummaryPicture").val(JSON.stringify(cropCoords));
	jQuery("#divCropPicture").dialog2("close");
	jQuery('#waitingPopup').dialog2('open');
	jQuery("#form-editNews").submit();
}

function resetCropCoords(){
	if (jQuery("#cropSummaryPictureOrigin").val() != ""){
		cropCoords = eval('(' + jQuery("#cropSummaryPictureOrigin").val() + ')');
	}
	else{
		cropCoords = "";
	}
	jcrop_api.animateTo(extractTabCropCoords());
}

$(document).ready(function() {
	jQuery("#divDownloadPicture").dialog2({
		autoOpen : false,
		buttons: {
			"Annuler": function() { jQuery(this).dialog2("close"); }
		},
	});
});

function openDownloadPicture(){
	jQuery("#divDownloadPicture").dialog2("open");
	jQuery("#form-downloadPicture").clearForm();
}

function savePicture(){
	jQuery("#newsPicture").appendTo('#form-editNews');
	jQuery("#divDownloadPicture").dialog2("close");
	jQuery('#waitingPopup').dialog2('open');
	jQuery("#form-editNews").submit();
}

function deleteSummaryPicture(message){
	if (confirm(message)){
		jQuery('#waitingPopup').dialog2('open');
		jQuery("#formDeleteSummaryPicture").submit();
	}
}

function deleteTemplatePreviewBlob(labsNewsId, serverUrl, msgConfirm) {
	if (confirm(msgConfirm)) {
		jQuery('#waitingPopup').dialog2('open');
	    var operation = 'Blob.Remove';
	    var d = {"params":{"xpath":"let:preview"},"input":"doc:"+labsNewsId,"context":{}};
		jQuery.ajax({
			type: "POST",
			url: serverUrl + '/automation/' + operation,
	        async: false,
	        contentType: 'application/json+nxrequest',
	        data: JSON.stringify(d),
	        success: function(data, textStatus, jqXHR) {
	        	jQuery('#divElementPreview').hide()
				jQuery('#waitingPopup').dialog2('close');
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert( textStatus + ':' + errorThrown );
				jQuery('#waitingPopup').dialog2('close');
			}
			/*
	        ,complete: function(jqXHR, textStatus) {alert('complete');}
			 */
		});
	}
}
