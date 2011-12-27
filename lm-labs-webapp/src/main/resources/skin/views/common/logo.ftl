<#assign logoWidth = site.themeManager.theme.logoWidth />
<#assign canDrop = Session.hasPermission(Document.ref, 'Everything') || Session.hasPermission(Document.ref, 'ReadWrite')/>


<#if logoWidth &gt; 0>
	<#if canDrop>
		<script type="text/javascript">
		
			<#include "/resources/js/dragHtmlElt.js" />
		
			function updateLogoXY() {
				jQuery.ajax({
						async : false,
						type: 'POST',
						url: "${Context.modulePath}/${site.URL}/@theme/${site.themeManager.theme.name}/logoXY",
						data : {
							"posX" : dragDrop.draggedObject.offsetLeft,
							"posY" : dragDrop.draggedObject.offsetTop
						},
						success : function (r) {
							//alert(r);
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
		style="left:${site.themeManager.theme.logoPosX}px;top:${site.themeManager.theme.logoPosY}px;width:${logoWidth}px;" 
		<#if canDrop>
			class="logoImgId-move"
		</#if>
		src="${Context.modulePath}/${Common.siteDoc(Document).site.URL}/@theme/${site.themeManager.theme.name}/logo" />
	
	<#if canDrop>
		<div 
			id="logoDragMsgId" 
			class="logoDragMsg"
			style="top:${site.themeManager.theme.logoPosY+4}px;left:${site.themeManager.theme.logoPosX}px;width:${logoWidth+46}px;">
	        	<center><span>${Context.getMessage("label.labssites.appearance.theme.edit.drag_n_drop")}<span></center>
	    </div>
	</#if>
</#if>
