			<style media="all" type="text/css">
				.assistant {
					display: block;
					position: relative;
					top: 5%;
					left: 5%;
				}
				.dropdown-menu a {
					white-space: normal;
				}
				#divAssistantPagesPreview {
					margin-top: 30px;
					margin-left: 25px;
				}
			</style>
			<div class="editblock">
				<div id="editprops" style="display: none;">
					  <#if news != null>
					  	<div style="width: auto;">
  					  	  <h4>Prévisualisation du résumé</h4>
						  <section class="labsnews well">
						  	<div class="row-fluid" id="summaryNews${news.documentModel.ref}">
			          			<@generateSummaryNews news=news path=This.path withHref=false />
								<div style="font-size: 32px; margin-left: 15px;float: right;margin-top:5px;">
							    	<i class="icon-plus-sign openCloseBt" style="cursor: default;" ></i>
								</div>
				          	</div>
				          </section>
				        </div>
				       </#if>
				  	<#--<div style="width: 100%;text-align: right;">
						<a id="btnModifyPropsNews" class="btn" style="cursor: pointer;margin-right: 5px;" onclick="javascript:actionPropsNews();">Modifier les propriétés</a>
					</div>-->
					<div class="well" style="width: 730px;margin-left: auto;margin-right: auto;">
						<#--<h1>Editer les information de l'actualité</h1>-->
						<form class="form-horizontal" id="form-editNews" method="post" action="${This.path}" class="well" enctype="multipart/form-data" >
						  <input name="idPageTemplate" id="idPageTemplate" type="hidden" value="" />
						  <fieldset>
						    <legend>Propriétés de l'actualité</legend>
						    <#--Titre de la news-->
						    <div class="control-group">
						      <label class="control-label" for="dc:title">${Context.getMessage('label.labsNews.edit.title')}</label>
						      <div class="controls">
						        <input type="text" required-error-text="Le titre est obligatoire !" class="focused required span7" name="dc:title"  value="<#if news?? && news != null >${news.title?html}</#if>" />
						      </div>
						    </div>
							<#--Périodes de publication de la news-->
						    <div class="control-group">
						      <label class="control-label" for="newsPeriod">${Context.getMessage('label.labsNews.edit.period')}</label>
						      <div class="controls">
						        <input type="text" id="newsStartPublication" required-error-text="La date de début est obligatoire pour la période !" class="date-pick required" name="newsStartPublication"  <#if news?? && news != null && news.startPublication!=null> value="${news.startPublication.time?string("dd/MM/yyyy 'à' HH:mm")}" </#if> />
						        &nbsp;${Context.getMessage('label.labsNews.edit.au')}&nbsp;
						        <input type="text" id="newsEndPublication" class="date-pick" name="newsEndPublication"  <#if news?? && news != null && news.endPublication!=null> value="${news.endPublication.time?string("dd/MM/yyyy 'à' HH:mm")}" </#if> />
						      </div>
						    </div>
						    <#--Accroche de la news-->
						    <div class="control-group">
						      <label class="control-label" for="newsAccroche">${Context.getMessage('label.labsNews.edit.accroche')}</label>
						      <div class="controls">
						        <textarea class="span7" style="height:60px;" id="newsAccroche" name="newsAccroche"><#if news?? && news != null >${news.accroche}</#if></textarea>
						      </div>
						    </div>
						    <#--Commentaires sur la news-->
					        <div class="control-group">
								<div class="controls">
									<label class="checkbox" for="commentablePage">
									<input class="checkbox" id="commentablePage" type="checkbox" name="commentablePage" />
									${Context.getMessage('label.parameters.page.authorizedCommentable')}</label>
								</div>
					        </div>
						    <#--isTop-->
					        <div class="control-group">
								<div class="controls">
									<label class="checkbox" for="isTop">
									<input class="checkbox" id="isTop" type="checkbox" name="isTop" />
									${Context.getMessage('label.labsNews.isTop')}</label>
								</div>
					        </div>
						    <#--Photo de la news-->
						    <a class="btn" id="btnSetSummaryPicture" style="cursor: pointer;" onclick="javascript:openDownloadPicture();"><i class="icon-plus"></i>Associer une miniature</a>
						    <#if news?? && news != null && news.hasSummaryPicture() >
							    <div id="div-editPicture" style="float: right;">
							    	<#--Suppression de la photo -->
							    	<img src="${This.path}/summaryPictureTruncated" style="width:20ps;height: 20px;"/>
							    	<span onclick="javascript:deleteSummaryPicture('Voulez-vous supprimer l\'image?');" style="cursor: pointer;">
								    	<img title="${Context.getMessage('label.delete')}" src="${skinPath}/images/x.gif"/>
								  	</span>
								    <#--Editeur de la photo -->
								    <a style="cursor: pointer;" onclick="javascript:openCropPicture();">Recadrer l'image du résumé</a>
								    <input type="hidden" name="cropSummaryPicture" id="cropSummaryPicture" value="${news.cropCoords?html}" />
								    <input type="hidden" id="cropSummaryPictureOrigin" value="${news.cropCoords?html}" />
								</div>
							</#if>
							<#if news?? && news != null>
								<#if mySite?? && mySite.isAdministrator(Context.principal.name)>
						            <#--  To define as template   -->
						            <div class="control-group">
										<div class="controls">
									    	<label class="checkbox" for="pageTemplate">
									        <input class="checkbox" id="pageTemplate" type="checkbox" name="let:elementTemplate" <#if news.elementTemplate>checked="true"</#if> />
									        &nbsp;${Context.getMessage('label.parameters.page.template')}</label>
									    </div>
									</div>
									<div class="control-group" id="pageTemplatePreviewDiv" <#if !news.elementTemplate>style="display:none;"</#if>>
										<#if news.hasElementPreview() >
									    	<div id="divElementPreview" style="float: right; margin-right: 25px;" >
										        <img style="width:400px;cursor:pointer;"
										          title="${Context.getMessage('label.element.template.preview.delete')}" 
										          onclick="javascript:deleteTemplatePreviewBlob('${news.document.id}', '${Context.serverURL}/nuxeo/site', '${Context.getMessage('label.element.template.preview.delete.confirm')?js_string}');"
										          src="${Context.serverURL}/nuxeo/site/automation/files/${news.document.id}?path=%2Flet%3Apreview"/>
									      	</div>
									    </#if>
									    <label class="control-label" for="siteTemplatePreview">${Context.getMessage('label.parameters.page.preview')}</label>
									    <div class="controls">
									        <input name="let:preview" type="file" size="25" id="pagePreview" />
									    </div>
									</div>
								</#if>
							<#else>
								<div class="control-group">
							    	<label class="control-label" for="assistant">Mode de création</label>
							    	<div class="controls">
									    <label class="radio inline">
										  <input type="radio" name="assistant" id="assistant" value="assistant" onChange="javascript:changeAssistant('assistant');" checked />
										  Assistant
										</label>
										<label class="radio">
										  <input type="radio" name="assistant" id="assistant" onChange="javascript:changeAssistant('blankPage');" value="blankPage" />
										  Page vierge
										</label>
									</div>
								</div>
								<div class="row-fluid" id="divAssistantContent">
								  	<div class="span4">
								  		<h3 style="text-align: center;">Catégories</h3>
										<ul class="dropdown-menu assistant" role="menu" aria-labelledby="dropdownMenu" style="width: 100%">
											  <li><a tabindex="-1" onClick="javascript:loadPagesTemplate('${This.path}/@labsNewsTemplateOfSiteElementTemplate', '${skinPath}', this);" href="#"><i class="icon-arrow-right"></i>Actualités standards</a></li>
											  <li class="divider"></li>
											  <li><a tabindex="-1" onClick="javascript:loadPagesTemplate('${This.path}/@labsNewsTemplateOfSite', '${skinPath}', this);" href="#"><i class="icon-arrow-right"></i>${Context.getMessage('label.list.elements.site.template')}</a></li>
										</ul>
								  	</div><#-- /span4 -->
								  	<div class="span4">
								  		<h3 style="text-align: center;">Modèles d'actualités</h3>
								  		<div id="divAssistantPages"></div>
								  	</div><#-- /span4 -->
								  	<div class="span4" id="divAssistantPagesPreview">
								  		
								  	</div><#-- /span4 -->
								  	<br>
								  </div><#-- /row -->
							</#if>
						    <div class="actions" style="margin-left: 200px;margin-top: 20px;">
						      <button class="btn btn-primary required-fields" form-id="form-editNews"><i class='icon-ok'></i>${Context.getMessage('label.labsNews.edit.save')}</button>
						      <#if news?? && news != null>
						      	<a class="btn btn-danger" id="btnDeleteNews" onclick="javascript:if(confirm('${Context.getMessage('label.admin.labsnews.deleteConfirm')}')){deleteNews('${This.path}', '${This.previous.path}');};"><i class='icon-remove'></i>Supprimer l'actualité</a>
						      </#if>
						      <a class="btn" id="btnCloseProps" onclick="javascript:closePropsNews();"><i class='icon-eye-close'></i>Fermer</a>
						  	</div>
						  </fieldset>
						</form>
						<form id="formDeleteSummaryPicture" method="post" action="${This.path}/deleteSummaryPicture"></form>
					</div>
				</div>
			</div>
		      	<script type="text/javascript">
				  $(document).ready(function() {				  		
				  	  initEditDateNews();
				  	  if (location.search.indexOf("props=open") > -1){
				  	  	actionPropsNews();
				  	  }
				  	  else{
				  	  	closePropsNews();
				  	  }
				  	  <#if !(news?? && news != null)>
				  	  	jQuery("#form-editNews").clearForm();
				  	  	jQuery("#btnModifyPropsNews").remove();
				  	  	jQuery("#btnCloseProps").remove();
				  	  	jQuery("#btnSetSummaryPicture").remove();
					  	  <#if This.page.commentable >
				  	  	jQuery('#commentablePage').attr('checked', 'checked');
					  	  </#if>
					  	jQuery('#assistant').attr('checked', 'checked');
				  	  <#else>
					  	  <#if news.commentable >
				  	  	jQuery('#commentablePage').attr('checked', 'checked');
					  	  </#if>
					  	  <#if news.isTop() >
				  	  	jQuery('#isTop').attr('checked', 'checked');
					  	  </#if>
					  	
					    jQuery('#pageTemplate').click(function() {
							if (jQuery(this).is(':checked')) {
								jQuery('#pageTemplatePreviewDiv').show();
							} else {
								jQuery('#pageTemplatePreviewDiv').hide();
							}
						});
				  	  </#if>
				  });
			  	</script>
			  	<#if news?? && news != null && news.hasSummaryPicture() >
				  	<div id="divCropPicture" class="jcrop" style="display: none;vertical-align: middle">
				  		<h1>
				  			Découper une image
				  		</h1>
				  		<img id="summaryPicture" src="${This.path}/summaryPicture"/>
				  		<table cellpadding="5" cellspacing="0" border="0">
				  			<tbody>
				  				<trcellspacing="0">
				  					<td>
				  						<div style="width:120px;height:90px;overflow:hidden;">
				      						<img  src="${This.path}/summaryPictureTruncated"/>
				      					</div>
				  					</td>
				  					<td>
				  						<div style="width:120px;height:90px;overflow:hidden;">
				  							<img id="summaryPicturePreview" src="${This.path}/summaryPicture"/>
				  						</div>
				  					</td>
				  				</tr>
				  			</tbody>
				  		</table>		  	
				    </div>
			    </#if>
			    
			  	<#if news?? && news != null >
				  	<div id="divDownloadPicture" style="display: none;">
				  		<h1>
				  			Télécharger une image
				  		</h1>
				  		<form class="form-horizontal" id="form-downloadPicture" enctype="multipart/form-data" onSubmit="javascript:return false;" >
						  <fieldset>
						    <div id="divPictureNews" class="control-group">
						      <label class="control-label" for="newspicture">${Context.getMessage('label.labsNews.edit.picture')}</label>
						      <div class="controls">
						        <input type="file" class="input-xlarge input-file required" name="newsPicture" id="newsPicture"/>
						      </div>
						    </div>
						    <div class="actions">
						      <input type="submit" class="btn required-fields" form-id="form-downloadPicture" onClick="javascript:savePicture();" value="${Context.getMessage('label.labsNews.edit.save')}" />
						  	</div>
						  </fieldset>
						</form>  	
				    </div>
			    </#if>