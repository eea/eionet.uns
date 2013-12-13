package com.eurodyn.uns.service.daemons.notificator;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyStringMap;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.EventMetadata;
import com.eurodyn.uns.model.NotificationTemplate;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.util.common.WDSLogger;

public class PrepareText {
    private static final WDSLogger logger = WDSLogger.getLogger(PrepareText.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    public static final String UNSUBSCRIBE_LINK_PATH = "/subscriptions/unsubscribe.jsf?subsc=";
    public static final String INSPECTOR_LINK_PATH = "/admin/reports/notifications_report.jsf?";

    public static final String TITLE_RSS_PREDICATE = "http://purl.org/rss/1.0/title";
    public static final String TITLE_ELEMENTS_PREDICATE = "http://purl.org/dc/elements/1.1/title";
    public static final String PLAIN_TEXT_NOTIFICATION = "plain";
    public static final String HTML_NOTIFICATION = "html";
    public static final String NOTIFICATION_SUBJECT = "subj";
    public static final String INSPECTOR_LINK_TEXT = "List of other receivers";

    public static HashMap<String, String> prepare(NotificationTemplate template, Event event, Subscription subscription, String homeUrl) throws Exception {
        try {
            PrepareTextContext context = new PrepareTextContext(template, event, subscription, homeUrl);

            String subject = replaceSummaryPlaceholders(template.getSubject(), context);
            String plainTextNotification = replacePlaceHoldersInTemplate(template.getPlainText(), subject, false, context);
            String htmlNotification = context.buildHtml
                    ? replacePlaceHoldersInTemplate(template.getHtmlText(), subject, true, context)
                    : null;

            PyStringMap pythonNamespace = setupPythonNamespace(context);

            HashMap<String, String> ret = new HashMap<String, String>();
            if (plainTextNotification != null) {
                ret.put(PLAIN_TEXT_NOTIFICATION, RenderText.executeTemplate(plainTextNotification, pythonNamespace, false));
            }
            if (htmlNotification != null) {
                ret.put(HTML_NOTIFICATION, RenderText.executeTemplate(htmlNotification, pythonNamespace, true));
            }

            ret.put(NOTIFICATION_SUBJECT, subject);
            return ret;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new Exception("Error occurred when preparing notification text: " + e.toString(), e);
        }
    }

    private static String replacePlaceHoldersInTemplate(String template, String subject,
                                                        boolean isHtml, PrepareTextContext context) {
        if (template != null) {
            template = replaceSummaryPlaceholders(template, context);
            template = template.replaceAll("(\\$UNSUBSCRIBE_LINK)|(\\$UNSUSCRIBE_LINK)",
                        createUnsubscribeLink(isHtml, context));
            String inspectorLink = isChannelInspector(context.user, context.subscription.getChannel())
                    ? createInspectorLink(subject, isHtml, context) : "";
            template = template.replaceAll("\\$INSPECTOR_LINK", inspectorLink);
            String newLineSeparator = isHtml ? "<br/>" : "\n";
            StringBuilder event_body = new StringBuilder();
            for (EventMetadata em : context.eventMetadata.values()) {
                String value = StringUtils.defaultString(em.getValue());

                event_body.append(getLocalName(em.getProperty())).append(": ");
                if (isHtml) {
                    value = value.replaceAll("\n", "<br/>");
                    value = createHtmlLinkIfRequired(value.matches("https?://"), value);
                }
                event_body.append(value).append(newLineSeparator);
            }
            template = template.replaceAll("\\$EVENT", event_body.toString());
        }
        return template;
    }

    private static String createUnsubscribeLink(boolean isHtml, PrepareTextContext context) {
        String unsub_link = context.homeUrl + UNSUBSCRIBE_LINK_PATH + context.subscription.getSecondaryId();
        unsub_link = createHtmlLinkIfRequired(isHtml, unsub_link);
        return unsub_link;
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

    private static String createHtmlLinkIfRequired(boolean isHtml, String link) {
        return isHtml ? createLink(link, link) : link;
    }

    private static String createInspectorLink(String subject, boolean isHtml, PrepareTextContext context) {

        String link = "";
        try {
            link = context.homeUrl + INSPECTOR_LINK_PATH
                    + "subject=" + URLEncoder.encode(subject, "UTF-8")
                    + "&user=" + URLEncoder.encode(context.user.getExternalId(), "UTF-8")
                    + "&notificationDate=" +
                    URLEncoder.encode(DATE_FORMAT.format(context.event.getCreationDate()), "UTF-8");

            return isHtml ? createLink(link, INSPECTOR_LINK_TEXT) : INSPECTOR_LINK_TEXT + ": " + link;

        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }

        return link;
    }

    private static String createLink(String link, String text) {
        return "<a href=\"" + link + "\">" + text + "</a>";
    }

    private static String replaceSummaryPlaceholders(String text, PrepareTextContext context) {
        text = text.replaceAll("\\$EVENT.DATE", context.eventCreationDate);
        text = text.replaceAll("\\$USER", context.user.getFullName());
        text = text.replaceAll("\\$EVENT.TITLE", context.eventTitle);
        return text.replaceAll("\\$EVENT.CHANNEL", context.subscription.getChannel().getTitle());
    }

    private static PyStringMap setupPythonNamespace(PrepareTextContext context) {
        PyStringMap pyUser = new PyStringMap();
        pyUser.__setitem__("fullName", new PyString(context.user.getFullName()));
        pyUser.__setitem__("externalId", new PyString(context.user.getExternalId()));

        PyStringMap channel = new PyStringMap();
        channel.__setitem__("title", new PyString(context.subscription.getChannel().getTitle()));

        PyStringMap pysubscription = new PyStringMap();
        pysubscription.__setitem__("channel", channel);
        pysubscription.__setitem__("user", pyUser);

        PyStringMap event_metadata = new PyStringMap();
        PyStringMap metadata_dict = new PyStringMap();
        PyList metadata_list = new PyList();

        Map<String, PyList> multipleMap = new HashMap<String, PyList>();

        for (EventMetadata em : context.eventMetadata.values()) {
            String property = em.getProperty();
            String val = StringUtils.defaultString(em.getValue());

            event_metadata.__setitem__(property, new PyString(val));
            PyObject dict_val = metadata_dict.get(new PyString(property), new PyString());
            String dval = dict_val.toString() + val;
            if (property != null && isMultiple(property, context.eventMetadata)) {
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
        pyevent.__setitem__("date", new PyString(context.eventCreationDate));
        pyevent.__setitem__("title", new PyString(context.eventTitle));
        pyevent.__setitem__("metadata", event_metadata);

        PyStringMap templ_namespace = new PyStringMap();
        templ_namespace.__setitem__("subscription", pysubscription);
        templ_namespace.__setitem__("event", pyevent);
        templ_namespace.__setitem__("metadata_dict", metadata_dict);
        templ_namespace.__setitem__("metadata_list", metadata_list);
        return templ_namespace;
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

    private static class PrepareTextContext {
        private static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        final NotificationTemplate template;
        final Event event;
        final Subscription subscription;
        final String homeUrl;
        final String eventCreationDate;
        final Map<String, EventMetadata> eventMetadata;
        final String eventTitle;
        final User user;
        final boolean buildHtml;

        @SuppressWarnings("unchecked")
        private PrepareTextContext(NotificationTemplate template, Event event, Subscription subscription, String homeUrl) {
            this.template = template;
            this.event = event;
            this.subscription = subscription;
            this.homeUrl = homeUrl;
            this.eventCreationDate = DATE_TIME_FORMATTER.format(event.getCreationDate());
            eventMetadata = event.getEventMetadata();
            eventTitle = findEventTitle(eventMetadata);
            user = subscription.getUser();
            buildHtml = user.getPreferHtml();
        }

        private String findEventTitle(Map<String, EventMetadata> metadata) {
            for (EventMetadata em : metadata.values()) {
                String property = em.getProperty();
                if (TITLE_RSS_PREDICATE.equals(property) || TITLE_ELEMENTS_PREDICATE.equals(property)) {
                    return em.getValue();
                }
            }
            return "";
        }
    }
}
