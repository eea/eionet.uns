package com.eurodyn.uns.web.jsf.rpc;

import java.util.List;

import com.eurodyn.uns.model.Dto;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.web.jsf.BaseBean;
import com.eurodyn.uns.web.jsf.SortableTable;


public class RpcChannelListForm extends BaseBean {
	private ChannelFacade channelFacade;

	public RpcChannelListForm()
	{		
		channelFacade = new ChannelFacade();
		st = new SortableTable("title");
	}
	
	public List getChannels()
	{		
		Dto dto = new Dto();
		String order = st.isAscending()?"asc":"desc";
		dto.put("orderProperty",st.getSort());
		dto.put("order",order);
		dto.put("user",getUser());
		return (List) channelFacade.getRpcUserChannels(dto).get("list");
	}

	
}
