package com.leroymerlin.corp.fr.nuxeo.labs.site.news;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.Sorter;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.query.sql.NXQL;

import com.leroymerlin.corp.fr.nuxeo.labs.site.AbstractPage;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Docs;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteUtils;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

public class PageNewsAdapter extends AbstractPage implements PageNews {

    public PageNewsAdapter(DocumentModel doc) {
        this.doc = doc;
    }

    @Override
    public LabsNews createNews( String pTitle)
            throws ClientException {
        CoreSession session = doc.getCoreSession();

        DocumentModel document = session
                .createDocumentModel(doc.getPathAsString(), LabsSiteUtils.doLabsSlugify(pTitle),
                        LabsSiteConstants.Docs.LABSNEWS.type());

        document.setPropertyValue("dc:title", pTitle);
        return session.createDocument(document).getAdapter(LabsNews.class);
    }

    @Override
    public List<LabsNews> getAllNews() throws ClientException {
      List<LabsNews> listNews = new ArrayList<LabsNews>();
      DocumentModelList listDoc = null;
      Sorter pageNewsSorter = new PageNewsSorter();
      if (getCoreSession().hasPermission(doc.getRef(),
              SecurityConstants.WRITE)) {
          listDoc = getCoreSession().getChildren(doc.getRef(),
                  LabsSiteConstants.Docs.LABSNEWS.type(), null, null,
                  pageNewsSorter);
      } else {
          listDoc = getCoreSession().getChildren(doc.getRef(),
                  LabsSiteConstants.Docs.LABSNEWS.type(), null,
                  new PageNewsFilter(Calendar.getInstance()), pageNewsSorter);
      }

      for (DocumentModel doc : listDoc) {
          LabsNews news = doc.getAdapter(LabsNews.class);
          listNews.add(news);
      }
      return listNews;
    }

    public List<LabsNews> getTopNews(int pMaxNews) throws ClientException{
        List<LabsNews> listNews = new ArrayList<LabsNews>();
        DocumentModelList listDoc = null;
        Sorter pageNewsSorter = new PageNewsSorter();
        listDoc = getCoreSession().getChildren(doc.getRef(),
                LabsSiteConstants.Docs.LABSNEWS.type(), null,
                new PageNewsFilter(Calendar.getInstance()), pageNewsSorter);
        int nb = 0;
        for (DocumentModel doc : listDoc) {
            if (nb >= pMaxNews){
                break;
            }
            LabsNews news = doc.getAdapter(LabsNews.class);
            listNews.add(news);
            nb ++;
        }
        return listNews;
    }

    private CoreSession getCoreSession() {
        return doc.getCoreSession();
    }

    // TODO unit tests
    @Override
    public Collection<DocumentModel> getTopNewsStartingOn(Calendar startDate) throws ClientException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(startDate.getTime());

        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ").append(Docs.LABSNEWS.type()).append(" WHERE ");
        query.append(NXQL.ECM_PATH).append(" STARTSWITH '").append(doc.getPathAsString().replace("'", "\\'")).append("'");
        query.append(" AND ").append(LabsNewsAdapter.START_PUBLICATION).append(" >= TIMESTAMP '").append(dateStr).append(" 00:00:00").append("'");
        query.append(" AND ").append(LabsNewsAdapter.START_PUBLICATION).append(" <= TIMESTAMP '").append(dateStr).append(" 23:59:59").append("'");

        return doc.getCoreSession().query(query.toString());
    }

    @Override
    public SyndFeed buildRssLabsNews(List<LabsNews> pLabsNews, String pPathBase, String pDefaultDescription) throws ClientException {
        String feedType = "rss_2.0";

        final SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType(feedType);

        feed.setTitle(getTitle());
        feed.setLink(pPathBase);
        feed.setDescription(buildRssPageNewsDescription(pDefaultDescription));
        feed.setEntries(createRssEntries(pLabsNews, pPathBase));
        return feed;
    }

    private String buildRssPageNewsDescription(String pDefaultDescription) throws ClientException {
        String description = getDescription();
        if (!StringUtils.isEmpty(description)){
            return description;
        }
        else{
            return pDefaultDescription;
        }
    }

    private List<SyndEntry> createRssEntries(List<LabsNews> topNews, String pPathBase) throws ClientException {
        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        SyndEntry entry;
        for (LabsNews news:topNews){
            entry = new SyndEntryImpl();
            entry.setTitle(news.getTitle());
            entry.setLink(pPathBase + "/" + news.getDocumentModel().getName());
            entry.setPublishedDate(news.getStartPublication().getTime());
            entry.setDescription(NewsTools.createRssNewsDescription(news));
            entries.add(entry);
        }
        return entries;
    }
}
