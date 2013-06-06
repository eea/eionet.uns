package com.eurodyn.uns.web.jsf.admin.config;

import java.io.Serializable;

public class ConfigElement implements Serializable {

    private static final long serialVersionUID = -8639428215259191096L;

    private String path;

    private Object value;

    private Object tempValue;

    public ConfigElement() {
    }

    public ConfigElement(String path, Object val) {
        this.path = path;
        this.value = val;
        this.tempValue = val;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Object getTempValue() {
        return tempValue;
    }

    public void setTempValue(Object tempValue) {
        this.tempValue = tempValue;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
