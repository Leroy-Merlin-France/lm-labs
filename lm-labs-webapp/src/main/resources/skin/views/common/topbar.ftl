<#if This.type.name != "sitesRoot" && !mySite?? >
	<#assign mySite=Common.siteDoc(Document).getSite() />
</#if>
<#assign isAdministrator = (mySite?? && mySite.isAdministrator(Context.principal.name) ) />
<#assign canSetHomePage = (isAdministrator && This.page?? && !(mySite.getHomePageRef() == This.page.document.id) ) />
<#-- site'contributor = page'contributor (same rights)  -->
<#assign isContributor = ((mySite?? && mySite.isContributor(Context.principal.name)) || (This.page?? && This.page != null && This.page.isContributor(Context.principal.name)) ) />
<#if This.type.name != "sitesRoot" >
<#include "views/common/init_mode.ftl" />
</#if>
    <div class="navbar-wrapper" style="z-index: 5;">
    <div class="navbar">
      <div class="navbar-inner">
        <div class="container-fluid">
            <#if mySite??>
              <a class="brand" href="${Context.modulePath}/${mySite.document.webcontainer.url}">${mySite.title}<#if !mySite.visible>&nbsp;<i class="icon-eye-close"></i></#if></a>
            <#else>
              <a class="brand" href="${Context.modulePath}">LABS</a>
            </#if>

          <div class="nav-collapse" >
          <ul class="nav pull-right" style="margin-right: -20px;">
            <#if mySite?? >
            <li>
              <form class="navbar-search pull-right" accept-charset="ISO-8859-1" action="${Context.modulePath}/${mySite.URL}/@search">
              <input class="search-query" placeholder="${Context.getMessage('label.search')}" name="fullText"/>
              </form>
            </li>
            </#if>
            <li id="FKerrorLogin" class="navbar-text" style="/*display: none;*/">${Context.getMessage('label.login.failed')}</li>
            <li><#include "common/labsLogin.ftl" /></li>
            <#if Context.principal.isAnonymous() == false>
            <li class="dropdown	">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown">${Context.principal.firstName} ${Context.principal.lastName}<b class="caret"></b></a>
              <ul class="dropdown-menu">
                <@block name="docactions">
                	<#assign hasDocActions = false />
	                <#if isContributor >
	                	<#assign hasDocActions = true />
	                	<@block name="docactionsaddpage">
	                		<!--   add page     -->
	                		<li><a  onclick="javascript:openAddContentView('${This.path}/@addContentView');" href="#"><i class="icon-plus"></i>${Context.getMessage('command.docactions.addcontent')}</a></li>
	                		<div id="divAddContentView" style="display: none;">
							</div>
							<script type="text/javascript">
		                		jQuery(document).ready(function() {
									jQuery("#divAddContentView").dialog2({
										width : '900px',
										height : '385px',
										autoOpen : false,
										closeOnOverlayClick : false,
										removeOnClose : false,
										showCloseHandle : false,
									});
								});
							</script>
	                	</@block>
	                	<!--   Mode page     -->
	                	<li><a id="page_edit" href="#" ><i class="icon-eye-open"></i>Visualiser</a></li>
	                	<@block name="docactionsonpage">
		                	<!--   Manage parameter's page     -->
							<li><a href="#" onclick="javascript:openParametersPage();"><i class="icon-adjust"></i>${Context.getMessage('command.docactions.parameters')}</a></li>
							<div id="divEditParametersPage" style="display: none;">
								<#include "views/common/page_parameters.ftl" />
							</div>
							<!--   set homePage     -->
							<li>
								<#if canSetHomePage >
									<a id="set-home-page" href="#" ><i class="icon-home"></i>${Context.getMessage('command.docactions.homePage')}</a>
								</#if>
							</li>
							<script type="text/javascript">
								jQuery(document).ready(function() {
									jQuery("#divEditParametersPage").dialog2({
										height : '385px',
										autoOpen : false,
										closeOnOverlayClick : false,
										removeOnClose : false,
										showCloseHandle : false,
									});
								});

							</script>
			                <#if This.page?? && This.page != null && This.page.isAdministrator(Context.principal.name) >
			                	<#assign hasDocActions = true />
			                	<#--   Manage permissions's page     -->
			                	<li><a href="${This.path}/@views/pagePermissions"><i class="icon-share"></i>${Context.getMessage('command.admin.rights')}</a></li>
			                </#if>

						</@block>
	                </#if>
					<#if hasDocActions >
						<li class="divider"></li>
					</#if>

                </@block>
                <@block name="siteactions">
	                <#if isAdministrator >
	                	<#if mySite.visible>
	                		<li><a href="#" onclick="javascript:draftSite();"><i class="icon-eye-close"></i>${Context.getMessage('command.siteactions.draft')}</a></li>
	                	<#else>
	                		<li><a href="#" onclick="javascript:publishSite();"><i class="icon-share-alt"></i>${Context.getMessage('command.siteactions.publish')}</a></li>
	                	</#if>
	                	<!--   delete     -->
	                	<!--li><a href="#" onclick="javascript:deleteSite();"><i class="icon-remove"></i>${Context.getMessage('command.siteactions.delete')}</a></li-->
	                	<script type="text/javascript">
	                	</script>
	                </#if>
	                <#if mySite?? && mySite.isContributor(Context.principal.name) >
	                <li><a href="${Context.modulePath}/${mySite.URL}/@views/edit"><i class="icon-cogs"></i>${Context.getMessage('label.contextmenu.administration')}</a></li>
	                </#if>
	                <#if mySite??>
	                  <li><a href="${Context.modulePath}/${mySite.URL}/@views/sitemap"><i class="icon-map-marker"></i>${Context.getMessage('label.contextmenu.sitemap')}</a></li>
	                </#if>
	                <#if mySite?? && !Context.principal.anonymous >
	                  <li><a href="${Context.modulePath}/${mySite.URL}/@views/subscriptions"><i class="icon-envelope"></i>${Context.getMessage('label.contextmenu.notifications')}</a></li>
	                </#if>
                </@block>
                <li><a id="printLnk" href="#"<#-- onclick="window.print();"-->><i class="icon-print"></i>${Context.getMessage('command.contextmenu.print')}</a></li>
                <li class="divider"></li>
                <li><a id="logoutLnk" href="#"<#-- onclick="doLogout();return false;"-->><i class="icon-signout"></i>${Context.getMessage('command.contextmenu.logout')}</a></li>
              </ul>
            </li>
            </#if>
            <li>
    			<div class="divLogoIntraLM"><img src="${skinPath}/images/LogoLMInTRALABS_Arrondie_s1.jpg" width="26" height="26" /></div>
            </li>
          </ul>
          </div>
        </div>
      </div><#-- /fill -->
    </div><#-- /navbar -->
    </div><#-- /navbar-wrapper -->

<script>
jQuery(document).ready(function() {
<#if canSetHomePage >
    jQuery("#set-home-page").click(function() {
        setAsHomePage('${This.path}');
    });
</#if>
<#if isContributor >
    jQuery("#page_edit").click(function() {
        simulateKeyup69();
    });
</#if>
});
</script>
