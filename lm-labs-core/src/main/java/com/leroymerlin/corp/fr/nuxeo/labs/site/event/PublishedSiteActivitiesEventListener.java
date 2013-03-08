package com.leroymerlin.corp.fr.nuxeo.labs.site.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.platform.ec.notification.NotificationConstants;
import org.nuxeo.ecm.platform.ec.notification.email.EmailHelper;
import org.nuxeo.runtime.api.Framework;

import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.DirectoriesUtils;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Directories;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.EventNames;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.Tools;

public class PublishedSiteActivitiesEventListener  implements EventListener {

    private static final Log LOG = LogFactory.getLog(PublishedSiteActivitiesEventListener.class);

    @Override
    public void handleEvent(Event evt) throws ClientException {
    	if (!(evt.getContext() instanceof DocumentEventContext)) {
            return;
        }
        String eventName = evt.getName();
        if (!EventNames.PUBLISHED_SITE.equals(eventName)) {
            return;
        }
        DocumentEventContext ctx = (DocumentEventContext) evt.getContext();
        DocumentModel doc = ctx.getSourceDocument();
        if (doc != null && LabsSiteConstants.Docs.SITE.type().equals(doc.getType())) {
			try {
				sendDocument(doc, ctx);
			} catch (Exception e) {
				LOG.error(e, e);
			}
        }
    }
    
    private void sendDocument(DocumentModel doc, DocumentEventContext ctx) throws Exception {
    	List<String> sendTo = DirectoriesUtils.getDirList(Directories.NOTIFICATION);
        if (sendTo.size() > 0){
			Map<String, Object> infoMap = new HashMap<String, Object>();
	        infoMap.put("document", doc);
	        infoMap.put(NotificationConstants.SUBJECT_TEMPLATE_KEY, "subjectPublishedSite");
	        infoMap.put("sender",  (NuxeoPrincipal)ctx.getPrincipal());
	        infoMap.put(NotificationConstants.TEMPLATE_KEY, "publishedSite");
	
	        LabsSite site = Tools.getAdapter(LabsSite.class, doc, ctx.getCoreSession());
	        infoMap.put("siteTitle", site.getTitle());
	        infoMap.put("siteUrl", site.getURL());
	        String baseUrl = Framework.getProperty("labs.baseUrl");
	        if(StringUtils.isEmpty(baseUrl)){
	        	baseUrl = Framework.getProperty("nuxeo.loopback.url")+ "/site/labssites";
	        }
	        infoMap.put("labsBaseUrl", baseUrl);
	        
	        EmailHelper emailHelper = new EmailHelper();
	        for (String to : sendTo) {
	            infoMap.put("mail.to", to);
	            try {
	                emailHelper.sendmail(infoMap);
	            } catch (Exception e) {
	            	LOG.debug("Failed to send notification email " + e);
	            }
	        }
        }
        else{
        	LOG.info("No configured notifications for published site. ");
        }
    }

}
