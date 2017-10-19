package com.eurodyn.uns.web.jsf.rss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.delegates.ChannelServerDelegate;
import com.eurodyn.uns.service.facades.UserFacade;
import com.eurodyn.uns.web.jsf.BaseBean;

public class DashboardForm extends BaseBean {

    protected static final Map moveCoordinates = new HashMap();

    static {
        moveCoordinates.put("up", new int[] { 0, -1 });
        moveCoordinates.put("down", new int[] { 0, 1 });
        moveCoordinates.put("right", new int[] { 1, 0 });
        moveCoordinates.put("left", new int[] { -1, 0 });
    }

    protected UserFacade userFacade;

    protected List channels;

    private boolean portletExist = false;

    protected String direction;

    private Integer[] columns;

    public DashboardForm() {

    }

    public boolean isPortletExist() {
        return portletExist;
    }

    public void setPortletExist(boolean portletExist) {
        this.portletExist = portletExist;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setChannels(List channels) {
        this.channels = channels;
    }

    public List getChannels() {
        return portletExist ? channels : null;
    }

    public Integer[] getColumns() {
        return columns;
    }

    public int[] getRows() {
        return new int[] { 1 }; // 
    }

    protected void setUpVisibleButtons() {
        int numCols = channels.size();
        for (int i = 0; i < numCols; i++) {
            List col = (List) channels.get(i);
            for (int j = 0; j < col.size(); j++) {
                Subscription subs = (Subscription) col.get(j);
                subs.getChannel().setLeft(i > 0);
                subs.getChannel().setRight(i < numCols - 1);
                subs.getChannel().setUp(j > 0);
                subs.getChannel().setDown(j < col.size() - 1);
            }
        }
    }

    protected void reloadDashChannels() {
        portletExist = false;
        User user = getUser(true);
        channels = new ArrayList();

        int numCols = user.getNumberOfColumns().intValue();
        for (int i = 0; i < numCols; i++) {
            channels.add(new LinkedList());
        }

        if (user.getSubscriptions() != null){

            List subscriptions = new ArrayList(user.getSubscriptions().values());

            Collections.sort(subscriptions, new Comparator() {
                public int compare(Object a, Object b) {
                    Short y1 = ((Subscription) a).getDashCordY();
                    Short y2 = ((Subscription) b).getDashCordY();
                    return y1.compareTo(y2);
                }
            });

            for (Iterator iter = subscriptions.iterator(); iter.hasNext();) {
                Subscription subs = (Subscription) iter.next();
                if (subs.getDashCordX().intValue() != -1) {
                    portletExist = true;                    
                    String result = ChannelServerDelegate.instance.getChannelContent(subs);
                    subs.getChannel().setContent(result);
                    ((List) channels.get(subs.getDashCordX().intValue())).add(subs);
                }
            }

            if (portletExist)
                setUpVisibleButtons();
            
        }
    }

    protected void updateUserChannels() {

        User user = getUser();
        Map subscriptions = user.getSubscriptions();
        for (int i = 0; i < channels.size(); i++) {
            List col = (List) channels.get(i);
            for (int j = 0; j < col.size(); j++) {
                Subscription subs = (Subscription) col.get(j);
                subs.setDashCordY(new Short((short) j));
                subs.setDashCordX(new Short((short) i));
                subs.setUser(user);
                subscriptions.put(subs.getChannel().getId(), subs);

            }
        }

        userFacade.updateUser(user);
    }

}
