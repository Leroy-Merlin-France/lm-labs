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
            <#if envType??>
    		<li id="nav${envType}Assets"><a href="#" onclick="javascript:displayForumAssets();">Public</a></li>
    		</#if>
    		<li id="navSiteAssets"><a href="#" onclick="javascript:displaySiteAssets();">Site</a></li>
    		<li id="navCommonsAssets"><a href="#" onclick="javascript:displayCommonsAssets();">Commun</a></li>
    	</ul>
		
		<div id="siteAssets">
		      <div class="span3">
		        <div class="bloc" style="margin-right: 5px;" >
		          <div class="header">Arborescence</div>
		          <div class="treeroot"></div>
		          <ul id="treenav" class="treeview"></ul>
		        </div> <!-- bloc -->
		      </div>
		
		      <div class="content span9">
		        <div class="row">
		          <div id="fileContent" class="columns well" style="min-height:300px;">
		          </div>
		        </div> <#-- row -->
		        <#if writeAssetsSite>
		        <div class="row">
		          <div class="actions">
		            <a href="#" rel="addFileDialogSite" class="open-dialog btn btn-small btn-primary"><i class="icon-file"></i>Ajouter un fichier</a>
		            <a href="#" rel="addFolderDialogSite" class="open-dialog btn btn-small"><i class="icon-folder-close"></i>Ajouter un répertoire</a>
		          </div>
		
		          <div id="addFolderDialogSite" style="display:none;">
		            <h1>Ajouter un répertoire</h1>
		            <form class="form-horizontal" id="addFolderFormSite" action="${This.path}" onSubmit="addFolderAsset('Site');return false;" method="post">
		              <input type="hidden" name="doctype" value="Folder"/>
		              <input type="hidden" name="no_redirect" value="1" />
		              <fieldset>
		                <div class="control-group">
		                    <label class="control-label" for="title">Nom du répertoire</label>
		                      <div class="controls">
		                        <input name="dublincore:title" class="required input"/>
		                      </div>
		                    </div>
		              </fieldset>
		              <div class="actions">
		                <button type="submit" class="btn btn-primary required-fields" form-id="addFolderFormSite">Ajouter</button>
		              </div>
		            </form>
		          </div>
		
		          <#include "macros/add_file_dialog.ftl" />
		          <@addFileDialog action="${This.path}?callFunction=${callFunction}&calledRef=${calledRef}" onSubmit="addFileAsset('Site');return false;" no_redirect="1" envType='Site' redirectPath='${This.path}' />
		        </div><#-- row -->
		        </#if>
		      </div><#-- content -->
		</div><#-- siteAssets -->
		
		<div id="commonsAssets">
		      <div class="span3">
		        <div class="bloc" style="margin-right: 5px;" >
		          <div class="header">Arborescence</div>
		           <div class="treeroot"></div>
		           <ul id="treenavCommon" class="treeview"></ul>
		        </div> <!-- bloc -->
		      </div>
		
		      <div class="content span9">
		        <div class="row">
		          <div id="fileContentCommon" class="columns well" style="min-height:300px;">
		          </div>
		        </div> <#-- row -->
		      </div> <#-- content -->
		</div><#-- commonsAssets -->
		
		<div id="${envType}Assets">
		      <div class="span3">
		        <div class="bloc" style="margin-right: 5px;" >
		          <div class="header">Arborescence</div>
		          <div class="treeroot"></div>
		          <ul id="treenav${envType}" class="treeview"></ul>
		        </div> <!-- bloc -->
		      </div>
		
		      <div class="content span9">
		        <div class="row">
		          <div id="fileContent${envType}" class="columns well" style="min-height:300px;">
		          </div>
		        </div> <#-- row -->
		        <div class="row">
		          <div class="actions">
		            <a href="#" rel="addFileDialog${envType}" class="open-dialog btn btn-small btn-primary"><i class="icon-file"></i>Ajouter un fichier</a>
		            <a href="#" rel="addFolderDialog${envType}" class="open-dialog btn btn-small"><i class="icon-folder-close"></i>Ajouter un répertoire</a>
		          </div>
		
		          <div id="addFolderDialog${envType}" style="display:none;">
		            <h1>Ajouter un répertoire</h1>
		            <form class="form-horizontal" id="addFolderForm${envType}" action="${This.path}" onSubmit="addFolderAsset('${envType}');return false;" method="post">
		              <input type="hidden" name="doctype" value="Folder"/>
		              <input type="hidden" name="no_redirect" value="1" />
		              <input type="hidden" name="envType" value="${envType}" />
		              <input type="hidden" name="envName" value="${envName}" />
		              <fieldset>
		                <div class="control-group">
		                    <label class="control-label" for="title">Nom du répertoire</label>
		                      <div class="controls">
		                        <input name="dublincore:title" class="required input"/>
		                      </div>
		                    </div>
		              </fieldset>
		              <div class="actions">
		                <button type="submit" class="btn btn-primary required-fields" form-id="addFolderForm${envType}">Ajouter</button>
		              </div>
		            </form>
		          </div>
		
		          <#include "macros/add_file_dialog.ftl" />
		          <@addFileDialog action="${This.path}?callFunction=${callFunction}&calledRef=${calledRef}" onSubmit="addFileAsset('${envType}');return false;" no_redirect="1" envType='${envType}' envName="${envName}" redirectPath="${This.path}" />
		        </div> <#-- row -->
		      </div> <#-- content -->
		</div><#-- ${envType}Assets -->
		
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
	jQuery('#fileContent${envType}').on('click', 'a.sendToCallFunction', function() {
		sendToCallFunction(this, jQuery(this).data('url'));
		return false;
	});
	loadContentAsset("", "");
	loadContentAsset("", "site");
	loadContentAsset("", "${envType}");
    
    loadTreeNavSite();
    loadTreeNavCommon();
    loadTreeNavForum();
    <#if !envType??>
	if (location.search.indexOf("isCommon=true") > -1){
		displayCommonsAssets();
	}
	else{
		displaySiteAssets();
	}
    <#else>
	displayForumAssets();
    </#if>
  });

    function sendToCallFunction(obj, href) {
        window.opener.${callFunction}('${calledRef}', href<#if callFunction != "CKEDITOR.tools.callFunction" >, jQuery(obj).data('docid')</#if>);
        window.close();
    }
    
    function loadTreeNavSite() {
      loadTreeNav("#treenav", "${Context.modulePath}/${mySite.URL}/@assets/json?callFunction=${This.callFunction}&calledRef=${This.calledRef}",
          "assets-navtree", "#navtreecontrol");
    }
    
    function loadTreeNavCommon() {
      loadTreeNav("#treenavCommon", "${Context.modulePath}/${mySite.URL}/@assets/json?callFunction=${This.callFunction}&calledRef=${This.calledRef}&isCommon=true", 
          "assets-navtree-common", "#navtreeCommoncontrol");
    }
    
    function loadTreeNavForum() {
      loadTreeNav("#treenav${envType}", "${Context.modulePath}/${mySite.URL}/@assets/json?callFunction=${This.callFunction}&calledRef=${This.calledRef}&envName=${envName}&envType=${envType}", "assets-navtree-${envType}", "#navtree${envType}control");
    }
    
    function loadTreeNav(tagId, url, cookieId, control) {
      $(tagId).empty(); 
	  $(tagId).treeview({
        url: url,
        persist: "cookie",
        control: control,
        collapsed: true,
        cookieId: "${mySite.document.id}-"+cookieId
      });
    }
    
    function loadContentAsset(pathAssets, param) {
    
       var url = "${This.path}" + pathAssets;
       url += "/@views/content";
       url += '?callFunction=${callFunction}&calledRef=${calledRef}';
       if (param == "site")
       {
         $("#fileContent").load(encodeURI(url));
       } else if (param == "${envType}")
       {
         url += "&envName=${envName}&envType=${envType}";
         $("#fileContent${envType}").load(url);
       } else
       {
         url += "&isCommon=true";
         $("#fileContentCommon").load(encodeURI(url));
       }
    }
    
    function displayForumAssets() {
    	$("#commonsAssets").hide();
    	$("#navCommonsAssets").removeClass("active");
    	$("#siteAssets").hide();
    	$("#navSiteAssets").removeClass("active");
    	$("#${envType}Assets").show();
    	$("#nav${envType}Assets").addClass("active");
    }
    
    function displaySiteAssets() {
    	$("#commonsAssets").hide();
    	$("#navCommonsAssets").removeClass("active");
    	$("#${envType}Assets").hide();
    	$("#nav${envType}Assets").removeClass("active");
    	$("#siteAssets").show();
    	$("#navSiteAssets").addClass("active");
    }
    
    function displayCommonsAssets() {
    	$("#siteAssets").hide();
    	$("#navSiteAssets").removeClass("active");
    	$("#${envType}Assets").hide();
    	$("#nav${envType}Assets").removeClass("active");
    	$("#commonsAssets").show();
    	$("#navCommonsAssets").addClass("active");
    }
    
    function addFolderAsset(envType) {
    	jQuery('#waitingPopup').dialog2('open');
    	var action = $("#pathToAction"+envType).val();
    	jQuery.ajax({
			type: "POST",
			url: action,
			data: $("#addFolderForm" + envType).serialize(),
			success: function(msg){
                loadTreeNavSite();
                loadTreeNavForum();
				jQuery('#waitingPopup').dialog2('close');
			},
			error: function(msg){
				alert('add folder failed:' + msg.responseText );
				jQuery('#waitingPopup').dialog2('close');
			}
		});
		jQuery("#addFolderDialog"+envType).dialog2('close');
    }
    
    function addFileAsset(envType) {
        $("#addFileForm"+envType).attr("action", $("#pathToAction"+envType).val());
        $("#addFileForm"+envType).submit();
        jQuery('#addFileDialog'+envType).dialog2('close');
    }
    </script>
  </div><#-- FKtopContent -->
  </body>
</html>
