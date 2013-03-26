<#assign paramCategory = Context.request.getParameter('idCategory') />
<#if paramCategory == "">
	<#assign idCurrentCategory = -1 />
<#else>
	<#assign idCurrentCategory = paramCategory?number />
</#if>
<#assign undeletedLabsSites = This.getUndeletedLabsSites(idCurrentCategory) />
<#if (undeletedLabsSites?size > 0) >
	<#assign hasAtLeastOneAdminSite = false />
	<#list undeletedLabsSites as undeletedSite>
		<#if undeletedSite.isAdministrator(Context.principal.name) >
			<#assign hasAtLeastOneAdminSite = true />
			<#break>
		</#if>
	</#list>
  <table class="table table-striped table-bordered bs<#if idCurrentCategory == -1 > hasCategoryColumn</#if><#if hasAtLeastOneAdminSite > hasDeleteColumn</#if>" id="MySites" >
    <thead>
      <tr>
        <th>${Context.getMessage('label.labssite.list.headers.site')}</th>
        <th>${Context.getMessage('label.labssite.list.headers.owner')}</th>
        <th style="min-width: 70px;" >${Context.getMessage('label.labssite.list.headers.created')}</th>
        <#if idCurrentCategory == -1 >
        <th>${Context.getMessage('label.labssite.list.headers.category')}</th>
        </#if>
        <th style="width: 57px;">&nbsp;</th>
        <#if hasAtLeastOneAdminSite>
        <th style="width: 88px;"></th>
        </#if>
      </tr>
    </thead>
    <tbody>
      <#list undeletedLabsSites as sit>
        <tr data-category="${sit.category}" >
          <td>${sit.title}<#if !sit.visible>&nbsp;<i class="icon-eye-close"></i></#if></td>
          <#if (sit.administratorsSite?size > 0)>
          	<td>
          		<#list sit.administratorsSite as siteAdministrator>
          			${userFullName(siteAdministrator)}<#if (siteAdministrator != sit.administratorsSite?last)>,&nbsp;</#if>
          		</#list>
          	</td>
          <#else>
          	<td>${userFullName(sit.document['dc:creator'])}</td>
          </#if>
	      <#assign creationDate = sit.document['dc:created']?datetime />
	      <#assign creationDateStr = creationDate?string("EEEE dd MMMM yyyy HH:mm") />
	      <td><span title="${creationDateStr}" >${Context.getMessage('label.labssite.list.dateInWordsFormat',[dateInWords(creationDate)])}</span><span class="sortValue">${creationDate?string("yyyyMMddHHmmss")}</span></td>
          <#if idCurrentCategory == -1 >
          <td>${sit.category}</td>
          </#if>
          <td><a class="btn" href="${This.path}/${sit.URL}">${Context.getMessage('command.labssite.list.open')}</a></td>
          <#if hasAtLeastOneAdminSite>
          <td>
          <#if sit.isAdministrator(Context.principal.name) >
          	<#assign confirm = Context.getMessage("label.lifeCycle.site.wouldYouDelete.extended", sit.title) />
          	<#assign successStatus = Context.getMessage("label.lifeCycle.site.hasDeleted.success") />
          	<#assign successMsg = Context.getMessage("label.lifeCycle.site.hasDeleted.extended", sit.title) />
          	<#assign errorStatus = Context.getMessage("label.lifeCycle.site.hasNotDeleted.failed") />
          	<#assign errorMsg = Context.getMessage("label.lifeCycle.site.hasNotDeleted.extended", sit.title) />
          	<a href="#" class="btn btn-danger" onclick="if (confirm('${confirm?js_string?html}')){deleteSite(this, '${Context.modulePath}/${sit.URL}/@labspublish/delete', {successStatus:'${successStatus?js_string}', errorStatus:'${serrorStatus?js_string}', successMsg:'${successMsg?js_string?html}', errorMsg:'${errorMsg?js_string?html}'});}" >${Context.getMessage('command.siteactions.delete')}</a>
          </#if>
          </td>
          </#if>
        </tr>
      </#list>
  </table>
<#else>
Aucun site trouvé
</#if>
