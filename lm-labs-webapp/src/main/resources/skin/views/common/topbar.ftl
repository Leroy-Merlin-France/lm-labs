<#if This.type.name != "sitesRoot" >
  <#assign siteDoc = breadcrumbsDocs(This.document)?first />
</#if>
<script type="text/javascript">
function topBarFullTextSearch() {
  <#if siteDoc?? >
  window.location.href = '${Context.modulePath}/${siteDoc.webcontainer.url}/@views/searchresults?fullText=' + escape(jQuery('input[name=q]').val());
  </#if>

}
</script>
    <div class="topbar-wrapper" style="z-index: 5;">
    <div class="topbar">
      <div class="topbar-inner">
        <div class="container">
          <h3>
            <#if siteDoc??>
              <a href="${Context.modulePath}/${siteDoc.webcontainer.url}">${siteDoc.title}</a>
            <#else>
              <a href="${Context.modulePath}">LABS</a>
            </#if>
          </h3>


          <ul class="nav secondary-nav">
            <#if siteDoc?? >
            <li>
              <form onsubmit="topBarFullTextSearch();return false;" >
              <input class="normal" placeholder="${Context.getMessage('label.search')}" name="q"/>
              </form>
            </li>
            </#if>
            <li><#include "common/login.ftl" /></li>
            <#if Context.principal.isAnonymous() == false>
            <li class="dropdown	">
              <a href="#" class="dropdown-toggle">${Context.principal.firstName} ${Context.principal.lastName}</a>
              <ul class="dropdown-menu">
                <@block name="docactions"></@block>
                <@block name="siteactions">
                <#if site?? && Session.hasPermission(site.document.ref, "WRITE")>
                <li><a href="${Context.modulePath}/${site.URL}/@views/edit">Administration</a></li>
                </#if>
                </@block>

              <#if siteDoc?? && Session.hasPermission(siteDoc.ref, 'Everything') >
                <li><a href="${Context.baseURL}/nuxeo/nxpath/default/default-domain/sites/${siteDoc.title}/tree@view_documents?tabIds=%3A" target="_blank" >${Context.getMessage('command.LabsSite.goToBackOffice')}</a></li>
              </#if>
                <li class="divider"></li>
                <li><a id="logout" href="#">Déconnexion</a></li>
              </ul>
            </li>
            </#if>
          </ul>
        </div>
      </div><!-- /fill -->
    </div><!-- /topbar -->
    </div><!-- /topbar-wrapper -->
    &nbsp;