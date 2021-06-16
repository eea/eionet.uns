package com.eurodyn.uns.web;

import com.eurodyn.uns.service.ServiceDispatcher;
import com.eurodyn.uns.service.UserBasicAuthenticationService;
import com.eurodyn.uns.service.impl.UserBasicAuthenticationServiceImpl;
import com.eurodyn.uns.web.exceptions.EndpointCallException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Vector;

@Controller
@RequestMapping(value = "/uns/event")
public class UNSEventController {

    /**
     * Logger.
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(UNSEventController.class);

    private ServiceDispatcher serviceDispatcher;

    private UserBasicAuthenticationService userBasicAuthenticationService;

    @Autowired
    public UNSEventController() {
        initUserBasicAuthenticationService();
    }

    @RequestMapping(value = "/createChannel/{channel_name}/{description}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String createChannel(HttpServletRequest request, @PathVariable String channel_name, @PathVariable String description) throws Exception {
        /* Get Basic Authentication header*/
        String authentication = request.getHeader("Authorization");
        String username = getUserBasicAuthenticationService().checkUserAuthentication(authentication);

        initServiceDispatcher(username);
        String result = getServiceDispatcher().createChannel(channel_name, description);
        LOGGER.info("User " + username + " called method createChannel");
        return result;
    }

    @RequestMapping(value = "/legacy/sendNotification/{channel_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String sendNotification(HttpServletRequest request, @PathVariable String channel_id) throws Exception {
        /* Get Basic Authentication header*/
        String authentication = request.getHeader("Authorization");
        String username = getUserBasicAuthenticationService().checkUserAuthentication(authentication);

        String triplesStr = request.getParameter("triples");
        if (triplesStr == null){
            throw new EndpointCallException("Could not find triples parameter");
        }

        // deserialize triples
        ByteArrayInputStream in = new ByteArrayInputStream(Hex.decodeHex(triplesStr.toCharArray()));
        Vector triples = (Vector) new ObjectInputStream(in).readObject();

        initServiceDispatcher(username);
        String result = getServiceDispatcher().sendNotification(channel_id, triples);
        LOGGER.info("User " + username + " called method send notification");
        return result;
    }

    @RequestMapping(value = "/sendNotificationRDF/{channel_id}/{rdf}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String sendNotificationRDF(HttpServletRequest request, @PathVariable String channel_id, @PathVariable String rdf) throws Exception {
        /* Get Basic Authentication header*/
        String authentication = request.getHeader("Authorization");
        String username = getUserBasicAuthenticationService().checkUserAuthentication(authentication);

        initServiceDispatcher(username);
        String result = getServiceDispatcher().sendNotificationRDF(channel_id, rdf);
        LOGGER.info("User " + username + " called method send notification RDF");
        return result;
    }

    @RequestMapping(value = "/canSubscribe/{channel_id}/{subscriberUserName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean canSubscribe(HttpServletRequest request, @PathVariable String channel_id, @PathVariable String subscriberUserName) throws Exception {
        /* Get Basic Authentication header*/
        String authentication = request.getHeader("Authorization");
        String username = getUserBasicAuthenticationService().checkUserAuthentication(authentication);

        initServiceDispatcher(username);
        Boolean result = getServiceDispatcher().canSubscribe(channel_id, subscriberUserName);
        LOGGER.info("User " + username + " called method can subscribe");
        return result;
    }

    @RequestMapping(value = "/legacy/makeSubscription/{channel_id}/{subscriberUserName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String makeSubscription(HttpServletRequest request, @PathVariable String channel_id, @PathVariable String subscriberUserName) throws Exception {
        /* Get Basic Authentication header*/
        String authentication = request.getHeader("Authorization");
        String username = getUserBasicAuthenticationService().checkUserAuthentication(authentication);

        String filtersStr = request.getParameter("filters");
        if (filtersStr == null){
            throw new EndpointCallException("Could not find filters parameter");
        }

        // deserialize filters
        ByteArrayInputStream in = new ByteArrayInputStream(Hex.decodeHex(filtersStr.toCharArray()));
        Vector filters = (Vector) new ObjectInputStream(in).readObject();

        initServiceDispatcher(username);
        String result = getServiceDispatcher().makeSubscription(channel_id, subscriberUserName, filters);
        LOGGER.info("User " + username + " called method make subscription");
        return result;
    }

    public ServiceDispatcher getServiceDispatcher() {
        return serviceDispatcher;
    }

    protected void initServiceDispatcher(String username){
        this.serviceDispatcher = new ServiceDispatcher(username);
    }

    public UserBasicAuthenticationService getUserBasicAuthenticationService() {
        return userBasicAuthenticationService;
    }

    protected void initUserBasicAuthenticationService(){
        this.userBasicAuthenticationService = new UserBasicAuthenticationServiceImpl();
    }

}
