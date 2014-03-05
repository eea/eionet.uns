package com.eurodyn.uns.service.daemons.harvester;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.EventMetadata;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.service.facades.EventMetadataFacade;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.util.rdf.RdfContext;
import com.hp.hpl.jena.vocabulary.RSS;

public class PullerThread implements Runnable {

    private static final WDSLogger LOGGER = WDSLogger.getLogger(PullerThread.class);

    Channel channel;

    public PullerThread(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void run() {

        String url = "";

        try {
            ChannelFacade channelFacade = new ChannelFacade();
            EventMetadataFacade eventMetadataFacade = new EventMetadataFacade();

            channel.setLastHarvestDate(new Date());
            channelFacade.updateChannel(channel);

            url = channel.getFeedUrl();
            if (url != null && url.length() > 0) {
                if (exists(url)) {

                    RdfContext rdfctx = new RdfContext(channel);
                    Map pulledEvents = rdfctx.getData(new ResourcesProcessor());
                    Date lastSeen = new Date();

                    for (Iterator eventIdentifiers = pulledEvents.keySet().iterator(); eventIdentifiers.hasNext();) {

                        String eventIdentifier = (String) eventIdentifiers.next();
                        if (eventIdentifier != null && eventIdentifier.length() > 0) {

                            Event event = eventMetadataFacade.findEventByExtId(eventIdentifier);
                            boolean eventExists = event != null;

                            // If event already exists in UNS, just update its "last seen" date. Otherwise create it.
                            if (eventExists) {
                                event.setLastSeen(lastSeen);
                                channelFacade.updateEvent(event);
                            } else {
                                event = new Event();
                                event.setChannel(channel);
                                event.setExtId(eventIdentifier);
                                event.setCreationDate(lastSeen);
                                event.setProcessed(new Byte("0").byteValue());
                                event.setRtype(RSS.item.toString());
                                event.setLastSeen(lastSeen);

                                eventMetadataFacade.createEvent(event);

                                if (event.getId() != 0) {

                                    Map eventMetadata = (Map) pulledEvents.get(eventIdentifier);
                                    for (Iterator eventProperties = eventMetadata.keySet().iterator(); eventProperties.hasNext();) {

                                        String property = (String) eventProperties.next();
                                        String value = (String) eventMetadata.get(property);

                                        EventMetadata em = new EventMetadata();
                                        em.setEvent(event);
                                        em.setProperty(property);
                                        em.setValue(value);

                                        eventMetadataFacade.createEventMetadata(em);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LOGGER.error("Harvester warning! Following URL does not exist: " + url);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Harvesting " + url + "gave: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean exists(String URLName) {

        boolean ret = false;

        try {
            HttpURLConnection.setFollowRedirects(true);
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");

            ret = (con.getResponseCode() == HttpURLConnection.HTTP_OK);
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return ret;
    }
}
