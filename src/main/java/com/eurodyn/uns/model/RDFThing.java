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
 *    Original code: Sasha Milosavljevic (ED) 
 */

package com.eurodyn.uns.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RDFThing implements Serializable {

    private static final long serialVersionUID = -4141581253831659597L;

    private Integer eventId;

    private String ext_id;

    private String type;

    private String title;

    private Date receivedDate;

    private String transformedContent;

    private Map metadata;

    public RDFThing(String ext_id, String type) {
        this.ext_id = ext_id;
        this.type = type;
        title = "No title";
        metadata = new HashMap();
    }

    public String getExt_id() {
        return ext_id;
    }

    public void setExt_id(String ext_id) {
        this.ext_id = ext_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getTransformedContent() {
        return transformedContent;
    }

    public void setTransformedContent(String transformedContent) {
        this.transformedContent = transformedContent;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Map getMetadata() {
        return metadata;
    }

    public void setMetadata(Map metadata) {
        this.metadata = metadata;
    }

    public String toString() {
        return "title " + title + "\n" + "ext_id " + ext_id + "\n" + metadata.toString();
    }
}
