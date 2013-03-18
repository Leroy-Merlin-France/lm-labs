package com.leroymerlin.corp.fr.nuxeo.labs.site.event.mail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

abstract class SendMailActivitiesEventListener  implements EventListener {

    private static final Log LOG = LogFactory.getLog(SendMailActivitiesEventListener.class);
    protected static final String IMPOSSIBLE_TO_GET_SERVICE_USER_MANAGER = "Impossible to get service UserManager !";
    
    private UserManager userManager;

    @Override
    public void handleEvent(Event evt) throws ClientException {
    	if (!(evt.getContext() instanceof DocumentEventContext)) {
            return;
        }
    	DocumentEventContext ctx = (DocumentEventContext) evt.getContext();
    	DocumentModel doc = ctx.getSourceDocument();
        String eventName = evt.getName();
        if (accept(doc, eventName)) {
			try {
				sendDocument(doc, ctx);
			} catch (Exception e) {
				LOG.error(e, e);
			}
        }
    }
    
    abstract void sendDocument(DocumentModel doc, DocumentEventContext ctx) throws Exception;
    
    abstract boolean accept(DocumentModel doc, String EventName);
    
    private UserManager getUsermanager() throws ClientException{
    	if (userManager == null){
            try {
                userManager = Framework.getService(UserManager.class);
            } catch (Exception e) {
                throw new ClientException(IMPOSSIBLE_TO_GET_SERVICE_USER_MANAGER, e);
            }
    	}
    	return userManager;
    }
    
    protected String authorFullName(String ldap) throws ClientException{
    	NuxeoPrincipal principal = getUsermanager().getPrincipal(ldap);
    	return principal.getFirstName() + " " + principal.getLastName();
    }

}
