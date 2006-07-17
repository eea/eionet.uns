package com.eurodyn.uns.web.jsf.rpc;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.NotificationTemplate;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.web.jsf.BaseBean;

public class RpcChannelForm extends BaseBean {
	private ChannelFacade channelFacade;

	private Channel channel;

	public RpcChannelForm() {

		channelFacade = new ChannelFacade();

	}

	public Channel getChannel() {
		if (channel == null)
			channel = new Channel();
		return channel;
	}


	public String save() {

		try {

			if (channel.getId().intValue() == -1) {
				channel.setMode("PUSH");
				channel.setCreator(getUser());
				channel.setStatus(new Integer(0));
				channel.setNotificationTemplate(new NotificationTemplate(new Integer (1) ));
				channelFacade.createChannel(channel);
				addInfoMessage(null, "label.channel.success.create", new Object[] { channel.getTitle() });
			} else {
				channelFacade.updateChannel(channel);
				addInfoMessage(null, "label.channel.success.update", new Object[] { channel.getTitle() });
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "rpcUserChannels";

	}
	
	
	public String edit()
	{		
		try {
			channel = channelFacade.getChannel(channel.getId()); //edit from existing
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "editRpcChannel";
	}


}
