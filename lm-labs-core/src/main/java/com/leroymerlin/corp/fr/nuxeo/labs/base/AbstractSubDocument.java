package com.leroymerlin.corp.fr.nuxeo.labs.base;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.ecm.platform.filemanager.api.FileManager;
import org.nuxeo.runtime.api.Framework;

import com.leroymerlin.common.core.utils.Slugify;
import com.leroymerlin.corp.fr.nuxeo.labs.site.classeur.ClasseurException;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.FacetNames;

public abstract class AbstractSubDocument extends LabsAdapterImpl implements SubDocument {

    public AbstractSubDocument(DocumentModel document) {
		super(document);
	}

	@Override
    public DocumentModel addFile(Blob blob, String description, String title)
            throws ClientException {
        if (blob == null) {
            throw new IllegalArgumentException(
                    "Could not find any uploaded file");
        } else {
            try {
                return doAddfile(blob, description, title);
            } catch (Exception e) {
                throw new ClasseurException(
                        "label.classeur.error.unable_to_add_file_to_folder", e);
            }
        }
    }

    private DocumentModel doAddfile(Blob blob, String description, String title)
            throws Exception {
        blob.persist();
        CoreSession session = getSession();
        String filename = blob.getFilename();
        DocumentModel fileDoc = Framework.getService(FileManager.class)
                .createDocumentFromBlob(session, blob,
                        doc.getPathAsString(), true,
                        Slugify.slugify(filename, false));
        if(!StringUtils.isEmpty(title)){
            fileDoc.setPropertyValue("dc:title", title);
        }
        else{
            fileDoc.setPropertyValue("dc:title", filename);
        }
        if (!StringUtils.isEmpty(description)) {
            fileDoc.setPropertyValue("dc:description", description);
        }
        fileDoc = session.saveDocument(fileDoc);
        return fileDoc;
    }

    @Override
    public DocumentModelList getFiles() throws ClientException {
    	CoreSession session = getSession();
        StringBuilder sb = new StringBuilder("SELECT * From Document");
        sb.append(" WHERE ecm:parentId = '")
                .append(doc.getId())
                .append("'");
        sb.append(" AND ecm:isProxy = 0 AND ecm:isCheckedInVersion = 0");
        sb.append(" AND ecm:currentLifeCycleState <> 'deleted'");
        if (!session.hasPermission(doc.getParentRef(), SecurityConstants.EVERYTHING)) {
            sb.append(" AND ").append(NXQL.ECM_MIXINTYPE).append(" <> '").append(FacetNames.LABSHIDDEN).append("'");
        }
        sb.append(" ORDER BY dc:title ASC");

        DocumentModelList list = session.query(sb.toString());
        return list;

    }

    @Override
    public void removeFile(String filename) throws ClientException {
        DocumentModelList files = getFiles();
        for(DocumentModel doc : files) {
            if(filename.equals(doc.getTitle())) {
                getSession().removeDocument(doc.getRef());
                break;
            }
        }
    }

}
