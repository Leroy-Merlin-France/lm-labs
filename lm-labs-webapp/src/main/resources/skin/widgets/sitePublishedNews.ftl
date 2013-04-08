<#include "macros/documentModelList.ftl" />
<#assign sitePublishedNewsDivId = "sitePublishedNews" + "_" + section_index + "_r_" + row_index + "_c_" + content_index />
<#assign dateStr = Common.getNow().time?string("yyyy-MM-dd HH:mm:ss") />
<#assign params = "/default-domain/sites/" + Common.siteDoc(Document).site.document.name + "," + dateStr + "," + dateStr >
<@documentModelList documentModelList=Common.getPageProviderDocs(Session, "published_news", params, 5)
    divId=sitePublishedNewsDivId
    divTitle=Context.getMessage('label.rss.lastNews.title')
    tooltipDocProp="ln:accroche"
    divClass="sitePublishedNews supplychain-unstyled-bloc"
    showDate=true
    dateTimeDocProp="ln:startPublication" />