package com.eurodyn.uns.web.jsf.subscriptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.eurodyn.uns.model.Dto;
import com.eurodyn.uns.service.facades.RoleFacade;
import com.eurodyn.uns.service.facades.SubscriptionFacade;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.web.jsf.BaseBean;
import com.eurodyn.uns.web.jsf.SortableTable;

public class SubscriptionListForm extends BaseBean {

    private static final WDSLogger logger = WDSLogger.getLogger(SubscriptionListForm.class);

    private SubscriptionFacade subscriptionFacade;
 
    private RoleFacade roleFacade;

    private List subscriptions;

    private List channels;
    
    public SubscriptionListForm() {
        subscriptionFacade = new SubscriptionFacade();
        roleFacade = new RoleFacade();
        st = new SortableTable("title");

    }

    public boolean isPreparedSubscriptions() {
        try {
            if (isRenderPhase()) {
                if (subscriptions == null && getUser().getSubscriptions() != null || reset){
                    subscriptions = new ArrayList((new HashMap(getUser(true).getSubscriptions())).values());
                    reset = false;
                }
                if (st.getSort().equals("title"))
                    st.setSort("channel.title");
                st.sort(subscriptions);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return true;
    }

    public boolean isPreparedChannels() {
        try {
            if (isRenderPhase()) {
                if (channels == null || reset) {
                    if (st.getSort().equals("channel.title"))
                        st.setSort("title");                    
                    List roles = roleFacade.getUserRoles(getUser().getExternalId());
                    Dto dto = new Dto();
                    String order = st.isAscending() ? "asc" : "desc";
                    dto.put("orderProperty", st.getSort());
                    dto.put("order", order);
                    dto.put("user", getSession().getAttribute("user"));
                    dto.put("roles", roles);
                    channels = (List) subscriptionFacade.getAvailableChannels(dto).get("list");
                    reset = false;
                }
                st.sort(channels);

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return true;
    }

    public List getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List getChannels() {
        return channels;
    }

    public void setChannels(List channels) {
        this.channels = channels;
    }
}
