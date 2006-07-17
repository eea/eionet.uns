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

package com.eurodyn.uns.util.rdf;

import java.util.HashMap;
import java.util.Map;

import com.eurodyn.uns.util.common.WDSLogger;
import com.hp.hpl.jena.mem.ModelMem;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFException;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RSS;

public class RssChannelsProcessor implements IRdfProcessStrategy {
    private static final WDSLogger logger = WDSLogger.getLogger(RssChannelsProcessor.class);
    
    
    public Map collect(ModelMem model, IChannel channel) throws Exception {
        Map result = null;
        try {
            renderChannel(model, channel);
            result = getMetadataElements(model);
        } catch (Exception e) {
            logger.error(e);
        }
        return result;
    }

    
    private void renderChannel(ModelMem model, IChannel channel) throws RDFException {
        ResIterator channels = model.listSubjectsWithProperty(RDF.type, RSS.channel);
        while (channels.hasNext()) {
            Resource channelRes = (Resource) channels.next();
            //Statement url = channelRes.getProperty(RSS.link);
            Statement title = channelRes.getProperty(RSS.title);
            Statement desc = channelRes.getProperty(RSS.description);
            Statement name = channelRes.getProperty(RSS.name);
            //logger.debug(url == null ? "" : url.getString());
            if (title != null) {
                channel.setTitle(title.getString());
            } else if (name != null) {
                channel.setTitle(name.getString());
            }
            channel.setDescription(desc == null ? null : desc.getString());
            //logger.debug(channel.getTitle());
            //logger.debug(channel.getDescription());
            //logger.debug(name == null ? "" : name.getString());
        }
    }
    
    /*
     * @TODO Use RDQL for data retreival
     */
    private Map getMetadataElements(ModelMem model) {
        Map metadata = null;
        StmtIterator iter = model.listStatements();
        if (iter.hasNext()) {
            metadata = new HashMap();
        }
        ResIterator images = model.listSubjectsWithProperty(RDF.type, RSS.image);
        Resource image=null;
        while (images.hasNext()) {
            image = (Resource) images.next();
        }
        ResIterator channels = model.listSubjectsWithProperty(RDF.type, RSS.channel);
        Resource channelRes=null;
        while (channels.hasNext()) {
            channelRes = (Resource) channels.next();
        }
        
        int i=1;
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            Resource subject = stmt.getSubject();
            Property predicate = stmt.getPredicate();
            RDFNode object = stmt.getObject();
            if (!subject.equals(channelRes) && !subject.equals(image) && object instanceof Literal && !predicate.equals(RDF.type) && !subject.isAnon()) {
                if (!metadata.containsKey(predicate.toString())) {
                    metadata.put(predicate.toString(), predicate.getLocalName());
                    //logger.debug(Integer.toString(i)+" " + predicate.toString() + " ");
                }
            }
            ++i;
        }
        return metadata;
    }


   
}
