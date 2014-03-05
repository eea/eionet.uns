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

/**
 * Pulls events from a PULL channel (e.g. an RSS feed).
 *
 * @author EuroDynamics
 * @author Jaanus
 */
public class PullerThread implements Runnable {

    /** Static logger for this class. */
    private static final WDSLogger LOGGER = WDSLogger.getLogger(PullerThread.class);

    /** The channel to pull from. */
    Channel channel;

    /** The date-time this thread was executed. */
    private Date harvestDate;

    /** Exception caught by the {@link #run()} method. */
    private Exception exception;

    /**
     * Default constructor.
     *
     * @param channel
     *            The channel to pull from.
     */
    public PullerThread(Channel channel) {
        this.channel = channel;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        harvestDate = new Date();
        String url = "";

        try {
            ChannelFacade channelFacade = new ChannelFacade();
            EventMetadataFacade eventMetadataFacade = new EventMetadataFacade();

            channel.setLastHarvestDate(harvestDate);
            channelFacade.updateChannel(channel);

            url = channel.getFeedUrl();
            if (url != null && url.length() > 0) {
                if (exists(url)) {

                    RdfContext rdfctx = new RdfContext(channel);
                    Map pulledEvents = rdfctx.getData(new ResourcesProcessor());
                    harvestDate = new Date();

                    for (Iterator eventIdentifiers = pulledEvents.keySet().iterator(); eventIdentifiers.hasNext();) {

                        String eventIdentifier = (String) eventIdentifiers.next();
                        if (eventIdentifier != null && eventIdentifier.length() > 0) {

                            Event event = eventMetadataFacade.findEventByExtId(eventIdentifier);
                            boolean eventExists = event != null;

                            // If event already exists in UNS, just update its "last seen" date. Otherwise create it.
                            if (eventExists) {
                                event.setLastSeen(harvestDate);
                                channelFacade.updateEvent(event);
                            } else {
                                event = new Event();
                                event.setChannel(channel);
                                event.setExtId(eventIdentifier);
                                event.setCreationDate(harvestDate);
                                event.setProcessed(new Byte("0").byteValue());
                                event.setRtype(RSS.item.toString());
                                event.setLastSeen(harvestDate);

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
            exception = e;
            LOGGER.error("Harvesting " + url + "gave: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Checks if the given URL actually exists and responds with HTTP 200 (follows redirects!).
     *
     * @param url The URL to check.
     * @return True/false.
     */
    public static boolean exists(String url) {

        boolean ret = false;

        try {
            HttpURLConnection.setFollowRedirects(true);
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("HEAD");

            ret = (con.getResponseCode() == HttpURLConnection.HTTP_OK);
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return ret;
    }

    /**
     * @return the harvestDate
     */
    protected Date getHarvestDate() {
        return harvestDate;
    }

    /**
     * @return the exception
     */
    public Exception getException() {
        return exception;
    }
}
