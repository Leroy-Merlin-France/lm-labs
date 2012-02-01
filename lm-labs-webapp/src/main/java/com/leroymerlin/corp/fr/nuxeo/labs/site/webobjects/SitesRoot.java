package com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.rest.DocumentObject;
import org.nuxeo.ecm.core.storage.sql.coremodel.SQLBlob;
import org.nuxeo.ecm.platform.rendering.api.RenderingEngine;
import org.nuxeo.ecm.webengine.WebException;
import org.nuxeo.ecm.webengine.forms.FormData;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.exceptions.WebResourceNotFoundException;
import org.nuxeo.ecm.webengine.model.exceptions.WebSecurityException;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.runtime.api.Framework;

import com.leroymerlin.common.freemarker.BytesFormatTemplateMethod;
import com.leroymerlin.common.freemarker.DateInWordsMethod;
import com.leroymerlin.common.freemarker.UserFullNameTemplateMethod;
import com.leroymerlin.corp.fr.nuxeo.freemarker.LatestUploadsPageProviderTemplateMethod;
import com.leroymerlin.corp.fr.nuxeo.labs.site.SiteDocument;
import com.leroymerlin.corp.fr.nuxeo.labs.site.SiteManager;
import com.leroymerlin.corp.fr.nuxeo.labs.site.exception.NotAuthorizedException;
import com.leroymerlin.corp.fr.nuxeo.labs.site.exception.SiteManagerException;
import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.CommonHelper;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Docs;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteUtils;

import edu.emory.mathcs.backport.java.util.Collections;

@WebObject(type = "sitesRoot")
@Produces("text/html; charset=UTF-8")
@Path("/labssites")
public class SitesRoot extends ModuleRoot {

    private final class ComparatorLabsSite implements Comparator<LabsSite> {
        @Override
        public int compare(LabsSite lb1, LabsSite lb2) {
            try {
                return lb1.getTitle().compareTo(lb2.getTitle());
            } catch (ClientException e) {
                return 0;
            }
        }
    }

    private static final Log log = LogFactory.getLog(SitesRoot.class);

    private static final String[] MESSAGES_TYPE = new String[] { "error",
            "info", "success", "warning" };

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nuxeo.ecm.webengine.model.impl.AbstractResource#initialize(java.lang
     * .Object[])
     */
    @Override
    protected void initialize(Object... args) {
        super.initialize(args);
        // Add global fm variables
        // WARNING : these are GLOBAL vars, try to avoid using this trick (DMR)
        RenderingEngine rendering = getContext().getEngine().getRendering();
        rendering.setSharedVariable("bytesFormat",
                new BytesFormatTemplateMethod());
        rendering.setSharedVariable("latestUploadsPageProvider",
                new LatestUploadsPageProviderTemplateMethod());
        rendering.setSharedVariable("userFullName",
                new UserFullNameTemplateMethod());
        rendering.setSharedVariable("dateInWords", new DateInWordsMethod());
        rendering.setSharedVariable("site", null);
        rendering.setSharedVariable("Common", new CommonHelper());
        rendering.setSharedVariable(
                "serverTimeout",
                String.valueOf(getContext().getRequest().getSession().getMaxInactiveInterval()));
    }

    public List<LabsSite> getUndeletedLabsSites() {
        try {
            return getLabsSitesUndeleted(getLabsSites());
        } catch (ClientException e) {
            log.error("Impossible to get the sites!", e);
            return new ArrayList<LabsSite>();
        }
    }

    public List<LabsSite> getDeletedLabsSites() {
        try {
            return getLabsSitesDeleted(getLabsSites());
        } catch (ClientException e) {
            log.error("Impossible to get the sites!", e);
            return new ArrayList<LabsSite>();
        }
    }

    public List<LabsSite> getTemplateLabsSites() {
        try {
            return getTemplateLabsSites(getLabsSites());
        } catch (ClientException e) {
            log.error("Impossible to get the template sites!", e);
            return new ArrayList<LabsSite>();
        }
    }

    private List<LabsSite> getTemplateLabsSites(List<LabsSite> labsSites)
            throws ClientException {
        List<LabsSite> result = new ArrayList<LabsSite>();
        for (LabsSite labsSite : labsSites) {
            if (!labsSite.isDeleted() && labsSite.isSiteTemplate()) {
                result.add(labsSite);
            }
        }
        Collections.sort(result, new ComparatorLabsSite());
        return result;
    }

    private List<LabsSite> getLabsSitesUndeleted(List<LabsSite> pOrigin)
            throws ClientException {
        List<LabsSite> result = new ArrayList<LabsSite>();
        for (LabsSite labsSite : pOrigin) {
            if (!labsSite.isDeleted() && !labsSite.isSiteTemplate()) {
                result.add(labsSite);
            }
        }
        Collections.sort(result, new ComparatorLabsSite());
        return result;
    }

    private List<LabsSite> getLabsSitesDeleted(List<LabsSite> pOrigin)
            throws ClientException {
        List<LabsSite> result = new ArrayList<LabsSite>();
        for (LabsSite labsSite : pOrigin) {
            if (labsSite.isDeleted()) {
                result.add(labsSite);
            }
        }
        Collections.sort(result, new ComparatorLabsSite());
        return result;
    }

    @GET
    public Object doGetDefaultView() {
        return getView("index");
    }

    @Path("{url}")
    public Object doGetSite(@PathParam("url") final String pURL) {
        CoreSession session = getContext().getCoreSession();
        SiteManager sm = getSiteManager();
        try {
            LabsSite site = sm.getSite(session, pURL);
            return newObject("LabsSite", site.getDocument());
        } catch (ClientException e) {
            throw WebException.wrap(e);
        } catch (SiteManagerException e) {
            throw new NotAuthorizedException(e.getMessage(), e);
        }
    }
    
    @GET
    @Path("@templatePreview/{url}")
    public Object doGetTemplatePreview(@Context Request request, @PathParam("url") final String url) {
    	// comme l'acces au webobject d'un site de type modèle est impossible
    	// pour un utilisateur non-administrateur, j'ai mis en place ce petit hack
    	// afin d'obtenir l'image de preview.
        try {
        	CoreSession session = getContext().getCoreSession();
        	SiteManager sm = getSiteManager();
            LabsSite site = sm.getSite(session, url);
            BlobHolder blobHolder = site.getDocument().getAdapter(BlobHolder.class);
            Blob blob = blobHolder.getBlob();
            EntityTag etag = null;
            String digest = ((SQLBlob) blob).getBinary().getDigest();
            if (digest != null) {
            	etag = new EntityTag(digest);
            }
            Response.ResponseBuilder rb = request.evaluatePreconditions(etag);
            if (rb != null) {
                return rb.build();
            }
            String mimeType = blob.getMimeType() == null ? "image/jpeg"
                    : blob.getMimeType();
            String filename = blob.getFilename() == null ? "file"
                    : blob.getFilename();
            rb = Response.ok(blob).header("Content-Disposition",
            	"inline;filename=\"" + filename + "\"").type(mimeType);
            if (etag != null) {
                rb.tag(etag);
            }
            return rb.build();
        } catch (ClientException e) {
            throw WebException.wrap(e);
        } catch (SiteManagerException e) {
            throw new WebResourceNotFoundException(e.getMessage(), e);
        }
    }

    /**
     * Needed by OpenSocial gadgets.
     * 
     * @param id
     * @return
     * @throws URIException
     * @throws URISyntaxException
     */
    @Path("id/{id}")
    public Object doGetPageById(@PathParam("id") String id) {
        CoreSession session = getContext().getCoreSession();
        DocumentModel document;
        try {
            document = session.getDocument(new IdRef(id));
            if (Docs.SITE.type().equals(document.getType())) {
                return (DocumentObject) ctx.newObject("LabsSite", document);
            } else {
                return (DocumentObject) ctx.newObject(document.getType(),
                        document);
            }
        } catch (ClientException e) {
            throw new WebResourceNotFoundException(e.getMessage(), e);
        }
    }

    private List<LabsSite> getLabsSites() throws ClientException {
        CoreSession coreSession = ctx.getCoreSession();
        List<LabsSite> newAllSites = new ArrayList<LabsSite>();
        for (LabsSite site : getSiteManager().getAllSites(coreSession)) {
            if (!(LabsSiteUtils.isOnlyRead(site.getDocument()) && !site.isVisible())) {
                newAllSites.add(site);
            }
        }
        return newAllSites;
    }

    @POST
    public Response doPost() {
        FormData form = ctx.getForm();
        String piwikId = form.getString("piwik:piwikId");
        String pDescription = form.getString("dc:description");
        String pURL = form.getString("webc:url");
        String pTitle = form.getString("dc:title");
        String templateId = form.getString("siteTemplateId");
        CoreSession session = ctx.getCoreSession();
        try {
            SiteManager sm = getSiteManager();
            LabsSite labSite = sm.createSite(session, pTitle, pURL);
            labSite.setPiwikId(StringUtils.trim(piwikId));
            labSite.setDescription(pDescription);
            String siteTemplateStr = form.getString("labssite:siteTemplate");
            boolean isSiteTemplate = BooleanUtils.toBoolean(siteTemplateStr);
            labSite.setSiteTemplate(isSiteTemplate);
            if (isSiteTemplate) {
                if (form.isMultipartContent()) {
                    Blob preview = form.getBlob("labssite:siteTemplatePreview");
                    if (preview != null
                            && !StringUtils.isEmpty(preview.getFilename())) {
                        labSite.setSiteTemplatePreview(preview);
                    }
                }
            }
            session.saveDocument(labSite.getDocument());
            boolean templateToCopy = false;
            boolean copied = false;
            if (StringUtils.isNotEmpty(StringUtils.trim(templateId))) {
                IdRef templateRef = new IdRef(templateId);
                if (session.exists(templateRef)) {
                    templateToCopy = true;
                    try {
                        labSite.applyTemplateSite(session.getDocument(templateRef));
                        session.saveDocument(labSite.getDocument());
                        copied = true;
                    } catch (ClientException e) {
                        log.error("Copy of site template failed. "
                                + e.getMessage());
                    } catch (IllegalArgumentException e) {
                        log.error("Copy of site template failed. "
                                + e.getMessage());
                    }
                }
            }
            session.save();
            if (templateToCopy && !copied) {
                return redirect(getPath()
                        + "?message_error=label.labssites.edit.error.template.copy.failed");
            }
            return redirect(getPath() + "/" + labSite.getURL());
        } catch (SiteManagerException e) {
            return redirect(getPath() + "?message_error=" + e.getMessage());
        } catch (ClientException e) {
            throw WebException.wrap(e);
        }

    }

    private SiteManager getSiteManager() {
        try {
            return Framework.getService(SiteManager.class);
        } catch (Exception e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nuxeo.ecm.webengine.model.impl.ModuleRoot#handleError(javax.ws.rs
     * .WebApplicationException)
     */
    @Override
    public Object handleError(WebApplicationException e) {
        if (e instanceof WebResourceNotFoundException) {
            String fileName = "error/error_404.ftl";
            log.debug(fileName);
            return Response.status(404).entity(getTemplate(fileName)).build();
        } else if (e instanceof WebSecurityException) {
            return Response.status(401).entity(
                    getTemplate("error/error_401.ftl")).type("text/html").build();
        } else if (e instanceof NotAuthorizedException) {
            return Response.status(202).entity(
                    getTemplate("error/error_202.ftl")).type("text/html").build();
        } else {

            return Response.status(500).entity(
                    getTemplate("error/labserror_500.ftl").arg("trace",
                            getStackTrace(e))).type("text/html").build();
        }
    }

    private static String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }

    /**
     * Returns a Map containing all "flash" messages
     * 
     * @return
     */
    public Map<String, String> getMessages() {
        Map<String, String> messages = new HashMap<String, String>();
        FormData form = ctx.getForm();
        for (String type : MESSAGES_TYPE) {
            String message = form.getString("message_" + type);
            if (StringUtils.isNotBlank(message)) {
                messages.put(type, message);
            }
        }
        return messages;

    }

    @Override
    public String getLink(DocumentModel document) {
        SiteDocument siteDocument = document.getAdapter(SiteDocument.class);
        try {
            return new StringBuilder().append(getPath()).append("/").append(
                    siteDocument.getResourcePath()).toString();
        } catch (ClientException e) {
            return getPath();
        }
    }

    public String getTruncatedLink(DocumentModel document) {
        SiteDocument siteDocument = document.getAdapter(SiteDocument.class);
        try {
            return siteDocument.getResourcePath();
        } catch (ClientException e) {
            return getPath();
        }
    }

}
