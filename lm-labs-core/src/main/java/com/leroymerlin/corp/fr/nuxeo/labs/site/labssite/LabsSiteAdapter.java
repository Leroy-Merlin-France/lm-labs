/**
 * 
 */
package com.leroymerlin.corp.fr.nuxeo.labs.site.labssite;

import java.io.Serializable;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;

import com.leroymerlin.corp.fr.nuxeo.labs.site.AbstractPage;

/**
 * @author fvandaele
 *
 */
public class LabsSiteAdapter extends AbstractPage implements LabsSite {
    
    static final String URL = "webcontainer:url";
    
    static final String BANNER = "webcontainer:logo";

    public LabsSiteAdapter(DocumentModel doc) {
        this.doc = doc;
    }

    @Override
    public DocumentModel getDocumentModel() {
        return doc;
    }

    @Override
    public String getURL() throws ClientException {
        return (String) doc.getPropertyValue(URL);
    }

    @Override
    public void setURL(String pURL) throws ClientException {
        doc.setPropertyValue(URL, pURL);
    }

    @Override
    public void setDescription(String description) throws PropertyException, ClientException {
        if (description == null) {
            return;
        }
        doc.setPropertyValue("dc:description", description);
    }

    @Override
    public String getDescription() throws PropertyException, ClientException {
        return (String) doc.getPropertyValue("dc:description");
    }

    @Override
    public Blob getLogo() throws ClientException {
        return (Blob) doc.getPropertyValue(BANNER);
    }

    @Override
    public void setLogo(Blob pBlob) throws ClientException {
        if (pBlob == null){
            doc.setPropertyValue(BANNER, null);
        }else{
            doc.setPropertyValue(BANNER, (Serializable) pBlob);
        }
    }

}
