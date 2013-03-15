var homePageId = '<#if adminTreeviewType == "Pages" && mySite?? && mySite != null >${mySite.homePageRef}</#if>';
function changeIconOfHomePage() {
	var insObj = jQuery('li#' + homePageId + ' > a > ins.jstree-icon:eq(0)');
	jQuery(insObj).css('background-image', 'url(/nuxeo/icons/home.gif)');
	jQuery(insObj).css('background-position', '0 0');
}

function getLabelHtml(state, isPageTemplate) {
	var labelHtml = '';
	if (state == 'draft') {
		labelHtml = '<span class="label label-success">${Context.getMessage('label.status.draft')}</span>';
	} else if (state == 'deleted') {
		labelHtml = '<span class="label label-warning">${Context.getMessage('label.status.deleted')}</span>';
	}
	if (isPageTemplate == true) {
		labelHtml = labelHtml + '<span class="label label-info" style="margin-left: 3px;">${Context.getMessage('label.status.page.template')}</span>';
	}
	if (labelHtml.length > 0){
		labelHtml = '<ins style="margin-left: 3px;">' + labelHtml + '</ins>';
	}
	return labelHtml;
}

function appenStatusLabel(ahref, state, isPageTemplate) {
	var label = getLabelHtml(state, isPageTemplate);
	if (label.length > 0 && !jQuery(ahref).html().match(label + '$')) {
		jQuery(ahref).append(getLabelHtml(state, isPageTemplate));
	}
}

function addNodesStatusLabels(data) {
<#--
	//console.log('<addNodesStatusLabels> ' + data.inst);
-->
	jQuery.each(data.args, function (i, node) {
	<#--
		//console.log(i + '+++' + jQuery(node).html());
	-->
		data.inst._get_children(node)
		jQuery.each(data.inst._get_children(node), function (index, subnode) {
		<#--
			//console.log('  ' + index + '+++' + jQuery(subnode).children('a').html());
		-->
			appenStatusLabel(
				jQuery(subnode).children('a:first'),
				data.inst._get_node(subnode).data("lifecyclestate"),
				data.inst._get_node(subnode).data("isPageTemplate")
			);
		});
	});
}

function addStatusLabels(data) {
<#--
	//console.log('<addStatusLabels> ' + data.inst);
-->
	hrefs = jQuery('div.jstree li > a');
	jQuery.each(hrefs, function(i, href) {
	<#--
		//console.log(i + '---' + jQuery(href).html());
		//console.log(i + '---' + data.inst._get_node(jQuery(href).parent()).data("lifecyclestate"));
		//console.log(i + '---' + getLabelHtml(data.inst._get_node(jQuery(href).parent()).data("lifecyclestate")));
	-->
		appenStatusLabel(
			href,
			data.inst._get_node(jQuery(href).parent()).data("lifecyclestate"),
			data.inst._get_node(jQuery(href).parent()).data("isPageTemplate")
		);
	});
}

