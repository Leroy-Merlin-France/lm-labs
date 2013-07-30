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
import org.nuxeo.ecm.core.rest.DocumentObject;
import org.nuxeo.ecm.platform.picture.api.adapters.PictureResourceAdapter;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.forms.FormData;
import org.nuxeo.ecm.webengine.model.Template;
import org.nuxeo.ecm.webengine.model.WebAdapter;
import org.nuxeo.ecm.webengine.model.WebContext;

import com.leroymerlin.corp.fr.nuxeo.forum.LMForum;
import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.news.PageNews;
import com.leroymerlin.corp.fr.nuxeo.labs.site.tree.assets.AssetsDocumentTree;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.CommonHelper;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants;
import com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects.webadapter.CkEditorParametersAdapter;

@WebAdapter(name = "assets", type = "assetsAdapter", targetType = "LabsSite")
public class AssetsAdapter extends CkEditorParametersAdapter {

    public AssetsAdapter() {
        WebContext ctx = WebEngine.getActiveContext();
        // callerRef
        String parameter = ctx.getRequest().getParameter(
                PARAM_NAME_CKEDITOR_CALLBACK);
        if (StringUtils.isBlank(parameter)) {
            parameter = ctx.getRequest().getParameter(
                    PARAM_NAME_CALLED_REFERENCE);
        }
        if (StringUtils.isNotBlank(parameter)) {
            ctx.getRequest().getSession()
                    .setAttribute(PARAM_NAME_CALLED_REFERENCE, parameter);
        }

        // jscallback
        parameter = ctx.getRequest().getParameter(PARAM_NAME_CALLBACK);
        if (StringUtils.isBlank(parameter)) {
            parameter = PARAM_VALUE_CKEDITOR_CALLBACK;
        }
        ctx.getRequest().getSession()
                .setAttribute(PARAM_NAME_CALLBACK, parameter);
    }

    @GET
    public Template doGet() throws ClientException {
        Template template;

        DocumentObject dobj = (DocumentObject) getTarget();
        DocumentModel doc = dobj.getDocument();
        if (doc.getAdapter(LMForum.class) != null) {
            AssetFolderResource resource = getAssetResource(doc);
            template = resource.getView("index");
            template.arg("envType",
                    LabsSiteConstants.Docs.ASSETS_FORUMS.docName());
            template.arg("envName", doc.getName());
        } else if (doc.getAdapter(PageNews.class) != null) {
            AssetFolderResource resource = getAssetResource(doc);
            template = resource.getView("index");
            template.arg("envType",
                    LabsSiteConstants.Docs.ASSETS_NEWS.docName());
            template.arg("envName", doc.getName());
        } else {
            AssetFolderResource resource = getAssetResource(getSite());
            template = resource.getView("index");
        }

        template.arg(
                "writeAssetsSite",
                getContext().getCoreSession().hasPermission(
                        getAssetResource(getSite()).getDocument().getRef(),
                        "Write"));
        return template;
    }

    @POST
    public Response doPost() throws ClientException {

        FormData form = ctx.getForm();
        String envType = form.getString("envType");
        String envName = form.getString("envName");

        AssetFolderResource resource = null;
        LabsSite site = getSite();
        if (StringUtils.isEmpty(envType) || "Site".equals(envType)) {
            resource = getAssetResource(site);
        } else {
            if (envType.equals(LabsSiteConstants.Docs.ASSETS_FORUMS.docName())) {
                DocumentModel assetForum = site.getAssetsPublicDoc(
                        LabsSiteConstants.Docs.ASSETS_FORUMS, envName);
                resource = getAssetResource(assetForum);
            } else if (envType.equals(LabsSiteConstants.Docs.ASSETS_NEWS
                    .docName())) {
                DocumentModel asset = site.getAssetsPublicDoc(
                        LabsSiteConstants.Docs.ASSETS_NEWS, envName);
                resource = getAssetResource(asset);
            }
        }

        if (resource != null) {
            return resource.doPost();
        }

        return null;
    }

    protected AssetFolderResource getAssetResource(LabsSite site)
            throws ClientException {
        return (AssetFolderResource) ctx.newObject("AssetFolder",
                site.getAssetsDoc());
    }

    protected AssetFolderResource getAssetResource(DocumentModel doc)
            throws ClientException {
        return (AssetFolderResource) ctx.newObject("AssetFolder", doc);
    }

    protected AssetFolderResource getAssetLocalResource()
            throws ClientException {
        final DocumentObject dobj = (DocumentObject) getTarget();
        DocumentModel docAsset = ctx.getCoreSession().getChild(
                dobj.getDocument().getRef(), "assets");
        return (AssetFolderResource) ctx.newObject("AssetFolder", docAsset);
    }

    @Path("{path}")
    public Object doTraverse(@PathParam("path") String path)
            throws ClientException {
        String isCommon = ctx.getRequest().getParameter("isCommon");
        String envType = ctx.getForm().getString("envType");
        String envName = ctx.getForm().getString("envName");

        AssetFolderResource res = null;
        if (StringUtils.isEmpty(envType) || "Site".equals(envType)) {
            if ("true".equals(isCommon)) {
                res = (AssetFolderResource) ctx.newObject("AssetFolder",
                        getSiteRootAssetsDoc());
            } else {
                res = getAssetResource(getSite());
            }
        } else {
            LabsSite site = getSite();
            if (envType.equals(LabsSiteConstants.Docs.ASSETS_FORUMS.docName())) {
                res = getAssetResource(site.getAssetsPublicDoc(
                        LabsSiteConstants.Docs.ASSETS_FORUMS, envName));
            } else if (envType.equals(LabsSiteConstants.Docs.ASSETS_NEWS
                    .docName())) {
                res = getAssetResource(site.getAssetsPublicDoc(
                        LabsSiteConstants.Docs.ASSETS_NEWS, envName));
            }
        }
        if (res != null)
            return res.traverse(path);

        return null;
    }

    @Path("id/{id}")
    public Object doTraverseWithId(@PathParam("id") String id)
            throws ClientException {
        return doTraverseAsset(id);
    }

    @Path("paramId")
    public Object doTraverseWithParamId() throws ClientException {
        return doTraverseAsset(ctx.getForm().getString("id"));
    }

    private Object doTraverseAsset(final String id) throws ClientException {
        AssetFolderResource res = getAssetResource(getSite());
        String path = getContext().getCoreSession().getDocument(new IdRef(id))
                .getPath().toString();

        if (path.endsWith("assets")) {
            path = "";
        } else {
            path = path.substring(path.indexOf("assets/") + "assets/".length(),
                    path.length());
        }

        return res.traverse(path);
    }

    /**
     * 
     * @param root
     * @param isCommon
     * @param envType
     *            type d'assets (forum...)
     * @param envName
     *            nom de de l'assets (sous rep de envtype)
     * @return
     * @throws ClientException
     */
    @GET
    @Path("json")
    public Response doGetJson(@QueryParam("root") String root,
            @QueryParam("isCommon") String isCommon,
            @QueryParam("envType") String envType,
            @QueryParam("envName") String envName) throws ClientException {

        LabsSite site = getSite();

        if (site != null) {
            AssetsDocumentTree tree = null;
            if (StringUtils.isEmpty(envType)) {
                if (!StringUtils.isEmpty(isCommon) && isCommon.equals("true")) {
                    tree = new AssetsDocumentTree(ctx, getSiteRootAssetsDoc());
                } else {
                    tree = new AssetsDocumentTree(ctx, site.getAssetsDoc(),
                            "site");
                }
            } else {
                if (envType.equals(LabsSiteConstants.Docs.ASSETS_FORUMS
                        .docName())) {
                    DocumentModel assetForum = site.getAssetsPublicDoc(
                            LabsSiteConstants.Docs.ASSETS_FORUMS, envName);
                    tree = new AssetsDocumentTree(ctx, assetForum, envType);
                } else if (envType.equals(LabsSiteConstants.Docs.ASSETS_NEWS
                        .docName())) {
                    DocumentModel assetNews = site.getAssetsPublicDoc(
                            LabsSiteConstants.Docs.ASSETS_NEWS, envName);
                    tree = new AssetsDocumentTree(ctx, assetNews, envType);
                }
            }
            if (tree != null) {
                String result = "";
                if (root == null || "source".equals(root)) {
                    tree.enter(ctx, "");
                    result = tree.getTreeAsJSONArray(ctx);
                } else {
                    result = tree.enter(ctx, root);
                }
                return Response.ok().entity(result).build();
            }
        }
        return null;
    }

    protected DocumentModel getSiteRootAssetsDoc() throws ClientException {
        return ctx.getCoreSession().getDocument(
                CommonHelper.getRefSiteRootAssetsDoc());
    }

    @GET
    @Path("/@views/content")
    public Template doGetRootContent() throws ClientException {
        AssetFolderResource folder = null;
        String isCommon = ctx.getRequest().getParameter("isCommon");
        String envType = ctx.getRequest().getParameter("envType");
        String envName = ctx.getRequest().getParameter("envName");
        if (StringUtils.isEmpty(envType)) {
            if ("true".equals(isCommon)) {
                folder = (AssetFolderResource) ctx.newObject("AssetFolder",
                        getSiteRootAssetsDoc());
            } else {
                folder = getAssetResource(getSite());
            }
        } else {
            LabsSite site = getSite();
            if (envType.equals(LabsSiteConstants.Docs.ASSETS_FORUMS.docName())) {
                folder = getAssetResource(site.getAssetsPublicDoc(
                        LabsSiteConstants.Docs.ASSETS_FORUMS, envName));
            } else if (envType.equals(LabsSiteConstants.Docs.ASSETS_NEWS
                    .docName())) {
                folder = getAssetResource(site.getAssetsPublicDoc(
                        LabsSiteConstants.Docs.ASSETS_NEWS, envName));
            }
        }
        if (folder != null) {
            Template template = folder.getView("content_root");
            template.arg("isCommon", isCommon);
            template.arg("envType", envType);
            template.arg("envName", envName);
            return template;
        }
        return null;
    }

    protected LabsSite getSite() {
        return (LabsSite) ctx.getProperty("site");
    }

    @POST
    @Path("rotCW/{id}")
    public Response rotateCW(@PathParam("id") String id) throws ClientException {
        return doRotate(90, id);

    }

    @POST
    @Path("@rotCCW/{id}")
    public Response rotateCCW(@PathParam("id") String id)
            throws ClientException {
        return doRotate(-90, id);
    }

    private Response doRotate(int deg, String id) throws ClientException {
        CoreSession session = getContext().getCoreSession();
        DocumentModel doc = getContext().getCoreSession().getDocument(
                new IdRef(id));
        PictureResourceAdapter picture = doc
                .getAdapter(PictureResourceAdapter.class);

        picture.doRotate(deg);

        session.saveDocument(doc);
        session.save();
        return Response.ok("Picture has been rotated", MediaType.TEXT_PLAIN)
                .build();

    }

}
