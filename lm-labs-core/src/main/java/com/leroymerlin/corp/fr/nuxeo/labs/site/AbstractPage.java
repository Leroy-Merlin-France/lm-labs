package com.leroymerlin.corp.fr.nuxeo.labs.site;

import java.io.Serializable;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.schema.DocumentType;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.runtime.api.Framework;

import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Docs;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteUtils;

public abstract class AbstractPage implements Page {

    protected DocumentModel doc;

    @Override
    public DocumentModel getDocument() {
        return doc;
    }

    @Override
    public void setTitle(String title) throws PropertyException,
            ClientException, IllegalArgumentException {
        if (title == null) {
            throw new IllegalArgumentException("title cannot be null.");
        }
        doc.setPropertyValue("dc:title", title);
    }

    @Override
    public String getTitle() throws PropertyException, ClientException {
        return (String) doc.getPropertyValue("dc:title");
    }

    @Override
    public void setDescription(String description) throws PropertyException,
            ClientException {
        setDescription(doc, description);
    }

    protected static void setDescription(DocumentModel document,
            String description) throws PropertyException, ClientException {
        if (description == null) {
            return;
        }
        document.setPropertyValue("dc:description", description);
    }

    @Override
    public String getDescription() throws PropertyException, ClientException {
        return (String) doc.getPropertyValue("dc:description");
    }

    @Override
    public void setCommentable(boolean isCommentable) throws ClientException {
        doc.setPropertyValue("pg:commentable", isCommentable);
    }

    @Override
    public boolean isCommentable() throws ClientException {
        Serializable propertyValue = doc.getPropertyValue("pg:commentable");
        if (propertyValue instanceof Boolean) {
            return ((Boolean) propertyValue).booleanValue();
        }
        return false;
    }

    @Override
    public String getPath() throws ClientException {
        LabsSite site = getSite();
        if (site == null) {
            throw new IllegalArgumentException("This page is not in a LabsSite");
        }
        return site.getURL()  + doc.getPathAsString()
                .replace(site.getTree()
                        .getPathAsString(), "");
    }

    private LabsSite getSite() throws ClientException {
        CoreSession session = doc.getCoreSession();
        DocumentModel parentDoc = doc;
        while (parentDoc != null && !parentDoc.getType()
                .equals(Docs.SITE.type())) {
            parentDoc = session.getDocument(parentDoc.getParentRef());
        }
        return parentDoc != null ? parentDoc.getAdapter(LabsSite.class) : null;
    }

    protected String[] getAllowedSubtypes(DocumentModel doc) throws ClientException {
        try {
            SchemaManager sm = Framework.getService(SchemaManager.class);
            DocumentType documentType = sm.getDocumentType(doc.getType());
            return documentType.getChildrenTypes();
        } catch (Exception e) {
            throw ClientException.wrap(e);
        }
    }

    @Override
    public String[] getAllowedSubtypes() throws ClientException {
        return getAllowedSubtypes(doc);
    }

    @Override
    public void publish() throws ClientException{
        doc.followTransition(LabsSiteConstants.State.PUBLISH.getTransition());
    }
    
    @Override
    public void draft() throws ClientException{
        doc.followTransition(LabsSiteConstants.State.DRAFT.getTransition());
    }
    
    @Override
    public boolean isVisible() throws ClientException{
        return LabsSiteConstants.State.PUBLISH.getState().equals(doc.getCurrentLifeCycleState());
    }

    @Override
    public void delete() throws ClientException{
        doc.followTransition(LabsSiteConstants.State.DELETE.getTransition());
    }

    @Override
    public void undelete() throws ClientException{
        doc.followTransition(LabsSiteConstants.State.UNDELETE.getTransition());
    }
    
    @Override
    public boolean isDeleted() throws ClientException{
        return LabsSiteConstants.State.DELETE.getState().equals(doc.getCurrentLifeCycleState());
    }
    
    @Override
    public boolean isAuthorized(String pUser, boolean pIsAnonymous) throws ClientException{
        String user = null;
        if (pIsAnonymous) {
            user = SecurityConstants.EVERYONE;
        } else {
            user = pUser;
        }
        if (LabsSiteUtils.isOnlyRead(doc, user)){
            return doc.getAdapter(Page.class).isVisible();
        }
        return true;
    }

}
