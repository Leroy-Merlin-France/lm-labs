package com.leroymerlin.corp.fr.nuxeo.labs.site.news;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

import com.leroymerlin.corp.fr.nuxeo.labs.base.AbstractPage;
import com.leroymerlin.corp.fr.nuxeo.labs.site.exception.LabsBlobHolderException;
import com.leroymerlin.corp.fr.nuxeo.labs.site.html.ChangeListener;
import com.leroymerlin.corp.fr.nuxeo.labs.site.html.HtmlRow;
import com.leroymerlin.corp.fr.nuxeo.labs.site.html.HtmlSection;
import com.leroymerlin.corp.fr.nuxeo.labs.site.html.HtmlSectionImpl;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.Tools;

public class LabsNewsAdapter extends AbstractPage implements LabsNews,
        ChangeListener {

    static final String CREATOR = "dublincore:creator";
    static final String LAST_CONTRIBUTOR = "dublincore:lastContributor";

    static final String START_PUBLICATION = "ln:startPublication";
    static final String END_PUBLICATION = "ln:endPublication";
    static public final String ACCROCHE = "ln:accroche";
    static final String CONTENT = "ln:content";
    static final String NEWS_TEMPLATE = "ln:template";
    static final String IS_TOP = "ln:isTop";

    private String lastContributorFullName = null;
    private HtmlSection section;
    
    private LabsNewsBlobHolder blobHolder = null;

    public LabsNewsAdapter(DocumentModel doc) {
        super(doc);
        this.blobHolder = (LabsNewsBlobHolder)this.doc.getAdapter(BlobHolder.class);
    }

    @Override
    public Calendar getStartPublication() throws ClientException {
        return (Calendar) doc.getPropertyValue(START_PUBLICATION);
    }

    @Override
    public String getCreator() throws ClientException {
        return (String) doc.getPropertyValue(CREATOR);
    }

    @Override
    public String getAccroche() throws ClientException {
        return (String) doc.getPropertyValue(ACCROCHE);
    }

    @Override
    public String getContent() throws ClientException {
        return (String) doc.getPropertyValue(CONTENT);
    }

    @Override
    public void setAccroche(String pAccroche) throws ClientException {
        doc.setPropertyValue(ACCROCHE, pAccroche);
    }

    @Override
    public void setContent(String pContent) throws ClientException {
        doc.setPropertyValue(CONTENT, pContent);
    }

    @Override
    public String getNewsTemplate() throws ClientException {
        return (String) doc.getPropertyValue(NEWS_TEMPLATE);
    }

    @Override
    public void setNewsTemplate(String pNewsTemplate) throws ClientException {
        doc.setPropertyValue(NEWS_TEMPLATE, pNewsTemplate);
    }

    @Override
    public void setStartPublication(Calendar pStartPublication)
            throws ClientException {
        if (pStartPublication != null){
            pStartPublication.set(Calendar.SECOND, 0);
            pStartPublication.set(Calendar.MILLISECOND, 0);
        }
        doc.setPropertyValue(START_PUBLICATION, pStartPublication);

    }

    @Override
    public Calendar getEndPublication() throws ClientException {
        return (Calendar) doc.getPropertyValue(END_PUBLICATION);
    }

    @Override
    public void setEndPublication(Calendar pEndPublication)
            throws ClientException {
        if (pEndPublication != null){
//            pEndPublication.add(Calendar.DATE, 1);
//            pEndPublication.set(Calendar.HOUR_OF_DAY, 0);
//            pEndPublication.set(Calendar.MINUTE, 0);
            pEndPublication.set(Calendar.SECOND, 0);
            pEndPublication.set(Calendar.MILLISECOND, 0);
//            pEndPublication.add(Calendar.MILLISECOND, -1);
        }
        doc.setPropertyValue(END_PUBLICATION, pEndPublication);

    }

    @Override
    public String getLastContributor() throws ClientException {
        String lastContributor = (String) doc.getPropertyValue(LAST_CONTRIBUTOR);
        if (StringUtils.isEmpty(lastContributor)) {
            lastContributor = getCreator();
        }
        return lastContributor;
    }

    @Override
    public DocumentModel getDocumentModel() {
        return doc;
    }

    public String getLastContributorFullName() throws Exception {
        if (StringUtils.isEmpty(lastContributorFullName)) {
            UserManager userManager = Framework.getService(UserManager.class);
            NuxeoPrincipal user = userManager.getPrincipal(getLastContributor());
            if (user != null){
            	lastContributorFullName = user.getFirstName() + " "
                    + user.getLastName();
            }
            else{
            	lastContributorFullName = getLastContributor();
            }
        }
        return lastContributorFullName;
    }

    @Override
    public HtmlRow addRow() throws ClientException {
        return getSection().addRow();
    }

    @Override
    public List<HtmlRow> getRows() {
        try {
            return getSection().getRows();
        } catch (Exception e) {
            return new ArrayList<HtmlRow>();
        }
    }

    private HtmlSection getSection() throws ClientException {
        if (section == null) {
            @SuppressWarnings("unchecked")
            List<Map<String, Serializable>> sectionsMap = (List<Map<String, Serializable>>) doc.getPropertyValue("html:sections");
            List<HtmlSection> sections = new ArrayList<HtmlSection>(
                    sectionsMap.size());
            for (Map<String, Serializable> map : sectionsMap) {
                sections.add(new HtmlSectionImpl(this, map));
            }
            if (sections.size() > 0) {
                section = sections.get(0);
            } else {
                section = new HtmlSectionImpl(this);
                section.setTitle(getTitle());
            }

        }
        return section;

    }

    private void update() throws ClientException {
        List<Map<String, Serializable>> sectionsMap = new ArrayList<Map<String, Serializable>>();
        HtmlSectionImpl si = (HtmlSectionImpl) getSection();
        sectionsMap.add(si.toMap());
        doc.setPropertyValue("html:sections", (Serializable) sectionsMap);
    }

    @Override
    public HtmlRow row(int index) {
        try {
            return getSection().row(index);
        } catch (ClientException e) {
            return null;
        }
    }

    @Override
    public HtmlSection insertBefore() throws ClientException {
        return this;
    }

    @Override
    public void remove() throws ClientException {
        // Do nothing
    }

    @Override
    public HtmlRow insertBefore(HtmlRow htmlRow) throws ClientException {
        return getSection().insertBefore(htmlRow);
    }

    @Override
    public void remove(HtmlRow row) throws ClientException {
        getSection().remove(row);

    }

    @Override
    public void onChange(Object obj) throws ClientException {
        update();
        if (isParentPageNewsPublished()) {
            Calendar now = Calendar.getInstance();
            Calendar startPublication = getStartPublication();
            if (startPublication != null && startPublication.before(now) && now.before(getEndPublication())) {
                setStartPublication(now);
            }
        }

    }

    private boolean isParentPageNewsPublished() {
        DocumentModel parentDocument;
        try {
            parentDocument = getSession().getParentDocument(doc.getRef());
            PageNews pageNews = parentDocument.getAdapter(PageNews.class);
            if (pageNews != null) {
                if (pageNews.isVisible()) {
                    return true;
                }
            }
        } catch (ClientException e) {
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return this.doc.getId().equals(((LabsNews) obj).getDocumentModel().getId());
    }

    @Override
    public HtmlRow addRow(String cssClass, List<String> userClass) throws ClientException {
        return getSection().addRow(cssClass, userClass);
    }

    @Override
    public LabsNewsBlobHolder getBlobHolder() throws ClientException {
//        if(blobHolder == null){
//            blobHolder = (LabsNewsBlobHolder)this.doc.getAdapter(BlobHolder.class);
//        }
        return blobHolder;
    }

    @Override
    public void setOriginalPicture(Blob blob) throws ClientException {
        getBlobHolder().setBlob(blob);
        
    }

    @Override
    public void setSummaryPicture(Blob blob) throws ClientException, IOException {
        getBlobHolder().addAccordeonPicture(blob);
        
    }

    @Override
    public String getCropCoords() throws ClientException {
        return getBlobHolder().getCropCoords();
    }

    @Override
    public void setCropCoords(String cropCoords) throws ClientException {
        getBlobHolder().setCropCoords(cropCoords);
    }

    @Override
    public Blob getSummaryPicture() throws ClientException, IOException {
        return getBlobHolder().getBlob(LabsNewsBlobHolder.SUMMARY_TRUNCATED_PICTURE);
    }

    @Override
    public boolean hasSummaryPicture() {
        try {
            return getBlobHolder().getBlob(LabsNewsBlobHolder.SUMMARY_TRUNCATED_PICTURE) != null;
        } catch (ClientException e) {
        	return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void checkPicture(Blob blob) throws LabsBlobHolderException, ClientException {
        getBlobHolder().checkPicture(blob);
    }

    @Override
    public void deleteSummaryPicture() throws ClientException {
        getBlobHolder().deleteSummaryPicture();
    }

    @Override
    public void setTitle(String title) throws PropertyException,
            ClientException, IllegalArgumentException {
        if (title == null) {
            throw new IllegalArgumentException("title cannot be null.");
        }
        doc.setPropertyValue(DC_TITLE, title);
    }

    @Override
    public void setDescription(String description) throws PropertyException,
            ClientException {
        setDescription(doc, description);
    }
//
//	@Override
//	public void moveUp(int index) throws ClientException {
//		if (index - 1 < 0){
//			return;
//		}
//		Tools.changePositionWith(index, index - 1, getRows());
//		update();
//	}
//
//	@Override
//	public void moveDown(int index) throws ClientException  {
//		if (index + 1 > getRows().size()){
//			return;
//		}
//		Tools.changePositionWith(index, index + 1, getRows());
//		update();
//	}


	@Override
	public void moveUp(int index) throws ClientException {
		if (index - 1 < 0){
			return;
		}
		Tools.changePositionWith(index, index - 1, getSection().getRows());
		updateRows();
		update();
	}

	@Override
	public void moveDown(int index) throws ClientException  {
		if (index + 1 > getSection().getRows().size()){
			return;
		}
		Tools.changePositionWith(index, index + 1, getSection().getRows());
		updateRows();
		update();
	}

	private void updateRows() {
		List<Serializable> rowsMap = new ArrayList<Serializable>();
        for (HtmlRow row : getRows()) {
            rowsMap.add((Serializable) row.toMap());
        }
        toMap().put("rows", (Serializable) rowsMap);
	}

	@Override
	public Map<String, Serializable> toMap() {
		try {
			return getSection().toMap();
		} catch (ClientException e) {
			return null;
		}
	}

	@Override
	public boolean isTop() throws ClientException {
		Serializable propertyValue = doc.getPropertyValue(IS_TOP);
        if (propertyValue instanceof Boolean) {
            return ((Boolean) propertyValue).booleanValue();
        }
        return false;
	}

	@Override
	public void setTop(boolean isTop) throws ClientException {
		doc.setPropertyValue(IS_TOP, isTop);
	}
}
