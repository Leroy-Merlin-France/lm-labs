<@extends src="/views/labs-base.ftl">

	<@block name="title">${Context.module.name} - ${This.document.type} ${This.document.title}</@block>

	<@block name="scripts">
	  	<script type="text/javascript" src="${skinPath}/js/jquery/jquery-1.5.1.min.js"></script>
	    <script type="text/javascript" src="${skinPath}/js/jquery/jquery.treeview.js"></script>
	    <script type="text/javascript" src="${skinPath}/js/jquery/jquery.treeview.edit.js"></script>
	    <script type="text/javascript" src="${skinPath}/js/jquery/jquery.treeview.async.js"></script>
	    <script type="text/javascript" src="${skinPath}/js/jquery/jquery.treeview.sortable.js"></script>
	 </@block>

	 <@block name="css">
	  	<@superBlock/>
	    <link rel="stylesheet" type="text/css" media="all" href="${skinPath}/css/jquery/jquery.treeview.css"/>
	    <link rel="stylesheet" type="text/css" media="all" href="${skinPath}/css/sitemap.css"/>
	</@block>
	
	<@block name="content">
		<div id="content">
			<h1>PLAN DU SITE</h1>
		    <div>
		    	<a href="${This.path}/siteMap"><button id="treeButton">Arborescence</button></a> <a href="${This.path}/siteMapAsList"><button id="listButton">Liste</button></a>
		    </div>
		    <table id="sitemapList">
		    	<tr><th>Elément</th><th>Créé par</th><th>Dernière mise à jour par</th></tr>
			    <#list allDoc as doc>
			    	<tr>
			    		<td class="nameCol"><a href="${This.path}/${doc.title}">${doc.title}</a></td>
			    		<td class="createdCol">${This.getCreatorUsername(doc.ref)} (${This.getCreated(doc.ref)})</td>
			    		<td class="updatedCol">${This.getLastModifierUsername(doc.ref)} (${This.getLastModified(doc.ref)})</td>
			    	</tr>
			    </#list>
			</table>
		    <div>
		    	<a href="${This.path}/siteMap"><button id="treeButton">Arborescence</button></a> <a href="${This.path}/siteMapAsList"><button id="listButton">Liste</button></a>
		    </div>
		</div>
	</@block>
</@extends>	