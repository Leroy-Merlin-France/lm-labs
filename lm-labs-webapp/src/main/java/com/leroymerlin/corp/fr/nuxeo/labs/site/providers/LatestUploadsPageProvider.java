package com.leroymerlin.corp.fr.nuxeo.labs.site.providers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.Filter;
import org.nuxeo.ecm.platform.query.api.AbstractPageProvider;
import org.nuxeo.ecm.platform.query.api.PageProvider;

import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteUtils;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteWebAppUtils;

public class LatestUploadsPageProvider extends AbstractPageProvider<DocumentModel> implements PageProvider<DocumentModel> {

    private static final long serialVersionUID = 1L;

    public static final String PARENT_DOCUMENT_PROPERTY = "parentDocument";

    private static final Log LOG = LogFactory.getLog(LatestUploadsPageProvider.class);

    private static final String UPLOADS_SORT_FIELD = "dc:modified";
    
    protected List<DocumentModel> pageUploads = null;

    @Override
    public List<DocumentModel> getCurrentPage() {
        if (pageUploads == null) {
            pageUploads = new ArrayList<DocumentModel>();
            DocumentModel doc = getParentDocument();
            StringBuilder query = new StringBuilder();
            try {
                query.append("SELECT * FROM Document WHERE ")
                .append("ecm:currentLifeCycleState <> 'deleted'")
                .append(" AND ")
                .append("ecm:path STARTSWITH '" + LabsSiteUtils.getSiteTree(LabsSiteUtils.getParentSite(doc)).getPathAsString() + "'")
                .append(" ORDER BY " + UPLOADS_SORT_FIELD + " DESC");
                @SuppressWarnings("serial")
                List<DocumentModel> documents = doc.getCoreSession().query(query.toString(), new Filter() {
                    @Override
                    public boolean accept(DocumentModel arg0) {
                        try {
                            return LabsSiteWebAppUtils.canGetPreview(arg0);
                        } catch (ClientException e) {
                            LOG.error(e, e);
                        }
                        return false;
                    }});
                if (!hasError()) {
                    resultsCount = documents.size();
                    long pageSize = getMinMaxPageSize();
                    if (pageSize == 0) {
                        pageUploads.addAll(documents);
                    } else {
                        // handle offset
                        if (offset <= resultsCount) {
                            for (int i = Long.valueOf(offset).intValue(); i < resultsCount
                                    && i < offset + pageSize; i++) {
                                pageUploads.add(documents.get(i));
                            }
                        }
                    }
                }
            } catch (ClientException e) {
                error = e;
                LOG.error(e, e);
            }
        }
        return pageUploads;
    }

    private DocumentModel getParentDocument() {
        Map<String, Serializable> props = this.getProperties();
        return (DocumentModel) props.get(PARENT_DOCUMENT_PROPERTY);
    }

    /* (non-Javadoc)
     * @see org.nuxeo.ecm.platform.query.api.AbstractPageProvider#isSortable()
     */
    @Override
    public boolean isSortable() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.nuxeo.ecm.platform.query.api.AbstractPageProvider#pageChanged()
     */
    @Override
    protected void pageChanged() {
        super.pageChanged();
        pageUploads = null;
    }

    /* (non-Javadoc)
     * @see org.nuxeo.ecm.platform.query.api.AbstractPageProvider#refresh()
     */
    @Override
    public void refresh() {
        super.refresh();
        pageUploads = null;
    }

}