$(document).ready(function() {
    jQuery(".sections-edition").dialog2({
        autoOpen : false,
        closeOnOverlayClick : false
    });
  prettyPrint();

  jQuery("#div-modifyCSSLine").dialog2({
    autoOpen : false,
	closeOnOverlayClick : true,
	removeOnClose : false,
	showCloseHandle : true
  });
  
  var executedDoEllipsisTabbed_divSections = new Array();
	jQuery('#tabbed_divSections').find('a[data-toggle="tab"]').on('shown', function (e) {
        var pattern=/#.+/gi //use regex to get anchor(==selector)
        var contentID = e.target.toString().match(pattern)[0];   
        //Refresh ellipsis
        if (!executedDoEllipsisTabbed_divSections[contentID]){
        	doEllipsisTextFind(jQuery(contentID));
        	executedDoEllipsisTabbed_divSections[contentID] = true;
        }
	});
	
	var executedDoEllipsisCarousel_divSections = new Array();
	jQuery('#carousel_divSections').bind('slid', function() {
		var carouselActiveItem = jQuery(this).find('div.item.active');
		initOpensocialGadgets(carouselActiveItem);
		initHtmlWidgets(carouselActiveItem);
        //Refresh ellipsis 
		var contentID = jQuery(carouselActiveItem).attr("id"); 
        if (!executedDoEllipsisCarousel_divSections[contentID]){
        	doEllipsisTextFind(jQuery("#"+contentID));
        	executedDoEllipsisCarousel_divSections[contentID] = true;
        }
	});
	
});


function initWidgets(parentObj) {
	initOpensocialGadgets(jQuery(parentObj));
}

