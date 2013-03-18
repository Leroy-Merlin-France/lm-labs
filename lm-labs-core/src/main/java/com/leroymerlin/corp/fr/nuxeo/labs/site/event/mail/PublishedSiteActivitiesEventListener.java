package com.leroymerlin.corp.fr.nuxeo.labs.site.event.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
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

public class PublishedSiteActivitiesEventListener  extends SendMailActivitiesEventListener {

    private static final Log LOG = LogFactory.getLog(PublishedSiteActivitiesEventListener.class);

    
    void sendDocument(DocumentModel doc, DocumentEventContext ctx) throws Exception {
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
	        infoMap.put("siteCreator", authorFullName((String)doc.getPropertyValue("dc:creator")));
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

	@Override
	boolean accept(DocumentModel doc, String eventName)  {
		if (!(EventNames.PUBLISHED_SITE.equals(eventName)) || doc == null) {
            return false;
        }
		if (LabsSiteConstants.Docs.SITE.type().equals(doc.getType())) {
			return true;
		}
		return false;
	}
}
