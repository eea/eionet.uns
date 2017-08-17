package com.eurodyn.uns.service.daemons.notificator;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import com.eurodyn.uns.ApplicationTestContext;
import junit.framework.TestCase;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.EventMetadata;
import com.eurodyn.uns.model.NotificationTemplate;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class PrepareTextTest {

    public static final String SUBSCRIPTION_SECONDARY_ID = "secondary_id";
    public static final String HOME_URL = "http://europe.eu";
    public static final String UNSUBSCRIBE_LINK = HOME_URL + PrepareText.UNSUBSCRIBE_LINK_PATH + SUBSCRIPTION_SECONDARY_ID;
    public static final String UNSUBSCRIBE_HTML_LINK = "<a href=\"" + UNSUBSCRIBE_LINK + "\">" + UNSUBSCRIBE_LINK + "</a>";

    public static final String EVENT_PLACEHOLDER = "$EVENT";
    public static final String EVENT_CHANNEL_PLACEHOLDER = "$EVENT.CHANNEL";
    public static final String USER_PLACEHOLDER = "$USER";
    public static final String EVENT_TITLE_PLACEHOLDER = "$EVENT.TITLE";
    public static final String EVENT_DATE_PLACEHOLDER = "$EVENT.DATE";
    public static final String UNSUBSCRIBE_LINK_PLACEHOLDER = "$UNSUBSCRIBE_LINK";
    public static final String UNSUSCRIBE_LINK_MISSPELLED_PLACEHOLDER = "$UNSUSCRIBE_LINK";
    public static final String CHANNEL_INSPECTORS_PLACEHOLDER = "$INSPECTOR_LINK";

    public static final String TEMPLATE = "Important message from " + EVENT_CHANNEL_PLACEHOLDER + " (user: " + USER_PLACEHOLDER
            + ") about " + EVENT_TITLE_PLACEHOLDER + " happened at " + EVENT_DATE_PLACEHOLDER
            + ", there is an option to unsubscribe using " + UNSUBSCRIBE_LINK_PLACEHOLDER;
    public static final String TEMPLATE_WITH_MISSPELLED_UNSUBSCRIBE_PLACEHOLDER = "Another important message, "
            + "there is an option to unsubscribe using " + UNSUSCRIBE_LINK_MISSPELLED_PLACEHOLDER;
    public static final String TEMPLATE_WITH_INSPECTOR_LINK = "Inspect other receivers\n" + CHANNEL_INSPECTORS_PLACEHOLDER;

    public static final String EVENT_TITLE = "Event title";
    public static final String TEST_USER_EXTERNAL_ID = "testuser";
    public static final String DEFAULT_CHANNEL_TITLE = "Channel title";
    public static final String DEFAULT_USER_FULL_NAME = "Test User";

    @Test
    public void test_ifTextContainsUnsubscribePlaceHolder_replaceItWithUnsubscribeLink() throws Exception {
        HashMap prepare = prepareText(TEMPLATE, false);

        assertTrue(prepare.get(PrepareText.PLAIN_TEXT_NOTIFICATION).toString().contains(UNSUBSCRIBE_LINK));
    }

    @Test
    public void test_ifTextContainsMisspelledUnsubscribePlaceHolder_replaceItWithUnsubscribeLink() throws Exception {
        HashMap prepare = prepareText(TEMPLATE_WITH_MISSPELLED_UNSUBSCRIBE_PLACEHOLDER, false);

        assertTrue(prepare.get(PrepareText.PLAIN_TEXT_NOTIFICATION).toString().contains(UNSUBSCRIBE_LINK));
    }

    @Test
    public void test_ifUserPrefersHtml_replaceUnsubscribeLinkWithHtml() throws Exception {
        HashMap preparedText = prepareText(TEMPLATE, true);

        assertTrue(preparedText.get(PrepareText.HTML_NOTIFICATION).toString().contains(UNSUBSCRIBE_HTML_LINK));
    }

    @Test
    public void test_ifUserPrefersHtmlAndUnsubscribePlaceholderMisspelled_replaceUnsubscribeLinkWithHtml() throws Exception {
        HashMap preparedText = prepareText(TEMPLATE_WITH_MISSPELLED_UNSUBSCRIBE_PLACEHOLDER, true);

        assertTrue(preparedText.get(PrepareText.HTML_NOTIFICATION).toString().contains(UNSUBSCRIBE_HTML_LINK));
    }

    @Test
    public void test_eventTitleIsSelectedByRSSPredicateAndReplacedInTemplate() throws Exception {
        Event event = createEventWithTitleUsingPredicate(PrepareText.TITLE_RSS_PREDICATE);

        HashMap prepareText = prepareText(event);

        assertEventTitleReplaced(prepareText.get(PrepareText.PLAIN_TEXT_NOTIFICATION).toString(), EVENT_TITLE);
    }

    @Test
    public void test_eventTitleIsSelectedByElementsPredicateAndReplacedInTemplate() throws Exception {
        Event event = createEventWithTitleUsingPredicate(PrepareText.TITLE_ELEMENTS_PREDICATE);

        HashMap prepareText = prepareText(event);

        assertEventTitleReplaced(prepareText.get(PrepareText.PLAIN_TEXT_NOTIFICATION).toString(), EVENT_TITLE);
    }

    @Test
    public void test_replacesAllSummaryPlaceholdersInPlainTextAndHtmlAndSubject() throws Exception {
        Event event = createEventWithTitleUsingPredicate(PrepareText.TITLE_ELEMENTS_PREDICATE);
        HashMap<String, String> preparedTexts = prepareText(createTemplate(TEMPLATE), event, createSubscription());

        assertNoSummaryPlaceholdersInText(preparedTexts.get(PrepareText.PLAIN_TEXT_NOTIFICATION));
        assertNoSummaryPlaceholdersInText(preparedTexts.get(PrepareText.HTML_NOTIFICATION));
        assertNoSummaryPlaceholdersInText(preparedTexts.get(PrepareText.NOTIFICATION_SUBJECT));
    }

    @Test
    public void test_ifEventTitleNotFound_replacePlaceholderWithEmptyString() throws Exception {
        Event event = createEvent();
        event.setEventMetadata(new HashMap());

        HashMap prepareText = prepareText(event);

        assertEventTitleReplaced(prepareText.get(PrepareText.PLAIN_TEXT_NOTIFICATION).toString(), "");
    }

    @Test
    public void test_ifUserDoesNotWantToReceiveHtml_htmlTextWillNotBePrepared() throws Exception {
        HashMap prepareText = prepareText(TEMPLATE, false);

        assertNull(prepareText.get(PrepareText.HTML_NOTIFICATION));
    }

    @Test
    public void test_noExceptionsIfTemplatePlainTextNotSet() throws Exception {
        NotificationTemplate template = createTemplate(TEMPLATE);
        template.setPlainText(null);

        HashMap prepareText = prepareTextWithHtmlTurnedOn(template);

        assertNull(prepareText.get(PrepareText.PLAIN_TEXT_NOTIFICATION));
    }

    @Test
    public void test_noExceptionsIfTemplateHtmlTextNotSet() throws Exception {
        NotificationTemplate template = createTemplate(TEMPLATE);
        template.setHtmlText(null);

        HashMap prepareText = prepareTextWithHtmlTurnedOn(template);

        assertNull(prepareText.get(PrepareText.HTML_NOTIFICATION));
    }

    @Test
    public void test_localNameIsCalculatedBySplittingPredicateBySharpSymbol() throws Exception {
        String localName = PrepareText.getLocalName("test.predicate#localNameBySharp");

        assertEquals("localNameBySharp", localName);
    }

    @Test
    public void test_ifMultipleSharpSymbols_localNameIsTakenFromSecondToken() throws Exception {
        String localName = PrepareText.getLocalName("test.predicate#localName#BySharp");

        assertEquals("localName", localName);
    }

    @Test
    public void test_ifNoSharpSymbol_localNameIsCalculatedBySplittingOnForwardSlashSymbol() throws Exception {
        String localName = PrepareText.getLocalName("predicate.test/localNameBySlash");

        assertEquals("localNameBySlash", localName);
    }

    @Test
    public void test_ifNoSharpOrSlashSymbol_localNameEqualsPredicate() throws Exception {
        String predicate = "predicate.test.localName";
        String localName = PrepareText.getLocalName(predicate);

        assertEquals(predicate, localName);
    }

    @Test
    public void test_replacesChannelInspectorsPlaceholder() throws Exception {
        NotificationTemplate template = getNotificationTemplateForInspector();

        HashMap<String, String> preparedText = prepareText(template, createEvent(), createSubscription());

        assertFalse(preparedText.get(PrepareText.HTML_NOTIFICATION).contains(CHANNEL_INSPECTORS_PLACEHOLDER));
        assertFalse(preparedText.get(PrepareText.PLAIN_TEXT_NOTIFICATION).contains(CHANNEL_INSPECTORS_PLACEHOLDER));
    }

    @Test
    public void test_ifNotifiedUserIsChannelInspector_replacePlaceHolderWithALink() throws Exception {
        Subscription subscription = createSubscription();
        subscription.getChannel().setInspectorsCsv(TEST_USER_EXTERNAL_ID + ",otheruser");
        HashMap<String, String> prepareText = prepareText(getNotificationTemplateForInspector(), createEvent(), subscription);

        String plainTextNotification = prepareText.get(PrepareText.PLAIN_TEXT_NOTIFICATION);
        assertFalse(plainTextNotification.contains(CHANNEL_INSPECTORS_PLACEHOLDER));
        assertTrue(plainTextNotification.contains(PrepareText.INSPECTOR_LINK_PATH));
    }

    @Test
    public void test_ifNotifiedUserIsNotChannelInspector_replacePlaceHolderWithEmptyString() throws Exception {
        HashMap<String, String> prepareText =
                prepareText(getNotificationTemplateForInspector(), createEvent(), createSubscription());

        String plainTextNotification = prepareText.get(PrepareText.PLAIN_TEXT_NOTIFICATION);
        assertFalse(plainTextNotification.contains(CHANNEL_INSPECTORS_PLACEHOLDER));
        assertFalse(plainTextNotification.contains(PrepareText.INSPECTOR_LINK_PATH));
    }

    @Test
    public void test_placeholdersInSubjectShouldBeSubstitutedWithRightData() throws Exception {
        NotificationTemplate template = new NotificationTemplate();
        template.setSubject(subjectTemplate(EVENT_CHANNEL_PLACEHOLDER, EVENT_DATE_PLACEHOLDER, EVENT_TITLE_PLACEHOLDER,
                USER_PLACEHOLDER));
        Event event = createEventWithTitleUsingPredicate(PrepareText.TITLE_RSS_PREDICATE);
        event.setCreationDate(defaultDate());

        HashMap<String, String> prepareText = prepareText(template, event, createSubscription());

        // Since the tested code uses 3-letter months, ensure we set up the expected date in the same locale with tested code.
        Date expectedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("0006-05-04 01:02:03");
        String expectedDateStr = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss").format(expectedDate);
        String subjectTemplate = subjectTemplate(DEFAULT_CHANNEL_TITLE, expectedDateStr, EVENT_TITLE, DEFAULT_USER_FULL_NAME);
        String notificationSubject = prepareText.get(PrepareText.NOTIFICATION_SUBJECT);
        assertEquals(subjectTemplate, notificationSubject);
    }

    @Test
    public void test_createsTextFromEventsMetadata() throws Exception {
        Event event = createEvent();
        Map<String, EventMetadata> eventMetadata = new LinkedHashMap<String, EventMetadata>();
        eventMetadata.put("key1", new EventMetadata("property1", "value1"));
        eventMetadata.put("key2", new EventMetadata("property2", "value2"));
        eventMetadata.put("key3", new EventMetadata("property3", "value3"));
        event.setEventMetadata(eventMetadata);

        HashMap<String, String> prepareText =
                prepareText(createTemplate("Subject", EVENT_PLACEHOLDER), event, createSubscription());

        assertEquals("property1: value1\n" + "property2: value2\n" + "property3: value3\n",
                prepareText.get(PrepareText.PLAIN_TEXT_NOTIFICATION));

        assertEquals("property1: value1<br/>" + "property2: value2<br/>" + "property3: value3<br/>",
                prepareText.get(PrepareText.HTML_NOTIFICATION));
    }

    @Test
    public void test_placeholdersInPlainTextAndHtmlShouldBeSubstitutedWithRightData() throws Exception {
        String templatePrefix = "Text with all placeholders: ";
        String subject = "Notification subject";
        NotificationTemplate template =
                createTemplate(subject, templatePrefix + EVENT_PLACEHOLDER + "," + EVENT_CHANNEL_PLACEHOLDER + ","
                        + USER_PLACEHOLDER + "," + EVENT_TITLE_PLACEHOLDER + "," + EVENT_DATE_PLACEHOLDER + ","
                        + UNSUBSCRIBE_LINK_PLACEHOLDER + "," + UNSUSCRIBE_LINK_MISSPELLED_PLACEHOLDER + ","
                        + CHANNEL_INSPECTORS_PLACEHOLDER);

        Event event = createEventWithTitleUsingPredicate(PrepareText.TITLE_RSS_PREDICATE);
        event.setCreationDate(defaultDate());

        Subscription subscription = createSubscription();
        subscription.getChannel().setInspectorsCsv(TEST_USER_EXTERNAL_ID);
        HashMap<String, String> prepareText = prepareText(template, event, subscription);

        String inspectorUrl =
                HOME_URL + PrepareText.INSPECTOR_LINK_PATH + "subject=" + URLEncoder.encode(subject, "UTF-8")
                        + "&user=&notificationDate=04-05-0006";

        // Since the tested code uses 3-letter months, ensure we set up the expected date in the same locale with tested code.
        Date expectedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("0006-05-04 01:02:03");
        String expectedDateStr = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss").format(expectedDate);

        String expected =
                templatePrefix + "title: " + EVENT_TITLE + "\n" + "," + DEFAULT_CHANNEL_TITLE + "," + DEFAULT_USER_FULL_NAME + ","
                        + EVENT_TITLE + "," + expectedDateStr + "," + UNSUBSCRIBE_LINK + "," + UNSUBSCRIBE_LINK + ","
                        + PrepareText.INSPECTOR_LINK_TEXT + ": " + inspectorUrl;
        String actual = prepareText.get(PrepareText.PLAIN_TEXT_NOTIFICATION);
        assertEquals(expected, actual);

        assertEquals(templatePrefix + "title: " + EVENT_TITLE + "<br/>" + "," + DEFAULT_CHANNEL_TITLE + ","
                + DEFAULT_USER_FULL_NAME + "," + EVENT_TITLE + "," + expectedDateStr + "," + UNSUBSCRIBE_HTML_LINK + ","
                + UNSUBSCRIBE_HTML_LINK + ",<a href=\"" + inspectorUrl + "\">" + PrepareText.INSPECTOR_LINK_TEXT + "</a>",
                prepareText.get(PrepareText.HTML_NOTIFICATION));
    }

    private Date defaultDate() throws ParseException {
        return new SimpleDateFormat("hh:mm:ss dd-MM-yyyy").parse("01:02:03 04-05-0006");
    }

    private String subjectTemplate(String eventChannel, String eventDate, String eventTitle, String user) {
        return "Subject with all available placeholders:" + eventChannel + "," + eventDate + "," + eventTitle + "," + user;
    }

    private HashMap<String, String> prepareText(Event event) throws Exception {
        return prepareText(createTemplate(TEMPLATE), event,
                createSubscriptionWithUserAndChannel(createChannel(), createUserAndSetPreferHtmlTo(false)));
    }

    private HashMap<String, String> prepareTextWithHtmlTurnedOn(NotificationTemplate template) throws Exception {
        return prepareText(template, createEvent(),
                createSubscriptionWithUserAndChannel(createChannel(), createUserAndSetPreferHtmlTo(true)));
    }

    private HashMap<String, String> prepareText(String template, boolean preferHtml) throws Exception {
        Subscription subscriptionWithUser =
                createSubscriptionWithUserAndChannel(createChannel(), createUserAndSetPreferHtmlTo(preferHtml));

        return prepareText(createTemplate(template), createEvent(), subscriptionWithUser);
    }

    private HashMap<String, String> prepareText(NotificationTemplate template, Event event, Subscription subscription)
            throws Exception {
        return PrepareText.prepare(template, event, subscription, HOME_URL);
    }

    private void assertEventTitleReplaced(String plainTextNotification, String eventTitle) {
        assertTrue(!plainTextNotification.contains(EVENT_TITLE_PLACEHOLDER));
        assertTrue(plainTextNotification.contains(eventTitle));
    }

    private void assertNoSummaryPlaceholdersInText(String text) {
        assertTrue(!text.contains(EVENT_CHANNEL_PLACEHOLDER));
        assertTrue(!text.contains(EVENT_DATE_PLACEHOLDER));
        assertTrue(!text.contains(EVENT_TITLE_PLACEHOLDER));
        assertTrue(!text.contains(USER_PLACEHOLDER));
    }

    private NotificationTemplate getNotificationTemplateForInspector() {
        return createTemplate("Attractive subject about " + EVENT_TITLE_PLACEHOLDER, TEMPLATE_WITH_INSPECTOR_LINK);
    }

    private Subscription createSubscription() {
        return createSubscriptionWithUserAndChannel(createChannel(), createUserAndSetPreferHtmlTo(true));
    }

    private User createUserAndSetPreferHtmlTo(boolean preferHtml) {
        User user = new User();
        user.setFullName(DEFAULT_USER_FULL_NAME);
        user.setExternalId(TEST_USER_EXTERNAL_ID);
        user.setPreferHtml(preferHtml);
        return user;
    }

    private NotificationTemplate createTemplate(String template) {
        return createTemplate(template, template);
    }

    private NotificationTemplate createTemplate(String subject, String template) {
        NotificationTemplate notificationTemplate = new NotificationTemplate();
        notificationTemplate.setSubject(subject);
        notificationTemplate.setPlainText(template);
        notificationTemplate.setHtmlText(template);
        return notificationTemplate;
    }

    private Channel createChannel() {
        Channel channel = new Channel();
        channel.setTitle(DEFAULT_CHANNEL_TITLE);
        return channel;
    }

    private Subscription createSubscriptionWithUserAndChannel(Channel channel, User user) {
        Subscription subscription = new Subscription();
        subscription.setSecondaryId(SUBSCRIPTION_SECONDARY_ID);
        subscription.setUser(user);
        subscription.setChannel(channel);
        return subscription;
    }

    private Event createEvent() {
        Event event = new Event();
        event.setCreationDate(new Date());
        return event;
    }

    private Event createEventWithTitleUsingPredicate(String predicate) {
        Event event = createEvent();
        HashMap eventMetadata = new HashMap();
        EventMetadata metadata = new EventMetadata();
        metadata.setProperty(predicate);
        metadata.setValue(EVENT_TITLE);
        eventMetadata.put("whatever_key", metadata);
        event.setEventMetadata(eventMetadata);

        return event;
    }
}
