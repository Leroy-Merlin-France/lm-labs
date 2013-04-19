<#macro labsContentAssets ref path=This.path isCommon="false" pathSuffix="" >
	<#assign endUrl = "" />
	<#assign selectPictureView = "@blob" />
	<#if isCommon == "true">
		<#assign endUrl = "?isCommon=true" />
	</#if>
	<ul class="thumbnails">
	<#list Session.getChildren(ref) as doc>
	  <#if !doc.isFolder >
	  <li class="">
	  <div class="thumbnail" style="background-color:white;" >
	  <#assign paramValue = path + "/" + doc.name + "/" + selectPictureView + endUrl />
	  <a class="sendToCallFunction" data-url="${paramValue}" data-docid="${doc.id}" >
	    <img src="${path}/${doc.name}/@view/Thumbnail${endUrl}" width="120"/>
	    <p style="font-size: smaller;margin: 3px;" >${doc.title?html}</p>
	  </a>
	  </div>
	  </li>
	  </#if>
	</#list>
	</ul>
	<input type="hidden" id="pathToAction" value="${path}${pathSuffix}" />
</#macro>