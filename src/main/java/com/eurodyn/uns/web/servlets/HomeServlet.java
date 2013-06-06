package com.eurodyn.uns.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eurodyn.uns.model.User;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.web.jsf.LoginBean;

public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = -8927395040934638998L;

    private static final WDSLogger logger = WDSLogger.getLogger(HomeServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String redirectToPage = "home.jsf";
            
            if (request.getRequestURI().indexOf("subsc/edit") > -1){
                redirectToPage = request.getContextPath() + "/subscriptions/subscription.jsf?sid="+ request.getParameter("sid");
            }else{
                User user = LoginBean.getUser(request);
                if (user != null && !request.isUserInRole("xmlrpc")) {
                    if (user.getPreferDashboard().booleanValue())
                        redirectToPage = "dash/" + user.getExternalId() + "/dashboard.jsf";
                    else
                        redirectToPage = "rss/" + user.getExternalId() + "/rssReader.jsf";
                }
                
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
