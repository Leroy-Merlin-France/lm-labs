<#assign mySite=Common.siteDoc(Document).getSite() />
<@extends src="/views/TemplatesBase/" + mySite.template.getTemplateName() + "/template.ftl">
  <#assign isAuthorized = Session.hasPermission(Document.ref, 'Write')>

  <@block name="title">${mySite.title}-${This.document.title}</@block>

  <@block name="scripts">
    <script type="text/javascript" src="${contextPath}/wro/labs.pagenav.js"></script> <#--MUST BE BEFORE superBlock -->
    <@superBlock/>
    <script type="text/javascript" >
      <#--jQuery(document).ready(function() {
        jQuery('.news-navigation a[rel=popover]').popover({offset: 10, html: true});
      });-->
    </script>
  </@block>

  <@block name="css">
    <link rel="stylesheet" type="text/css" media="all" href="${contextPath}/wro/labs.news.css"/>
    <@superBlock/>
    <#--<#include "views/common/datepicker_css.ftl">-->
  </@block>


  <@block name="content">
  	<#-- include "views/PageNav/macro.ftl"-->
    <div id="divPageNav" class="container-fluid">
      	<#include "views/common/page_header.ftl">
      	<#if isAuthorized>
        	<div class="editblock" style="margin-top: 5px;width: 100%;text-align: right;float: right">
				<a id="btnModifyPropsPageNav" class="btn" style="cursor: pointer;margin-right: 5px;" onclick="javascript:actionPropsPageNav();"><i class="icon-eye-open"></i>Modifier les critères</a>
		  	</div>
		  	<br><br>
		  	<#include "views/PageNav/editProps.ftl" />
        </#if>
        
        <#list taggedPages as page>
        	<#if This.pageAsPreview(page) >
        		<div style="width: 80%;border: 1px solid black;">
	        		sa marche !
	        	</div>
        	<#else>
	        	<div style="width: 80%;border: 1px solid black;">
	        		<h2>${page.title?html}</h2>
	        		<br>
	        		${page.description}
	        	</div>
	        </#if>
        </#list>
        
        
    </div>

  </@block>
  
</@extends>