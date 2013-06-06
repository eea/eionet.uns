package com.eurodyn.uns.web.filters;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.facades.UserFacade;
import com.eurodyn.uns.util.common.WDSLogger;

import edu.yale.its.tp.cas.client.filter.CASFilter;

public class EionetCASFilter extends CASFilter {

    public static final String EIONET_LOGIN_COOKIE_NAME = "eionetCasLogin";

    private static final WDSLogger logger = WDSLogger.getLogger(EionetCASFilter.class);

    private static final String EIONET_COOKIE_LOGIN_PATH = "eionetCookieLogin";

    private static String CAS_LOGIN_URL = null;

    private static String SERVER_NAME = null;

    private static String EIONET_LOGIN_COOKIE_DOMAIN = null;
    
    private UserFacade userFacade = new UserFacade();

    public void init(FilterConfig config) throws ServletException {
        CAS_LOGIN_URL = config.getInitParameter(LOGIN_INIT_PARAM);
        SERVER_NAME = config.getInitParameter(SERVERNAME_INIT_PARAM);
        EIONET_LOGIN_COOKIE_DOMAIN = config.getInitParameter("eionetLoginCookieDomain");
        super.init(config);

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws ServletException, IOException {
        logger.debug("Request hits the EionetCASFilter ");
        CASFilterChain chain = new CASFilterChain();
        super.doFilter(request, response, chain);

        if (chain.isDoNext()) {
            logger.debug("chain.isDoNext() is true");
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            HttpSession session = httpRequest.getSession();
            if (session != null && ( session.getAttribute("user") == null ||  !((User) session.getAttribute("user")).isLoggedIn() ) ) {
                User user = (User) session.getAttribute("user");
                logIn(httpRequest,httpResponse,user);               
                logger.debug("Logged in user " + session.getAttribute(CAS_FILTER_USER));
                String requestURI = httpRequest.getRequestURI();
                if (requestURI.indexOf(EIONET_COOKIE_LOGIN_PATH) > -1) {
                    redirectAfterEionetCookieLogin(httpRequest, httpResponse);
                    return;
                } else if (requestURI.indexOf("/login/") > -1) {
                    attachEionetLoginCookie(httpResponse,true);
                    redirectAfterLogin(httpRequest,httpResponse);
                    return;
                }
            }
            fc.doFilter(httpRequest, response);
            return;
        }
        logger.debug("chain.isDoNext() is false");
    }

    public static void attachEionetLoginCookie(HttpServletResponse response, boolean isLoggedIn){
        Cookie tgc = new Cookie(EIONET_LOGIN_COOKIE_NAME, isLoggedIn?"loggedIn":"loggedOut");
        tgc.setMaxAge(-1);
        if (!EIONET_LOGIN_COOKIE_DOMAIN.equalsIgnoreCase("localhost"))
            tgc.setDomain(EIONET_LOGIN_COOKIE_DOMAIN);
        tgc.setPath("/");           
        response.addCookie(tgc);        
    }
    
    
    public static String getCASLoginURL(HttpServletRequest request) {
        return CAS_LOGIN_URL + "?service=" + request.getScheme() + "://" + SERVER_NAME + request.getContextPath() + "/login/home.jsf";
    }

    public static String getCASLogoutURL(HttpServletRequest request) {
        return CAS_LOGIN_URL.replaceFirst("/login","/logout")+ "?url=" + request.getScheme() + "://" + SERVER_NAME + request.getContextPath();
    }
    
    
    public static String getEionetCookieCASLoginURL(HttpServletRequest request) {

        String contextPath = request.getContextPath();
        String serviceURL =  request.getRequestURL().toString(); 
        if (request.getQueryString() != null && request.getQueryString().length() > 0){
            serviceURL = serviceURL + "?" + request.getQueryString();
        }

        String serviceURI = serviceURL.substring(serviceURL.indexOf("/", serviceURL.indexOf("://") + 3));

        if (contextPath.equals("")) {
            if (serviceURI.equals("/"))
                serviceURL = serviceURL + EIONET_COOKIE_LOGIN_PATH + "/";
            else
                serviceURL = serviceURL.replaceFirst(forRegex(serviceURI), "/" + EIONET_COOKIE_LOGIN_PATH + serviceURI);
        } else {
            String servletPath = serviceURI.substring(contextPath.length(), serviceURI.length());
            if (serviceURI.equals("/"))
                serviceURL = serviceURL + EIONET_COOKIE_LOGIN_PATH + "/";
            else
                serviceURL = serviceURL.replaceFirst(forRegex(serviceURI), contextPath + "/" + EIONET_COOKIE_LOGIN_PATH + servletPath);
        }

        try {
            serviceURL = URLEncoder.encode(serviceURL,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
        
        return CAS_LOGIN_URL + "?service=" +   serviceURL ;


    }

    private void redirectAfterEionetCookieLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestUri = request.getRequestURI() + (request.getQueryString() != null ? ("?" +request.getQueryString()):"" );
        String realURI = null;
        if (requestUri.endsWith(EIONET_COOKIE_LOGIN_PATH + "/"))
            realURI = requestUri.replaceFirst(EIONET_COOKIE_LOGIN_PATH + "/", "");
        else
            realURI = requestUri.replaceFirst("/" + EIONET_COOKIE_LOGIN_PATH, "");
        response.sendRedirect(realURI);
    }

    private void  logIn(HttpServletRequest request, HttpServletResponse response, User user){
        HttpSession session = request.getSession();
        if (user == null || !user.getExternalId().equals(session.getAttribute(CAS_FILTER_USER)))
            user = userFacade.getUser( (String)session.getAttribute(CAS_FILTER_USER), true);
        
        String cookieId = UserFacade.hasPerm(user.getExternalId(), "/" + "xmlrpc", "x") ? "-1" : user.getId().toString();
        Cookie cookie = new Cookie("unsDashboard", cookieId);
        cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
        cookie.setPath(request.getContextPath().equals("")?"/":request.getContextPath());
        response.addCookie(cookie);
        user.setLoggedIn(true);
        request.getSession().setAttribute("user", user);
        
        
    }
    
    public static String logIn2(HttpServletRequest request) throws IOException{

        User user = new User();
        user.setFullName("Risto Alt");
        user.setLoggedIn(true);
        user.setExternalId("altnyris");
        List roles = new ArrayList();
        roles.add("admin");
        user.setUserRoles(roles);
        request.getSession().setAttribute("user", user);
        return "http://localhost:8080/subscriptions/subscriptions.jsf";     
        
    }
    
    
    private void redirectAfterLogin(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String where = "/subscriptions/subscriptions.jsf";
        if (request.isUserInRole("xmlrpc"))
            where = "/xmlrpc/rpcUserChannels.jsf";
        String redirectUrl = SERVER_NAME.startsWith("http") ? ((SERVER_NAME + request.getContextPath() + where)) : (("http://" + SERVER_NAME + request.getContextPath() + where));
        response.sendRedirect(redirectUrl);     
    }
    
      public static String forRegex(String aRegexFragment){
            final StringBuffer result = new StringBuffer();

            final StringCharacterIterator iterator = new StringCharacterIterator(aRegexFragment);
            char character =  iterator.current();
            while (character != CharacterIterator.DONE ){
              /*
              * All literals need to have backslashes doubled.
              */
              if (character == '.') {
                result.append("\\.");
              }
              else if (character == '\\') {
                result.append("\\\\");
              }
              else if (character == '?') {
                result.append("\\?");
              }
              else if (character == '*') {
                result.append("\\*");
              }
              else if (character == '+') {
                result.append("\\+");
              }
              else if (character == '&') {
                result.append("\\&");
              }
              else if (character == ':') {
                result.append("\\:");
              }
              else if (character == '{') {
                result.append("\\{");
              }
              else if (character == '}') {
                result.append("\\}");
              }
              else if (character == '[') {
                result.append("\\[");
              }
              else if (character == ']') {
                result.append("\\]");
              }
              else if (character == '(') {
                result.append("\\(");
              }
              else if (character == ')') {
                result.append("\\)");
              }
              else if (character == '^') {
                result.append("\\^");
              }
              else if (character == '$') {
                result.append("\\$");
              }
              else {
                //the char is not a special one
                //add it to the result as is
                result.append(character);
              }
              character = iterator.next();
            }
            return result.toString();
          }

}

class CASFilterChain implements FilterChain {

    private boolean doNext = false;

    public void doFilter(ServletRequest request, ServletResponse response) {
        doNext = true;
    }

    public boolean isDoNext() {
        return doNext;
    }
}



