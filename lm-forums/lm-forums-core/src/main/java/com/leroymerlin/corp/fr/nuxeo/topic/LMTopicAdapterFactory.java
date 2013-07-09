package com.leroymerlin.corp.fr.nuxeo.topic;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

public class LMTopicAdapterFactory implements DocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> klass) {
        if (doc.getType().equals("LMForumTopic")) {
            return new LMTopicImpl(doc);
        }
        return null;
    }
}
