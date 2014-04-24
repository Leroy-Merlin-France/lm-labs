package com.leroymerlin.corp.fr.nuxeo.labs.site.list;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.ecm.platform.comment.api.CommentableDocument;

import com.leroymerlin.common.core.security.SecurityData;
import com.leroymerlin.corp.fr.nuxeo.labs.base.AbstractPage;
import com.leroymerlin.corp.fr.nuxeo.labs.site.exception.NullException;
import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.list.bean.EntriesLine;
import com.leroymerlin.corp.fr.nuxeo.labs.site.list.bean.Entry;
import com.leroymerlin.corp.fr.nuxeo.labs.site.list.bean.EntryType;
import com.leroymerlin.corp.fr.nuxeo.labs.site.list.bean.Header;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.AuthorFullName;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Docs;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.FacetNames;
import com.leroymerlin.common.core.security.SecurityDataHelper;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.Tools;

public class PageListAdapter extends AbstractPage implements PageList {

    private static final String NO = "NON";
    private static final String YES = "OUI";
    private static final String EMPTY_STRING = "";
    private static final String DOUBLE_POINT = ") : ";
    private static final String COMMENTED_BY = "(Commenté le ";
    private static final String FORMAT_DATE_COMMENTS_EXTRACTION = "dd MMMMM yyyy 'à' HH:mm:ss";
    public static final String LINE_TITTLE = "lineTitle";
    private static final String PGL_HEADERLIST = LabsSiteConstants.Schemas.PAGELIST.prefix() + ":headerlist";
    private static final String ALL_CONTRIBUTORS = LabsSiteConstants.Schemas.PAGELIST.prefix() + ":allContributors";
    private static final String COMMENTABLE_LINES = LabsSiteConstants.Schemas.PAGELIST.prefix() + ":commentableLines";
    private static final String WIDTH = "width";
    private static final String ID_HEADER = "idHeader";
    private static final String FONT_SIZE = "fontSize";
    private static final String FONT_NAME = "fontName";
    private static final String TYPE = "type";
    private static final String NAME = "name";
    private static final String ORDER_POSITION = "orderPosition";
    private static final String SELECT_LIST = "selectlist";
    private static final String FORMAT_DATE = "formatDate";
    private static final String ALTERABLE = "alterable";
    private static final String MANDATORY = "mandatory";
    public static final int NULL_VALUE_FOR_INT = -1;

    private static SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMMM yyyy");

    public PageListAdapter(DocumentModel doc) {
        super(doc);
    }

    public static class Model {
        private DocumentModel doc;

        private CoreSession session;

        /**
         * PageList adapter = new PageListAdapter.Model(session, "/",
         * "title").create();
         *
         * @param session
         * @param parentPath
         * @param title
         * @throws ClientException
         */
        public Model(CoreSession session, String parentPath, String title) throws ClientException {
            this.session = session;
            this.doc = session.createDocumentModel(parentPath, title, Docs.PAGELIST.type());
        }

        /**
         * Creates document model in repository.
         *
         * @return an adapter
         * @throws ClientException
         */
        public PageList create() throws ClientException {
            PageListAdapter pageListAdapter = new PageListAdapter(session.createDocument(this.doc));
            pageListAdapter.setSession(session);
            return pageListAdapter;
        }

        /**
         * Getter an adapter
         *
         * @return an adapter
         * @throws ClientException
         */
        public PageList getAdapter() throws ClientException {
            PageListAdapter pageListAdapter = new PageListAdapter(this.doc);
            pageListAdapter.setSession(session);
            return pageListAdapter;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.leroymerlin.corp.fr.nuxeo.labs.site.list.PageList#addHeader(com.
     * leroymerlin.corp.fr.nuxeo.labs.site.list.dto.Header)
     */
    @Override
    public void addHeader(Header pHead) throws ClientException {
        Map<String, Object> map = getHeaderMap(pHead);
        Serializable objHeaderList = doc.getPropertyValue(PGL_HEADERLIST);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> headerList = (List<Map<String, Object>>) objHeaderList;
        headerList.add(map);
        doc.getProperty(PGL_HEADERLIST)
                .setValue(headerList);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.leroymerlin.corp.fr.nuxeo.labs.site.list.PageList#setHeaders(java
     * .util.List)
     */
    @Override
    public void setHeaders(List<Header> headersToSave) throws ClientException {
        List<Map<String, Object>> listHeaders = new ArrayList<Map<String, Object>>();
        Map<Integer, EntryType> linePropIdHeaders = new HashMap<Integer, EntryType>();
        for (Header head : headersToSave) {
            listHeaders.add(getHeaderMap(head));
            String headerType = head.getType();
            if (headerType != null && EntryType.linePropTypes().contains(EntryType.valueOf(headerType))
            ) {
                linePropIdHeaders.put(head.getIdHeader(), EntryType.valueOf(headerType));
            }
        }
        doc.getProperty(PGL_HEADERLIST).setValue(listHeaders);
        // in case a 'lineProp' column is added on a existing PageList we have to check cells' content of 'lineProp' columns
        if (!linePropIdHeaders.isEmpty()) {
            checkLinePropDataConsistency(linePropIdHeaders);
        }
    }

    private void checkLinePropDataConsistency(Map<Integer, EntryType> idHeaders) throws ClientException {
        for(EntriesLine line : getLines()) {
            boolean lineModified = false;
            for (int idHeader : idHeaders.keySet()) {
                Entry entry = line.getEntryByIdHead(idHeader);
                if (entry == null) {
                    Entry newEntry = new Entry();
                    newEntry.setIdHeader(idHeader);
                    newEntry.setText(EMPTY_STRING);
                    line.addEntry(newEntry);
                    entry = newEntry;
                }
                if (entry.getText().isEmpty() || !idHeaders.get(idHeader).xpath().isEmpty()) {
                    entry.setText(idHeaders.get(idHeader).xpath());
                    lineModified = true;
                }
            }
            if (lineModified) {
                saveLine(line, null);
            }
        }
    }

    /**
     * Get the map of header, the keys are the properties of header
     *
     * @param pHead
     * @return the map of header
     */
    private Map<String, Object> getHeaderMap(Header pHead) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(NAME, pHead.getName());
        map.put(TYPE, pHead.getType());
        map.put(FONT_NAME, pHead.getFontName());
        map.put(FONT_SIZE, pHead.getFontSize());
        map.put(ID_HEADER, pHead.getIdHeader());
        map.put(WIDTH, pHead.getWidth());
        map.put(ORDER_POSITION, pHead.getOrderPosition());
        map.put(SELECT_LIST, pHead.getSelectlist());
        map.put(FORMAT_DATE, pHead.getFormatDate());
        map.put(ALTERABLE, pHead.isAlterable());
        map.put(MANDATORY, pHead.isMandatory());
        return map;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.leroymerlin.corp.fr.nuxeo.labs.site.list.PageList#getHeaderlist()
     */
    @Override
    public SortedSet<Header> getHeaderSet() throws ClientException {
        SortedSet<Header> headerlist = new TreeSet<Header>();
        Serializable objHeaderList = doc.getProperty(PGL_HEADERLIST);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> headerList = (List<Map<String, Object>>) objHeaderList;
        for (Map<String, Object> list : headerList) {
            headerlist.add(getHeader(list));
        }
        return headerlist;
    }

    /**
     * Create the header with a map of property and value
     *
     * @param pMap
     * @throws PropertyException
     * @throws NullException
     */
    private Header getHeader(Map<String, Object> pMap) throws PropertyException {
        if (pMap == null) {
            return null;
        }
        Header header = new Header();
        header.setName(Tools.getString(pMap.get(NAME)));
        header.setType(Tools.getString(pMap.get(TYPE)));
        header.setFontName(Tools.getString(pMap.get(FONT_NAME)));
        try {
            header.setIdHeader(Tools.getInt(pMap.get(ID_HEADER)));
        } catch (NullException e) {
            throw new IllegalArgumentException("This object is null.", e);
        }
        try {
            header.setOrderPosition(Tools.getInt(pMap.get(ORDER_POSITION)));
            header.setAlterable(Tools.getBoolean(pMap.get(ALTERABLE)));
            header.setMandatory(Tools.getBoolean(pMap.get(MANDATORY)));
        } catch (NullException e) {
            throw new IllegalArgumentException("This object is null.", e);
        }
        header.setFontSize(Tools.getString(pMap.get(FONT_SIZE)));
        header.setWidth(Tools.getString(pMap.get(WIDTH)));
        header.setSelectlist(Tools.getStringList(pMap.get(SELECT_LIST)));
        header.setFormatDate(Tools.getString(pMap.get(FORMAT_DATE)));
        return header;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.leroymerlin.corp.fr.nuxeo.labs.site.list.PageList#resetHeaders()
     */
    @Override
    public void resetHeaders() throws ClientException {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        doc.getProperty(PGL_HEADERLIST)
                .setValue(list);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.leroymerlin.corp.fr.nuxeo.labs.site.list.PageList#getLines()
     */
    @Override
    public List<EntriesLine> getLines() throws ClientException {
        DocumentModelList listDocLines = null;
        List<EntriesLine> entriesLines = new ArrayList<EntriesLine>();
        CoreSession session = getSession();
        StringBuilder sb = new StringBuilder("SELECT * From PageListLine");
        sb.append(" WHERE ecm:parentId = '")
                .append(doc.getId()).append("'");
        sb.append(" AND ecm:isCheckedInVersion = 0");
        sb.append(" AND ecm:currentLifeCycleState <> 'deleted'");
        if (!session.hasPermission(doc.getRef(), SecurityConstants.READ_WRITE)) {
            sb.append(" AND ").append(NXQL.ECM_MIXINTYPE).append(" <> '").append(FacetNames.LABSHIDDEN).append("'");
        }
        sb.append(" ORDER BY dc:created ASC");
        listDocLines = session.query(sb.toString());
        //listDocLines = session.getChildren(doc.getRef(), LabsSiteConstants.Docs.PAGELIST_LINE.type());
        EntriesLine line;
        PageListLine adapterLine;
        for (DocumentModel docTmp : listDocLines) {
            adapterLine = Tools.getAdapter(PageListLine.class, docTmp, session);
            line = adapterLine.getLine();
            line.setDocLine(docTmp);
            line.setNbComments(adapterLine.getNbComments());
            line.setVisible(adapterLine.isVisible());
            line.setNbrFiles(adapterLine.getFiles().size());
            entriesLines.add(line);
        }
        return entriesLines;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.leroymerlin.corp.fr.nuxeo.labs.site.list.PageList#saveLine(com.
     * leroymerlin.corp.fr.nuxeo.labs.site.list.bean.EntriesLine)
     */
    @Override
    public void saveLine(EntriesLine pLine, LabsSite pSite) throws ClientException {
        CoreSession session = getSession();
        DocumentModel lineDoc = null;
        boolean isNew = pLine.getDocLine() == null;
        if (isNew) {
            lineDoc = session.createDocumentModel(doc.getPathAsString(), LINE_TITTLE, LabsSiteConstants.Docs.PAGELIST_LINE.type());
        } else {
            lineDoc = pLine.getDocLine();
        }
        PageListLine line = Tools.getAdapter(PageListLine.class, lineDoc, session);
        line.setLine(pLine);
        if(pLine.isVisible()){
            line.show();
        }
        else{
            line.hide();
        }
        if (isNew) {
            lineDoc = session.createDocument(lineDoc);
            pLine.setDocLine(lineDoc);
        } else {
            lineDoc = session.saveDocument(lineDoc);
        }
        session.save();
        if (isNew) {
            manageAddedPermission(pLine, pSite);
        }
    }

    private void manageAddedPermission(final EntriesLine pLine, final LabsSite site) throws ClientException{
        if (site == null){
            return;
        }
        final boolean isAllContributors = isAllContributors();
        if (!isAllContributors || site.isAdministrator(pLine.getUserName()) || site.isContributor(pLine.getUserName())){
            return;
        }
        final DocumentRef ref = pLine.getDocLine().getRef();
        UnrestrictedSessionRunner runner = new UnrestrictedSessionRunner(getSession()){
            @Override
            public void run() throws ClientException {
                DocumentModel docu = session.getDocument(ref);
                SecurityData data = SecurityDataHelper.buildSecurityData(docu);
                data.addModifiablePrivilege(pLine.getUserName(), SecurityConstants.READ_WRITE, true);
                data.addModifiablePrivilege(pLine.getUserName(), SecurityConstants.ADD_CHILDREN, true);
                data.addModifiablePrivilege(pLine.getUserName(), SecurityConstants.REMOVE_CHILDREN, true);
                SecurityDataHelper.updateSecurityOnDocument(docu, data);
                session.save();
            }

        };
        runner.runUnrestricted();

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.leroymerlin.corp.fr.nuxeo.labs.site.list.PageList#removeLine(org.
     * nuxeo.ecm.core.api.DocumentRef)
     */
    @Override
    public void removeLine(DocumentRef pRef) throws ClientException {
        getSession().removeDocument(pRef);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.leroymerlin.corp.fr.nuxeo.labs.site.list.PageList#getLine(org.nuxeo
     * .ecm.core.api.DocumentRef)
     */
    @Override
    public EntriesLine getLine(DocumentRef pRef) throws ClientException {
        EntriesLine line = new EntriesLine();
        CoreSession session = getSession();
        DocumentModel lineDoc = session.getDocument(pRef);
        if (lineDoc != null) {
            line.setDocLine(lineDoc);
            line = Tools.getAdapter(PageListLine.class, lineDoc, session)
                    .getLine();
        }
        return line;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.leroymerlin.corp.fr.nuxeo.labs.site.list.PageList#isAllCintibutors()
     */
    @Override
    public boolean isAllContributors() throws ClientException {
        Serializable propertyValue = doc.getPropertyValue(ALL_CONTRIBUTORS);
        if (propertyValue instanceof Boolean) {
            return ((Boolean) propertyValue).booleanValue();
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.leroymerlin.corp.fr.nuxeo.labs.site.list.PageList#setAllContributors
     * (boolean)
     */
    @Override
    public void setAllContributors(final boolean isAllContributors) throws ClientException {
        if (isAllContributors != isAllContributors()){
            final DocumentRef ref = doc.getRef();
            UnrestrictedSessionRunner runner = new UnrestrictedSessionRunner(getSession()){

                @SuppressWarnings("deprecation")
                @Override
                public void run() throws ClientException {
                    DocumentModel docu = session.getDocument(ref);
                    SecurityData data = SecurityDataHelper.buildSecurityData(docu);
                    if (isAllContributors){
                        data.addModifiablePrivilege(SecurityConstants.MEMBERS, SecurityConstants.ADD_CHILDREN, true);
                        data.addModifiablePrivilege(SecurityConstants.MEMBERS, SecurityConstants.REMOVE_CHILDREN, true);
                    }
                    else{
                        data.removeModifiablePrivilege(SecurityConstants.MEMBERS, SecurityConstants.ADD_CHILDREN, true);
                        data.addModifiablePrivilege(SecurityConstants.MEMBERS, SecurityConstants.REMOVE_CHILDREN, true);
                    }
                    SecurityDataHelper.updateSecurityOnDocument(docu, data);
                    session.save();
                }

            };
            runner.runUnrestricted();
            doc.setPropertyValue(ALL_CONTRIBUTORS, isAllContributors);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.leroymerlin.corp.fr.nuxeo.labs.site.list.PageList#isCommentableLines
     * ()
     */
    @Override
    public boolean isCommentableLines() throws ClientException {
        Serializable propertyValue = doc.getPropertyValue(COMMENTABLE_LINES);
        if (propertyValue instanceof Boolean) {
            return ((Boolean) propertyValue).booleanValue();
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.leroymerlin.corp.fr.nuxeo.labs.site.list.PageList#setCommentableLines
     * (boolean)
     */
    @Override
    public void setCommentableLines(boolean isAllCommentablesLines) throws ClientException {
        doc.setPropertyValue(COMMENTABLE_LINES, isAllCommentablesLines);
    }

    /* (non-Javadoc)
     * @see com.leroymerlin.corp.fr.nuxeo.labs.site.list.PageList#exportExcel(java.io.OutputStream)
     */
    @Override
    public void exportExcel(OutputStream pOut) throws ClientException, IOException {
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet("export");

        int numRow = 0;
        int numCell = 0;
        Cell cell = null;
        Row row = null;
        SortedSet<Header> headers = getHeaderSet();

        Font titleFont = wb.createFont();
        titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        CellStyle style = wb.createCellStyle();
        style.setFont(titleFont);

        Row headerRow = sheet.createRow(numRow);
        for (Header head : headers) {
            cell = headerRow.createCell(numCell);
            cell.setCellStyle(style);
            cell.setCellValue(head.getName());
            numCell ++;
        }
        //Add hearder for comments
        cell = headerRow.createCell(numCell);
        cell.setCellStyle(style);
        cell.setCellValue("Ligne cachée");
        numCell++;
        //Add hearder for comments
        cell = headerRow.createCell(numCell);
        cell.setCellStyle(style);
        cell.setCellValue("Commentaires");

        numCell = 0;
        numRow ++;
        SimpleDateFormat sdfHeader = null;
        for (EntriesLine line : getLines()) {
            row = sheet.createRow(numRow);
            for (Header head : headers) {
                cell = row.createCell(numCell);
                Entry entry = line.getEntryByIdHead(head.getIdHeader());
                if (entry != null){
                    switch (EntryType.valueOf(head.getType())) {
                    case CHECKBOX:
                        if (entry.isCheckbox()){
                            cell.setCellValue(YES);
                        }
                        else{
                            cell.setCellValue(NO);
                        }
                        break;
                    case DATE:
                        cell.setCellValue(getCellFormattedDateString(entry.getDate(), head.getFormatDate()));
                        break;
                    case SELECT:
                        cell.setCellValue(entry.getText());
                        break;
                    case TEXT:
                        cell.setCellValue(entry.getText());
                        break;
                    case MODIFIED:
                    case CREATED:
                    {
                        Calendar datePropVal = (Calendar) line.getDocLine().getPropertyValue(StringUtils.defaultIfEmpty(entry.getText(), EntryType.valueOf(head.getType()).xpath()));
                        cell.setCellValue(getCellFormattedDateString(datePropVal, head.getFormatDate()));
                        break;
                    }
                    case CREATOR:
                        String creator = (String) line.getDocLine().getPropertyValue(StringUtils.defaultIfEmpty(entry.getText(), EntryType.CREATOR.xpath()));
                        cell.setCellValue(AuthorFullName.getFormattedUserName(creator, null));
                        break;
                    case URL:
                        if (entry.getUrl() != null) {
                            cell.setCellValue(entry.getUrl()
                                    .getUrl());
                        } else {
                            cell.setCellValue(EMPTY_STRING);
                        }
                        break;
                    case TEXTAREA:
                        cell.setCellValue(entry.getText());
                        break;
                    case FILES:
                        cell.setCellValue(line.getNbrFiles());
                        break;
                    default:
                        cell.setCellValue(EMPTY_STRING);
                        break;
                    }
                }
                else{
                    cell.setCellValue(EMPTY_STRING);
                }
                numCell++;
            }
            //Lignes cachées
            cell = row.createCell(numCell);
            if (line.isVisible()){
                cell.setCellValue(false);
            }
            else{
                cell.setCellValue(true);
            }
            numCell++;

            //Write comments of line
            CommentableDocument adapterComments = line.getDocLine().getAdapter(CommentableDocument.class);
            if (adapterComments != null){
                List<DocumentModel> comments = adapterComments.getComments();
                if (!comments.isEmpty()){
                    AuthorFullName afn = new AuthorFullName(new HashMap<String, String>(), LabsSiteConstants.Comments.COMMENT_AUTHOR);
                    afn.loadFullName(comments);
                    for (DocumentModel comment : comments){
                        cell = row.createCell(numCell);
                        sdfHeader = new SimpleDateFormat(FORMAT_DATE_COMMENTS_EXTRACTION);
                        StringBuilder strBuider = new StringBuilder(afn.getFullName((String)comment.getPropertyValue(LabsSiteConstants.Comments.COMMENT_AUTHOR)));
                        strBuider.append(COMMENTED_BY)
                            .append(sdfHeader.format(((GregorianCalendar)comment.getPropertyValue(LabsSiteConstants.Comments.COMMENT_CREATION_DATE)).getTime()))
                            .append(DOUBLE_POINT).append((String)comment.getPropertyValue(LabsSiteConstants.Comments.COMMENT_TEXT));
                        cell.setCellValue(strBuider.toString());
                        numCell++;
                    }
                }
            }
            numCell = 0;
            numRow++;
        }
        wb.write(pOut);
    }

    private String getCellFormattedDateString(Calendar entryDate, String dateFormat) {
        String cellValue = EMPTY_STRING;
        if (entryDate != null) {
            if(!StringUtils.isEmpty(dateFormat)){
                SimpleDateFormat sdfHeader = new SimpleDateFormat(dateFormat);
                cellValue = sdfHeader.format(entryDate.getTime());
            }
            else{
                cellValue = sdf.format(entryDate.getTime());
            }
        }
        return cellValue;
    }

}
