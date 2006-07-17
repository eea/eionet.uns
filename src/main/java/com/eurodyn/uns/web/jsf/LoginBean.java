package com.eurodyn.uns.web.jsf;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import com.eurodyn.uns.model.DeliveryType;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.facades.UserFacade;
import com.eurodyn.uns.util.common.WDSLogger;

public class LoginBean extends BaseBean {

	private static final WDSLogger logger = WDSLogger.getLogger(LoginBean.class);

	private UserFacade userFacade = new UserFacade();

	// properies
	private String username;

	private String password;

	private String afterLogin;

	public boolean isLoggedIn() {
		return getExternalContext().getRemoteUser() != null;
	}

	public String getUserRole() {
		if (getExternalContext().isUserInRole("admin"))
			return "admin";
		if (getExternalContext().isUserInRole("xmlrpc"))
			return "rpc";
		if (getExternalContext().getRemoteUser() != null)
			return "eea";

		if (hasValidCookie()) {
			return "eeaNotLogged";
		}
		return "";
	}

	private boolean hasValidCookie() {
		Cookie dashoardCookie = (Cookie) getExternalContext().getRequestCookieMap().get("unsDashboard");
		return dashoardCookie != null && !dashoardCookie.getValue().equals("-1");
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String login() {
		String outcome = null;
		try {
			boolean authanticated = userFacade.uitAuthenticate(username, password);
			if (authanticated) {
				User user = userFacade.getUser(username, true);
				if (user != null) {
					user.setLoggedIn(true);
					getSession().setAttribute("user", user);
					//String cookieId = getRequest().isUserInRole("xmlrpc") ? "-1" : user.getId().toString();
					//Cookie cookie = new Cookie("unsDashboard", cookieId);
					//cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
					//getResponse().addCookie(cookie);

					if (afterLogin != null && afterLogin.length() != 0) {
						redirect(afterLogin);
					} else if (!getRequest().isUserInRole("xmlrpc")) {
						if(hasDashboard(user)){
							if (user.getPreferDashboard().booleanValue()) {
								redirectToDashboard();
							} else {
								redirectToRssReader();
							}			
							return null;
						}else
							outcome = "subscriptions";

						
					} else
						outcome = "success";
				} else
					addErrorMessage(null, "System error", null);
			} else {

				addErrorMessage(null, "label.login.error.invalid", null);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}
		return outcome;
	}

	
	public void casLogin(String userName) {
//		String outcome = null;
//		try {
//			boolean authanticated = userFacade.uitAuthenticate(username, password);
//			if (authanticated) {
				User user = userFacade.getUser(username, true);
				if (user != null) {
					user.setLoggedIn(true);
					getSession().setAttribute("user", user);
					String cookieId = getRequest().isUserInRole("xmlrpc") ? "-1" : user.getId().toString();
					Cookie cookie = new Cookie("unsDashboard", cookieId);
					cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
					getResponse().addCookie(cookie);
				}

//					if (afterLogin != null && afterLogin.length() != 0) {
//						redirect(afterLogin);
//					} else if (!getRequest().isUserInRole("xmlrpc")) {
//						if(hasDashboard(user)){
//							if (user.getPreferDashboard().booleanValue()) {
//								redirectToDashboard();
//							} else {
//								redirectToRssReader();
//							}
//						}else
//							outcome = "subscriptions";
//
//						
//					} else
//						outcome = "success";
//				} else
//					addErrorMessage(null, "System error", null);
//			} else {
//
//				addErrorMessage(null, "label.login.error.invalid", null);
//			}
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			addSystemErrorMessage();
//		}
//		return outcome;
	}

	
	
	private boolean hasDashboard(User user) {
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

	public String logout() {
		String outcome = "success";
		User user = getUser();
		user.setLoggedIn(false);
		setAfterLogin(null);
		if (hasValidCookie() && hasDashboard(user)) {
			if (user.getPreferDashboard().booleanValue()) {
				redirectToDashboard();
			} else {
				redirectToRssReader();
			}
		}
		return outcome;
	}

	public String getAfterLogin() {
		if (getRequestAttribute("afterLogin") != null)
			return getRequestAttribute("afterLogin").toString();
		return afterLogin;
	}

	public void setAfterLogin(String afterLogin) {
		this.afterLogin = afterLogin;
	}

}