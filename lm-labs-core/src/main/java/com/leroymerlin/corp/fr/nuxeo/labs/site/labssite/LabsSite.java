/**
 *
 */
package com.leroymerlin.corp.fr.nuxeo.labs.site.labssite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

import com.leroymerlin.corp.fr.nuxeo.labs.base.LabsBase;
import com.leroymerlin.corp.fr.nuxeo.labs.site.Page;
import com.leroymerlin.corp.fr.nuxeo.labs.site.blocs.ExternalURL;
import com.leroymerlin.corp.fr.nuxeo.labs.site.html.HtmlPage;
import com.leroymerlin.corp.fr.nuxeo.labs.site.news.LabsNews;
import com.leroymerlin.corp.fr.nuxeo.labs.site.theme.SiteThemeManager;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Docs;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.State;

/**
 * @author fvandaele
 * 
 */
public interface LabsSite extends LabsBase {

    /**
     * The last url part to get this site. This MUST be unique amongst all sites
     * 
     * @return
     * @throws ClientException
     */
    String getURL() throws ClientException;

    /**
     * Set the url of the site. It SHOULD throw an exception if the URL is not
     * unique
     * 
     * @param pURL
     * @throws ClientException
     */
    void setURL(String pURL) throws ClientException;

    /**
     * Returns a Blob containing the banner of the site
     * 
     * @return
     * @throws ClientException
     */
    Blob getBanner() throws ClientException;

    /**
     * @param pBlob
     * @throws ClientException
     */
    void setBanner(Blob pBlob) throws ClientException;

    DocumentModel getIndexDocument() throws ClientException;

    /**
     * Returns all the page of the site. Pages are document that are renderable
     * with a web view
     * 
     * @return
     * @throws ClientException
     */
    List<Page> getAllPages() throws ClientException;

    /**
     * Returns the templatified pages
     * 
     * @return
     * @throws ClientException
     */
    List<Page> getAllPagesTemplate() throws ClientException;

    /**
     * @param docType
     * @param llifecycleState
     *            can be <code>null</code>.
     * @return
     * @throws ClientException
     */
    Collection<DocumentModel> getPages(Docs docType, State lifecycleState)
            throws ClientException;

    /**
     * Returns the base document of the tree
     * 
     * @return
     * @throws ClientException
     */
    DocumentModel getTree() throws ClientException;

    /**
     * Return the theme manager for this site
     * 
     * @return
     * @throws ClientException
     */
    SiteThemeManager getThemeManager() throws ClientException;

    /**
     * Returns the base document for assets management
     * 
     * @return
     */
    DocumentModel getAssetsDoc() throws ClientException;

    DocumentModel getAssetsPublicDoc(LabsSiteConstants.Docs asset) throws ClientException;

    DocumentModel getAssetsPublicDoc(LabsSiteConstants.Docs assetasset, String name) throws ClientException;

    List<Page> getAllDeletedPages() throws ClientException;

    DocumentModelList getAllDeletedDocs() throws ClientException;

    void setHomePageRef(String homePageRef) throws ClientException;

    String getHomePageRef() throws ClientException;

    DocumentModelList getLastUpdatedDocs() throws ClientException;

    DocumentModelList getLastUpdatedNewsDocs() throws ClientException;

    DocumentModelList getLastPublishedNewsDocs(CoreSession session)
            throws ClientException;

    ArrayList<ExternalURL> getExternalURLs() throws ClientException;

    ExternalURL createExternalURL(String title) throws ClientException;

    boolean updateUrls(String oldUrl, String newUrl) throws ClientException;

    List<String> getAdministratorsSite() throws Exception;

    List<String> getContacts() throws Exception;

    boolean addContact(String ldap) throws Exception;

    boolean deleteContact(String ldap) throws Exception;

    String getPiwikId() throws ClientException;

    void setPiwikId(String piwikId) throws ClientException;

    boolean isPiwikEnabled() throws ClientException;

    void applyTemplateSite(final DocumentModel templateSite)
            throws ClientException, IllegalArgumentException;

    void setThemeName(String name) throws ClientException;

    String getThemeName() throws ClientException;

    List<Page> getSubscribedPages() throws ClientException;

    void setCategory(String category) throws ClientException;

    String getCategory() throws ClientException;

    HtmlPage getSidebar() throws ClientException;

    boolean isTopPageNavigation() throws ClientException;

    void setTopPageNavigation(boolean isTopPageNavigation)
            throws ClientException;

    List<LabsNews> getAllLabsNewsTemplate() throws ClientException;
}
