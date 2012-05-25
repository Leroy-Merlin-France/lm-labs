package com.leroymerlin.corp.fr.nuxeo.labs.site.event;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.ec.notification.NotificationConstants;
import org.nuxeo.ecm.platform.notification.api.NotificationManager;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;
import com.leroymerlin.corp.fr.nuxeo.features.directory.LMTestDirectoryFeature;
import com.leroymerlin.corp.fr.nuxeo.labs.site.Page;
import com.leroymerlin.corp.fr.nuxeo.labs.site.SiteManager;
import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.notification.PageSubscription;
import com.leroymerlin.corp.fr.nuxeo.labs.site.publisher.LabsPublisher;
import com.leroymerlin.corp.fr.nuxeo.labs.site.test.SiteFeatures;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Docs;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.NotifNames;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.PermissionsHelper;

@RunWith(FeaturesRunner.class)
@Features({ LMTestDirectoryFeature.class, SiteFeatures.class })
@Deploy({"org.nuxeo.ecm.platform.notification.api", 
    "org.nuxeo.ecm.platform.types.core",
    "org.nuxeo.ecm.platform.types.api",
    "org.nuxeo.ecm.platform.notification.core",
    "org.nuxeo.ecm.core.persistence",
    "org.nuxeo.ecm.platform.placeful.core:OSGI-INF/nxplacefulservice-framework.xml",
    "com.leroymerlin.labs.core.test:OSGI-INF/hibernate-contrib.xml",
    "com.leroymerlin.labs.core.test:OSGI-INF/core-types-contribTest.xml",
    "com.leroymerlin.labs.core.test:OSGI-INF/core-types-contrib2Test.xml",
    "com.leroymerlin.labs.core.test:OSGI-INF/document-types-contrib.xml",
    "com.leroymerlin.labs.core.test:OSGI-INF/notification-override.xml",
    "org.nuxeo.ecm.platform.url.core",
    "org.nuxeo.ecm.platform.placeful.api"})
@RepositoryConfig(user="Administrator")
public class PageSubscriptionForSiteSubscribersTest {

    private static final String URL_SITE1 = "site1";
    private static final String DOCNAME_CLASSEUR1 = "classeur1";

    private static final String CGM = "CGM";
//    private static final String CSLOG = "CSLOG";

    @Inject
    SiteManager sm;

    @Inject
    private CoreSession session;
    
    @Inject NotificationManager nm;
    
    @Test
    public void notificationDeployed() throws Exception {
        assertNotNull(nm.getNotificationByName(NotifNames.PAGE_MODIFIED));
    }

    @Test
    public void iCanSubsribeToSite() throws Exception {
        LabsSite labsSite = sm.createSite(session, "Site1", URL_SITE1);
        PermissionsHelper.addPermission(labsSite.getDocument(), SecurityConstants.READ, CGM, true);
        session.save();
        PageSubscription subscriptionAdapter = labsSite.getDocument().getAdapter(PageSubscription.class);
        assertNotNull(subscriptionAdapter);
        subscriptionAdapter.subscribe(CGM);
        session.save();
        assertTrue(subscriptionAdapter.isSubscribed(CGM));
    }
    
    @Test
    public void siteSubscriberSubscribedOnPageWhenCreated() throws Exception {
        LabsSite labsSite = sm.getSite(session, URL_SITE1);
        DocumentModel classeur = session.createDocumentModel(Docs.PAGECLASSEUR.type());
        classeur.setPathInfo(labsSite.getTree(session).getPathAsString(), DOCNAME_CLASSEUR1);
//        classeur.putContextData(NotificationConstants.DISABLE_NOTIFICATION_SERVICE, new Boolean(true));
        classeur = session.createDocument(classeur);
        session.save();
        assertTrue(nm.getUsersSubscribedToNotificationOnDocument(NotifNames.PAGE_MODIFIED, classeur.getId()).contains(NotificationConstants.USER_PREFIX + CGM));
        assertTrue(classeur.getAdapter(PageSubscription.class).isSubscribed(CGM));
    }
    
    @Test
    public void makeSurePageModificationDoesNotSubscribeUser() throws Exception {
        LabsSite labsSite = sm.getSite(session, URL_SITE1);
        DocumentModel classeur = session.getDocument(new PathRef(labsSite.getTree(session).getPathAsString() + "/" + DOCNAME_CLASSEUR1));
        classeur.getAdapter(PageSubscription.class).unsubscribe(CGM);
        assertFalse(classeur.getAdapter(PageSubscription.class).isSubscribed(CGM));
        classeur.getAdapter(Page.class).setDescription("desc");
        session.saveDocument(classeur);
        session.save();
        assertFalse(classeur.getAdapter(PageSubscription.class).isSubscribed(CGM));
    }
    
    @Test
    public void makeSurePagePublicationDoesNotSubscribeUser() throws Exception {
        LabsSite labsSite = sm.getSite(session, URL_SITE1);
        DocumentModel classeur = session.getDocument(new PathRef(labsSite.getTree(session).getPathAsString() + "/" + DOCNAME_CLASSEUR1));
        classeur.getAdapter(PageSubscription.class).unsubscribe(CGM);
        assertFalse(classeur.getAdapter(PageSubscription.class).isSubscribed(CGM));
        classeur.getAdapter(LabsPublisher.class).publish();
        session.saveDocument(classeur);
        session.save();
        assertFalse(classeur.getAdapter(PageSubscription.class).isSubscribed(CGM));
    }
    
    @Test
    public void makeSurePageCopySubscribeUser() throws Exception {
        LabsSite labsSite = sm.getSite(session, URL_SITE1);
        DocumentModel classeur = session.getDocument(new PathRef(labsSite.getTree(session).getPathAsString() + "/" + DOCNAME_CLASSEUR1));
        classeur.getAdapter(PageSubscription.class).subscribe(CGM);
        assertTrue(classeur.getAdapter(PageSubscription.class).isSubscribed(CGM));
        DocumentModel copy = session.copy(classeur.getRef(), labsSite.getTree(session).getRef(), "copyOf_classeur1");
        session.save();
        assertTrue(copy.getAdapter(PageSubscription.class).isSubscribed(CGM));
    }
}
