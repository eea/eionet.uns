package com.eurodyn.uns.web.jsf.rpc;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.NotificationTemplate;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.web.jsf.BaseBean;

public class RpcChannelActions extends BaseBean {

	private static final WDSLogger logger = WDSLogger.getLogger(RpcChannelActions.class);

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
				channelFacade.createChannel(channel);
				addInfoMessage(null, "label.channel.success.create", new Object[] { channel.getTitle() });
			} else {
				channelFacade.updateChannel(channel);
				addInfoMessage(null, "label.channel.success.update", new Object[] { channel.getTitle() });
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
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
