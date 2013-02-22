<#assign mySite=Common.siteDoc(Document).getSite() />
<#macro displayNewsNavigation newsDoc isPrevious=true accrocheMaxLength=200 >
	<#if newsDoc.labsnews.accroche?length < accrocheMaxLength >
		<#assign accroche = newsDoc.labsnews.accroche />
	<#else>
		<#assign accroche = newsDoc.labsnews.accroche?substring(0, accrocheMaxLength) + " ..." />
	</#if>
	<#assign picHtml = "" />
	<#assign hasSummaryPicture = false />
	<#if This.getLabsNews(newsDoc).hasSummaryPicture()>
		<#assign hasSummaryPicture = true />
		<#assign picHtml = '<div style="float: left;padding-bottom: 10px;padding-right: 5px;" ><img src="${Root.getLink(newsDoc)}/summaryPictureTruncated" /></div>' />
	</#if>
	<a href="${Root.getLink(newsDoc)}" style="float: <#if isPrevious>left<#else>right</#if>;"
		rel="popover" data-trigger="hover" data-content="<div <#if hasSummaryPicture>style='min-height: 100px;'</#if> >${picHtml?html}<div style='' >${accroche?html}</div></div>"
		data-original-title="${newsDoc.title?html}"
		data-placement="<#if isPrevious>right<#else>left</#if>" >
	<#if isPrevious>
	<i class="icon-backward" style="text-decoration: none;" ></i>${Context.getMessage('label.labsNews.navigation.previous')}
	<#else>
	${Context.getMessage('label.labsNews.navigation.next')}&nbsp;<i class="icon-forward" style="text-decoration: none;" ></i>
	</#if>
	</a>
</#macro>

<@extends src="/views/TemplatesBase/" + mySite.template.getTemplateName() + "/template.ftl">
  <#assign isAuthorized = Session.hasPermission(Document.ref, 'Write')>

  <@block name="title">${mySite.title}-${This.document.title}</@block>


  <@block name="scripts">
    <script type="text/javascript" src="${contextPath}/wro/labs.news.js"></script><#-- MUST BE BEFORE superBlock -->
    <@superBlock/>
    <script type="text/javascript" >
      jQuery(document).ready(function() {
        jQuery('.news-navigation a[rel=popover]').popover({offset: 10, html: true});
      });
    </script>
  </@block>

  <@block name="css">
    <link rel="stylesheet" type="text/css" media="all" href="${contextPath}/wro/labs.news.css"/>
    <@superBlock/>
    <#include "views/common/datepicker_css.ftl">
  </@block>



  <@block name="docactionsaddpage"></@block>
  <@block name="docactionsonpage"></@block>

  <#assign isContributor = This.previous.page?? && This.previous.page.isContributor(Context.principal.name) />
  <#if isContributor >
    <#assign layouts = This.columnLayoutsSelect />
  </#if>

  <@block name="content">
  <#include "macros/HtmlPage.ftl" />
		<script type="text/javascript" >
			<#assign nbrUserClassInput = 0 />
			var userClassInputTab = new Array() ;
        </script>
  	<#include "views/LabsNews/macroNews.ftl">
    <#if news??>
      <div class="container-fluid">
      	<#if This.hasPrevNewsDoc() || This.hasNextNewsDoc() >
      	  <div class="news-navigation" style="width: 100%;" >
      	  	<#if This.hasPrevNewsDoc() >
      	  		<@displayNewsNavigation newsDoc=This.prevNewsDoc />
      	  	</#if>
      	  	<#if This.hasNextNewsDoc() >
      	  		<@displayNewsNavigation newsDoc=This.nextNewsDoc isPrevious=false />
      	  	</#if>
      	  </div>
      	  <div style="clear: both;" ></div>
      	</#if>
          <section>
          	  <#if news != null>
	              <div class="page-header <@generateClassNewsVisibility news=news result="hiddenNews"/>">
	                <a name="section_0"></a>
	                <h1 class="titleHeaderNews">${news.title} <small>${Context.getMessage('label.labsNews.display.by')} ${news.lastContributorFullName}</small></h1>
	                <small>${Context.getMessage('label.labsNews.display.publish')} ${news.startPublication.time?string('dd MMMMM yyyy')}</small>
	              	<#if isContributor >
		              	<div class="editblock" style="margin-top: -15px;width: 100%;text-align: right;float: right">
							<a id="btnModifyPropsNews" class="btn" style="cursor: pointer;margin-right: 5px;" onclick="javascript:actionPropsNews();"><i class="icon-eye-open"></i>Modifier les propriétés</a>
					  	</div>
				  	</#if>
	              </div>
	          <#else>
	              <div class="page-header">
	                <h1>${Context.getMessage('label.labsNews.edit.title')} <small>${Context.getMessage('label.labsNews.display.by')}...</small></h1>
	                <small>${Context.getMessage('label.labsNews.display.publish')}...</small>
	              </div>
			  </#if>
			  <#if isContributor >
			  	<#assign news=news/>
			  	<#include "views/LabsNews/editProps.ftl" />
			  </#if>

		  	<#assign section_index=0/>



      		<div id="div_section_0_rows">
		  	<#list news.getRows() as row>
		  		<script type="text/javascript">
                	userClassInputTab[${nbrUserClassInput}] = [<@generateInputCssClass row=row />];
                </script>
		  		<#if isContributor >
		  		  <div id="div_row_${row_index}">
			        <div class="row-fluid<@generateCssClass row=row />" id="row_s${section_index}_r${row_index}">
			              <#list row.contents as content>
			              	  <div class="span${content.colNumber}">
				                    <div class="columns viewblock">
					                    <#if content.html == "">
					                        &nbsp;
					                    <#else>
					                      ${content.html}
					                    </#if>
				                    </div>

						              <div class="row-ckeditor columns editblock">
						                <div id="s_${section_index}_r_${row_index}_c_${content_index}" class="ckeditorBorder" style="cursor: pointer" >${content.html}</div>
						                <script type="text/javascript">
						                  $('#s_${section_index}_r_${row_index}_c_${content_index}').ckeip({
						                    e_url: '${This.path}/s/${section_index}/r/${row_index}/c/${content_index}',
						                    ckeditor_config: ckeditorconfig,
						                    emptyedit_message: "<div style='font-weight: bold;font-size: 18px;padding: 5px;text-decoration: underline;cursor: pointer'>${Context.getMessage('label.PageHtml.double_click_to_edit_content')}</div>",
						                    view_style: "span${content.colNumber} columns cke_hidden "
											}, reloadPageLabsNews);

											function reloadPageLabsNews(response, ckeObj, ckeip_html) {
												jQuery(ckeObj).closest('div.row-ckeditor').siblings('.viewblock').html(ckeip_html);
												scrollToRowAfterCkeip(response, ckeObj, ckeip_html);
											}
						                </script>
						                <noscript>
						                  <a  class="btn editblock" href="${This.path}/s/${section_index}/r/${row_index}/c/${content_index}/@views/edit">Modifier</a>
						                </noscript>
						                &nbsp; <!-- Needed to give an empty cell a content -->
						              </div>
						         </div>
			              </#list>
			         </div>
			         <div class=" editblock btn-group" style="float: right;">
					      	<a class="btn dropdown-toggle" data-toggle="dropdown"><i class="icon-cog"></i>Ligne <span class="caret"></span></a>
							<ul class="dropdown-menu" style="left: auto;right: 0px;">
								<li>
									<a href="#" onClick="javascript:openModifiyCSSLine('${This.path}/s/0/r/${row_index}', '${row.cssClass}', userClassInputTab[${nbrUserClassInput}]);" rel="modifyCSSLine" style="float: left;"><i class="icon-adjust"></i>Modifier le style</a>
									<a href="#" onclick="$('#rowdelete_s${section_index}_r${row_index}').submit();return false;"><i class="icon-remove"></i>Supprimer la ligne</a>
									<a href="#" onClick="javascript:moveUp('${This.path}/s/0/r/${row_index}', '${This.path}', 'div_row_${row_index}', '#div_section_0_rows>div');" title="Monter" alt="Monter"><i class="icon-arrow-up"></i>Monter</a>
		    						<a href="#" onClick="javascript:moveDown('${This.path}/s/0/r/${row_index}', '${This.path}', 'div_row_${row_index}', '#div_section_0_rows>div');" title="Descendre" alt="Descendre"><i class="icon-arrow-down"></i>Descendre</a>
								</li>
							</ul>
					  </div>
					  <form id="rowdelete_s${section_index}_r${row_index}" style="margin: 0px 0px 0px;" action="${This.path}/s/${section_index}/r/${row_index}/@delete" method="get" onsubmit="return confirm('Voulez vous vraiment supprimer la ligne ?');" >
					  </form>
					  <br />
			          <hr class="editblock"/>
			       </div>
			    <#else>
			      <div id="div_row_${row_index}">
			    	<div class="row-fluid<@generateCssClass row=row />" id="row_s${section_index}_r${row_index}">
		                  <#list row.contents as content>
		                    <div class="span${content.colNumber} columns">
		                      <#if content.html == "">
		                        &nbsp;
		                      <#else>
		                        ${content.html}
		                      </#if>
		                    </div>
		                  </#list>
		            </div>
		          </div>
			     </#if>
			     <#assign nbrUserClassInput = nbrUserClassInput + 1 />
		    </#list>
		    </div>

			<#if isContributor >
	          <div class="editblock">
			    <a href="#divAddRow_${section_index}" class="btn btn-primary btn-small" style="margin-bottom: 5px" id="actionAddLineOnSection_${section_index}" onClick="javascript:actionAddLine('${section_index}');" ><i class="icon-plus"></i>Ajouter une ligne</a>
		        <div id="divAddRow_${section_index}" class="well" style="padding: 5px;display: none;">
		            <div style="float: right;">
		          		<a href="#divAddRow_${section_index}" onClick="javascript:actionAddLine('${section_index}');" ><i class="icon-remove"></i></a>
		            </div>
		            <form class="form-horizontal" id="addrow" action="${This.path}/s/${section_index}" method="post" >
		              <input type="hidden" name="action" value="addrow"/>
		              	<fieldset>
			                <legend>Ajouter ligne(s)</legend>
			                <div class="control-group">
			                  <label class="control-label" for="title">Type de ligne</label>
			                  <div class="controls">
			                    <select name="rowTemplate">
			                    <#list layouts?keys as layoutCode >
			                      <option value="${layoutCode}">${Context.getMessage(layouts[layoutCode])}</option>
			                    </#list>
				                </select>
				                <select name="rowNumber">
			                      <option value="1" selected>Insérer 1 fois</option>
			                      <option value="2">Insérer 2 fois</option>
			                      <option value="3">Insérer 3 fois</option>
			                      <option value="4">Insérer 4 fois</option>
			                      <option value="5">Insérer 5 fois</option>
			                      <option value="6">Insérer 6 fois</option>
			                      <option value="7">Insérer 7 fois</option>
			                      <option value="8">Insérer 8 fois</option>
			                      <option value="9">Insérer 9 fois</option>
			                      <option value="10">Insérer 10 fois</option>
				                </select>
				                <button type="submit" class="btn btn-small btn-primary">Ajouter</button>
				                <div id="displayCssClass_${section_index}" style="display: none;">
				                	<br />Classe CSS : <input class="input-medium" name="cssClass" />
				                </div>
				                <div id="herfDisplayCssClass_${section_index}" style="cursor: pointer;" onClick="javascript:displayCssClass('${section_index}');">
				                	<br>Ajouter un style à la ligne
				                </div>
				                <p class="help-block">
				                    Sélectionnez le type de ligne à ajouter. Plusieurs modèles sont disponibles, les chiffres entre
				                    parenthèses représentent des pourcentages de taille de colonne.
				                </p>
			                  </div>

			                </div>
			              </fieldset>
		            </form>
		         </div>
	          </div>
	        </#if>
		  </section>
      </div>
      
      		<div id="div-modifyCSSLine" style="display:none;" >
		    	<h1>Modifier la ligne</h1>
		    	<form class="form-horizontal" action="${This.path}" id="form-modifyCSSLine" method="post">
		      		<input type="hidden" name="section" value=""/>
		      		<input type="hidden" name="row" value=""/>
		      		<input type="hidden" name="userClass" id="userClass" value=""/>
		      		<fieldset>
			            <div class="control-group">
			              <label class="control-label" for="userClassSelect">Styles utilisateur</label>
			              <div class="controls">
			                <select multiple id="userClassSelect" name="userClassSelect" style="width: 100%;">
			                	<#assign mapUserClass = This.getAvailableUserClass()>
			                	<#assign keys = mapUserClass?keys>
			                	<#if (keys?size > 0)>
			                		<#list keys as key>
			                			<option value="${mapUserClass[key]}">${key?js_string}</option>
			                		</#list>
			                	</#if>
			                </select>
			              </div>
			            </div>
			            <div class="control-group">
			              <label class="control-label" for="cssName">Classe CSS</label>
			              <div class="controls">
			                <input class="input-large" id="cssName" name="cssName" type="text" />
			              </div>
			            </div>

			            <div class="actions">
			              <button type="submit" class="btn btn-small btn-primary">Modifier</button>&nbsp;
			            </div>
	           		</fieldset>
	        	</form>
	    	</div>
      
    </#if>
  </@block>
	<@block name="pageCommentable">
	   	<#include "views/LabsComments/macroComments.ftl">
		<@displayAddComment pageCommentable=This.labsNews />
	</@block>
  
</@extends>