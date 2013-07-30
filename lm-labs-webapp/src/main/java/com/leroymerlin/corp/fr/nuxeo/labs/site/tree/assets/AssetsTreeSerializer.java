/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package com.leroymerlin.corp.fr.nuxeo.labs.site.tree.assets;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.common.utils.URIUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.ui.tree.TreeItem;

import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.tree.AbstractJSONSerializer;

public class AssetsTreeSerializer extends AbstractJSONSerializer {
    private String param;

    public AssetsTreeSerializer() {
        this(null);
    }

    public AssetsTreeSerializer(String param) {
        this.param = param;
    }

    @Override
    public String getUrl(TreeItem item) {
        StringBuilder result = new StringBuilder("javascript:loadContentAsset(");
        result.append("'");
        String path = item.getPath().toString();
        if (!StringUtils.isEmpty(path) && path.length() > 1) {
            result.append(URIUtils.quoteURIPathComponent(path, false));
        }
        result.append("'");
        if (!StringUtils.isEmpty(this.param)) {
            result.append(", '");
            result.append(param);
            result.append("'");
        }
        result.append(");");
        return result.toString();
    }

    @Override
    protected String getBasePath(WebContext ctx) throws ClientException {
        StringBuilder sb = new StringBuilder(ctx.getModulePath());
        LabsSite site = (LabsSite) ctx.getProperty("site");
        sb.append("/" + URIUtils.quoteURIPathComponent(site.getURL(), true));
        sb.append("/@assets");

        return sb.toString();
    }

    @Override
    protected JSONObject item2JSON(TreeItem item, JSONArray children) {
        JSONObject json = super.item2JSON(item, children);
        DocumentModel doc = (DocumentModel) item.getObject();
        json.element("data-docid", doc.getId());
        return json;
    }
}
