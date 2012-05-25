package com.leroymerlin.corp.fr.nuxeo.labs.site.test;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants;

public class PageDashBoardRepositoryInit extends AllDocTypeRepositoryInit {

    @Override
    public void populate(CoreSession session) throws ClientException {
        super.populate(session);

        String parentPath = ofm.getAdapter(LabsSite.class)
                .getTree(session)
                .getPathAsString();

        DocumentModel pageList = session.createDocumentModel(parentPath,
                "blocs", LabsSiteConstants.Docs.DASHBOARD.type());
        pageList = session.createDocument(pageList);

    }

}
