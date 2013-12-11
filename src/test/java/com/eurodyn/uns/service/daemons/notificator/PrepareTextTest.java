/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Web Questionnaires 2
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Anton Dmitrijev
 */
package com.eurodyn.uns.service.daemons.notificator;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.EventMetadata;
import com.eurodyn.uns.model.NotificationTemplate;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import junit.framework.TestCase;

import java.util.Date;
import java.util.HashMap;

/**
 */
public class PrepareTextTest extends TestCase {

    public static final String SUBSCRIPTION_SECONDARY_ID = "secondary_id";
    public static final String HOME_URL = "http://europe.eu";
    public static final String UNSUBSCRIBE_LINK = HOME_URL + PrepareText.UNSUBSCRIBE_LINK_PATH + SUBSCRIPTION_SECONDARY_ID;
    public static final String UNSUBSCRIBE_HTML_LINK = "<a href=\"" + UNSUBSCRIBE_LINK + "\">" + UNSUBSCRIBE_LINK + "</a>";

    public static final String EVENT_CHANNEL_PLACEHOLDER = "$EVENT.CHANNEL";
    public static final String USER_PLACEHOLDER = "$USER";
    public static final String EVENT_TITLE_PLACEHOLDER = "$EVENT.TITLE";
    public static final String EVENT_DATE_PLACEHOLDER = "$EVENT.DATE";
    public static final String UNSUBSCRIBE_LINK_PLACEHOLDER = "$UNSUBSCRIBE_LINK";
    public static final String UNSUSCRIBE_LINK_MISSPELLED_PLACEHOLDER = "$UNSUSCRIBE_LINK";
    public static final String CHANNEL_INSPECTORS_PLACEHOLDER = "$INSPECTOR_LINK";

    public static final String TEMPLATE = "Important message from " + EVENT_CHANNEL_PLACEHOLDER
            + " (user: " + USER_PLACEHOLDER + ") about " + EVENT_TITLE_PLACEHOLDER
            + " happened at " + EVENT_DATE_PLACEHOLDER
            + ", there is an option to unsubscribe using " + UNSUBSCRIBE_LINK_PLACEHOLDER;

    public static final String TEMPLATE_WITH_MISSPELLED_UNSUBSCRIBE_PLACEHOLDER = "Another important message, " +
            "there is an option to unsubscribe using " + UNSUSCRIBE_LINK_MISSPELLED_PLACEHOLDER;
    public static final String TEMPLATE_WITH_INSPECTOR_LINK = "Inspect other receivers\n" + CHANNEL_INSPECTORS_PLACEHOLDER;
    public static final String EVENT_TITLE = "Title1";
    public static final String TEST_USER_EXTERNAL_ID = "testuser";

    public void test_ifTextContainsUnsubscribePlaceHolder_replaceItWithUnsubscribeLink() throws Exception {
        HashMap prepare = prepareText(TEMPLATE, false);

        assertTrue(prepare.get(PrepareText.PLAIN_TEXT_NOTIFICATION).toString().contains(UNSUBSCRIBE_LINK));
    }

    public void test_ifTextContainsMisspelledUnsubscribePlaceHolder_replaceItWithUnsubscribeLink() throws Exception {
        HashMap prepare = prepareText(TEMPLATE_WITH_MISSPELLED_UNSUBSCRIBE_PLACEHOLDER, false);

        assertTrue(prepare.get(PrepareText.PLAIN_TEXT_NOTIFICATION).toString().contains(UNSUBSCRIBE_LINK));
    }

    public void test_ifUserPrefersHtml_replaceUnsubscribeLinkWithHtml() throws Exception {
        HashMap preparedText = prepareText(TEMPLATE, true);

        assertTrue(preparedText.get(PrepareText.HTML_NOTIFICATION).toString().contains(UNSUBSCRIBE_HTML_LINK));
    }

    public void test_ifUserPrefersHtmlAndUnsubscribePlaceholderMisspelled_replaceUnsubscribeLinkWithHtml() throws Exception {
        HashMap preparedText = prepareText(TEMPLATE_WITH_MISSPELLED_UNSUBSCRIBE_PLACEHOLDER, true);

        assertTrue(preparedText.get(PrepareText.HTML_NOTIFICATION).toString().contains(UNSUBSCRIBE_HTML_LINK));
    }

    public void test_eventTitleIsSelectedByRSSPredicateAndReplacedInTemplate() throws Exception {
        Event event = createEventWithTitleUsingPredicate(PrepareText.TITLE_RSS_PREDICATE);

        HashMap prepareText = prepareText(event);

        assertEventTitleReplaced(prepareText.get(PrepareText.PLAIN_TEXT_NOTIFICATION).toString(), EVENT_TITLE);
    }

    public void test_eventTitleIsSelectedByElementsPredicateAndReplacedInTemplate() throws Exception {
        Event event = createEventWithTitleUsingPredicate(PrepareText.TITLE_ELEMENTS_PREDICATE);

        HashMap prepareText = prepareText(event);

        assertEventTitleReplaced(prepareText.get(PrepareText.PLAIN_TEXT_NOTIFICATION).toString(), EVENT_TITLE);
    }

    public void test_replacesAllSummaryPlaceholdersInPlainTextAndHtmlAndSubject() throws Exception {
        Event event = createEventWithTitleUsingPredicate(PrepareText.TITLE_ELEMENTS_PREDICATE);
        HashMap<String, String> preparedTexts = prepareText(createTemplate(TEMPLATE), event, createSubscription());

        assertNoSummaryPlaceholdersInText(preparedTexts.get(PrepareText.PLAIN_TEXT_NOTIFICATION));
        assertNoSummaryPlaceholdersInText(preparedTexts.get(PrepareText.HTML_NOTIFICATION));
        assertNoSummaryPlaceholdersInText(preparedTexts.get(PrepareText.NOTIFICATION_SUBJECT));
    }

    public void test_ifEventTitleNotFound_replacePlaceholderWithEmptyString() throws Exception {
        Event event = createEvent();
        event.setEventMetadata(new HashMap());

        HashMap prepareText = prepareText(event);

        assertEventTitleReplaced(prepareText.get(PrepareText.PLAIN_TEXT_NOTIFICATION).toString(), "");
    }

    public void test_ifUserDoesNotWantToReceiveHtml_htmlTextWillNotBePrepared() throws Exception {
        HashMap prepareText = prepareText(TEMPLATE, false);

        assertNull(prepareText.get(PrepareText.HTML_NOTIFICATION));
    }

    public void test_noExceptionsIfTemplatePlainTextNotSet() throws Exception {
        NotificationTemplate template = createTemplate(TEMPLATE);
        template.setPlainText(null);

        HashMap prepareText = prepareTextWithHtmlTurnedOn(template);

        assertNull(prepareText.get(PrepareText.PLAIN_TEXT_NOTIFICATION));
    }

    public void test_noExceptionsIfTemplateHtmlTextNotSet() throws Exception {
        NotificationTemplate template = createTemplate(TEMPLATE);
        template.setHtmlText(null);

        HashMap prepareText = prepareTextWithHtmlTurnedOn(template);

        assertNull(prepareText.get(PrepareText.HTML_NOTIFICATION));
    }

    public void test_localNameIsCalculatedBySplittingPredicateBySharpSymbol() throws Exception {
        String localName = PrepareText.getLocalName("test.predicate#localNameBySharp");

        assertEquals("localNameBySharp", localName);
    }

    public void test_ifMultipleSharpSymbols_localNameIsTakenFromSecondToken() throws Exception {
        String localName = PrepareText.getLocalName("test.predicate#localName#BySharp");

        assertEquals("localName", localName);
    }

    public void test_ifNoSharpSymbol_localNameIsCalculatedBySplittingOnForwardSlashSymbol() throws Exception {
        String localName = PrepareText.getLocalName("predicate.test/localNameBySlash");

        assertEquals("localNameBySlash", localName);
    }

    public void test_ifNoSharpOrSlashSymbol_localNameEqualsPredicate() throws Exception {
        String predicate = "predicate.test.localName";
        String localName = PrepareText.getLocalName(predicate);

        assertEquals(predicate, localName);
    }

    public void test_replacesChannelInspectorsPlaceholder() throws Exception {
        NotificationTemplate template = getNotificationTemplateForInspector();

        HashMap<String, String> preparedText = prepareText(template, createEvent(), createSubscription());

        assertFalse(preparedText.get(PrepareText.HTML_NOTIFICATION)
                .contains(CHANNEL_INSPECTORS_PLACEHOLDER));
        assertFalse(preparedText.get(PrepareText.PLAIN_TEXT_NOTIFICATION)
                .contains(CHANNEL_INSPECTORS_PLACEHOLDER));
    }

    public void test_ifNotifiedUserIsChannelInspector_replacePlaceHolderWithALink() throws Exception {
        Subscription subscription = createSubscription();
        subscription.getChannel().setInspectorsCsv(TEST_USER_EXTERNAL_ID + ",otheruser");
        HashMap<String, String> prepareText = prepareText(getNotificationTemplateForInspector(), createEvent(), subscription);

        String plainTextNotification = prepareText.get(PrepareText.PLAIN_TEXT_NOTIFICATION);
        assertFalse(plainTextNotification.contains(CHANNEL_INSPECTORS_PLACEHOLDER));
        assertTrue(plainTextNotification.contains(PrepareText.INSPECTOR_LINK_PATH));
    }

    public void test_ifNotifiedUserIsNotChannelInspector_replacePlaceHolderWithEmptyString() throws Exception {
        HashMap<String, String> prepareText = prepareText(getNotificationTemplateForInspector(), createEvent(), createSubscription());

        String plainTextNotification = prepareText.get(PrepareText.PLAIN_TEXT_NOTIFICATION);
        assertFalse(plainTextNotification.contains(CHANNEL_INSPECTORS_PLACEHOLDER));
        assertFalse(plainTextNotification.contains(PrepareText.INSPECTOR_LINK_PATH));
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
        Subscription subscriptionWithUser = createSubscriptionWithUserAndChannel(createChannel(), createUserAndSetPreferHtmlTo(preferHtml));

        return prepareText(createTemplate(template), createEvent(), subscriptionWithUser);
    }

    private HashMap<String, String> prepareText(NotificationTemplate template, Event event, Subscription subscription) throws Exception {
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
        NotificationTemplate template = createTemplate(TEMPLATE_WITH_INSPECTOR_LINK);
        template.setSubject("Attractive subject about " + EVENT_TITLE_PLACEHOLDER);
        return template;
    }

    private Subscription createSubscription() {
        return createSubscriptionWithUserAndChannel(createChannel(),
                createUserAndSetPreferHtmlTo(true));
    }

    private User createUserAndSetPreferHtmlTo(boolean preferHtml) {
        User user = new User();
        user.setFullName("Test User");
        user.setExternalId(TEST_USER_EXTERNAL_ID);
        user.setPreferHtml(preferHtml);
        return user;
    }

    private NotificationTemplate createTemplate(String template) {
        NotificationTemplate notificationTemplate = new NotificationTemplate();
        notificationTemplate.setSubject(template);
        notificationTemplate.setPlainText(template);
        notificationTemplate.setHtmlText(template);
        return notificationTemplate;
    }

    private Channel createChannel() {
        Channel channel = new Channel();
        channel.setTitle("Title");
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
