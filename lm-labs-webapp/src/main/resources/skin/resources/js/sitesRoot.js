function extractSortValue(node) {
    // extract data from markup and return it
    var sortValues = jQuery(node).find('span[class=sortValue]');
    if (sortValues.length > 0) {
        return sortValues[0].innerHTML;
    }
    return node.innerHTML;
}

function initTablesSorters() {
	initTablesSorter(jQuery("table[class*='table-striped']"));
}

function initTablesSorter(sitesTables) {
    jQuery.each(jQuery(sitesTables), function(i, val) {
        if (jQuery(val).hasClass('hasCategoryColumn') && jQuery(val).hasClass('hasDeleteColumn')) {
            jQuery(val).tablesorter({
                headers: { 4: { sorter: false}, 5: { sorter: false}},
                sortList: [[0,0]],
                textExtraction: extractSortValue
            });
        } else if (jQuery(val).hasClass('hasCategoryColumn') && !jQuery(val).hasClass('hasDeleteColumn')) {
            jQuery(val).tablesorter({
                headers: { 4: { sorter: false}},
                sortList: [[0,0]],
                textExtraction: extractSortValue
            });
        } else if (jQuery(val).hasClass('hasCategoryColumn') || jQuery(val).hasClass('hasDeleteColumn')) {
            jQuery(val).tablesorter({
                headers: { 3: { sorter: false}, 4: { sorter: false}},
                sortList: [[0,0]],
                textExtraction: extractSortValue
            });
        } else if (jQuery(val).attr('id') == 'MyDeletedSites') {
            jQuery(val).tablesorter({
                headers: { 3: { sorter: false}, 4: { sorter: false}},
                sortList: [[0,0]],
                textExtraction: extractSortValue
            });
        } else {
            jQuery(val).tablesorter({
                headers: { 3: { sorter: false}},
                sortList: [[0,0]],
                textExtraction: extractSortValue
            });
        }
    });
}

function refreshAllTabs() {
	jQuery.each(jQuery('div.tab-content div.tab-pane'), function(i, val) {
		jQuery(this).load(jQuery(this).data('view-url'), function() {
        	var table = jQuery(this).find('table');
	        initTablesSorter(table);
	        if (jQuery(this).hasClass('active')) {
	        	jQuery(this).closest('div.tab-content').siblings('ul.nav-tabs').find('li.active a span.badge').html(jQuery(table).find('tbody tr').size());
	        }
		});
	});
	jQuery('ul.nav-tabs').find('li.active a span.badge').html(jQuery('div.tab-content div.active').find('tbody tr').size());
    jQuery('ul.nav-tabs').find('li:not(.active) a span.badge').html('');
}

jQuery(document).ready(function(){
	initTablesSorters();
	$(".dialog2").each(function() {
		$(this).dialog2({
			showCloseHandle : true,
			removeOnClose : false,
			closeOnOverlayClick : false,
			autoOpen : false
		});
	});
  
  jQuery.pnotify.defaults.history = false;
  jQuery('ul.nav-tabs').find('li.active a span').html(jQuery('div.tab-content div.active').find('tbody tr').size());
  jQuery('#sitesTabs').find('a[data-toggle="tab"]').on('shown', function (e) {
	  var pattern=/#.+/gi //use regex to get anchor(==selector)
		  var contentID = e.target.toString().match(pattern)[0];
	  jQuery(contentID).load(jQuery(contentID).data('view-url'), function() {
		  var table = jQuery(contentID).find('table');
		  initTablesSorter(table);
		  var navTabs = jQuery(contentID).closest('div.tab-content').siblings('ul.nav-tabs');
		  jQuery(navTabs).find('li.active a span.badge').html(jQuery(table).find('tbody tr').size());
		  jQuery(navTabs).find('li:not(.active) a span.badge').html('');
	  });
  });

  $(".open-dialog").click(function(event) {
    event.preventDefault();
    dialogId = $(this).attr("dialog");
    $(dialogId).dialog2("open");
  });
});