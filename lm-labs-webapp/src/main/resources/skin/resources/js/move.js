function moveDown(url, path, id, selector){
	var finded = false;
	var after;
	var current;
	
	jQuery(selector).each(function (index, element){
		if (finded){
			after = element;
			return false;
		}
		if (jQuery(element).attr("id") == id){
			finded = true;
			current = element;
		}
	});
	
	jQuery(current).insertAfter(after);
	
	if(jQuery(after).attr("id") != undefined){
		ajaxMove2(url + "/moveDownElement", path);
	}
}

function moveUp(url, path, id, selector){
	var before;
	var current;
	
	jQuery(selector).each(function (index, element){
		if (jQuery(element).attr("id") == id){
			current = element;
			return false;
		}
		else{
			before = element;
		}
	});
	jQuery(before).insertAfter(current);
	
	if(jQuery(before).attr("id") != undefined){
		ajaxMove2(url + "/moveUpElement", path);
	}
}

function ajaxMove2(url, path){
	jQuery('#waitingPopup').dialog2('open');
	jQuery.ajax({
		type: "GET",
		url: url,
		data: '',
		success: function(msg){
			// si location.reload : ne pas fermer la fenêtre d'attente !
			//jQuery('#waitingPopup').dialog2('close');
			//TODO vérifier le doublement des ckeditor (avec annul / modif) quand  on enlève le
			// rechargement et reload, car normalement, pas besoin
			window.location.href=path;
			window.location.reload(true);
		},
		error: function(msg){
//			alert( msg.responseText );
			alert('Non sauvegardé!');
			document.location.href=path;
		}
	});
}