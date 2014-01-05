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

    private static final WDSLogger logger = WDSLogger.getLogger(PullerThread.class);

    Channel channel;

    public PullerThread(Channel channel) {
        this.channel = channel;
    }

    public void run() {

        try {
            ChannelFacade channelFacade = new ChannelFacade();
            EventMetadataFacade emFacade = new EventMetadataFacade();

            channel.setLastHarvestDate(new Date());
            channelFacade.updateChannel(channel);

            String url = channel.getFeedUrl();
            if (url != null && url.length() > 0) {
                if (exists(url)) {

                    RdfContext rdfctx = new RdfContext(channel);
                    Map things = rdfctx.getData(new ResourcesProcessor());

                    for (Iterator it = things.keySet().iterator(); it.hasNext();) {
                        String key = (String) it.next();
                        if (key != null && key.length() > 0) {
                            Event event = new Event();
                            event.setChannel(channel);
                            event.setExtId(key);
                            event.setCreationDate(new Date());
                            event.setProcessed(new Byte("0").byteValue());
                            String rtype = RSS.item.toString();
                            event.setRtype(rtype);

                            if (!emFacade.eventExists(key)) {
                                emFacade.createEvent(event);

                                if (event.getId() != 0) {
                                    Map elements = (Map) things.get(key);
                                    for (Iterator it2 = elements.keySet().iterator(); it2.hasNext();) {
                                        String property = (String) it2.next();
                                        String val = (String) elements.get(property);
                                        EventMetadata em = new EventMetadata();
                                        em.setEvent(event);
                                        em.setProperty(property);
                                        em.setValue(val);

                                        emFacade.createEventMetadata(em);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    logger.error("Harvester warning! Following URL does not exist: " + url);
                }
            }

        } catch (Exception e) {
            logger.error("Harvesting " + url + "gave: " + e.getMessage());
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
