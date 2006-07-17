package com.eurodyn.uns.web.jsf.admin.channels;

import java.util.List;

import com.eurodyn.uns.model.Dto;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.web.jsf.BaseBean;
import com.eurodyn.uns.web.jsf.SortableTable;

public class ChannelListForm extends BaseBean {

	private static final WDSLogger logger = WDSLogger.getLogger(ChannelListForm.class);

	private ChannelFacade channelFacade;

	private List pullChannels;

	private List pushChannels;

	public ChannelListForm() {
		channelFacade = new ChannelFacade();
		st = new SortableTable("title");
	}

	public boolean isPreparedPullChannels() {
		try {
			if (isRenderPhase()) {
				if (pullChannels == null || reset) {
					Dto dto = new Dto();
					String order = st.isAscending() ? "asc" : "desc";
					dto.put("orderProperty", st.getSort());
					dto.put("order", order);
					// getExternalContext().getSessionMap().remove("channelBean");
					pullChannels = (List) channelFacade.getPullChannels(dto).get("list");
					reset = false;
				}
				st.sort(pullChannels);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}
		return true;
	}

	public boolean isPreparedPushChannels() {
		try {
			if (isRenderPhase()) {
				if (pushChannels == null || reset) {
					Dto dto = new Dto();
					String order = st.isAscending() ? "asc" : "desc";
					dto.put("orderProperty", st.getSort());
					dto.put("order", order);
					pushChannels = (List) channelFacade.getPushChannels(dto).get("list");
					reset = false;
				}
				st.sort(pushChannels);

			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}
		return true;
	}

	public List getPullChannels() {
		return pullChannels;
	}

	public void setPullChannels(List notificationTemplates) {
		this.pullChannels = notificationTemplates;
	}

	public List getPushChannels() {
		return pushChannels;
	}

	public void setPushChannels(List stylesheets) {
		this.pushChannels = stylesheets;
	}

}
