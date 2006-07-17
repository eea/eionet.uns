package com.eurodyn.uns.web.jsf.rss;

import java.util.List;

import javax.faces.event.PhaseId;
import javax.servlet.http.Cookie;

import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.facades.UserFacade;
import com.eurodyn.uns.util.common.WDSLogger;

public class DashboardActions extends DashboardForm {

	private static final WDSLogger logger = WDSLogger.getLogger(DashboardActions.class);

	public DashboardActions() {
		try {
			userFacade = new UserFacade();
			getResponse().setHeader("Refresh", "5");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}
	}

	public boolean isPreparedForm() {
		try {
			if (getExternalContext() != null && getExternalContext().getRequestMap() != null){
			if (getExternalContext().getRequestMap().get(PhaseId.RENDER_RESPONSE) != null) {
				logger.debug("Dashboard initialisation ");
				Integer[] columns = new Integer[getUser().getNumberOfColumns().intValue()];
				for (int i = 0; i < columns.length; i++) {
					columns[i] = new Integer(i);
				}
				if (channels == null)
					reloadDashChannels();
			}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}
		return true;
	}

	public String moveChannel() {

		try {
			int[] coordinates = (int[]) moveCoordinates.get(direction);
			int moveX = coordinates[0];
			int moveY = coordinates[1];

			Subscription subs = (Subscription) getUser().getSubscriptions().get(getId());

			int moveChannelX = subs.getDashCordX().intValue();
			int moveChannelY = subs.getDashCordY().intValue();

			// Move channel in desired direction

			List col = (List) channels.get(moveChannelX);
			subs = (Subscription) col.get(moveChannelY);
			col.remove(moveChannelY);

			if (moveY != 0) {
				col.add(moveChannelY + moveY, subs);
			} else if (moveX != 0) {
				col = (List) channels.get(moveChannelX + moveX);
				if (moveChannelY > col.size())
					moveChannelY = col.size();
				col.add(moveChannelY, subs);
			}
			updateUserChannels();
			setUpVisibleButtons();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}
		return null;
	}

	public String close() {

		try {
			Subscription subs = (Subscription) getUser().getSubscriptions().get(getId());
			List deliveryTypes = subs.getDeliveryTypes();
			if (deliveryTypes.size() > 1) {
				deliveryTypes.remove(deliveryTypesMap.get("WDB"));
				subs.setDashCordX(new Short((short) -1));
				subs.setDashCordY(new Short((short) -1));
			} else {// remove subscription cause WDB is only delivery type
				getUser().getSubscriptions().remove(getId());
			}

			userFacade.updateUser(getUser());
			reloadDashChannels();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}
		return null;
		
	}


	
}
