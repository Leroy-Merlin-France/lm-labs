<#macro labsContentAssets ref path=This.path isCommon="false" pathSuffix="" envType="" envName="">
    <#assign endUrl = "" />
    <#assign selectPictureView = "@blob" />
    <#if isCommon == "true">
      <#assign endUrl = "?isCommon=true" />
    <#elseif envType?? && envType != "">
      <#assign endUrl = "?envType=${envType}&envName=${envName}" />
      <#assign endFormName = envType />
    <#else>
      <#assign endFormName= "Site" />
    </#if>
    <ul class="thumbnails">
    <#list Session.getChildren(ref)?sort_by('title') as doc>
      <#if !doc.isFolder >
      <li class="">
        <div class="thumbnail" style="background-color:white;">
          <#assign paramValue = path + "/" + doc.name?js_string?html + "/" + selectPictureView + endUrl />
          <a class="sendToCallFunction" data-url="${paramValue}" data-docid="${doc.id}" title="${doc.title?html} (Image Originale)" >
            <img src="${path}/${doc.name}/@view/Thumbnail${endUrl}" width="120"/>
            <p style="font-size: smaller;margin: 3px;" >${doc.title?html}</p>
          </a>
          <#if doc.type == "Picture" >
            <#assign paramValue = path + "/" + doc.name?js_string?html + "/@view/OriginalJpeg" + endUrl />
            <a class="sendToCallFunction btn btn-mini" data-url="${paramValue}" data-docid="${doc.id}" title="Image Originale JPEG" >JPEG</a>
            <#assign paramValue = path + "/" + doc.name?js_string?html + "/@view/Medium" + endUrl />
            <a class="sendToCallFunction btn btn-mini" data-url="${paramValue}" data-docid="${doc.id}" title="Image Qualité Moyenne" >Moy.</a>
            <#assign paramValue = path + "/" + doc.name?js_string?html + "/@view/Thumbnail" + endUrl />
            <a class="sendToCallFunction btn btn-mini" data-url="${paramValue}" data-docid="${doc.id}" title="Image Vignette" >Vign.</a>
          </#if>
        </div>
      </li>
      </#if>
    </#list>
    </ul>
    <#-- utilise pour l'enregistrement de fichier car partie refraichit en ajax -->
    <input type="hidden" id="pathToAction${endFormName}" value="${path}${pathSuffix}" />
</#macro>