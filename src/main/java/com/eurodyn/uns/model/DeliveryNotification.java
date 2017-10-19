package com.eurodyn.uns.model;

// Generated Apr 16, 2006 9:40:13 PM by Hibernate Tools 3.1.0.beta4

/**
 * Delivery generated by hbm2java
 */

public class DeliveryNotification implements java.io.Serializable {

    // Fields
    private static final long serialVersionUID = 6443270929313798783L;
    private DeliveryType deliveryType;
    private Notification notification;

    // Constructors

    /** default constructor */
    public DeliveryNotification() {
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

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((deliveryType == null) ? 0 : deliveryType.hashCode());
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
        DeliveryNotification other = (DeliveryNotification) obj;
        if (deliveryType == null) {
            if (other.deliveryType != null)
                return false;
        } else if (!deliveryType.equals(other.deliveryType))
            return false;
        if (notification == null) {
            if (other.notification != null)
                return false;
        } else if (!notification.equals(other.notification))
            return false;
        return true;
    }
}
