<#-- assign canWrite = Session.hasPermission(Document.ref, 'Write') -->
<#assign canWrite = This.page?? && This.page.isContributor(Context.principal.name)>
<@extends src="/views/TemplatesBase/" + This.page.template.getTemplateName() + "/template.ftl">
  <#assign mySite=Common.siteDoc(Document).getSite() />
  <@block name="title">${ mySite.title}-${This.document.title}</@block>

  <@block name="css">
    <link rel="stylesheet" type="text/css" media="all" href="${contextPath}/wro/labs.forum.css"/><#-- MUST BE BEFORE superBlock -->
    <@superBlock/>
  </@block>

  <@block name="scripts">
    <script type="text/javascript" src="${contextPath}/wro/labs.forum.js"></script>
    <@superBlock/>
  </@block>

  <#assign allTopics = This.getLabsForum().getTopics(Session)/>
  <@block name="content">
  	<div id="divPageForum" class="container-fluid">
  		<#include "views/common/page_header.ftl">
  		<#if (Context.principal.isAnonymous() == false && Session.hasPermission(Document.ref, 'AddChildren'))>
		 	<div style="width: 100%; text-align: right; margin-bottom: 5px;">
		  		<a style="margin-right: 5px;" class="btn" href="${This.path}/@views/addtopic?props=open"><i class="icon-plus"></i>Ajouter un topic</a>
			</div>
		</#if>
		<#if (0 < allTopics?size)>
  			<div style="margin-bottom: 5px;">
			  <center><h4 id="titleTopicsPage">Liste des topics :</h4></center>
			  <table class="table table-striped forumTopics bs table-bordered labstable" >
			  	<thead>
	          		<tr>
	            		<th width="29%">Topic</th>
			            <th width="17%"><center>Auteur</center></th>
			            <th width="10%">Réponses</th>
			            <th width="36%">Dernier message</th>
			            <th width="8%"></th>
	          		</tr>
		        </thead>

				<#list allTopics as topic>
					<#if !topic.document.facets?seq_contains("LabsHidden") || canWrite || Context.principal.name == topic.document.author >
					  	<tr <#if topic.document.facets?seq_contains("LabsHidden")> class="hiddenThrough"</#if>>
					  		<td>
					  			<a href="${This.path}/${topic.document.name}">
						  			<strong>${topic.title}</strong><br/>
		        					<small>${Context.getMessage('label.topic.date')} ${topic.document['dublincore:created']?datetime?string("EEEE dd MMMM yyyy HH:mm")}</small>
								  	<div class="ellipsisText" ellipsisTextOptions="{ max_rows:4, alt_text_e:false, alt_text_t:true, whole_word:false }">${topic.description}</div>
								</a>
					  		</td>
					  		<td>
					  			<center>
						  			<span><img class="imgTopic thumbnail" width="50px;" src="http://intralm2.fr.corp.leroymerlin.com/contact/id/${topic.document.author}/picture"></span>
									<span>${This.getFullName(topic.document.author)}</span>
								</center>
					  		</td>
					  		<td>
					  			<center>${topic.nbComments}</center>
					  		</td>
					  		<td>
					  			<#if 0 <  topic.comments?size>
						  			${This.getFullName(topic.comments?last['comment:author'])} (${topic.comments?last['comment:creationDate']?datetime?string("EEEE dd MMMM yyyy HH:mm")}) :<br/>
									<div class="ellipsisText" ellipsisTextOptions="{ max_rows:4, alt_text_e:false, alt_text_t:true, whole_word:false }">${topic.comments?last['comment:text']}</div>
								</#if>
					  		</td>
					  		<#if canWrite || Context.principal.name == topic.document.author >
						  		<td>
								  	<div class="btn-group editblock">
								      	<a class="btn dropdown-toggle" data-toggle="dropdown"><i class="icon-cog"></i><span class="caret"></span></a>
										<ul class="dropdown-menu" style="left: auto;right: 0px;">
											<li>
												<a href="${This.path}/${topic.document.name}/@modify"><i class="icon-edit"></i>Editer</a>
												<#if topic.document.facets?seq_contains("LabsHidden")>
													<a href="${This.path}/${topic.document.name}/@visibility/show"><i class="icon-eye-open"></i>Afficher</a>
												<#else>
													<a href="${This.path}/${topic.document.name}/@visibility/hide"><i class="icon-eye-close"></i>Masquer</a>
												</#if>
											</li>
										</ul>
									</div>
						  		</td>
						  	<#else>
						  		<td></td>
						  	</#if>
						</tr>
					</#if>
				</#list>
			  </table>
			  <#if (Context.principal.isAnonymous() == false && Session.hasPermission(Document.ref, 'AddChildren'))>
				  <div style="width: 100%; text-align: right; margin-bottom: 5px;">
				  	<a style="margin-right: 5px;" class="btn" href="${This.path}/@views/addtopic?props=open"><i class="icon-plus"></i>Ajouter un topic</a>
				  </div>
			  </#if>
		  </div>
	  	</#if>
	</div>
  </@block>
</@extends>

<script type="text/javascript">
	$(document).ready(function() {
		$(".imgTopic").each(function(i, obj){
			$(obj).error(function(){
				$(obj).attr("src", "http://intralm2.fr.corp.leroymerlin.com/contact/skin/images/lm-man.png");
			});
		});
	});
</script>
