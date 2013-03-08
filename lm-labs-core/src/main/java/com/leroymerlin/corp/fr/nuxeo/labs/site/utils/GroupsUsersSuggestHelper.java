/**
 * 
 */
package com.leroymerlin.corp.fr.nuxeo.labs.site.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.directory.SizeLimitExceededException;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

import com.leroymerlin.common.core.security.GroupUserSuggest;


//TODO move to lm-nuxeo-common and delete from lm-portal-common
/**
 * @author <a href="mailto:vincent.dutat@ext.leroymerlin.fr">Vincent Dutat</a>
 *
 */
public class GroupsUsersSuggestHelper {
	
	private static final Log log = LogFactory.getLog(GroupsUsersSuggestHelper.class);

	private GroupsUsersSuggestHelper() {}

	public static List<GroupUserSuggest> getSuggestions() {
		final String logPrefix = "<getSuggestions> ";
		log.debug(logPrefix);
		List<GroupUserSuggest> suggests = new ArrayList<GroupUserSuggest>();
		try {
			UserManager userManager = Framework.getService(UserManager.class);
			for (String group : userManager.getGroupIds()) {
				suggests.add(new GroupUserSuggest(group, ""));
			}
			for (DocumentModel principal : userManager.searchUsers(null)) {
				suggests.add(new GroupUserSuggest((String) principal
						.getProperty(userManager.getUserSchemaName(),
								"username"), principal.getProperty(userManager
						.getUserSchemaName(), "firstName")
						+ " "
						+ principal.getProperty(userManager.getUserSchemaName(), "lastName")));
			}
		} catch (ClientException e) {
			log.error(e, e);
		} catch (Exception e) {
			log.error(e, e);
		}
		Collections.sort(suggests);

		return suggests;
	}
	
	public static String getJSONSuggestions() {
		// TODO better code
		List<GroupUserSuggest> suggests = getSuggestions();
		StringBuffer sb = new StringBuffer("[");
		for (GroupUserSuggest suggest : suggests) {
			sb.append("{");
			sb.append("\"username\":");
			sb.append("\"" + suggest.getName() + "\",");
			sb.append("\"fullname\":");
			sb.append("\"" + suggest.getFullName() + "\"");
			sb.append("},");
		}
		sb.append("]");

		return sb.toString();
	}
	
    public static List<GroupUserSuggest> getSuggestions(String str) throws ClientException {
        final String logPrefix = "<getSuggestions> ";
        log.debug(logPrefix + str);
        List<GroupUserSuggest> suggests = new ArrayList<GroupUserSuggest>();
        UserManager userManager = null;
        try {
            userManager = Framework.getService(UserManager.class);
        } catch (Exception e) {
            log.error(e, e);
        }
        if (userManager != null) {
            final String trimmed = StringUtils.trim(str);
            add(suggests, searchGroups(userManager, trimmed));
            add(suggests, searchUsers(userManager, trimmed));
            Collections.sort(suggests);
        }

        log.debug(logPrefix + "returns " + suggests.size() + " entries: " + suggests);
        return suggests;
    }

    private static void add(List<GroupUserSuggest> suggests, List<GroupUserSuggest> searchUsers) {
        for (final GroupUserSuggest suggest : searchUsers ) {
            if (!CollectionUtils.exists(suggests, new Predicate() {
                public boolean evaluate(Object o) {
                    return ((GroupUserSuggest) o).getName().equals(suggest.getName());
                }})) {
                suggests.add(suggest);
            }
        }
    }

    public static String getJSONSuggestions(String str) throws ClientException {
        // TODO better code
        List<GroupUserSuggest> suggests;
        try {
            suggests = getSuggestions(str);
            StringBuffer sb = new StringBuffer("[");
            for (GroupUserSuggest suggest : suggests) {
                sb.append("{");
                sb.append("\"username\":");
                sb.append("\"" + suggest.getName() + "\",");
                sb.append("\"fullname\":");
                sb.append("\"" + suggest.getFullName() + "\"");
                sb.append("},");
            }
            sb.append("]");
            
            if (log.isDebugEnabled()) {
                log.debug("JSON suggest:" + sb.toString());
            }
            return sb.toString();
        } catch (SizeLimitExceededException e) {
            log.error(e, e);
            return "";
        }
    }
    
    /**
     * @param suggests
     * @param userManager
     * @param filter
     * @throws ClientException
     */
//    private static List<GroupUserSuggest> searchGroups(UserManager userManager, Map<String, Serializable> filter) throws ClientException, SizeLimitExceededException {
//        final String logPrefix = "<searchGroups> ";
//        List<GroupUserSuggest> suggests = new ArrayList<GroupUserSuggest>();
//        HashSet<String> fulltext = new HashSet<String>();
//        if (!filter.isEmpty()) {
//            fulltext.addAll(filter.keySet());
//        }
//        for (DocumentModel group : userManager.searchGroups(
//                filter,
////                new HashMap<String, Serializable>(),
////              new HashSet<String>()
//                fulltext
//        )) {
//            suggests.add(new GroupUserSuggest((String) group.getProperty(userManager.getGroupSchemaName(), userManager.getGroupIdField()), ""));
//        }
//        if (log.isDebugEnabled()) {
//            log.debug(logPrefix + filter + " returns " + suggests.size() + " entries: " + suggests);
//        }
//        return suggests;
//    }

    private static List<GroupUserSuggest> searchGroups(UserManager userManager, String pattern) throws ClientException, SizeLimitExceededException {
        final String logPrefix = "<searchGroups> ";
        List<GroupUserSuggest> suggests = new ArrayList<GroupUserSuggest>();
		for (DocumentModel group : userManager.searchGroups(pattern)) {
			suggests.add(new GroupUserSuggest((String) group.getPropertyValue(userManager.getGroupIdField()), ""));
		}
        if (log.isDebugEnabled()) {
            log.debug(logPrefix + " returns " + suggests.size() + " entries: " + suggests);
        }
        return suggests;
    }

    /**
     * @param suggests
     * @param userManager
     * @param filter
     * @param columns
     * @throws ClientException
     */
//    private static List<GroupUserSuggest> searchUsers(UserManager userManager, Map<String, Serializable> filter, final HashSet<String> columns) throws ClientException, SizeLimitExceededException {
//        final String logPrefix = "<searchUsers> ";
//        List<GroupUserSuggest> suggests = new ArrayList<GroupUserSuggest>();
//        for (DocumentModel principal : userManager.searchUsers(
//              filter,
////                new HashMap<String, Serializable>(),
//                new HashSet<String>(filter.keySet())
////                new HashSet<String>()
//                )) {
//            suggests.add(new GroupUserSuggest((String) principal.getProperty(userManager.getUserSchemaName(),"username"),
//                    principal.getProperty(userManager.getUserSchemaName(), "firstName")
//                    + " "
//                    + principal.getProperty(userManager.getUserSchemaName(), "lastName")));
//        }
//        if (log.isDebugEnabled()) {
//            log.debug(logPrefix + filter + " returns " + suggests.size() + " entries: " + suggests);
//        }
//        return suggests;
//    }
    
    private static List<GroupUserSuggest> searchUsers(UserManager userManager, String pattern) throws ClientException, SizeLimitExceededException {
        final String logPrefix = "<searchUsers> ";
        List<GroupUserSuggest> suggests = new ArrayList<GroupUserSuggest>();
        for (DocumentModel principal : userManager.searchUsers(pattern)) {
            suggests.add(new GroupUserSuggest((String) principal.getProperty(userManager.getUserSchemaName(),"username"),
                    principal.getProperty(userManager.getUserSchemaName(), "firstName")
                    + " "
                    + principal.getProperty(userManager.getUserSchemaName(), "lastName")));
        }
        if (log.isDebugEnabled()) {
            log.debug(logPrefix + " returns " + suggests.size() + " entries: " + suggests);
        }
        return suggests;
    }
    
}
