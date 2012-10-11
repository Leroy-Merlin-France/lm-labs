package com.leroymerlin.corp.fr.nuxeo.labs.site;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.runtime.model.DefaultComponent;

import com.leroymerlin.common.core.security.SecurityData;
import com.leroymerlin.corp.fr.nuxeo.labs.site.exception.SiteManagerException;
import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Docs;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteUtils;
import com.leroymerlin.common.core.security.SecurityDataHelper;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.Tools;

public class SiteManagerImpl extends DefaultComponent implements SiteManager {

    private static final PathRef SITES_ROOT_REF = new PathRef(
            "/" + Docs.DEFAULT_DOMAIN.docName() + "/" + Docs.SITESROOT.docName());

    @Override
    public LabsSite createSite(CoreSession session, String title, String url)
            throws ClientException, SiteManagerException {

        validateSiteCreationRequest(session, title, url);

        DocumentModel sitesRoot = getSiteRoot(session);
        DocumentModel docLabsSite = session.createDocumentModel(
                sitesRoot.getPathAsString(), LabsSiteUtils.doLabsSlugify(title),
                LabsSiteConstants.Docs.SITE.type());

        LabsSite labSite = Tools.getAdapter(LabsSite.class, docLabsSite, session);
        labSite.setTitle(title);
        labSite.setURL(url);
        docLabsSite = session.createDocument(docLabsSite);
        session.save();
        final DocumentRef ref = docLabsSite.getRef();
        final String user = session.getPrincipal().getName();
        UnrestrictedSessionRunner runner = new UnrestrictedSessionRunner(
                session) {
            @Override
            public void run() throws ClientException {
                DocumentModel docLabsSite2 = session.getDocument(ref);
                SecurityData data = SecurityDataHelper.buildSecurityData(docLabsSite2);
                data.addModifiablePrivilege(user, SecurityConstants.EVERYTHING, true);
                data.setBlockRightInheritance(true, null);
                SecurityDataHelper.updateSecurityOnDocument(docLabsSite2, data);
                session.save();
            }
        };
        runner.runUnrestricted();
        return Tools.getAdapter(LabsSite.class, docLabsSite, session);
    }


    private void validateSiteCreationRequest(CoreSession session, String title, String url)
            throws ClientException, SiteManagerException {

        if(StringUtils.isEmpty(title.trim())) {
            throw new SiteManagerException(
                    "label.labssites.edit.required.title");
        }

        if(StringUtils.isEmpty(url.trim())) {
            throw new SiteManagerException(
                    "label.labssites.edit.required.url");
        }

        if (siteExists(session, url)) {
            throw new SiteManagerException(
                    "label.labssites.edit.error.url.exist");
        }
    }

    public DocumentModel getSiteRoot(CoreSession session)
            throws ClientException {
        DocumentRef sitesRootRef = SITES_ROOT_REF;
        if (!session.exists(sitesRootRef)) {
            return createSitesRoot(session);
        } else {
            return session.getDocument(sitesRootRef);
        }

    }

    private DocumentModel createSitesRoot(CoreSession session)
            throws ClientException {
        UnrestrictedSessionRunner runner = new UnrestrictedSessionRunner(
                session) {

            @Override
            public void run() throws ClientException {
                DocumentModel sitesRoot = session.createDocumentModel(
                        "/" + Docs.DEFAULT_DOMAIN.docName() + "/", Docs.SITESROOT.docName(), Docs.SITESROOT.type());
                sitesRoot.setPropertyValue("dc:title", "Default root of sites");
                sitesRoot = session.createDocument(sitesRoot);
            }
        };

        runner.runUnrestricted();
        return session.getDocument(SITES_ROOT_REF);
    }

    @Override
    public LabsSite getSite(CoreSession session, String url)
            throws ClientException, SiteManagerException {
        final String escapedUrl = url.replace("'", "\\'");
        String query = String.format("SELECT * FROM %s WHERE webc:url = '%s' AND ecm:path STARTSWITH '%s'",
                LabsSiteConstants.Docs.SITE.type(), escapedUrl, SITES_ROOT_REF.toString());
        DocumentModelList sites = session.query(query);
        assert sites.size() <= 1;
        if (sites.size() == 1) {
            return Tools.getAdapter(LabsSite.class, sites.get(0), session);
        } else {
            throw new SiteManagerException(
                    "label.sitemanager.error.site_does_not_exists");
        }

    }

    @Override
    public void removeSite(CoreSession session, LabsSite site)
            throws ClientException {
        session.removeDocument(site.getDocument().getRef());

    }

    @Override
    public Boolean siteExists(CoreSession session, String url)
            throws ClientException {
        try {
            getSite(session, url);
            return true;
        } catch (SiteManagerException e) {
            return false;
        }
    }

    @Override
    public List<LabsSite> getAllSites(CoreSession session) throws ClientException {
        String query = String.format("SELECT * FROM %s WHERE ecm:path STARTSWITH '%s'", LabsSiteConstants.Docs.SITE.type(), SITES_ROOT_REF.toString());
        DocumentModelList docs = session.query(query);
        List<LabsSite> sites = new ArrayList<LabsSite>();
        for(DocumentModel doc : docs) {
            LabsSite site = Tools.getAdapter(LabsSite.class, doc, session);
            if(site!= null) {
                sites.add(site);
            }
        }
        return sites;
    }

    @Override
    public void updateSite(CoreSession session, LabsSite site) throws ClientException, SiteManagerException {
        //Check if url change gets a collision on another site
        if(hasChangedUrl(session,site) && siteExists(session, site.getURL())) {
            throw new SiteManagerException("label.labssites.edit.error.url.exist");
        }
        session.saveDocument(site.getDocument());
    }


    private boolean hasChangedUrl(CoreSession session, LabsSite site) throws ClientException {
        DocumentModel originalDoc = session.getDocument(new IdRef(site.getDocument().getId()));
        LabsSite origSite = Tools.getAdapter(LabsSite.class, originalDoc, session);
        return !origSite.getURL().equals(site.getURL());
    }

}
