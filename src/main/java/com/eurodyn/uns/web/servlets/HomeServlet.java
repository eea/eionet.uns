package com.eurodyn.uns.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.facades.UserFacade;
import com.eurodyn.uns.util.common.WDSLogger;

public class HomeServlet extends HttpServlet {
	private static final long serialVersionUID = -8927395040934638998L;

	private static final WDSLogger logger = WDSLogger.getLogger(HomeServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			String redirectToPage = "home.jsf";
			try {
				if (request.getRemoteUser() == null) {
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

							redirectToPage = "dashboard.jsf";
							UserFacade userFacade = new UserFacade();
							User user = userFacade.findUser(userId);
							request.getSession(true).setAttribute("user", user);
							if (user.getPreferDashboard().booleanValue())
								redirectToPage = "dash/" + user.getExternalId() + "/dashboard.jsf";
							else
								redirectToPage = "rss/" + user.getExternalId() + "/rssReader.jsf";
						}

					}

				} else if (request.getSession().getAttribute("user") != null) {

					User user = (User) request.getSession().getAttribute("user");

					if (user.getPreferDashboard().booleanValue())
						redirectToPage = "dash/" + user.getExternalId() + "/dashboard.jsf";
					else
						redirectToPage = "rss/" + user.getExternalId() + "/rssReader.jsf";

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			response.sendRedirect(redirectToPage);

		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doGet(request, response);
	}
}
