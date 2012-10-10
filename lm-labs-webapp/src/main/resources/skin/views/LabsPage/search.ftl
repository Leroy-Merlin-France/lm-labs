<@extends src="/views/TemplatesBase/" + This.page.template.getTemplateName() + "/template.ftl">

  <@block name="title">${Common.siteDoc(Document).getSite().title}-${This.document.title}</@block>

    <@block name="scripts">
      <@superBlock/>
		<script type="text/javascript" src="${contextPath}/wro/labs.search.js"></script>
    </@block>

    <@block name="css">
      <@superBlock/>
		<link rel="stylesheet" type="text/css" media="all" href="${contextPath}/wro/labs.search.css"/>
    </@block>

    <@block name="content">

		<@block name="pageTags"/>

    <script type="text/javascript">
      jQuery(document).ready(function() {
        jQuery("#resultsSearch").tablesorter({
            sortList: [[1,0]],
            headers: {
              0: {
                sorter:false
              },
              5: {
                sorter:false
              }
            },
            textExtraction: function(node) {
                  // extract data from markup and return it
              var sortValues = jQuery(node).find('span[class=sortValue]');
              if (sortValues.length > 0) {
                return sortValues[0].innerHTML;
              }
                  return node.innerHTML;
              }
        });
      });
    </script>

    <div class="">
    <section>
      <div class="page-header">
        <h1>${Context.getMessage('label.search.title')}</h1>
        <#if result?size &gt; 0>
          <small>${Context.getMessage('label.search.displayResults', result?size, result?size)} <span>${query?split('"')[1]}</small>
        </#if>
      </div>
    <#if result?size &gt; 0>
      <table class="table table-striped table-bordered bs labstable" id="resultsSearch" >
        <thead>
          <tr>
          	<th class="header">&nbsp;</th>
          	<th class="header">${Context.getMessage('label.search.head.title')}</th>
          	<th class="header">${Context.getMessage('label.search.head.lastModification')}</th>
          	<th class="header">${Context.getMessage('label.search.head.size')}</th>
          	<th class="header">${Context.getMessage('label.search.head.page')}</th>
          	<th class="header">&nbsp;</th>
          </tr>
        </thead>
        <tbody>
          <#list result as doc>
            <#assign sd = Common.siteDoc(doc) />
            <tr>
              <td class="colIcon"><img title="${doc.type}" alt="&gt;&gt;" <#if doc.schemas?seq_contains("common") >src="/nuxeo/${doc.common.icon}"</#if> /></td>

                <td>
                	<#if (doc['dc:title']?length > 0)>
                		<a href="${Context.modulePath}/${sd.getResourcePath()}" target="_blank">${doc['dc:title']}</a>
                	<#else>
              			(${Context.getMessage('label.search.result.noTitle')})
              		</#if>
                </td>
                <td>${userFullName(doc['dc:lastContributor'])}</td>
                <#assign formattedFilesize = "(" + Context.getMessage('label.search.result.noFile') + ")" />
                <#assign filesize = 0 />
                <#assign hasFile = false />
                <#if sd.blobHolder?? && sd.blobHolder.blob != null >
                  <#assign hasFile = true />
                  <#assign filesize = sd.blobHolder.blob.length />
                  <#assign formattedFilesize = bytesFormat(filesize, "K", "fr_FR") />
                </#if>
              <td class="colFilesize">${formattedFilesize}<span class="sortValue">${filesize?string.computer}</span></td>

              <td>
              	<a href="${Context.modulePath}/${sd.getParentPage().getPath()}">${sd.getParentPage().title}</a>
              </td>
                <td>
                  <#if !hasFile>
                  <a href="#" class="btn disabled">
                  <#elseif doc.type != "LabsNews">
                  <a rel="nofollow" href="${Context.modulePath}/${sd.getResourcePath()}/@blob/preview" target="_blank" class="btn">
                  </#if>
                  <#if !hasFile || doc.type != "LabsNews">
                  <i class="icon-eye-open"></i>${Context.getMessage('command.LabsSite.latestuploads.display')}</a>
                  </#if>
                  <#if !hasFile>
                  <a disabled="disabled" href="#" class="btn disabled">
                  <#elseif doc.type != "LabsNews">
                  <a rel="nofollow" href="${Context.modulePath}/${sd.getResourcePath()}/@blob" target="_blank" class="btn">
                  </#if>
                  <#if !hasFile || doc.type != "LabsNews">
                  <i class="icon-download"></i>${Context.getMessage('command.LabsSite.latestuploads.download')}</a>
                  </#if>
                </td>
            </tr>
          </#list>
        </tbody>
      </table>
    <#else>
      <div class="resultsSearchSubtitle">Pas de résultats pour <span>${query?split('"')[1]}</span></div>
    </#if>


    <#if Context.principal.administrator >
    <small>Query performed: ${query}</small>
    </#if>

  </section>
</div>
</@block>
<@block name="pageCommentable">
</@block>
</@extends>




