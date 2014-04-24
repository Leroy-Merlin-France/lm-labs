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

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.ui.tree.ContentProvider;
import org.nuxeo.ecm.webengine.ui.tree.JSonTreeSerializer;

import com.leroymerlin.corp.fr.nuxeo.labs.site.tree.AbstractDocumentTree;

/**
 * Implementation of customized tree.
 */
public class AssetsDocumentTree extends AbstractDocumentTree {
    
    private String param;
    
    public AssetsDocumentTree(WebContext ctx, DocumentModel rootDoc) {
        this(ctx, rootDoc, null);
    }

    public AssetsDocumentTree(WebContext ctx, DocumentModel rootDoc, String param) {
        super(ctx, rootDoc);
        this.param = param;
    }

    @Override
    protected JSonTreeSerializer getSerializer(WebContext ctx) {
        return new AssetsTreeSerializer(param);
    }

    @Override
    protected ContentProvider getProvider(WebContext ctx) {
        return new AssetsContentProvider(ctx.getCoreSession());
    }

}
