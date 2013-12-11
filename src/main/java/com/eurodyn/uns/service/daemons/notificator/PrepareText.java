package com.eurodyn.uns.service.daemons.notificator;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.EventMetadata;
import com.eurodyn.uns.model.NotificationTemplate;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.util.common.WDSLogger;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyStringMap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PrepareText {

    private static final WDSLogger logger = WDSLogger.getLogger(PrepareText.class);
    private static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    public static final String UNSUBSCRIBE_LINK_PATH = "/subscriptions/unsubscribe.jsf?subsc=";
    public static final String INSPECTOR_LINK_PATH = "/admin/reports/notifications_report.jsf?";

    public static final String TITLE_RSS_PREDICATE = "http://purl.org/rss/1.0/title";
    public static final String TITLE_ELEMENTS_PREDICATE = "http://purl.org/dc/elements/1.1/title";
    public static final String PLAIN_TEXT_NOTIFICATION = "plain";
    public static final String HTML_NOTIFICATION = "html";
    public static final String NOTIFICATION_SUBJECT = "subj";

    public static HashMap<String, String> prepare(NotificationTemplate template, Event event, Subscription subscription, String homeUrl) throws Exception {
        try {
            Map event_md = event.getEventMetadata();

            String event_title = findEventTitle(event_md);

            String ev_creation_date = DATE_TIME_FORMATTER.format(event.getCreationDate());

            User user = subscription.getUser();
            Boolean buildHtml = user.getPreferHtml();
            Map<String, String> content = new HashMap<String, String>();
            content.put(PLAIN_TEXT_NOTIFICATION, template.getPlainText());
            if (buildHtml) {
                content.put(HTML_NOTIFICATION, template.getHtmlText());
            }

            //REPLACE common placeholders, create string from metadata.
            String subj = replaceSummaryPlaceholders(template.getSubject(), subscription, event_title, ev_creation_date);

            for (String key : content.keySet()) {
                String text = content.get(key);

                boolean isHtml = key.equals(HTML_NOTIFICATION);
                if (text != null) {
                    text = replaceSummaryPlaceholders(text, subscription, event_title, ev_creation_date);
                    if (text.contains("$UNSUBSCRIBE_LINK") || text.contains("$UNSUSCRIBE_LINK")) {
                        String unsub_link = homeUrl + UNSUBSCRIBE_LINK_PATH + subscription.getSecondaryId();
                        unsub_link = createHtmlLinkIfRequired(isHtml, unsub_link);
                        text = text.replaceAll("\\$UNSUBSCRIBE_LINK", unsub_link);
                        text = text.replaceAll("\\$UNSUSCRIBE_LINK", unsub_link);
                    }
                    String inspectorLink = isChannelInspector(subscription.getUser(), subscription.getChannel())
                            ? createInspectorLink(homeUrl, subj, user.getExternalId(), event.getCreationDate())
                            : "";
                    text = text.replaceAll("\\$INSPECTOR_LINK", inspectorLink);
                    String nl = isHtml ? "<br/>" : "\n";
                    StringBuilder event_body = new StringBuilder();
                    for (Object o : event_md.keySet()) {
                        String event_key = (String) o;
                        EventMetadata em = (EventMetadata) event_md.get(event_key);
                        String prop = em.getProperty();
                        String val = em.getValue();
                        if (val == null) {
                            val = "";
                        }

                        event_body.append(getLocalName(prop)).append(": ");
                        if (isHtml) {
                            val = val.replaceAll("\n", "<br/>");
                            val = createHtmlLinkIfRequired(val.matches("https?://"), val);
                        }
                        event_body.append(val).append(nl);
                    }
                    text = text.replaceAll("\\$EVENT", event_body.toString());
                    content.put(key, text);
                }
            }

            //SETUP of python objects
            PyStringMap pyUser = new PyStringMap();
            pyUser.__setitem__("fullName", new PyString(user.getFullName()));
            pyUser.__setitem__("externalId", new PyString(user.getExternalId()));

            PyStringMap channel = new PyStringMap();
            channel.__setitem__("title", new PyString(subscription.getChannel().getTitle()));

            PyStringMap pysubscription = new PyStringMap();
            pysubscription.__setitem__("channel", channel);
            pysubscription.__setitem__("user", pyUser);

            PyStringMap event_metadata = new PyStringMap();
            PyStringMap metadata_dict = new PyStringMap();
            PyList metadata_list = new PyList();

            Map<String, PyList> multipleMap = new HashMap<String, PyList>();

            for (Object o : event_md.keySet()) {
                String key = (String) o;
                EventMetadata em = (EventMetadata) event_md.get(key);
                String property = em.getProperty();
                String val = em.getValue();
                if (val == null) {
                    val = "";
                }

                event_metadata.__setitem__(property, new PyString(val));
                PyObject dict_val = metadata_dict.get(new PyString(property), new PyString());
                String dval = dict_val.toString() + val;
                if (property != null && isMultiple(property, event_md)) {
                    PyList list = new PyList();
                    if (multipleMap.containsKey(property)) {
                        list = multipleMap.get(property);
                    }
                    if (list != null) {
                        list.add(new PyString(dval));
                        multipleMap.put(property, list);
                    }
                } else {
                    metadata_dict.__setitem__(property, new PyString(dval));
                }
                metadata_list.add(new PyString(dval));
            }
            for (Map.Entry<String, PyList> entry : multipleMap.entrySet()) {
                metadata_dict.__setitem__(entry.getKey(), entry.getValue());
            }

            PyStringMap pyevent = new PyStringMap();
            pyevent.__setitem__("date", new PyString(ev_creation_date));
            pyevent.__setitem__("title", new PyString(event_title));
            pyevent.__setitem__("metadata", event_metadata);

            PyStringMap templ_namespace = new PyStringMap();
            templ_namespace.__setitem__("subscription", pysubscription);
            templ_namespace.__setitem__("event", pyevent);
            templ_namespace.__setitem__("metadata_dict", metadata_dict);
            templ_namespace.__setitem__("metadata_list", metadata_list);

            //EVALUATE python in templates
            HashMap<String, String> ret = new HashMap<String, String>();
            String plain = content.get(PLAIN_TEXT_NOTIFICATION);
            if (plain != null) {
                plain = RenderText.executeTemplate(plain, templ_namespace, false);
                ret.put(PLAIN_TEXT_NOTIFICATION, plain);
            }

            String html = content.get(HTML_NOTIFICATION);
            if (html != null) {
                html = RenderText.executeTemplate(html, templ_namespace, true);
                ret.put(HTML_NOTIFICATION, html);
            }

            ret.put(NOTIFICATION_SUBJECT, subj);
            return ret;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new Exception("Error occured when prepearing notification text: " + e.toString(), e);
        }
    }

    private static boolean isChannelInspector(User user, Channel channel) {
        String inspectorsCsv = channel.getInspectorsCsv();
        if (org.apache.commons.lang.StringUtils.isNotEmpty(inspectorsCsv)) {
            for (String userId : inspectorsCsv.split(",")) {
                if (userId.equals(user.getExternalId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String createHtmlLinkIfRequired(boolean isHtml, String unsub_link) {
        if (isHtml)
            unsub_link = createLink(unsub_link, unsub_link);
        return unsub_link;
    }

    private static String createInspectorLink(String homeUrl, String subject, String user, Date notificationDate) {
        String baseUrl = homeUrl + INSPECTOR_LINK_PATH
                + "subject=" + subject
                + "&user=" + user
                + "&notificationDate=" +
                DATE_FORMAT.format(notificationDate);

        return createLink(baseUrl, "List of other receivers");
    }

    private static String createLink(String link, String text) {
        return "<a href=\"" + link + "\">" + text + "</a>";
    }

    private static String replaceSummaryPlaceholders(String text, Subscription subscription, String eventTitle, String creationDate) {
        text = text.replaceAll("\\$EVENT.DATE", creationDate);
        text = text.replaceAll("\\$USER", subscription.getUser().getFullName());
        text = text.replaceAll("\\$EVENT.TITLE", eventTitle);
        return text.replaceAll("\\$EVENT.CHANNEL", subscription.getChannel().getTitle());
    }

    private static String findEventTitle(Map event_md) {
        for (Object v : event_md.values()) {
            EventMetadata em = (EventMetadata) v;
            String property = em.getProperty();
            if (TITLE_RSS_PREDICATE.equals(property) || TITLE_ELEMENTS_PREDICATE.equals(property)) {
                return em.getValue();
            }
        }
        return "";
    }

    private static boolean isMultiple(String predicate, Map event_md) {
        int i = 0;
        for (Object o : event_md.keySet()) {
            String key = (String) o;
            EventMetadata em = (EventMetadata) event_md.get(key);
            String property = em.getProperty();
            if (property != null && property.equals(predicate)) {
                i = i + 1;
            }
            if (i > 1) {
                return true;
            }
        }
        return false;
    }

    static String getLocalName(String predicate) {
        String[] tokens = predicate.split("#");
        if (tokens.length < 2) {
            tokens = predicate.split("/");
            return tokens[tokens.length - 1];
        } else {
            return tokens[1];
        }
    }
}
