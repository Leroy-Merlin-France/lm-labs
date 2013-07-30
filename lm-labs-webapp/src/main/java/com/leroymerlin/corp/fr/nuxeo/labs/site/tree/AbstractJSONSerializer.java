package com.leroymerlin.corp.fr.nuxeo.labs.site.tree;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.nuxeo.common.utils.URIUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.ui.tree.JSonTreeSerializer;
import org.nuxeo.ecm.webengine.ui.tree.TreeItem;

public abstract class AbstractJSONSerializer extends JSonTreeSerializer {
    @Override
    public String getUrl(TreeItem item) {
        WebContext ctx = WebEngine.getActiveContext();

        try {
            StringBuilder sb = new StringBuilder(getBasePath(ctx));
            sb.append(URIUtils.quoteURIPathComponent(item.getPath().toString(),
                    false));
            return sb.toString();
        } catch (ClientException e) {
            return "#";
        }
    }

    protected abstract String getBasePath(WebContext ctx)
            throws ClientException;

    /**
     * You may override this method to change the output JSON.
     */
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
