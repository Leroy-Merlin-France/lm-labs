package com.leroymerlin.corp.fr.nuxeo.labs.site.notification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.DefaultComponent;

import com.leroymerlin.corp.fr.nuxeo.labs.site.Page;
import com.leroymerlin.corp.fr.nuxeo.labs.site.SiteDocument;
import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.news.PageNews;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Docs;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.EventNames;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.State;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.Tools;

public class PageNotificationServiceImpl extends DefaultComponent implements PageNotificationService {

    private static final Log LOG = LogFactory.getLog(PageNotificationServiceImpl.class);
    
    @Override
    public Page getRelatedPage(DocumentModel doc, CoreSession session) throws ClientException {
        Page page = Tools.getAdapter(Page.class, doc, session);
        if (page == null) {
            page = Tools.getAdapter(SiteDocument.class, doc, session).getParentPage();
        }
        return page;
    }
    
    @Override
    public boolean canBeMarked(DocumentModel doc, CoreSession session) throws ClientException {
        Page page = getRelatedPage(doc, session);
        if (page == null || (!Docs.LABSNEWS.type().equals(doc.getType()) && !State.PUBLISH.getState().equals(page.getDocument().getCurrentLifeCycleState()))) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean markForNotification(DocumentModel doc) throws ClientException {
    	LoginContext loginContext = null;
    	CoreSession session = null;
        try {
        	//A placer avant le récupération de la session
        	loginContext = Framework.login();
        	session = openSession(doc.getRepositoryName());
        	if (!canBeMarked(doc, session)) {
        		return false;
        	}
            DocumentModel notif;
            Page page = getRelatedPage(doc, session);
            PathRef ref = new PathRef(page.getDocument().getPathAsString() + "/" + Docs.NOTIFACTIVITIES.docName());
            if (!session.exists(ref)) {
                notif = session.createDocumentModel(page.getDocument().getPathAsString(), Docs.NOTIFACTIVITIES.docName(), Docs.NOTIFACTIVITIES.type());
                notif.setPropertyValue(PROPERTY_TONOTIFY, Boolean.TRUE);
                notif = session.createDocument(notif);
            } else {
                notif = session.getDocument(ref);
                notif.setPropertyValue(PROPERTY_TONOTIFY, Boolean.TRUE);
                notif = session.saveDocument(notif);
            }
            session.save();
        } catch (LoginException e) {
            LOG.error(e, e);
        } finally {
            closeCoreSession(loginContext, session);
        }
        return true;
    }

    @Override
    public void notifyPagesOfSite(DocumentModel site) throws ClientException {
        LoginContext loginContext = null;
        CoreSession session = null;
        try {
        	//A placer avant le récupération de la session
            loginContext = Framework.login();
            session = openSession(site.getRepositoryName());
            List<DocumentModel> docs = getMarkedDocsOfSite(site, session);
            Map<String, List<DocumentModel>> pageNewsMap = new HashMap<String, List<DocumentModel>>();
            for (DocumentModel doc : docs) {
                if (Docs.LABSNEWS.type().equals(doc.getType())) {
                    DocumentModel pageNews = session.getParentDocument(doc.getRef());
                    addNews(pageNewsMap, pageNews.getId(), doc);
                } else if (State.PUBLISH.getState().equals(doc.getCurrentLifeCycleState())) {
                    notifyPage(Tools.getAdapter(Page.class, doc, session), session);
                }
            }
            for (String id : pageNewsMap.keySet()) {
                DocumentModel pageNews = session.getDocument(new IdRef(id));
                try {
                    notifyPageNews(Tools.getAdapter(PageNews.class, pageNews, session), pageNewsMap.get(id), session);
                } catch (Exception e) {
                    LOG.error(e, e);
                }
            }
        } catch (LoginException e) {
            LOG.error(e, e);
        } finally {
            closeCoreSession(loginContext, session);
        }
    }

    private void addNews(Map<String, List<DocumentModel>> pageNewsMap, String pageNewsId, DocumentModel news) {
        if (!pageNewsMap.containsKey(pageNewsId)) {
            List<DocumentModel> list = new ArrayList<DocumentModel>();
            list.add(news);
            pageNewsMap.put(pageNewsId, list);
        } else {
            pageNewsMap.get(pageNewsId).add(news);
        }
    }

    @Override
    public boolean notifyPage(Page page, CoreSession session) throws ClientException {
        DocumentModel document = page.getDocument();
        if (isToBeNotified(document, session)) {
            unmarkForNotification(document, session);
            session.save();
            String eventName = EventNames.PAGE_MODIFIED;
            if (Docs.PAGENEWS.type().equals(page.getDocument().getType())) {
                eventName = EventNames.NEWS_PUBLISHED_UNDER_PAGENEWS;
            }
            return notifyPageEvent(page, eventName, session);
        }
        return false;
    }

    @Override
    public void notifyPageNews(PageNews pageNews, List<DocumentModel> newsList, CoreSession session) throws Exception {
        DocumentModel document = pageNews.getDocument();
        for (DocumentModel news : newsList) {
            if (isToBeNotified(news, session)) {
                unmarkForNotification(news, session);
                session.save();
            }
        }
        if (!State.PUBLISH.getState().equals(document.getCurrentLifeCycleState())) {
            return;
        }
        String eventName = EventNames.NEWS_PUBLISHED_UNDER_PAGENEWS;
        DocumentEventContext ctx = getContext(document, session);
        ctx.setProperty("PageNewsId", document.getId());
        List<String> titles = new ArrayList<String>();
        for (DocumentModel news : newsList) {
            titles.add(news.getTitle());
        }
        ctx.setProperty("newsTitlesList", (Serializable) titles);
        ctx.setProperty("pageNewsUrl", pageNews.getPath());
        fireEvent(pageNews.getDocument(), eventName, ctx);
    }
    
    private boolean isToBeNotified(DocumentModel page, CoreSession session) {
        PathRef ref = new PathRef(page.getPathAsString() + "/" + Docs.NOTIFACTIVITIES.docName());
        try {
            if (session.exists(ref)) {
                DocumentModel notif = session.getDocument(ref);
                return (Boolean) notif.getPropertyValue(PROPERTY_TONOTIFY);
            }
        } catch (PropertyException e) {
            LOG.error("Unable to retrieve value of " + PROPERTY_TONOTIFY + " of document " + page.getPathAsString());
        } catch (ClientException e) {
            LOG.error("Unable to access document " + page.getPathAsString());
        }
        return false;
    }

    @Override
    public boolean notifyPageEvent(Page page, String eventName, CoreSession session) throws ClientException {
        try {
            DocumentModel document = page.getDocument();
            fireEvent(document, eventName, getContext(document, session));
            return true;
        } catch (Exception e) {
            LOG.error(e, e);
        }
        return false;
    }

    private void unmarkForNotification(DocumentModel page, CoreSession session) throws ClientException {
    	//No need session for nullable test
        Page adapter = page.getAdapter(Page.class);
        if (adapter == null) {
            return;
        }
        PathRef ref = new PathRef(page.getPathAsString() + "/" + Docs.NOTIFACTIVITIES.docName());
        if (session.exists(ref)) {
//            session.removeDocument(ref);
            DocumentModel notif = session.getDocument(ref);
            notif.setPropertyValue(PROPERTY_NOTIFIED, Calendar.getInstance());
            notif.setPropertyValue(PROPERTY_TONOTIFY, Boolean.FALSE);
            notif = session.saveDocument(notif);
        }
    }

    public List<DocumentModel> getMarkedDocsOfSite(DocumentModel site, CoreSession session) throws ClientException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ").append(Docs.NOTIFACTIVITIES.type())
            .append(" WHERE ")
            .append(NXQL.ECM_PATH).append(" STARTSWITH '").append(site.getPathAsString().replace("'", "\\'")).append("'")
            .append(" AND ")
            .append(PROPERTY_TONOTIFY).append(" = 1")
        ;
        DocumentModelList notifs = session.query(query.toString());
        List<DocumentModel> docs = new ArrayList<DocumentModel>();
        for (DocumentModel notif : notifs) {
            docs.add(session.getDocument(notif.getParentRef()));
        }
        return docs;
    }
    
    public void fireEvent(DocumentModel doc, String eventName, DocumentEventContext ctx) throws Exception {
        LOG.debug("firing event " + eventName + " for " + doc.getPathAsString());
        EventProducer evtProducer = Framework.getService(EventProducer.class);
        evtProducer.fireEvent(ctx.newEvent(eventName));
    }

    private DocumentEventContext getContext(DocumentModel doc, CoreSession session) throws ClientException, PropertyException {
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

    protected CoreSession openSession(String repositoryName) throws ClientException {
        try {
            RepositoryManager m = Framework.getService(RepositoryManager.class);
            if (repositoryName == null) {
                return m.getDefaultRepository().open();
            } else {
                return m.getRepository(repositoryName).open();
            }
        } catch (Exception e) {
            throw new ClientException("Unable to get session", e);
        }
    }
    
    protected void closeCoreSession(LoginContext loginContext,
            CoreSession session) throws ClientException {
        if (loginContext != null) {
            try {
                loginContext.logout();
            } catch (LoginException e) {
                throw new ClientException(e);
            }
        }
        if (session != null) {
            CoreInstance.getInstance().close(session);
        }
    }
    @Override
    public Calendar getLastNotified(DocumentModel page, CoreSession session) throws ClientException, PropertyException {
        PathRef ref = new PathRef(page.getPathAsString() + "/" + Docs.NOTIFACTIVITIES.docName());
        try {
            if (session.exists(ref)) {
                DocumentModel notif = session.getDocument(ref);
                return (Calendar) notif.getPropertyValue(PROPERTY_NOTIFIED);
            }
        } catch (PropertyException e) {
            LOG.error("Unable to retrieve value of " + PROPERTY_NOTIFIED + " of document " + page.getPathAsString());
        } catch (ClientException e) {
            LOG.error("Unable to access document " + page.getPathAsString());
        }
        return null;
    }

}
