package com.leroymerlin.corp.fr.nuxeo.labs.site.utils;

import static org.nuxeo.ecm.webengine.WebEngine.SKIN_PATH_PREFIX_KEY;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.directory.SizeLimitExceededException;
import org.nuxeo.ecm.platform.comment.api.CommentableDocument;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.nuxeo.ecm.platform.query.nxql.CoreQueryDocumentPageProvider;
import org.nuxeo.ecm.webengine.model.Module;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.runtime.api.Framework;

import com.leroymerlin.common.core.security.GroupUserSuggest;
import com.leroymerlin.corp.fr.nuxeo.labs.site.SiteDocument;
import com.leroymerlin.corp.fr.nuxeo.labs.site.exception.NoDraftException;
import com.leroymerlin.corp.fr.nuxeo.labs.site.exception.NoPublishException;
import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.list.PageListLine;
import com.leroymerlin.corp.fr.nuxeo.labs.site.providers.LatestUploadsPageProvider;
import com.leroymerlin.corp.fr.nuxeo.labs.site.publisher.LabsPublisher;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Docs;
import com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects.webadapter.LabsPublishService;

public final class LabsSiteWebAppUtils {

    public static final String DEFAULT_BANNER = "/images/default_banner.png";

    private static final String LATEST_UPLOADS_PAGEPROVIDER = "latest_uploads";

    public static final String DIRECTORY_TEMPLATE = "/skin/views/TemplatesBase";

    public static final String DIRECTORY_THEME = "/skin/resources/less/theme";

    public static String NUXEO_WEBENGINE_BASE_PATH = "nuxeo-webengine-base-path";

    public static final String IMPOSSIBLE_TO_PUBLISH = "Impossible to publish!";
    
    private static final Log LOG = LogFactory.getLog(LabsSiteWebAppUtils.class);

    private LabsSiteWebAppUtils() {
    }

    public static PageProvider<DocumentModel> getLatestUploadsPageProvider(
            DocumentModel doc, long pageSize, CoreSession session) throws Exception {
        PageProviderService ppService = Framework.getService(PageProviderService.class);
        List<SortInfo> sortInfos = null;
        Map<String, Serializable> props = new HashMap<String, Serializable>();

        SiteDocument sd = Tools.getAdapter(SiteDocument.class, doc, session);

        props.put(LatestUploadsPageProvider.PARENT_DOCUMENT_PROPERTY,
                (Serializable) sd.getSite().getTree());
        props.put(CoreQueryDocumentPageProvider.CORE_SESSION_PROPERTY,
                        (Serializable) session);
        @SuppressWarnings("unchecked")
        PageProvider<DocumentModel> pp = (PageProvider<DocumentModel>) ppService.getPageProvider(
                LATEST_UPLOADS_PAGEPROVIDER, sortInfos, new Long(pageSize),
                null, props, "");
        return pp;
    }

    public static List<String> getFoldersUnderFolder(String path) {
        File directoryBase = new File(path);
        return Arrays.asList(directoryBase.list(new DirectoryFilter()));
    }

    /**
     * to complete with '/'
     * 
     * @param module of webengine
     * @param ctx the webContext
     * @return
     */
    public static String getSkinPathPrefix(Module module, WebContext ctx) {
        if (Framework.getProperty(SKIN_PATH_PREFIX_KEY) != null) {
            return module.getSkinPathPrefix();
        }
        String webenginePath = ctx.getRequest().getHeader(
                NUXEO_WEBENGINE_BASE_PATH);
        if (webenginePath == null) {
            return module.getSkinPathPrefix();
        } else {
            return ctx.getBasePath() + "/" + module.getName() + "/skin";
        }
    }

    public static String getPathDefaultBanner(Module module, WebContext ctx) {
        return getSkinPathPrefix(module, ctx) + DEFAULT_BANNER;
    }

    public static Map<String, Object> getSuggestedUsers(String pattern) {
        Map<String, Object> params = new HashMap<String, Object>();
        List<GroupUserSuggest> suggests;
        try {
            suggests = GroupsUsersSuggestHelper.getSuggestions(pattern);
            params.put("suggests", suggests);
        } catch (SizeLimitExceededException e) {
            params.put("errorMessage",
                    "Trop de resultats, veuillez affiner votre recherche.");
        } catch (ClientException e) {
            params.put("errorMessage", e.getMessage());
        }

        return params;
    }
    
    /**
     * publish a page or site
     * @param document to publish
     * @throws NoPublishException if no published with a problem
     */
    public static void publish(DocumentModel document, CoreSession session) throws NoPublishException{
        try {
            if (LabsSiteConstants.State.DRAFT.getState().equals(document.getCurrentLifeCycleState())){
                LabsPublisher publisherAdapter = Tools.getAdapter(LabsPublisher.class, document, session);
                publisherAdapter.publish();
                if (Docs.SITE.type().equals(document.getType())) {
                    LabsSite site = Tools.getAdapter(LabsSite.class, document, session);
                    LabsPublisher publisher = Tools.getAdapter(LabsPublisher.class, site.getIndexDocument(), session);
                    if (publisher.isDraft()) {
                        publisher.publish();
                    }
                }
            }
        } catch (ClientException e) {
            throw new NoPublishException(LabsPublishService.NOT_PUBLISHED);
        }
    }
    
    /**
     * draft a page or site
     * @param document to draft
     * @throws NoDraftException if no drafted with a problem
     */
    public static void draft(DocumentModel document) throws NoDraftException{
        try {
            if (LabsSiteConstants.State.PUBLISH.getState().equals(document.getCurrentLifeCycleState())){
                LabsPublisher publisherAdapter = Tools.getAdapter(LabsPublisher.class, document, null);
                publisherAdapter.draft();
            }
        } catch (ClientException e) {
            throw new NoDraftException(LabsPublishService.NOT_DRAFT);
        }
    }
    
    /**
     * update the setter of comments'size on th line of pageList
     * @param session
     */
    public static void updateAllCommentsOnLinesOfPageList(CoreSession session){
        try {
            DocumentModelList children = session.query("SELECT * FROM PageListLine");
            for (DocumentModel lineDoc: children){
                lineDoc.getAdapter(PageListLine.class).setNbComments(Tools.getAdapter(CommentableDocument.class, lineDoc, null).getComments().size());
                session.saveDocument(lineDoc);
            }
            session.save();
        } catch (ClientException e) {
            LOG.error("updateNbCommentsListLine : " , e);
        }
    }
}
