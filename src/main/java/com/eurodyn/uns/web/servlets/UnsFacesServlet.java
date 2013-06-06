package com.eurodyn.uns.web.servlets;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.faces.webapp.FacesServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.eurodyn.uns.util.common.WDSLogger;

public class UnsFacesServlet implements Servlet {

    private static final WDSLogger logger = WDSLogger.getLogger(UnsFacesServlet.class);

    private final Servlet FACES_SERVLET = new FacesServlet();

    public void destroy() {
        FACES_SERVLET.destroy();
    }

    public ServletConfig getServletConfig() {

        return FACES_SERVLET.getServletConfig();

    }

    public void init(ServletConfig servletConfig) throws ServletException {
        // Save our ServletConfig instance
        FACES_SERVLET.init(servletConfig);

    }

    public String getServletInfo() {

        return (FACES_SERVLET.getClass().getName());

    }

    public void service(ServletRequest servlet_request, ServletResponse servlet_response) {

        try {
            FACES_SERVLET.service(servlet_request, servlet_response);
        } catch (Throwable t) {
            logger.error(t.getMessage());
            // dampRequest(((HttpServletRequest) servlet_request));
        }

    }

    private void dampRequest(HttpServletRequest request) {

        logger.info("");
        logger.info("");
        logger.info("*************       REQUEST START     *************************************************************************");
        logger.info("Request Auth path is " + request.getAuthType());
        logger.info("Request character encoding is " + request.getCharacterEncoding());
        logger.info("Request content length is " + request.getContentLength());
        logger.info("Request content type is " + request.getContentType());
        logger.info("Request method is " + request.getMethod());
        logger.info("Request path inof is " + request.getPathInfo());
        logger.info("Request path translated is " + request.getPathTranslated());
        logger.info("Request protocol is " + request.getProtocol());
        logger.info("Request query string is " + request.getQueryString());
        logger.info("Request remote addr is " + request.getRemoteAddr());
        logger.info("Request remote host is " + request.getRemoteHost());
        logger.info("Request user name is " + request.getRemoteUser());
        logger.info("Request sessionId is " + request.getRequestedSessionId());
        logger.info("Request URI is " + request.getRequestURI());
        logger.info("Request URL is " + request.getRequestURL());
        logger.info("Request Scheme is  " + request.getScheme());
        // logger.info(request.getH;
        logger.info("Request server name is " + request.getServerName());
        logger.info("Request server port is " + request.getServerPort());
        logger.info("Request servlet path is " + request.getServletPath());
        logger.info("Request real path is " + request.getRealPath(request.getServletPath()));
        // logger.info(re);

        java.util.Enumeration enum011 = request.getHeaderNames();
        String headerName = "";
        String headerValue = "";

        while (enum011.hasMoreElements()) {
            headerName = (String) enum011.nextElement();
            headerValue = request.getHeader(headerName);
            logger.info("Header name " + headerName + " Header value " + headerValue);
        }

        // Enumeration enum7 = request.getLocales();
        // Locale local = null;
        //
        // while (enum7.hasMoreElements())
        // {
        // local = (Locale) enum7.nextElement();
        // if (local != null)
        // {
        // logger.info("Local display country is " +
        // local.getDisplayCountry());
        // logger.info("Local display languages is " +
        // local.getDisplayLanguage());
        // }
        //
        // }
        //
        logger.info("///////////////////////// PARAMETERS ////////////////////////////////////////");
        Map map = request.getParameterMap();

        Iterator iter = map.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();

            logger.info(entry.getKey().toString() + "  " + request.getParameter(entry.getKey().toString()));

        }

        logger.info("///////////////////////// PARAMETERS ////////////////////////////////////////");

        String attributeName = "";
        Object attributeValue = "";
        String attributeValueS = "";
        //
        // Enumeration enum2 = request.getAttributeNames();
        // ;
        // while (enum2.hasMoreElements())
        // {
        // attributeName = (String) enum2.nextElement();
        // attributeValue = request.getAttribute(attributeName);
        // if (attributeValue != null)
        // attributeValueS = attributeValue.toString();
        // else
        // attributeValueS = "null";
        // logger.info("Request Attribute name " + attributeName + "
        // Request Attribute value " + attributeValueS);
        // }
        //
        Cookie[] cookies = request.getCookies();

        for (int i = 0; i < cookies.length; i++) {
            logger.info("Cookie name " + cookies[i].getName() + " value " + cookies[i].getValue() + " domain " + cookies[i].getDomain() + " path " + cookies[i].getPath());
            ;
        }

        HttpSession session = request.getSession();

        Enumeration enum3 = session.getAttributeNames();
        ;
        while (enum3.hasMoreElements()) {
            attributeName = (String) enum3.nextElement();
            attributeValue = session.getAttribute(attributeName);
            if (attributeValue != null)
                attributeValueS = attributeValue.toString();
            else
                attributeValueS = "null";

            logger.info("Session Attribute name " + attributeName + " Session Attribute value " + attributeValueS);
        }

        logger.info("Sesion context (Aplication ) server info " + session.getServletContext().getServerInfo());
        logger.info("Sesion context (Aplication ) context name " + session.getServletContext().getServletContextName());

        // Enumeration enum4 =
        // session.getServletContext().getAttributeNames();
        // while (enum4.hasMoreElements())
        // {
        // attributeName = (String) enum4.nextElement();
        //
        // attributeValue =
        // session.getServletContext().getAttribute(attributeName);
        // if (attributeValue != null)
        // attributeValueS = attributeValue.toString();
        // else
        // attributeValueS = "null";
        //
        // logger.info("Application Attribute name " + attributeName
        // + " Application Attribute value " + attributeValueS);
        // }
        //
        // logger.info("///////////////////////// Application init
        // PARAMETERS ////////////////////////////////////////");
        //
        // Enumeration enum5 =
        // session.getServletContext().getInitParameterNames();
        // while (enum5.hasMoreElements())
        // {
        // attributeName = (String) enum5.nextElement();
        // attributeValue =
        // session.getServletContext().getInitParameter(attributeName);
        // logger.info("Application parameter name " + attributeName
        // + " Application parameter value " + attributeValue);
        // }
        //
        // logger.info("///////////////////////// Application init
        // PARAMETERS ////////////////////////////////////////");
        //
        // logger.info("************* REQUEST END
        // *************************************************************************");
        // logger.info("");
        // logger.info("");

        // printSystemProperty();

    }

}