
	<div class="viewblock">${Document.dublincore.description}</div>
		
	<div class="well editblock toc-noreplace" id="pageDescription">
		<div id="description">${Document.dublincore.description}</div>
		<script type="text/javascript">
function reloadPageForTocIfNeeded(response, ckeObj, ckeip_html) {
<#if This.type.name == "PageClasseur" || This.type.name == "HtmlPage" >
    if (ckeip_html.indexOf('[[TOC]]') != -1) {
        jQuery('#waitingPopup').dialog2('open');
        window.location.reload();
    }
</#if>
    jQuery(ckeObj).closest('div.row').children('.viewblock').html(ckeip_html);
    return false;
}
		</script>
		<script type="text/javascript">
		  <#-- detect error when loading picture -->
		  $('#description').find('img').each(function() {
		  	$(this).error( function() { 
		  		$(this).attr('style', ''); 
		  		$(this).attr('src', '/nuxeo/icons/image_100.png'); 
		  	});
		  });
		   <#-- load CKEIP plugin -->
	      $('#description').ckeip({
	      	<#-- @put (the nuxeo way to update description) seems to not work with ckeditor
	        e_url: '${This.path}/@put', -->
	        e_url: '${This.path}/updateDescriptionCKEIP',
	        ckeditor_config: ckeditorconfig,
	        view_style: "cke_hidden",
	        emptyedit_message: "${Context.getMessage('label.ckeditor.double_click_to_edit_content')}"<#if (!This.page.isDisplayable(This.DC_DESCRIPTION))>,
	        display_ckeipTex: false</#if>
	        }, reloadPageForTocIfNeeded);
	    </script>
	</div>
