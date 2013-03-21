/**
 *
 */
package com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects.webadapter.assets;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.picture.api.adapters.PictureResourceAdapter;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.Template;
import org.nuxeo.ecm.webengine.model.WebAdapter;
import org.nuxeo.ecm.webengine.model.WebContext;

import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.tree.assets.AssetsDocumentTree;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.CommonHelper;
import com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects.webadapter.CkEditorParametersAdapter;

@WebAdapter(name = "assets", type = "assetsAdapter", targetType = "LabsSite")
public class AssetsAdapter extends CkEditorParametersAdapter {

    public AssetsAdapter() {
        WebContext ctx = WebEngine.getActiveContext();
        // callerRef
        String parameter = ctx.getRequest()
                .getParameter(PARAM_NAME_CKEDITOR_CALLBACK);
        if (StringUtils.isBlank(parameter)) {
            parameter = ctx.getRequest()
                    .getParameter(PARAM_NAME_CALLED_REFERENCE);
        }
        if (StringUtils.isNotBlank(parameter)) {
            ctx.getRequest()
                    .getSession()
                    .setAttribute(PARAM_NAME_CALLED_REFERENCE, parameter);
        }

        // jscallback
        parameter = ctx.getRequest()
                .getParameter(PARAM_NAME_CALLBACK);
        if (StringUtils.isBlank(parameter)) {
            parameter = PARAM_VALUE_CKEDITOR_CALLBACK;
        }
        ctx.getRequest()
                .getSession()
                .setAttribute(PARAM_NAME_CALLBACK, parameter);
    }

    @GET
    public Template doGet() throws ClientException {
        AssetFolderResource resource = getAssetResource(getSite());
        return resource.getView("index");
    }

    @POST
    public Response doPost() throws ClientException {
        AssetFolderResource resource = getAssetResource(getSite());
        return resource.doPost();
    }

    private AssetFolderResource getAssetResource(LabsSite site)
            throws ClientException {
        return (AssetFolderResource) ctx.newObject("AssetFolder",
                site.getAssetsDoc());
    }

    @Path("{path}")
    public Object doTraverse(@PathParam("path") String path)
            throws ClientException {
        String isCommon = ctx.getRequest().getParameter("isCommon");
        AssetFolderResource res = null;
        if ("true".equals(isCommon)) {
            res = (AssetFolderResource) ctx.newObject("AssetFolder",
                    getSiteRootAssetsDoc());
        } else {
            res = getAssetResource(getSite());
        }
        return res.traverse(path);
    }

    @Path("id/{id}")
    public Object doTraverseWithId(@PathParam("id") String id)
            throws ClientException {
        return doTraverseAsset(id);
    }

    @Path("paramId")
    public Object doTraverseWithParamId() throws ClientException {
        return doTraverseAsset(ctx.getForm()
                .getString("id"));
    }

    private Object doTraverseAsset(final String id) throws ClientException {
        AssetFolderResource res = getAssetResource(getSite());
        String path = getContext().getCoreSession()
                .getDocument(new IdRef(id))
                .getPath()
                .toString();

        if (path.endsWith("assets")) {
            path = "";
        } else {
            path = path.substring(path.indexOf("assets/") + "assets/".length(),
                    path.length());
        }

        return res.traverse(path);
    }

    @GET
    @Path("json")
    public Response doGetJson(@QueryParam("root") String root,
            @QueryParam("isCommon") String isCommon) throws ClientException {
        LabsSite site = getSite();
        if (site != null) {
            AssetsDocumentTree tree;
            if (!StringUtils.isEmpty(isCommon) && isCommon.equals("true")) {
                tree = new AssetsDocumentTree(ctx, getSiteRootAssetsDoc(), true);
            } else {
                tree = new AssetsDocumentTree(ctx, site.getAssetsDoc(), false);
            }
            String result = "";
            if (root == null || "source".equals(root)) {
                tree.enter(ctx, "");
                result = tree.getTreeAsJSONArray(ctx);
            } else {
                result = tree.enter(ctx, root);
            }
            return Response.ok()
                    .entity(result)
                    .build();
        }
        return null;
    }

    private DocumentModel getSiteRootAssetsDoc() throws ClientException {
        return ctx.getCoreSession()
                .getDocument(CommonHelper.getRefSiteRootAssetsDoc());
    }

    @GET
    @Path("/@views/content")
    public Template doGetRootContent() throws ClientException {
        AssetFolderResource folder = null;
        String isCommon = ctx.getRequest().getParameter("isCommon");
        if ("true".equals(isCommon)){
            folder = (AssetFolderResource) ctx.newObject("AssetFolder",getSiteRootAssetsDoc());
        }
        else{
            folder = getAssetResource(getSite());
        }
        return folder.getView("content_root").arg("isCommon", isCommon);
    }

    private LabsSite getSite() {
        return (LabsSite) ctx.getProperty("site");
    }

    @POST
    @Path("rotCW/{id}")
    public Response rotateCW(@PathParam("id") String id) throws ClientException {
        return doRotate(90, id);

    }

    @POST
    @Path("@rotCCW/{id}")
    public Response rotateCCW(@PathParam("id") String id) throws ClientException {
        return doRotate(-90, id);
    }

    private Response doRotate(int deg, String id) throws ClientException {
        CoreSession session = getContext().getCoreSession();
        DocumentModel doc = getContext().getCoreSession().getDocument(new IdRef(id));
        PictureResourceAdapter picture = doc.getAdapter(PictureResourceAdapter.class);

        picture.doRotate(deg);

        session.saveDocument(doc);
        session.save();
        return Response.ok("Picture has been rotated", MediaType.TEXT_PLAIN).build();

    }

}
