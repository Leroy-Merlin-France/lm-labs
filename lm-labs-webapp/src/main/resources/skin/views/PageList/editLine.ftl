
<div class="container" style="width: auto;">
	<form class="form-horizontal" method="post" name="form-editLine" id="form-editLine" action="${This.path}">
		<fieldset>
			<input type="hidden" name="lastPage" id="lastPage" value="" />
			<input type="hidden" name="currentPage" id="currentPage" value="" />
			<#list bean.headersSet as header>
				<div class="control-group">
					<label class="control-label" for="${header.idHeader}">${header.name}</label>
					<div class="controls">
						<#include "/views/PageList/" + header.type?lower_case + "/edit.ftl" />
					</div>
				</div>
			</#list>
			<#if line != null>
				<div class="control-group">
					<label class="control-label" for="isVisible">${Context.getMessage('label.pageList.edit.line.hidden')?js_string}</label>
					<div class="controls">
						<label class="radio">
		                	<input type="radio" name="isHidden" id="isHidden" value="yes" <#if !line.visible >checked</#if>>
		                	Oui
		              	</label>
						<label class="radio">
		                	<input type="radio" name="isHidden" id="isHidden" value="no" <#if line.visible >checked</#if>>
		                	Non
		              	</label>
					</div>
				</div>
			</#if>
		</fieldset>
	</form>
</div>
<#if line != null>
	<div id="divBtnDeleteLine" style="text-align: right;">
		<button id="deleteLine" class="btn btn-warning" onClick="javascript:if(confirm('${Context.getMessage('label.pageList.line_deleted.confirm')?js_string}')){deleteLine('${This.previous.path}');}{return false;}" title="${Context.getMessage('label.pageList.edit.manage.delete')}"><i class="icon-remove"></i>${Context.getMessage('label.pageList.edit.manage.delete')}</button>
	</div>
</#if>
