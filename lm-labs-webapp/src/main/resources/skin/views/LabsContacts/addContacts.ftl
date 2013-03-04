	<h1>${Context.getMessage('label.admin.contact.addContact.title')}</h1>
	<div class="container-fluid">
		<section>
		<div class="input-prepend input-append">
			<button type="button" class="btn" rel="tooltip" data-original-title="${Context.getMessage('label.security.labs.grouporuser')}">
				<i class="icon-question-sign" style="font-size: 16px;" ></i>
			</button>
		 	<input type="text" id="usernameContacts" name="usernameContacts" value="" class="span4">
			<button type="button" id="searchUsersBt" title="${Context.getMessage('command.security.searchUsers')}" class="btn btn-primary disabled" ><i class="icon-search"></i></button>
		</div>
		</section>
		<section>
			<h5>${Context.getMessage('label.admin.contact.addContact.toSelect')}</h5>
			<div id="divSelectedUsers">
				<#include "views/LabsContacts/selectUsers.ftl" >
			</div>
		</section>
	</div>
	<div  class="actions">
		<button id="addPerm" onClick="javascript:addContact();" title="${Context.getMessage('command.contact.add')}" class="btn btn-primary" style="margin-left:20px;" >${Context.getMessage('command.contact.add')}</button>
		<button id="cancel" class="btn" onClick="javascript:closeAddContact();" title="${Context.getMessage('command.contact.cancel')}">${Context.getMessage('command.contact.cancel')}</button>
	</div>

<script type="text/javascript">
jQuery(document).ready(function(){
  jQuery("#usernameContacts").keyup(function() {
    if (jQuery(this).val().length >= 3) {
      jQuery("#searchUsersBt").removeClass('disabled');
    } else {
      jQuery("#searchUsersBt").addClass('disabled');
    }
  });
  
  jQuery("#searchUsersBt").unbind().click(function() {
    jQuery("#divSelectedUsers").html('<img src="${skinPath}/images/loading.gif" />');
    jQuery.ajax({
      type: 'GET',
      async: false,
      url: '${This.path}' + '/@labscontacts/suggestedUsers/' + jQuery("#usernameContacts").val() ,
      success: function(data) {
	      jQuery("#divSelectedUsers").html(data);
	  },
	  error: function(jqXHR, textStatus, errorThrown) {
      	jQuery("#divSelectedUsers").html('<div class="alert alert-block alert-error no-fade"><p><strong>' + textStatus + '</strong>:' + errorThrown + '</p></div>');
	  }
    });
    return false;
  });

});

function addContact(){
	var username=jQuery("#divSelectedUsers input:radio:checked").val();
	if (username == undefined) {
  		alert("Pas d'utilisateur selectionn\u00E9");
	} else {
		jQuery('#waitingPopup').dialog2('open');
    	jQuery.ajax({
			type: 'GET',
		    async: false,
		    url: "${This.path}/@labscontacts/add?id=" + username,
		    success: function(data) {
		    	window.location.reload();
		    },
			error: function(jqXHR, textStatus, errorThrown) {
				alert(textStatus + ':' + errorThrown);
			    jQuery('#waitingPopup').dialog2('close');
			  }
		});
	}
}

$(function () {
	$("button[rel=tooltip]")
		.tooltip({trigger: 'hover', placement: 'right'});
	}
) 

</script>


	