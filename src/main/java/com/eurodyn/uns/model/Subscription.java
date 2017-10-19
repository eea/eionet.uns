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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Subscription implements Serializable {

    private static final long serialVersionUID = -4790440204099589341L;

    // Fields
    private Integer id;

    private Channel channel;

    private User user;

    private Integer leadTime;

    private Date creationDate;

    private String secondaryId;

    private Short dashCordX;

    private Short dashCordY;

    private List deliveryTypes = new ArrayList(0);

    private List filters = new ArrayList(0);

    // Constructors

    /** default constructor */
    public Subscription() {
        dashCordX = new Short((short) -1);
        dashCordY = new Short((short) -1);
    }

    // Property accessors

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;

    }

    public Integer getLeadTime() {
        return this.leadTime;
    }

    public void setLeadTime(Integer leadTime) {
        this.leadTime = leadTime;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getSecondaryId() {
        return this.secondaryId;
    }

    public void setSecondaryId(String secondaryId) {
        this.secondaryId = secondaryId;
    }

    public Short getDashCordX() {
        return this.dashCordX;
    }

    public void setDashCordX(Short dashCordX) {
        if (dashCordX != null)
            this.dashCordX = dashCordX;
    }

    public Short getDashCordY() {
        return this.dashCordY;
    }

    public void setDashCordY(Short dashCordY) {
        if (dashCordY != null)
            this.dashCordY = dashCordY;
    }

    public List getDeliveryTypes() {
        return this.deliveryTypes;
    }

    public void setDeliveryTypes(List deliveryTypes) {
        this.deliveryTypes = deliveryTypes;
    }

    public List getFilters() {
        return this.filters;
    }

    public void setFilters(List filters) {
        this.filters = filters;
    }

}
