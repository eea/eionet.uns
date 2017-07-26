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
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import com.hp.hpl.jena.mem.ModelMem;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RSS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlResourcesProcessor implements IRdfProcessStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlResourcesProcessor.class);
    
    
    private List properties;
    public FlResourcesProcessor() {
        
    }
    
    public FlResourcesProcessor(List properties) {
        this.properties=properties;        
    }

    
    /*
     * @TODO Use RDQL for data retreival
     */
    public Map collect(ModelMem model, IChannel channel) throws Exception {
        Map data = null;
        StmtIterator iter = model.listStatements();

        if (iter.hasNext()) {
            data = new HashMap();
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

        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            Resource subject = stmt.getSubject();
            Property predicate = stmt.getPredicate();
            RDFNode object = stmt.getObject();
            if (!subject.equals(channelRes) && !subject.equals(image) && object instanceof Literal && !predicate.equals(RDF.type) && !subject.isAnon()) {
                if (!data.containsKey(subject.toString())) {
                    Map elements=new HashMap();
                    data.put(subject.toString(),elements);
                    for (Iterator iterator = properties.iterator(); iterator.hasNext();) {
                        String element = (String) iterator.next();
                        elements.put(element,getValue(subject,element));
                        LOGGER.debug(getValue(subject,element));
                    }
                }
            }
        }
        return data;
    }
    
    private String getValue(Resource subject, String property) {
        String result=null;
        Statement s=subject.getProperty(new PropertyImpl(property));
        if(s!=null) {
            result=s.getObject()==null?null:s.getObject().toString();
        }
        return result;
    }

}
