package com.leroymerlin.corp.fr.nuxeo.labs.site.blocs;

import org.nuxeo.ecm.core.api.DocumentModel;

import com.leroymerlin.corp.fr.nuxeo.labs.base.AbstractPage;

public class PageBlocsAdapter extends AbstractPage implements PageBlocs {

    public PageBlocsAdapter(DocumentModel doc) {
        super(doc);
    }
}
