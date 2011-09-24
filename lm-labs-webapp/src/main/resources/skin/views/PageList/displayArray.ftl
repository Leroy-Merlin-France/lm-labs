<table id="sortArray" class="zebra-striped">
	<thead>
		<tr>
			<#list headersSet as header>
				<th class="header headerSortDown">${header.name}</th>
			</#list>
		</tr>
	</thead>
	<tbody>
		<#list entriesLines as entriesLine>
			<tr>
				<#list headersSet as header>
					<#if isAuthorized>
						<td style="cursor: pointer;" onclick="javascript:modifyLine('${This.path}/line/${entriesLine.getDocRef().reference()}');">
					<#else>
						<td>
					</#if>
						<#assign entry = entriesLine.getEntryByIdHead(header.idHeader) />
						<#if entry != null>
							<#include "/views/PageList/" + header.type?lower_case + "/display.ftl" />
						</#if>
					</td>
				</#list>
			</tr>
		</#list>
	</tbody>
</table>
<#if 0 < entriesLines?size>
	<script type="text/javascript">
		$("table#sortArray").tablesorter({});
	</script>
</#if>