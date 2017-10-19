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
 *    Original code: Dusan Popovic (ED)
 */

package com.eurodyn.uns.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eurodyn.uns.service.facades.UserFacade;

/**
 * @hibernate.class table="EEA_USER" lazy="false"
 */
public class User implements Serializable {

    private static final long serialVersionUID = -5818776384073027978L;

    private Integer id;

    private String externalId;

    private Short numberOfColumns;

    private Integer pageRefreshDelay;

    private String fullName;

    private Map subscriptions = new HashMap();;

    private Map deliveryAddresses;

    private List userRoles;

    private boolean loggedIn;

    private Date vacationExpiration;

    private Boolean vacationFlag;

    private Boolean disabledFlag;

    private Boolean preferHtml;

    private Boolean preferDashboard;


    public User() {
    }

    public User(String externalId) {
        this.externalId = externalId;
        this.loggedIn = false;
        this.pageRefreshDelay = new Integer(60);
        this.disabledFlag = false;
    }

    /**
     * @hibernate.property column="EXT_ID" length="50" not-null="true"
     * @return String
     */
    public String getExternalId() {
        return externalId;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
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
     * @hibernate.property column="NUMCOLUMNS"
     * @return Short
     */
    public Short getNumberOfColumns() {
        return numberOfColumns;
    }

    public void setNumberOfColumns(Short numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }

    /**
     * @hibernate.property column="PAGE_REFRESH_DELAY"
     * @return Integer
     */
    public Integer getPageRefreshDelay() {
        return pageRefreshDelay;
    }

    public void setPageRefreshDelay(Integer pageRefreshDelay) {
        this.pageRefreshDelay = pageRefreshDelay;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @hibernate.set lazy="false" cascade="all" order-by="CORD_X, CORD_Y"
     * @hibernate.collection-key column="EEA_USER_ID"
     * @hibernate.collection-one-to-many class="com.eurodyn.uns.dto.UserChannel"
     */
    public Map getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Map subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List userRoles) {
        this.userRoles = userRoles;
    }

    public boolean hasPerm(String aclPath, String prm) {
        return UserFacade.hasPerm(getExternalId(), aclPath, prm);
    }

    public Date getVacationExpiration() {
        return vacationExpiration;
    }

    public void setVacationExpiration(Date vacationExpiration) {
        this.vacationExpiration = vacationExpiration;
    }

    public Boolean getPreferHtml() {
        return preferHtml;
    }

    public void setPreferHtml(Boolean preferHtml) {
        this.preferHtml = preferHtml;
    }

    public Boolean getVacationFlag() {
        return vacationFlag;
    }

    public void setVacationFlag(Boolean vacationFlag) {
        this.vacationFlag = vacationFlag;
    }

    public Map getDeliveryAddresses() {
        return deliveryAddresses;
    }

    public void setDeliveryAddresses(Map deliveryAddresses) {
        this.deliveryAddresses = deliveryAddresses;

    }

    public Boolean getPreferDashboard() {
        return preferDashboard;
    }

    public void setPreferDashboard(Boolean preferDashboard) {
        this.preferDashboard = preferDashboard;
    }

    public Boolean getDisabledFlag() {
        return disabledFlag;
    }

    public void setDisabledFlag(Boolean disabledFlag) {
        this.disabledFlag = disabledFlag;
    }

}
