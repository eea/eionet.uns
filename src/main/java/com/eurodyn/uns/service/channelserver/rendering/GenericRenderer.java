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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.ChannelMetadataElement;
import com.eurodyn.uns.model.RDFThing;
import com.eurodyn.uns.util.rdf.FlResourcesProcessor;
import com.eurodyn.uns.util.rdf.RdfContext;

public class GenericRenderer implements IRenderStrategy {
    //private static final Logger LOGGER = LoggerFactory.getLogger(GenericRenderer.class);


    public String render(Channel channel, Map things) throws Exception {
        StringBuffer result = new StringBuffer();
        List properties = new ArrayList();
        List elements = new ArrayList(channel.getMetadataElements());
        Collections.sort(elements);
        if (elements != null) {
            for (Iterator iter = elements.iterator(); iter.hasNext();) {
                ChannelMetadataElement element = (ChannelMetadataElement) iter.next();
                if(element.isVisible().booleanValue())
                    properties.add(element.getMetadataElement().getName());
            }
        } else {
            properties.add("http://www.w3.org/2000/01/rdf-schema#label");
        }
        
        

        boolean loadedThings = things != null; 
        if (!loadedThings){
            RdfContext rdfctx = new RdfContext(channel);
            things = rdfctx.getData(new FlResourcesProcessor(properties));          
        }
                
        ArrayList values = null;
        if (things != null) {
            result.append("<ul>\n");
            for (Iterator iter = things.keySet().iterator(); iter.hasNext();) {
                String subject = (String) iter.next();
                Map el = null;
                if (!loadedThings)
                    el = (Map)  things.get(subject);
                else
                    el = ((RDFThing) things.get(subject)).getMetadata();
                result.append("\t<li>\n");
                for (int i = 0; i < properties.size(); i++) {
                    String property = (String) properties.get(i);
                    if (!loadedThings){
                        values = new ArrayList();
                        values.add(el.get(property));
                    }else{
                        values = (ArrayList) el.get(property);
                    }                       
                    if (values == null) continue;
                    for (int j = 0; j < values.size(); j++) {
                        String value = (String) values.get(j);
                        if (i == 0 && j == 0) {
                            result.append("<span>");
                            result.append(getLablel(property).toUpperCase()).append(": ");
                            result.append("</span>");
                            result.append("<a href=\"").append(subject).append("\" target=\"_blank\">").append(value).append("</a>\n");
                            result.append("\n");
                        } else if (value != null) {
                            result.append("<p>");
                            result.append("<span>");
                            result.append(getLablel(property).toUpperCase()).append(": ");
                            result.append("</span>");
                            result.append(value);
                            result.append("</p>\n");
                        }
                    }
                }
                result.append("\t</li>\n");
            }
            result.append("</ul>\n");
        }

        return result.toString();
    }


    
    
    private String getLablel(String uri) {
        String result = "";
        int i1 = uri.lastIndexOf("#");
        int i2 = uri.lastIndexOf("/");
        if (i1 < 0) {
            result = uri.substring(i2 + 1);
        } else {
            result = uri.substring(i1 + 1);
        }
        return result;
    }

}
