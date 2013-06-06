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

public class DeliveryAddress implements Serializable {

    private static final long serialVersionUID = -2905658215261453682L;

    // Fields

    private long id;

    private String address;

    private DeliveryType deliveryType;

    // Constructors

    /** default constructor */
    public DeliveryAddress() {
    }

    /** minimal constructor */
    public DeliveryAddress(long id) {
        this.id = id;
    }

    /** minimal constructor */
    public DeliveryAddress(String address) {
        this.address = address;
    }

    public DeliveryAddress(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    /** full constructor */
    public DeliveryAddress(long id, String address) {
        this.id = id;
        this.address = address;
    }

    // Property accessors

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

}
