<ul style="list-style: none;">
	  <li id="suggestDefault">
	    <input type="radio" id="radioUserInputEveryone" name="radioUserInput" value="Everyone">${Context.getMessage('label.security.labs.Everyone')}</input>
	  </li>
	  <#list suggests as suggest >
	  <li>
	    <input type="radio" name="radioUserInput" <#if suggests?size == 1>checked="checked"</#if> value="${suggest.name}">${suggest.name}<#if suggest.fullName?length &gt; 0> (${suggest.fullName})</#if>
	    </input>
	  </li>
	  </#list>
</ul>