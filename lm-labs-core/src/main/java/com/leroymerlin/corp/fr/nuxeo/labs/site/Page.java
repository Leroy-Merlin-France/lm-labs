package com.leroymerlin.corp.fr.nuxeo.labs.site;

import org.nuxeo.ecm.core.api.ClientException;

public interface Page extends LabsBase {

    public String getPath() throws ClientException;
    
    boolean isCommentable() throws ClientException;
    
    void setCommentable(boolean isCommentable) throws ClientException;
    
    boolean isDisplayableTitle() throws ClientException;
    
    void setDisplayableTitle(boolean isDisplayableTitle) throws ClientException;
    
    boolean isDisplayableDescription() throws ClientException;
    
    void setDisplayableDescription(boolean isDisplayableDescription) throws ClientException;
}
