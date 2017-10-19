package com.eurodyn.uns.web.listeners;

import com.eurodyn.uns.Properties;
import edu.yale.its.tp.cas.client.filter.CASFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

/**
 *
 * TODO: remove or replace
 */
@Component
public class SpringEventListeners {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringEventListeners.class);

    @EventListener
    public void handleContextEvent(ContextRefreshedEvent e) {
        ApplicationContext appContext = e.getApplicationContext();
        if (!(appContext instanceof WebApplicationContext)) {
            return;
        }
        WebApplicationContext ctx = (WebApplicationContext) e.getApplicationContext();
        ServletContext context = ctx.getServletContext();
        context.setInitParameter(CASFilter.LOGIN_INIT_PARAM, "https://sso.eionet.europa.eu/login");
        context.setInitParameter(CASFilter.SERVERNAME_INIT_PARAM, Properties.getStringProperty("cas.filter.serverName"));
        context.setInitParameter("eionetLoginCookieDomain", ".eionet.europa.eu");
        context.setInitParameter("edu.yale.its.tp.cas.client.filter.wrapRequest", "true");
        context.setInitParameter("edu.yale.its.tp.cas.client.filter.validateUrl", "https://sso.eionet.europa.eu/serviceValidate");
    }
}
