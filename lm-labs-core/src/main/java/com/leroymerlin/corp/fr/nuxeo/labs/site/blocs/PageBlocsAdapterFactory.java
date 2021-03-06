package com.leroymerlin.corp.fr.nuxeo.labs.site.blocs;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants;

public class PageBlocsAdapterFactory implements DocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        if(LabsSiteConstants.Docs.PAGEBLOCS.type().equals(doc.getType())) {
            return new PageBlocsAdapter(doc);
        }
        return null;
    }

}
