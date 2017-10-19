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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Event implements java.io.Serializable {

    private static final long serialVersionUID = 6687769519436084622L;

    private int id;

    private Channel channel;

    private String extId;

    private Date creationDate;

    private String rtype;

    private byte processed;

    private Map eventMetadata = new HashMap(0);

    private Set notifications = new HashSet(0);

    private Set eventMetadataSet = new HashSet(0);

    private Date lastSeen;

    // Constructors

    /** default constructor */
    public Event() {
    }

    /** minimal constructor */
    public Event(int id, Channel channel, String extId, Date creationDate, String rtype, byte processed) {
        this.id = id;
        this.channel = channel;
        this.extId = extId;
        this.creationDate = creationDate;
        this.rtype = rtype;
        this.processed = processed;
    }

    // Property accessors

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }


    public String getExtId() {
        return this.extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getRtype() {
        return this.rtype;
    }

    public void setRtype(String rtype) {
        this.rtype = rtype;
    }

    public byte getProcessed() {
        return this.processed;
    }

    public void setProcessed(byte processed) {
        this.processed = processed;
    }

    public Map getEventMetadata() {
        return this.eventMetadata;
    }

    public void setEventMetadata(Map eventMetadata) {
        this.eventMetadata = eventMetadata;
    }

    public Set getNotifications() {
        return this.notifications;
    }

    public void setNotifications(Set notifications) {
        this.notifications = notifications;
    }


    public Set getEventMetadataSet() {
        return eventMetadataSet;
    }

    public void setEventMetadataSet(Set eventMetadataSet) {
        this.eventMetadataSet = eventMetadataSet;
    }

    /**
     * @return the lastSeen
     */
    public Date getLastSeen() {
        return lastSeen;
    }

    /**
     * @param lastSeen the lastSeen to set
     */
    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }
}
