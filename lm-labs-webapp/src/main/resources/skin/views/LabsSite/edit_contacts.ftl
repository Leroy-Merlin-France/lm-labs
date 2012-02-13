<#if site?? && Session.hasPermission(site.document.ref, "Everything")>
<@extends src="/views/labs-admin-base.ftl">

  <@block name="docactions"></@block>

  <@block name="breadcrumbs">
    <#include "views/common/breadcrumbs_siteadmin.ftl" >
  </@block>

  <@block name="tabs">
    <#include "macros/admin_menu.ftl" />
	<@adminMenu item="contacts"/>
  </@block>

  <@block name="content">
    <div class="container">
      <section>
		<#include "views/LabsContacts/contactsManager.ftl" >
		
      </section>

    </div>
  </@block>
</@extends>
<#else>
	<#include "error/error_404.ftl" >
</#if>