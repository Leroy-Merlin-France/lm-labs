// insert the whole table, as stupid IE can't do a tbody.innerHtml
function tableStart(jsonObject, nxParams) {
  var html = "";
  if (nxParams.bootstrapEnabled) {
	  html += "<table class='table table-striped table-condensed' style='width: 100%;' >";
  } else {
	  html += "<table class='dataList'>";
  }
  if (!nxParams.hideHeaders) {
	  html += "  <thead>";
	  html += "    <tr";
	  if (nxParams.rowOddColor && nxParams.rowOddColor !== "") {
		  html += " style='background-color: " + nxParams.rowOddColor + "'";
	  }
	  html += ">";
	  for (idx in nxParams.displayColumns) {
		  html += mkColHeader(nxParams.displayColumns[idx]);
	  }
	  html += "    </tr>";
	  html += "  </thead>";
  }
  html += "  <tbody>";
  return html;
}

function mkColHeader(colDef) {
    var html = "";
    if (colDef.type == 'builtin') {
        if (colDef.field == "icon" || colDef.field == "download") {
            html = "<th/>";
        }
        else if (colDef.field == "titleWithLink"
        	|| colDef.field == "titleWithLabsLink"
        	|| colDef.field == "titleWithLabsDownloadLink"
        	|| colDef.field == "titleWithLabsPageLink") {
            html = "<th> " + colDef.label + " </th>";
        }
    }
    else {
        html = "<th>" + colDef.label + "</th>";
    }
    return html;
}

function tableEnd() {
    var html = "";
    html += "  </tbody>";
    html += "</table>";
    return html
}

function displayDocumentList(entries, nxParams) {
  var htmlContent = '';
  if (entries.length == 0) {
    nxParams.noEntryLabel = nxParams.noEntryLabel || 'Nothing to show.';
    htmlContent = '<p>' + nxParams.noEntryLabel + '</p>';
  } else {
    htmlContent = tableStart(entries, nxParams);
    for (var i = 0; i < entries.length; i++) {
        htmlContent += mkRow(entries[i], i, nxParams);
    }
    htmlContent += tableEnd();
  }

  displayPageNavigationControls(nxParams);
  _gel("nxDocumentListData").innerHTML = htmlContent + "<br/>";
  _gel("nxDocumentList").style.display = 'block';
  gadgets.window.adjustHeight();
}

function displayPageNavigationControls(nxParams) {
  if (nxParams.usePagination && maxPage > 1) {
    _gel('pageNavigationControls').style.display = 'block'
    _gel('nxDocumentListPage').innerHTML = (currentPage + 1) + "/" + maxPage;
    _gel('navFirstPage').onclick = function(e) {
      firstPage(nxParams)
    };
    _gel('navPrevPage').onclick = function(e) {
        prevPage(nxParams)
    };
    _gel('navNextPage').onclick = function(e) {
        nextPage(nxParams)
    };
    _gel('navLastPage').onclick = function(e) {
        lastPage(nxParams)
    };
  } else {
    _gel('pageNavigationControls').style.display = 'none';
  }
}

function parseISODate(datestr) {
    var m = /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2}(?:\.\d*)?)(?:([\+-])(\d{2})\:(\d{2}))?Z?$/.exec(datestr);
    if (m) {
        // TODO ms
        var t = Date.UTC(+m[1], +m[2] - 1, +m[3], +m[4], +m[5], +m[6]);
        if (m[7]) {
            var tz = m[8] * 3600 + m[9] * 60;
            if (m[7] == '-') {
                tz = -tz;
            }
            t -= tz * 1000;
        }
        return new Date(t);
    }
    return datestr;
}

function getDateForDisplay(datestr) {
    try {
        var d = parseISODate(datestr);
        return d.toLocaleDateString() + " "
                + d.toLocaleTimeString().substring(0, 5);
    } catch (e) {
        return datestr;
    }
}

function mkRow(dashBoardItem, i, nxParams) {
	var htmlRow = "<tr";
	if (nxParams.bootstrapEnabled) {

	} else {
		htmlRow += " class=\"";
		if (i % 2 == 0) {
			htmlRow += "dataRowEven";
		} else {
			htmlRow += "dataRowOdd";
		}
		htmlRow += "\"";
	}
    if (i % 2 == 0 && nxParams.rowEvenColor && nxParams.rowEvenColor !== "") {
        htmlRow += " style='background-color: " + nxParams.rowEvenColor + ";";
        if (nxParams.textColor && nxParams.textColor !== "") {
        	htmlRow += "color: " + textColor;
        }
        htmlRow += "'";
    } else if (nxParams.rowOddColor && nxParams.rowOddColor !== "") {
        htmlRow += " style='background-color: " + nxParams.rowOddColor + ";";
        if (nxParams.textColor && nxParams.textColor !== "") {
        	htmlRow += "color: " + textColor + ";";
        }
        htmlRow += "'";
    }
    htmlRow += ">";

    for (idx in nxParams.displayColumns) {
        htmlRow += mkCell(nxParams.displayColumns[idx], dashBoardItem, nxParams);
    }

    htmlRow += "</tr>";
    return htmlRow;
}

function mkCell(colDef, dashBoardItem, nxParams) {
    var html = "";
    var textColorStyle = "";
    if (nxParams.textColor && nxParams.textColor !== "") {
    	textColorStyle += " style='";
    	textColorStyle += "color: " + textColor + ";";
    	textColorStyle += "' ";
    }
    if (colDef.type == 'builtin') {
        if (colDef.field == "icon") {
        	if (nxParams.bootstrapEnabled) {
        		html += "<td>";
        	} else {
        		html += "<td class=\"iconColumn\">";
        	}
            html += "<img alt=\"File\" src=\"";
            html += NXGadgetContext.clientSideBaseUrl;
            html += dashBoardItem.properties["common:icon"];
            html += "\" />";
            html += "</td>";
        }
        else if (colDef.field == "download") {
        	if (nxParams.bootstrapEnabled) {
        		html += "<td>";
        	} else {
        		html += "<td class=\"iconColumn\">";
        	}
            html += "<a title=\"";
            if (colDef.tooltip) {
            	html += colDef.tooltip;
            }
            html += "\" href=\"";
            html += NXGadgetContext.clientSideBaseUrl;
            html += "site/labssites/id/";
            html += dashBoardItem.uid;
            html += "/@blob";
            html += "\"";
            if (textColorStyle !== "") {
            	html += textColorStyle;
            }
            html += " />";
            html += "<img alt=\"";
            html += colDef.field;
            html += "\" src=\"";
            html += NXGadgetContext.clientSideBaseUrl;
            html += "icons/download.png";
            html += "\"/>";
            html += "</a></td>";
        }
        else if (colDef.field == "titleWithLink") {
            var  view = "view_documents";
            if (colDef.view){
                view = colDef.view;
            }
            html += "<td><a target = \"_top\" title=\"";
            html += dashBoardItem.title;
            html += "\" href=\"";
            html += NXGadgetContext.clientSideBaseUrl;
            html += "nxpath/default";
            html += dashBoardItem.path;
            html += "@" + view;
            html += "\"";
            if (textColorStyle !== "") {
            	html += textColorStyle;
            }
            html += " />";
            html += dashBoardItem.title;
            html += "</a></td>";
        }
        else if (colDef.field == "titleWithLabsPageLink") {
        	var fullUrl = NXGadgetContext.clientSideBaseUrl + colDef.labsSiteModulePath + "/id/" + dashBoardItem.uid;
            html += "<td><a title=\"";
            if (colDef.tooltip) {
            	html += colDef.tooltip;
            } else {
                html += dashBoardItem.title;
            }
            html += "\" href=\"";
        	html += "#";
        	html += "\" ";
        	html += "onclick=\"containerNavigateTo('";
        	html += fullUrl;
        	html += "');\" ";
            if (textColorStyle !== "") {
            	html += textColorStyle;
            }
            html += " />";
            html += dashBoardItem.title;
            html += "</a></td>";
        }
        else if (colDef.field == "titleWithLabsLink") {
            html += "<td><a target = \"_blank\" title=\"";
            if (colDef.tooltip) {
            	html += colDef.tooltip;
            }
            html += "\" href=\"";
            html += NXGadgetContext.clientSideBaseUrl;
            html += "restAPI/preview/default/";
            html += dashBoardItem.uid;
            html += "/default/";
            html += "\"";
            if (textColorStyle !== "") {
            	html += textColorStyle;
            }
            html += " />";
            html += dashBoardItem.title;
            html += "</a></td>";
        }
        else if (colDef.field == "titleWithLabsDownloadLink") {
            html += "<td><a target = \"_blank\" title=\"";
            if (colDef.tooltip) {
            	html += colDef.tooltip;
            }
            html += "\" href=\"";
            html += NXGadgetContext.clientSideBaseUrl;
            html += "site/labssites/id/";
            html += dashBoardItem.uid;
            html += "/@blob";
            html += "\"";
            if (textColorStyle !== "") {
            	html += textColorStyle;
            }
            html += " />";
            html += dashBoardItem.title;
            html += "</a></td>";
        }
    } else {
        html += "<td>";
        if (colDef.type == 'date') {
            html += getDateForDisplay(dashBoardItem.properties[colDef.field]);
        } else if (colDef.type == 'filesize') {
        	html += filesize(dashBoardItem.properties[colDef.field]);
        } else {
            html += dashBoardItem.properties[colDef.field];
        }
        html += "</td>";
    }
    return html;
}

function nextPage(nxParams) {
    if (currentPage < maxPage - 1) {
        currentPage += 1;
    }
    refresh(nxParams);
}

function prevPage(nxParams) {
    if (currentPage > 0) {
        currentPage = currentPage - 1;
    }
    refresh(nxParams);
}

function firstPage(nxParams) {
    currentPage = 0;
    refresh(nxParams);
}

function lastPage(nxParams) {
    currentPage = maxPage - 1;
    if (currentPage < 0) {
        currentPage = 0;
    }
    refresh(nxParams);
}

function refresh(nxParams) {
    nxParams.refreshCB(nxParams);
    //doAutomationRequest(nxParams);
}

function containerNavigateTo(url) {
	gadgets.rpc.call("", "navigateTo", null, url);
}
