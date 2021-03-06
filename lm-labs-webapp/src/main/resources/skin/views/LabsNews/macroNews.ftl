<#-- genere le contenu entier de la news -->
<#macro generateContentHtmlNews news>
		<#if (news.getRows()?size == 0)>
			${news.accroche?replace("\n", "<br />")}
		<#else>
		  <#list news.getRows() as row>
		    <div class="row-fluid" id="row_s${news_index}_r${row_index}">
		      <#list row.contents as content>
		        <div class="span${content.colNumber} columns">
		          <#if content.html == "">
		            &nbsp;
		          <#else>
		            ${content.html}
		          </#if>
		
		        </div>
		      </#list>
		    </div>
		  </#list>
		</#if>
</#macro>

<#-- genere l'entete de la news -->
<#macro generateHeaderNews news path withHref withBy>
	<div class="headerNews">
		<#if withHref>
			<div onclick="javascript:window.location.href='${path}'" class="hrefTitleHeaderNews">
				<h2 class="titleHeaderNews">
					${news.title}
					<#if withBy>
						<small>${Context.getMessage('label.labsNews.display.by')} ${news.lastContributorFullName}</small>
					</#if>
				</h2>
			</div>
		<#else>
			<h2 class="titleHeaderNews">
				${news.title}
				<#if withBy>
					<small>${Context.getMessage('label.labsNews.display.by')} ${news.lastContributorFullName}</small>
				</#if>
			</h2>
		</#if>
	   	<h3 class="dateNews">
	   		<p class="labsNewsDate">
	   			<small>${Context.getMessage('label.labsNews.display.publish')} 
	   					<#if news.startPublication != null >${news.startPublication.time?string('dd MMMMM yyyy')}</#if>
	   			</small>
	   		</p>
	   	</h3>
	   	<#if news.startPublication != null >
			<div class="labsNewsStartPublicationDDMM">${news.startPublication.time?string("dd MMM")}</div>
		</#if>
	</div>
</#macro>

<#-- genere l'image du résumé de la news -->
<#macro generateSummaryPictureNews path>
	<img src="${path}/summaryPictureTruncated" class="summaryPictureNews"/>
</#macro>

<#-- genere le contenu tronqué dans le résumé de la news -->
<#macro generateHeaderNewsEllipsis news path withHref withBy>
	<@generateHeaderNews news=news path=path withHref=withHref withBy=false/>
	<#if (news.accroche?trim?length < 1)>
		<div class="ellipsisText" rel="news" ellipsisTextOptions="{ max_rows:2, alt_text_e:false, alt_text_t:false }">
			<@generateContentHtmlNews news=news />
		</div>
	<#else>
		${news.accroche?replace("\n", "<br />")}
	</#if>
</#macro>

<#-- genere le résumé de la news -->
<#macro generateSummaryNews news path withHref>
	<#if news.hasSummaryPicture()>
      		<#-- Image -->
      		<div class="span2">
      			<@generateSummaryPictureNews path=path/>
      		</div>
      		<#-- Central -->
      		<div class="span9 <@generateClassNewsVisibility news=news result="hiddenNews"/>">
      			<@generateHeaderNewsEllipsis news=news path=path withHref=withHref withBy=false/>
      		</div>
  	<#else>
  		<#-- Central -->
  		<div class="span11 <@generateClassNewsVisibility news=news result="hiddenNews"/>">
  			<@generateHeaderNewsEllipsis news=news path=path withHref=withHref withBy=false/>
  		</div>
  	</#if>
</#macro>

<#macro generateClassNewsVisibility news result>
	<#assign now = Common.getNow().timeInMillis/>
	<#if (news.startPublication.timeInMillis >= now || (news.endPublication != null && news.endPublication.timeInMillis <= now))>
		${result}
	</#if>
</#macro>