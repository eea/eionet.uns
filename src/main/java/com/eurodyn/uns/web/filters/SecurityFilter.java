package com.eurodyn.uns.web.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.facades.UserFacade;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.web.jsf.LoginBean;

public class SecurityFilter implements Filter {
	private static final WDSLogger logger = WDSLogger.getLogger(SecurityFilter.class);
	
	private UserFacade userFacade = new UserFacade();

	FilterConfig fc = null;

	public void init(FilterConfig arg0) throws ServletException {
		this.fc = arg0;
	}

	/**
	 * Provides SecuredServletRequest if session is available. Otherwise, provides original HttpServletRequest.
	 * 
	 * @see SecuredServletRequest
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		// check if session is available
		
		final HttpServletRequest httpReq = (HttpServletRequest) req;
		final HttpServletResponse httpRes = (HttpServletResponse) res;
		final HttpSession session = httpReq.getSession(false);
		
		String authUsername=httpReq.getRemoteUser();

		// if session is available use SecuredServletRequest
		final HttpServletRequest newReq;
		if (session != null) {
			logger.debug("httpReq.getRemoteUser() " + httpReq.getRemoteUser());
			newReq = createSecuredServletRequest(httpReq, session);
		} else {
			newReq = httpReq;
		}


		newReq.setCharacterEncoding("UTF-8");
		logger.debug(newReq.getRemoteUser());
		
		if (authUsername!=null ) {
			User user = (User )session.getAttribute("user");
			if(user == null){// login
				logger.debug("Perform login ");
				
				user = userFacade.getUser(authUsername, true);				
				user.setLoggedIn(true);
				session.setAttribute("user", user);
				String cookieId = newReq.isUserInRole("xmlrpc") ? "-1" : user.getId().toString();
				Cookie cookie = new Cookie("unsDashboard", cookieId);
				cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
				cookie.setPath(newReq.getContextPath());
				httpRes.addCookie(cookie);
				String path = newReq.getRequestURI();
				if (path.indexOf("/login/") != -1){
					 String where = "/subscriptions/subscriptions.jsf";
					 String redirectUrl = fc.getServletContext().getInitParameter("edu.yale.its.tp.cas.client.filter.serverName").startsWith("http")? ((fc.getServletContext().getInitParameter("edu.yale.its.tp.cas.client.filter.serverName") + newReq.getContextPath() + where)): (( "http://" + fc.getServletContext().getInitParameter("edu.yale.its.tp.cas.client.filter.serverName") + newReq.getContextPath() + where));
					 httpRes.sendRedirect(redirectUrl);
				}
				
				
			}else{
				String path = newReq.getRequestURI();
				user.setLoggedIn(true);
				if (path.indexOf("/login/") != -1){
					 String where = "/subscriptions/subscriptions.jsf";
					 String redirectUrl = fc.getServletContext().getInitParameter("edu.yale.its.tp.cas.client.filter.serverName").startsWith("http")? ((fc.getServletContext().getInitParameter("edu.yale.its.tp.cas.client.filter.serverName") + newReq.getContextPath() + where)): (( "http://" + fc.getServletContext().getInitParameter("edu.yale.its.tp.cas.client.filter.serverName") + newReq.getContextPath() + where));
					 httpRes.sendRedirect(redirectUrl);
				}

			}
			if (!isAuthorised(newReq)){
				session.invalidate();
				httpRes.sendRedirect(fc.getServletContext().getInitParameter("edu.yale.its.tp.cas.client.filter.loginUrl"));
								
			}
			
			
	
		}

	
		// call next filter

		if (newReq.getParameter("myCss") != null) {
			HttpSession sess = newReq.getSession();
			String css = newReq.getParameter("myCss");
			sess.setAttribute("myCss", css + ".css");
			if (css.indexOf("main1") != -1 || css.indexOf("main2") != -1) {
				sess.setAttribute("portletCss", "portlet-green.css");
			} else {
				sess.setAttribute("portletCss", "portlet.css");

			}

		}

		chain.doFilter(newReq, res);
	}

	private boolean isAuthorised(HttpServletRequest request) {
		String path = request.getRequestURI();
		if (path.indexOf("/admin/") > -1 && !request.isUserInRole("admin"))
			return false;
		if (path.indexOf("/xmlrpc/") > -1 && !request.isUserInRole("rpc"))
			return false;
		return true;
	}
	
	

	protected SecuredServletRequest createSecuredServletRequest(final HttpServletRequest httpReq, final HttpSession session) {

		return new SecuredServletRequest(httpReq, session);
	}

	public void destroy() {
		// do nothing
	}

	protected class SecuredServletRequest extends HttpServletRequestWrapper {

		HttpServletRequest request = null;
		String remoteUser=null;

		public SecuredServletRequest(HttpServletRequest request, HttpSession session) {
			super(request);
			this.request = request;
		}

		public String getRemoteUser() {

			HttpSession session = request.getSession();
			if (session != null) {
				User user = (User) session.getAttribute("user");
				if (user != null && user.isLoggedIn()) {
					return user.getExternalId();
				}
			}
			return null;
		}		

		public boolean isUserInRole(String roleName) {
			String userName = getRemoteUser();
			if (userName != null && UserFacade.hasPerm(userName, "/" + roleName, "x")) {
				return true;
			} else {
				return false;
			}

		}

	}

}
