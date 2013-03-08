/**
 *
 */
package com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects.webadapter;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.LifeCycleConstants;
import org.nuxeo.ecm.core.rest.DocumentObject;
import org.nuxeo.ecm.webengine.model.WebAdapter;
import org.nuxeo.ecm.webengine.model.impl.DefaultAdapter;

import com.leroymerlin.corp.fr.nuxeo.labs.site.SiteDocument;
import com.leroymerlin.corp.fr.nuxeo.labs.site.exception.NoDraftException;
import com.leroymerlin.corp.fr.nuxeo.labs.site.exception.NoPublishException;
import com.leroymerlin.corp.fr.nuxeo.labs.site.publisher.LabsPublisher;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Docs;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteUtils;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteWebAppUtils;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.Tools;

/**
 * @author fvandaele
 *
 */
@WebAdapter(name = "labspublish", type = "LabsPublish")
@Produces("text/html; charset=UTF-8")
public class LabsPublishService extends DefaultAdapter {
    
    private static final String NOT_BE_EMPTY = "notBeEmpty";
    private static final String IMPOSSIBLE_TO_BE_EMPTY_TRASH = "Impossible to be empty trash !";
    private static final String BE_EMPTY = "beEmpty";
    private static final String IMPOSSIBLE_TO_DRAFT = "Impossible to draft!";
    public static final String IMPOSSIBLE_TO_PUBLISH = "Impossible to publish!";
    private static final String PUBLISH = "publish";
    private static final String DRAFT = "draft";
    private static final String DELETE = "delete";
    private static final String UNDELETE = "undelete";
    public static final String NOT_DRAFT = "not drafted";
    public static final String NOT_PUBLISHED = "not published";

    private static final Log log = LogFactory.getLog(LabsPublishService.class);
    private static final Object IMPOSSIBLE_TO_DELETE = "Impossible to delete!";
    private static final Object NOT_DELETED = "not deleted";


    
    @PUT
    @Path("publish")
    public Object doPublish() {
        DocumentModel document = getDocument();
        try {
            LabsSiteWebAppUtils.publish(document, ctx.getCoreSession());
            if (LabsSiteConstants.Docs.SITE.type().equals(document.getType())){
            	LabsSiteUtils.eventFirePublisedSite(document, ctx.getCoreSession());
            }
            return Response.ok(PUBLISH).build();
        } catch (NoPublishException e) {
            log.error(IMPOSSIBLE_TO_PUBLISH, e);
            return Response.ok(NOT_PUBLISHED).build();
        }
    }
    
    @PUT
    @Path("draft")
    public Object doDraft() {
        DocumentModel document = getDocument();
        try {
            LabsSiteWebAppUtils.draft(document);
            return Response.ok(DRAFT).build();
        } catch (NoDraftException e) {
            log.error(IMPOSSIBLE_TO_DRAFT, e);
            return Response.ok(NOT_DRAFT).build();
        }
    }
    
    @PUT
    @Path("delete")
    public Object doDelete() {
        DocumentModel document = getDocument();
        try {
            if (!LabsSiteConstants.State.DELETE.getState().equals(document.getCurrentLifeCycleState())){
                LabsPublisher publisherAdapter = Tools.getAdapter(LabsPublisher.class, document, null);
                publisherAdapter.delete();
                return Response.ok(DELETE).build();
            }
        } catch (ClientException e) {
            log.error(IMPOSSIBLE_TO_DELETE, e);
        }
        return Response.ok(NOT_DELETED).build();
    }
    
    @PUT
    @Path("undelete")
    public Object doUndelete() {
        DocumentModel document = getDocument();
        try {
            if (undelete(document.getId())) {
                return Response.ok(UNDELETE).build();
            }
        } catch (ClientException e) {
            log.error(IMPOSSIBLE_TO_DELETE, e);
        }
        return Response.ok(NOT_DELETED).build();
    }
    
    @Deprecated
    @PUT
    @Path("undelete/{ref}")
    public Object doUndeleteRef(@PathParam("ref") final String ref) {
        try {
            if (undelete(ref)) {
                return Response.ok(UNDELETE).build();
            }
        } catch (ClientException e) {
            log.error(IMPOSSIBLE_TO_DELETE, e);
        }
        return Response.ok(NOT_DELETED).build();
    }
    
    @DELETE
    @Path("emptyTrash")
    public Object doEmptyTrash() {
        DocumentModel document = getDocument();
        CoreSession session = ctx.getCoreSession();
        try {
            DocumentModelList docs = Tools.getAdapter(SiteDocument.class, document, session).getSite().getAllDeletedDocs();
            boolean deleted = false;
            for (DocumentModel deletedDoc : docs) {
                session.removeDocument(deletedDoc.getRef());
                deleted = true;
            }
            if (deleted) {
                session.save();
            }
            return Response.ok(BE_EMPTY).build();
        } catch (ClientException e) {
            log.error(IMPOSSIBLE_TO_BE_EMPTY_TRASH, e);
        }
        return Response.ok(NOT_BE_EMPTY).build();
    }

    @DELETE
    @Path("bulkRemove")
    public Response doBulkRemove(@QueryParam("id") List<String> ids) {
        try {
            boolean removed = false;
            for (String id : ids) {
                IdRef idRef = new IdRef(id);
                if (ctx.getCoreSession().exists(idRef)) {
                    ctx.getCoreSession().removeDocument(idRef);
                    removed = true;
                }
            }
            if (removed) {
                ctx.getCoreSession().save();
            }
        } catch (ClientException e) {
            log.error(e.getMessage());
            return Response.serverError().status(Status.NOT_MODIFIED).entity(
                    e.getMessage()).build();
        }
        return Response.status(Status.NO_CONTENT).build();
    }

    @PUT
    @Path("bulkUndelete")
    public Response doBulkUndelete(@QueryParam("id") List<String> ids) {
        try {
            boolean removed = false;
            for (String id : ids) {
                if (undelete(id)) {
                    removed = true;
                }
            }
            if (removed) {
                ctx.getCoreSession().save();
            }
        } catch (ClientException e) {
            log.error(e.getMessage());
            return Response.serverError().status(Status.NOT_MODIFIED).entity(
                    e.getMessage()).build();
        }
        return Response.status(Status.NO_CONTENT).build();
    }
    
    /**
     * @return
     */
    private DocumentModel getDocument() {
        DocumentObject dobj = (DocumentObject) getTarget();
        return dobj.getDocument();
    }
    
    private boolean undelete(String id) throws ClientException {
        IdRef idRef = new IdRef(id);
        boolean undeleted = false;
        if (ctx.getCoreSession().exists(idRef)) {
            DocumentModel document = getContext().getCoreSession().getDocument(idRef);
            if (cascadeUndelete(document)) {
                undeleted = undeleteDoc(document);
            }
        }
        return undeleted;
    }
    
    private boolean undeleteDoc(DocumentModel document) throws ClientException {
        boolean undeleted = false;
        if ("default".equals(document.getLifeCyclePolicy())) {
            if (LifeCycleConstants.DELETED_STATE.equals(document.getCurrentLifeCycleState())) {
                document.followTransition(LifeCycleConstants.UNDELETE_TRANSITION);
                undeleted = true;
            }
        } else {
            if (LabsSiteConstants.State.DELETE.getState().equals(document.getCurrentLifeCycleState())){
                LabsPublisher publisherAdapter = Tools.getAdapter(LabsPublisher.class, document, null);
                publisherAdapter.undelete();
                undeleted = true;
            }
        }
        return undeleted;
    }

    private boolean cascadeUndelete(DocumentModel document) throws ClientException {
        boolean undeleted = true;
        CoreSession session = ctx.getCoreSession();
        DocumentModel parentDoc = session.getDocument(document.getParentRef());
        if (Docs.PAGECLASSEUR.type().equals(parentDoc.getType())
                && Docs.PAGECLASSEURFOLDER.type().equals(document.getType())) {
            // cascade down undelete
            undeleted = false;
            DocumentModelList files = session.getFiles(document.getRef());
            for (DocumentModel file : files) {
                undeleteDoc(file);
                undeleted = true;
            }
        } else if (Docs.PAGECLASSEURFOLDER.type().equals(parentDoc.getType())
                && LifeCycleConstants.DELETED_STATE.equals(parentDoc.getCurrentLifeCycleState())) {
            DocumentModel grandParentDoc = session.getDocument(parentDoc.getParentRef());
            if (Docs.PAGECLASSEUR.type().equals(grandParentDoc.getType())) {
                // cascade up undelete
                undeleted = false;
                if (undeleteDoc(parentDoc)) {
                    undeleted = true;
                }
            }
        }
        return undeleted;
    }

}
