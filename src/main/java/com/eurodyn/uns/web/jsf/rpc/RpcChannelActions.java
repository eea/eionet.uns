package com.eurodyn.uns.web.jsf.rpc;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.DeliveryType;
import com.eurodyn.uns.model.NotificationTemplate;
import com.eurodyn.uns.model.Stylesheet;
import com.eurodyn.uns.service.facades.ChannelFacade;

import com.eurodyn.uns.web.jsf.BaseBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcChannelActions extends BaseBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcChannelActions.class);

    private ChannelFacade channelFacade;

    private Channel channel;

    public RpcChannelActions() {
        channelFacade = new ChannelFacade();
    }

    
    public String save() {

        try {
            if (channel.getId().intValue() == -1) {
                channel.setMode("PUSH");
                channel.setCreator(getUser());
                channel.setStatus(new Integer(0));
                channel.setNotificationTemplate(new NotificationTemplate(new Integer(1)));
                channel.setTransformation(new Stylesheet(1));
                ArrayList dt=new ArrayList();
                for (int i = 1; i < 5; i++) {
                    DeliveryType d1=new DeliveryType();
                    d1.setId(new Integer(i));
                    dt.add(d1);
                }
                channel.setDeliveryTypes(dt);
                channel.setLastHarvestDate((new GregorianCalendar(1990, 1, 1, 0, 0, 0)).getTime());
                channelFacade.createChannel(channel);
                addInfoMessage(null, "label.channel.success.create", new Object[] { channel.getTitle() });
            } else {
                channelFacade.updateChannel(channel);
                addInfoMessage(null, "label.channel.success.update", new Object[] { channel.getTitle() });
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return "rpcUserChannels";

    }

    
    public String edit() {
        if (channel == null)
            channel = new Channel();
        return "editRpcChannel";
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

}
