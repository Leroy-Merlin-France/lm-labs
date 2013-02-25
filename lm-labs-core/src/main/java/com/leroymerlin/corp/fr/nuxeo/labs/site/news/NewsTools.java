package com.leroymerlin.corp.fr.nuxeo.labs.site.news;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEnclosureImpl;

public class NewsTools {

    private static final Log LOG = LogFactory.getLog(NewsTools.class);
    
    private NewsTools () {}
    
    public static List<SyndEnclosure> createRssNewsEnclosures(final LabsNews news, final String url) throws ClientException {
        List<SyndEnclosure> enclosures = new ArrayList<SyndEnclosure>();
        if (news.hasSummaryPicture()) {
            SyndEnclosure enclosure = new SyndEnclosureImpl();
            try {
                Blob picture = news.getSummaryPicture();
                enclosure.setType(picture.getMimeType());
                enclosure.setUrl(url + "/summaryPictureTruncated");
                enclosure.setLength(picture.getLength());
                enclosures.add(enclosure);
            } catch (IOException e) {
                LOG.error(e, e);
            }
        }
        return enclosures;
    }
    
    public static SyndContent createRssNewsDescription(LabsNews news) throws ClientException {
        SyndContent description;
        description = new SyndContentImpl();
        description.setType("text/html");
        if(!StringUtils.isEmpty(news.getAccroche())){
            description.setValue(news.getAccroche());
        }
        else if (contentNewsIsInRow(news)){
            description.setValue(news.getRows().get(0).content(0).getHtml());
        }
        else{
            description.setValue(news.getContent());
        }
        return description;
    }
    
    private static boolean contentNewsIsInRow(LabsNews news) {
        return news.getRows() != null && news.getRows().size() > 0 
                && news.getRows().get(0) != null && news.getRows().get(0).content(0) != null 
                && !StringUtils.isEmpty(news.getRows().get(0).content(0).getHtml());
    }
}
