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
 *                   Dusan Popovic (ED) 
 */

package com.eurodyn.uns.model;

import com.eurodyn.uns.util.rdf.IChannel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @hibernate.class table="CHANNEL" lazy="false"
 */
public class Channel implements IChannel, Serializable {

    private static final long serialVersionUID = -4307121279555040243L;

    private Integer id;

    private String title;

    private String description;

    private String feedUrl;

    private String mode;

    private Integer refreshDelay;

    private Byte UserSpecific;

    private Stylesheet transformation;

    private NotificationTemplate notificationTemplate;

    private String secondaryId;

    private Date creationDate;

    private Integer status;

    private User creator;

    private User user;

    private Set roles;

    private Set elements;

    private List metadataElements = new ArrayList(0);

    private List deliveryTypes;

    private List subscriptions;

    private String content;

    private Integer numberOfSubscriptions;

    private Date lastHarvestDate;

    private String language;

    private String inspectorsCsv;

    private boolean left;

    private boolean right;

    private boolean up;

    private boolean down;

    private boolean close;

    private Map transformedEvents = new HashMap();

    public Channel() {
        id = new Integer(-1);
        UserSpecific = new Byte((byte) 0);
        refreshDelay = new Integer(15);
        left = right = up = down = true;
        close = true;
        status = new Integer(0);

    }

    public Channel(int id) {
        this.id = new Integer(id);
    }

    /**
     * @hibernate.property column="DESCRIPTION" length="255"
     * @return String
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @hibernate.property column="FEED_URL" length="255"
     * @return String
     */
    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    /**
     * 
     * @hibernate.id column="ID" generator-class="native"
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @hibernate.property column="MODE" length="4" not-null="true"
     * @return String
     */
    public String getMode() {
        return mode.toUpperCase();
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * @hibernate.property column="REFRESH_DELAY"
     * @return Integer
     */
    public Integer getRefreshDelay() {
        return refreshDelay;
    }

    public void setRefreshDelay(Integer refreshDelay) {
        this.refreshDelay = refreshDelay;
    }

    /**
     * @hibernate.property column="TITLE" length="50" not-null="true"
     * @return String
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @hibernate.property column="USER_SPECIFIC" not-null="true"
     * @return String
     */
    public Byte getUserSpecific() {
        return UserSpecific;
    }

    public void setUserSpecific(Byte userSpecific) {
        UserSpecific = userSpecific;
    }

    /**
     * @hibernate.many-to-one column="STYLESHEET_ID" cascade="none"
     */
    public Stylesheet getTransformation() {
        return transformation;
    }

    public void setTransformation(Stylesheet transformation) {
        this.transformation = transformation;
    }

    /**
     * @hibernate.property column="CREATION_DATE" not-null="true"
     * @return Date
     */
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @hibernate.property column="SECONDARY_ID" length="100"
     * @return String
     */
    public String getSecondaryId() {
        return secondaryId;
    }

    public void setSecondaryId(String secondaryId) {
        this.secondaryId = secondaryId;
    }

    /**
     * @hibernate.property column="CSTATUS"
     * @return Integer
     */
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getInspectorsCsv() {
        return inspectorsCsv;
    }

    public void setInspectorsCsv(String inspectorsCsv) {
        this.inspectorsCsv = inspectorsCsv;
    }

    /**
     * @hibernate.many-to-one column="CREATOR" cascade="none"
     */
    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    /**
     * @hibernate.set lazy="false" cascade="none" table="CHANNEL_EEA_ROLE"
     * @hibernate.collection-key column="CHANNEL_ID"
     * @hibernate.collection-many-to-many class="com.eurodyn.uns.dto.Role" column="EEA_ROLE_ID"
     */
    public Set getRoles() {
        return roles;
    }

    public void setRoles(Set roles) {
        this.roles = roles;
    }

    /**
     * @hibernate.set lazy="false" cascade="all-delete-orphan" order-by="APPEARANCE_ORDER"
     * @hibernate.collection-key column="CHANNEL_ID" not-null="true"
     * @hibernate.collection-one-to-many class="com.eurodyn.uns.dto.VisibleElement" update="false"
     */
    public Set getElements() {
        return elements;
    }

    public void setElements(Set elements) {
        this.elements = elements;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isClose() {
        return close;
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof Channel))
            return false;

        Channel castOther = (Channel) other;

        return (id == null && castOther.getId() == null) || (id != null && id.equals(castOther.getId()));
    }

    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List getMetadataElements() {
        return metadataElements;
    }

    public void setMetadataElements(List metadataElements) {
        this.metadataElements = metadataElements;
    }

    public List getDeliveryTypes() {
        return deliveryTypes;
    }

    public void setDeliveryTypes(List deliveryTypes) {
        this.deliveryTypes = deliveryTypes;
    }
    
    public List getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List subscriptions) {
        this.subscriptions = subscriptions;
    }

    public NotificationTemplate getNotificationTemplate() {
        return notificationTemplate;
    }

    public void setNotificationTemplate(NotificationTemplate notificationTemplate) {
        this.notificationTemplate = notificationTemplate;
    }

    public Date getLastHarvestDate() {
        return lastHarvestDate;
    }

    public void setLastHarvestDate(Date lastHarvestDate) {
        this.lastHarvestDate = lastHarvestDate;
    }

    public Integer getNumberOfSubscriptions() {
        return numberOfSubscriptions;
    }

    public void setNumberOfSubscriptions(Integer numberOfSubscriptions) {
        this.numberOfSubscriptions = numberOfSubscriptions;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Map getTransformedEvents() {
        return transformedEvents;
    }

    public void setTransformedEvents(Map transformedEvents) {
        this.transformedEvents = transformedEvents;
    }

}