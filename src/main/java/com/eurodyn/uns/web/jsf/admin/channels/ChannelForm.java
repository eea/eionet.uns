package com.eurodyn.uns.web.jsf.admin.channels;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.eurodyn.uns.model.ChannelMetadataElement;
import com.eurodyn.uns.model.Role;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.service.facades.DeliveryTypeFacade;
import com.eurodyn.uns.service.facades.EventMetadataFacade;
import com.eurodyn.uns.service.facades.MetadataElementFacade;
import com.eurodyn.uns.service.facades.NotificationTemplateFacade;
import com.eurodyn.uns.service.facades.RoleFacade;
import com.eurodyn.uns.service.facades.SubscriptionFacade;
import com.eurodyn.uns.service.facades.UserFacade;
import com.eurodyn.uns.service.facades.XslFacade;
import com.eurodyn.uns.web.jsf.SortableTable;
import com.eurodyn.uns.web.jsf.util.Period;

public abstract class ChannelForm extends BaseChannelBean {

	protected RoleFacade roleFacade;

	protected XslFacade xslFacade;

	protected UserFacade userFacade;

	protected DeliveryTypeFacade deliveryTypeFacade;

	protected NotificationTemplateFacade notificationTemplateFacade;
	
	protected SubscriptionFacade subscriptionFacade;

	protected List availableRoles;

	protected List allStylesheets;

	protected boolean stylesheetSelected;

	protected List allDeliveryTypes;

	protected String[] deliveryTypes;

	protected String transformedContent;

	protected Period refreshDelay;

	protected List allNotificationTemplates;

	protected String currentChannelRoles;

	protected List allRoles;

	protected String visibleElements;


	protected SortableTable st = new SortableTable("name");
	
	public SortableTable getSt() {
		return st;
	}

	protected void initForm() {
		roleFacade = new RoleFacade();
		channelFacade = new ChannelFacade();
		xslFacade = new XslFacade();
		userFacade = new UserFacade();
		metadataElementFacade = new MetadataElementFacade();
		deliveryTypeFacade = new DeliveryTypeFacade();
		notificationTemplateFacade = new NotificationTemplateFacade();
		eventMetadataFacade = new EventMetadataFacade();
		subscriptionFacade = new SubscriptionFacade();
		allDeliveryTypes = (List) deliveryTypeFacade.getDeliveryTypes().get("list");
	}

	protected void setUpChannelRoles() {
		if (currentChannelRoles != null) {
			Set channelRoles = new HashSet();
			if (currentChannelRoles.length() > 0) {
				String roles[] = currentChannelRoles.split(";");
				for (int i = 0; i < roles.length; i++) {
					Role role = roleFacade.getRole(new Integer(roles[i]));
					channelRoles.add(role);
				}
			}
			channel.setRoles(channelRoles);
		}
		currentChannelRoles = null;
	}

	protected void setUpVisibleElements() {
		if (visibleElements != null && visibleElements.length() > 0) {
			List visibleElementsList = Arrays.asList(visibleElements.split(";"));
			for (Iterator iter = channel.getMetadataElements().iterator(); iter.hasNext();) {
				ChannelMetadataElement cme = (ChannelMetadataElement) iter.next();
				int index = visibleElementsList.indexOf(cme.getMetadataElement().getName());
				cme.setVisible(index > -1 ? Boolean.TRUE : Boolean.FALSE);
				cme.setAppearanceOrder(new Integer(index));
			}
		}
	}

	public boolean isStylesheetSelected() {
		return stylesheetSelected;
	}

	public void setStylesheetSelected(boolean stylesheetSelected) {
		this.stylesheetSelected = stylesheetSelected;
	}

	public void setCurrentChannelRoles(String currentChannelRoles) {
		this.currentChannelRoles = currentChannelRoles;
	}

	public String getCurrentChannelRoles() {
		return "";
	}

	public String getVisibleElements() {
		return "";
	}

	public void setVisibleElements(String visibleElements) {
		this.visibleElements = visibleElements;
	}

	public String getTransformedContent() {
		return transformedContent;
	}

	public Period getRefreshDelay() {
		return refreshDelay;
	}

	public List getAvailableRolesItems() {
		return toSelectItems(availableRoles, "id", "localName");
	}

	public List getChannelRolesItems() {
		return toSelectItems(channel, "roles", "id", "localName", false);
	}


	public List getAvailableElementsItems() {
		return toConditionalSelectItems(channelMetadataElements, "metadataElement.name", "metadataElement.localName", "visible", new Boolean(false));
	}

	public List getVisibleElementsItems() {
		return toConditionalSelectItems(channelMetadataElements, "metadataElement.name", "metadataElement.localName", "visible", new Boolean(true));

	}

	public List getStylesheetsItems() {
		return toSelectItems((List) allStylesheets, "id", "fullName");
	}

	public List getDeliveryTypesItems() {
		return toSelectItems(allDeliveryTypes, "id", "name", true);
	}

	public List getNotificationTemplatesItems() {
		return toSelectItems(allNotificationTemplates, "id", "name");
	}

}
