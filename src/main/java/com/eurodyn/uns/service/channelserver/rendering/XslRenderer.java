/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 * 
 * The Original Code is Unified Notification System
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 * 
 * Contributors(s):
 *    Original code: Nedeljko Pavlovic (ED) 
 */

package com.eurodyn.uns.service.channelserver.rendering;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.RDFThing;
import com.eurodyn.uns.util.URLReader;
import com.eurodyn.uns.util.xml.XSLTransformer;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RSS;

public class XslRenderer implements IRenderStrategy {

    public static final Logger LOGGER = LoggerFactory.getLogger(XslRenderer.class);
    public static XSLTransformer transform;

    static {
        transform = new XSLTransformer();
    }

    public String render(Channel channel, Map things) throws Exception {
        InputSource source = null;

        String url = channel.getFeedUrl();
        
        if (things != null){
            String rssContent = toRss(channel,things);
            source = new InputSource(new BufferedReader(new StringReader(rssContent)));
        }       
        else if (channel.getContent() != null) {
            URLReader urlReader = new URLReader();
            InputStream stream = urlReader.getContentAsInStream(url);
            source = new InputSource(stream);
            source.setSystemId(url);
            
        } else {
            URLReader urlReader = new URLReader();
            InputStream stream = urlReader.getContentAsInStream(url);
            source = new InputSource(stream);
            source.setSystemId(url);
        }

        Map parameters = new HashMap();
        parameters.put("openinpopup", "true");
        parameters.put("showdescription", "true");
        parameters.put("showtitle", "true");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transform.transform(channel.getTransformation().getName(), channel.getTransformation().getContent(), source, baos, parameters);
        return baos.toString();

    }

    public String toRss(Channel channel, Map things) throws Exception {

        
        Model rdf = ModelFactory.createDefaultModel();
        rdf.setNsPrefix("", "http://purl.org/rss/1.0/");
        rdf.setNsPrefix("content", "http://purl.org/rss/1.0/modules/content/");
        rdf.setNsPrefix("slash", "http://purl.org/rss/1.0/modules/slash/");
        Resource rssChannel = rdf.createResource(RSS.channel);
        rssChannel.addProperty(RSS.title, channel.getTitle());
        if (channel.getFeedUrl() != null)
            rssChannel.addProperty(RSS.link, channel.getFeedUrl());
        rssChannel.addProperty(RSS.description, channel.getDescription());
        try {
            Iterator it = things.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                RDFThing rdfThing = (RDFThing) pairs.getValue();
                Resource item = rdf.createResource(rdfThing.getExt_id(), ResourceFactory.createResource(rdfThing.getType()));
                Iterator iter = rdfThing.getMetadata().entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry pairs2 = (Map.Entry) iter.next();
                    String pred = (String) pairs2.getKey();
                    ArrayList values = (ArrayList) pairs2.getValue();
                    for (int i = 0; i < values.size(); i++) {
                        item.addProperty(ResourceFactory.createProperty(pred) , (String) values.get(i));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        StringWriter out = new StringWriter();
        RDFWriter writer = rdf.getWriter("RDF/XML-ABBREV");
        writer.write(rdf, new BufferedWriter(out), null);       
        return out.toString();

    }

}
