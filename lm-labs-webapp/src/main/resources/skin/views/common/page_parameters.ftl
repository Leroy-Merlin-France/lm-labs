<#if This.page??>
<#assign canHidePage = (mySite.homePageRef != This.page.document.id) />
<#include "views/common/template_description_js.ftl">
	<style>
		#form_editParameters .input {
			width: 45%;
		}
			
		#form_editParameters .input input[type="checkbox"] {
			margin-top: 6px;
			float: left;
		}
			
		#form_editParameters .input label {
			text-align: left;
			width: 90%;
		}
	</style>
	
	<h1>${Context.getMessage('label.parameters.page.title')}</h1>
	<div id="divEditParametersPageForm">
		<form class="form-horizontal" id="form_editParameters" action="${This.path}/@managePage" method="post" enctype="multipart/form-data">
			<div class="control-group">
				<label class="control-label" for="updateTitlePage">${Context.getMessage('label.parameters.page.updateTitlePage')}</label>
				<div class="controls">
					<input id="updateTitlePage" type="text" name="updateTitlePage" value="${This.document.title?html}" maxlength="90"/>
				</div>
			</div>
			<div class="control-group">
				<div class="controls">
					<label class="checkbox" for="publishPage">
					<input class="checkbox" id="publishPage" type="checkbox" name="publishPage" <#if This.page.visible>checked="true"</#if> />
					${Context.getMessage('label.parameters.page.publishPage')}</label>
				</div>
            </div>
            <div class="control-group">
				<div class="controls">
					<label class="checkbox" for="commentablePage">
					<input class="checkbox" id="commentablePage" type="checkbox" name="commentablePage" <#if This.page.commentable >checked="true"</#if> />
					${Context.getMessage('label.parameters.page.authorizedCommentable')}</label>
				</div>
            </div>
            <div class="control-group">
				<div class="controls">
					<label class="checkbox" for="${This.DC_TITLE}">
					<input class="checkbox" id="${This.DC_TITLE}" type="checkbox" name="${This.DC_TITLE}" value="${This.DC_TITLE}" <#if This.page.isDisplayable(This.DC_TITLE) >checked="true"</#if> />
					${Context.getMessage('label.parameters.page.displayableTitlePage')}</label>
				</div>
            </div>
            <div class="control-group">
				<div class="controls">
					<label class="checkbox" for="${This.DC_DESCRIPTION}">
					<input class="checkbox" id="${This.DC_DESCRIPTION}" type="checkbox" name="${This.DC_DESCRIPTION}" value="${This.DC_DESCRIPTION}" <#if This.page.isDisplayable(This.DC_DESCRIPTION) >checked="true"</#if> />
					${Context.getMessage('label.parameters.page.displayableDescriptionPage')}</label>
				</div>
			</div>
			<#if Document.type == "HtmlPage" || Document.type == "PageClasseur" >
            <div class="control-group">
				<div class="controls">
					<label class="checkbox" for="display-pg:collapseType">
					<input class="checkbox" type="checkbox" id="display-pg:collapseType" name="display-pg:collapseType"
						<#if This.page.isDisplayable("pg:collapseType") >checked="true"</#if> />
					${Context.getMessage('label.parameters.' + Document.type + '.displayableCollapseType')}</label>
				</div>
			</div>
<#-- A GARDER
            <div class="control-group"<#if !This.page.isDisplayable("pg:collapseType") > style="display:none;"</#if> >
              <label class="control-label" for="pg:collapseType">${Context.getMessage('label.parameters.' + Document.type + '.collapseType')}</label>
              <div class="controls">
                <select name="pg:collapseType" class="span4" >
                    <#assign collapseTypesList = Common.collapseTypesList />
                    <#assign currentCollapseType = This.page.collapseType />
                    <#list collapseTypesList?sort as collapseType>
                        <option value="${collapseType}" <#if currentCollapseType == collapseType >selected</#if>>${Context.getMessage('label.parameters.' + Document.type + '.collapseType.' + collapseType)}</option>
                    </#list>
                </select>
              </div>
            </div>
-->
			</#if>
			<#if Document.type == "PageList" || Document.type == "PageNews" || Document.type == "PageNav" >
	            <div class="control-group">
					<div class="control-label" for="elementsPerPage">${Context.getMessage('label.parameters.page.elementsPerPage')}</div>
					<div class="controls">
						<input class="input-mini" id="elementsPerPage" type="input" name="elementsPerPage" value="${This.page.elementsPerPage}" />
						<p class="help-block">${Context.getMessage('label.parameters.page.elementsPerPage.without')}</p>
					</div>
				</div>
			</#if>
            <div class="control-group">
              <label class="control-label" for="template">${Context.getMessage('label.labssites.appearance.template.label')}</label>
              <div class="controls">
                <select name="template" id="template" class="span4" onchange="updateTemplateDescription(this, 'template-description');" >
                    <#include "views/common/getTemplatesMap.ftl">
                    <#assign templatesMap = getTemplatesMap() />
                    <#assign documentTemplateName = This.page.template.documentTemplateName />
                    <#list templatesMap?sort_by('title') as template>
                        <option value="${template.name}" <#if documentTemplateName == template.name >selected</#if>>${template.title}</option>
                    </#list>
                    <option value="" <#if documentTemplateName == "" >selected</#if>>${Context.getMessage('label.labssites.appearance.templates.none')}</option>
                </select>
                <#assign selectedTemplateName = documentTemplateName />
                <#if selectedTemplateName == "" >
                    <#assign selectedTemplateName = "none" />
                </#if>
                <p id="template-description" class="help-block"><small>${Context.getMessage('label.labssites.appearance.templates.' + selectedTemplateName + '.description')}</small></p>
                <p class="help-block">${Context.getMessage('label.labssites.appearance.template.help.block')}</p>
              </div>
            </div>
            <#if canHidePage >
            <div class="control-group">
				<div class="controls">
					<label class="checkbox" for="hiddenInLabsNavigation">
					<input class="checkbox" type="checkbox" id="hiddenInLabsNavigation" name="hiddenInLabsNavigation"
						<#if This.page.isHiddenInNavigation() >checked="true"</#if> />
					${Context.getMessage('label.parameters.page.hideInLabsNavigation')}</label>
				</div>
			</div>
            </#if>
            <#if Document.type == "HtmlPage" >
	            <div class="control-group">
	              <label class="control-label" for="contentView">${Context.getMessage('label.parameters.HtmlPage.contentView')}</label>
	              <div class="controls">
	                <select name="contentView" id="contentView" class="span4" >
	                    <#assign contentViews = Common.getPageContentViews(Document.type) />
	                    <#assign documentContentView = This.contentView />
	                    <#list contentViews as contentView>
	                        <option value="${contentView}" <#if documentContentView == contentView >selected</#if>>${Context.getMessage('label.page.contentview.' + contentView)}</option>
	                    </#list>
	                </select>
	              </div>
	            </div>
            </#if>
            <#if Document.type == "PageForum" >
            	<div class="control-group">
					<div class="controls">
						<label class="checkbox" for="${This.TOPIC_NOT_ALL_CONTRIBUTOR}">
						<input class="checkbox" id="${This.TOPIC_NOT_ALL_CONTRIBUTOR}" type="checkbox" name="${This.TOPIC_NOT_ALL_CONTRIBUTOR}" value="${This.TOPIC_NOT_ALL_CONTRIBUTOR}" <#if This.page.isDisplayable(This.TOPIC_NOT_ALL_CONTRIBUTOR) >checked="true"</#if> />
						${Context.getMessage('label.parameters.topic.allContributor')}</label>
					</div>
	            </div>
            </#if>
            <#if mySite?? && mySite.isAdministrator(Context.principal.name)>
	            <#--  To define as template   -->
	            <#assign page = This.page />
	            <div class="control-group">
					<div class="controls">
				    	<label class="checkbox" for="pageTemplate">
				        <input class="checkbox" id="pageTemplate" type="checkbox" name="let:elementTemplate" <#if page.elementTemplate>checked="true"</#if> />
				        &nbsp;${Context.getMessage('label.parameters.page.template')}</label>
				    </div>
				</div>
				<div class="control-group" id="pageTemplatePreviewDiv" <#if !page.elementTemplate>style="display:none;"</#if>>
					<#if page.hasElementPreview() >
				    	<div id="divElementPreview" style="float: right; margin-right: 25px;" >
					        <img style="width:400px;cursor:pointer;"
					          title="${Context.getMessage('label.element.template.preview.delete')}" 
					          onclick="if (confirm('${Context.getMessage('label.element.template.preview.delete.confirm')?js_string}')){jQuery('#waitingPopup').dialog2('open'); jQuery.ajax({url:'${This.path}/@blob', type:'DELETE', success:function() {jQuery('#divElementPreview').hide();jQuery('#waitingPopup').dialog2('close');}, error:function(data){jQuery('#waitingPopup').dialog2('close');}});}"
					          src="${This.path}/@blob"/>
				      	</div>
				    </#if>
				    <label class="control-label" for="siteTemplatePreview">${Context.getMessage('label.parameters.page.preview')}</label>
				    <div class="controls">
				        <input name="let:preview" type="file" size="25" id="pagePreview" />
				    </div>
				</div>
			</#if>
		</form>
		<hr />
		${Context.getMessage('label.parameters.page.usedModel')} <strong>${Context.getMessage('label.doctype.'+This.document.type)}</strong>
	</div>
	<div class="actions">
	    <button id="page-parameters-submit" class="btn btn-primary" >${Context.getMessage('label.parameters.page.save')}</button>
	    <button id="page-parameters-close" class="btn" >${Context.getMessage('label.parameters.page.cancel')}</button>
	</div>
</#if>
<script>
<#-- A GARDER
<#if This.page?? >
	<#if Document.type == "HtmlPage" >
function showHideCollapseTypeSelect(checkboxObj, selectDivObj) {
	if (jQuery(checkboxObj).is(':checked')) {
		jQuery(selectDivObj).show();
	}
	else {
		jQuery(selectDivObj).hide();
	}
}
	</#if>
</#if>
-->
jQuery(document).ready(function() {
<#if This.page?? >
<#-- A GARDER
	<#if Document.type == "HtmlPage" >
	var selectCollapseTypeDivObj = jQuery('select[name="pg:collapseType"]').closest('div.control-group');
	var collapseTypeCheckboxObj = jQuery('input[name="display-pg:collapseType"]');
	jQuery(collapseTypeCheckboxObj).change(function() {
		showHideCollapseTypeSelect(this, selectCollapseTypeDivObj);
	});
	showHideCollapseTypeSelect(collapseTypeCheckboxObj, selectCollapseTypeDivObj);
	</#if>
-->
    jQuery("#page-parameters-submit").click(function() {
        submitParametersPage();
    });
    jQuery("#page-parameters-close").click(function() {
        closeParametersPage();
    });
    jQuery('#pageTemplate').click(function() {
		if (jQuery(this).is(':checked')) {
			jQuery('#pageTemplatePreviewDiv').show();
		} else {
			jQuery('#pageTemplatePreviewDiv').hide();
		}
	});
</#if>
});
</script>
