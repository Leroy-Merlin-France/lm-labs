<#include "macros/widgets.ftl" />
<#assign mySite=Common.siteDoc(Document).getSite() />
<#if mySite?? && Session.hasPermission(mySite.document.ref, "Everything")>
<@extends src="/views/labs-admin-base.ftl">

	<@block name="scripts">
	    <@superBlock/>
	    <script type="text/javascript" src="${skinPath}/js/siteTheme.js"></script>
	    <script type="text/javascript" src="${skinPath}/js/jquery/jquery.miniColors.min.js"></script>
	    <script type="text/javascript" src="${skinPath}/js/jquery/jquery.fancybox-1.3.4.pack.js"></script>
	    <script type="text/javascript" src="${contextPath}/opensocial/gadgets/js/rpc.js?c=1"></script>
	    <script type="text/javascript" src="${skinPath}/js/register_rpc_show_fancybox.js"></script>
	    <script type="text/javascript" src="${skinPath}/js/move.js"></script>
	    <script type="text/javascript" src="${skinPath}/js/externalLinksHelper.js"></script>
	    <#include "views/common/template_description_js.ftl">
	</@block>
	
	<@block name="css">
	    <@superBlock/>
        <link rel="stylesheet" type="text/css" media="all" href="${skinPath}/css/jquery/jquery.miniColors.css"/>
	</@block>

	<@block name="docactions"></@block>

  <@block name="tabs">
    <#include "macros/admin_menu.ftl" />
	<@adminMenu item="theme" basePath="${This.previous.path}"/>
  </@block>

  <@block name="content">
    <div class="container">
      
		<!--    TEMPLATE THEME -->
      <section>
        <div class="page-header">
          <h3>
          	${Context.getMessage('label.labssites.appearance.title')}
          	<small><a href="${Context.modulePath}/${mySite.URL}" target="_blank">${Context.getMessage('label.labssites.appearance.preview')}</a></small>
          </h3>
        </div>
        <div class="row">
          <div class="span2 columns">
			&nbsp;
          </div>
          <div class="span8 columns">
            <form class="form-horizontal well" action="${This.path}/appearance" method="post" id="form-appearance">
              <fieldset>
                <div class="control-group">
                  <label class="control-label" for="theme">${Context.getMessage('label.labssites.appearance.theme.label')}</label>
                  <div class="controls">
                    <a href="#" id="modifyThemeParameters" onClick="javascript:loadEditParameters('${This.path}/editParameters');"><br/>${Context.getMessage('label.labssites.appearance.theme.parameters')}</a> 
                    <select name="theme" id="theme" onChange="javascript:manageDisplayModifyParameters('${mySite.getThemeManager().getTheme(Context.coreSession).getName()}');linkThemeTemplate();">
                        <#assign themesMap = [] />
                        <#list This.getThemes() as theme>
                            <#assign trad = Context.getMessage('label.labssites.appearance.themes.' + theme) />
                            <#if trad?starts_with('!') >
                                <#assign themeName = theme />
                            <#else>
                                <#assign themeName = trad />
                            </#if>
                            <#assign themesMap = themesMap + [ {"name" : theme, "title" : themeName} ] />
                        </#list>
                        <#list themesMap?sort_by('title') as theme>
                            <option value="${theme.name}"  <#if mySite.getThemeManager().getTheme(Context.coreSession).getName() == theme.name >selected</#if>>${theme.title}</option>
                        </#list>
                    </select>
                    <p class="help-block">${Context.getMessage('label.labssites.appearance.theme.help.block')}</p>
                  </div>
                </div>
                <div class="control-group">
                  <label class="control-label" for="template">${Context.getMessage('label.labssites.appearance.template.label')}</label>
                  <div class="controls">
                    <select name="template" id="template" onchange="updateTemplateDescription(this, 'template-description');" >
                    <#include "views/common/getTemplatesMap.ftl">
                    <#assign templatesMap = getTemplatesMap() />
                    <#list templatesMap?sort_by('title') as template>
            			<option value="${template.name}"  <#if mySite.template.getTemplateName() == template.name >selected</#if>>${template.title}</option>
            		</#list>
	            	</select>
                    <p id="template-description" class="help-block"><small>${Context.getMessage('label.labssites.appearance.templates.' + mySite.template.templateName + '.description')}</small></p>
                    <p class="help-block">${Context.getMessage('label.labssites.appearance.template.help.block')}</p>
                  </div>
                </div>
                <div class="control-group">
			      <div class="controls">
			        <label class="checkbox" for="navigation">
			          <input class="checkbox" id="navigation" type="checkbox" name="labssite:topPageNavigation" <#if mySite.topPageNavigation>checked="true"</#if> />
			        &nbsp;${Context.getMessage('label.labssite.edit.topPageNavigation')}</label>
			      </div>
			    </div>
               </fieldset>
              <div class="form-actions" >
                <button class="btn btn-primary">${Context.getMessage('label.labssites.appearance.save')}</button>
              </div>
            </form>
          </div>
        </div>
      </section>

	  <div id="div-editTheme-container"></div>
	  
	  <!--    SIDEBAR -->
      <section id="sectionSidebar">
        <div class="page-header">
          <h3>
          	${Context.getMessage('label.labssites.appearance.sidebar.title')}
          </h3>
        </div>
        <div class="row">
          <div class="span2 columns">
			&nbsp;
          </div>
          <div class="span8 columns">
            <form class="form-horizontal well" action="${Context.modulePath}/${mySite.URL}/@manage-sidebar" method="post" id="form-sidebar">
              <fieldset>
                <input type="hidden" name="nbRows" id="nbRows" value="${mySite.sidebar.section(0).rows?size}"/>
				
				<#assign uncategorizedWidgets = Common.getUncategorizedWidgets("sidebar") />
				<#assign rows = mySite.sidebar.section(0).rows />
					<#list rows as row>
						<#assign content = row.content(0) />
					    <#assign widgets = [] />
					    <#if content.type == "widgetcontainer">
					        <#assign widgets = content.getGadgets(Session) />
					    </#if>
						<div class="control-group">
						    <label class="control-label" for="bloc${row_index}">Bloc ${row_index + 1}</label>
						    <div class="controls" >
						        <select name="bloc${row_index}" id="bloc${row_index}" onChange="javascript:onChangeWidget(this);" >
						            <#assign widgetTitle = Context.getMessage('label.HtmlPage.widget.html.editor') />
						            <#if widgetTitle?starts_with('!') >
						                <#assign widgetTitle = "Aucun" />
						            </#if>
						            <option value="html/editor"<#if content.type == "html"> selected</#if> >${widgetTitle}</option>
						            <@createSelectOptionWidget  />
						        </select>
						        <#if content.type != "html">
						        	<div class="btn-group">
								      	<a class="btn dropdown-toggle" data-toggle="dropdown"><i class="icon-cog"> </i>Actions <span class="caret"></span></a>
										<ul class="dropdown-menu" style="left: auto;right: 0px;">
											<li>
												<a rowIdx="${row_index}" rel="divConfigGadget" ><i class="icon-gift"></i>${Context.getMessage('command.HtmlPage.row.widgets.config.button')}</a>
												<#if rows?first != row>
													<a onClick="javascript:ajaxMove2('${Context.modulePath}/${mySite.URL}/@element-sidebar/r/${row_index}/moveUpElement', '${This.path}#sectionSidebar');" title="Monter" alt="Monter"><i class="icon-arrow-up"></i>Monter</a>
					    						</#if>
					    						<#if rows?last != row>
					    							<a onClick="javascript:ajaxMove2('${Context.modulePath}/${mySite.URL}/@element-sidebar/r/${row_index}/moveDownElement', '${This.path}#sectionSidebar');" title="Descendre" alt="Descendre"><i class="icon-arrow-down"></i>Descendre</a>
					    						</#if>
											</li>
										</ul>
								    </div>
						        
						        
						        
						        	<!--<a class="btn" rel="divConfigGadget" rowIdx="${row_index}" ><i class="icon-edit"></i>${Context.getMessage('command.HtmlPage.widget.config.button')}</a>-->
						        </#if>
						    </div>
						</div>
					</#list>
              </fieldset>
			<div class="form-actions" >
                <button id="saveSidebar" class="btn btn-primary disabled" disabled="disabled">${Context.getMessage('label.labssites.sidebar.save')}</button>
            </div>
            </form>
          </div>
        </div>
        
        <div id="divConfigGadget" class="dialog2" style="display:none;" >
          <h1>${Context.getMessage('label.HtmlPage.widget.config.title')}</h1>
          <form id="config-gadget-form" method="post" onSubmit="javascript:return false;" class="form-horizontal" action="" >
          <input type="hidden" name="rowIdx" value="" class="span1"/>
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
	    
	</div>
	<script type="text/javascript" >
		jQuery(document).ready(function() {
			jQuery('#form-sidebar').ajaxForm({
		        data: {ajax: 'false'},
		        beforeSubmit:  function () {
				    //jQuery('#divConfigRowGadgets div.actions a, input, button').attr('disabled', '');
				    return true;
				},
		        error: function () {
				    alert('error');
				},
		        success: function () {
				    document.location.reload(true);
				}
		    });
		    
		    jQuery("#divConfigGadget").dialog2({
		        autoOpen : false,
		        closeOnOverlayClick : false
		    });
		    
		    jQuery('a[rel=divConfigGadget]').click(function() {
		    	if(jQuery(this).attr("disabled") != "disabled"){
		    		jQuery("#divConfigGadget").dialog2('open');
		    		jQuery("#divConfigGadget-content").html('<img src="${skinPath}/images/loading.gif" />');
			        
			        var rowIndex = jQuery(this).attr("rowIdx");
			        jQuery('#divConfigGadget input[name=rowIdx]').val(rowIndex);
			        
			        jQuery.ajax({
			            type : "GET",
			            url : "${Context.modulePath}/${mySite.URL}/@configWidget-sidebar/c/0/w/@views/edit",
			            data : "rowIdx=" + rowIndex,
			            success : function(msg) {
			                jQuery("#divConfigGadget-content").html(msg);
			            },
			            error : function(msg) {
			                jQuery("#divConfigGadget-content").html(msg.responseText);
			                //alert('ERROR' + msg.responseText);
			            }
			        });
			    }
		    });
		    
		    jQuery('#config-gadget-form-btn').click(function() {
		        var inputs = jQuery("#divConfigGadget form > fieldset").find("input[type=text],input[type=file],select,checkbox,textarea");
		        if (inputs.length == 0) {
		            jQuery('#divConfigGadget').dialog2('close');
		            return false; <#-- prevents form submit -->
		        }
		        jQuery('#waitingPopup').dialog2('open');
			        
		        var url = "${Context.modulePath}/${mySite.URL}/@configWidget-sidebar/c/0/w/@put";
		        if (jQuery("#typeConfigGadget") && jQuery("#typeConfigGadget").val() == "html"){
		        	url = "${Context.modulePath}/${mySite.URL}/@configWidget-sidebar/c/0";
		        }
		        
		        jQuery.ajax({
					type: "POST",
					url: url,
					data: $("#config-gadget-form").serialize(),
					success: function(msg){
						alert("Configuration du widget enregistré.");
						jQuery('#divConfigGadget').dialog2('close');
						jQuery('#waitingPopup').dialog2('close');
					},
					error: function(msg){
						alert( msg.responseText );
						jQuery('#waitingPopup').dialog2('close');
					}
				});
		    });
		    
		    initSelectSidebar();
		});
		
		function refreshConfigGadgetDialog() {
			var waitingPic = '<img src="${skinPath}/images/loading.gif" />';
			jQuery("#divConfigGadget-content").html(waitingPic);
		    jQuery.ajax({
		        type : "GET",
		        url : '${Context.modulePath}/${mySite.URL}/@configWidget-sidebar/c/0/w/@views/edit',
			    data : "rowIdx=" + jQuery('#divConfigGadget input[name=rowIdx]').val(),
		        success : function(msg) {
		            jQuery("#divConfigGadget-content").html(msg);
		        },
		        error : function(msg) {
		            jQuery("#divConfigGadget-content").html(msg.responseText);
		            //alert('ERROR' + msg.responseText);
		        }
		    });
		}
		
		function isValidExternalContent(content) {
			return (content.indexOf('dontbeevil') !== -1);
		}
		
		function isValidExternalContentUrl(url) {
			var modulePath = '${Context.modulePath}';
			var elems = modulePath.split('/');
			return (url.length > 0 && url.indexOf(elems[elems.length-1]) !== -1);
		}
		
		function initSelectSidebar(){
			var finded = false;
			var element = null;
			jQuery('#form-sidebar option:selected').each(function() {
				element = this;	
	            if (jQuery(this).val() == "html/editor"){
	            	finded = true;
	            }
	        });
	        if (!finded && element != null){
	        	addWidget(element);
	        }
		}
		
		var isChanged = false;
		
		function onChangeWidget(element){
			if (!isChanged){
				isChanged = true;
				jQuery("#saveSidebar").removeClass("disabled");
				jQuery("#saveSidebar").removeAttr("disabled");
			}
			var bt = jQuery(element).parent().parent().find("a");
			jQuery(bt).attr("disabled", "disabled");
			jQuery(bt).addClass("disabled");
			addWidget(element);
		}
		
		function addWidget(element){
			if (jQuery(element).val() != 'html/editor'){
				var nbRows = parseInt(jQuery("#nbRows").val(),10);
				jQuery("#nbRows").val(nbRows + 1);
				var controlGroup = '<div class="control-group">\n';
				controlGroup = controlGroup + '<label class="control-label" for="bloc' + nbRows + '">Bloc ' + (nbRows + 1) + '</label>\n';
				controlGroup = controlGroup + '<div class="controls" >\n';
				controlGroup = controlGroup + '<select name="bloc' + nbRows + '" id="bloc' + nbRows + '" onChange="javascript:onChangeWidget(this);" >\n';
				<#assign widgetTitle = Context.getMessage('label.HtmlPage.widget.html.editor') />
	            <#if widgetTitle?starts_with('!') >
	                <#assign widgetTitle = "Aucun" />
	            </#if>
	            controlGroup = controlGroup + '<option value="html/editor" selected >${widgetTitle?js_string}</option>\n';
				<#list Common.getPageWidgetGroups(mySite.sidebar.document.type) as widgetGroup>
					<#assign widgetDocs = Common.getPageWidgets("sidebar", widgetGroup) />
					<#if (widgetDocs?size > 0) >
				     	controlGroup = controlGroup + '<optgroup label="${widgetGroup?js_string}">\n';
						<#list widgetDocs as widgetDoc >
				            <#assign gadgetType = widgetDoc['labshtmlpagewidgets:type'] gadgetName = widgetDoc['labshtmlpagewidgets:wname'] />
				            <@gadgetOptionJS type=gadgetType name=gadgetName selected=false />
						</#list>
					</#if>
				</#list>
				<#if 0 < uncategorizedWidgets?size >
				            controlGroup = controlGroup + '<optgroup label="Autres">\n';
					<#list uncategorizedWidgets as gadgetName >
				            <@gadgetOptionJS type="html" name=gadgetName selected=false />
					</#list>
				</#if>
				controlGroup = controlGroup + '</select>\n';
				controlGroup = controlGroup + '</div>\n';
				controlGroup = controlGroup + '</div>\n';
				jQuery(controlGroup).appendTo('#form-sidebar fieldset');
			}
		}
		
    </script>
	
  </@block>
</@extends>
<#else>
	<#include "error/error_404.ftl" >
</#if>

<#macro createSelectOptionWidget >
    <#list Common.getPageWidgetGroups(mySite.sidebar.document.type) as widgetGroup>
		<#assign widgetDocs = Common.getPageWidgets("sidebar", widgetGroup) />
		<#if (widgetDocs?size > 0) >
	     <optgroup label="${widgetGroup}">
			<#list widgetDocs as widgetDoc >
	            <#assign gadgetType = widgetDoc['labshtmlpagewidgets:type'] gadgetName = widgetDoc['labshtmlpagewidgets:wname'] />
	            <@gadgetOption type=gadgetType name=gadgetName selected=isOptionSelected(gadgetType, gadgetName, widgets) />
			</#list>
		</#if>
	</#list>
	<#if 0 < uncategorizedWidgets?size >
	            <optgroup label="Autres">
		<#list uncategorizedWidgets as gadgetName >
	            <@gadgetOption type="html" name=gadgetName selected=isOptionSelected("html", gadgetName, widgets) />
		</#list>
	</#if>
</#macro>

<#macro gadgetOptionJS type name selected=false >
    <#assign widgetTitle = Context.getMessage('label.HtmlPage.widget.' + type + '.' + name) />
    <#if widgetTitle?starts_with('!') >
        <#assign widgetTitle = name />
    </#if>
    controlGroup = controlGroup + '<option value="${type}/${name}"<#if selected > selected</#if> >Widget ${widgetTitle?js_string}</option>\n';
</#macro>

