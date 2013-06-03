<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN"
    "http://www.w3.org/TR/html4/strict.dtd">
<html lang="fr">
<#assign bsMinified = ".min" />
<#assign popoverPlacement = "" />
<#assign mySite = Context.getProperty("site") />
<#assign callFunction = Context.request.getParameter('callFunction') />
<#if callFunction == "">
	<#assign callFunction = This.callFunction />
</#if>
<#assign calledRef = Context.request.getParameter('calledRef') />
<#if calledRef == "">
	<#assign calledRef = This.calledRef />
</#if>
<#include "views/AssetFolder/macro.ftl"/>
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <title>Interface des médias</title>

        <link rel="icon" type="image/x-icon" href="/nuxeo/img/logo.jpeg" />
        <link rel="shortcut icon"  type="image/x-icon" href="/nuxeo/img/logo.jpeg"/>

        <link rel="stylesheet" href="${Context.modulePath}/${mySite.URL}/@currenttheme/rendercss-${mySite.themeManager.getTheme(Session).document['dc:modified']?string("yyyyMMddHHmmss")}" />
    	<link rel="stylesheet" type="text/css" href="${contextPath}/wro/labs.assets.css" />

      	<script type="text/javascript" src="/nuxeo/wro/labs.assets.js"></script>

    </head>
    <body id="body">
  <#-- timeout -->
    <input type="hidden" id="serverTimeoutId" value="${serverTimeout}" />

  <div id="FKtopContent" style="padding: 10px 10px;" >
  <div class="container" >
    <div class="row-fluid">
    	<ul class="nav nav-tabs">
    		<li id="navSiteAssets" class="active"><a href="#" onclick="javascript:displaySiteAssets();">Site</a></li>
    		<li id="navCommonsAssets"><a href="#" onclick="javascript:displayCommonsAssets();">Commun</a></li>
    	</ul>
		
		<div id="siteAssets">
		      <div class="span3">
		        <div class="bloc" style="margin-right: 5px;" >
		          <div class="header">
		            Arborescence
		          </div>
		
		
		
		
		           <div class="treeroot"></div>
		           <ul id="treenav" class="treeview">
		
		           </ul>
		
		        </div> <!-- bloc -->
		      </div>
		
		
		      <div class="content span9">
		        <div class="row">
		          <div id="fileContent" class="columns well" style="min-height:300px;">
		            <@labsContentAssets ref=Document.ref />
		          </div>
		
		        </div> <#-- row -->
		        <div class="row">
		          <div class="actions">
		            <a href="#" rel="addFileDialog" class="open-dialog btn btn-small btn-primary"><i class="icon-file"></i>Ajouter un fichier</a>
		            <a href="#" rel="addFolderDialog" class="open-dialog btn btn-small"><i class="icon-folder-close"></i>Ajouter un répertoire</a>
		          </div>
		
		          <div id="addFolderDialog" style="display:none;">
		            <h1>Ajouter un répertoire</h1>
		            <form class="form-horizontal" id="addFolderForm" action="${This.path}" onSubmit="addFolderAsset();return false;" method="post">
		              <input type="hidden" name="doctype" value="Folder"/>
		              <fieldset>
		                <div class="control-group">
		                    <label class="control-label" for="title">Nom du répertoire</label>
		                      <div class="controls">
		                        <input name="dublincore:title" class="required input"/>
		                      </div>
		                    </div>
		              </fieldset>
		              <div class="actions">
		                <button type="submit" class="btn btn-primary required-fields" form-id="addFolderForm">Ajouter</button>
		              </div>
		            </form>
		          </div>
		
		     <#include "macros/add_file_dialog.ftl" />
		     <@addFileDialog action="${This.path}?callFunction=${callFunction}&calledRef=${calledRef}" onSubmit="addFileAsset();return false;"/>
		        </div> <#-- row -->
		      </div> <#-- content -->
		</div><#-- siteAssets -->
		
		
		<div id="commonsAssets">
		      <div class="span3">
		        <div class="bloc" style="margin-right: 5px;" >
		          <div class="header">
		            Arborescence
		          </div>
		
		
		
		
		           <div class="treeroot"></div>
		           <ul id="treenavCommon" class="treeview">
		
		           </ul>
		
		        </div> <!-- bloc -->
		      </div>
		
		
		      <div class="content span9">
		        <div class="row">
		          <div id="fileContentCommon" class="columns well" style="min-height:300px;">
		            <@labsContentAssets ref=Common.getRefSiteRootAssetsDoc() path=Context.modulePath + "/" + mySite.URL + "/@assets" isCommon="true" />
		          </div>
		        </div> <#-- row -->
		      </div> <#-- content -->
		</div><#-- commonsAssets -->
		
		
		
    </div> <#-- row-fluid -->
  </div> <#-- container -->

   <script>
   var currentPath = "${This.path}";

$(document).ready(function() {
	jQuery('#fileContent').on('click', 'a.sendToCallFunction', function() {
		sendToCallFunction(this, jQuery(this).data('url'));
		return false;
	});   
	jQuery('#fileContentCommon').on('click', 'a.sendToCallFunction', function() {
		sendToCallFunction(this, jQuery(this).data('url'));
		return false;
	});   
     $('#treenav').treeview({
       url: "${Context.modulePath}/${mySite.URL}/@assets/json?callFunction=${This.callFunction}&calledRef=${This.calledRef}",
       persist: "cookie",
       control: "#navtreecontrol",
       collapsed: true,
       cookieId: "${mySite.document.id}-assets-navtree"
      }
    );
    
	$('#treenavCommon').treeview({
       url: "${Context.modulePath}/${mySite.URL}/@assets/json?callFunction=${This.callFunction}&calledRef=${This.calledRef}&isCommon=true",
       persist: "cookie",
       control: "#navtreeCommoncontrol",
       collapsed: true,
       cookieId: "${mySite.document.id}-assets-navtree-common"
      }
	);
	
	if (location.search.indexOf("isCommon=true") > -1){
		displayCommonsAssets();
	}
	else{
		displaySiteAssets();
	}
	
	
  });

    function sendToCallFunction(obj, href) {
        window.opener.${callFunction}('${calledRef}', href<#if callFunction != "CKEDITOR.tools.callFunction" >, jQuery(obj).data('docid')</#if>);
        window.close();
    }
    
    function loadContentAsset(url, isCommon){
    	var sep = '?';
    	if (url.indexOf("?") > -1) {
    		sep = '&';
    	}
    	var urlParams = sep + 'callFunction=${callFunction}&calledRef=${calledRef}';
    	if (isCommon){
    		$("#fileContentCommon").load(encodeURI(url) + urlParams);
    	}
    	else{
    		$("#fileContent").load(encodeURI(url) + urlParams);
    	}
    }
    
    function displaySiteAssets(){
    	$("#commonsAssets").hide();
    	$("#navCommonsAssets").removeClass("active");
    	$("#siteAssets").show();
    	$("#navSiteAssets").addClass("active");
    }
    
    function displayCommonsAssets(){
    	$("#siteAssets").hide();
    	$("#navSiteAssets").removeClass("active");
    	$("#commonsAssets").show();
    	$("#navCommonsAssets").addClass("active");
    }
    
    function addFolderAsset(){
    	jQuery('#waitingPopup').dialog2('open');
    	var action = $("#pathToAction").val();
    	jQuery.ajax({
			type: "POST",
			url: action,
			data: $("#addFolderForm").serialize(),
			success: function(msg){
				loadContentAsset(action + '/@views/content', false);
				jQuery('#waitingPopup').dialog2('close');
			},
			error: function(msg){
				alert('add folder failed:' + msg.responseText );
				jQuery('#waitingPopup').dialog2('close');
			}
		});
		jQuery('#addFolderDialog').dialog2('close');
    }
    
    function addFileAsset(){
    	$("#addFileForm").attr("action", $("#pathToAction").val());
		$("#addFileForm").submit();
		jQuery('#addFileDialog').dialog2('close');
    }
    </script>
  </div><#-- FKtopContent -->
  </body>
</html>
