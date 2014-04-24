package com.leroymerlin.corp.fr.nuxeo.labs.site.tree.site;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.URIUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.ui.tree.TreeItem;

import com.leroymerlin.corp.fr.nuxeo.labs.site.Page;
import com.leroymerlin.corp.fr.nuxeo.labs.site.SiteDocument;
import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.tree.AbstractJSONSerializer;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.Tools;

public class AdminSiteTreeSerializer extends AbstractJSONSerializer {

    protected WebContext ctx;

    private static final Log LOG = LogFactory
            .getLog(AdminSiteTreeSerializer.class);

    public AdminSiteTreeSerializer() {
        super();
    }

    public AdminSiteTreeSerializer(WebContext ctx) {
        this();
        this.ctx = ctx;
    }

    @Override
    protected String getBasePath(WebContext ctx) throws ClientException {
        StringBuilder sb = new StringBuilder(ctx.getModulePath());
        LabsSite site = (LabsSite) ctx.getProperty("site");
        sb.append("/" + URIUtils.quoteURIPathComponent(site.getURL(), true));
        return sb.toString();
    }

    @Override
    protected JSONObject item2JSON(TreeItem item, JSONArray children) {
        JSONObject json = new JSONObject();

        String text = (ctx != null && LabsSiteConstants.Docs.ASSETS.type()
                .equals(getText(item))) ? ctx
                .getMessage("label.admin.asset.rootNode") : getText(item);
        json.element("data", text);

        JSONObject attrs = new JSONObject();
        DocumentModel doc = (DocumentModel) item.getObject();
        attrs.put("id", doc.getId());
        attrs.put("rel", doc.getType());
        json.element("attr", (Object) attrs);
        JSONObject metadata = new JSONObject();
        CoreSession session = WebEngine.getActiveContext().getCoreSession();
        try {
            metadata.put("lifecyclestate", doc.getCurrentLifeCycleState());
        } catch (ClientException e) {
            LOG.error("Unable to get current life cycle of document "
                    + doc.getPathAsString() + ": " + e.getCause());
        }
        Page page = Tools.getAdapter(Page.class, doc, session);
        if (page != null) {
            try {
                metadata.put("isPageTemplate", page.isElementTemplate());
            } catch (ClientException e) {
                LOG.error("Unable to get template page of document "
                        + doc.getPathAsString() + ": " + e.getCause());
            }
        } else {
            metadata.put("isPageTemplate", false);
        }
        try {
            SiteDocument siteAdapter = Tools.getAdapter(SiteDocument.class,
                    doc, session);
            if (siteAdapter != null) {
                LabsSite site = siteAdapter.getSite();
                if (page == null) {
                    metadata.put("url", URIUtils.quoteURIPathComponent(
                            siteAdapter.getSite().getURL(), true));
                } else {
                    metadata.put("url", siteAdapter.getResourcePath());
                }
                if (site.getHomePageRef().equals(doc.getId())) {
                    metadata.put("ishomepage", "true");
                }
            }
        } catch (ClientException e) {
            LOG.error("Unable to get URL of page " + doc.getPathAsString()
                    + ": " + e.getCause());
        }
        json.element("metadata", (Object) metadata);
        if (item.isContainer()) {
            if (item.hasChildren()) {
                json.element("children", children);
            }
            json.element("state", item.isExpanded() ? "open" : "closed");
        }
        return json;
    }

    private String getText(TreeItem item) {
        return item.getLabel()
        // + " (" + ((DocumentModel) item.getObject()).getId() + ")"
        ;
    }

}
