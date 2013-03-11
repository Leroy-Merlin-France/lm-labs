package com.leroymerlin.corp.fr.nuxeo.labs.site.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.platform.comment.api.CommentEvents;
import org.nuxeo.runtime.api.Framework;

import com.leroymerlin.corp.fr.nuxeo.labs.site.Page;
import com.leroymerlin.corp.fr.nuxeo.labs.site.SiteDocument;
import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Docs;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.Tools;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.EventNames;

public class CommentsActivitiesEventListener implements EventListener {

    private static final Log LOG = LogFactory.getLog(CommentsActivitiesEventListener.class);

    @Override
    public void handleEvent(Event evt) throws ClientException {
    	if (!(evt.getContext() instanceof DocumentEventContext)) {
            return;
        }
        String eventName = evt.getName();
        if (!CommentEvents.COMMENT_ADDED.equals(eventName)) {
            return;
        }
        DocumentEventContext ctx = (DocumentEventContext) evt.getContext();
        DocumentModel doc = ctx.getSourceDocument();
        if (doc != null) {
        	List<String> allTypes = new ArrayList<String>();
            allTypes.addAll(PageActivitiesEventListener.markDocs);
            allTypes.add(Docs.LABSTOPIC.type());
//            allTypes.addAll(PageActivitiesEventListener.markParentDocs);
//            allTypes.addAll(PageActivitiesEventListener.markParentPageDocs);
            if (!allTypes.contains(doc.getType())) {
            	return;
            }
			try {
				EventProducer evtProducer = Framework.getService(EventProducer.class);
				evtProducer.fireEvent(getContext(doc, ctx.getCoreSession()).newEvent(EventNames.PAGE_ADDED_COMMENT));
			} catch (Exception e) {
				LOG.error(e, e);
			}
        }
    }
    


    protected DocumentEventContext getContext(DocumentModel doc, CoreSession session) throws ClientException, PropertyException {
        DocumentEventContext ctx = new DocumentEventContext(session, session.getPrincipal(), doc);
        ctx.setProperty("PageId", doc.getId());
        String baseUrl = Framework.getProperty("labs.baseUrl");
        if(StringUtils.isEmpty(baseUrl)){
        	baseUrl = Framework.getProperty("nuxeo.loopback.url")+ "/site/labssites";
        }
        ctx.setProperty("labsBaseUrl", baseUrl);
        LabsSite site = Tools.getAdapter(SiteDocument.class, doc, session).getSite();
		ctx.setProperty("siteUrl", (Serializable) site.getURL());
        ctx.setProperty("siteTitle", (Serializable) site.getTitle());
        ctx.setProperty("pageUrl", Tools.getAdapter(Page.class, doc, session).getPath());
        return ctx;
    }
}
