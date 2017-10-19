package com.eurodyn.uns.model;

// Generated Apr 16, 2006 9:40:13 PM by Hibernate Tools 3.1.0.beta4

import java.util.Date;

/**
 * Delivery generated by hbm2java
 */

public class Delivery implements java.io.Serializable {

    // Fields
    private static final long serialVersionUID = 6443270929313798783L;
    
    private DeliveryNotification id;
    
    private int   deliveryStatus;

    private Date deliveryTime;
    
    private DeliveryType deliveryType;
    
    private Notification notification;

    // Constructors

    /** default constructor */
    public Delivery() {
    }

    // Property accessors

    public int getDeliveryStatus() {
        return this.deliveryStatus;
    }

    public void setDeliveryStatus(int deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public Date getDeliveryTime() {
        return this.deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }
    
    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public DeliveryNotification getId() {
        return id;
    }

    public void setId(DeliveryNotification id) {
        this.id = id;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + deliveryStatus;
        result = prime * result
                + ((deliveryTime == null) ? 0 : deliveryTime.hashCode());
        result = prime * result
                + ((deliveryType == null) ? 0 : deliveryType.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((notification == null) ? 0 : notification.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Delivery other = (Delivery) obj;
        if (deliveryStatus != other.deliveryStatus)
            return false;
        if (deliveryTime == null) {
            if (other.deliveryTime != null)
                return false;
        } else if (!deliveryTime.equals(other.deliveryTime))
            return false;
        if (deliveryType == null) {
            if (other.deliveryType != null)
                return false;
        } else if (!deliveryType.equals(other.deliveryType))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (notification == null) {
            if (other.notification != null)
                return false;
        } else if (!notification.equals(other.notification))
            return false;
        return true;
    }


}
