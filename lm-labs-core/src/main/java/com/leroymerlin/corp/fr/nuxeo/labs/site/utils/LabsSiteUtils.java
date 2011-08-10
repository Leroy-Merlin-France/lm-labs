package com.leroymerlin.corp.fr.nuxeo.labs.site.utils;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;

import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Docs;

public final class LabsSiteUtils {

    private LabsSiteUtils() {}
    
    @Deprecated
    public static final DocumentModelList getRootFolder(
            final DocumentModel site, final CoreSession session)
            throws ClientException {
        PathRef welcomeRef = new PathRef(site.getPathAsString() + "/"
                + Docs.TREE.docName() + "/"
                + Docs.WELCOME.docName());
        if (site == null || session == null || !session.exists(welcomeRef)) {
            return null;
        }

        return session.getChildren(welcomeRef);
    }
    
    public static DocumentModel getSitesRoot(final CoreSession session) throws ClientException {
        return session.getDocument(new PathRef(getSitesRootPath()));
        
    }

    public static String getSitesRootPath() {
        return "/" + Docs.DEFAULT_DOMAIN.docName() + "/" + Docs.SITESROOT.docName();
    }
    
    public static String getSiteTreePath(DocumentModel siteDoc) throws ClientException {
        if (!Docs.SITE.type().equals(siteDoc.getType())) {
            throw new IllegalArgumentException("document is not a " + Docs.SITE.type());
        }
        return getSitesRootPath() + "/" + siteDoc.getName() + "/" + Docs.TREE.docName();
    }
    
    public static DocumentModel getSiteTree(DocumentModel siteDoc) throws ClientException {
        return siteDoc.getCoreSession().getDocument(new PathRef(getSiteTreePath(siteDoc)));
    }
    
    public static Object getSiteMap(final DocumentModel site,
            final CoreSession session) throws ClientException {
        if (site == null) {
            return null;
        }
        
        return session.getChildren(site.getRef());
    }
    
     public static String getTreeviewChildren(final DocumentModel parent,
            final CoreSession session) throws ClientException {
        if (parent == null || session == null || parent.getRef() == null) {
            return null;
        }

        DocumentModelList children = session.getChildren(parent.getRef());
        StringBuilder result = null;
        if (children != null) {
            result = new StringBuilder();

            result.append("[");
            for (DocumentModel doc : children) {
                // TODO "expanded" : true when there are grand-children
                // result.append("[{\"text\": \"1. Review of existing structures\",\"expanded\": true},");
                // result.append("{\"text\": \"<a href='www.google.fr'>Google de ouf</a>\"},");
                // result.append("{\"text\": \"3. Summary\"},");
                // result.append("{\"text\": \"4. Questions and answers\"}]");

                result.append("{").append("\"text\": ").append("\"<a href='").append(
                        doc.getPathAsString()).append("'>").append(
                        doc.getName()).append("</a>\"}");
            }
            result.append("]");

        }

        return result.toString();
    }

    public static DocumentModel getParentSite(DocumentModel doc) throws ClientException {
        CoreSession session = doc.getCoreSession();
        if (Docs.SITE.type().equals(doc.getType())){
            return doc;
        }
        while (!doc.getParentRef().equals(session.getRootDocument().getRef())){
            doc = session.getDocument(doc.getParentRef());
            if (Docs.SITE.type().equals(doc.getType())) {
                return doc;
            }
        }
        throw new IllegalArgumentException("document '" + doc.getPathAsString() + "' is not lacated in a site.");
    }
}
