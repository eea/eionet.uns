package com.eurodyn.uns.web.filters;

import com.eurodyn.uns.Properties;
import edu.yale.its.tp.cas.client.filter.CASFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * CAS Login Filter.
 *
 */
public class EionetCASFilter2 extends CASFilter {

    /** Static logger for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EionetCASFilter2.class);
    /** FQN of this class. */
    private static final String CLASS_NAME = EionetCASFilter2.class.getName();

    public static String CAS_LOGIN_URL = null;
    public static String SERVER_NAME = null;
    /*
     * (non-Javadoc)
     *
     * @see edu.yale.its.tp.cas.client.filter.CASFilter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
        LOGGER.info("Initializing " + CLASS_NAME + " ...");
        CASFilterConfig casFilterConfig = CASFilterConfig.getInstance(config);
        CAS_LOGIN_URL = casFilterConfig.getInitParameter(CASFilter.LOGIN_INIT_PARAM);
        SERVER_NAME = casFilterConfig.getInitParameter(CASFilter.SERVERNAME_INIT_PARAM);
        super.init(casFilterConfig);
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.yale.its.tp.cas.client.filter.CASFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse,
     * javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws ServletException, IOException {
        LOGGER.trace(CLASS_NAME + ".doFilter() invoked ...");
        super.doFilter(request, response, fc);
    }

    public static String getCASLoginURL(HttpServletRequest request) {
        /*String casLoginUrl = request.getSession().getServletContext().getInitParameter(CASFilter.LOGIN_INIT_PARAM);
        String servername = request.getSession().getServletContext().getInitParameter(CASFilter.SERVERNAME_INIT_PARAM);*/
        return CAS_LOGIN_URL + "?service=" + request.getScheme() + "://" + SERVER_NAME + request.getContextPath() + "/login/home.jsf";
    }

    public static String getCASLogoutURL(HttpServletRequest request) {
        /*String casLoginUrl = request.getSession().getServletContext().getInitParameter(CASFilter.LOGIN_INIT_PARAM);
        String servername = request.getSession().getServletContext().getInitParameter(CASFilter.SERVERNAME_INIT_PARAM);*/
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
            LOGGER.error("Error", e);
        }

        return CAS_LOGIN_URL + "?service=" +   serviceURL ;


    }
}
