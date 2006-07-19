package com.eurodyn.uns.web.jsf.admin.channels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.faces.event.ValueChangeEvent;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.ChannelMetadataElement;
import com.eurodyn.uns.model.DeliveryAddress;
import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.MetadataElement;
import com.eurodyn.uns.model.NotificationTemplate;
import com.eurodyn.uns.model.Role;
import com.eurodyn.uns.model.Stylesheet;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.delegates.ChannelServerDelegate;
import com.eurodyn.uns.util.MailAuthenticator;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.util.rdf.RdfContext;
import com.eurodyn.uns.util.rdf.RssChannelsProcessor;
import com.eurodyn.uns.web.jsf.admin.config.ConfigElement;
import com.eurodyn.uns.web.jsf.admin.config.ConfigManager;
import com.eurodyn.uns.web.jsf.admin.templates.NotificationTemplateInterpreter;
import com.eurodyn.uns.web.jsf.util.Period;
import com.sun.mail.smtp.SMTPTransport;

public class ChannelActions extends ChannelForm {

	private static final WDSLogger logger = WDSLogger.getLogger(ChannelActions.class);

	public ChannelActions() {

		try {
			initForm();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}

	}

	public void channelUrlChange(ValueChangeEvent event) {
		channel.setContent(null);
	}

	public String save() {

		String outcome = null;
		try {
			if (!isStylesheetSelected()) {
				channel.setTransformation(null);
				setUpVisibleElements();
			}

			setUpChannelRoles();
			if (channel.getMode().equals("PULL"))
				channel.setRefreshDelay(new Integer(refreshDelay.getDays() * 1440 + refreshDelay.getHours() * 60 + refreshDelay.getMinutes()));

			if (channel.getId().intValue() == -1) {
				channel.setMode("PULL");
				channel.setCreator(getUser());
				channel.setStatus(new Integer(1));
				channel.setLastHarvestDate((new GregorianCalendar(1990, 1, 1, 0, 0, 0)).getTime());
				channelFacade.createChannel(channel);
				addInfoMessage(null, "label.channel.success.create", new Object[] { channel.getTitle() });
			} else {
				Set channelRoles = channel.getRoles();
				if (channelRoles != null && channelRoles.size() > 0) {
					List subscriptions = subscriptionFacade.findSubscriptionsForChannel(channel);
					if (subscriptions != null && subscriptions.size() > 0) {
						NotificationTemplate lostRoleTemplate = notificationTemplateFacade.getNotificationTemplate(new Integer(2));
						for (Iterator iter = subscriptions.iterator(); iter.hasNext();) {
							Subscription subscription = (Subscription) iter.next();
							User user = subscription.getUser();
							List userRoles = roleFacade.getUserRoles(user.getExternalId());
							boolean hasValidRole = false;
							for (Iterator iterator = userRoles.iterator(); iterator.hasNext();) {
								Role role = (Role) iterator.next();
								if (channelRoles.contains(role)) {
									hasValidRole = true;
									break;
								}

							}
							if (!hasValidRole) {
								subscriptionFacade.deleteSubscription(subscription);
								sendUnsubscribedEmail(lostRoleTemplate, subscription);
								if (subscription.getUser().getId().equals(getUser().getId()))
									getUser(true);
							}

						}

					}
				}

				channelFacade.updateChannel(channel);
				addInfoMessage(null, "label.channel.success.update", new Object[] { channel.getTitle() });
			}

			outcome = channel.getMode().equalsIgnoreCase("PUSH") ? "pushChannels" : "pullChannels";

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}
		return outcome;

	}

	public String changeStatus() {
		try {
			// channel = channelFacade.getChannel(channel.getId());
			if (channel.getStatus().intValue() == 1) {
				channel.setStatus(new Integer(0));
				addInfoMessage(null, "label.channel.success.disable", new Object[] { channel.getTitle() });
			} else {
				channel.setStatus(new Integer(1));
				addInfoMessage(null, "label.channel.success.enable", new Object[] { channel.getTitle() });
			}
			channelFacade.updateChannel(channel);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}
		return "pushChannels";

	}

	public String remove() {

		String outcome = null;
		try {
			List subscriptions = subscriptionFacade.findSubscriptionsForChannel(channel);
			if (subscriptions.size() > 0) {
				NotificationTemplate deletionTemplate = notificationTemplateFacade.getNotificationTemplate(new Integer(3));
				for (Iterator iter = subscriptions.iterator(); iter.hasNext();) {
					Subscription subscription = (Subscription) iter.next();
					subscriptionFacade.deleteSubscription(subscription);
					sendUnsubscribedEmail(deletionTemplate, subscription);
					if (subscription.getUser().getId().equals(getUser().getId()))
						getUser(true);
				}

			}

			channelFacade.deleteChannel(channel);

			addInfoMessage(null, "label.channel.success.delete", new Object[] { channel.getTitle() });
			outcome = channel.getMode().equalsIgnoreCase("PUSH") ? "pushChannels" : "pullChannels";
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
			e.printStackTrace();
		}
		return outcome;
	}

	private void sendUnsubscribedEmail(NotificationTemplate notificationTemplate, Subscription subscription) throws Exception {

		Event event = new Event();
		event.setChannel(subscription.getChannel());
		event.setCreationDate(new Date());
		User user = userFacade.findUser(subscription.getUser().getId());

		NotificationTemplateInterpreter nti = new NotificationTemplateInterpreter();
		Map result = nti.pyhtonInterpreter(notificationTemplate, event, user, null);

		String mailAddress = ((DeliveryAddress) user.getDeliveryAddresses().get(new Integer(1))).getAddress();
		Map configMap = ConfigManager.getInstance().getConfigMap();
		Properties props = System.getProperties();
		props.put("mail.smtp.host", ((ConfigElement) configMap.get("smtpserver/smtp_host")).getValue());

		props.put("mail.smtp.auth", ((ConfigElement) configMap.get("smtpserver/smtp_useauth")).getValue().toString());

		String username = ((ConfigElement) configMap.get("smtpserver/smtp_username")).getValue().toString();
		String password = ((ConfigElement) configMap.get("smtpserver/smtp_password")).getValue().toString();

		// construct the message
		String from = ((ConfigElement) configMap.get("pop3server/adminmail")).getValue().toString();

		MailAuthenticator auth = new MailAuthenticator(username, password);
		Session session = Session.getInstance(props, auth);
		session.setDebug(false);

		Message msg = new MimeMessage(session);
		if (from != null)
			msg.setFrom(new InternetAddress(from));
		else
			msg.setFrom();

		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailAddress, false));
		String subject = nti.replacePlaceholders(notificationTemplate.getSubject(), event, user, null, false);
		msg.setSubject(subject);

		if (user.getPreferHtml().booleanValue()) {
			msg.setText(result.get("resultHtml").toString());
		} else {
			msg.setText(result.get("resultText").toString());
		}

		msg.setSentDate(new Date());

		// Get a Session object

		// mailSession.setDebug(true);
		SMTPTransport t = (SMTPTransport) session.getTransport("smtp");

		t.connect();
		t.sendMessage(msg, msg.getAllRecipients());

		t.close();

	}

	public String edit() {
		String outcome = "editChannel";
		try {
			if (channel.getId().intValue() > 0) 
				channel = channelFacade.getChannel(channel.getId());

			if (reset)
				reset();

			boolean isPullChannel = channel.getMode().equals("PULL");

			if (isPullChannel && channel.getContent() == null && !feed()) {
				outcome = null;
			}

			if (!isPullChannel) {
				setUpChannelMetadataElements(eventMetadataFacade.findChannelProperties(channel));
			}

			if (outcome != null) {
				if (allRoles == null) {
					allRoles = roleFacade.getRoles();
				}
				setUpChannelRoles();
				availableRoles = new ArrayList(allRoles);
				if (channel.getRoles() != null)
					availableRoles.removeAll(channel.getRoles());
				if (isPullChannel)
					prepareRefreshDelay();
			}

			getRequest().setAttribute("selectedSubMenu", channel.getMode().equals("PULL") ? "pullChannels" : "pushChannels");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}

		return outcome;
	}

	public String getSubMenu() {
		return channel.getMode().equals("PULL") ? "pullChannels" : "pushChannels";
	}

	public String create() {
		try {
			channel = new Channel();
			reset();
			channel.setDeliveryTypes(new ArrayList(allDeliveryTypes));
			channel.setMode("PULL");
			channel.setNotificationTemplate(new NotificationTemplate(new Integer(1)));
			channel.setTransformation(new Stylesheet(1));			
			stylesheetSelected = true;								
			prepareRefreshDelay();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}
		return "channelUrl";
	}

	public String prepareChoosableElements() {
		try {
			Collections.sort(channelMetadataElements, new Comparator() {
				public int compare(Object a, Object b) {
					MetadataElement me1 = ((ChannelMetadataElement) a).getMetadataElement();
					MetadataElement me2 = ((ChannelMetadataElement) b).getMetadataElement();
					return me1.compareTo(me2);
				}
			});

			if (!st.isAscending())
				Collections.reverse(channelMetadataElements);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}
		return "choosable";
	}

	public String prepareTemplates() {

		try {
			if (allStylesheets == null) {
				allStylesheets = (List) xslFacade.getStylesheets().get("list");
			}
			if (allNotificationTemplates == null) {
				allNotificationTemplates = (List) notificationTemplateFacade.findNotificationTemplatesForAssigment();
			}

			if (!stylesheetSelected) {
				setUpVisibleElements();
				channel.setTransformation(new Stylesheet());
			}
			if (channel.getNotificationTemplate() == null)
				channel.setNotificationTemplate(new NotificationTemplate(new Integer(1)));

			channelMetadataElements = new ArrayList(channel.getMetadataElements());
			Collections.sort(channelMetadataElements);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}

		return "templates";
	}

	public String preview() {

		if (!isStylesheetSelected()) {
			channel.setTransformation(null);
			setUpVisibleElements();
		} else {
			channel.setTransformation(xslFacade.getStylesheet(channel.getTransformation().getId()));
		}

		try {
			String result = ChannelServerDelegate.instance.testNewChannel(channel);
			if (result != null && result.length() > 12) {
				if (result.indexOf("</svg>") > 0) {
					result = "<div style=\"overflow:auto; width: 100%; height:180px\">";
					result += "<img src=\"../svg.unsvg\" alt=\"Generated SVG\" />";
					result += "</div>";
					getSession().setAttribute("testChannel",channel);
				}
			} else {
				result = "<p class=\"nocontent\">CONTENT IS NOT AVAILABLE !</p>";
			}
			transformedContent = result;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}

		return "preview";
	}

	private void reset() {
		stylesheetSelected = channel.getTransformation() != null;
		channel.setContent(null);
		refreshDelay = null;
		allNotificationTemplates = null;
		allStylesheets = null;
		allRoles = null;
		reset = false;
		currentChannelRoles = null;
		visibleElements = null;
		reset = false;
	}

	private boolean feed() {
		try {

			String oldDescription = channel.getDescription();
			String oldTitle = channel.getTitle();

			RdfContext rdfctx = new RdfContext(channel);
			Map availableElements = rdfctx.getData(new RssChannelsProcessor());

			if (oldDescription != null) {
				channel.setDescription(oldDescription);
			}

			if (oldTitle != null) {
				channel.setTitle(oldTitle);
			}

			setUpChannelMetadataElements(availableElements.keySet());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			addErrorMessage(null, "messages.badUrl", new Object[] { channel.getFeedUrl() });
			return false;
		}
		return true;
	}

	private void prepareRefreshDelay() {
		if (refreshDelay == null) {
			int rd = channel.getRefreshDelay() != null ? channel.getRefreshDelay().intValue() : 60;
			refreshDelay = new Period();
			refreshDelay.setDays(rd / 1440);
			rd = rd % 1440;
			refreshDelay.setHours(rd / 60);
			rd = rd % 60;
			refreshDelay.setMinutes(rd);
		}
	}

}
