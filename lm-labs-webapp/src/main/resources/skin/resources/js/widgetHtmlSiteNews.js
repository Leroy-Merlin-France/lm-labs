function getEntryAhref(title, description, dateString, url, idPrefix, elemIndex, feedType, picUrl) {
    var newcontent = '';
//    if(description === '') {
//        description = '${Context.getMessage("label.last_message.no_desc")}';
//    }
    newcontent += '<a href="' + url + '">';
    if(picUrl !== ''){
    	newcontent += '<div class="row-fluid itemList-item"><div class="span3" style="margin-bottom: 5px;"><img class="thumbnail" style="padding: 2px;" src="' + picUrl + '" /></div>';
    	newcontent += '<div class="span9" style="float: right;">';
    }
    else{
    	newcontent += '<div class="row-fluid itemList-item">';
    }
    newcontent += '<div class="date">' + dateString + '</div>';
    newcontent += '<div class="title"><div id="' + idPrefix + '-' + feedType + 'TitleEllipsisText' + elemIndex +'" class="ellipsisText" ellipsisTextOptions="{ max_rows:1, alt_text_e:true, alt_text_t:true }">' + title + '</div></div>';
    newcontent += '<div class="ellipsisText desc" id="' + idPrefix + '-' + feedType + 'DescEllipsisText' + elemIndex +'"" ellipsisTextOptions="{ max_rows:2, alt_text_e:true, alt_text_t:true }">' + jQuery("<div>" + description + "</div>").text() + '</div>';
    newcontent += '</div>';
    newcontent += '</a>';
    if(picUrl !== ''){
    	newcontent += '</div>';
    }
    return newcontent;
}
