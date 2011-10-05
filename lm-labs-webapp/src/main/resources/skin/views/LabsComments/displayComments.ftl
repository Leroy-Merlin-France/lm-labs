<div id="divCommentable"  class="fixed-container dialog2" style="display: none;">
	<h1>${Context.getMessage('label.comments.title')}</h1>
	<div class="fixed-container" style="">
		<#if !Context.principal.anonymous>
			<form id="form-commentable" method="post" class="form" action="${This.path}/@comments">
				<!--         Comment      ------->
				<div class="clearfix" style="margin-bottom: 0px;margin-left: 0px;">
					<textarea name="text" id="text" class="labscomments text"></textarea>
				</div>
			</form>
		<#else>
				${Context.getMessage('label.comments.mandatory.connexion')}
		</#if>
		<div id="divListComments" class="fixed-container" style=""></div>
	</div>
	<#if !Context.principal.anonymous>
		<div  class="actions">
			<button class="btn primary" onClick="javascript:saveComment();" title="${Context.getMessage('label.comments.save')}">${Context.getMessage('label.comments.save')}</button>
			<button class="btn" onClick="javascript:closeComments();" title="${Context.getMessage('label.comments.cancel')}">${Context.getMessage('label.comments.cancel')}</button>
		</div>
	</#if>
</div>
<script type="text/javascript">
	var urlActionBase = null;
	var urlActionEnd = null;
	
	jQuery(document).ready(function(){
			initModalComments('divCommentable');
		});
	
	function initModalComments(name){
		jQuery("#" + name).dialog2({
			showCloseHandle : false,
			removeOnClose : true,
			autoOpen : false
		});
	}

	function openComments(url, endUrl){
		urlActionBase = url;
		urlActionEnd = endUrl;
		getComments();
		jQuery("#divCommentable").dialog2('open');
		jQuery("#form-commentable").clearForm();
	}

	function closeComments(){
		jQuery("#divCommentable").dialog2('close');
	}

	function saveComment(){
		jQuery.ajax({
			type : "POST",
			url : urlActionBase,
			data : jQuery("#form-commentable").serialize(),
			success : function(msg) {
				jQuery("#form-commentable").clearForm();
				getComments();
			},
			error : function(msg) {
				alert('ERROR' + msg.responseText);
			}
		});
	}

	function deleteComment(url, id){
		jQuery.ajax({
			type : "DELETE",
			url : url + '?property=' + id,
			data : '',
			success : function(msg) {
				getComments();
			},
			error : function(msg) {
				alert('ERROR' + msg.responseText);
			}
		});
	}
	
	function getComments() {
		jQuery.ajax({
			type : "GET",
			url : urlActionBase + urlActionEnd,
			data : '',
			success : function(msg) {
				jQuery("#divListComments")[0].innerHTML = msg;
			},
			error : function(msg) {
				alert('ERROR' + msg.responseText);
			}
		});
	}
</script>