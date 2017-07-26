package com.eurodyn.uns.web.jsf.rss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.faces.event.PhaseId;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.RDFThing;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.service.facades.FeedFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RssReaderActions extends RssReaderForm {

    private static final Logger LOGGER = LoggerFactory.getLogger(RssReaderActions.class);

    public RssReaderActions() {
        try {
            feedFacade = new FeedFacade();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }

    }

    public boolean isPreparedForm() {
        try {

            if (getExternalContext().getRequestMap().get(PhaseId.RENDER_RESPONSE) != null) {
                LOGGER.debug("RssReader initialisation ");
                if (channels == null) {
                    channels = new ArrayList();

                    Map subscriptions = getUser(true).getSubscriptions();
                    Iterator it = subscriptions.values().iterator();
                    while (it.hasNext()) {
                        Subscription subs = (Subscription) it.next();
                        if (subs.getDashCordX().intValue() != -1) {
                            channels.add(subs.getChannel());
                            populateThings(subs);
                        }
                    }
                    if(channels.size() == 0){ // user does't have rss channel
                        return true;
                    }
                    Collections.sort(channels, new Comparator() {
                        public int compare(Object a, Object b) {
                            String t1 = ((Channel) a).getTitle();
                            String t2 = ((Channel) b).getTitle();
                            return t1.compareTo(t2);
                        }
                    });
                    
                }

                if (channel == null){
                    if (getRequest().getParameter("reset") == null){
                        channel = (Channel)getSession().getAttribute("currentReaderChannel");                       
                    }   
                    if (channel == null)
                        channel = (Channel) channels.get(0);
                    
                }else{
                    getSession().setAttribute("currentReaderChannel",channel);
                }       
                events = new ArrayList(channel.getTransformedEvents().values());
                Collections.sort(events, new Comparator() {
                    public int compare(Object a, Object b) {
                        Date d1 = ((RDFThing) a).getReceivedDate();
                        Date d2 = ((RDFThing) b).getReceivedDate();
                        return d2.compareTo(d1);
                    }
                });

                StringBuffer sb = new StringBuffer();
                for (Iterator iter = events.iterator(); iter.hasNext();) {
                    RDFThing rdfThing = (RDFThing) iter.next();
                    sb.append("<div  id='" + rdfThing.getEventId() + "' class='event' >");
                    sb.append(rdfThing.getTransformedContent());
                    sb.append("</div>");
                }

                renderedEvents = sb.toString().replaceFirst(" class='event' ", " class='event' style='display:block' ");
                if (events.size() > 0) {
                    String eventId = ((RDFThing) events.get(0)).getEventId().toString();
                    getSession().setAttribute("currentEvent", eventId);
                }

                getSession().setAttribute("rrChanelId", channel.getId());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }

        return true;

    }

    public String changeChannel() {
        return null;
    }

}
