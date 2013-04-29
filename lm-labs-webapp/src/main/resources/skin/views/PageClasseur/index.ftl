<#-- assign canWrite = Session.hasPermission(Document.ref, 'Write') -->
<#include "macros/PageClasseur_file_links.ftl" >
<#assign canWrite = This.page?? && This.page.isContributor(Context.principal.name)>
<#assign isAdministrator = This.page?? && This.page.isAdministrator(Context.principal.name)>
<@extends src="/views/TemplatesBase/" + This.page.template.getTemplateName() + "/template.ftl">
  <#assign mySite=Common.siteDoc(Document).getSite() />
  <@block name="title">${ mySite.title}-${This.document.title}</@block>

  <@block name="css">
        <link rel="stylesheet" type="text/css" media="all" href="${contextPath}/wro/labs.classeur.css"/><#-- MUST BE BEFORE superBlock -->
    <@superBlock/>
  </@block>

  <@block name="scripts">
    <script type="text/javascript" src="${contextPath}/wro/labs.classeur.js"></script>
    <@superBlock/>
  </@block>

  <@block name="content">
  <div class="container-fluid">
  <@tableOfContents anchorSelector="section > div > div.header-toc" anchorTitleSelector="h2 > span">

  <#assign folders = classeur.getFolders() />
  <div class="">
    <#if folders?size &gt; 0 && This.page.isDisplayable("pg:collapseType")>
		<img class='allFoldersOpened' src="${skinPath}/images/toggle_minus.png" onclick="slideAllFolders(this);" style="float: left; margin: 5px; cursor: pointer;" title="${Context.getMessage('label.PageClasseur.allFolders.collapse')}" alt="${Context.getMessage('command.PageClasseur.allFolders.collapse')}" />
	</#if>
	<#include "views/common/page_header.ftl">

    <#if folders?size &gt; 0>
	  <div id="classeurTopActions" class="editblock" style="margin-top: 5px;margin-bottom: 5px;">
	    <@mainButtons />
	  </div>
    </#if>

    <input type="hidden" id="folderPath" value="" />

    <div class="classeur" id="${This.path}">

  <#assign children = Session.getChildren(Document.ref, "Folder")>

  <#if folders?size &gt; 0>
  <#assign i=0 />
  <#list folders as folder>
    <section class="${folder.document.type}" id="${folder.document.id}" >
      
      <div>
      	<#if folder.getFiles()?size &gt; 0 && This.page.isDisplayable("pg:collapseType")>
	      <img class="openCloseBt" src="${skinPath}/images/toggle_minus.png" onclick="slideFolder(this, '');" style="float: left; margin: 5px; cursor: pointer;" title="${Context.getMessage('label.PageClasseur.collapse')}" alt="${Context.getMessage('command.PageClasseur.collapse')}" />
	    </#if>
	    <#if canWrite>
	      	<div class="editblock">
		      	<div class="btn-group" style="float:right;margin-top: 8px;">
		            <a class="btn btn-small dropdown-toggle" data-toggle="dropdown"><i class="icon-cog"></i>Actions <span class="caret"></span></a>
		            <ul class="dropdown-menu" style="left: auto;right: 0px;" >
		              <li><a 
		                <#if folders?first.document.id == folder.document.id>class="arrowOpacity" onclick="return false" <#else> onclick="moveFolder('${This.path}', '${Document.ref}', '${folder.document.id}', $('#${folder.document.id}').prev('section').attr('id'));return false"</#if>
		                href=""><i class="icon-arrow-up"></i>${Context.getMessage("command." + Document.type + ".moveup" )}</a></li>
		              <li><a 
		                <#if folders?last.document.id == folder.document.id>class="arrowOpacity" onclick="return false" <#else> onclick="moveFolder('${This.path}', '${Document.ref}', $('#${folder.document.id}').next('section').attr('id'), '${folder.document.id}');return false"</#if>
		                href=""><i class="icon-arrow-down"></i>${Context.getMessage("command." + Document.type + ".movedown" )}</a></li>
		              <li><a href="#" rel="addfile_${folder.document.id}_modal" class="open-dialog" ><i class="icon-upload"></i>${Context.getMessage("command." + Document.type + ".addFiles" )}</a></li>
		              <#-- This button submits the hidden delete form -->
		              <li><a href="#" onclick="$('#delete_${folder.document.id}').submit();return false;"><i class="icon-remove"></i>${Context.getMessage('command.PageClasseur.deleteFolder')}</a></li>
				      <#-- rename   -->
				      <li><a href="#" onClick='javascript:renameFolder("${This.path}/${folder.document.name}/@put", "${folder.document.id}");return false;'><i class="icon-edit"></i>${Context.getMessage("command." + Document.type + ".renameFolder" )}</a></li>	
		            </ul>
		          </div> <#-- btn-group -->
		    </div>
		   </#if>
	      
	    <div class="header-toc">
          <a name="section_${folder_index}" ></a>
          <h2>
          	<span id="spanFolderTitle${folder.document.id}">
          		${folder.document['dc:title']?html}
          	</span>
          </h2>
          <div id="divFolderDescription${folder.document.id}" class="divFolderDescription">
	          	${folder.document['dc:description']}
	      </div>
        </div>
        <#if canWrite>
	    <div class="folder-actions row-fluid editblock">
          
          <div id="addfile_${folder.document.id}_modal" >
              <h1>${Context.getMessage('command.PageClasseur.addFile')}</h1>
              <form class="form-horizontal form-upload-file" id="form-upload-file-${folder.document.id}"
                action="${This.path}/${folder.document.name}" method="post" enctype="multipart/form-data">
                <div class="alert alert-block no-fade" style="display:none;" >
                <a class="close" data-dismiss="alert">×</a>
                <h4 class="alert-heading"><i class="icon-warning-sign" style="font-size: 24px;" ></i>${Context.getMessage('label.PageClasseur.form.fileapi.notsupported.warning')}</h4>
                <p>${Context.getMessage('label.PageClasseur.form.fileapi.notsupported.full-message')}</p>
                </div>
    
                <fieldset>
                  <div class="control-group">
                        <label class="control-label" for="title">${Context.getMessage('label.PageClasseur.form.title')}</label>
                          <div class="controls">
                            <input type="text" name="title" class="input-large"/>
                          </div>
                        </div>
                        
                   <div class="control-group">
                        <label class="control-label" for="blob[]">${Context.getMessage('label.PageClasseur.choosefile')}</label>
                          <div class="controls">
                            <input type="file" name="blob[]" class="required focused input-file" multiple="multiple" onchange="filesSelected(event);return false;" />
                            <p class="help-block files-size" style="display:none;" ></p>
                            <p class="help-block files-nbr" style="display:none;" ></p>
                            <p class="help-block" ><strong>${Context.getMessage('label.note')}: </strong>${Context.getMessage('help.PageClasseur.choosefile')}</p>
                            <p class="help-block" ><strong>${Context.getMessage('label.warning')}: </strong>${Context.getMessage('help.PageClasseur.choosefile.limits', This.maxNbrUploadFiles, This.maxTotalFileSizeInMB)}</p>
                          </div>
                        </div>
                   <div class="control-group" style="display:none;" >
                        <label class="control-label" >${Context.getMessage('label.PageClasseur.form.selectedFiles')}</label>
                        <div class="controls selectedFiles" ></div>
                   </div>

                   <div class="control-group">
                        <label class="control-label" for="description">${Context.getMessage('label.PageClasseur.form.description')}</label>
                          <div class="controls">
                            <textarea class="input" name="description"></textarea>
                          </div>
                        </div>
                  </fieldset>

              <div class="actions">
                <button class="btn btn-primary required-fields addFilesBtn" form-id="form-upload-file-${folder.document.id}" >Envoyer</button>
              </div>
              </form>

            </div> <#-- /modal -->

              <form id="delete_${folder.document.id}" onsubmit="return confirm('${Context.getMessage('label.PageClasseur.delete.folder.confirm')}');" action="${This.path}/${folder.document.name}/@delete" method="get" enctype="multipart/form-data">
              </form>

        </div> <#-- folder-actions -->
        </#if> <#-- canWrite -->
      </div> <#-- page-header -->

      <@displayChildren folder=folder />
    </section> <!-- Folder -->
    <#assign i=i+1 />
  </#list>
  </#if>

  <div id="classeurBottomActions" class="editblock">
    <@mainButtons />
  </div>

  </div><!-- table -->


<div id="div-moveElements" style="display: none;">
	<h1>${Context.getMessage('label.PageClasseur.moveElements.title')}</h1>
	<form class="form-horizontal" onsubmit="javascript:bulkMove();return false;">
		<fieldset>
	      <div class="control-group">
	          <label class="control-label" for="selectedFolder">${Context.getMessage('label.PageClasseur.moveElements.selectedFolder')}</label>
	            <div class="controls">
          			<#list folders as folder>
	              		<label class="radio">
                			<input type="radio" id="moveElementsSelectedFolder" name="moveElementsSelectedFolder" value="${folder.document.id}" <#if folders?first.document.id == folder.document.id>checked</#if>>
                			${folder.document['dc:title']?html}<br>
              			</label>	
            		</#list>
	            </div>
	       </div>
      </fieldset>
      <div class="actions">
	    <button class="btn btn-primary">Valider</button>
	  </div>									
	</form>
</div>

<div id="div-addfolder" style="display: none;" >
    <h1>${Context.getMessage('label.PageClasseur.form.folder.title')}</h1>
  <form class="ajax form-horizontal" id="form-folder" action="${This.path}" method="post" enctype="multipart/form-data">
      <fieldset>
	      <div class="control-group">
	          <label class="control-label" for="dc:title">${Context.getMessage('label.PageClasseur.form.folder.name')}</label>
	            <div class="controls">
	              <input id="folderName" name="dc:title" class="focused required input-xlarge"/>
	            </div>
	       </div>
		   <div class="control-group">
	          <label class="control-label" for="dc:description">${Context.getMessage('label.PageClasseur.form.folder.description')}</label>
	            <div class="controls">
	              <textarea rows="10" cols="80"  name="dc:description" id="folderDescription"></textarea>
	            </div>
	       </div>
      </fieldset>

  <div class="actions">
    <button class="btn btn-primary required-fields" form-id="form-folder">Envoyer</button>
  </div>
  </form>

</div>

<div id="div-renameTitleElement" style="display: none;" >
    <h1>${Context.getMessage('label.PageClasseur.form.rename.title')}</h1>
  <form id="form-renameTitleElement" class="form-horizontal" action="" method="post">
      <fieldset>
	      <div class="control-group">
	          <label class="control-label" for="dc:title">${Context.getMessage('label.PageClasseur.form.title')}</label>
	          <div class="controls">
	            <input id="renameTitleElement" name="dc:title" class="required input-xlarge"/>
	          </div>
	      </div>
	      <div class="control-group">
	          <label class="control-label" for="dc:description">${Context.getMessage('label.PageClasseur.form.description')}</label>
	          <div class="controls">
	            <textarea name="dc:description" id="descriptionElement" class="input-xlarge"></textarea>
	          </div>
	      </div>
      </fieldset>
	  <div class="actions">
	    <button class="btn btn-primary required-fields" form-id="form-renameTitleElement">${Context.getMessage('label.PageClasseur.form.rename.save')}</button>
	  </div>
  </form>

</div>

<#macro mainButtons >
    <#if canWrite && Session.hasPermission(Document.ref, 'AddChildren') >
    <button class="btn btn-primary btn-small addFolder" ><i class="icon-plus"></i>${Context.getMessage('command.' + Document.type + '.addFolder')}</button>
    </#if>
    <#if canWrite>
    <div class="btn-group" style="margin-left: 11em;" >
        <a class="btn btn-small dropdown-toggle arrowOpacity" data-toggle=""><i class="icon-cog"></i>${Context.getMessage('command.PageClasseur.selectionActions')} <span class="caret"></span></a>
        <ul class="dropdown-menu" style="min-width: 40px;" >
        <#if canWrite && Session.hasPermission(Document.ref, 'RemoveChildren') >
          <li><a href="#" class="selectionActionsBt deleteSelection" ><i class="icon-remove"></i>${Context.getMessage('command.PageClasseur.deleteSelection')}</a></li>
          <#if folders?size &gt; 1>
	      	<li><a href="#" class="selectionActionsBt moveSelection"><i class="icon-move"></i>${Context.getMessage("command.PageClasseur.moveElements" )}</a></li>
	      </#if>
        </#if>
        <#if isAdministrator >
          <li><a href="#" class="selectionActionsBt showSelection" ><i class="icon-eye-open"></i>${Context.getMessage('command.PageClasseur.showSelection')}</a></li>
          <li><a href="#" class="selectionActionsBt hideSelection" ><i class="icon-eye-close"></i>${Context.getMessage('command.PageClasseur.hideSelection')}</a></li>
        </#if>
        </ul>
    </div>
    </#if>
</#macro>

<#macro displayChildren folder recurse=false>

  <#if folder.getFiles()?size &gt; 0>
  <div class="folder-collapsable" >
  <table class="table table-striped classeurFiles bs table-bordered labstable" >
  <thead>
    <tr>
      <th class="header noSort">&nbsp;</th>
      <#if canWrite>
      <th class="noSort header editblock" style="min-width: 25px">
        <input type="checkbox" name="checkoptionsFolder" value="${folder.document.id}" title="${Context.getMessage('label.PageClasseur.folder.checkbox')}"/>
      </th>
      </#if>
      <th class="header">${Context.getMessage('label.PageClasseur.tableheader.filename')}</th>
      <th class="header" style="min-width: 42px">${Context.getMessage('label.PageClasseur.tableheader.size')}</th>
      <#-- <th>${Context.getMessage('label.PageClasseur.tableheader.version')}</th> -->
      <th class="header">${Context.getMessage('label.PageClasseur.tableheader.modified')}</th>
      <th class="header">${Context.getMessage('label.PageClasseur.tableheader.creator')}</th>
      <th class="header noSort actions" <#--style="min-width: 69px; width: 104px;"-->>&nbsp;</th>
    </tr>
  </thead>
  <tbody>
  <#list folder.getFiles() as child>
  <tr class="main ${child.id}<#if child.facets?seq_contains("LabsHidden")> hiddenThrough editblock</#if>">
    <td>
      <img title="${child.type}" alt="${child.type}/" src="/nuxeo${child.common.icon}" />
    </td>
    <#if canWrite>
	    <td <#if !child.facets?seq_contains("LabsHidden")>class="editblock"</#if>>
	      <#if (child.facets?seq_contains("Folderish") == false) >
	        <input type="checkbox" name="checkoptions" value="${child.id}" />
	      </#if>
	    </td>
    </#if>
    <#if child.facets?seq_contains("Folderish") == false >
      <#assign modifDate = child['dc:modified']?datetime />
      <#assign modifDateStr = modifDate?string("EEEE dd MMMM yyyy HH:mm") />
      <#assign filename = child['dc:title'] />
      <#assign words = filename?word_list />
      <#assign isModifiedFilename = false />
      <#assign max_len_word = 50 />
      <#list words as word>
      	<#if (word?length > max_len_word)>
      		<#assign isModifiedFilename = true />
      		<#break>
      	</#if>
      </#list>
      <#assign blob = This.getBlobHolder(child).blob />
      <#assign blobLenght = blob.length />
      <#assign max_lenght = This.getPropertyMaxSizeFileRead() />
      <td>
      	<#if (isModifiedFilename)>
      		<span title="${blob.filename} - ${child.name} - ${child['dc:description']?html}">${filename?substring(0, max_len_word)}...</span>
      	<#else>
      		<span title="${blob.filename} - ${child['dc:description']?html}">${filename?html}</span><span class="sortValue">${filename?html}</span>
      	</#if>
      </td>
      <td>${bytesFormat(blobLenght, "K", "fr_FR")}<span class="sortValue">${blobLenght?string.computer}</span></td>
      <#-- <td>${child.versionLabel}</span></td> -->
      <td><span title="${modifDateStr}" >${Context.getMessage('label.PageClasseur.table.dateInWordsFormat',[dateInWords(modifDate)])}</span><span class="sortValue">${modifDate?string("yyyyMMddHHmmss")}</span></td>
      <td><span title="${child['dc:creator']}" >${userFullName(child['dc:creator'])}</span></td>
      <td class="actions" >
        <@fileDownloadLink url="${This.path}/${folder.document.name}/${Common.quoteURIPathComponent(child.name)}/@blob/" tooltip="${Context.getMessage('command.PageClasseur.download')}" />
        <#if (max_lenght > blobLenght) && This.hasConvertersForHtml(blob.mimeType)>
          <@fileDisplayLink url="${This.path}/${folder.document.name}/${Common.quoteURIPathComponent(child.name)}/@blob/preview" tooltip="${Context.getMessage('command.PageClasseur.display')}" />
        </#if>
      <#if canWrite>
      	<div  class="<#if !child.facets?seq_contains("LabsHidden")>editblock</#if> btn-group" style=" float:right;" >
        <a class="btn btn-mini dropdown-toggle" data-toggle="dropdown"><i class="icon-cog"></i> <span class="caret"></span></a>
        <ul class="dropdown-menu" style="left: auto;right: 0px;min-width: 0px;" >
      		<li><a href="#" onclick="openRenameTitleElement('${child['dc:title']?js_string?html}', '${child['dc:description']?js_string?html}', '${This.path}/${folder.document.name}/${Common.quoteURIPathComponent(child.name)}/@put');return false;"><i class="icon-edit"></i>${Context.getMessage('command.PageClasseur.renameFile')}</a></li>
            <li><a href="#" onclick="$('#docdelete_${child.id}').submit()"><i class="icon-remove"></i>${ Context.getMessage('command.PageClasseur.deleteFile')}</a></li>
        </ul>
        </div>
        <form id="docdelete_${child.id}" action="${This.path}/${folder.document.name}/${Common.quoteURIPathComponent(child.name)}/@delete" onsubmit="return confirm('${Context.getMessage('label.PageClasseur.delete.file.confirm')}')">
        </form>
      </#if>
      </td>

    </#if>
  </tr>
  </#list>
  </tbody>
  </table>
  </div>
  </#if>
</#macro>
  </div>
  </@tableOfContents>
  </div>
  </@block>
  
  <@block name="bottom-page-js" >
    <@superBlock />
    <#include "views/PageClasseur/PageClasseur-bottom-js.ftl" />
  </@block>
  
</@extends>