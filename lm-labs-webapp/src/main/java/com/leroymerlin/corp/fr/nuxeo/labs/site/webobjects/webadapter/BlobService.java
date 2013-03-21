package com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects.webadapter;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Calendar;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.convert.api.ConversionService;
import org.nuxeo.ecm.core.storage.sql.coremodel.SQLBlob;
import org.nuxeo.ecm.platform.picture.api.adapters.PictureBlobHolder;
import org.nuxeo.ecm.platform.preview.adapter.PreviewAdapterManager;
import org.nuxeo.ecm.platform.preview.api.HtmlPreviewAdapter;
import org.nuxeo.ecm.webengine.model.WebAdapter;
import org.nuxeo.ecm.webengine.model.impl.DefaultAdapter;
import org.nuxeo.runtime.api.Framework;

import com.leroymerlin.corp.fr.nuxeo.labs.base.LabsElementTemplateBlobHolder;

@WebAdapter(name = "blob", type = "BlobService")
public class BlobService extends DefaultAdapter {

    private enum ContentDisposition {
        attachement, inline;
    }

    private static final Log LOG = LogFactory.getLog(BlobService.class);

    @GET
    public Object doGet(@Context Request request) {
        DocumentModel doc = this.getTarget()
                .getAdapter(DocumentModel.class);
        return getBlob(doc, request, ContentDisposition.attachement);
    }

    @GET
    @Path("preview")
    public Object getPreview() {
        DocumentModel doc = this.getTarget()
                .getAdapter(DocumentModel.class);
        String previewURL = ctx.getBaseURL() + "/nuxeo/" + getPreviewURL(doc);
        return this.redirect(previewURL);
    }

    public String getPreviewURL(DocumentModel doc) throws IllegalStateException {
        PreviewAdapterManager pam = Framework.getLocalService(PreviewAdapterManager.class);
        boolean hasPreview = pam.hasAdapter(doc);
        if (hasPreview) {
            HtmlPreviewAdapter previewAdapter = pam.getAdapter(doc);
            return previewAdapter.getFilePreviewURL();
        }
        throw new IllegalStateException("No preview adapter available for "
                + doc.getPathAsString());

    }

    @GET
    @Path("html")
    @Deprecated
    public Object doGetView(@Context Request request) throws Exception {
        DocumentModel doc = this.getTarget()
                .getAdapter(DocumentModel.class);
        BlobHolder blobHolder = doc.getAdapter(BlobHolder.class);
        Blob blob = blobHolder.getBlob();
        ConversionService service = Framework.getService(ConversionService.class);
        String converterName = service.getConverterName(blob.getMimeType(),
                "text/html");
        BlobHolder convert = service.convert(converterName, blobHolder, null);
        return convert.getBlob()
                .getString();
    }
    
    @DELETE
    public Object doDelete() {
        DocumentModel documentModel = this.getTarget().getAdapter(DocumentModel.class);
		BlobHolder blobHolder = documentModel.getAdapter(BlobHolder.class);
        return delete(documentModel, blobHolder);
    }

	private Object delete(DocumentModel documentModel, BlobHolder blobHolder) {
		try {
			blobHolder.setBlob(null);
			getContext().getCoreSession().saveDocument(documentModel);
		} catch (ClientException e) {
			LOG.error(e, e);
			return Response.notModified().build();
		}
        return Response.noContent().build();
	}
    
    @DELETE
    @Path("labsNews")
    public Object doDeleteLabsNews() {
        DocumentModel documentModel = this.getTarget().getAdapter(DocumentModel.class);
        BlobHolder blobHolder = new LabsElementTemplateBlobHolder(documentModel, "let:preview");
        return delete(documentModel, blobHolder);
    }
    
    @GET
    @Path("labsNews")
    public Object doGetBlobLabsNews(@Context Request request) {
        DocumentModel doc = this.getTarget().getAdapter(DocumentModel.class);
        BlobHolder blobHolder = new LabsElementTemplateBlobHolder(doc, "let:preview");
        return getResponseBlob(doc, request, ContentDisposition.attachement, blobHolder);

    }

    protected Object getBlob(DocumentModel doc, Request request,
            ContentDisposition disposition) {
        	BlobHolder blobHolder = doc.getAdapter(BlobHolder.class);
            return getResponseBlob(doc, request, disposition, blobHolder);

    }

	private Object getResponseBlob(DocumentModel doc, Request request,
			ContentDisposition disposition, BlobHolder blobHolder) {
		try {
			Blob blob = null;
			if (blobHolder instanceof PictureBlobHolder){
				blob = ((PictureBlobHolder)blobHolder).getBlob("Original");
			}
			else{
				blob = blobHolder.getBlob();
			}
			//"originalJpeg"
			EntityTag etag = null;
			try {
			    String digest = ((SQLBlob) blob).getBinary()
			            .getDigest();
			    if (digest != null) {
			        etag = new EntityTag(digest);
			    }
	
			} catch (ClassCastException e) {
			    // Rare case where we are not in VCS
			    etag = computeEntityTag(doc);
			}
	
			Response.ResponseBuilder rb = request.evaluatePreconditions(etag);
			if (rb != null) {
			    return rb.build();
			}
			String mimeType = blob.getMimeType() == null ? "image/jpeg"
			        : blob.getMimeType();
			String filename = blob.getFilename() == null ? "file"
			        : blob.getFilename();
	
			rb = Response.ok(blob)
			        .header("Content-Disposition",
			                disposition.name() + ";filename=\"" + filename
			                        + "\"")
			        .type(mimeType);
	
			if (etag != null) {
			    rb.tag(etag);
			}
	
			return rb.build();
		} catch (NullPointerException e) {
            LOG.error(e, e);
        } catch (ClientException e) {
            LOG.error(e, e);
        }
        return Response.status(Status.NOT_FOUND)
                .build();
	}

    protected EntityTag computeEntityTag(DocumentModel doc)
            throws PropertyException, ClientException {
        return new EntityTag(
                computeDigest(((Calendar) doc.getPropertyValue("dc:modified")).toString()));
    }

    private String computeDigest(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            byte[] digest = md.digest(content.getBytes());
            BigInteger bi = new BigInteger(digest);
            return bi.toString(16);
        } catch (Exception e) {
            return "";
        }
    }

}
