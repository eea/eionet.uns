package com.eurodyn.uns.web.jsf.admin.templates;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.python.core.Py;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.EventMetadata;
import com.eurodyn.uns.model.NotificationTemplate;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.util.common.AppConfigurator;
import com.eurodyn.uns.util.common.WDSLogger;

public class NotificationTemplateInterpreter {

	private static final WDSLogger logger = WDSLogger.getLogger(NotificationTemplateInterpreter.class);

	private static final String[] python_initialization = { "from NotifTest.ERA.Subscription import Subscription", "from NotifTest.ERA.Channel import Channel", "from NotifTest.ERA.EEAUser import EEAUser", "from NotifTest.ERA.Event import Event", "from NotifTest.embedded_code import executeTestTemplate", "event = Event()", "channel = Channel()", "subscription = Subscription()", "user = EEAUser()", "creator = EEAUser()", "templ_namespace = {}", "errorMessage = ''" };

	protected static String jython_home;

	protected static String python_source;

	static {
		try {
			jython_home = AppConfigurator.getInstance().getBoundle("uns").getString("jython_home");
			python_source = AppConfigurator.getInstance().getBoundle("uns").getString("python_source");
			System.setProperty("python.home", jython_home);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

		}

	}

	public Map pyhtonInterpreter(NotificationTemplate notificationTemplate, Event event, User user, String unsubscribeLink) {

		String plainNotificationText = notificationTemplate.getPlainText();
		String htmlNotifcationText = notificationTemplate.getHtmlText();
		Map eventMetadata = event.getEventMetadata();

		if (plainNotificationText != null)
			plainNotificationText = replacePlaceholders(plainNotificationText, event, user, unsubscribeLink, false);
		if (htmlNotifcationText != null)
			htmlNotifcationText = replacePlaceholders(htmlNotifcationText, event, user, unsubscribeLink, true);

		PythonInterpreter interp = new PythonInterpreter(null, new PySystemState());
		PySystemState sys = Py.getSystemState();
		sys.path.append(new PyString(python_source));

		for (int i = 0; i < python_initialization.length; i++) {
			interp.exec(python_initialization[i]);
		}

		interp.set("channel_j", (Object) event.getChannel());
		interp.set("user_j", (Object) user);
		interp.exec("event['metadata'] = {}");
		for (Iterator iter = eventMetadata.values().iterator(); iter.hasNext();) {
			EventMetadata element_j = (EventMetadata) iter.next();
			interp.set("element_j", element_j);
			interp.exec("event['metadata'][element_j.getProperty()]= element_j.getValue()");
		}
		interp.exec("user['fullName'] = user_j.getFullName() ");
		interp.exec("user['externalId'] = user_j.getExternalId() ");
		interp.exec("creator['fullName'] = channel_j.getCreator().getFullName()");
		interp.exec("channel['creator'] = creator ");
		interp.exec("channel['title'] = channel_j.getTitle() ");
		interp.exec("subscription['channel'] = channel ");
		interp.exec("subscription['user'] = user ");
		interp.exec("templ_namespace['subscription'] = subscription");
		interp.exec("templ_namespace['event'] = event");

		interp.set("templateText_j", (Object) plainNotificationText);
		interp.exec("executionResult = executeTestTemplate(templateText_j, templ_namespace,False)");
		String executionResult = interp.get("executionResult").toString();
		//String resultText = executionResult.trim().replaceAll("\n", "<br\\>");
		String resultText = executionResult.trim();		

		interp.set("templateHtml_j", (Object) htmlNotifcationText);
		interp.exec("executionResult = executeTestTemplate(templateHtml_j, templ_namespace,True)");
		executionResult = interp.get("executionResult").toString();
		String resultHtml = executionResult.trim();

		Map result = new HashMap();
		result.put("resultText", resultText);
		result.put("resultHtml", resultHtml);

		return result;

	}

	public String replacePlaceholders(String notificationTemplate, Event event, User user, String unsubscribeLink, boolean isHtml) {
		Map eventMetadata = event.getEventMetadata();
		notificationTemplate = notificationTemplate.replaceAll("\\$USER", user.getFullName());
		if (eventMetadata != null){
			EventMetadata titleEm = (EventMetadata) eventMetadata.get("http://purl.org/rss/1.0/title");
			if (titleEm == null)
				titleEm = (EventMetadata) eventMetadata.get("http://purl.org/dc/elements/1.1/title");
			notificationTemplate = notificationTemplate.replaceAll("\\$EVENT.TITLE", ((titleEm != null)?titleEm.getValue():""));			
		}

		notificationTemplate = notificationTemplate.replaceAll("\\$EVENT.DATE", event.getCreationDate().toString());
		notificationTemplate = notificationTemplate.replaceAll("\\$EVENT.CHANNEL", event.getChannel().getTitle());
		notificationTemplate = notificationTemplate.replaceAll("\\$UNSUSCRIBE_LINK", unsubscribeLink);
		if (notificationTemplate.indexOf("$EVENT") != -1) {
			StringBuffer sb = new StringBuffer();
			for (Iterator iter = event.getEventMetadata().values().iterator(); iter.hasNext();) {
				EventMetadata em = (EventMetadata) iter.next();
				sb.append(getLocalName(em.getProperty()) + " : " + em.getValue() + (isHtml?"<BR/>":"\n"));
			}
			notificationTemplate = notificationTemplate.replaceAll("\\$EVENT", sb.toString());
		}
		return notificationTemplate;
	}

	protected String getLocalName(String name) {
		return name.substring(name.lastIndexOf("/") + 1);
	}
}