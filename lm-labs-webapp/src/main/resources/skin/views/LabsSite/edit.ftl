<#if site?? && (Session.hasPermission(site.document.ref, "Everything") || Session.hasPermission(site.document.ref, "ReadWrite"))>
<@extends src="/views/labs-admin-base.ftl">

  <@block name="breadcrumbs">
    <#include "views/common/breadcrumbs_siteadmin.ftl" >
  </@block>

  <@block name="tabs">
    <#include "macros/admin_menu.ftl" />
	<@adminMenu item="general"/>
  </@block>

  <@block name="content">
    <div class="container">

      <section>
        <div class="page-header">
          <h3>Propriétés</h3>
        </div>
        <div class="row">
          <div class="span4 columns">
&nbsp;
          </div>
          <div class="span12 columns">
            <form action="${This.path}" method="post">
              <input type="hidden" name="action" value="edit"/>
              <fieldset>
                <legend>Mettez à jour les propriétés du site</legend>
                <div class="clearfix">
                  <label for="labsSiteTitle">${Context.getMessage('label.labssite.edit.title')}</label>
                  <div class="input">
                    <input class="required" name="title" value="${site.title}"/>
                  </div>
                </div><!-- /clearfix -->

                <div class="clearfix">
                  <label for="labsSiteURL">${Context.getMessage('label.labssite.edit.url')}</label>
                  <div class="input">
                    ${Context.modulePath}/<input type="hidden" class="required" name="URL" value="${site.URL}" /><span>${site.URL}</span>
                    <span class="help-block">C'est par ce lien que le site sera accessible</span>
                  </div>
                </div><!-- /clearfix -->

                <div class="clearfix">
                  <label for="labsSiteDescription">${Context.getMessage('label.labssite.edit.description')}</label>
                  <div class="input">
                    <textarea name="description" id="labsSiteDescription" >${site.description}</textarea>
                  </div>
                </div><!-- /clearfix -->
              </fieldset>
              <div class="actions">
                <button class="btn primary">${Context.getMessage('label.labssites.edit.valid')}</button>
              </div>
            </form>
          </div>
        </div>
      </section>

      <section>
        <div class="page-header">
          <h3>Catégories</h3>
        </div>
        <div class="row">
          <div class="span4 columns">
&nbsp;
          </div>
          <div class="span12 columns">

          </div>
        </div>
      </section>
    </div>
  </@block>
</@extends>
<#else>
	<#include "error/error_404.ftl" >
</#if>
