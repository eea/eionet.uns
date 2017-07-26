package com.eurodyn.uns.web.filters;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Provides logging of HTTP requests and response.
 * 
 */
public class LogFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogFilter.class);

    /**
     * The filter configuration object we are associated with.  If this value
     * is null, this filter instance is not currently configured.
     */
    protected FilterConfig filterConfig = null;

    /**
     * Should a logging level  specified by the client be ignored?
     */
    protected boolean ignore = true;
    protected boolean state = true;
    

    public void init(FilterConfig arg0) throws ServletException {
        this.filterConfig = arg0;
        String stateValue= filterConfig.getInitParameter("state");
        String ignoreValue = filterConfig.getInitParameter("ignore");
        LOGGER.debug(ignoreValue);
        if (stateValue == null)
            this.state = false;
        else if (stateValue.equalsIgnoreCase("ON"))
            this.state = true;
        else if (stateValue.equalsIgnoreCase("OFF"))
            this.state = false;
        else
            this.state = false;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
        if (state) {
            if (LOGGER.isDebugEnabled()) {
                logAttributes((HttpServletRequest) request);
            }
        }
    }

    public void destroy() {
        this.filterConfig = null;
    }

    private void logAttributes(HttpServletRequest req) {
        LOGGER.debug("========== FILTER LOG START ===========");
        logMiscInfo(req);
        logParams(req);
        logRequestAttributes(req);
        logSessionAttributes(req);
        LOGGER.debug("========== FILTER LOG START ===========");
    }

    private void logMiscInfo(HttpServletRequest req) {
        final StringBuffer sb = new StringBuffer();
        sb.append("URI=" + req.getRequestURI());
        sb.append(", UserPrincipal=" + req.getUserPrincipal());
        sb.append(", Locale=" + req.getLocale());
        LOGGER.debug(String.valueOf(sb));
    }

    private void logRequestAttributes(final HttpServletRequest req) {
        final LogSource rep = new LogSource() {
            public String getPrefix() {
                return " REQ";
            }

            public Enumeration getKeys() {
                return req.getAttributeNames();
            }

            public Object getValue(String key) {
                return req.getAttribute(key);
            }
        };
        printKeysAndValues(rep);
    }

    private void logParams(final HttpServletRequest req) {

        final LogSource rep = new LogSource() {
            public String getPrefix() {
                return " PARAMS";
            }

            public Enumeration getKeys() {
                return req.getParameterNames();
            }

            public Object getValue(String key) {
                return req.getParameter(key);
            }
        };
        printKeysAndValues(rep);
    }

    private void logSessionAttributes(HttpServletRequest req) {

        final HttpSession session = req.getSession(false);
        if (session == null) {
            return;
        }

        final LogSource rep = new LogSource() {
            public String getPrefix() {
                return " SESSION";
            }

            public Enumeration getKeys() {
                return session.getAttributeNames();
            }

            public Object getValue(String key) {
                return session.getAttribute(key);
            }
        };
        printKeysAndValues(rep);
    }

    private void printKeysAndValues(LogSource rep) {

        //retrieve keys
        final Enumeration en = rep.getKeys();
        if (!en.hasMoreElements()) {
            return;
        }

        //print values
        final StringBuffer sb = new StringBuffer("\n");
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            sb.append(rep.getPrefix());
            sb.append("[");
            sb.append(key);
            sb.append("]: ");
            sb.append(rep.getValue(key));
            if (en.hasMoreElements()) {
                sb.append("\n");
            }
        }
        LOGGER.debug(sb.toString());
    }

    private interface LogSource {
        public String getPrefix();

        public Enumeration getKeys();

        public Object getValue(String key);
    }

}
