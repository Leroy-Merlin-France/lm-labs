package com.leroymerlin.corp.fr.nuxeo.forum;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import com.leroymerlin.corp.fr.nuxeo.labs.site.Page;
import com.leroymerlin.corp.fr.nuxeo.topic.LMTopic;

public interface LMForum extends Page {

    public LMTopic addTopic(CoreSession session, String topicPath)
            throws ClientException;

    public LMTopic getTopic(CoreSession session, String topicPath)
            throws ClientException;

    public List<LMTopic> getTopics(CoreSession session) throws ClientException;

    public boolean isAllContributors() throws ClientException;

    public void manageAllContributors(final boolean isAllContributors)
            throws ClientException;
}
