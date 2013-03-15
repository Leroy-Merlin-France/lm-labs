/**
 *
 */
package com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects.webadapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.rest.DocumentObject;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.webengine.WebException;
import org.nuxeo.ecm.webengine.model.WebAdapter;
import org.nuxeo.ecm.webengine.model.impl.DefaultAdapter;
import org.nuxeo.runtime.api.Framework;

import com.leroymerlin.common.core.security.LMPermission;
import com.leroymerlin.common.core.security.PermissionsHelper;
import com.leroymerlin.common.core.security.SecurityData;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Rights;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteUtils;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteUtils.Action;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteWebAppUtils;
import com.leroymerlin.common.core.security.SecurityDataHelper;

/**
 * @author fvandaele
 * 
 */
@WebAdapter(name = "labspermissions", type = "LabsPermissions")
public class LabsPermissionsService extends DefaultAdapter {

    //private static final Log log = LogFactory.getLog(LabsPermissionsService.class);

    @GET
    public Object doGet() {
        List<LMPermission> permissions = extractPermissions(getDocument());
        List<LMPermission> permissionsAdmin = new ArrayList<LMPermission>();
        List<LMPermission> permissionsWrite = new ArrayList<LMPermission>();
        List<LMPermission> permissionsRead = new ArrayList<LMPermission>();
        for (LMPermission perm : permissions) {
            if (Rights.EVERYTHING.getRight().equals(perm.getPermission())) {
                permissionsAdmin.add(perm);
            } else if (Rights.WRITE.getRight().equals(perm.getPermission())) {
                permissionsWrite.add(perm);
            } else if (Rights.READ.getRight().equals(perm.getPermission())) {
                permissionsRead.add(perm);
            }
        }
        return getView("displayArray").arg("permissionsAdmin", permissionsAdmin).arg(
                "permissionsWrite", permissionsWrite).arg("permissionsRead",
                permissionsRead);
    }

    @GET
    @Path("permAdmin")
    public Object doGetPermissionsAdmin() {
        List<LMPermission> permissions = extractPermissions(getDocument());
        List<LMPermission> permissionsAdmin = new ArrayList<LMPermission>();
        Map<String, String> usernameAndEmail = new HashMap<String, String>(
                permissionsAdmin.size());
        UserManager userManager = null;
        try {
            userManager = Framework.getService(UserManager.class);
        } catch (Exception e) {
            throw WebException.wrap(e);
        }

        for (LMPermission perm : permissions) {
            if (Rights.EVERYTHING.getRight().equals(perm.getPermission())) {
                permissionsAdmin.add(perm);
                try {
                    NuxeoPrincipal principal = userManager.getPrincipal(perm.getName());
                    if (principal != null){
                    	usernameAndEmail.put(perm.getName(), principal.getEmail());
                    }
                    else{
                    	usernameAndEmail.put(perm.getName(), "");
                    }
                } catch (ClientException e) {
                    throw WebException.wrap(e);
                }
            }
        }

        return getView("admin_contact").arg("permissionsAdmin",
                permissionsAdmin).arg("usernameAndEmail", usernameAndEmail);
    }

    private List<LMPermission> extractPermissions(DocumentModel document) {
        try {
            return LabsSiteUtils.extractPermissions(document);
        } catch (Exception e) {
            throw WebException.wrap(e);
        }
    }

    /**
     * @return
     */
    private DocumentModel getDocument() {
        DocumentObject dobj = (DocumentObject) getTarget();
        DocumentModel document = dobj.getDocument();
        return document;
    }
    
    public boolean isPageForPermissions(){
        return !getDocument().hasSchema(LabsSiteConstants.Schemas.LABSSITE.getName());
    }

    @GET
    @Path("suggestedUsers/{string}")
    @Produces("plain/text")
    public Object getSuggestedUsers(@PathParam("string") String str) {
        final String pattern = StringEscapeUtils.unescapeJavaScript(str);
        Map<String, Object> params = LabsSiteWebAppUtils.getSuggestedUsers(pattern);
        return getView("selectUsers").args(params);
    }

    @GET
    @Path("haspermission")
    public Response hasPermission(
            @QueryParam(value = "permission") String permission,
            @QueryParam(value = "id") String id) {
        DocumentModel document = getDocument();
        try {
            return Response.ok(
                    Boolean.toString(PermissionsHelper.hasPermission(document,
                            permission, id))).build();
        } catch (Exception e) {
            throw WebException.wrap(e);
        }
    }

    @GET
    @Path("higherpermission")
    public Response hasHigherPermission(
            @QueryParam(value = "permission") String permission,
            @QueryParam(value = "id") String id) {
        DocumentModel document = getDocument();
        try {
        	List<String> labsRightsString = LabsSiteUtils.getListRights();
            PermissionsHelper helper = new PermissionsHelper(labsRightsString);
            return Response.ok(
                    Boolean.toString(helper.hasHigherPermission(document,
                            permission, id))).build();
        } catch (Exception e) {
            throw WebException.wrap(e);
        }
    }

    @POST
    @Path("add")
    public Response postPermission(
            @FormParam(value = "permission") String permission,
            @FormParam(value = "id") String id,
            @FormParam(value = "override") String overrideStr) {
        DocumentModel document = getDocument();
        try {
            boolean override = BooleanUtils.toBoolean(overrideStr);
            assert StringUtils.isNotEmpty(permission);
            assert StringUtils.isNotEmpty(id);
            LabsSiteUtils.setPermission(document, permission, id, Action.GRANT, override, ctx.getCoreSession());
            return Response.ok().build();

        } catch (IllegalStateException e1) {
            return Response.ok(ctx.getMessage(e1.getMessage()),
                    MediaType.TEXT_PLAIN).build();
        } catch (Exception e) {
            throw WebException.wrap(e);
        }
    }


    @DELETE
    @Path("delete")
    public Response deletePermission(
            @QueryParam(value = "permission") String permission,
            @QueryParam(value = "id") String id) throws IllegalStateException,
            Exception {
        DocumentModel document = getDocument();
        assert StringUtils.isNotEmpty(permission);
        assert StringUtils.isNotEmpty(id);
        LabsSiteUtils.setPermission(document, permission, id, Action.REMOVE, false, ctx.getCoreSession());
        return Response.ok().build();
    }

    @GET
    @Path("blockInherits")
    public Response blockInherits(@QueryParam(value = "permission") final String permission) throws IllegalStateException,
            Exception {
        DocumentModel document = getDocument();
        
        final List<LMPermission> permissions = extractPermissions(document);
        
        SecurityData sd = SecurityDataHelper.buildSecurityData(document);         
        sd.setBlockRightInheritance(true, null);
        SecurityDataHelper.updateSecurityOnDocument(document, sd);
        final DocumentRef ref = document.getRef();
        
        CoreSession session = ctx.getCoreSession();
        UnrestrictedSessionRunner runner = new UnrestrictedSessionRunner(session){
            

            @Override
            public void run() throws ClientException {
                DocumentModel docu = session.getDocument(ref);
                List<LMPermission> permissionsAdmin = new ArrayList<LMPermission>();
                List<LMPermission> permissionsWrite = new ArrayList<LMPermission>();
                List<LMPermission> permissionsRead = new ArrayList<LMPermission>();
                for (LMPermission perm : permissions) {
                    if (Rights.EVERYTHING.getRight().equals(perm.getPermission())) {
                        permissionsAdmin.add(perm);
                    } else if (Rights.WRITE.getRight().equals(perm.getPermission())) {
                        permissionsWrite.add(perm);
                    } else if (Rights.READ.getRight().equals(perm.getPermission())) {
                        permissionsRead.add(perm);
                    }
                }
                try {
                    for (LMPermission perm : permissionsAdmin) {
                        LabsSiteUtils.setPermission(docu, perm.permission, perm.getName(), Action.GRANT, true, session);
                    }
                    if (!StringUtils.isEmpty(permission)){
                        if (Rights.WRITE.getRight().equals(permission)){
                            for (LMPermission perm : permissionsRead) {
                                LabsSiteUtils.setPermission(docu, perm.permission, perm.getName(), Action.GRANT, true, session);
                            }
                        }
                        if (Rights.READ.getRight().equals(permission)){
                            for (LMPermission perm : permissionsWrite) {
                                LabsSiteUtils.setPermission(docu, perm.permission, perm.getName(), Action.GRANT, true, session);
                            }
                        }
                    }
                } catch (Exception e) {
                    throw WebException.wrap("Problem for change permissions", e);
                }
                session.save();
            }
            
        };
        runner.runUnrestricted();
        
        session.save();
        
        //get Read and ReadWrite rights on parents
        LabsSiteUtils.unblockInherits("Read", getDocument(), session);
        LabsSiteUtils.unblockInherits("ReadWrite", getDocument(), session);
        
        return Response.ok().build();
    }

    @GET
    @Path("unblockInherits")
    public Response unblockInherits(@QueryParam(value = "permission") String permission) throws IllegalStateException,
            Exception {
        LabsSiteUtils.unblockInherits(permission, getDocument(), ctx.getCoreSession());
        return Response.ok().build();
    }

}
