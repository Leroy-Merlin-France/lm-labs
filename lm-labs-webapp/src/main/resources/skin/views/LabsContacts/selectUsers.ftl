<#if errorMessage != null>
<div class="alert alert-block alert-error no-fade"><p>${errorMessage}</p></div>
<#else>
<ul style="list-style: none;">
	  <#list suggests as suggest >
		  <li>
		    <input type="radio" name="radioUserInput" <#if suggests?size == 1>checked="checked"</#if> value="${suggest.name}">${suggest.name}<#if suggest.fullName?length &gt; 0> (${suggest.fullName})</#if>
		    </input>
		  </li>
	  </#list>
</ul>
</#if>