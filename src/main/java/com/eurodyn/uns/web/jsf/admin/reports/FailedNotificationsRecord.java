package com.eurodyn.uns.web.jsf.admin.reports;

import java.io.Serializable;

import com.eurodyn.uns.model.Subscription;

public class FailedNotificationsRecord implements Serializable {

    private static final long serialVersionUID = -166866592740094947L;

    private Subscription subscription;

    private String deliveryType;

    private String address;

    private Integer count;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

}
