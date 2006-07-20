package com.eurodyn.uns.web.jsf;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.eurodyn.uns.model.DeliveryType;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.facades.UserFacade;
import com.eurodyn.uns.util.common.WDSLogger;

public class LoginBean  {

	private static final WDSLogger logger = WDSLogger.getLogger(LoginBean.class);

	public static String getUserRole(HttpServletRequest request) {
		if (request.isUserInRole("admin"))
			return "admin";
		if (request.isUserInRole("xmlrpc"))
			return "rpc";
		if (request.getRemoteUser() != null)
			return "eea";

		if (getUser(request) != null) {
			return "eeaNotLogged";
		}
		return "";
	}


	public static User getUser(HttpServletRequest request) {
		User user = null;
		HttpSession session = request.getSession();
		if (session != null) {
			user = (User) session.getAttribute("user");
			try {
				if (user == null) {
					Cookie[] c = request.getCookies();
					Integer userId = null;
					if (c != null) {
						for (int i = 0; i < c.length; i++) {
							if (c[i].getName().compareTo("unsDashboard") == 0) {
								userId = new Integer(c[i].getValue());
								break;
							}
						}
						if (userId != null && userId.intValue() != -1) {
							user = (new UserFacade()).findUser(userId);
							if(user != null){
								session.setAttribute("user", user);
							}
							
						}
					}

				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return user;
	}

	public static boolean hasDashboard(User user) {
		boolean hasDashboard = false;

		Map subscriptionsMap = user.getSubscriptions();

		if (subscriptionsMap != null && subscriptionsMap.size() > 0) {
			Collection subscriptions = subscriptionsMap.values();
			for (Iterator iter = subscriptions.iterator(); iter.hasNext();) {
				Subscription subscription = (Subscription) iter.next();
				List deliveryTypesList = subscription.getDeliveryTypes();
				for (Iterator iterator = deliveryTypesList.iterator(); iterator.hasNext();) {
					DeliveryType deliveryType = (DeliveryType) iterator.next();
					if (deliveryType.getId().intValue() == DeliveryType.WDB) {
						hasDashboard = true;
						break;
					}
				}
			}
		}
		return hasDashboard;
	}

}