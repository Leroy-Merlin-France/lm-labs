package com.leroymerlin.corp.fr.nuxeo.labs.site.utils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.security.SecurityConstants;

import com.leroymerlin.common.core.security.PermissionsHelper;

public final class LabsSiteConstants {

    public final class Comments {
        public static final String COMMENT_AUTHOR = "comment:author";
        public static final String COMMENT_TEXT = "comment:text";
        public static final String COMMENT_CREATION_DATE = "comment:creationDate";

    }

    public final class AdvancedSearch {
        public static final String LIST_TAGS = "search:coverage";
        public static final String CREATED_MIN = "search:created_min";
        public static final String CREATED_MAX = "search:created_max";
        public static final String MODIFIED_MAX = "search:modified_max";
        public static final String MODIFIED_MIN = "search:modified_min";
        public static final String USER_QUERY = "search:source";
        public static final String LIST_CURRENT_LIFE_CYCLE_STATES = "search:currentLifeCycleStates";

    }

    public enum CommentsState {
        PENDING("", "moderation_pending"), PUBLISHED("moderation_publish",
                "moderation_published"), REJECT("moderation_reject",
                "moderation_rejected");

        private String transition;
        private String state;

        private static final Map<String, CommentsState> stringToEnum = new HashMap<String, CommentsState>();
        static { // Initialize map from constant name to enum constant
            for (CommentsState op : values())
                stringToEnum.put(op.getState(), op);
        }

        // Returns Operation for string, or null if string is invalid
        public static CommentsState fromString(String symbol) {
            return stringToEnum.get(symbol);
        }

        private CommentsState(String transition, String state) {
            this.transition = transition;
            this.state = state;
        }

        /**
         * @return transition name
         */
        public String getTransition() {
            return transition;
        }

        /**
         * @return state name
         */
        public String getState() {
            return state;
        }
    }

    public final class Forum {
        public static final String FORUM_CREATOR = "dc:creator";
    }

    public final class NotifNames {
        public static final String NEWS_PUBLISHED = "News published";
        public static final String PAGE_MODIFIED = "Page modified";
        public static final String PAGE_REMOVED = "Page removed";
        public static final String PAGE_ADDED_COMMENT = "Page added comment";
    }

    public final class EventNames {
        public static final String NEWS_PUBLISHED_UNDER_PAGENEWS = "newsPublishedUnderPageNews";
        @Deprecated
        public static final String CHECK_PUBLISHED_NEWS_TO_NOTIFY = "checkPublishedNewsToNotify";
        public static final String PAGE_MODIFIED = "pageModified";
        public static final String CHECK_PAGES_TO_NOTIFY = "checkPagesToNotify";
        public static final String PAGE_REMOVED = "pageRemoved";
        public static final String PAGE_ADDED_COMMENT = "pageAddedComment";
        public static final String PUBLISHED_SITE = "publishedSite";
        public static final String PUBLISHED_SITE_MAIL = "publishedSiteMail";
        // public static final String URL_SITE_CHANGED = "urlSiteChanged";
    }

    public final class FacetNames {
        public static final String LABSPAGE = "LabsPage";
        public static final String LABSTEMPLATE = "LabsTemplate";
        public static final String LABSTHEME = "LabsTheme";
        public static final String LABSHIDDEN = "LabsHidden";
        public static final String HIDDENINLABSNAVIGATION = "HiddenInLabsNavigation";
        public static final String LABSPAGECUSTOMVIEW = "LabsPageCustomView";
        public static final String LABS_ELEMENT_TEMPLATE = "LabsElementTemplate";
    }

    public enum Schemas {
        PAGE("page", "pg"), LABSTAGS("labstags", "ltg"), PAGENEWS("page_news",
                "pgn"), LABSNEWS("labsnews", "ln"), PAGELIST("page_list", "pgl"), PAGELIST_LINE(
                "page_list_line", "pgll"), PAGEBLOCS("page_blocs", "pgb"), PAGEHTML(
                "page_html", "html"), EXTERNALURL("external_url", "exturl"), LABSTEMPLATE(
                "labstemplate", "labstemplate"), LABSSITE("labssite",
                "labssite"), LABSCONTENTVIEW("labscontentview",
                "labscontentview"), LABS_ELEMENT_TEMPLATE(
                "labs_element_template", "let"), LABS_CATEGORY("labscategory",
                "labscategory"), SITETHEME("sitetheme", "sitetheme");

        private String name;
        private String prefix;

        private Schemas(String name, String prefix) {
            this.name = name;
            this.prefix = prefix;
        }

        /**
         * @return schema name
         */
        public String getName() {
            return name;
        }

        public String prefix() {
            return prefix;
        }
    }

    public enum Docs {
        DEFAULT_DOMAIN("Domain", "default-domain"), SITESROOT("SitesRoot",
                "sites"), SITE("Site", StringUtils.EMPTY), TREE("Tree", "tree"), ASSETS(
                "Assets", "assets"), ASSETS_FORUMS("Assets", "assetsForums"), ASSETS_NEWS(
                "Assets", "assetsNews"), PAGE("Page", StringUtils.EMPTY), PAGEBLOCS(
                "PageBlocs", StringUtils.EMPTY), EXTERNAL_URL("ExternalURL",
                StringUtils.EMPTY), EXTERNAL_URLS("OrderedFolder",
                "externalUrls"), PAGENEWS("PageNews", StringUtils.EMPTY), LABSNEWS(
                "LabsNews", StringUtils.EMPTY), LABSTOPIC("LMForumTopic",
                StringUtils.EMPTY), PAGECLASSEUR("PageClasseur",
                StringUtils.EMPTY), PAGECLASSEURFOLDER("Folder",
                StringUtils.EMPTY), PAGELIST("PageList", StringUtils.EMPTY), PAGELIST_LINE(
                "PageListLine", StringUtils.EMPTY), FOLDER("Folder",
                StringUtils.EMPTY), HTMLPAGE("HtmlPage", StringUtils.EMPTY), DASHBOARD(
                "Space", StringUtils.EMPTY), NOTIFACTIVITIES(
                "NotificationActivities", "notifActivities"), SITETHEMESROOT(
                "Folder", "themes"), SITETHEME("SiteTheme", StringUtils.EMPTY), PAGEFORUM(
                "PageForum", StringUtils.EMPTY), COMMONSASSETS("Assets",
                "commons-assets"), PAGENAV("PageNav", StringUtils.EMPTY), SIDEBAR(
                HTMLPAGE.type(), "sidebar"), WELCOME(HTMLPAGE.type(), "Accueil"); // TODO

        private String docType;
        private String name;

        private static final Map<String, Docs> stringToEnum = new HashMap<String, Docs>();
        static { // Initialize map from constant name to enum constant
            for (Docs op : values())
                if (op != WELCOME && op != SIDEBAR) {
                    stringToEnum.put(op.type(), op);
                }
        }

        // Returns Operation for string, or null if string is invalid
        public static Docs fromString(String symbol) {
            return stringToEnum.get(symbol);
        }

        private Docs(String docType, String name) {
            this.name = name;
            this.docType = docType;
        }

        /**
         * @return document name of document type which can be instantiated only
         *         once otherwise an empty string.
         */
        public String docName() {
            return name;
        }

        /**
         * @return document type.
         */
        public String type() {
            return docType;
        }

        public static EnumSet<Docs> pageDocs() {
            return EnumSet.of(HTMLPAGE, PAGE, DASHBOARD, PAGEBLOCS,
                    PAGECLASSEUR, PAGELIST, PAGENEWS, PAGEFORUM, WELCOME,
                    PAGENAV);
        }

        public static EnumSet<Docs> notifiableDocs() {
            return EnumSet.of(HTMLPAGE, DASHBOARD, PAGECLASSEUR, PAGELIST,
                    PAGENEWS, PAGEFORUM, LABSTOPIC, SITE, PAGENAV);
        }

        public static EnumSet<Docs> labsLifeCycleDocs() {
            return EnumSet.of(SITE, PAGE, PAGEBLOCS, PAGENEWS, PAGECLASSEUR,
                    PAGELIST, HTMLPAGE, PAGEFORUM, DASHBOARD, PAGENAV);
        }
    }

    public enum State {
        DRAFT("draft", "draft"), PUBLISH("publish", "published"), DELETE(
                "delete", "deleted"), UNDELETE("undelete", null);

        private String transition;
        private String state;

        private static final Map<String, State> stringToEnum = new HashMap<String, State>();
        static { // Initialize map from constant name to enum constant
            for (State op : values())
                stringToEnum.put(op.getState(), op);
        }

        // Returns Operation for string, or null if string is invalid
        public static State fromString(String symbol) {
            return stringToEnum.get(symbol);
        }

        private State(String transition, String state) {
            this.transition = transition;
            this.state = state;
        }

        /**
         * @return transition name
         */
        public String getTransition() {
            return transition;
        }

        /**
         * @return state name
         */
        public String getState() {
            return state;
        }

    }

    public enum Sidebar {
        LOGO("logo"), NOTIFICATION("notification"), SITEMAP("siteMap"), TOPPAGE(
                "topPage"), SUBPAGE("subPage"), LAST_ACTIVITIES(
                "lastActivities"), EXTERNAL_URL("externalUrl"), LAST_UPLOAD(
                "lastUpload");

        private String name;

        private static final List<String> stringToEnum = new ArrayList<String>();
        static { // Initialize map from constant name to enum constant
            for (Sidebar op : values())
                stringToEnum.add(op.getName());
        }

        // Returns Operation for string, or null if string is invalid
        public static String fromString(String symbol) {
            return stringToEnum.get(stringToEnum.indexOf(symbol));
        }

        private Sidebar(String name) {
            this.name = name;
        }

        /**
         * @return state name
         */
        public String getName() {
            return name;
        }

    }

    /*
     * Define in the file 'lifecycle-contrib.xml'
     */
    public static String LIFE_CYCLE_LABS = "labs";

    public static List<String> OREDERED_PERMISSONS;

    public static PermissionsHelper PERMISSIONS_HELPER;

    static {
        OREDERED_PERMISSONS = new ArrayList<String>();
        OREDERED_PERMISSONS.add(SecurityConstants.EVERYTHING);
        OREDERED_PERMISSONS.add(SecurityConstants.READ_WRITE);
        OREDERED_PERMISSONS.add(SecurityConstants.READ);
        PERMISSIONS_HELPER = new PermissionsHelper(OREDERED_PERMISSONS);
    }

    public enum Rights {

        EVERYTHING(SecurityConstants.EVERYTHING), WRITE(
                SecurityConstants.READ_WRITE), READ(SecurityConstants.READ);

        private String right;

        Rights(String right) {
            this.right = right;
        }

        public String getRight() {
            return right;
        }
    }

    public enum PropertyType {

        COLOR("color"), FONT("font"), SIZE("size"), STRING("string"), IMAGE(
                "image"), BOOL("boolean"), VOID("void");

        private String type;

        private static final Map<String, PropertyType> stringToEnum = new HashMap<String, PropertyType>();
        static { // Initialize map from constant name to enum constant
            for (PropertyType op : values())
                stringToEnum.put(op.toString(), op);
        }

        // Returns Operation for string, or null if string is invalid
        public static PropertyType fromString(String symbol) {
            return stringToEnum.get(symbol.toLowerCase());
        }

        PropertyType(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }

    public enum Directories {
        COLUMNS_LAYOUT("columns_layout", "columns_layout", "code", "", "order"), PAGE_TEMPLATES(
                "labs_page_templates", "vocabulary", "id", "label", "ordering"), THEMES(
                "labs_themes", "vocabulary", "id", "label", "ordering"), FONT_SIZES(
                "labs_fontsizes", "vocabulary", "id", "label", "ordering"), PAGE_CONTENTVIEWS(
                "labs_HtmlPage_contentviews", "vocabulary", "id", "label",
                "ordering"), PAGE_WIDGETS("labs_HtmlPage_widgets",
                "labshtmlpagewidgets", "code", "", "ordering"), SIDEBAR_WIDGETS(
                "labs_sidebar_widgets", "labshtmlpagewidgets", "code", "",
                "ordering"), PAGE_WIDGETGROUPS("labs_HtmlPage_widgetGroups",
                "vocabulary", "id", "label", "ordering"), FONT_FAMILIES(
                "labs_fontfamilies", "vocabulary", "id", "label", "label"), USER_STYLE(
                "labs_userStyle", "vocabulary", "id", "label", "ordering"), CATEGORY(
                "labs_category", "vocabulary", "id", "label", "ordering"), NOTIFICATION(
                "labs_notification", "vocabulary", "id", "label", "ordering");

        private String dirName;
        private String schema;
        private String idField;
        private String labelField;
        private String orderingField;

        private static final Map<String, Directories> stringToEnum = new HashMap<String, Directories>();
        static {
            for (Directories op : values())
                stringToEnum.put(op.dirName(), op);
        }

        public static Directories fromString(String symbol) {
            return stringToEnum.get(symbol);
        }

        private Directories(String name, String prefix, String idField,
                String labelField, String orderingField) {
            this.dirName = name;
            this.schema = prefix;
            this.idField = idField;
            this.labelField = labelField;
            this.orderingField = orderingField;
        }

        /**
         * @return directory name
         */
        public String dirName() {
            return dirName;
        }

        /**
         * @return schema name
         */
        public String schema() {
            return schema;
        }

        public String idField() {
            return idField;
        }

        public String labelField() {
            return labelField;
        }

        public String orderingField() {
            return orderingField;
        }
    }

    private LabsSiteConstants() {
    }

}
