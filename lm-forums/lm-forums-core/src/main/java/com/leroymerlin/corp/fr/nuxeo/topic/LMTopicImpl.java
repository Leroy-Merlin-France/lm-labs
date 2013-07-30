package com.leroymerlin.corp.fr.nuxeo.topic;

import java.util.Date;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.comment.api.CommentableDocument;

import com.leroymerlin.corp.fr.nuxeo.labs.base.AbstractPage;

public class LMTopicImpl extends AbstractPage implements LMTopic {

    public LMTopicImpl(DocumentModel doc) {
        super(doc);
    }

    public void addComment(CoreSession session, String comment)
            throws ClientException {
        CommentableDocument aCommentableDocument = doc
                .getAdapter(CommentableDocument.class);
        DocumentModel newComment = newComment(session, comment);
        // The method addComment save the comment and the link.
        aCommentableDocument.addComment(newComment);
    }

    private DocumentModel newComment(CoreSession session, String cText)
            throws ClientException {
        DocumentModel comment = session.createDocumentModel("Comment");
        comment.setPropertyValue("comment:author", session.getPrincipal()
                .getName());
        comment.setPropertyValue("comment:text", cText);
        comment.setPropertyValue("comment:creationDate", new Date()); 

        return comment;
    }

    @Override
    public List<DocumentModel> getComments() throws ClientException {
        CommentableDocument aCommentableDocument = doc
                .getAdapter(CommentableDocument.class);
        return aCommentableDocument.getComments();
    }

    @Override
    public Integer getNbComments() throws ClientException {
        CommentableDocument aCommentableDocument = doc
                .getAdapter(CommentableDocument.class);
        return aCommentableDocument.getComments().size();
    }
}
