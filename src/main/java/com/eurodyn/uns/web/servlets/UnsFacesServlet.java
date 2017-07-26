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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UnsFacesServlet implements Servlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnsFacesServlet.class);

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
            LOGGER.error(t.getMessage());
            // dampRequest(((HttpServletRequest) servlet_request));
        }

    }

    private void dampRequest(HttpServletRequest request) {

        LOGGER.info("");
        LOGGER.info("");
        LOGGER.info("*************       REQUEST START     *************************************************************************");
        LOGGER.info("Request Auth path is " + request.getAuthType());
        LOGGER.info("Request character encoding is " + request.getCharacterEncoding());
        LOGGER.info("Request content length is " + request.getContentLength());
        LOGGER.info("Request content type is " + request.getContentType());
        LOGGER.info("Request method is " + request.getMethod());
        LOGGER.info("Request path inof is " + request.getPathInfo());
        LOGGER.info("Request path translated is " + request.getPathTranslated());
        LOGGER.info("Request protocol is " + request.getProtocol());
        LOGGER.info("Request query string is " + request.getQueryString());
        LOGGER.info("Request remote addr is " + request.getRemoteAddr());
        LOGGER.info("Request remote host is " + request.getRemoteHost());
        LOGGER.info("Request user name is " + request.getRemoteUser());
        LOGGER.info("Request sessionId is " + request.getRequestedSessionId());
        LOGGER.info("Request URI is " + request.getRequestURI());
        LOGGER.info("Request URL is " + request.getRequestURL());
        LOGGER.info("Request Scheme is  " + request.getScheme());
        // LOGGER.info(request.getH;
        LOGGER.info("Request server name is " + request.getServerName());
        LOGGER.info("Request server port is " + request.getServerPort());
        LOGGER.info("Request servlet path is " + request.getServletPath());
        LOGGER.info("Request real path is " + request.getRealPath(request.getServletPath()));
        // LOGGER.info(re);

        java.util.Enumeration enum011 = request.getHeaderNames();
        String headerName = "";
        String headerValue = "";

        while (enum011.hasMoreElements()) {
            headerName = (String) enum011.nextElement();
            headerValue = request.getHeader(headerName);
            LOGGER.info("Header name " + headerName + " Header value " + headerValue);
        }

        // Enumeration enum7 = request.getLocales();
        // Locale local = null;
        //
        // while (enum7.hasMoreElements())
        // {
        // local = (Locale) enum7.nextElement();
        // if (local != null)
        // {
        // LOGGER.info("Local display country is " +
        // local.getDisplayCountry());
        // LOGGER.info("Local display languages is " +
        // local.getDisplayLanguage());
        // }
        //
        // }
        //
        LOGGER.info("///////////////////////// PARAMETERS ////////////////////////////////////////");
        Map map = request.getParameterMap();

        Iterator iter = map.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();

            LOGGER.info(entry.getKey().toString() + "  " + request.getParameter(entry.getKey().toString()));

        }

        LOGGER.info("///////////////////////// PARAMETERS ////////////////////////////////////////");

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
        // LOGGER.info("Request Attribute name " + attributeName + "
        // Request Attribute value " + attributeValueS);
        // }
        //
        Cookie[] cookies = request.getCookies();

        for (int i = 0; i < cookies.length; i++) {
            LOGGER.info("Cookie name " + cookies[i].getName() + " value " + cookies[i].getValue() + " domain " + cookies[i].getDomain() + " path " + cookies[i].getPath());
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

            LOGGER.info("Session Attribute name " + attributeName + " Session Attribute value " + attributeValueS);
        }

        LOGGER.info("Sesion context (Aplication ) server info " + session.getServletContext().getServerInfo());
        LOGGER.info("Sesion context (Aplication ) context name " + session.getServletContext().getServletContextName());

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
        // LOGGER.info("Application Attribute name " + attributeName
        // + " Application Attribute value " + attributeValueS);
        // }
        //
        // LOGGER.info("///////////////////////// Application init
        // PARAMETERS ////////////////////////////////////////");
        //
        // Enumeration enum5 =
        // session.getServletContext().getInitParameterNames();
        // while (enum5.hasMoreElements())
        // {
        // attributeName = (String) enum5.nextElement();
        // attributeValue =
        // session.getServletContext().getInitParameter(attributeName);
        // LOGGER.info("Application parameter name " + attributeName
        // + " Application parameter value " + attributeValue);
        // }
        //
        // LOGGER.info("///////////////////////// Application init
        // PARAMETERS ////////////////////////////////////////");
        //
        // LOGGER.info("************* REQUEST END
        // *************************************************************************");
        // LOGGER.info("");
        // LOGGER.info("");

        // printSystemProperty();

    }

}