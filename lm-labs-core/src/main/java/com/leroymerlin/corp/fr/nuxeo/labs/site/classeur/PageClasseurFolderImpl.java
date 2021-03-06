package com.leroymerlin.corp.fr.nuxeo.labs.site.classeur;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.LifeCycleConstants;

import com.leroymerlin.corp.fr.nuxeo.labs.base.AbstractLabsBase;
import com.leroymerlin.corp.fr.nuxeo.labs.base.AbstractSubDocument;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.FacetNames;

public class PageClasseurFolderImpl extends AbstractSubDocument implements PageClasseurFolder {


    public PageClasseurFolderImpl(DocumentModel document) {
		super(document);
	}

	@Override
    public DocumentModel getDocument() {
        return doc;
    }

    @Override
    public String getTitle() throws ClientException {
        return doc.getTitle();
    }

    @Override
    public String getDescription() throws ClientException {
    	return (String) doc.getPropertyValue(AbstractLabsBase.DC_DESCRIPTION);
    }

    @Override
    public boolean setAsDeleted() throws ClientException {
        if (doc.getAllowedStateTransitions().contains(LifeCycleConstants.DELETE_TRANSITION)) {
            doc.followTransition(LifeCycleConstants.DELETE_TRANSITION);
            doc = getSession().saveDocument(doc);
            return true;
        }
        return false;
    }

	@Override
	public boolean hide(DocumentModel file) throws ClientException {
		CoreSession session = getSession();
		if (session.getParentDocument(file.getRef()).equals(doc)) {
	    	if (!file.getFacets().contains(FacetNames.LABSHIDDEN)) {
	    		file.addFacet(FacetNames.LABSHIDDEN);
	    		session.saveDocument(file);
	    		return true;
	    	}
		}
		return false;
	}

	@Override
	public boolean show(DocumentModel file) throws ClientException {
		CoreSession session = getSession();
		if (session.getParentDocument(file.getRef()).equals(doc)) {
	    	if (file.getFacets().contains(FacetNames.LABSHIDDEN)) {
	    		file.removeFacet(FacetNames.LABSHIDDEN);
	    		session.saveDocument(file);
	    		return true;
	    	}
		}
		return false;
	}
}
