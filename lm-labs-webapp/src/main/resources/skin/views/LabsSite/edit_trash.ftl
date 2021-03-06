<#assign mySite=Common.siteDoc(Document).getSite() />
<#if mySite?? && Session.hasPermission(mySite.document.ref, "Everything")>
<@extends src="/views/labs-admin-base.ftl">

<@block name="scripts">
    <@superBlock/>
    <script type="text/javascript" src="${skinPath}/js/bootstrap/bootstrap-tooltip.js"></script>
    <script type="text/javascript" src="${skinPath}/js/bootstrap/bootstrap-popover.js"></script>
    <script type="text/javascript" src="${skinPath}/js/jquery/jquery.tablesorter.min.js"></script>
	<script type="text/javascript">
jQuery(document).ready(function() {
  jQuery('.btn').attr('disabled', false);
  jQuery("table[class*='table-striped']").tablesorter({
  	headers: { 0: { sorter: false}, 6: { sorter: false}},
      sortList: [[5,0]],
      textExtraction: function(node) {
            // extract data from markup and return it
        var sortValues = jQuery(node).find('span[class=sortValue]');
        if (sortValues.length > 0) {
          return sortValues[0].innerHTML;
        }
            return node.innerHTML;
        }
  });
  jQuery('input[name="checkoptionsHeader"]').change(function() {
    var checkboxes = jQuery(this).closest('table').find('input[name="checkoptions"]');
    if (jQuery(this).is(':checked')) {
      jQuery(checkboxes).attr('checked','checked');
    } else {
      jQuery(checkboxes).removeAttr('checked');
    }
  });
		  
});
	</script>
  </@block>
  
<@block name="css">
    <@superBlock/>
    <link rel="stylesheet" type="text/css" media="all" href="${skinPath}/css/tablesorter.css"/>
 </@block>

  <@block name="docactions"></@block>


  <@block name="content">
    <#include "macros/admin_menu.ftl" />
	<@adminMenu item="trash"/>
	<div class="container">
      <section>
        <#assign deletedDocs = This.getDeletedDocs() />
        <div class="page-header">
          <h3>
          	${Context.getMessage('label.lifeCycle.trash.title')} <#if (deletedDocs?size > 0)><span class="badge badge-info" style="vertical-align: middle;" >${deletedDocs?size}</span></#if>
          	<div style="text-align: right;margin-top: -37px;">
				<button onClick="beEmptyTrash();" title="${Context.getMessage('label.lifeCycle.page.emptyTrash')}" class="btn btn-danger" style="margin-left:20px;" ><i class="icon-fire"></i>${Context.getMessage('label.lifeCycle.page.emptyTrash')}</button>
			</div>
          </h3>
        </div>
        <#if (deletedDocs?size > 0)>
            <div id="trashActions" style="margin-bottom: 3px;" >
              <button class="btn btn-danger" id="deleteSelection" onClick="processSelection(this, 'remove');"><i class="icon-fire"></i>${Context.getMessage('command.trash.remove.bulk')}</button>
              <button class="btn" id="restoreSelection" onClick="processSelection(this, 'restore');"><i class="icon-share-alt"></i>${Context.getMessage('command.trash.restore.bulk')}</button>
            </div>
	        <table class="table table-striped table-bordered bs">
	            <thead>
	            <tr>
	              <th><input type="checkbox" name="checkoptionsHeader" title="${Context.getMessage('label.trash.checkbox')}"/></th>
	              <th>${Context.getMessage('label.lifeCycle.trash.page')}</th>
	              <th>${Context.getMessage('label.lifeCycle.trash.createdBy')}</th>
	              <th>${Context.getMessage('label.lifeCycle.trash.the')}</th>
	              <th>${Context.getMessage('label.lifeCycle.trash.lastModifiedBy')}</th>
	              <th>${Context.getMessage('label.lifeCycle.trash.the')}</th>
	              <th>&nbsp;</th>
	            </tr>
	          </thead>
	          <tbody>
	          <#list deletedDocs as doc>
	            <tr>
	              <td>
	                <input type="checkbox" name="checkoptions" value="${doc.id}" />
	              </td>
	              <td>
	              	<a href="#" rel="popover" data-trigger="hover" data-content="&lt;strong&gt;${Context.getMessage('label.labspath')?html}: &lt;/strong&gt;&lt;br&gt;${Root.getTruncatedLink(doc)}&lt;br&gt;&lt;strong&gt;${Context.getMessage('label.doctype')}: &lt;/strong&gt;&lt;br&gt;${doc.type}&lt;br&gt;&lt;strong&gt;${Context.getMessage('label.description')}: &lt;/strong&gt;&lt;br&gt;${doc['dc:description']?html}&lt;br&gt;"
	              	  data-original-title="${Context.getMessage('label.properties')}">${doc['dc:title']}</a>
	              </td>
	              <td>${userFullName(doc['dc:creator'])}</td>
	              <td>
	                ${doc['dc:created']?string.medium}
	                <span class="sortValue">${doc['dc:created']?string("yyyyMMddHHmmss")}</span>
	              </td>
	              <#assign modified=doc['dc:modified']/>
	              <td>${userFullName(doc['dc:lastContributor'])}</td>
	              <td>
	                ${modified?string.medium}
	                <span class="sortValue">${modified?string("yyyyMMddHHmmss")}</span>
	              </td>
	              <td>
	              	<#assign docUrl = Root.getLink(doc) />
	              	<a  href="${docUrl}" class="btn" style="margin-bottom: 3px;" onclick="javascript:undeletePage(this);return false;"><i class="icon-share-alt"></i>${Context.getMessage('command.trash.undelete')}</a>
	              	<a  href="${docUrl}" class="btn btn-danger" onclick="deletePage(this);return false;"><i class="icon-fire"></i>${Context.getMessage('command.trash.delete')}</a>
	              </td>
	            </tr>
	          </#list>
	          </tbody>
	        </table>
	        
	      </#if>
      </section>
    </div>
    <script type="text/javascript">
		function deletePage(bt){
			var url = jQuery(bt).attr('href');
			if (confirm("${Context.getMessage('label.trash.remove.confirm')}")){
				jQuery('#waitingPopup').dialog2('open');
    			jQuery.ajax({
					type: 'DELETE',
				    async: false,
				    url: url,
				    success: function(data) {
						alert("${Context.getMessage('label.trash.remove.success')}");
						window.location.reload();
				    },
					error: function(jqXHR, textStatus, errorThrown) {
						<#--alert(errorThrown + ": " + "," + jqXHR.responseText);-->
						alert("${Context.getMessage('label.trash.remove.failed')}");
						jQuery('#waitingPopup').dialog2('close');
					}
				});
			}
		}
		function undeletePage(bt){
			var url = jQuery(bt).attr('href');
			if (confirm("${Context.getMessage('label.lifeCycle.page.wouldYouUndelete')}")){
				jQuery('#waitingPopup').dialog2('open');
    			jQuery.ajax({
					type: 'PUT',
				    async: false,
				    url: url + '/@labspublish/undelete',
				    success: function(data) {
				    	if (data == 'undelete') {
				          alert("${Context.getMessage('label.lifeCycle.page.hasUndeleted')}");
				          document.location.href = '${This.path}/@views/edit_trash';
				        }
				        else {
				          alert("${Context.getMessage('label.lifeCycle.page.hasNotUndeleted')}");
				          jQuery('#waitingPopup').dialog2('close');
				        }
				    },
				    error: function(data){
				    	jQuery('#waitingPopup').dialog2('close');
				    }
				});
			}
		}
		function beEmptyTrash(){
			if (confirm("${Context.getMessage('label.lifeCycle.page.wouldYouBeEmptyTrash')}")){
				jQuery('#waitingPopup').dialog2('open');
    			jQuery.ajax({
					type: 'DELETE',
				    async: false,
				    url: '${This.path}/@labspublish/emptyTrash',
				    success: function(data) {
				    	if (data == 'beEmpty') {
				          alert("${Context.getMessage('label.lifeCycle.page.beEmpty')}");
				          document.location.href = '${This.path}/@views/edit_trash';
				        }
				        else {
				          alert("${Context.getMessage('label.lifeCycle.page.notBeEmpty')}");
				          jQuery('#waitingPopup').dialog2('close');
				        }
				    },
				    error: function(data){
				    	jQuery('#waitingPopup').dialog2('close');
				    }
				});
			}
		}
		function processSelection(bt, action) {
			var n = jQuery('input[name="checkoptions"]:checked').length;
      		if (n <= 0) {
        		return false;
      		}
			if (action != "remove" && action != "restore") {
				return;
			}
			var confirmMsg = '';
			var successMsg = '';
			var failureMsg = '';
			if (action == "remove") {
				confirmMsg = "${Context.getMessage('label.trash.remove.bulk.confirm')}";
				successMsg = "${Context.getMessage('label.trash.remove.bulk.success')}";
				failureMsg = "${Context.getMessage('label.trash.remove.bulk.failed')}";
			} else {
				confirmMsg = "${Context.getMessage('label.trash.restore.bulk.confirm')}";
				successMsg = "${Context.getMessage('label.trash.restore.bulk.success')}";
				failureMsg = "${Context.getMessage('label.trash.restore.bulk.failed')}";
			}
			if (confirm(confirmMsg)) {
				jQuery('#waitingPopup').dialog2('open');
				var actionUrl = '';
				var ajaxAction = '';
				if (action == "remove") {
					actionUrl = 'bulkRemove';
					ajaxAction = 'DELETE';
				} else {
					actionUrl = 'bulkUndelete';
					ajaxAction = 'PUT';
				}
				var url = '${This.path}/@labspublish/' + actionUrl + '?';
				jQuery('input[name="checkoptions"]:checked').each(function(i) {
					url += "id=" + this.value;
					if (i < n-1) {
						url += "&";
					}
				});
				jQuery.ajax({
					url: url,
					type: ajaxAction,
					complete: function(jqXHR, textStatus) {
						jQuery('input[name="checkoptions"]').attr('checked', false);
					},
					success: function (data, textStatus, jqXHR) {
						alert(successMsg);
						window.location.reload();
					},
					error: function(jqXHR, textStatus, errorThrown) {
						<#--alert(errorThrown + ": " + "," + jqXHR.responseText);-->
						alert(failureMsg);
						jQuery('#waitingPopup').dialog2('close');
					}
				});
			}
			return false;
		}
		jQuery(function () {
				jQuery("a[rel=popover]")
					.popover({offset: 10, html:true})
					.click(
						function(e) {e.preventDefault()}
					)
			}
		) 
	</script>
  </@block>
</@extends>
<#else>
	<#include "error/error_404.ftl" >
</#if>
