<@extends src="/views/TemplatesBase/templateCommon.ftl">

	<@block name="FKtopContent">
		<@superBlock/>
		
		<#--  masthead  -->
		<div id="masthead">
			<#--  Logo  -->
			<#--  Banner  -->
			</div>
		
		<#--  breadcrumbs  -->
		<#include "views/common/breadcrumbs.ftl" >
		
		<#--  action-message -->
		<#include "views/common/action_message.ftl" >
		
		<#--  content -->
		<div class="container-fluid">
			<div class="row-fluid">
				
				<#--  sidebar -->
				<div class="sidebar span2"> 
					<@block name="sidebar">
					   <@block name="lastActivities" />
					   <@block name="lastUploads" />
					   <#include "views/common/sidebar_area.ftl" />
				    </@block>
				</div>
			    <#--
		      		<#include "views/common/topnavigation_area.ftl" />
		      	-->
				
				<#--  central content -->
		        <div class="span10">
		        	<#--  Content  -->
				    <@block name="content" />
				    
				    <#--  Commentaires  -->
				    <@block name="pageCommentable">
						<#include "/views/LabsComments/displayCommentsPage.ftl" />
					</@block>
		        </div>
			
			    <div style="clear:both;"></div>
			    
			</div><#--  /row-fluid -->
		</div><#-- /container-fluid -->
	</@block>
</@extends>