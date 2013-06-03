<#include "macros/HtmlPage.ftl" />
<#include "macros/widgets.ftl" />
<#list page.sections as section >
	<@displayRawSection section=section />
</#list>