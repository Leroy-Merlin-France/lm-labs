<#assign mySite=Common.siteDoc(Document).getSite() />
<#if mySite?? && mySite.piwikEnabled >
<!-- Piwik -->
<script type="text/javascript">
var pkBaseURL = (("https:" == document.location.protocol) ? "https://analytics.fr.corp.leroymerlin.com/" : "http://piwik.cocfr2.fr.corp.leroymerlin.com/");
<#--
var pkBaseURL = (("https:" == document.location.protocol) ? "https://10.2.55.196/piwik/" : "http://10.2.55.196/piwik/");
-->
document.write(unescape("%3Cscript src='" + pkBaseURL + "piwik.js' type='text/javascript'%3E%3C/script%3E"));
</script><script type="text/javascript">
try {
var piwikTracker = Piwik.getTracker(pkBaseURL + "piwik.php", ${mySite.piwikId});
piwikTracker.trackPageView();
piwikTracker.enableLinkTracking();
} catch( err ) {}
</script><noscript><p><img src="http://analytics.fr.corp.leroymerlin.com/piwik.php?idsite=${mySite.piwikId}" style="border:0" alt="" /></p></noscript>
<!-- End Piwik Tracking Code -->
</#if>
