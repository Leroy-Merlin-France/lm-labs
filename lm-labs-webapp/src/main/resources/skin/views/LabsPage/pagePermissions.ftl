<#if This.page?? && This.page != null && This.page.isAdministrator(Context.principal.name) >
<#-- @extends src="/views/TemplatesBase/" + mySite.template.getTemplateName() + "/template.ftl" -->
<@extends src="/views/labs-admin-base.ftl">
<#if !mySite?? >
    <#assign mySite=Common.siteDoc(Document).getSite() />
</#if>
<#assign isHomePage = This.page?? && (mySite.getHomePageRef() == This.page.document.id) />
  <@block name="title">${mySite.title} - ${This.document.title} - ${Context.getMessage('label.admin.page.rights.pagetitle')}</@block>

  <#-- @block name="docactionsaddpage"></@block>
  <@block name="docactionsonpage"></@block -->

  <@block name="content">
    <div class="am-i-sure" style="margin-bottom: 10px;" >
        <div class="alert alert-block no-fade" >
        <h4 class="alert-heading"><i class="icon-warning-sign" style="font-size: 48px;" ></i>${Context.getMessage('label.admin.page.rights.warining.warning')}</h4>
        <p>${Context.getMessage('label.admin.page.rights.warining.full-message')}</p>
        </div>
        <btn class="btn btn-large btn-warning" onclick="jQuery('.manage-page-rights').show();jQuery('.am-i-sure').hide();return false;" >
            ${Context.getMessage('command.admin.page.rights.iamsure')}
        </btn>
        <a href="${This.path}" class="btn btn-large btn-primary"><i class="icon-share-alt"></i>${Context.getMessage('command.admin.page.rights.back')}</a>
    </div>
    <div class="manage-page-rights" style="display: none;" >
  	<div class="page-header">
    	<h3>${Context.getMessage('label.admin.page.rights.title')}&nbsp;<i>${This.document.title}</i> &nbsp;</h3>
    	<div style="position: relative; float: right;margin-top: -19px;">
    		<a href="${This.path}" class="btn btn-mini btn-primary"><i class="icon-share-alt"></i>${Context.getMessage('command.admin.page.rights.back')}</a>
    		<#if !isHomePage>
    			<a href="javascript:blockInherits('');" class="btn btn-mini">${Context.getMessage('command.admin.page.rights.block')}</a>
    			<a href="javascript:unblockInherits('');" class="btn btn-mini btn-danger">${Context.getMessage('command.admin.page.rights.unblock')}</a>
    		</#if>
    	</div>
    </div>
	<script type="text/javascript">
	function blockInherits(permission){
		jQuery('#waitingPopup').dialog2('open');
		jQuery.ajax({
			type: 'GET',
		    async: false,
		    url: "${This.path}/@labspermissions/blockInherits?permission=" + permission,
		    success: function(data) {
		    	jQuery('#waitingPopup').dialog2('close');
		    	jQuery("#divDislayArray")[0].innerHTML = '<img src="${skinPath}/images/loading.gif" />';
				jQuery("#divDislayArray").load('${This.path}/@labspermissions');
				// initModalLabsPermissions(); Delete sinon n'affiche pas le bouton valider dans les popups
		    },
		    error: function(data){
		    	jQuery('#waitingPopup').dialog2('close');
		    }
		});
	}
	
	
	</script>
  	<#include "views/LabsPermissions/permissionsManager.ftl" >
  	</div>
  </@block>
  <@block name="bottom-page-js" >
  <@superBlock/>
  <script type="text/javascript">
function unblockInherits(permission){
	if(confirm("Vous allez supprimer les droits spécifiques à la page et restaurer les droits parents, continuez ?")){
		jQuery('#waitingPopup').dialog2('open');
		jQuery.ajax({
			type: 'GET',
		    async: false,
		    url: "${This.path}/@labspermissions/unblockInherits?permission=" + permission,
		    success: function(data) {
		    	jQuery('#waitingPopup').dialog2('close');
		    	jQuery("#divDislayArray")[0].innerHTML = '<img src="${skinPath}/images/loading.gif" />';
				jQuery("#divDislayArray").load('${This.path}/@labspermissions');
				// initModalLabsPermissions(); Delete sinon n'affiche pas le bouton valider dans les popups
		    },
		    error: function(data){
		    	jQuery('#waitingPopup').dialog2('close');
		    }
		});
	}
}
  </script>
  </@block>
  
</@extends>
<#else>
    <#include "error/error_404.ftl" >
</#if>