/**
 *
 */
package com.leroymerlin.corp.fr.nuxeo.labs.site.labssite;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.operations.services.DocumentPageProviderOperation;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.Filter;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.LifeCycleConstants;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.runtime.api.Framework;

import com.leroymerlin.common.core.security.LMPermission;
import com.leroymerlin.corp.fr.nuxeo.labs.base.AbstractLabsBase;
import com.leroymerlin.corp.fr.nuxeo.labs.filter.DocUnderVisiblePageFilter;
import com.leroymerlin.corp.fr.nuxeo.labs.site.Page;
import com.leroymerlin.corp.fr.nuxeo.labs.site.blocs.ExternalURL;
import com.leroymerlin.corp.fr.nuxeo.labs.site.exception.HomePageException;
import com.leroymerlin.corp.fr.nuxeo.labs.site.html.HtmlContent;
import com.leroymerlin.corp.fr.nuxeo.labs.site.html.HtmlPage;
import com.leroymerlin.corp.fr.nuxeo.labs.site.html.HtmlRow;
import com.leroymerlin.corp.fr.nuxeo.labs.site.html.HtmlSection;
import com.leroymerlin.corp.fr.nuxeo.labs.site.news.LabsNews;
import com.leroymerlin.corp.fr.nuxeo.labs.site.notification.PageSubscription;
import com.leroymerlin.corp.fr.nuxeo.labs.site.theme.SiteTheme;
import com.leroymerlin.corp.fr.nuxeo.labs.site.theme.SiteThemeManager;
import com.leroymerlin.corp.fr.nuxeo.labs.site.theme.SiteThemeManagerImpl;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Docs;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.FacetNames;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Rights;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Schemas;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.State;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteUtils;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.Tools;
import com.leroymerlin.corp.fr.nuxeo.piwik.Piwik;

/**
 * @author fvandaele
 *
 */
public class LabsSiteAdapter extends AbstractLabsBase implements LabsSite {

    public LabsSiteAdapter(DocumentModel document) {
		super(document);
	}

	public static final int NB_LAST_UPDATED_DOCS = 20;

    public static final int NB_LAST_UPDATED_NEWS_DOCS = 20;

    public static final String PROPERTY_SITE_TEMPLATE = Schemas.LABSSITE.prefix()
            + ":siteTemplate";

    private static final String PROPERTY_HOME_PAGE_REF = Schemas.LABSSITE.prefix()
            + ":homePageRef";

    private static final String PROPERTY_CONTACTS = Schemas.LABSSITE.prefix()
            + ":contacts";

    public static final String PROPERTY_CATEGORY = Schemas.LABSSITE.prefix()
            + ":category";

    private static final String PROPERTY_URL = "webcontainer:url";
    
    public static final String PROPERTY_TOP_PAGE_NAVIGATION = Schemas.LABSSITE.prefix()
            + ":topPageNavigation";

    private static final Log LOG = LogFactory.getLog(LabsSiteAdapter.class);
    
    private HtmlPage sidebar = null;

    @Override
    public String getURL() throws ClientException {
        return (String) doc.getPropertyValue(PROPERTY_URL);
    }

    @Override
    public void setURL(String pURL) throws ClientException {
        doc.setPropertyValue(PROPERTY_URL, pURL);
    }

    @Override
    public void setDescription(String description) throws PropertyException,
            ClientException {
        if (description == null) {
            return;
        }
        doc.setPropertyValue("dc:description", description);
    }

    @Override
    public String getDescription() throws PropertyException, ClientException {
        return (String) doc.getPropertyValue("dc:description");
    }

    @Override
    public Blob getBanner() throws ClientException {
        CoreSession session = getSession();
		return Tools.getAdapter(LabsSite.class, doc, session).getThemeManager().getTheme(session).getBanner();
    }

    @Override
    public void setBanner(Blob pBlob) throws ClientException {
    	CoreSession session = getSession();
        SiteTheme theme = doc.getAdapter(LabsSite.class).getThemeManager().getTheme(getSession());
        if (pBlob == null) {
            theme.setBanner(null);
            session.saveDocument(theme.getDocument());
        } else {
            theme.setBanner(pBlob);
            session.saveDocument(theme.getDocument());
        }
    }

    @Override
    public List<Page> getAllPages() throws ClientException {
        CoreSession session = getSession();
		DocumentModelList docs = session.query(
                "SELECT * FROM Page, Space where ecm:currentLifeCycleState <> 'deleted' AND ecm:path STARTSWITH '"
                        + getTree().getPathAsString().replace("'", "\\'") + "'");

        List<Page> pages = new ArrayList<Page>();
        for (DocumentModel doc : docs) {
            Page page = Tools.getAdapter(Page.class, doc, session);
            if (page != null) {
                pages.add(page);
            }
        }
        return pages;
    }

    @Override
    public List<Page> getAllPagesTemplate() throws ClientException {
        CoreSession session = getSession();
		DocumentModelList docs = session.query(
                "SELECT * FROM Page, Space where ecm:currentLifeCycleState <> 'deleted' AND ecm:path STARTSWITH '"
                        + getTree().getPathAsString().replace("'", "\\'") + "' AND ecm:mixinType = '" + LabsSiteConstants.FacetNames.LABS_ELEMENT_TEMPLATE + "'");

        List<Page> pages = new ArrayList<Page>();
        for (DocumentModel doc : docs) {
            Page page = Tools.getAdapter(Page.class, doc, session);
            if (page != null) {
                pages.add(page);
            }
        }
        return pages;
    }

    // TODO unit tests
    @Override
    public Collection<DocumentModel> getPages(Docs docType, State lifecycleState)
            throws ClientException {
        String docTypeStr = Docs.PAGE.type() + ", " + Docs.DASHBOARD.type();
        if (docType != null) {
            docTypeStr = Docs.PAGE.type();
        }
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ").append(docTypeStr).append(" WHERE ").append(
                NXQL.ECM_PATH).append(" STARTSWITH ").append("'").append(
                doc.getPathAsString().replace("'", "\\'")).append("'");
        if (lifecycleState != null) {
            query.append(" AND ").append(NXQL.ECM_LIFECYCLESTATE).append(" = '").append(
                    lifecycleState.getState()).append("'");
        } else {
            query.append(NXQL.ECM_LIFECYCLESTATE).append(" <> 'deleted'");
        }
        return getSession().query(query.toString());
    }

    @Override
    public List<Page> getAllDeletedPages() throws ClientException {
        CoreSession session = getSession();
		DocumentModelList docs = session.query(
                "SELECT * FROM Page, Space where ecm:currentLifeCycleState = 'deleted' AND ecm:path STARTSWITH '"
                        + doc.getPathAsString().replace("'", "\\'") + "'");

        List<Page> pages = new ArrayList<Page>();
        for (DocumentModel doc : docs) {
            Page page = Tools.getAdapter(Page.class, doc, session);
            if (page != null) {
                pages.add(page);
            }
        }
        return pages;
    }

    @Override
    public DocumentModelList getAllDeletedDocs() throws ClientException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM Document").append(" WHERE ").append(
                NXQL.ECM_LIFECYCLESTATE).append(" = '").append(
                LifeCycleConstants.DELETED_STATE).append("'").append(" AND ").append(
                NXQL.ECM_PATH).append(" STARTSWITH '").append(
                doc.getPathAsString().replace("'", "\\'")).append("'");
        final CoreSession session = getSession();
        DocumentModelList docs = session.query(query.toString(),
                new Filter() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public boolean accept(DocumentModel arg0) {
                        if (Docs.pageDocs().contains(
                                Docs.fromString(arg0.getType()))) {
                            return true;
                        } else if (Docs.PAGECLASSEURFOLDER.type().equals(
                                arg0.getType())) {
                            try {
                                DocumentModel parent = session.getParentDocument(
                                        arg0.getRef());
                                if (Docs.PAGECLASSEUR.type().equals(
                                        parent.getType())) {
                                    return true;
                                }
                            } catch (ClientException e) {
                                return false;
                            }
                            return false;
                        } else {
                            try {
                                DocumentModel parent = arg0.getCoreSession().getParentDocument(
                                        arg0.getRef());
                                DocumentModel grandParent = session.getDocument(
                                        parent.getParentRef());
                                return Docs.PAGECLASSEURFOLDER.type().equals(
                                        parent.getType())
                                        && Docs.pageDocs().contains(
                                                Docs.fromString(grandParent.getType()));
                            } catch (ClientException e) {
                                LOG.error(e, e);
                            }
                            return false;
                        }
                    }
                });
        return docs;
    }

    @Override
    public DocumentModel getTree() throws ClientException {
        return getSession().getChild(doc.getRef(), Docs.TREE.docName());
    }

    public static DocumentModel getDefaultRoot(CoreSession coreSession) {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LabsSite)) {
            return false;
        }
        return getDocument().getId().equals(
                ((LabsSite) obj).getDocument().getId());
    }

    @Override
    public int hashCode() {
        return getDocument().getId().hashCode();
    }

    @Override
    public String toString() {
        String url = "";
        try {
            url = getURL();
        } catch (ClientException e) {
            url = "ClientException : Url not found";
        }
        return String.format("LabsSite at %s (url: %s)", doc.getPathAsString(),
                url);
    }

    @Override
    public SiteThemeManager getThemeManager() throws ClientException {
        return new SiteThemeManagerImpl(doc);
    }

    @Override
    public DocumentModel getIndexDocument() throws ClientException {
        DocumentModel welcome = null;
        String ref = getHomePageRef();
        CoreSession session = getSession();
        if (!StringUtils.isEmpty(ref)){
            try {
                welcome = session.getDocument(new IdRef(ref));
            } catch (ClientException e) {
                LOG.error("No document for id : '" + ref);
            }
        }
        if (welcome == null){
            LOG.warn("No setted home page for site '" + getURL());
            DocumentModelList list = LabsSiteUtils.getChildrenPageDocuments(getTree(), session);
            if (list.isEmpty()){
                LOG.error("Unable to get home page for site '" + getURL());
                throw new HomePageException("Unable to get home page for site '" + getURL());
            }
            welcome = list.get(0);
        }
        return welcome;
    }

    @Override
    public String[] getAllowedSubtypes() throws ClientException {
        return getAllowedSubtypes(getTree());
    }

    @Override
    public DocumentModel getAssetsDoc() throws ClientException {
        return getSession().getChild(doc.getRef(), Docs.ASSETS.docName());
    }

    @Override
    public void setHomePageRef(String homePageRef) throws ClientException {
        doc.setPropertyValue(PROPERTY_HOME_PAGE_REF, homePageRef);
    }

    @Override
    public String getHomePageRef() throws ClientException {
            return (String) doc.getPropertyValue(PROPERTY_HOME_PAGE_REF);
    }

    @Override
    // TODO UT LabsNews
    public DocumentModelList getLastUpdatedDocs() throws ClientException {
        StringBuilder query = new StringBuilder("SELECT * FROM Document");
        query.append(" WHERE ").append(NXQL.ECM_PATH).append(" STARTSWITH '").append(
                doc.getPathAsString().replace("'", "\\'")).append("/").append(
                LabsSiteConstants.Docs.TREE.docName()).append("'")
            .append(" AND ").append(NXQL.ECM_ISVERSION).append(" = 0")
            .append(" AND ").append(NXQL.ECM_LIFECYCLESTATE).append(" <> 'deleted'")
            .append(" AND ").append(NXQL.ECM_MIXINTYPE).append(
                " <> 'HiddenInNavigation'");
        CoreSession session = getSession();
        DocumentModelList docs;
        if (!isContributor(session.getPrincipal().getName())) {
            query.append(" AND ").append(NXQL.ECM_MIXINTYPE).append(
                    " <> '" + FacetNames.HIDDENINLABSNAVIGATION + "'")
                .append(" AND ").append(NXQL.ECM_PRIMARYTYPE).append(" <> '").append(Docs.LABSNEWS.type()).append("'");
            docs = session.query(query.toString(), new DocUnderVisiblePageFilter(session), NB_LAST_UPDATED_DOCS);
            docs.addAll(getLastPublishedNewsDocs(session));
            Collections.sort(docs, new Comparator<DocumentModel>() {
                @Override
                public int compare(DocumentModel arg0, DocumentModel arg1) {
                    try {
                        Calendar date1 = (Calendar) arg0.getPropertyValue("dc:modified");
                        Calendar date2 = (Calendar) arg1.getPropertyValue("dc:modified");
                        return date2.compareTo(date1);
                    } catch (PropertyException e) {
                        LOG.error(e.getMessage());
                    } catch (ClientException e) {
                        LOG.error(e.getMessage());
                    }
                    return 0;
                }});
        } else {
            query.append(" ORDER BY dc:modified DESC");
            docs = session.query(query.toString(), new DocUnderVisiblePageFilter(session), NB_LAST_UPDATED_DOCS);
        }
		return docs;
    }

    @Override
    public DocumentModelList getLastUpdatedNewsDocs() throws ClientException {
    	List<String> queryParams = new ArrayList<String>();
    	queryParams.add(doc.getPathAsString().replace("'", "\\'") + "/" + LabsSiteConstants.Docs.TREE.docName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(Calendar.getInstance().getTime());
        queryParams.add(dateStr);
        queryParams.add(dateStr);
        StringUtils.join(queryParams, ',');
        
    	StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(Docs.LABSNEWS.type());
        query.append(" WHERE ").append(NXQL.ECM_PATH).append(" STARTSWITH '").append(
                doc.getPathAsString().replace("'", "\\'")).append("/").append(
                LabsSiteConstants.Docs.TREE.docName()).append("'");
        query.append(" AND ecm:isCheckedInVersion = 0");
        query.append(" AND ecm:currentLifeCycleState <> 'deleted'");
        query.append(" AND ").append(NXQL.ECM_MIXINTYPE).append(
                " <> 'HiddenInNavigation'");
        query.append(" ORDER BY ln:startPublication DESC");

        CoreSession session = getSession();
		return session.query(query.toString(), new DocUnderVisiblePageFilter(session), NB_LAST_UPDATED_NEWS_DOCS);
    }

    @Override
    public DocumentModelList getLastPublishedNewsDocs(CoreSession session) throws ClientException {
    	List<String> queryParamsList = new ArrayList<String>();
    	queryParamsList.add(doc.getPathAsString().replace("'", "\\'") + "/" + LabsSiteConstants.Docs.TREE.docName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(Calendar.getInstance().getTime());
        queryParamsList.add(dateStr + " 23:59:59");
        queryParamsList.add(dateStr + " 00:00:00");
        String queryParams = StringUtils.join(queryParamsList, ',');
        OperationContext ctx = new OperationContext(session);
        final String providerName = "published_news";
		OperationChain chain = new OperationChain(providerName + "_" + session.getSessionId());
        chain.add(DocumentPageProviderOperation.ID).set("providerName", providerName).set("queryParams", queryParams).set("pageSize", new Integer(NB_LAST_UPDATED_NEWS_DOCS));
        try {
			return (DocumentModelList) Framework.getService(AutomationService.class).run(ctx, chain);
		} catch (Exception e) {
			LOG.error(e, e);
			return new DocumentModelListImpl();
		}
    }

    @Override
    public ArrayList<ExternalURL> getExternalURLs() throws ClientException {
    	CoreSession session = getSession();
        ArrayList<ExternalURL> listExtURL = new ArrayList<ExternalURL>();
        if (session.exists(
                new PathRef(doc.getPathAsString() + "/"
                        + Docs.EXTERNAL_URLS.docName()))) {
            DocumentModel folder = session.getDocument(
                    new PathRef(doc.getPathAsString() + "/"
                            + Docs.EXTERNAL_URLS.docName()));
            DocumentModelList listDoc = session.getChildren(
                    folder.getRef(),
                    LabsSiteConstants.Docs.EXTERNAL_URL.type(), null, null,
                    null);
            for (DocumentModel urlDoc : listDoc) {
                ExternalURL extURL = urlDoc.getAdapter(ExternalURL.class);
                listExtURL.add(extURL);
            }
        }
        return listExtURL;
    }

    @Override
    public ExternalURL createExternalURL(String title) throws ClientException {
    	CoreSession session = getSession();
        DocumentModel folder;
        if (!session.exists(
                new PathRef(doc.getPathAsString() + "/"
                        + Docs.EXTERNAL_URLS.docName()))) {
            folder = session.createDocumentModel(
                    doc.getPathAsString(), Docs.EXTERNAL_URLS.docName(),
                    Docs.EXTERNAL_URLS.type());
            folder.setPropertyValue("dc:title", Docs.EXTERNAL_URLS.docName());
            folder = session.createDocument(folder);
        } else {
            folder = session.getDocument(
                    new PathRef(doc.getPathAsString() + "/"
                            + Docs.EXTERNAL_URLS.docName()));
        }
        DocumentModel docExtURL = session.createDocumentModel(
                folder.getPathAsString(), title,
                LabsSiteConstants.Docs.EXTERNAL_URL.type());
        ExternalURL extURL = docExtURL.getAdapter(ExternalURL.class);
        extURL.setName(title);
        session.createDocument(docExtURL);
        return extURL;
    }

    // TODO vdu unit test
    @Override
    public boolean updateUrls(String oldUrl, String newUrl)
            throws ClientException {
    	CoreSession session = getSession();
        List<String> stringUrlFields = new ArrayList<String>(
                Arrays.asList("dc:description"));
        StringBuilder queryFormat = new StringBuilder();
        queryFormat.append("SELECT * FROM ").append(Docs.PAGE.type()).append(
                " WHERE ").append(NXQL.ECM_PATH).append(" STARTSWITH '").append(
                doc.getPathAsString().replace("'", "\\'")).append("'").append(" AND ").append("%s").append(
                " LIKE '%%").append(oldUrl).append("%%'");
        String queryFormatStr = queryFormat.toString();

        boolean anyUpdated = false;
        for (String fieldName : stringUrlFields) {
            boolean updated = false;
            String query = String.format(queryFormatStr, fieldName);
            DocumentModelList docs = session.query(query);
            for (DocumentModel document : docs) {
                updated = updateUrlInProperty(document, fieldName, oldUrl,
                        newUrl);
                if (updated) {
                    session.saveDocument(document);
                }
            }
            anyUpdated |= updated;
        }

        // TODO since 5.5.0 you can query complex properties
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ").append(
                StringUtils.join(new String[] { Docs.HTMLPAGE.type(),
                        Docs.LABSNEWS.type() }, ",")).append(" WHERE ").append(
                NXQL.ECM_PATH).append(" STARTSWITH '").append(
                doc.getPathAsString()).append("'");
        DocumentModelList docs = session.query(query.toString());
        for (DocumentModel document : docs) {
            boolean updated = false;
            if (Docs.HTMLPAGE.type().equals(document.getType())) {
                HtmlPage htmlPage = Tools.getAdapter(HtmlPage.class, document, session);
                for (HtmlSection section : htmlPage.getSections()) {
                    if(processRows(section.getRows(), oldUrl, newUrl)){
                        updated = true;
                    }
                }
            } else { // LabsNews
                LabsNews news = Tools.getAdapter(LabsNews.class, document, session);
                updated = processRows(news.getRows(), oldUrl, newUrl);
            }
            if (updated) {
                session.saveDocument(document);
            }
            anyUpdated |= updated;
        }
        return anyUpdated;
    }

    private boolean processRows(List<HtmlRow> rows, String oldUrl, String newUrl)
            throws ClientException {
        boolean updated = false;
        for (HtmlRow row : rows) {
            for (HtmlContent content : row.getContents()) {
                String html = content.getHtml();
                if (html.contains(oldUrl)) {
                    html = StringUtils.replace(html, oldUrl, newUrl);
                    content.setHtml(html);
                    updated = true;
                }
            }
        }
        return updated;
    }

    private boolean updateUrlInProperty(DocumentModel document,
            String propertyName, String oldUrl, String newUrl)
            throws ClientException {
        boolean updated = false;
        String stringField = (String) document.getPropertyValue(propertyName);
        if (stringField.contains(oldUrl)) {
            stringField = StringUtils.replace(stringField, oldUrl, newUrl);
            document.setPropertyValue(propertyName, stringField);
            updated = true;
        }
        return updated;
    }

    @Override
    public List<String> getAdministratorsSite() throws Exception {
        List<String> result = new ArrayList<String>();
        List<LMPermission> permissions = LabsSiteUtils.extractPermissions(this.doc);
        for (LMPermission perm : permissions) {
            if (Rights.EVERYTHING.getRight().equals(perm.getPermission())) {
                result.add(perm.getName());
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getContacts() throws Exception {
        return (List<String>) getDocument().getPropertyValue(PROPERTY_CONTACTS);
    }

    @Override
    public boolean addContact(String ldap) throws Exception {
    	CoreSession session = getSession();
        List<String> contacts = getContacts();
        if (!contacts.contains(ldap)) {
            DocumentModel doc = getDocument();
            contacts.add(ldap);
            doc.setPropertyValue(PROPERTY_CONTACTS, (Serializable) contacts);
            session.saveDocument(doc);
            session.save();
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteContact(String ldap) throws Exception {
    	CoreSession session = getSession();
        List<String> contacts = getContacts();
        if (contacts.contains(ldap)) {
            DocumentModel doc = getDocument();
            contacts.remove(ldap);
            doc.setPropertyValue(PROPERTY_CONTACTS, (Serializable) contacts);
            session.saveDocument(doc);
            session.save();
            return true;
        }
        return false;
    }

    @Override
    public String getPiwikId() throws ClientException {
        return doc.getAdapter(Piwik.class).getId();
    }

    @Override
    public void setPiwikId(String piwikId) throws ClientException {
        doc.getAdapter(Piwik.class).setId(piwikId);
    }

    @Override
    public boolean isPiwikEnabled() throws ClientException {
        return StringUtils.isNotBlank(getPiwikId());
    }

    @Override
    public void applyTemplateSite(final DocumentModel templateSite)
            throws ClientException, IllegalArgumentException {
        final String templateThemeName = (String) templateSite.getPropertyValue(Schemas.LABSSITE.prefix()
                + ":theme_name");
        UnrestrictedSessionRunner sessionRunner = new UnrestrictedSessionRunner(getSession()) {
            @Override
            public void run() throws ClientException {
                LabsSite labsSiteTemplate = Tools.getAdapter(LabsSite.class, templateSite, session);
                if (labsSiteTemplate.isElementTemplate()) {
                    Path indexLastSegments = labsSiteTemplate.getIndexDocument().getPath().removeFirstSegments(
                            templateSite.getPath().segmentCount());
                    session.removeDocuments(session.getChildrenRefs(
                            doc.getRef(), null).toArray(new DocumentRef[] {}));
                    for (DocumentModel document : session.getChildren(templateSite.getRef(), null)){
                    	// TODO it looks like Nuxeo does NOT copy schemas of dynamically added facets !!! LabsSiteUtils.copyHierarchyPage
                    	LabsSiteUtils.copyHierarchyPage(document.getRef(), doc.getRef(), document.getName(), document.getTitle(), session, true);
                    }
                    setThemeName(templateThemeName);
                    getTemplate().setTemplateName(labsSiteTemplate.getTemplate().getTemplateName());
                    Path indexPath = doc.getPath().append(indexLastSegments);
                    setHomePageRef(session.getDocument(
                            new PathRef(indexPath.toString())).getId());
                } else {
                    throw new IllegalArgumentException(
                            templateSite.getPathAsString()
                                    + " is not a site template.");
                }

            }
        };
        sessionRunner.runUnrestricted();
    }

    protected void copyFacetSchemas(DocumentModel site, final DocumentModel templateSite, CoreSession session) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ").append(Docs.PAGE.type())
            .append(" WHERE ").append(NXQL.ECM_PATH).append(" STARTSWITH '").append(templateSite.getPathAsString()).append("'")
            .append(" AND ").append(NXQL.ECM_MIXINTYPE).append(" = '").append(FacetNames.LABSTEMPLATE).append("'");
        try {
            DocumentModelList facetedDocs = session.query(query.toString());
            for(DocumentModel facetedDoc : facetedDocs) {
                String endPath = StringUtils.substringAfter(facetedDoc.getPathAsString(), templateSite.getPathAsString());
                DocumentModel document = session.getDocument(new PathRef(site.getPathAsString() + endPath));
                document.addFacet(FacetNames.LABSTEMPLATE);
                //TODO ajouter les nouvelles factes que l'on peut avoir dans une page
                document.setProperties(Schemas.LABSTEMPLATE.getName(), facetedDoc.getProperties(Schemas.LABSTEMPLATE.getName()));
                session.saveDocument(document);
            }
        } catch (ClientException e) {
            LOG.error(e, e);
        }
    }

    @Override
    public void setThemeName(String name) throws ClientException {
        doc.setPropertyValue(Schemas.LABSSITE.prefix() + ":theme_name", name);
    }

    @Override
    public String getThemeName() throws ClientException {
        return (String) doc.getPropertyValue(Schemas.LABSSITE.prefix()
                + ":theme_name");
    }

    @Override
    public List<Page> getSubscribedPages() throws ClientException {
        List<Page> subscribedPages = new ArrayList<Page>();
        for (Page page : getAllPages()) {
            try {
                PageSubscription adapter = page.getDocument().getAdapter(
                        PageSubscription.class);
                if (adapter != null
                        && adapter.isSubscribed(getSession().getPrincipal().getName())) {
                    subscribedPages.add(page);
                }
            } catch (Exception e) {
                LOG.error(e, e);
            }
        }
        return subscribedPages;
    }

    public DocumentModelList getPagesOfCreator(String username, CoreSession session) {
        OperationContext ctx = new OperationContext(session);
        String providerName = "all_pages_same_author";
		OperationChain chain = new OperationChain(providerName + "_" + session.getSessionId());
        chain.add(DocumentPageProviderOperation.ID).set("providerName", providerName).set("queryParams", doc.getPathAsString() + "," + username).set("pageSize", new Integer(5));
        try {
			return (DocumentModelList) Framework.getService(AutomationService.class).run(ctx, chain);
		} catch (Exception e) {
			LOG.error(e, e);
			return new DocumentModelListImpl();
		}
    }

	@Override
	public void setCategory(String category) throws ClientException {
		if (category == null) {
            return;
        }
        doc.setPropertyValue(PROPERTY_CATEGORY, category);
	}

	@Override
	public String getCategory() throws ClientException {
		return (String) doc.getPropertyValue(PROPERTY_CATEGORY);
	}

	@Override
	public HtmlPage getSidebar() throws ClientException {
		if (sidebar == null){
			PathRef pathRefSidebar = new PathRef(this.getDocument().getPathAsString() + "/"
					+ LabsSiteConstants.Docs.SIDEBAR.docName());
			CoreSession session = getSession();
			DocumentModel docSidebar = null;
			if (session.exists(pathRefSidebar)){
				docSidebar = session.getDocument(pathRefSidebar);
			}
			else{
				docSidebar = LabsSiteUtils.createDefaultSidebarPage(this.getDocument(), session);
				session.save();
			}
			sidebar = Tools.getAdapter(HtmlPage.class, docSidebar, session);
		}
		return sidebar;
	}

	@Override
	public boolean isTopPageNavigation() throws ClientException {
		Serializable propertyValue = doc.getPropertyValue(PROPERTY_TOP_PAGE_NAVIGATION);
        if (propertyValue instanceof Boolean) {
            return ((Boolean) propertyValue).booleanValue();
        }
        return false;
	}

	@Override
	public void setTopPageNavigation(boolean isTopPageNavigation)
			throws ClientException {
		doc.setPropertyValue(PROPERTY_TOP_PAGE_NAVIGATION, isTopPageNavigation);
	}
	
    
    public Boolean isSubscribed(String userName) {
        try {
            PageSubscription subscriptionAdapter = Tools.getAdapter(PageSubscription.class, doc, getSession());
            return subscriptionAdapter.isSubscribed(userName);
        } catch (Exception e) {
            LOG.error(e, e);
        }
        return false;
    }

	@Override
	public List<LabsNews> getAllLabsNewsTemplate() throws ClientException {
		CoreSession session = getSession();
		List<LabsNews> result = new ArrayList<LabsNews>();
		StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ").append(Docs.LABSNEWS.type()).append(" WHERE ");
        query.append(NXQL.ECM_PATH).append(" STARTSWITH '").append(doc.getPathAsString().replace("'", "\\'")).append("'");
        query.append(" AND ").append(NXQL.ECM_MIXINTYPE).append(" = '").append(FacetNames.LABS_ELEMENT_TEMPLATE).append("'");
		DocumentModelList docus = session.query(query.toString());
    	for (DocumentModel docu : docus){
    		result.add(Tools.getAdapter(LabsNews.class, docu, session));
    	}
		return result;
	}
}
