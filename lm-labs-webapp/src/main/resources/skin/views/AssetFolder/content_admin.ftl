<#-- TODO : externalize style -->

<style>
	.emptyMsg {
		font-style:italic;
	}
	.assetVignette {
		height:140px;
		width:125px;
		border:1px groove #DDDDDD;
		border-radius:4px;
		box-shadow: 0 1px 1px rgba(0, 0, 0, 0.075);
		padding: 4px;
		float:left;
		margin:0.4em;
		word-wrap:break-word;
		cursor: pointer;
		padding-right: 10px;
	}
	.assetVignette:hover {
		background: #BEEBFF;
		border: 1px solid #99DEFD;
	}
	.imgVignette {
		float: left;
	}
</style>
<script type="text/javascript" >
jQuery(document).ready(function() {
	jQuery('a.open-fancybox').fancybox(
	{
        'width'				: '75%',
<#--
        'autoScale'			: true,
        'height'			: '75%',
        'transitionIn'		: 'none',
        'transitionOut'		: 'none',
        'type'				: 'iframe',
        'enableEscapeButton': true,
-->
        'centerOnScroll': true
	}
	);
	jQuery('a.open-fancybox.fancybox-image').fancybox(
	{
        'width'				: '75%',
        'type'          : 'image',
        'centerOnScroll': true
	}
	);
	jQuery.each(jQuery('div.player-button'), function() {
		jQuery(this).load(jQuery(this).data('viewurl'));
    });
});
</script>

<#assign children=Session.getChildren(Document.ref) />
<#if children?size==0>
	<div id="contentAdminPictures" class="thumbnails" style="width: 664px;text-align:center;margin-left: -15px;">
 		<span class="emptyMsg">${Context.getMessage("label.admin.asset.noPicture")}</span>
 	</div>
<#else>
	<div id="contentAdminPictures" class="thumbnails" style="width: 664px;margin-left: -15px;">
		<#assign hasPicture="0" />
		<#list children as doc>
		  <#if !doc.isFolder >
		  <#--We affect the same 'id' attribute to all element in this div, because JsTree needs -->
		  <div id="${doc.id}" data-docid="${doc.id}" class="assetVignette jstree-draggable" title="${doc.name}">
		    <div id="divimg-${doc.id}" class="pull-left" style="min-height: 70%;" >
		    	<#if doc.type == "Picture" >
		      <a class="open-fancybox fancybox-image" href="${This.path}/@assets/id/${doc.id}/@blob">
				</#if>
		      <img id="img-${doc.id}" src="/nuxeo/nxpicsfile/default/${doc.id}/Thumbnail:content/any_value" class="imgVignette jstree-draggable" />
		    	<#if doc.type == "Picture" >
		      </a>
				</#if>
		    </div>
		    <div class="pull-right actions" style="width:22px;" >
		    	<#if doc.type == "Picture" >
					<a class="btn btn-mini" onclick="rotatePictureCW('${doc.id}');" title="${Context.getMessage("label.admin.asset.rotateCW")}" ><i class="icon-repeat"></i></a>
					<a class="btn btn-mini" onclick="rotatePictureCCW('${doc.id}');" title="${Context.getMessage("label.admin.asset.rotateCCW")}" ><i class="icon-undo"></i></a>
				</#if>
				<a class="btn btn-mini btn-primary" href="${This.path}/@assets/id/${doc.id}/@blob" title="Télécharger" ><i class="icon-download-alt"></i></a>
				<a class="btn btn-mini btn-danger" onclick="deletePicture('${doc.id}');" title="${Context.getMessage("label.admin.asset.delete")}" ><i class="icon-remove"></i></a>
				<#if doc.facets?seq_contains("HasStoryboard") >
					<a class="btn btn-mini btn-info open-fancybox" href="${This.path}/@assets/id/${doc.id}/@labsvideo/@views/video_info_popup" title="${Context.getMessage('heading.video.info')}" ><i class="icon-film"></i></a>
					<div class="player-button" data-viewurl="${Context.modulePath}/${Common.siteDoc(Document).site.URL}/@assets/id/${doc.id}/@labsvideo/@views/player_button" >
					</div>
				</#if>
		    </div>
			<div style="clear:both"></div>
		    <div id="ellipsis-${doc.id}" class="ellipsisText" rel="adminAsset" ellipsisTextOptions="{max_rows:2, alt_text_e:true, alt_text_t:true}" style="width:125px;margin-top:10px" >${doc.title?html}</div>
		  </div>
		  <#assign hasPicture="1" />
		  </#if>
		</#list>
		<#if hasPicture=="0">
			<div style="text-align:center">
				<span class="emptyMsg">${Context.getMessage("label.admin.asset.noPicture")}</span>
			</div>
		</#if>
	</div>
</#if>

