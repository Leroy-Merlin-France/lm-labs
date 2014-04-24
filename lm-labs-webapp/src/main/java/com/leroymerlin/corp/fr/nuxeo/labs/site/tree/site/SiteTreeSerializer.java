package com.leroymerlin.corp.fr.nuxeo.labs.site.tree.site;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.nuxeo.common.utils.URIUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.ui.tree.TreeItem;

import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.tree.AbstractJSONSerializer;

public class SiteTreeSerializer extends AbstractJSONSerializer {

    @Override
    protected String getBasePath(WebContext ctx) throws ClientException {
        StringBuilder sb = new StringBuilder(ctx.getModulePath());
        LabsSite site = (LabsSite) ctx.getProperty("site");
        sb.append("/" + URIUtils.quoteURIPathComponent(site.getURL(), true));
        return sb.toString();
    }

    protected JSONObject item2JSON(TreeItem item, JSONArray children) {
        JSONObject json = new JSONObject();
        json.element("text", item.getLabel())
                .element("id", item.getPath().toString())
                .element("href", getUrl(item));
        json.element("expanded", item.isExpanded());
        if (item.isContainer()) {
            if (item.hasChildren()) {
                json.element("children", children);
            } else {
                json.element("hasChildren", true);
            }
        } else {
            json.element("hasChildren", false);
        }
        return json;
    }

}
