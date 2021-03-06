package com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.BooleanUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.rest.DocumentObject;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "ExternalUrl")
public class ExternalUrlResource extends DocumentObject {

    @Override
    public Response doPost() {
        Response response = super.doPost();
        String redirect = ctx.getForm().getString("redirect");
        if (BooleanUtils.toBoolean(redirect)) {
            return response;
        }
        return Response.status(Status.OK).build();
    }

    @GET
    @Path(value = "@moveUpExternalURL/{beforeId}")
    public Object moveUpExternalURL(@PathParam("beforeId") final String beforeId) {
        CoreSession session = ctx.getCoreSession();
        try {
            DocumentModel beforeDoc = session.getDocument(new IdRef(beforeId));
            return moveExternalURL(doc.getName(), beforeDoc.getName());
        } catch (ClientException e) {
            return Response.status(Status.GONE).build();
        }
    }

    @GET
    @Path(value = "@moveDownExternalURL/{afterId}")
    public Object moveDownExternalURL(@PathParam("afterId") final String afterId) {
        CoreSession session = ctx.getCoreSession();
        try {
            DocumentModel afterDoc = session.getDocument(new IdRef(afterId));
            return moveExternalURL(afterDoc.getName(), doc.getName());
        } catch (ClientException e) {
            return Response.status(Status.GONE).build();
        }
    }
    
    private Response moveExternalURL(String beforeName, String afterName) throws ClientException{
        CoreSession session = ctx.getCoreSession();
        session.orderBefore(doc.getParentRef(), beforeName, afterName);
        session.save();
        return Response.status(Status.OK).build();
    }

    @Path("ajax")
    @DELETE
    public Response doDeleteAjax() {
        super.doDelete();
        return Response.noContent().build();
    }

}
