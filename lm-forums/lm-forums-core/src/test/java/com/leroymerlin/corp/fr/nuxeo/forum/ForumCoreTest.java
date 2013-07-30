package com.leroymerlin.corp.fr.nuxeo.forum;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.comment.api.CommentManager;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;
import com.leroymerlin.corp.fr.nuxeo.labs.base.LabsCommentFeature;
import com.leroymerlin.corp.fr.nuxeo.labs.site.Page;
import com.leroymerlin.corp.fr.nuxeo.labs.site.SiteManager;
import com.leroymerlin.corp.fr.nuxeo.labs.site.exception.SiteManagerException;
import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.publisher.LabsPublisher;
import com.leroymerlin.corp.fr.nuxeo.labs.site.test.LabsRepositoryInit;
import com.leroymerlin.corp.fr.nuxeo.labs.site.test.SiteFeatures;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants;
import com.leroymerlin.corp.fr.nuxeo.topic.LMTopic;

@RunWith(FeaturesRunner.class)
@Features({ CoreFeature.class, SiteFeatures.class, LabsCommentFeature.class })
@Deploy({ "org.nuxeo.ecm.opensocial.spaces", "com.leroymerlin.labs.forums.core" })
@RepositoryConfig(init = LabsRepositoryInit.class)
public class ForumCoreTest {

    private static final String FORUM_TITLE = "Mon titre de forum";
    private static final String FORUM_DESCRIPTION = "Ma description de forum";

    private static final String TOPIC_TITLE = "Mon titre de topic";
    private static final String TOPIC_DESCRIPTION = "Ma description de topic";

    private static final String TEST_COMMENT = "test comment";

    @Inject
    CoreSession session;

    @Inject
    CommentManager commentManager;

    @Inject
    SiteManager siteManager;

    @Test
    public void iCanCreateAForumDoc() throws Exception {
        DocumentModel doc = getOrCreateMyForum();
        LMForum forum = doc.getAdapter(LMForum.class);

        assertNotNull(forum);
        forum.setTitle(FORUM_TITLE);
        forum.setDescription(FORUM_DESCRIPTION);
        // doc.setPropertyValue("dc:title", FORUM_TITLE);
        // doc.setPropertyValue("dc:description", FORUM_DESCRPTION);
        doc = session.saveDocument(forum.getDocument());
        session.save();

        doc = session.getDocument(new PathRef(getSite().getTree()
                .getPathAsString() + "/myForum"));
        assertThat(doc.getTitle(), is(FORUM_TITLE));
    }

    @Test
    public void iCanCreateATopicInAForum() throws Exception {
        DocumentModel doc = getOrCreateMyForum();
        LMForum forum = doc.getAdapter(LMForum.class);
        forum.addTopic(session, "myTopic");

        // forum.getTopic(session, "myTopic").setPropertyValue("dc:title",
        // TOPIC_TITLE);
        // forum.getTopic(session, "myTopic").setPropertyValue("dc:description",
        // TOPIC_DESCRIPTION);
        // session.saveDocument(forum.getTopic(session, "myTopic"));
        LMTopic topic = forum.getTopic(session, "myTopic");
        topic.setTitle(TOPIC_TITLE);
        topic.setDescription(TOPIC_DESCRIPTION);
        session.saveDocument(topic.getDocument());
        session.save();

        // DocumentModel doc = session.createDocumentModel("/myForum",
        // "myTopic","LMForumTopic");
        // doc = session.createDocument(doc);
        // doc.setPropertyValue("dc:title", TOPIC_TITLE);
        // doc.setPropertyValue("dc:description", TOPIC_DESCRIPTION);
        // session.saveDocument(doc);
        // session.save();
    }

    @Test
    public void iCanGetAForumAdapter() throws Exception {
        DocumentModel doc = getOrCreateMyForum();
        LMForum forum = doc.getAdapter(LMForum.class);
        assertThat(forum, is(notNullValue()));
        assertThat(forum.getTitle(), is(FORUM_TITLE));
        assertThat(forum.getDescription(), is(FORUM_DESCRIPTION));
    }

    @Test
    public void isForumCommentable() throws Exception {
        DocumentModel doc = getOrCreateMyForum();
        assertTrue(doc.hasFacet("Commentable"));
    }

    @Test
    public void isCommentManagerNotNull() throws Exception {
        assertThat(commentManager, is(notNullValue()));
    }

    @Test
    public void iCanGetATopicAdapter() throws Exception {
        LMForum forum = getOrCreateMyForum().getAdapter(LMForum.class);
        getOrCreateMyTopic(forum);
        DocumentModel doc = session.getDocument(new PathRef(getSite().getTree()
                .getPathAsString() + "/myForum/myTopic"));
        LMTopic topic = doc.getAdapter(LMTopic.class);
        assertThat(topic, is(notNullValue()));
        assertThat(topic.getTitle(), is(TOPIC_TITLE));
        assertThat(topic.getDescription(), is(TOPIC_DESCRIPTION));
    }

    @Test
    public void iCanCreateCommentsOnTopic() throws Exception {
        LMForum forum = getOrCreateMyForum().getAdapter(LMForum.class);
        getOrCreateMyTopic(forum);
        DocumentModel doc = session.getDocument(new PathRef(getSite().getTree()
                .getPathAsString() + "/myForum/myTopic"));
        LMTopic topic = doc.getAdapter(LMTopic.class);
        assertThat(topic, is(notNullValue()));

        topic.addComment(session, TEST_COMMENT);
        assertThat(topic.getComments(), is(notNullValue()));
        assertThat(topic.getComments().size(), is(1));
        assertThat(
                (String) topic.getComments().get(0)
                        .getPropertyValue("comment:text"), is(TEST_COMMENT));
    }

    @Test
    public void LMForumHasAPageAdapter() throws Exception {
        getOrCreateMyForum().getAdapter(LMForum.class);
        DocumentModel doc = session.getDocument(new PathRef(getSite().getTree()
                .getPathAsString() + "/myForum"));
        Page page = doc.getAdapter(Page.class);
        assertThat(page, is(notNullValue()));

        LabsPublisher publisher = doc.getAdapter(LabsPublisher.class);
        assertThat(publisher, is(notNullValue()));
    }

    @Test
    public void canIsAllContibutorsDefault() throws Exception {
        getOrCreateMyForum().getAdapter(LMForum.class);
        DocumentModel doc = session.getDocument(new PathRef(getSite().getTree()
                .getPathAsString() + "/myForum"));
        LMForum forum = doc.getAdapter(LMForum.class);
        assertThat(forum.isAllContributors(), is(true));
    }

    @Test
    public void canSetAndIsAllContibutors() throws Exception {
        getOrCreateMyForum();
        DocumentModel doc = session.getDocument(new PathRef(getSite().getTree()
                .getPathAsString() + "/myForum"));
        LMForum forum = doc.getAdapter(LMForum.class);
        List<String> fieldsNotDisplayable = new ArrayList<String>();
        fieldsNotDisplayable.add(LMForumImpl.TOPIC_NOT_ALL_CONTRIBUTOR);
        forum.setNotDisplayableParameters(fieldsNotDisplayable);
        forum.manageAllContributors(false);
        session.saveDocument(forum.getDocument());
        session.save();
        doc = session.getDocument(new PathRef(getSite().getTree()
                .getPathAsString() + "/myForum"));
        forum = doc.getAdapter(LMForum.class);
        assertThat(forum.isAllContributors(), is(false));

        forum.setNotDisplayableParameters(new ArrayList<String>());
        forum.manageAllContributors(true);
        session.saveDocument(forum.getDocument());
        session.save();
        doc = session.getDocument(new PathRef(getSite().getTree()
                .getPathAsString() + "/myForum"));
        forum = doc.getAdapter(LMForum.class);
        assertThat(forum.isAllContributors(), is(true));
    }

    @Test
    public void iCanCreateAssets() throws Exception {
        LMForum forum1 = getOrCreateMyForum().getAdapter(LMForum.class);
        LabsSite site = getSite();
        DocumentModel docAssetsForum1 = site.getAssetsPublicDoc(
                LabsSiteConstants.Docs.ASSETS_FORUMS, forum1.getDocument()
                        .getName());
        assertEquals(
                "/default-domain/sites/monsite/assets/assetsForums/myForum",
                docAssetsForum1.getPathAsString());

        // autre forum mais avec meme nom
        DocumentModel docForum2 = session.createDocumentModel(getSite()
                .getTree().getPathAsString(), "myForum", "PageForum");
        docForum2 = session.createDocument(docForum2);
        LMForum forum2 = docForum2.getAdapter(LMForum.class);
        forum2.setTitle(FORUM_TITLE);
        forum2.setDescription(FORUM_DESCRIPTION);
        docForum2 = session.saveDocument(docForum2);
        DocumentModel docAssetsForum2 = site.getAssetsPublicDoc(
                LabsSiteConstants.Docs.ASSETS_FORUMS, forum2.getDocument()
                        .getName());
        assertNotSame(
                "/default-domain/sites/monsite/assets/assetsForums/myForum",
                docAssetsForum2.getPathAsString());
    }

    private LabsSite getSite() throws ClientException, SiteManagerException {
        LabsSite site = siteManager.getSite(session, SiteFeatures.SITE_NAME);
        return site;
    }

    private DocumentModel getOrCreateMyForum() throws ClientException,
            SiteManagerException {
        PathRef ref = new PathRef(getSite().getTree().getPathAsString()
                + "/myForum");
        if (!session.exists(ref)) {
            DocumentModel doc = session.createDocumentModel(getSite().getTree()
                    .getPathAsString(), "myForum", "PageForum");
            doc = session.createDocument(doc);
            LMForum forum = doc.getAdapter(LMForum.class);
            forum.setTitle(FORUM_TITLE);
            forum.setDescription(FORUM_DESCRIPTION);
            doc = session.saveDocument(doc);
        }
        return session.getDocument(ref);
    }

    private DocumentModel getOrCreateMyTopic(LMForum forum)
            throws ClientException, SiteManagerException {
        PathRef ref = new PathRef(forum.getDocument().getPathAsString()
                + "/myTopic");
        if (!session.exists(ref)) {
            LMTopic topic = forum.addTopic(session, "myTopic");
            topic.setTitle(TOPIC_TITLE);
            topic.setDescription(TOPIC_DESCRIPTION);
            session.saveDocument(topic.getDocument());
        }
        return session.getDocument(ref);
    }
}