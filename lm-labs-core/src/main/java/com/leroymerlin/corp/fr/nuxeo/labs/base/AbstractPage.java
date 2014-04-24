package com.leroymerlin.corp.fr.nuxeo.labs.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import com.leroymerlin.corp.fr.nuxeo.labs.site.Page;
import com.leroymerlin.corp.fr.nuxeo.labs.site.exception.NullException;
import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.notification.MailNotification;
import com.leroymerlin.corp.fr.nuxeo.labs.site.notification.PageSubscription;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Docs;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.FacetNames;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Schemas;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.Tools;

public abstract class AbstractPage extends AbstractLabsBase implements Page {

	private static final String THIS_PAGE_IS_NOT_IN_A_LABS_SITE = "This page is not in a LabsSite";
    private static final String PROPERTY_PG_COMMENTABLE = Schemas.PAGE.prefix() + ":commentable";
    private static final String PROPERTY_PG_DISPLAYABLE_PARAMETERS = Schemas.PAGE.prefix() + ":displayableParameters";
    private static final String PROPERTY_PG_ELEMENTS_PER_PAGE = Schemas.PAGE.prefix() + ":elementsPerPage";
    private static final String PROPERTY_TAGS = Schemas.LABSTAGS.prefix() + ":tags";
    
    public static final String PG_COLLAPSETYPE = Schemas.PAGE.prefix() + ":collapseType";

    public AbstractPage(DocumentModel document) {
		super(document);
	}

    @Override
    public void setCommentable(boolean isCommentable) throws ClientException {
        doc.setPropertyValue(PROPERTY_PG_COMMENTABLE, isCommentable);
    }

    @Override
    public boolean isCommentable() throws ClientException {
        Serializable propertyValue = doc.getPropertyValue(PROPERTY_PG_COMMENTABLE);
        if (propertyValue instanceof Boolean) {
            return ((Boolean) propertyValue).booleanValue();
        }
        return false;
    }

    @Override
    public String getPath() throws ClientException {
        LabsSite site = getSite();
        if (site == null) {
            throw new IllegalArgumentException(THIS_PAGE_IS_NOT_IN_A_LABS_SITE);
        }
        return site.getURL()  + doc.getPathAsString()
                .replace(site.getTree().getPathAsString(), "");
    }

    protected LabsSite getSite() throws ClientException {
        DocumentModel parentDoc = doc;
        CoreSession session = getSession();
		while (parentDoc != null && !parentDoc.getType()
                .equals(Docs.SITE.type())) {
            parentDoc = session.getDocument(parentDoc.getParentRef());
        }
        return parentDoc != null ? Tools.getAdapter(LabsSite.class, parentDoc, session) : null;
    }

    @Override
    public String[] getAllowedSubtypes() throws ClientException {
        return getAllowedSubtypes(doc);
    }

    @Override
    public List<String> getNotDisplayableParameters() throws ClientException {
        @SuppressWarnings("unchecked")
        List<String> propertyValue = (List<String>) doc.getPropertyValue(PROPERTY_PG_DISPLAYABLE_PARAMETERS);
        if (propertyValue == null){
            propertyValue = new ArrayList<String>();
        }
        return propertyValue;
    }

    @Override
    public void setNotDisplayableParameters(List<String> properties) throws ClientException {
        doc.getProperty(PROPERTY_PG_DISPLAYABLE_PARAMETERS).setValue(properties);
    }

    @Override
    public boolean isDisplayable(String fieldName) throws ClientException {
        List<String> parameters = getNotDisplayableParameters();
        return !parameters.contains(fieldName);
    }

    @Override
    public Calendar getLastNotified() throws ClientException {
        try {
            return Tools.getAdapter(MailNotification.class, doc, getSession()).getLastNotified();
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public int getElementsPerPage() throws ClientException{
    	try {
			return Tools.getInt(doc.getProperty(PROPERTY_PG_ELEMENTS_PER_PAGE));
		} catch (NullException e) {
			return 0;
		}
    }

    @Override
    public void setElementsPerPage(int elementsPerPage) throws ClientException {
        doc.setPropertyValue(PROPERTY_PG_ELEMENTS_PER_PAGE, elementsPerPage);
    }
    
    @Override
    public void hideInNavigation() throws ClientException {
    	doc.addFacet(FacetNames.HIDDENINLABSNAVIGATION);
    }
    
    @Override
    public void showInNavigation() throws ClientException {
        doc.removeFacet(FacetNames.HIDDENINLABSNAVIGATION);
    }

    @Override
    public boolean isHiddenInNavigation() throws ClientException {
        return doc.getFacets().contains(FacetNames.HIDDENINLABSNAVIGATION);
    }

    @Override
    public List<String> getLabsTags() throws ClientException {
        @SuppressWarnings("unchecked")
        List<String> tags = (List<String>) doc.getPropertyValue(PROPERTY_TAGS);
        if (tags == null){
            tags = new ArrayList<String>();
        }
        return tags;
    }

    @Override
    public void setLabsTags(List<String> tags) throws ClientException {
        doc.getProperty(PROPERTY_TAGS).setValue(tags);
    }
    
    public Boolean isSubscribed(String userName) {
        try {
            PageSubscription subscriptionAdapter = Tools.getAdapter(PageSubscription.class, doc, getSession());
            return subscriptionAdapter.isSubscribed(userName);
        } catch (Exception e) {
            LOG.error(e, e);
        }
        return false;
    }

    @Override
    public boolean isLiked(String username) throws ClientException {
        return this.doc.getAdapter(LabsLike.class).isLiked(username);
    }

    @Override
    public LabsLike like(String username) throws ClientException {
        return this.doc.getAdapter(LabsLike.class).like(username);
    }

    @Override
    public List<String> getUsersLiked() throws ClientException {
        return this.doc.getAdapter(LabsLike.class).getUsersLiked();
    }

    @Override
    public long getLikesCount() throws ClientException {
        return this.doc.getAdapter(LabsLike.class).getLikesCount();
    }

    /* A GARDER
	@Override
	public void setCollapseType(final String collapseType) throws ClientException {
		doc.setPropertyValue(PG_COLLAPSETYPE, collapseType);
	}

	@Override
	public void setCollapseType(CollapseTypes collapseType) throws ClientException {
		doc.setPropertyValue(PG_COLLAPSETYPE, collapseType.type());
	}

	@Override
	public String getCollapseType() throws ClientException {
		return (String) doc.getPropertyValue(PG_COLLAPSETYPE);
	}
	*/

}
