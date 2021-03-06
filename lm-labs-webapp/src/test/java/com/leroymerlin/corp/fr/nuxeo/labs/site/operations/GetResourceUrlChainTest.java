package com.leroymerlin.corp.fr.nuxeo.labs.site.operations;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;
import com.leroymerlin.corp.fr.nuxeo.labs.site.SiteManager;
import com.leroymerlin.corp.fr.nuxeo.labs.site.features.LabsWebAppFeature;
import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.test.OfmRepositoryInit;
import com.leroymerlin.corp.fr.nuxeo.labs.site.test.PageClasseurPageRepositoryInit;

@RunWith(FeaturesRunner.class)
@Features( { LabsWebAppFeature.class })
@Deploy({ "org.nuxeo.ecm.automation.core" })
@RepositoryConfig(init = PageClasseurPageRepositoryInit.class)
public class GetResourceUrlChainTest {

    private static final String OPERATIONSCHAIN = "LabsSite.GetResourceUrl";

    @Inject
    AutomationService service;

    @Inject
    CoreSession session;

    @Test
    public void iCanGetFileUrl() throws Exception {
        LabsSite site = getSiteManager().getSite(session, OfmRepositoryInit.SITE_URL);
        DocumentModel folder = session.getDocument(
                new PathRef(
                        site.getTree().getPathAsString() + "/" + PageClasseurPageRepositoryInit.PAGE_CLASSEUR_TITLE + "/" + PageClasseurPageRepositoryInit.FOLDER1_NAME));
        OperationContext ctx = new OperationContext(session);
        OperationChain chain = service.getOperationChain(OPERATIONSCHAIN);
        chain.getOperations().get(0).set("value", folder.getId());
        String url = (String) service.run(ctx, chain);
        assertEquals("ofm/Page Classeur/folder1", url);
    }
    
    protected SiteManager getSiteManager() {
        try {
            return Framework.getService(SiteManager.class);
        } catch (Exception e) {
            return null;
        }
    }

}
