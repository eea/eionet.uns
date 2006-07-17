package com.eurodyn.uns.model;

import java.util.HashMap;
import java.util.List;

public class Dto extends HashMap {

    /**
     * Returns a String value for specified key.
     */
    public String getAsString(Object key) {
        return (String) super.get(key);
    }

    /**
     * Returns a boolean value for specified key.
     */
    public boolean getAsBool(Object key) {
        return Boolean.valueOf(this.getAsString(key)).booleanValue();
    }
    
    /**
     * Returns a int value for specified key.
     */
    public int getAsInt(Object key) {
        return Integer.parseInt((String) get(key));
    }
    
    /**
     * Returns a Integer value for specified key.
     */
    public Integer getAsInteger(Object key) {
        return Integer.valueOf((String) get(key));
    }
    

    public List getAsList(Object key) {
        return (List) get(key);
    }

    /**
     * Trims specified value.
     */
    public void trim(String key) {

        final String value = (String) super.get(key);
        if (value != null) {
            super.put(key, value.trim());
        }
    }

    /**
     * Make specified value lower case.
     */
    public void toLowerCase(String key) {

        final String value = (String) super.get(key);
        if (value != null) {
            super.put(key, value.toLowerCase());
        }
    }

}
