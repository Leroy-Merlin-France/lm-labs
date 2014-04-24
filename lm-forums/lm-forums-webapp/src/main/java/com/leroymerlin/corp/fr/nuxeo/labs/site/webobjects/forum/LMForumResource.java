package com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects.forum;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.WebException;
import org.nuxeo.ecm.webengine.forms.FormData;
import org.nuxeo.ecm.webengine.model.WebObject;

import com.leroymerlin.corp.fr.nuxeo.forum.LMForum;
import com.leroymerlin.corp.fr.nuxeo.labs.base.AbstractLabsBase;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.AuthorFullName;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.Tools;
import com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects.NotifiablePageResource;
import com.leroymerlin.corp.fr.nuxeo.topic.LMTopic;

@WebObject(type = "PageForum", superType = "LabsPage")
public class LMForumResource extends NotifiablePageResource {

     private static final Log log = LogFactory.getLog(LMForumResource.class);

    LMForum lmForum;

    @Override
    public void initialize(Object... args) {
        super.initialize(args);
        ctx.getEngine().getRendering()
                .setSharedVariable("forum", getLabsForum());
    }

    public LMForum getLabsForum() {
        CoreSession session = getCoreSession();
        LMForum forum = Tools.getAdapter(LMForum.class, doc, session);
        afn = new AuthorFullName(new HashMap<String, String>(),
                LabsSiteConstants.Forum.FORUM_CREATOR);
        List<DocumentModel> listModelDoc = new LinkedList<DocumentModel>();
        try {
            for (LMTopic aLmTopic : forum.getTopics(session))
                listModelDoc.add(aLmTopic.getDocument());

            afn.loadFullName(listModelDoc);
        } catch (ClientException e) {
            log.error("Error", e);
        }
        

        return forum;
    }

    @Override
    @POST
    public Response doPost() {
        LMTopic topic = null;
        try {

            FormData form = ctx.getForm();
            String pTitle = form.getString(AbstractLabsBase.DC_TITLE);
            CoreSession session = ctx.getCoreSession();
            LMForum lmForum = Tools.getAdapter(LMForum.class, doc, session);
            topic = lmForum.addTopic(session, pTitle);
            topic.setDescription(form
                    .getString(AbstractLabsBase.DC_DESCRIPTION));
            topic.setCommentable(true);
            save(topic);

            return redirect(getPath() + "/");
        } catch (ClientException e) {
            throw WebException.wrap(e);
        }
    }

    /**
     * save a new topic
     * 
     * @param topic
     * @return a {@link DocumentModel}
     */
    private DocumentModel save(LMTopic topic) {
        CoreSession session = getCoreSession();
        DocumentModel newDoctopic = null;
        try {
            newDoctopic = session.saveDocument(topic.getDocument());
            session.save();
        } catch (ClientException e) {
            throw WebException.wrap(e);
        }
        return newDoctopic;
    }
}
