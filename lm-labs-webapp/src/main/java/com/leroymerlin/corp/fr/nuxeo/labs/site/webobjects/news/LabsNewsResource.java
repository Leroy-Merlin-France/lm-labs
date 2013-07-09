package com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects.news;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.filemanager.utils.FileManagerUtils;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.ecm.webengine.WebException;
import org.nuxeo.ecm.webengine.forms.FormData;
import org.nuxeo.ecm.webengine.model.WebObject;

import com.leroymerlin.corp.fr.nuxeo.labs.site.Page;
import com.leroymerlin.corp.fr.nuxeo.labs.site.exception.LabsBlobHolderException;
import com.leroymerlin.corp.fr.nuxeo.labs.site.html.HtmlRow;
import com.leroymerlin.corp.fr.nuxeo.labs.site.news.LabsNews;
import com.leroymerlin.corp.fr.nuxeo.labs.site.news.PageNews;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.CommonHelper;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.Tools;
import com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects.PageResource;

@WebObject(type = "LabsNews", superType = "LabsPage")
@Produces("text/html; charset=UTF-8")
public class LabsNewsResource extends PageResource {

    private static final Log log = LogFactory.getLog(LabsNewsResource.class);

    LabsNews labsNews;
    private DocumentModel prevNewsDoc = null;
    private DocumentModel nextNewsDoc = null;
    private PageProvider<DocumentModel> newsPageProvider;

    private static final SimpleDateFormat sdf = new SimpleDateFormat(
            "dd/MM/yyyy 'à' HH:mm");

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(Object... args) {
        super.initialize(args);
        ctx.getEngine()
                .getRendering()
                .setSharedVariable("news", getLabsNews());
        if (args.length >= 2) {
        	newsPageProvider = (PageProvider<DocumentModel>) args[1];
        }
    }

    private void initPrevNextNews() {
    	PageProvider<DocumentModel> pageProvider = getNewsPageProvider();
    	pageProvider.setPageSize(Long.MAX_VALUE);
    	try {
    		pageProvider.setCurrentEntry(getDocument());
    		getNewsPageProvider();
    		if (pageProvider.isPreviousEntryAvailable()) {
    			pageProvider.previousEntry();
    			prevNewsDoc = pageProvider.getCurrentEntry();
    		}
    		pageProvider.setCurrentEntry(getDocument());
    		getNewsPageProvider();
    		if (pageProvider.isNextEntryAvailable()) {
    			pageProvider.nextEntry();
    			nextNewsDoc = pageProvider.getCurrentEntry();
    		}
    	} catch (Exception e) {
    		throw WebException.wrap(e);
    	}
    }

    public DocumentModel getPrevNewsDoc() {
    	return prevNewsDoc;
    }

    public DocumentModel getNextNewsDoc() {
    	return nextNewsDoc;
    }

    public boolean hasNextNewsDoc() {
    	return nextNewsDoc == null ? false : true;
    }

    public boolean hasPrevNewsDoc() {
    	return prevNewsDoc == null ? false : true;
    }

    @Override
    public Object doGet() {
    	initPrevNextNews();
    	return super.doGet();
    }

    public LabsNews getLabsNews() {
        if (labsNews == null){
            labsNews = Tools.getAdapter(LabsNews.class, doc, ctx.getCoreSession());
        }
        return labsNews;
    }

    @Override
    public Page getPage() throws ClientException {
        CoreSession session = ctx.getCoreSession();
        Page page = Tools.getAdapter(Page.class, doc, session);
        return page;
    }

    public Page getParentPage() throws ClientException {
        CoreSession session = ctx.getCoreSession();
        DocumentModel parentDoc = session.getDocument(doc.getParentRef());
        PageNews pageNews = Tools.getAdapter(PageNews.class, parentDoc, session);
        return pageNews;
    }

    @POST
    @Override
    public Response doPost() {
        FormData form = ctx.getForm();
        CoreSession session = ctx.getCoreSession();
        try {
            LabsNews news = Tools.getAdapter(LabsNews.class, doc, session);
            fillNews(form, news, session);
            if (CommonHelper.siteDoc(doc).getSite().isAdministrator(ctx.getPrincipal().getName())){
                String elementTemplateStr = form.getString("let:elementTemplate");
                boolean isElementTemplate = BooleanUtils.toBoolean(elementTemplateStr);
                if (news.isElementTemplate() != isElementTemplate) {
                	news.setElementTemplate(isElementTemplate);
                }
                if (isElementTemplate) {
                    if (form.isMultipartContent()) {
                        Blob preview = form.getBlob("let:preview");
                        if (preview != null
                                && !StringUtils.isEmpty(preview.getFilename())) {
                        	news.setElementPreview(preview);
                        }
                    }
                }
            }
            session.saveDocument(doc);
            session.save();

            return redirect(getPath()
                    + "?message_success=label.labsNews.news_updated&props=open");
        } catch (ClientException e) {
            throw WebException.wrap(e);
        } catch (IOException e) {
            throw WebException.wrap(e);
        }catch (LabsBlobHolderException e) {
            log.info("Summary picture not save : " + ctx.getMessage(e.getMessage()), e);
            save();
            return redirect(getPath()
                    + "?message_warning=" + e.getMessage() + "&props=open");
        }

    }

    private void save(){
        CoreSession session = ctx.getCoreSession();
        try {
            session.saveDocument(doc);
            session.save();
        } catch (ClientException e) {
            throw WebException.wrap(e);
        }
    }

    @Path("s/{index}")
    public Object doGetSection(@PathParam("index") int sectionIndex) throws ClientException {
        // For the news there is only one section
        return newObject("HtmlSection", doc, getLabsNews(), sectionIndex, getLabsNews());
    }

    static Calendar getDateFromStr(String strDate) {
        Calendar cal = Calendar.getInstance();
        if (!StringUtils.isEmpty(strDate)) {
            try {
                cal.setTimeInMillis((sdf.parse(strDate)).getTime());
                return cal;
            } catch (ParseException e) {
                log.error("Unable to parse string date '" + strDate + "'", e);
                return null;
            }
        }
        return null;
    }

    static void fillNews(FormData form, LabsNews news, CoreSession session) throws ClientException, IOException, LabsBlobHolderException {
        String pTitle = form.getString("dc:title");
        String startDate = form.getString("newsStartPublication");
        String endDate = form.getString("newsEndPublication");
        String content = form.getString("newsContent");
        String accroche = form.getString("newsAccroche");
        String cropSummaryPicture = form.getString("cropSummaryPicture");
        String commentable = form.getString("commentablePage");
        String isTop = form.getString("isTop");

        news.setTitle(pTitle);
        news.setStartPublication(getDateFromStr(startDate));
        news.setEndPublication(getDateFromStr(endDate));
        news.setContent(content);
        news.setAccroche(accroche);
        news.setCommentable(BooleanUtils.toBoolean(commentable));
        news.setTop(BooleanUtils.toBoolean(isTop));

        if (form.isMultipartContent()) {
            Blob blob = form.getBlob("newsPicture");
            if (blob != null){
                blob.setFilename(FileManagerUtils.fetchFileName(blob.getFilename()));
                blob.persist();
                if(blob.getLength() > 0){
                    //return an LabsBlobHolderException if no valid
                    news.checkPicture(blob);
                    news.setOriginalPicture(blob);
                }
            }
        }
        //After setOriginalPicture if necessary
        if (!StringUtils.isEmpty(cropSummaryPicture)){
            news.setCropCoords(cropSummaryPicture);
        }
    }

    @Override
    public Response doDelete() {
        super.doDelete();
        if (this.getPrevious() != null){
            redirect(this.getPrevious().getPath() + "?message_success=label.labsNews.news_updated");
        }
        return redirect(ctx.getBasePath());
    }

    public Map<String, String> getColumnLayoutsSelect() throws ClientException {
    	return HtmlRow.getColumnLayoutsSelect();
    }

    @GET
    @Path("summaryPicture")
    public Response getSummaryPicture() {
        try {
            Blob blob = getLabsNews().getBlobHolder().getBlob("OriginalJpeg");
            if (blob != null) {
                 return Response.ok().entity(blob).type(blob.getMimeType()).build();
            }
        } catch (Exception e) {
            throw WebException.wrap(e);
        }
        throw new WebException(Response.Status.NOT_FOUND);
    }

    @POST
    @Path("deleteSummaryPicture")
    public Response deleteSummaryPicture() {
        try {
            getLabsNews().deleteSummaryPicture();
            save();
        } catch (ClientException e) {
            throw WebException.wrap(e);
        }
        return redirect(this.getPath() + "?message_success=label.labsNews.summaryPicture_deleted&props=open");
    }

    @GET
    @Path("summaryPictureTruncated")
    public Response getSummaryPictureTruncated() {
        try {
            Blob blob = getLabsNews().getSummaryPicture();
            if (blob != null) {
                 return Response.ok().entity(blob).type(blob.getMimeType()).build();
            }
        } catch (Exception e) {
            throw WebException.wrap(e);
        }
        throw new WebException(Response.Status.NOT_FOUND);
    }

    private PageProvider<DocumentModel> getNewsPageProvider() {
    	//logNewsPageProvider();
    	return this.newsPageProvider;
    }

    public LabsNews getLabsNews(DocumentModel document) {
        return Tools.getAdapter(LabsNews.class, document, ctx.getCoreSession());
    }

	@SuppressWarnings("unused")
    private void logNewsPageProvider() {
		if (log.isDebugEnabled()) {
    		final String logPrefix = "<getNewsPageProvider> ";
    		try {
    			log.debug(logPrefix + "pageSize: " + newsPageProvider.getPageSize());
    			log.debug(logPrefix + "currentEntry: " + newsPageProvider.getCurrentEntry().getTitle());
    			log.debug(logPrefix + "isNextEntryAvailable: " + newsPageProvider.isNextEntryAvailable());
    			if (newsPageProvider.isNextEntryAvailable()) {
    				log.debug(logPrefix + "currentEntry: " + "");
    			}
    			log.debug(logPrefix + "isPreviousEntryAvailable: " + newsPageProvider.isPreviousEntryAvailable());
    		} catch (ClientException e) {
    			log.error(logPrefix);
    		}
    	}
	}

}
