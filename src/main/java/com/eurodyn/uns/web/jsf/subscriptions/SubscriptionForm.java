package com.eurodyn.uns.web.jsf.subscriptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.eurodyn.uns.model.DeliveryAddress;
import com.eurodyn.uns.model.DeliveryType;
import com.eurodyn.uns.model.Filter;
import com.eurodyn.uns.model.MetadataElement;
import com.eurodyn.uns.model.Statement;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.service.facades.EventMetadataFacade;
import com.eurodyn.uns.service.facades.MetadataElementFacade;
import com.eurodyn.uns.service.facades.SubscriptionFacade;
import com.eurodyn.uns.service.facades.UserFacade;
import com.eurodyn.uns.web.jsf.BaseBean;
import com.eurodyn.uns.web.jsf.SortableTable;

public class SubscriptionForm extends BaseBean {

    protected UserFacade userFacade = new UserFacade();

    protected User subscriber;

    protected MetadataElementFacade metadataElementFacade;
    
    protected EventMetadataFacade eventMetadataFacade;
    
    protected SubscriptionFacade subscriptionFacade;

    protected Subscription subscription;

    protected ChannelFacade channelFacade;

    private SortableTable st;

    protected SubscriptionForm() {
    }

    protected void initForm() throws Exception {
        channelFacade = new ChannelFacade();
        metadataElementFacade = new MetadataElementFacade();
        eventMetadataFacade = new EventMetadataFacade();
        subscriptionFacade = new SubscriptionFacade();
        st = new SortableTable("name");
        User user = getUser();
        subscriber = new User();
        subscriber.setNumberOfColumns(user.getNumberOfColumns());
        subscriber.setPreferHtml(user.getPreferHtml());
        subscriber.setPreferDashboard(user.getPreferDashboard());
        subscriber.setDeliveryAddresses(user.getDeliveryAddresses());
        subscriber.setPageRefreshDelay(user.getPageRefreshDelay());
        if (subscriber.getDeliveryAddresses() == null){
            HashMap da = new HashMap();
            da.put(EMAIL, new DeliveryAddress(new DeliveryType(EMAIL)));
            subscriber.setDeliveryAddresses(da);
        }
        else if (subscriber.getDeliveryAddresses().get(EMAIL) == null) {
            subscriber.getDeliveryAddresses().put(EMAIL, new DeliveryAddress(new DeliveryType(EMAIL)));
        }
        subscription = new Subscription();
    }

    public SortableTable getSt() {
        return st;
    }

    public User getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(User subscriber) {
        this.subscriber = subscriber;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public List getDeliveryTypesItems() {
        return toConvertedSelectItems(subscription, "channel.deliveryTypes", "name", true);
    }

    public String getSubMenu() {
        return (subscription != null && subscription.getId() != null) ? "subscriptions" : "avChannels";
    }

    // filters start

    protected List availableProperties = null;

    protected Map allChoosableStatements = null;

    protected List propertyValues = null;

    protected String property;

    protected String value;

    protected boolean editFilterMode;

    protected Filter filter;

    protected Statement statement;

    public Map getAllChoosableStatements() {
        return allChoosableStatements;
    }

    public void setAllChoosableStatements(Map allChoosableStatements) {
        this.allChoosableStatements = allChoosableStatements;
    }

    protected void prepareStatements(boolean selectFirstProperty) throws Exception {
        editFilterMode = true;
        if (allChoosableStatements != null) {
            availableProperties = new ArrayList(allChoosableStatements.keySet());
            if (filter.getStatements() != null) {
                for (Iterator iter = filter.getStatements().iterator(); iter.hasNext();) {
                    Statement st = (Statement) iter.next();
                    availableProperties.remove(st.getMetadataElement());
                }
            }
            if (availableProperties.size() > 0) {
                if (selectFirstProperty) {
                    property = ((MetadataElement) availableProperties.get(0)).getName();
                }
                propertyValues = (List) allChoosableStatements.get(new MetadataElement(property));
            }
        }

    }

    public List getPropertiesItems() {
        return toSelectItems(availableProperties, "name", "localName");
    }

    public List getPropertyValuesItems() {
        return propertyValues != null ? toSelectItems(propertyValues) : new ArrayList();
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public boolean isEditFilterMode() {
        return editFilterMode;
    }

    public void setEditFilterMode(boolean editFilterMode) {
        this.editFilterMode = editFilterMode;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;

    }

    public void setPropertyValues(List propertyValues) {
        this.propertyValues = propertyValues;
    }

    public List getPropertyValues() {
        return propertyValues;
    }

    public void setAvailableProperties(List availableProperties) {
        this.availableProperties = availableProperties;
    }

    public List getAvailableProperties() {
        return availableProperties;
    }
}
