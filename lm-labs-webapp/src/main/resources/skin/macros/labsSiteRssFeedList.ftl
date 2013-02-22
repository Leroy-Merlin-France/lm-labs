<#macro labsSiteRssFeedList feed="all" nbrItems="2" divId="rss-feed-list-" guid="" >
<#assign mySite=Common.siteDoc(Document).getSite() />
<div id="${divId}-container" style="position: relative; overflow: hidden;" >
<div id="${divId}" class="rss-feed-list bloc">
    <div class="header">${Context.getMessage('label.rss.' + feed + '.title')}</div>
        <div class="itemList">
        <img src="${skinPath}/images/loading.gif" />
        </div>
        <#-- Pas de pagination pour le moment
        <div class="browseLastMsg">
        </div>
        -->
</div>
</div>

<script type="text/javascript">
if(!${divId}_last_messages){
	var ${divId}_last_messages = new Array();
	$(".rss-feed-list.bloc > .itemList").ready(function() {
	  $.ajax({
	    type: "GET",
	    url: "${Context.modulePath}/${mySite.URL}/@labsrss/${feed}",
	    dataType: "xml",
	    success: ${divId}_parseXml
	  });
	});
}


function ${divId}_parseXml(xml) {
    $(xml).find("item").each(function(i) {
        var item = new Array(
            $(this).find("pubDate").text(),
            $(this).find("title").text(),
            $(this).find("description").text(),
            $(this).find("link").text(),
            <#if guid == "hasSummaryPicture">
            	$(this).find("guid").text()
            <#else>
            	''
            </#if>
        );
        ${divId}_last_messages.push(item);
    });
    ${divId}_loadContents(0, null);
    <#-- Pas de pagination pour le moment
    $("#${divId} .browseLastMsg").pagination(${divId}_last_messages.length, {
        items_per_page:${nbrItems},
        num_display_entries:0,
        num_edge_entries:0,
        prev_text:'<i class="icon-backward"></i>&nbsp;&nbsp;',
        next_text:'<i class="icon-forward"></i>',
        callback:${divId}_loadContents
    });
    -->
}

function ${divId}_loadContents(page_index, jq){
    // Get number of elements per pagination page from form
    var items_per_page = ${nbrItems};
    var max_elem = Math.min((page_index+1) * items_per_page, ${divId}_last_messages.length);
    var newcontent = '';

    // Iterate through a selection of the content and build an HTML string
    for(var i=page_index*items_per_page;i<max_elem;i++) {
    	var ahref = getEntryAhref(${divId}_last_messages[i][1], ${divId}_last_messages[i][2], dateAsString(${divId}_last_messages[i][0]), ${divId}_last_messages[i][3], '${divId}', i, 'rss-${feed}', ${divId}_last_messages[i][4]);
        newcontent += ahref;
        if (i < max_elem - 1){
            newcontent += '<hr style="margin:0;">';
        }
    }

    // Replace old content with new content
    $('#${divId} .itemList').html(newcontent);

    for(var i=page_index*items_per_page;i<max_elem;i++) {
        doEllipsisTextId("${divId}-rss-${feed}TitleEllipsisText" + i);
        doEllipsisTextId("${divId}-rss-${feed}DescEllipsisText" + i);
    }


    // Prevent click eventpropagation
    return false;
}
</script>

</#macro>