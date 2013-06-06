package com.eurodyn.uns.web.jsf.subscriptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ValueChangeEvent;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.DeliveryAddress;
import com.eurodyn.uns.model.DeliveryType;
import com.eurodyn.uns.model.Filter;
import com.eurodyn.uns.model.Statement;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.util.DateUtil;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.util.uid.UidGenerator;

public class SubscriptionActions extends SubscriptionForm {

    private static final WDSLogger logger = WDSLogger.getLogger(SubscriptionForm.class);

    public SubscriptionActions() {
        try {           
            initForm();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public boolean isExternalSubscription(){
        String secondaryID = getRequest().getParameter("sid");
        if (secondaryID != null){
            Channel channel = channelFacade.getChannelBySecId(secondaryID);
            User user = getUser(true);
            subscription = (Subscription)  user.getSubscriptions().get(channel.getId());
            if(subscription == null){
                subscription = new Subscription();
            }           
            subscription.setChannel(channel);           
            allChoosableStatements = eventMetadataFacade.findChoosableStatements(subscription.getChannel());
            checkOverlappedFilters();
        }
        return false;
    }
    
    public String save() {

        try {
            User user = getUser();
            
            subscription.setUser(user); // because after deserialization we may have two different instaces of the same User, and hibernate doesn't allow this.

            Integer channelId = subscription.getChannel().getId();
            if (user.getSubscriptions() == null)
                user.setSubscriptions(new HashMap());

            user.getSubscriptions().put(channelId, subscription);

            if (!saveJabberAddress(user)){
                addErrorMessage(null,"msg.missingJabberAddress",null);
                return null;
            }

            setUserDashboard();
            if (subscription.getId() == null) {
                subscription.setCreationDate(DateUtil.getCurrentUTCDate());
                subscription.setSecondaryId(UidGenerator.generate());
                addInfoMessage(null, "messages.subscription.success.create", new Object[] { subscription.getChannel().getTitle() });
            } else {
                addInfoMessage(null, "messages.subscription.success.update", new Object[] { subscription.getChannel().getTitle() });
            }
                        
            userFacade.updateUser(user);
            checkOverlappedFilters();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            addSystemErrorMessage();
        }

        return "subscriptions";
    }
    
    
    private boolean saveJabberAddress(User user){
        
        DeliveryAddress jabberAddress = (DeliveryAddress) subscriber.getDeliveryAddresses().get(JABBER); 
        
        if (user.getSubscriptions() != null){
            Collection subscriptions = user.getSubscriptions().values();
            for (Iterator iter = subscriptions.iterator(); iter.hasNext();) {
                Subscription subscription = (Subscription) iter.next();
                if (subscription.getDeliveryTypes().contains(new DeliveryType(JABBER)) && jabberAddress.getAddress().equals("")){
                    return false;
                }           
            }
                                    
        }
        if (!jabberAddress.getAddress().equals(""))
            user.setDeliveryAddresses(subscriber.getDeliveryAddresses());

        return true;    
            
    }
    
    
    public String saveUserPreferences() {

        try {
            User user = getUser();
            subscriber = getSubscriber();
            subscriber.setSubscriptions(user.getSubscriptions());
            if (!saveJabberAddress(subscriber)){
                addErrorMessage(null,"msg.missingJabberAddress",null);
                return null;

            
            }
            
            user.setPreferHtml(subscriber.getPreferHtml());
            user.setPreferDashboard(subscriber.getPreferDashboard());
            user.setDeliveryAddresses(subscriber.getDeliveryAddresses());
            user.setPageRefreshDelay(subscriber.getPageRefreshDelay());



            if (user.getPreferDashboard().booleanValue()) {
                user.setNumberOfColumns(subscriber.getNumberOfColumns());
                setUserDashboard();
            }


            userFacade.updateUser(getUser());
            addInfoMessage(null, "messages.preferences.success", null);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return null;
    }

    public String saveVacationFlag() {
        
        try {
            User user = getUser();

            if (!user.getVacationFlag().booleanValue()) {
                user.setVacationExpiration(null);
            }else{
                if(user.getVacationExpiration() != null && user.getVacationExpiration().before(new Date())){
                    addErrorMessage(null,"msg.wrongVacationDate",null);
                    user.setVacationFlag(Boolean.FALSE);
                    return null;
                }
            }
            
            userFacade.updateUser(getUser());
            addInfoMessage(null, "msg.vacatationFlag." + (user.getVacationFlag().booleanValue()?"enabled":"disabled"), null);
            
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return null;
    }
    
    public String edit() {
        try {
            if (subscription.getId() != null)
                subscription = subscriptionFacade.getSubscription(subscription.getId());
            else
                subscription.setChannel(channelFacade.getChannel(subscription.getChannel().getId()));
            allChoosableStatements = eventMetadataFacade.findChoosableStatements(subscription.getChannel());
            checkOverlappedFilters();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "editSubscription";
    }

    private void checkOverlappedFilters() {
        Set filters1 = new HashSet(getSubscription().getFilters());
        for (Iterator iter = filters1.iterator(); iter.hasNext();) {
            Filter filter1 = (Filter) iter.next();
            Set filters2 = new HashSet(getSubscription().getFilters());
            for (Iterator iterator = filters2.iterator(); iterator.hasNext();) {
                Filter filter2 = (Filter) iterator.next();
                if (filter1 == filter2)
                    continue;
                if (filter1.getStatements().containsAll(filter2.getStatements())) {
                    addWarningMessage(null, "messages.filter.overlapped", null);
                    return;
                }

            }
        }
    }

    public String remove() {
        try {
            subscription.getChannel().getId();
            getUser().getSubscriptions().remove(subscription.getChannel().getId());
            setUserDashboard();
            userFacade.updateUser(getUser());
            addInfoMessage(null, "messages.subscription.success.delete", new Object[] { subscription.getChannel().getTitle() });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return "subscriptions";
    }

    private void setUserDashboard() {

        User user = getUser();
        ArrayList cols = new ArrayList();
        int numCols = user.getNumberOfColumns().intValue();
        for (int i = 0; i < numCols; i++) {
            cols.add(new LinkedList());
        }

        // make column list
        if (user.getSubscriptions() != null) {
            Iterator iter = user.getSubscriptions().entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry element = (Map.Entry) iter.next();
                Subscription subs = (Subscription) element.getValue();

                if (!subs.getDeliveryTypes().contains(deliveryTypesMap.get("WDB"))) {
                    subs.setDashCordX(new Short((short) -1));
                    subs.setDashCordY(new Short((short) -1));
                    continue;
                }

                if (subs.getDashCordX().intValue() == -1 || subs.getDashCordY().intValue() == -1) {
                    int X = findMinCol(cols);
                    subs.setDashCordX(new Short((short) X));
                    subs.setDashCordY(new Short((short) 0));
                }

                if (subs.getDashCordX().intValue() >= numCols)
                    subs.setDashCordX(new Short((short) (numCols - 1)));

                int col = subs.getDashCordX().intValue();
                List colList = (List) cols.get(col);
                int row = colList.size();
                subs.setDashCordY(new Short((short) row));
                colList.add(subs);
            }

        }
    }

    private int findMinCol(List cols) {
        int colMax = Integer.MAX_VALUE;
        int X = 0;
        for (int j = 0; j < cols.size(); j++) {
            int size = ((List) cols.get(j)).size();
            if (size < colMax) {
                colMax = size;
                X = j;
            }
        }
        return X;
    }

    // filters start

    public String toEditFilterMode() {

        try {
            if (filter != null && filter.isEditMode()) {
                filter = new Filter(filter.getId(), filter.getSubscription(), new HashSet(filter.getStatements()));
            } else {
                filter = new Filter();
                filter.setSubscription(subscription);
                filter.setStatements(new HashSet());
            }
            prepareStatements(true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return null;
    }

    public String addStatement() {
        try {
            Statement statement = new Statement();
            statement.setValue(value);
            statement.setProperty(property);
            statement.setMetadataElement(metadataElementFacade.findByName(property));
            filter.getStatements().add(statement);
            prepareStatements(true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String removeStatement() {
        try {
            filter.getStatements().remove(statement);
            prepareStatements(true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return null;
    }

    public String addFilter() {

        try {
            List existingFilters = subscription.getFilters();

            boolean editExisting = false;

            for (Iterator iter = existingFilters.iterator(); iter.hasNext();) {
                Filter ex_filter = (Filter) iter.next();
                if (ex_filter.isEditMode()) {
                    ex_filter.setStatements(filter.getStatements());
                    ex_filter.setEditMode(false);
                    editExisting = true;
                    break;
                }
                if (ex_filter.equals(filter)) {
                    addErrorMessage(null, "messages.filter.exist", null);
                    return null;
                }
            }

            if (!editExisting)
                subscription.getFilters().add(filter);
            editFilterMode = false;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return null;
    }

    public String removeFilter() {
        try {
            subscription.getFilters().remove(filter);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return null;
    }

    public void propertyChanged(ValueChangeEvent ve) {
        try {
            property = ve.getNewValue().toString();
            prepareStatements(false);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
    }

    public boolean isPreparedUnsubscribe() {

        try {
            if (isRenderPhase()) {
                String subsSecondaryId = (String) getExternalContext().getRequestParameterMap().get("subsc");
                subscription = subscriptionFacade.findBySecondaryId(subsSecondaryId);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return true;
    }

}
