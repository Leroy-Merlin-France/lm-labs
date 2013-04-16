<#include "macros/documentModelList.ftl" />
<#assign pagesSameAuthorDivId = "publishedNewsSameAuthor" + "_" + section_index + "_r_" + row_index + "_c_" + content_index />
<#assign dateStr = Common.getNow().time?string("yyyy-MM-dd HH:mm:ss") />
<#assign params = "/default-domain/sites/" + Common.siteDoc(Document).site.document.name+","+Context.principal.name + "," + dateStr + "," + dateStr >
<@documentModelList documentModelList=Common.getPageProviderDocs(Session, "published_news_same_author", params, 5)
    divId=pagesSameAuthorDivId
    divTitle=Context.getMessage('label.HtmlPage.widget.html.myPublishedNews')
    tooltipDocProp="ln:accroche"
    divClass="publishedNewsSameAuthor supplychain-unstyled-bloc"
    showDate=true
    dateTimeDocProp="ln:startPublication" />