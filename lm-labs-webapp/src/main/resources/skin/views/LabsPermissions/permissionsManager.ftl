<div id="divDislayArray" class="container-fluid"><img src="${skinPath}/images/loading.gif" /></div>
<div id="divAddPermissions" style="display: none;">
	<#include "views/LabsPermissions/addPermissions.ftl" >
</div>

<script type="text/javascript">
jQuery(document).ready(function(){
	loadPermissions();
	initModalLabsPermissions();
  
});

function loadPermissions(){
	jQuery("#divDislayArray")[0].innerHTML = '<img src="${skinPath}/images/loading.gif" />';
	jQuery("#divDislayArray").load('${This.path}/@labspermissions');
}

var titleAddPermissions = '${Context.getMessage('label.security.labs.addPermissions.title')}';

function initModalLabsPermissions(){
	jQuery("#divAddPermissions").dialog2({
		width : '680px',
		height : '250px',
		overflowy : 'auto',
		overflowx : 'hidden',
		showCloseHandle : true,
		removeOnClose : false,
        buttons: {
            "${Context.getMessage('command.security.addPerm.cancel')}": function() { closeAddPerm(); }
        },
		autoOpen : false
	});
}

function displayAddPerm(permText, permKey) {
	jQuery("#divAddPermissions").dialog2('open');
	jQuery("#divAddPermissions").dialog2("options", {title: '${Context.getMessage('label.security.labs.addPermissions.title')} "' + permText + '"'});
	jQuery("#permissionKey").val(permKey);
	jQuery("#permissionText").val(permText);
	if (permKey === 'Read'){
		jQuery("#radioUserInputEveryone").val("Everyone");
	}
	else{
		jQuery("#radioUserInputEveryone").val("members");
	}
}

function closeAddPerm() {
	jQuery("#divAddPermissions").dialog2('close');
}



</script>