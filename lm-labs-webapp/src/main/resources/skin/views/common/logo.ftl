<#assign logoWidth = site.themeManager.theme.logoWidth />
<#assign canDrop = Session.hasPermission(Document.ref, 'Everything') || Session.hasPermission(Document.ref, 'ReadWrite')/>

<#if canDrop>
	<#assign logoBorderPadding=0 />
<#else>
	<#assign logoBorderPadding=23 />
</#if>

<#if logoWidth &gt; 0>
	<#if canDrop>
		<script type="text/javascript">
		
			<#include "/resources/js/dragHtmlElt.js" />
		
			function updateLogoXY(posX, posY) {
				jQuery('#waitingPopup').dialog2('open');
				jQuery.ajax({
						async : false,
						type: 'POST',
						url: "${Context.modulePath}/${site.URL}/@theme/${site.themeManager.theme.name}/logoXY",
						data : {
							"posX" : posX,
							"posY" : posY
						},
						success : function (r) {
							//alert(r);
							jQuery('#waitingPopup').dialog2('close');
				        },
						error : function (r) {
							jQuery('#waitingPopup').dialog2('close');
				        }
				  });
			}
			
			$(document).ready(function() {
		    	dragDrop.initElement(
		    		'logoImgId', 
		    		'logoDragMsgId', 
		    		'labssite-logo-move', 
		    		'${Context.getMessage("label.labssites.appearance.theme.edit.logo.update.confirm")}',
		    		updateLogoXY
		    	);
			});
		</script>
	</#if>

	<img
		id="logoImgId"
		style="left:${(site.themeManager.theme.logoPosX+logoBorderPadding)?string("########")}px;top:${(site.themeManager.theme.logoPosY+logoBorderPadding)?string("########")}px;width:${logoWidth}px;" 
		<#if canDrop>
			class="logoImgId-move"
		<#else>
            class="logoImgId-notmove"
		</#if>
		src="${Context.modulePath}/${Common.siteDoc(Document).site.URL}/@theme/${site.themeManager.theme.name}/logo" />
	
	<#if canDrop>
		<div 
			id="logoDragMsgId" 
			class="logoDragMsg"
			style="top:${(site.themeManager.theme.logoPosY+logoBorderPadding-14)?string("########")}px;left:${(site.themeManager.theme.logoPosX+logoBorderPadding)?string("########")}px;width:${logoWidth}px;">
	        	<span>${Context.getMessage("label.labssites.appearance.theme.edit.drag_n_drop")}</span>
	    </div>
	</#if>
</#if>
