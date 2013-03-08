<#assign mySite=Common.siteDoc(Document).getSite() />
<@extends src="/views/TemplatesBase/" + This.page.template.getTemplateName() + "/template.ftl">

  <@block name="title">${mySite.title}-${This.document.title}</@block>

  <@block name="scripts">
    <script type="text/javascript" src="${contextPath}/wro/labs.sitemap.js"></script>
    
  </@block>

  <@block name="css">
    <@superBlock/>
    <link rel="stylesheet" type="text/css" media="all" href="${contextPath}/wro/labs.sitemap.css"/>
	<link rel="stylesheet" type="text/css" media="all" href="${skinPath}/css/jquery/jquery-ui-1.8.14.css"/>
  </@block>

  <@block name="docactions" />
  
  <@block name="content">
  	<#-- timeout -->
	<input type="hidden" id="serverTimeoutId" value="${serverTimeout}" />
    <div id="content" style="margin-bottom: 5px;">
        <section>
          <div class="page-header">
            <h1>PLAN DU SITE 
          	<#if mySite?? && (Session.hasPermission(mySite.document.ref, 'Everything') || Session.hasPermission(mySite.document.ref, 'ReadWrite')) >
          	<a href="${This.path}/@views/administer_pages"><button id="adminPagesBt" class="btn btn-danger btn-small">${Context.getMessage('command.sitemap.goToPageAdmin')}</button></a>
          	</#if>
          	</h1>
          </div>
          <div class="">
            <#include "views/LabsSite/sitemap_switch_control.ftl">
            <hr/>
            <@block name="sitemap-content"/>
            <hr/>
            <#include "views/LabsSite/sitemap_switch_control.ftl" >
          </div> <!-- /row -->
        </section>
    </div>
  </@block>
  <@block name="pageCommentable">
  </@block>
  <@block name="like">
  </@block>
</@extends>