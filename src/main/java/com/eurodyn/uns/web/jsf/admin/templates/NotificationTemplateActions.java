package com.eurodyn.uns.web.jsf.admin.templates;

import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;

import com.eurodyn.uns.model.NotificationTemplate;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.service.facades.EventMetadataFacade;
import com.eurodyn.uns.service.facades.NotificationTemplateFacade;
import com.eurodyn.uns.util.common.WDSLogger;

public class NotificationTemplateActions extends NotificationTemplateForm {

	private static final WDSLogger logger = WDSLogger.getLogger(NotificationTemplateActions.class);

	public NotificationTemplateActions() {
		try {
			notificationTemplateFacade = new NotificationTemplateFacade();
			channelFacade = new ChannelFacade();
			eventMetadataFacade = new EventMetadataFacade();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}
	}

	public String edit() {
		if (reset) {
			notificationTemplate = new NotificationTemplate();
			reset = false;
		}
		return "notificationTemplate";
	}

	public String save() {

		try {
			if (notificationTemplate.getId() == null) {
				notificationTemplate.setEditOnly(Boolean.FALSE);
				notificationTemplateFacade.createNotificationTemplate(notificationTemplate);
				addInfoMessage(null, "messages.template.success.create", new Object[] { notificationTemplate.getName() });
			} else {
				notificationTemplateFacade.updateNotificationTemplate(notificationTemplate);
				addInfoMessage(null, "messages.template.success.update", new Object[] { notificationTemplate.getName() });
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}

		return "notificationTemplates";
	}

	public String remove() {

		try {
			if (!notificationTemplateFacade.deleteNotificationTemplate(notificationTemplate)){
				addErrorMessage(null,"msg.restrictTemplateDeletion",new Object[] { notificationTemplate.getName()});
			}else
				addInfoMessage(null, "messages.template.success.delete", new Object[] { notificationTemplate.getName() });
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}
		return "successUpdate";
	}

	public String prepareTest() {

		try {
			testEventsList = channelFacade.findOneEventForChannel();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}
		return "testNotifChannelsList";
	}

	public String test() {

		try {
			User user = getUser();

			HttpServletRequest req = getRequest();
			int port = req.getServerPort();
			String unsubscribeLink = req.getScheme() + "://" + req.getServerName() + (port != 0 ? (":" + port) : "") + req.getContextPath() + "/subscriptions/unsubscribe.jsf?subsc=797d5b22ab5f16c2bd55fe78c839dd51";
			testEvent = eventMetadataFacade.findEvent(getId());

			NotificationTemplateInterpreter nti = new NotificationTemplateInterpreter();
			Map result = nti.pyhtonInterpreter(notificationTemplate, testEvent, user, unsubscribeLink);
			resultText = result.get("resultText").toString();
			resultHtml = result.get("resultHtml").toString();

			if (resultText != null)
				resultText = resultText.trim().replaceAll("\n", "<br />");
			if (resultHtml != null){
				resultHtml = resultHtml.replaceAll("<br />", "\n");
				resultHtml = resultHtml.trim().replaceAll("\n", "<br />");
			}
			

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
			return "notificationTemplates";
		}
		return "notificationTemplateTestResult";
	}

	public void changeAfterTest(ActionEvent event) {
		afterTest = "notificationTemplate";
	}

	

}
