	<h1>${Context.getMessage('label.security.labs.addPermissions.title')}</h1>
	<fieldset>
		<div class="control-group"><div class="controls">
			<div class="input-prepend input-append">
			<button type="button" class="btn"  rel="tooltip" data-original-title="${Context.getMessage('label.security.labs.grouporuser')}">
			    <i class="icon-question-sign" style="font-size: 16px;" ></i>
		    </button>
		 	<input type="text" id="userNamePermissions" name="userNamePermissions" value="" class="span4 focused" style="vertical-align: top;" />
			<button id="searchUsersBt" title="${Context.getMessage('command.security.searchUsers')}" class="btn btn-primary disabled" style="margin-left: -5px;" ><i class="icon-search"></i></button>
			</div>
		</div></div>
		<section>
			<h5>
				${Context.getMessage('label.security.labs.addPermissions.toSelect')} 
			</h5>
			<div id="divSelectedUsers">
				<#include "views/LabsPermissions/selectUsers.ftl" >
			</div>
		</section>
	</fieldset>
	<div  class="actions">
		<input type="hidden" id="permissionKey" value=""/>
		<input type="hidden" id="permissionText" value=""/>
		<button id="addPerm" onClick="javascript:addPerm();" title="${Context.getMessage('command.security.addPerm')}" class="btn btn-primary" style="margin-left:20px;" >${Context.getMessage('command.security.addPerm')}</button>
	</div>

<script type="text/javascript">
jQuery(document).ready(function(){
  jQuery("#userNamePermissions").keyup(function() {
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
      url: '${This.path}' + '/@labspermissions/suggestedUsers/' + jQuery("#userNamePermissions").val() ,
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

function addPerm(){
	var username=jQuery("#divSelectedUsers input:radio:checked").val();
	if (username == undefined) {
  		alert("Pas d'utilisateur ou de groupe selectionn\u00E9");
	} else {
		var permission = jQuery("#permissionKey").val();
		var operationConfirmed = false;
		var permissionText = jQuery("#permissionText").val();
	    if (username == "Everyone" || username == "members") {
	      operationConfirmed = confirm(["DANGER: Souhaitez-vous r\u00E9ellement ajouter la permission '", permissionText, "' \u00E0 'Tout le monde' \u00E0 ce site ?"].join(""));
	    } else {
	      operationConfirmed = confirm(["Souhaitez-vous r\u00E9ellement ajouter la permission '", permissionText, "' \u00E0 '", username, "' \u00E0 ce site ?"].join(""));
	    }
	    if (operationConfirmed) {
	    	jQuery('#waitingPopup').dialog2('open');
	    	jQuery.ajax({
				type: 'GET',
			    async: false,
			    url: "${This.path}/@labspermissions/haspermission?permission=" + permission +"&id=" + username,
			    success: function(data) {
			    	jQuery('#waitingPopup').dialog2('close');
			    	if (data === 'true') {
			          alert(["permission '", permissionText, "' d\u00E9j\u00E0 assign\u00E9e \u00E0 l'utilisateur '", username, "' !"].join(""));
			        }
			        else {
			          labsPermissionsHigherpermission(permission, username);
			        }
			    },
		        error: function(data) {
		        	jQuery('#waitingPopup').dialog2('close');
		        }
			});
		}
	}
}

function labsPermissionsHigherpermission(permission, username){
	jQuery('#waitingPopup').dialog2('open');
	jQuery.ajax({
        type: 'GET',
        async: false,
        url: "${This.path}/@labspermissions/higherpermission?permission=" + permission + "&id=" + username,
        success: function(data) {
            var hashigher = (data === 'true');
            var doAdd = false;
            if (hashigher) {
	            if (confirm(["L'utilisateur '", username, "' a d\u00E9j\u00E0 cette permission ou une permission sup\u00E9rieure. Voulez-vous forcer la permission choisie ?"].join("")))
	              {
	                doAdd = true;
	              }
            } else {
              doAdd = true;
            }
            jQuery('#waitingPopup').dialog2('close');
            if (doAdd)
            {
              labsPermissionsAdd(username, permission);
            }
        },
        error: function(data) {
        	jQuery('#waitingPopup').dialog2('close');
        }
    });
}

function labsPermissionsAdd(username, permission){
	jQuery('#waitingPopup').dialog2('open');
	jQuery.ajax({
        type: 'POST',
        async: false,
        url: "${This.path}/@labspermissions/add",
        data: { permission: permission, id: username, override: "true"  },
        success: function(data, msg){
          if (data.length > 0) {
            alert(data);
          }
          else {
            loadPermissions();
            closeAddPerm();
          }
          jQuery('#waitingPopup').dialog2('close');
        },
        error: function(xhr, status, ex){
          alert(ex);
          jQuery('#waitingPopup').dialog2('close');
        }
    });
}

$(function () {
	$("button[rel=tooltip]")
		.tooltip({trigger: 'hover', placement: 'right'});
	}
) 

</script>


	