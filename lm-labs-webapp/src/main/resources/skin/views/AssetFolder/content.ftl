<#assign callFunction = Context.request.getParameter('callFunction') />
<#assign calledRef = Context.request.getParameter('calledRef') />
<#include "views/AssetFolder/macro.ftl"/>
<@labsContentAssets ref=Document.ref path=This.path isCommon=isCommon envType=envType envName=envName />