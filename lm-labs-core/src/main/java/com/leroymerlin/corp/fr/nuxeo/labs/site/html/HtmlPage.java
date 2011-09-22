package com.leroymerlin.corp.fr.nuxeo.labs.site.html;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;

import com.leroymerlin.corp.fr.nuxeo.labs.site.Page;

public interface HtmlPage extends Page {

    List<HtmlSection> getSections() throws ClientException;

    HtmlSection addSection() throws ClientException;

    HtmlSection section(int index) throws ClientException;

}
