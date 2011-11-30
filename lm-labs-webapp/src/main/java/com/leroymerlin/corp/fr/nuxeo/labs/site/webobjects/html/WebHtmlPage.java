package com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects.html;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.rendering.api.RenderingEngine;
import org.nuxeo.ecm.webengine.WebException;
import org.nuxeo.ecm.webengine.forms.FormData;
import org.nuxeo.ecm.webengine.model.WebObject;

import com.leroymerlin.corp.fr.nuxeo.labs.site.html.HtmlPage;
import com.leroymerlin.corp.fr.nuxeo.labs.site.html.HtmlSection;
import com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects.NotifiablePageResource;

@WebObject(type = "HtmlPage", superType = "LabsPage")
public class WebHtmlPage extends NotifiablePageResource {

    @Override
    public void initialize(Object... args) {
        super.initialize(args);

        RenderingEngine rendering = ctx.getEngine().getRendering();
        rendering.setSharedVariable("page", getHtmlPage());
    }

    @POST
    @Override
    public Response doPost() {
        FormData form = ctx.getForm();

        if ("addsection".equals(form.getString("action"))) {
            return doAddSection(form);
        }

        return super.doPost();

    }

    private Response doAddSection(FormData form) {
        try {
            String title = form.getString("title");
            String description = form.getString("description");
            String index = form.getString("index");

            int indexNb = -1;
            if (StringUtils.isNotBlank(index)) {
                indexNb = Integer.parseInt(index);
            }

            HtmlSection section = null;
            if(indexNb != -1) {
                section = getHtmlPage().addSection(indexNb);
            } else {
                section = getHtmlPage().addSection();
            }

            section.setTitle(title);
            section.setDescription(description);
            saveDocument();

            return redirect(getPath() + "/@views/edit");
        } catch (ClientException e) {
            throw WebException.wrap(e);
        } catch (NumberFormatException e) {
            throw WebException.wrap(e);
        }
    }

    @Path("s/{index}")
    public Object getSection(@PathParam("index") int sectionIndex) {
        try {
            HtmlSection section = getHtmlPage().section(sectionIndex);
            return newObject("HtmlSection", doc, section);
        } catch (ClientException e) {
            return Response.serverError().build();
        }
    }

    private void saveDocument() throws ClientException {
        getCoreSession().saveDocument(doc);
        getCoreSession().save();
    }

    private HtmlPage getHtmlPage() {
        return doc.getAdapter(HtmlPage.class);
    }

}
