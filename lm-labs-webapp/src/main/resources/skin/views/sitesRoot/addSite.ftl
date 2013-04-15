<style>
#form-labssite .input input[type="checkbox"] {
	margin-top: 6px;
	float: left;
}
	
#form-labssite .input label {
	text-align: left;
	width: 90%;
}
.radioSiteTemplate {
	display:block;
}
</style>

<h1>${Context.getMessage('label.labssite.add.site')}</h1>

<form class="form-horizontal" method="post" enctype="multipart/form-data" id="form-labssite" action="${This.path}/"> <#-- trailing slash in form's URL is very important, DONT'T REMOVE !! -->
  <fieldset>
    <div class="control-group">
      <label class="control-label" for="labsSiteTitle">${Context.getMessage('label.labssite.edit.title')}</label>
      <div class="controls">
        <input class="focused required input" name="dc:title" id="labsSiteTitle" required-error-text="${Context.getMessage('label.labssites.edit.required.title')}"/>
      </div>
    </div>
    
<#include "/macros/LabsSiteInputUrl.ftl" />
<@LabsSiteInputUrl />

    <div class="control-group">
      <label class="control-label" for="labsSiteDescription">${Context.getMessage('label.labssite.edit.description')}</label>
      <div class="controls">
        <textarea class="input" name="dc:description" id="labsSiteDescription" ></textarea>
      </div>
    </div>

    <div class="control-group">
      <label class="control-label" for="labsSiteCategory">${Context.getMessage('label.labssite.edit.category')}</label>
      <div class="controls">
        <select class="input" name="labssite:category" id="labsSiteCategory" >
        	<#assign labsCategories = Common.getCategories()/>
        	<#list labsCategories as category>
        		<#assign underLabsCategories = Common.getChildrenCategories(category)/>
        		<#if (underLabsCategories?size > 0)>
        			<optgroup label="${category.labscategory.label?html}">
		        		<#list underLabsCategories as underCategory>
		        			<option value="${underCategory.labscategory.label}">${underCategory.labscategory.label}</option>
		        		</#list>
		        	</optgroup>
		        <#else>
		        	<option value="${category.labscategory.label}">${category.labscategory.label}</option>
	        	</#if>
        	</#list>
        </select>
      </div>
    </div>

<#--
    <div class="control-group">
      <label class="control-label" for="piwik:piwikId">${Context.getMessage('label.labssite.edit.piwikId')}</label>
      <div class="controls">
        <input class="input" name="piwik:piwikId" id="piwik:piwikId" />
      </div>
    </div>
-->

	<#if Common.canCreateTemplateSite(Context.principal.name) >
    <div class="control-group">
      <div class="controls">
        <label class="checkbox" for="siteTemplate">
          <input id="siteTemplate" type="checkbox" name="labssite:siteTemplate" />
        &nbsp;${Context.getMessage('label.labssite.edit.siteTemplate')}</label>
      </div>
    </div>
    </#if>

    <div class="control-group" id="siteTemplateRadioDiv">
      <label class="control-label" for="siteTemplate">&nbsp;${Context.getMessage('label.labssite.edit.siteTemplateName')}</label>
      <div class="controls">
        <span class="radioSiteTemplate">
          <input class="radio" type="radio" name="siteTemplateId" value="" onclick="cleanPreview();" checked="checked" ></input>
          <span onclick="clickParentInput(this);" >${Context.getMessage('label.labssite.edit.siteTemplateName.none')}</span>
        </span>
        <#list Common.getTemplateSites() as templateName >
        <span class="radioSiteTemplate">
          <input class="radio" type="radio" name="siteTemplateId" value="${templateName.document.id}" 
        	<#if templateName.hasElementPreview()>
        	onclick="onSelectRadio(this, '${Context.modulePath}/@templatePreview/${templateName.URL}');"
        	<#else>
        	onclick="cleanPreview();"
        	</#if>
        	>
          </input>
          <span onclick="clickParentInput(this);" rel="popover" data-trigger="hover" data-content="${templateName.document['dc:description']?html}" data-original-title="${Context.getMessage('label.description')}">${templateName.document['dc:title']}</span>
        </span>
        </#list>
      </div>
    </div>

    <div class="control-group">
      <div id="templatePreview" ></div>
    </div>

	<div class="control-group" id="siteTemplatePreviewDiv" style="display:none;" >
      <label class="control-label" for="siteTemplatePreview">${Context.getMessage('label.labssite.edit.siteTemplatePreview')}</label>
      <div class="controls">
        <input class="input-file" name="labssite:siteTemplatePreview" type="file" size="30" id="siteTemplatePreview" />
      </div>
    </div>
  </fieldset>
  <div class="actions">
    <button class="btn btn-primary required-fields" form-id="form-labssite">${Context.getMessage('label.labssites.edit.valid')}</button>
    <a class="btn close-dialog" href="#" >Annuler</a>
  </div>
</form>
<script>
function callBackAddSite(data) {
	if (data == "site added"){
		document.location.href = '${This.path}/' + jQuery('#labsSiteURL').val();
	}
	else{
		jQuery('#waitingPopup').dialog2('close');
		jQuery.pnotify({type: 'error', hide: false, title: "", text: data});
		window.setTimeout(function() {
			 document.location.href = '${This.path}/';
			}, 5000);
	}
}

function beforeSubmitCheckSiteUrl() {
   	jQuery('#waitingPopup').dialog2('open');
	if (jQuery('#urlAvailability').val() !== 'true') {
		var ok = verifyUrlAvailability('${Context.modulePath}/@urlAvailability', function() {setCheckUrlButton('complete');}, function() {jQuery('#urlAvailability').val('false');setCheckUrlButton('failed');});
		if (ok == false) {
			jQuery('#waitingPopup').dialog2('close');
		}
		return ok;
	}
	return true;
}

jQuery(document).ready(function() {
	jQuery('[form-id=form-labssite]').click(function() {
		jQuery('#form-labssite').dialog2('close');
	});
    jQuery('#form-labssite').ajaxForm({
        beforeSubmit:  beforeSubmitCheckSiteUrl,
        complete: function() {jQuery('#waitingPopup').dialog2('close');},
        success: callBackAddSite
    });
	jQuery('#verifyUrlAvailability').attr('disabled', true);
	jQuery('#verifyUrlAvailability').button();
	jQuery('#verifyUrlAvailability').unbind('click');
	jQuery('#verifyUrlAvailability').click(function(evt) {
		var btnObj = evt.target;
		if (!jQuery(btnObj).hasClass('disabled')) {
			jQuery(btnObj).button('loading');
			verifyUrlAvailability('${Context.modulePath}/@urlAvailability',
				function() {
					jQuery('#urlAvailability').val('true');
					setCheckUrlButton('complete');
				},
				function() {
					jQuery('#urlAvailability').val('false');
					setCheckUrlButton('failed');
				}
			);
		}
	});
});
</script>

