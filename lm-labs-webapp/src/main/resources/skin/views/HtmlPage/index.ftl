<@extends src="/views/TemplatesBase/" + This.page.template.getTemplateName() + "/template.ftl">
  <#include "macros/widgets.ftl" />
  <#assign maxSpanSize = 12 />
  <#assign nbrOsGadgets = 0 />
  <#assign mySite=Common.siteDoc(Document).getSite() />
  <#assign availableHtmlWidgets = Common.declaredHtmlWidgets />
  <#assign sectionsViewMode = This.contentView />
  <#if sectionsViewMode?contains('_') >
    <#assign basicSectionsViewMode = sectionsViewMode?split('_')[0] />
  <#else>
    <#assign basicSectionsViewMode = sectionsViewMode />
  </#if>
  <@block name="title">${mySite.title}-${This.document.title}</@block>

  <@block name="css">
        <link rel="stylesheet" type="text/css" media="all" href="${contextPath}/wro/labs.pagehtml.css"/><#-- MUST BE BEFORE superBlock -->
    <@superBlock/>
        <!-- Doesnt compile in wro... :-() -->
        <link rel="stylesheet" type="text/css" media="all" href="${skinPath}/css/jquery/jquery.fancybox-1.3.4.css" />
  </@block>

  <@block name="scripts">
      <script type="text/javascript" src="${contextPath}/wro/labs.pagehtml.js"></script>
    <@superBlock/>
        <script type="text/javascript" >
			jQuery(document).ready(function() {
				setOpensocialOptions('${contextPath}/', '${Context.locale.language}');
				<#if basicSectionsViewMode == "tabbed" || basicSectionsViewMode == "pills" >
				jQuery('div.tab-pane').each(function(index, value) {
					initOpensocialGadgets(value);
				});
				<#elseif basicSectionsViewMode == "carousel" >
				jQuery(this).find('div.item.active').each(function(index, value) {
					initOpensocialGadgets(value);
				});
				<#else> <#-- basicSectionsViewMode == "default" -->
				initOpensocialGadgets(jQuery('#divPageHTML'));
				</#if>
			});
        </script>

  </@block>

  <@block name="content">
  <#--
  <@cache name="${This.cacheName}" key="${This.cacheKey}" >
  -->
  <@tableOfContents>
   	<div class="container-fluid" style="padding-left: 10px; padding-right: 10px;" >
	<#include "views/common/page_header.ftl">
  <#assign isContributor = This.page?? && This.page.isContributor(Context.principal.name) />
  <#if isContributor >
    <#assign layouts = This.columnLayoutsSelect />
  </#if>
<#include "macros/HtmlPage.ftl" />
		<script type="text/javascript" >
			<#assign nbrUserClassInput = 0 />
			var userClassInputTab = new Array() ;
        </script>
  <#assign sections = page.sections />
  <#if basicSectionsViewMode == "tabbed" || basicSectionsViewMode == "carousel" >
  	<#include "views/HtmlPage/sectionsView_${basicSectionsViewMode}.ftl" />
  <#elseif basicSectionsViewMode == "pills" >
  	<#include "views/HtmlPage/sectionsView_tabbed.ftl" />
  </#if>
  <div id="divPageHTML" class="<#if basicSectionsViewMode != "default" >editblock</#if>" >
  <#if isContributor || (!isContributor && !This.page.isAdministrator(Context.principal.name) && basicSectionsViewMode == "default") >
  <#list sections as section>
  	<div id="div_section_${section_index}" >
	    <section id="section_${section_index}">
	        <div class="page-header"<#if section.title?length == 0 && section.description?length == 0 > style="padding-bottom: 0px;"</#if> >
	            <a name="section_${section_index}"></a>
	            <h1 style="display:inline;">${section.title}</h1><h2 style="display:inline;"> <small>${section.description}</small></h2>
		        <#if isContributor >
			        <div class=" editblock btn-group pull-right" style="float: right;margin-top: 7px;">
				      	<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><i class="icon-cog"></i>Section <span class="caret"></span></a>
						<ul class="dropdown-menu" style="left: auto;right: 0px;">
							<li>
								<a id="addsectionlink" href="#" rel="addsection" class="open-dialog" ><i class="icon-plus"></i>Ajouter une section</a>
								<a href="#" rel="editsection_${section_index}" class="open-dialog" ><i class="icon-edit"></i>Modifier la section</a>
								<a href="#section_${section_index}" id="actionAddLineOnSection_${section_index}" onClick="javascript:actionAddLine('${section_index}');" ><i class="icon-plus-sign"></i>Ajouter ligne(s)</a>
								<a href="#" onclick="if(confirm('Voulez vous vraiment supprimer cette section ?')) { $('#frm_section_${section_index}_delete').submit();} ;return false;"><i class="icon-remove"></i>Supprimer la section</a>
								<a href="#" onClick="javascript:moveUp('${This.path}/s/${section_index}', '${This.path}#section_${section_index - 1}', 'div_section_${section_index}', '#divPageHTML>div');" title="Monter" alt="Monter"><i class="icon-arrow-up"></i>Monter</a>
	    						<a href="#" onClick="javascript:moveDown('${This.path}/s/${section_index}', '${This.path}#section_${section_index + 1}', 'div_section_${section_index}', '#divPageHTML>div');" title="Descendre" alt="Descendre"><i class="icon-arrow-down"></i>Descendre</a>
							</li>
						</ul>
				    </div>
				</#if>
				<#if This.page.isDisplayable("pg:collapseType") >
				    <div class="viewblock" style="font-size: 32px; float: right; margin-top: 7px;" >
				    	<i class="icon-minus-sign openCloseBt" title="${Context.getMessage('label.HtmlPage.collapse')}" onclick="slideSection(this, '');" ></i>
				    </div>
				</#if>
	        </div>

	        <#if isContributor >
	        	<div class="editblock">
			        <div id="divAddRow_${section_index}" class="well" style="padding: 5px;display: none;">
			          <div style="float: right;">
			          	<a href="#section_${section_index}" onClick="javascript:actionAddLine('${section_index}');" ><i class="icon-remove"></i></a>
			          </div>
			          <form class="form-horizontal" id="addrow_${section_index}" action="${This.path}/s/${section_index}" method="post" >
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

	          <div id="editsection_${section_index}" class="dialog2 sections-edition" style="display: none;" >
	          	  <h1>Modifier la section</h1>
			      <form class="form-horizontal " name="formEditsection_${section_index}" action="${This.path}/s/${section_index}/@put" method="post">
				      <fieldset>
			            <div class="control-group">
			              <label class="control-label" for="title">Titre</label>
			              <div class="controls">
			                <input class="input-large" id="sectionTitle" name="title" size="30" type="text" value="${section.title?html}"/>
			              </div>
			            </div>

			            <div class="control-group">
			              <label class="control-label" for="description">Sous-titre</label>
			              <div class="controls">
			                <input class="input-large" id="sectionDescription" name="description" size="30" type="text" value="${section.description}"/>
			                <p class="help-block">
			                  Texte ajouté en petit à côté du titre
			                </p>
			              </div>
			            </div>
				      </fieldset>
			          <div class="actions">
			            <button onclick="javascript:jQuery('#waitingPopup').dialog2('open');document.formEditsection_${section_index}.submit();return true;" class="btn btn-primary">Modifier</button>&nbsp;
			          </div>
			      </form>
	          </div>
	       	  <!-- Hidden form to handle delete action -->
	          <form action="${This.path}/s/${section_index}/@delete" method="get" id="frm_section_${section_index}_delete">
	          </form>

			</#if>

			<div class="section-collapsable" id="div_section_${section_index}_rows">
			<#assign rows = section.getRows() />
	        <#list rows as row>
	        	<script type="text/javascript">
                	userClassInputTab[${nbrUserClassInput}] = [<@generateInputCssClass row=row />];
                </script>
	        	<#if isContributor >
	        		<div id="div_row_${row_index}">
			          <div class="row-fluid<@generateCssClass row=row />" id="row_s${section_index}_r${row_index}">
			              <#list row.contents as content>
				              <div class="span<#if maxSpanSize != content.colNumber >${content.colNumber}</#if>">
		                      <#assign isWidgetCol = false />
		                      <#assign isOsGadgetCol = false />
		                      <#assign widgets = [] />
		                      <@determineWidgetType content=content />
		                      <#if isOsGadgetCol >
		                        <div id="gadgetCol-s_${section_index}_r_${row_index}_c_${content_index}" class="columns viewblock" >
		                        <#assign nbrOsGadgets = nbrOsGadgets + 1 />
		                        <script type="text/javascript">
		                        	userPrefsTab['${widgets[0].doc.id}'] = eval ( '(${Common.getUserPrefsFormatJS(widgets[0].userPrefs)?js_string})' );
		                        </script>
		                        
		                        <div id="${widgets[0].doc.id}" class="opensocialGadgets gadget-${widgets[0].name} bloc"
									data-gadget-title="${widgets[0].name}"
		                        	data-gadget-specurl="${widgets[0].specUrl}"
		                        >
		                        </div>
		                        </div>
		                        <div class="columns editblock bloc" style="text-align: center;" >
		                        	<@editblockHiddenInputs widget=widgets[0] sectionIdx=section_index rowIdx=row_index contentIdx=content_index />
		                            <#assign widgetTitle = Context.getMessage('label.HtmlPage.widget.' + widgets[0].type.type() + '.' + widgets[0].name) />
		                            <#if widgetTitle?starts_with('!') >
		                                <#assign widgetTitle = widgets[0].name />
		                            </#if>
		                            <a class="btn open-dialog" rel="divConfigGadget" ><i class="icon-edit"></i>${Context.getMessage('command.HtmlPage.widget.config.button')} ${widgetTitle}</a>
		                        </div>
		                      <#elseif isWidgetCol >
		                        <#if Common.getWidgetsWithCustomConfigView(widgets[0].type.type())?seq_contains(widgets[0].name) >
			                    <div class="columns viewblock">
			                    	<@displayContentHtmlWidget widget=widgets[0] widgetMode="view" sectionIdx=section_index rowIdx=row_index columnIdx=content_index content=content />
			                    </div>
					            <div class="columns editblock bloc" style="text-align: center;">
		                        	<@editblockHiddenInputs widget=widgets[0] sectionIdx=section_index rowIdx=row_index contentIdx=content_index />
		                            <#assign widgetTitle = Context.getMessage('label.HtmlPage.widget.' + widgets[0].type.type() + '.' + widgets[0].name) />
		                            <#if widgetTitle?starts_with('!') >
		                                <#assign widgetTitle = widgets[0].name />
		                            </#if>
		                            <a class="btn open-dialog" rel="divConfigGadget" ><i class="icon-edit"></i>${Context.getMessage('command.HtmlPage.widget.config.button')} ${widgetTitle}</a>
					            </div>
		                        <#else>
		                        <div class="columns" >
			                    	<@displayContentHtmlWidget widget=widgets[0] widgetMode="edit" sectionIdx=section_index rowIdx=row_index columnIdx=content_index />
		                        </div>
		                        </#if>
		                      <#else>
			                    <div class="columns viewblock">
			                       <@displayContentHtml content=content />
			                    </div>
					            <div class="row-ckeditor columns editblock toc-noreplace" style="display:none;" >
					                <div id="s_${section_index}_r_${row_index}_c_${content_index}" class="ckeditorBorder" style="cursor: pointer;" >${content.html}</div>
					            	<div class="col-link" >
					            		<input type="hidden" value="${This.path}/s/${section_index}/r/${row_index}/c/${content_index}" ></input>
				            			<a class="open-dialog" rel="divColumnUrl" style="text-decoration:none;" title="${Context.getMessage('tooltip.ckeditor.hover.button.link')}" >
						            		<i class="icon-link" ></i>
				            			</a>
				            			<a href="#" class="hidden-desktop" style="text-decoration:none;" title="${Context.getMessage('tooltip.ckeditor.hover.button.edit')}" 
				            				onclick="jQuery('#s_${section_index}_r_${row_index}_c_${content_index}').dblclick();return false;" >
						            		<i class="icon-pencil" ></i>
				            			</a>
					            	</div>
					                <script type="text/javascript">
					                  $('#s_${section_index}_r_${row_index}_c_${content_index}').ckeip({
					                    e_url: '${This.path}/s/${section_index}/r/${row_index}/c/${content_index}',
					                    ckeditor_config: ckeditorconfig,
					                    emptyedit_message: "<div style='font-weight: bold;font-size: 18px;padding: 5px;text-decoration: underline;cursor: pointer'>${Context.getMessage('label.ckeditor.double_click_to_edit_content')}</div>",
					                    save_button_state: true,
					                    view_style: "span${content.colNumber} columns cke_hidden "
					                    }, reloadPageForTocIfNeeded);
					                </script>
					                <noscript>
					                  <a  class="btn" href="${This.path}/s/${section_index}/r/${row_index}/c/${content_index}/@views/edit">Modifier</a>
					                </noscript>
					                &nbsp; <!-- Needed to give an empty cell a content -->
					            </div>
		                      </#if>
					          </div>
			              </#list>

			          </div>
					  <div class=" editblock btn-group" style="float: right;">
					      	<a class="btn dropdown-toggle" data-toggle="dropdown"><i class="icon-cog"> </i>Ligne <span class="caret"></span></a>
							<ul class="dropdown-menu" style="left: auto;right: 0px;">
								<li>
								    <input type="hidden" class="section-index-value" value="${section_index}" />
		                            <input type="hidden" class="row-index-value" value="${row_index}" />
		                            <input type="hidden" class="content-index-value" value="${content_index}" />
									<a href="#" onClick="javascript:openModifiyCSSLine('${This.path}/s/${section_index}/r/${row_index}', '${row.cssClass}', userClassInputTab[${nbrUserClassInput}]);" rel="modifyCSSLine" style="float: left;"><i class="icon-adjust"></i>Modifier le style</a>
									<a href="#" onclick="$('#rowdelete_s${section_index}_r${row_index}').submit();return false;"><i class="icon-remove"></i>Supprimer la ligne</a>
									<a href="#" class="open-dialog" rel="divConfigRowGadgets" ><i class="icon-gift"></i>${Context.getMessage('command.HtmlPage.row.widgets.config.button')}</a>
									<a href="#" onClick="javascript:moveUp('${This.path}/s/${section_index}/r/${row_index}', '${This.path}#section_${section_index - 1}', 'div_row_${row_index}', '#div_section_${section_index}_rows>div');" title="Monter" alt="Monter"><i class="icon-arrow-up"></i>Monter</a>
		    						<a href="#" onClick="javascript:moveDown('${This.path}/s/${section_index}/r/${row_index}', '${This.path}#section_${section_index + 1}', 'div_row_${row_index}', '#div_section_${section_index}_rows>div');" title="Descendre" alt="Descendre"><i class="icon-arrow-down"></i>Descendre</a>
								</li>
							</ul>
					  </div>
					  <form id="rowdelete_s${section_index}_r${row_index}" style="margin: 0px 0px 0px" action="${This.path}/s/${section_index}/r/${row_index}/@delete" method="get" onsubmit="return confirm('Voulez vous vraiment supprimer la ligne ?');" >
					  </form>
					  <br />
			          <hr class="editblock"/>
			    	</div>
		        <#else>
		           <div class="row-fluid<@generateCssClass row=row />" id="row_s${section_index}_r${row_index}">
		              <#list row.contents as content>
	                      <#assign isWidgetCol = false />
	                      <#assign isOsGadgetCol = false />
	                      <#assign widgets = [] />
	                      <@determineWidgetType content=content />
	                      <#if isOsGadgetCol >
	                        <div id="gadgetCol-s_${section_index}_r_${row_index}_c_${content_index}" class="span<#if maxSpanSize != content.colNumber >${content.colNumber}</#if> columns" >
	                        <#assign nbrOsGadgets = nbrOsGadgets + 1 />
		                        <script type="text/javascript">
		                        	userPrefsTab['${widgets[0].doc.id}'] = eval ( '(${Common.getUserPrefsFormatJS(widgets[0].userPrefs)?js_string})' );
		                        </script>
	                        
	                        <div id="${widgets[0].doc.id}" class="opensocialGadgets gadget-${widgets[0].name} bloc"
	                        	data-gadget-specurl="${widgets[0].specUrl}"
								data-gadget-title="${widgets[0].name}"
	                        >
	                        </div>
	                        </div>
	                      <#elseif isWidgetCol >
	                        <div class="span<#if maxSpanSize != content.colNumber >${content.colNumber}</#if> columns" >
	                    	<@displayContentHtmlWidget widget=widgets[0] widgetMode="view" sectionIdx=section_index rowIdx=row_index columnIdx=content_index content=content />
	                        </div>
	                      <#else>
	    	                <div class="span<#if maxSpanSize != content.colNumber >${content.colNumber}</#if> columns">
	                           <@displayContentHtml content=content />
	    	                </div>
	                      </#if>
		              </#list>
		           </div>
		        </#if>
	        </#list>
			</div>
			<#assign nbrUserClassInput = nbrUserClassInput + 1 />
	    </section>
	</div>
  </#list>
  </#if>
 </div>

		<#if isContributor >
			<#if (page.sections?size == 0)>
				<div class="editblock">
					<br />
			    	<a id="addsectionlink" href="#" rel="addsection" class="btn btn-small btn-primary open-dialog" ><i class="icon-plus"></i>Ajouter une section</a>
			    </div>
			</#if>
		    <#-- Add Section -->
		    <div id="addsection" style="display:none;" >
		    	<h1>Ajouter une section</h1>
		    	<form class="form-horizontal" id="addsectionfrm" action="${This.path}" method="post" submit="javascript:jQuery('#waitingPopup').dialog2('open');this.submit();return true;">
		      		<input type="hidden" name="action" value="addsection"/>
		      		<fieldset>
			            <div class="control-group">
			              <label class="control-label" for="title">Titre</label>
			              <div class="controls">
			                <input class="input-large" id="sectionTitle" name="title" size="30" type="text" />
			              </div>
			            </div>


			            <div class="control-group">
			              <label class="control-label" for="description">Sous-titre</label>
			              <div class="controls">
			                <input class="input-large" id="sectionDescription" name="description" size="30" type="text" />
			                <p class="help-block">
			                  Texte ajouté en petit à côté du titre
			                </p>
			              </div>
			            </div>

			            <div class="control-group">
			              	  <label class="control-label" for="index">Position</label>
				              <div class="controls">
				              	<select name="index">
				              		<#assign index=0 />
				              		<#list page.sections as section>
					              		<option class="large" id="sectionIndex" value="${index}">${index+1}</option>
				              			<#assign index=index+1/>
				              		</#list>
				              		<option class="large" id="sectionIndex" value="${page.sections?size}" selected="selected">Dernière</option>
				              	</select>
				              	<p class="help-block">
				                  Position de la section
				                </p>
				              </div>
			            </div>

			            <div class="actions">
			              <button type="submit" class="btn btn-small btn-primary">Ajouter</button>&nbsp;
			            </div>
	           		</fieldset>
	        	</form>
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
	    	<div id="divConfigRowGadgets" class="dialog2" style="display:none;" >
	    	  <h1>${Context.getMessage('label.HtmlPage.row.widgets.config.title')}</h1>
              <input type="hidden" name="section" value="" class="span1" />
              <input type="hidden" name="row" value="" class="span1"/>
              <form id="config-row-gadgets-form" method="post" class="form form-horizontal" action="" >
                <fieldset>
                  <div id="divConfigRowGadgets-content" >
                    <img src="${skinPath}/images/loading.gif" />
                  </div>
                </fieldset>
                <div class="actions">
                    <button id="config-row-gadgets-form-btn" class="btn btn-primary" form-id="config-row-gadgets-form" title="${Context.getMessage('help.HtmlPage.row.widgets.config.save')}" >${Context.getMessage('command.HtmlPage.row.widgets.config.save')}
                    </button>
                    <a class="btn close-dialog" href="#" title="${Context.getMessage('help.HtmlPage.row.widgets.config.cancel')}" >${Context.getMessage('command.HtmlPage.row.widgets.config.cancel')}</a>
                </div>
              </form>
	    	</div>
            <div id="divConfigGadget" class="dialog2" style="display:none;" >
              <h1>${Context.getMessage('label.HtmlPage.widget.config.title')}</h1>
              <input type="hidden" name="section" value="" class="span1" />
              <input type="hidden" name="row" value="" class="span1"/>
              <input type="hidden" name="content" value="" class="span1"/>
              <input type="hidden" name="widget-type" value="" class="span1"/>
              <input type="hidden" name="widget-name" value="" class="span1"/>
              <form id="config-gadget-form" method="post" class="form-horizontal" action="" >
                <fieldset>
                  <div id="divConfigGadget-content" >
                    <img src="${skinPath}/images/loading.gif" />
                  </div>
                </fieldset>
                <div class="actions">
                    <button id="config-gadget-form-btn" class="btn btn-primary" form-id="config-gadget-form" title="${Context.getMessage('help.HtmlPage.widget.config.save')}" >${Context.getMessage('command.HtmlPage.widget.config.save')}
                    </button>
                    <a class="btn close-dialog" href="#" title="${Context.getMessage('help.HtmlPage.widget.config.cancel')}" >${Context.getMessage('command.HtmlPage.widget.config.cancel')}</a>
                </div>
              </form>
            </div>
            <div id="divColumnUrl" class="dialog2" style="display:none;" >
            	<h1>${Context.getMessage('label.HtmlPage.column.url.title')}</h1>
            	<input type="text" value="" class="input-xxlarge input-focused" />
                <div class="actions">
                    <a class="btn btn-primary close-dialog" href="#" title="${Context.getMessage('label.close')}" >${Context.getMessage('label.close')}</a>
                </div>
            </div>
		</#if>

	</div>

  </@tableOfContents>
  <#--/@cache-->

<#macro editblockHiddenInputs widget sectionIdx rowIdx contentIdx >
<input type="hidden" class="section-index-value" value="${sectionIdx}" />
<input type="hidden" class="row-index-value" value="${rowIdx}" />
<input type="hidden" class="content-index-value" value="${contentIdx}" />
<input type="hidden" class="widget-type" value="${widget.type.type()}" />
<input type="hidden" class="widget-name" value="${widget.name}" />
</#macro>

  </@block>

  <@block name="bottom-page-js" >
  	<script type="text/javascript" src="${contextPath}/wro/labs.pagehtml.bottom.js"></script>
  	<script type="text/javascript" src="${skinPath}/js/externalLinksHelper.js"></script>
    <@superBlock />
    <#if 0 < nbrOsGadgets >
    <script type="text/javascript" src="${skinPath}/js/jquery/jquery.fancybox-1.3.4.pack.js"></script>
    <script type="text/javascript" src="${contextPath}/opensocial/gadgets/js/rpc.js?c=1"></script>
    <#--<script type="text/javascript" language="javascript" src="${contextPath}/opensocial/gadgets/js/rpc:pubsub:lmselectvalue.js?c=1"></script>-->
    <script type="text/javascript" src="${skinPath}/js/register_rpc_show_fancybox.js"></script>
    <script type="text/javascript" src="${skinPath}/js/register_rpc_navigateto.js"></script>
    <script type="text/javascript" src="${contextPath}/js/?scripts=opensocial/cookies.js|opensocial/util.js|opensocial/gadgets.js|opensocial/cookiebaseduserprefstore.js|opensocial/jquery.opensocial.gadget.js"></script>
    </#if>
    <#include "views/HtmlPage/bottom-js.ftl" />
  </@block>
</@extends>

