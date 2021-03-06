<#assign mySite=Common.siteDoc(Document).getSite() />
<@extends src="views/LabsSite/sitemap-base.ftl">
     <@block name="scripts">
      <@superBlock/>
      <script type="text/javascript" src="${skinPath}/js/jquery/jquery.tablesorter.min.js"></script>
<script type="text/javascript">
jQuery(document).ready(function() {
  jQuery("table[class*='table-striped']").tablesorter({
      sortList: [[1,0]],
      textExtraction: function(node) {
            // extract data from markup and return it
        var sortValues = jQuery(node).find('span[class=sortValue]');
        if (sortValues.length > 0) {
          return sortValues[0].innerHTML;
        }
        var hRefs  = jQuery(node).find('a');
        if (hRefs.length > 0) {
          return hRefs[0].innerHTML;
        }
        return node.innerHTML;
      }
  });
});
</script>
     </@block>

    <@block name="css">
    <@superBlock/>
    <link rel="stylesheet" type="text/css" media="all" href="${skinPath}/css/tablesorter.css"/>
  </@block>

     <@block name="sitemap-content">
        <table class="table table-striped bs table-bordered labstable">
            <thead>
            <tr>
              <th class="header" width="30%">Elément</th>
              <th class="header">Créé par</th>
              <th class="header">le</th>
              <th class="header">Dernière mise à jour par</th>
              <th class="header">le</th>
            </tr>
          </thead>
          <tbody>
          <#list mySite.getAllPages() as page>
            <#assign isAdmin = mySite.isAdministrator(Context.principal.name) />
            <#if isAdmin || (!mySite.isAdministrator(Context.principal.name) && page.document.type != "LabsNews" && page.visible) > 
            <tr>
              <#assign doc=page.document />
              <td class="nameCol"><a href="${Context.modulePath}/${page.getPath()?html}">${doc.title}</a></td>
              <td class="createdCol">${userFullName(doc['dc:creator'])}</td>
              <td class="createdCol">
                ${doc['dc:created']?string.medium}
                <span class="sortValue">${doc['dc:created']?string("yyyyMMddHHmmss")}</span>
              </td>
              <#assign modified=doc['dc:modified']/>
              <td class="updatedCol">${userFullName(doc['dc:lastContributor'])}</td>
              <td class="updatedCol">
                ${modified?string.medium}
                <span class="sortValue">${modified?string("yyyyMMddHHmmss")}</span>
              </td>
            </tr>
            </#if>
          </#list>
          </tbody>
      </table>

  </@block>
</@extends>